package io.netty.channel;

import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

public final class VoidChannelPromise extends AbstractFuture<Void> implements ChannelPromise {
   private final Channel channel;
   private final ChannelFutureListener fireExceptionListener;

   public VoidChannelPromise(Channel var1, boolean var2) {
      ObjectUtil.checkNotNull(var1, "channel");
      this.channel = var1;
      if (var2) {
         this.fireExceptionListener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) throws Exception {
               Throwable var2 = var1.cause();
               if (var2 != null) {
                  VoidChannelPromise.this.fireException0(var2);
               }
            }
         };
      } else {
         this.fireExceptionListener = null;
      }
   }

   public VoidChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      fail();
      return this;
   }

   public VoidChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      fail();
      return this;
   }

   public VoidChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      return this;
   }

   public VoidChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      return this;
   }

   public VoidChannelPromise await() throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this;
      }
   }

   @Override
   public boolean await(long var1, TimeUnit var3) {
      fail();
      return false;
   }

   @Override
   public boolean await(long var1) {
      fail();
      return false;
   }

   public VoidChannelPromise awaitUninterruptibly() {
      fail();
      return this;
   }

   @Override
   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      fail();
      return false;
   }

   @Override
   public boolean awaitUninterruptibly(long var1) {
      fail();
      return false;
   }

   @Override
   public Channel channel() {
      return this.channel;
   }

   @Override
   public boolean isDone() {
      return false;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   @Override
   public boolean setUncancellable() {
      return true;
   }

   @Override
   public boolean isCancellable() {
      return false;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public Throwable cause() {
      return null;
   }

   public VoidChannelPromise sync() {
      fail();
      return this;
   }

   public VoidChannelPromise syncUninterruptibly() {
      fail();
      return this;
   }

   public VoidChannelPromise setFailure(Throwable var1) {
      this.fireException0(var1);
      return this;
   }

   public VoidChannelPromise setSuccess() {
      return this;
   }

   @Override
   public boolean tryFailure(Throwable var1) {
      this.fireException0(var1);
      return false;
   }

   @Override
   public boolean cancel(boolean var1) {
      return false;
   }

   @Override
   public boolean trySuccess() {
      return false;
   }

   private static void fail() {
      throw new IllegalStateException("void future");
   }

   public VoidChannelPromise setSuccess(Void var1) {
      return this;
   }

   public boolean trySuccess(Void var1) {
      return false;
   }

   public Void getNow() {
      return null;
   }

   @Override
   public ChannelPromise unvoid() {
      DefaultChannelPromise var1 = new DefaultChannelPromise(this.channel);
      if (this.fireExceptionListener != null) {
         var1.addListener(this.fireExceptionListener);
      }

      return var1;
   }

   @Override
   public boolean isVoid() {
      return true;
   }

   private void fireException0(Throwable var1) {
      if (this.fireExceptionListener != null && this.channel.isRegistered()) {
         this.channel.pipeline().fireExceptionCaught(var1);
      }
   }
}
