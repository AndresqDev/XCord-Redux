package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

final class VoidChannelGroupFuture implements ChannelGroupFuture {
   private static final Iterator<ChannelFuture> EMPTY = Collections.<ChannelFuture>emptyList().iterator();
   private final ChannelGroup group;

   VoidChannelGroupFuture(ChannelGroup var1) {
      this.group = var1;
   }

   @Override
   public ChannelGroup group() {
      return this.group;
   }

   @Override
   public ChannelFuture find(Channel var1) {
      return null;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   @Override
   public ChannelGroupException cause() {
      return null;
   }

   @Override
   public boolean isPartialSuccess() {
      return false;
   }

   @Override
   public boolean isPartialFailure() {
      return false;
   }

   @Override
   public ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> var1) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... var1) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture await() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture awaitUninterruptibly() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture syncUninterruptibly() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture sync() {
      throw reject();
   }

   @Override
   public Iterator<ChannelFuture> iterator() {
      return EMPTY;
   }

   @Override
   public boolean isCancellable() {
      return false;
   }

   @Override
   public boolean await(long var1, TimeUnit var3) {
      throw reject();
   }

   @Override
   public boolean await(long var1) {
      throw reject();
   }

   @Override
   public boolean awaitUninterruptibly(long var1, TimeUnit var3) {
      throw reject();
   }

   @Override
   public boolean awaitUninterruptibly(long var1) {
      throw reject();
   }

   public Void getNow() {
      return null;
   }

   @Override
   public boolean cancel(boolean var1) {
      return false;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public boolean isDone() {
      return false;
   }

   public Void get() {
      throw reject();
   }

   public Void get(long var1, TimeUnit var3) {
      throw reject();
   }

   private static RuntimeException reject() {
      return new IllegalStateException("void future");
   }
}
