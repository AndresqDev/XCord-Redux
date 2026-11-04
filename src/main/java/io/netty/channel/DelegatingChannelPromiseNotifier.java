package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class DelegatingChannelPromiseNotifier implements ChannelFutureListener, ChannelPromise {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DelegatingChannelPromiseNotifier.class);
   private final ChannelPromise delegate;
   private final boolean logNotifyFailure;

   public DelegatingChannelPromiseNotifier(ChannelPromise var1) {
      this(var1, !(var1 instanceof VoidChannelPromise));
   }

   public DelegatingChannelPromiseNotifier(ChannelPromise var1, boolean var2) {
      this.delegate = ObjectUtil.checkNotNull(var1, "delegate");
      this.logNotifyFailure = var2;
   }

   public void operationComplete(ChannelFuture var1) throws Exception {
      InternalLogger var2 = this.logNotifyFailure ? logger : null;
      if (var1.isSuccess()) {
         Void var3 = var1.get();
         PromiseNotificationUtil.trySuccess(this.delegate, var3, var2);
      } else if (var1.isCancelled()) {
         PromiseNotificationUtil.tryCancel(this.delegate, var2);
      } else {
         Throwable var4 = var1.cause();
         PromiseNotificationUtil.tryFailure(this.delegate, var4, var2);
      }
   }

   @Override
   public Channel channel() {
      return this.delegate.channel();
   }

   @Override
   public ChannelPromise setSuccess(Void var1) {
      this.delegate.setSuccess(var1);
      return this;
   }

   @Override
   public ChannelPromise setSuccess() {
      this.delegate.setSuccess();
      return this;
   }

   @Override
   public boolean trySuccess() {
      return this.delegate.trySuccess();
   }

   public boolean trySuccess(Void var1) {
      return this.delegate.trySuccess(var1);
   }

   @Override
   public ChannelPromise setFailure(Throwable var1) {
      this.delegate.setFailure(var1);
      return this;
   }

   @Override
   public ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      this.delegate.addListener(var1);
      return this;
   }

   @Override
   public ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      this.delegate.addListeners(var1);
      return this;
   }

   @Override
   public ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      this.delegate.removeListener(var1);
      return this;
   }

   @Override
   public ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      this.delegate.removeListeners(var1);
      return this;
   }

   @Override
   public boolean tryFailure(Throwable var1) {
      return this.delegate.tryFailure(var1);
   }

   @Override
   public boolean setUncancellable() {
      return this.delegate.setUncancellable();
   }

   @Override
   public ChannelPromise await() throws InterruptedException {
      this.delegate.await();
      return this;
   }

   @Override
   public ChannelPromise awaitUninterruptibly() {
      this.delegate.awaitUninterruptibly();
      return this;
   }

   @Override
   public boolean isVoid() {
      return this.delegate.isVoid();
   }

   @Override
   public ChannelPromise unvoid() {
      return this.isVoid() ? new DelegatingChannelPromiseNotifier(this.delegate.unvoid()) : this;
   }

   @Override
   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      return this.delegate.await(var1, var3);
   }

   @Override
   public boolean await(long var1) throws InterruptedException {
      return this.delegate.await(var1);
   }

   @Override
   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      return this.delegate.awaitUninterruptibly(var1, var3);
   }

   @Override
   public boolean awaitUninterruptibly(long var1) {
      return this.delegate.awaitUninterruptibly(var1);
   }

   public Void getNow() {
      return this.delegate.getNow();
   }

   @Override
   public boolean cancel(boolean var1) {
      return this.delegate.cancel(var1);
   }

   @Override
   public boolean isCancelled() {
      return this.delegate.isCancelled();
   }

   @Override
   public boolean isDone() {
      return this.delegate.isDone();
   }

   public Void get() throws InterruptedException, ExecutionException {
      return this.delegate.get();
   }

   public Void get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      return this.delegate.get(var1, var3);
   }

   @Override
   public ChannelPromise sync() throws InterruptedException {
      this.delegate.sync();
      return this;
   }

   @Override
   public ChannelPromise syncUninterruptibly() {
      this.delegate.syncUninterruptibly();
      return this;
   }

   @Override
   public boolean isSuccess() {
      return this.delegate.isSuccess();
   }

   @Override
   public boolean isCancellable() {
      return this.delegate.isCancellable();
   }

   @Override
   public Throwable cause() {
      return this.delegate.cause();
   }
}
