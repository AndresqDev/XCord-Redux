package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CombinedFuture<V> extends AggregateFuture<Object, V> {
   private CombinedFuture<V>.CombinedFutureInterruptibleTask<?> task;

   CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> var1, boolean var2, Executor var3, AsyncCallable<V> var4) {
      super(var1, var2, false);
      this.task = new CombinedFuture.AsyncCallableInterruptibleTask(var4, var3);
      this.init();
   }

   CombinedFuture(ImmutableCollection<? extends ListenableFuture<?>> var1, boolean var2, Executor var3, Callable<V> var4) {
      super(var1, var2, false);
      this.task = new CombinedFuture.CallableInterruptibleTask(var4, var3);
      this.init();
   }

   @Override
   void collectOneValue(int var1, Object var2) {
   }

   @Override
   void handleAllCompleted() {
      CombinedFuture.CombinedFutureInterruptibleTask var1 = this.task;
      if (var1 != null) {
         var1.execute();
      }
   }

   @Override
   void releaseResources(AggregateFuture.ReleaseResourcesReason var1) {
      super.releaseResources(var1);
      if (var1 == AggregateFuture.ReleaseResourcesReason.OUTPUT_FUTURE_DONE) {
         this.task = null;
      }
   }

   @Override
   protected void interruptTask() {
      CombinedFuture.CombinedFutureInterruptibleTask var1 = this.task;
      if (var1 != null) {
         var1.interruptTask();
      }
   }

   private final class AsyncCallableInterruptibleTask extends CombinedFuture<V>.CombinedFutureInterruptibleTask<ListenableFuture<V>> {
      private final AsyncCallable<V> callable;

      AsyncCallableInterruptibleTask(AsyncCallable<V> var2, Executor var3) {
         super(var3);
         this.callable = Preconditions.checkNotNull(var2);
      }

      ListenableFuture<V> runInterruptibly() throws Exception {
         ListenableFuture var1 = this.callable.call();
         return Preconditions.checkNotNull(
            var1, "AsyncCallable.call returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", this.callable
         );
      }

      void setValue(ListenableFuture<V> var1) {
         CombinedFuture.this.setFuture(var1);
      }

      @Override
      String toPendingString() {
         return this.callable.toString();
      }
   }

   private final class CallableInterruptibleTask extends CombinedFuture<V>.CombinedFutureInterruptibleTask<V> {
      private final Callable<V> callable;

      CallableInterruptibleTask(Callable<V> var2, Executor var3) {
         super(var3);
         this.callable = Preconditions.checkNotNull(var2);
      }

      @ParametricNullness
      @Override
      V runInterruptibly() throws Exception {
         return this.callable.call();
      }

      @Override
      void setValue(@ParametricNullness V var1) {
         CombinedFuture.this.set((V)var1);
      }

      @Override
      String toPendingString() {
         return this.callable.toString();
      }
   }

   private abstract class CombinedFutureInterruptibleTask<T> extends InterruptibleTask<T> {
      private final Executor listenerExecutor;

      CombinedFutureInterruptibleTask(Executor var2) {
         this.listenerExecutor = Preconditions.checkNotNull(var2);
      }

      @Override
      final boolean isDone() {
         return CombinedFuture.this.isDone();
      }

      final void execute() {
         try {
            this.listenerExecutor.execute(this);
         } catch (RejectedExecutionException var2) {
            CombinedFuture.this.setException(var2);
         }
      }

      @Override
      final void afterRanInterruptiblySuccess(@ParametricNullness T var1) {
         CombinedFuture.this.task = null;
         this.setValue((T)var1);
      }

      @Override
      final void afterRanInterruptiblyFailure(Throwable var1) {
         CombinedFuture.this.task = null;
         if (var1 instanceof ExecutionException) {
            CombinedFuture.this.setException(((ExecutionException)var1).getCause());
         } else if (var1 instanceof CancellationException) {
            CombinedFuture.this.cancel(false);
         } else {
            CombinedFuture.this.setException(var1);
         }
      }

      abstract void setValue(@ParametricNullness T var1);
   }
}
