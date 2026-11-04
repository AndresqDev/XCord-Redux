package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;

@ElementTypesAreNonnullByDefault
@CanIgnoreReturnValue
@GwtIncompatible
public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService implements ListeningExecutorService {
   protected ForwardingListeningExecutorService() {
   }

   protected abstract ListeningExecutorService delegate();

   @Override
   public <T> ListenableFuture<T> submit(Callable<T> var1) {
      return this.delegate().submit(var1);
   }

   @Override
   public ListenableFuture<?> submit(Runnable var1) {
      return this.delegate().submit(var1);
   }

   @Override
   public <T> ListenableFuture<T> submit(Runnable var1, @ParametricNullness T var2) {
      return this.delegate().submit(var1, (T)var2);
   }
}
