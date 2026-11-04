package io.netty.channel;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelProgressivePromise
   extends DefaultProgressivePromise<Void>
   implements ChannelFlushPromiseNotifier.FlushCheckpoint,
   ChannelProgressivePromise {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelProgressivePromise(Channel var1) {
      this.channel = var1;
   }

   public DefaultChannelProgressivePromise(Channel var1, EventExecutor var2) {
      super(var2);
      this.channel = var1;
   }

   @Override
   protected EventExecutor executor() {
      EventExecutor var1 = super.executor();
      return (EventExecutor)(var1 == null ? this.channel().eventLoop() : var1);
   }

   @Override
   public Channel channel() {
      return this.channel;
   }

   @Override
   public ChannelProgressivePromise setSuccess() {
      return this.setSuccess(null);
   }

   @Override
   public ChannelProgressivePromise setSuccess(Void var1) {
      super.setSuccess(var1);
      return this;
   }

   @Override
   public boolean trySuccess() {
      return this.trySuccess(null);
   }

   @Override
   public ChannelProgressivePromise setFailure(Throwable var1) {
      super.setFailure(var1);
      return this;
   }

   @Override
   public ChannelProgressivePromise setProgress(long var1, long var3) {
      super.setProgress(var1, var3);
      return this;
   }

   @Override
   public ChannelProgressivePromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   @Override
   public ChannelProgressivePromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   @Override
   public ChannelProgressivePromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   @Override
   public ChannelProgressivePromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   @Override
   public ChannelProgressivePromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   @Override
   public ChannelProgressivePromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   @Override
   public ChannelProgressivePromise await() throws InterruptedException {
      super.await();
      return this;
   }

   @Override
   public ChannelProgressivePromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   @Override
   public long flushCheckpoint() {
      return this.checkpoint;
   }

   @Override
   public void flushCheckpoint(long var1) {
      this.checkpoint = var1;
   }

   public ChannelProgressivePromise promise() {
      return this;
   }

   @Override
   protected void checkDeadLock() {
      if (this.channel().isRegistered()) {
         super.checkDeadLock();
      }
   }

   @Override
   public ChannelProgressivePromise unvoid() {
      return this;
   }

   @Override
   public boolean isVoid() {
      return false;
   }
}
