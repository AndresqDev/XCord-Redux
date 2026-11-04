package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

@ElementTypesAreNonnullByDefault
@Beta
@CanIgnoreReturnValue
@GwtIncompatible
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {
   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Runnable var1, @ParametricNullness T var2) {
      return TrustedListenableFutureTask.create(var1, (T)var2);
   }

   @Override
   protected final <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return TrustedListenableFutureTask.create(var1);
   }

   @Override
   public ListenableFuture<?> submit(Runnable var1) {
      return (ListenableFuture<?>)super.submit(var1);
   }

   @Override
   public <T> ListenableFuture<T> submit(Runnable var1, @ParametricNullness T var2) {
      return (ListenableFuture<T>)super.<T>submit(var1, (T)var2);
   }

   @Override
   public <T> ListenableFuture<T> submit(Callable<T> var1) {
      return (ListenableFuture<T>)super.<T>submit(var1);
   }
}
