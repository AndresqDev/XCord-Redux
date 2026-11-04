package io.netty.channel;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;

public class DefaultChannelPromise extends DefaultPromise<Void> implements ChannelFlushPromiseNotifier.FlushCheckpoint, ChannelPromise {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelPromise(Channel var1) {
      this.channel = ObjectUtil.checkNotNull(var1, "channel");
   }

   public DefaultChannelPromise(Channel var1, EventExecutor var2) {
      super(var2);
      this.channel = ObjectUtil.checkNotNull(var1, "channel");
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
   public ChannelPromise setSuccess() {
      return this.setSuccess(null);
   }

   @Override
   public ChannelPromise setSuccess(Void var1) {
      super.setSuccess(var1);
      return this;
   }

   @Override
   public boolean trySuccess() {
      return this.trySuccess(null);
   }

   @Override
   public ChannelPromise setFailure(Throwable var1) {
      super.setFailure(var1);
      return this;
   }

   @Override
   public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   @Override
   public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   @Override
   public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   @Override
   public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   @Override
   public ChannelPromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   @Override
   public ChannelPromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   @Override
   public ChannelPromise await() throws InterruptedException {
      super.await();
      return this;
   }

   @Override
   public ChannelPromise awaitUninterruptibly() {
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

   @Override
   public ChannelPromise promise() {
      return this;
   }

   @Override
   protected void checkDeadLock() {
      if (this.channel().isRegistered()) {
         super.checkDeadLock();
      }
   }

   @Override
   public ChannelPromise unvoid() {
      return this;
   }

   @Override
   public boolean isVoid() {
      return false;
   }
}
