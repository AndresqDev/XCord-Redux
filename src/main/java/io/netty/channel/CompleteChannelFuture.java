package io.netty.channel;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;

abstract class CompleteChannelFuture extends CompleteFuture<Void> implements ChannelFuture {
   private final Channel channel;

   protected CompleteChannelFuture(Channel var1, EventExecutor var2) {
      super(var2);
      this.channel = ObjectUtil.checkNotNull(var1, "channel");
   }

   @Override
   protected EventExecutor executor() {
      EventExecutor var1 = super.executor();
      return (EventExecutor)(var1 == null ? this.channel().eventLoop() : var1);
   }

   @Override
   public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.addListener(var1);
      return this;
   }

   @Override
   public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.addListeners(var1);
      return this;
   }

   @Override
   public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      super.removeListener(var1);
      return this;
   }

   @Override
   public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      super.removeListeners(var1);
      return this;
   }

   @Override
   public ChannelFuture syncUninterruptibly() {
      return this;
   }

   @Override
   public ChannelFuture sync() throws InterruptedException {
      return this;
   }

   @Override
   public ChannelFuture await() throws InterruptedException {
      return this;
   }

   @Override
   public ChannelFuture awaitUninterruptibly() {
      return this;
   }

   @Override
   public Channel channel() {
      return this.channel;
   }

   public Void getNow() {
      return null;
   }

   @Override
   public boolean isVoid() {
      return false;
   }
}
