package io.netty.channel.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.Deque;
import java.util.concurrent.Callable;

public class SimpleChannelPool implements ChannelPool {
   private static final AttributeKey<SimpleChannelPool> POOL_KEY = AttributeKey.newInstance("io.netty.channel.pool.SimpleChannelPool");
   private final Deque<Channel> deque = PlatformDependent.newConcurrentDeque();
   private final ChannelPoolHandler handler;
   private final ChannelHealthChecker healthCheck;
   private final Bootstrap bootstrap;
   private final boolean releaseHealthCheck;
   private final boolean lastRecentUsed;

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2) {
      this(var1, var2, ChannelHealthChecker.ACTIVE);
   }

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3) {
      this(var1, var2, var3, true);
   }

   public SimpleChannelPool(Bootstrap var1, ChannelPoolHandler var2, ChannelHealthChecker var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   public SimpleChannelPool(Bootstrap var1, final ChannelPoolHandler var2, ChannelHealthChecker var3, boolean var4, boolean var5) {
      this.handler = ObjectUtil.checkNotNull(var2, "handler");
      this.healthCheck = ObjectUtil.checkNotNull(var3, "healthCheck");
      this.releaseHealthCheck = var4;
      this.bootstrap = ObjectUtil.checkNotNull(var1, "bootstrap").clone();
      this.bootstrap.handler(new ChannelInitializer<Channel>() {
         @Override
         protected void initChannel(Channel var1) throws Exception {
            assert var1.eventLoop().inEventLoop();

            var2.channelCreated(var1);
         }
      });
      this.lastRecentUsed = var5;
   }

   protected Bootstrap bootstrap() {
      return this.bootstrap;
   }

   protected ChannelPoolHandler handler() {
      return this.handler;
   }

   protected ChannelHealthChecker healthChecker() {
      return this.healthCheck;
   }

   protected boolean releaseHealthCheck() {
      return this.releaseHealthCheck;
   }

   @Override
   public final Future<Channel> acquire() {
      return this.acquire(this.bootstrap.config().group().next().newPromise());
   }

   @Override
   public Future<Channel> acquire(Promise<Channel> var1) {
      return this.acquireHealthyFromPoolOrNew(ObjectUtil.checkNotNull(var1, "promise"));
   }

   private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> var1) {
      try {
         final Channel var2 = this.pollChannel();
         if (var2 == null) {
            Bootstrap var3 = this.bootstrap.clone();
            var3.attr(POOL_KEY, this);
            ChannelFuture var4 = this.connectChannel(var3);
            if (var4.isDone()) {
               this.notifyConnect(var4, var1);
            } else {
               var4.addListener(new ChannelFutureListener() {
                  public void operationComplete(ChannelFuture var1x) throws Exception {
                     SimpleChannelPool.this.notifyConnect(var1x, var1);
                  }
               });
            }
         } else {
            EventLoop var6 = var2.eventLoop();
            if (var6.inEventLoop()) {
               this.doHealthCheck(var2, var1);
            } else {
               var6.execute(new Runnable() {
                  @Override
                  public void run() {
                     SimpleChannelPool.this.doHealthCheck(var2, var1);
                  }
               });
            }
         }
      } catch (Throwable var5) {
         var1.tryFailure(var5);
      }

      return var1;
   }

   private void notifyConnect(ChannelFuture var1, Promise<Channel> var2) {
      Channel var3 = null;

      try {
         if (var1.isSuccess()) {
            var3 = var1.channel();
            this.handler.channelAcquired(var3);
            if (!var2.trySuccess(var3)) {
               this.release(var3);
            }
         } else {
            var2.tryFailure(var1.cause());
         }
      } catch (Throwable var5) {
         this.closeAndFail(var3, var5, var2);
      }
   }

   private void doHealthCheck(final Channel var1, final Promise<Channel> var2) {
      try {
         assert var1.eventLoop().inEventLoop();

         Future var3 = this.healthCheck.isHealthy(var1);
         if (var3.isDone()) {
            this.notifyHealthCheck(var3, var1, var2);
         } else {
            var3.addListener(new FutureListener<Boolean>() {
               @Override
               public void operationComplete(Future<Boolean> var1x) {
                  SimpleChannelPool.this.notifyHealthCheck(var1x, var1, var2);
               }
            });
         }
      } catch (Throwable var4) {
         this.closeAndFail(var1, var4, var2);
      }
   }

   private void notifyHealthCheck(Future<Boolean> var1, Channel var2, Promise<Channel> var3) {
      try {
         assert var2.eventLoop().inEventLoop();

         if (var1.isSuccess() && (Boolean)var1.getNow()) {
            var2.attr(POOL_KEY).set(this);
            this.handler.channelAcquired(var2);
            var3.setSuccess(var2);
         } else {
            this.closeChannel(var2);
            this.acquireHealthyFromPoolOrNew(var3);
         }
      } catch (Throwable var5) {
         this.closeAndFail(var2, var5, var3);
      }
   }

   protected ChannelFuture connectChannel(Bootstrap var1) {
      return var1.connect();
   }

   @Override
   public final Future<Void> release(Channel var1) {
      return this.release(var1, var1.eventLoop().newPromise());
   }

   @Override
   public Future<Void> release(final Channel var1, final Promise<Void> var2) {
      try {
         ObjectUtil.checkNotNull(var1, "channel");
         ObjectUtil.checkNotNull(var2, "promise");
         EventLoop var3 = var1.eventLoop();
         if (var3.inEventLoop()) {
            this.doReleaseChannel(var1, var2);
         } else {
            var3.execute(new Runnable() {
               @Override
               public void run() {
                  SimpleChannelPool.this.doReleaseChannel(var1, var2);
               }
            });
         }
      } catch (Throwable var4) {
         this.closeAndFail(var1, var4, var2);
      }

      return var2;
   }

   private void doReleaseChannel(Channel var1, Promise<Void> var2) {
      try {
         assert var1.eventLoop().inEventLoop();

         if (var1.attr(POOL_KEY).getAndSet(null) != this) {
            this.closeAndFail(var1, new IllegalArgumentException("Channel " + var1 + " was not acquired from this ChannelPool"), var2);
         } else if (this.releaseHealthCheck) {
            this.doHealthCheckOnRelease(var1, var2);
         } else {
            this.releaseAndOffer(var1, var2);
         }
      } catch (Throwable var4) {
         this.closeAndFail(var1, var4, var2);
      }
   }

   private void doHealthCheckOnRelease(final Channel var1, final Promise<Void> var2) throws Exception {
      final Future var3 = this.healthCheck.isHealthy(var1);
      if (var3.isDone()) {
         this.releaseAndOfferIfHealthy(var1, var2, var3);
      } else {
         var3.addListener(new FutureListener<Boolean>() {
            @Override
            public void operationComplete(Future<Boolean> var1x) throws Exception {
               SimpleChannelPool.this.releaseAndOfferIfHealthy(var1, var2, var3);
            }
         });
      }
   }

   private void releaseAndOfferIfHealthy(Channel var1, Promise<Void> var2, Future<Boolean> var3) {
      try {
         if ((Boolean)var3.getNow()) {
            this.releaseAndOffer(var1, var2);
         } else {
            this.handler.channelReleased(var1);
            var2.setSuccess(null);
         }
      } catch (Throwable var5) {
         this.closeAndFail(var1, var5, var2);
      }
   }

   private void releaseAndOffer(Channel var1, Promise<Void> var2) throws Exception {
      if (this.offerChannel(var1)) {
         this.handler.channelReleased(var1);
         var2.setSuccess(null);
      } else {
         this.closeAndFail(var1, new SimpleChannelPool.ChannelPoolFullException(), var2);
      }
   }

   private void closeChannel(Channel var1) throws Exception {
      var1.attr(POOL_KEY).getAndSet(null);
      var1.close();
   }

   private void closeAndFail(Channel var1, Throwable var2, Promise<?> var3) {
      if (var1 != null) {
         try {
            this.closeChannel(var1);
         } catch (Throwable var5) {
            var3.tryFailure(var5);
         }
      }

      var3.tryFailure(var2);
   }

   protected Channel pollChannel() {
      return this.lastRecentUsed ? this.deque.pollLast() : this.deque.pollFirst();
   }

   protected boolean offerChannel(Channel var1) {
      return this.deque.offer(var1);
   }

   @Override
   public void close() {
      while (true) {
         Channel var1 = this.pollChannel();
         if (var1 == null) {
            return;
         }

         var1.close().awaitUninterruptibly();
      }
   }

   public Future<Void> closeAsync() {
      return GlobalEventExecutor.INSTANCE.submit(new Callable<Void>() {
         public Void call() throws Exception {
            SimpleChannelPool.this.close();
            return null;
         }
      });
   }

   private static final class ChannelPoolFullException extends IllegalStateException {
      private ChannelPoolFullException() {
         super("ChannelPool full");
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
