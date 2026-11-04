package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class TrustedListenableFutureTask<V> extends FluentFuture.TrustedFuture<V> implements RunnableFuture<V> {
   private volatile InterruptibleTask<?> task;

   static <V> TrustedListenableFutureTask<V> create(AsyncCallable<V> var0) {
      return new TrustedListenableFutureTask<>(var0);
   }

   static <V> TrustedListenableFutureTask<V> create(Callable<V> var0) {
      return new TrustedListenableFutureTask<>(var0);
   }

   static <V> TrustedListenableFutureTask<V> create(Runnable var0, @ParametricNullness V var1) {
      return new TrustedListenableFutureTask<>(Executors.callable(var0, (V)var1));
   }

   TrustedListenableFutureTask(Callable<V> var1) {
      this.task = new TrustedListenableFutureTask.TrustedFutureInterruptibleTask(var1);
   }

   TrustedListenableFutureTask(AsyncCallable<V> var1) {
      this.task = new TrustedListenableFutureTask.TrustedFutureInterruptibleAsyncTask(var1);
   }

   @Override
   public void run() {
      InterruptibleTask var1 = this.task;
      if (var1 != null) {
         var1.run();
      }

      this.task = null;
   }

   @Override
   protected void afterDone() {
      super.afterDone();
      if (this.wasInterrupted()) {
         InterruptibleTask var1 = this.task;
         if (var1 != null) {
            var1.interruptTask();
         }
      }

      this.task = null;
   }

   @Override
   protected String pendingToString() {
      InterruptibleTask var1 = this.task;
      if (var1 != null) {
         String var2 = String.valueOf(var1);
         return new StringBuilder(7 + String.valueOf(var2).length()).append("task=[").append(var2).append("]").toString();
      } else {
         return super.pendingToString();
      }
   }

   private final class TrustedFutureInterruptibleAsyncTask extends InterruptibleTask<ListenableFuture<V>> {
      private final AsyncCallable<V> callable;

      TrustedFutureInterruptibleAsyncTask(AsyncCallable<V> var2) {
         this.callable = Preconditions.checkNotNull(var2);
      }

      @Override
      final boolean isDone() {
         return TrustedListenableFutureTask.this.isDone();
      }

      ListenableFuture<V> runInterruptibly() throws Exception {
         return Preconditions.checkNotNull(
            this.callable.call(), "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable
         );
      }

      void afterRanInterruptiblySuccess(ListenableFuture<V> var1) {
         TrustedListenableFutureTask.this.setFuture(var1);
      }

      @Override
      void afterRanInterruptiblyFailure(Throwable var1) {
         TrustedListenableFutureTask.this.setException(var1);
      }

      @Override
      String toPendingString() {
         return this.callable.toString();
      }
   }

   private final class TrustedFutureInterruptibleTask extends InterruptibleTask<V> {
      private final Callable<V> callable;

      TrustedFutureInterruptibleTask(Callable<V> var2) {
         this.callable = Preconditions.checkNotNull(var2);
      }

      @Override
      final boolean isDone() {
         return TrustedListenableFutureTask.this.isDone();
      }

      @ParametricNullness
      @Override
      V runInterruptibly() throws Exception {
         return this.callable.call();
      }

      @Override
      void afterRanInterruptiblySuccess(@ParametricNullness V var1) {
         TrustedListenableFutureTask.this.set((V)var1);
      }

      @Override
      void afterRanInterruptiblyFailure(Throwable var1) {
         TrustedListenableFutureTask.this.setException(var1);
      }

      @Override
      String toPendingString() {
         return this.callable.toString();
      }
   }
}
