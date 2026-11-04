package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@CanIgnoreReturnValue
@GwtCompatible
public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V> implements ListenableFuture<V> {
   protected ForwardingListenableFuture() {
   }

   protected abstract ListenableFuture<? extends V> delegate();

   @Override
   public void addListener(Runnable var1, Executor var2) {
      this.delegate().addListener(var1, var2);
   }

   public abstract static class SimpleForwardingListenableFuture<V> extends ForwardingListenableFuture<V> {
      private final ListenableFuture<V> delegate;

      protected SimpleForwardingListenableFuture(ListenableFuture<V> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      protected final ListenableFuture<V> delegate() {
         return this.delegate;
      }
   }
}
