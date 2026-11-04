package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedChannelPool extends SimpleChannelPool {
   private final EventExecutor executor;
   private final long acquireTimeoutNanos;
   private final Runnable timeoutTask;
   private final Queue<FixedChannelPool.AcquireTask> pendingAcquireQueue = new ArrayDeque<>();
   private final int maxConnections;
   private final int maxPendingAcquires;
   private final AtomicInteger acquiredChannelCount = new AtomicInteger();
   private int pendingAcquireCount;
   private boolean closed;

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, int var3) {
      this(var1, var2, var3, Integer.MAX_VALUE);
   }

   public FixedChannelPool(Bootstrap var1, ChannelPoolHandler var2, int var3, int var4) {
      this(var1, var2, ChannelHealthChecker.ACTIVE, null, -1L, var3, var4);
   }

   public FixedChannelPool(
      Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, FixedChannelPool.AcquireTimeoutAction var4, long var5, int var7, int var8
   ) {
      this(var1, var2, var3, var4, var5, var7, var8, true);
   }

   public FixedChannelPool(
      Bootstrap var1,
      ChannelPoolHandler var2,
      ChannelHealthChecker var3,
      FixedChannelPool.AcquireTimeoutAction var4,
      long var5,
      int var7,
      int var8,
      boolean var9
   ) {
      this(var1, var2, var3, var4, var5, var7, var8, var9, true);
   }

   public FixedChannelPool(
      Bootstrap var1,
      ChannelPoolHandler var2,
      ChannelHealthChecker var3,
      FixedChannelPool.AcquireTimeoutAction var4,
      long var5,
      int var7,
      int var8,
      boolean var9,
      boolean var10
   ) {
      super(var1, var2, var3, var9, var10);
      ObjectUtil.checkPositive(var7, "maxConnections");
      ObjectUtil.checkPositive(var8, "maxPendingAcquires");
      if (var4 == null && var5 == -1L) {
         this.timeoutTask = null;
         this.acquireTimeoutNanos = -1L;
      } else {
         if (var4 == null && var5 != -1L) {
            throw new NullPointerException("action");
         }

         if (var4 != null && var5 < 0L) {
            throw new IllegalArgumentException("acquireTimeoutMillis: " + var5 + " (expected: >= 0)");
         }

         this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(var5);
         switch (var4) {
            case FAIL:
               this.timeoutTask = new FixedChannelPool.TimeoutTask() {
                  @Override
                  public void onTimeout(FixedChannelPool.AcquireTask var1) {
                     var1.promise.setFailure(new FixedChannelPool.AcquireTimeoutException());
                  }
               };
               break;
            case NEW:
               this.timeoutTask = new FixedChannelPool.TimeoutTask() {
                  @Override
                  public void onTimeout(FixedChannelPool.AcquireTask var1) {
                     var1.acquired();
                     FixedChannelPool.super.acquire(var1.promise);
                  }
               };
               break;
            default:
               throw new Error();
         }
      }

      this.executor = var1.config().group().next();
      this.maxConnections = var7;
      this.maxPendingAcquires = var8;
   }

   public int acquiredChannelCount() {
      return this.acquiredChannelCount.get();
   }

   @Override
   public Future<Channel> acquire(final Promise<Channel> var1) {
      try {
         if (this.executor.inEventLoop()) {
            this.acquire0(var1);
         } else {
            this.executor.execute(new Runnable() {
               @Override
               public void run() {
                  FixedChannelPool.this.acquire0(var1);
               }
            });
         }
      } catch (Throwable var3) {
         var1.tryFailure(var3);
      }

      return var1;
   }

   private void acquire0(Promise<Channel> var1) {
      try {
         assert this.executor.inEventLoop();

         if (this.closed) {
            var1.setFailure(new IllegalStateException("FixedChannelPool was closed"));
            return;
         }

         if (this.acquiredChannelCount.get() < this.maxConnections) {
            assert this.acquiredChannelCount.get() >= 0;

            Promise var2 = this.executor.newPromise();
            FixedChannelPool.AcquireListener var3 = new FixedChannelPool.AcquireListener(var1);
            var3.acquired();
            var2.addListener(var3);
            super.acquire(var2);
         } else {
            if (this.pendingAcquireCount >= this.maxPendingAcquires) {
               this.tooManyOutstanding(var1);
            } else {
               FixedChannelPool.AcquireTask var5 = new FixedChannelPool.AcquireTask(var1);
               if (this.pendingAcquireQueue.offer(var5)) {
                  this.pendingAcquireCount++;
                  if (this.timeoutTask != null) {
                     var5.timeoutFuture = this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
                  }
               } else {
                  this.tooManyOutstanding(var1);
               }
            }

            assert this.pendingAcquireCount > 0;
         }
      } catch (Throwable var4) {
         var1.tryFailure(var4);
      }
   }

   private void tooManyOutstanding(Promise<?> var1) {
      var1.setFailure(new IllegalStateException("Too many outstanding acquire operations"));
   }

   @Override
   public Future<Void> release(final Channel var1, final Promise<Void> var2) {
      ObjectUtil.checkNotNull(var2, "promise");
      Promise var3 = this.executor.newPromise();
      super.release(var1, var3.addListener(new FutureListener<Void>() {
         @Override
         public void operationComplete(Future<Void> var1x) {
            try {
               assert FixedChannelPool.this.executor.inEventLoop();

               if (FixedChannelPool.this.closed) {
                  var1.close();
                  var2.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                  return;
               }

               if (var1x.isSuccess()) {
                  FixedChannelPool.this.decrementAndRunTaskQueue();
                  var2.setSuccess(null);
               } else {
                  Throwable var2x = var1x.cause();
                  if (!(var2x instanceof IllegalArgumentException)) {
                     FixedChannelPool.this.decrementAndRunTaskQueue();
                  }

                  var2.setFailure(var1x.cause());
               }
            } catch (Throwable var3) {
               var2.tryFailure(var3);
            }
         }
      }));
      return var2;
   }

   private void decrementAndRunTaskQueue() {
      int var1 = this.acquiredChannelCount.decrementAndGet();

      assert var1 >= 0;

      this.runTaskQueue();
   }

   private void runTaskQueue() {
      while (this.acquiredChannelCount.get() < this.maxConnections) {
         FixedChannelPool.AcquireTask var1 = this.pendingAcquireQueue.poll();
         if (var1 != null) {
            ScheduledFuture var2 = var1.timeoutFuture;
            if (var2 != null) {
               var2.cancel(false);
            }

            this.pendingAcquireCount--;
            var1.acquired();
            super.acquire(var1.promise);
            continue;
         }
         break;
      }

      assert this.pendingAcquireCount >= 0;

      assert this.acquiredChannelCount.get() >= 0;
   }

   @Override
   public void close() {
      try {
         this.closeAsync().await();
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
         throw new RuntimeException(var2);
      }
   }

   @Override
   public Future<Void> closeAsync() {
      if (this.executor.inEventLoop()) {
         return this.close0();
      } else {
         final Promise var1 = this.executor.newPromise();
         this.executor.execute(new Runnable() {
            @Override
            public void run() {
               FixedChannelPool.this.close0().addListener(new FutureListener<Void>() {
                  @Override
                  public void operationComplete(Future<Void> var1x) throws Exception {
                     if (var1x.isSuccess()) {
                        var1.setSuccess(null);
                     } else {
                        var1.setFailure(var1x.cause());
                     }
                  }
               });
            }
         });
         return var1;
      }
   }

   private Future<Void> close0() {
      assert this.executor.inEventLoop();

      if (!this.closed) {
         this.closed = true;

         while (true) {
            FixedChannelPool.AcquireTask var1 = this.pendingAcquireQueue.poll();
            if (var1 == null) {
               this.acquiredChannelCount.set(0);
               this.pendingAcquireCount = 0;
               return GlobalEventExecutor.INSTANCE.submit(new Callable<Void>() {
                  public Void call() throws Exception {
                     FixedChannelPool.super.close();
                     return null;
                  }
               });
            }

            ScheduledFuture var2 = var1.timeoutFuture;
            if (var2 != null) {
               var2.cancel(false);
            }

            var1.promise.setFailure(new ClosedChannelException());
         }
      } else {
         return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
      }
   }

   private class AcquireListener implements FutureListener<Channel> {
      private final Promise<Channel> originalPromise;
      protected boolean acquired;

      AcquireListener(Promise<Channel> var2) {
         this.originalPromise = var2;
      }

      @Override
      public void operationComplete(Future<Channel> var1) throws Exception {
         try {
            assert FixedChannelPool.this.executor.inEventLoop();

            if (FixedChannelPool.this.closed) {
               if (var1.isSuccess()) {
                  ((Channel)var1.getNow()).close();
               }

               this.originalPromise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
               return;
            }

            if (var1.isSuccess()) {
               this.originalPromise.setSuccess((Channel)var1.getNow());
            } else {
               if (this.acquired) {
                  FixedChannelPool.this.decrementAndRunTaskQueue();
               } else {
                  FixedChannelPool.this.runTaskQueue();
               }

               this.originalPromise.setFailure(var1.cause());
            }
         } catch (Throwable var3) {
            this.originalPromise.tryFailure(var3);
         }
      }

      public void acquired() {
         if (!this.acquired) {
            FixedChannelPool.this.acquiredChannelCount.incrementAndGet();
            this.acquired = true;
         }
      }
   }

   private final class AcquireTask extends FixedChannelPool.AcquireListener {
      final Promise<Channel> promise;
      final long expireNanoTime = System.nanoTime() + FixedChannelPool.this.acquireTimeoutNanos;
      ScheduledFuture<?> timeoutFuture;

      AcquireTask(Promise<Channel> var2) {
         super(var2);
         this.promise = FixedChannelPool.this.executor.<Channel>newPromise().addListener(this);
      }
   }

   public static enum AcquireTimeoutAction {
      NEW,
      FAIL;
   }

   private static final class AcquireTimeoutException extends TimeoutException {
      private AcquireTimeoutException() {
         super("Acquire operation took longer then configured maximum time");
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   private abstract class TimeoutTask implements Runnable {
      private TimeoutTask() {
      }

      @Override
      public final void run() {
         assert FixedChannelPool.this.executor.inEventLoop();

         long var1 = System.nanoTime();

         while (true) {
            FixedChannelPool.AcquireTask var3 = FixedChannelPool.this.pendingAcquireQueue.peek();
            if (var3 == null || var1 - var3.expireNanoTime < 0L) {
               return;
            }

            FixedChannelPool.this.pendingAcquireQueue.remove();
            --FixedChannelPool.this.pendingAcquireCount;
            this.onTimeout(var3);
         }
      }

      public abstract void onTimeout(FixedChannelPool.AcquireTask var1);
   }
}
