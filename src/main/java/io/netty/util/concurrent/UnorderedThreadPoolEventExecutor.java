package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class UnorderedThreadPoolEventExecutor extends ScheduledThreadPoolExecutor implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnorderedThreadPoolEventExecutor.class);
   private final Promise<?> terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
   private final Set<EventExecutor> executorSet = Collections.singleton(this);

   public UnorderedThreadPoolEventExecutor(int var1) {
      this(var1, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class));
   }

   public UnorderedThreadPoolEventExecutor(int var1, ThreadFactory var2) {
      super(var1, var2);
   }

   public UnorderedThreadPoolEventExecutor(int var1, java.util.concurrent.RejectedExecutionHandler var2) {
      this(var1, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), var2);
   }

   public UnorderedThreadPoolEventExecutor(int var1, ThreadFactory var2, java.util.concurrent.RejectedExecutionHandler var3) {
      super(var1, var2, var3);
   }

   @Override
   public EventExecutor next() {
      return this;
   }

   @Override
   public EventExecutorGroup parent() {
      return this;
   }

   @Override
   public boolean inEventLoop() {
      return false;
   }

   @Override
   public boolean inEventLoop(Thread var1) {
      return false;
   }

   @Override
   public <V> Promise<V> newPromise() {
      return new DefaultPromise<>(this);
   }

   @Override
   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new DefaultProgressivePromise<>(this);
   }

   @Override
   public <V> Future<V> newSucceededFuture(V var1) {
      return new SucceededFuture<>(this, (V)var1);
   }

   @Override
   public <V> Future<V> newFailedFuture(Throwable var1) {
      return new FailedFuture<>(this, var1);
   }

   @Override
   public boolean isShuttingDown() {
      return this.isShutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      List var1 = super.shutdownNow();
      this.terminationFuture.trySuccess(null);
      return var1;
   }

   @Override
   public void shutdown() {
      super.shutdown();
      this.terminationFuture.trySuccess(null);
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      this.shutdown();
      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.executorSet.iterator();
   }

   @Override
   protected <V> RunnableScheduledFuture<V> decorateTask(Runnable var1, RunnableScheduledFuture<V> var2) {
      return (RunnableScheduledFuture<V>)(var1 instanceof UnorderedThreadPoolEventExecutor.NonNotifyRunnable
         ? var2
         : new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask<>(this, var2, false));
   }

   @Override
   protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> var1, RunnableScheduledFuture<V> var2) {
      return new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask<>(this, var2, true);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return (ScheduledFuture<?>)super.schedule(var1, var2, var4);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return (ScheduledFuture<V>)super.<V>schedule(var1, var2, var4);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return (ScheduledFuture<?>)super.scheduleAtFixedRate(var1, var2, var4, var6);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return (ScheduledFuture<?>)super.scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   @Override
   public Future<?> submit(Runnable var1) {
      return (Future<?>)super.submit(var1);
   }

   @Override
   public <T> Future<T> submit(Runnable var1, T var2) {
      return (Future<T>)super.<T>submit(var1, (T)var2);
   }

   @Override
   public <T> Future<T> submit(Callable<T> var1) {
      return (Future<T>)super.<T>submit(var1);
   }

   @Override
   public void execute(Runnable var1) {
      super.schedule(new UnorderedThreadPoolEventExecutor.NonNotifyRunnable(var1), 0L, TimeUnit.NANOSECONDS);
   }

   private static final class NonNotifyRunnable implements Runnable {
      private final Runnable task;

      NonNotifyRunnable(Runnable var1) {
         this.task = var1;
      }

      @Override
      public void run() {
         this.task.run();
      }
   }

   private static final class RunnableScheduledFutureTask<V> extends PromiseTask<V> implements ScheduledFuture<V>, RunnableScheduledFuture<V> {
      private final RunnableScheduledFuture<V> future;
      private final boolean wasCallable;

      RunnableScheduledFutureTask(EventExecutor var1, RunnableScheduledFuture<V> var2, boolean var3) {
         super(var1, var2);
         this.future = var2;
         this.wasCallable = var3;
      }

      @Override
      V runTask() throws Throwable {
         Object var1 = super.runTask();
         if (var1 != null || !this.wasCallable) {
            return (V)var1;
         } else {
            assert this.future.isDone();

            try {
               return this.future.get();
            } catch (ExecutionException var3) {
               throw var3.getCause();
            }
         }
      }

      @Override
      public void run() {
         if (!this.isPeriodic()) {
            super.run();
         } else if (!this.isDone()) {
            try {
               this.runTask();
            } catch (Throwable var2) {
               if (!this.tryFailureInternal(var2)) {
                  UnorderedThreadPoolEventExecutor.logger.warn("Failure during execution of task", var2);
               }
            }
         }
      }

      @Override
      public boolean isPeriodic() {
         return this.future.isPeriodic();
      }

      @Override
      public long getDelay(TimeUnit var1) {
         return this.future.getDelay(var1);
      }

      public int compareTo(Delayed var1) {
         return this.future.compareTo(var1);
      }
   }
}
