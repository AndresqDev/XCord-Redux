package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public class ListenableFutureTask<V> extends FutureTask<V> implements ListenableFuture<V> {
   private final ExecutionList executionList = new ExecutionList();

   public static <V> ListenableFutureTask<V> create(Callable<V> var0) {
      return new ListenableFutureTask<>(var0);
   }

   public static <V> ListenableFutureTask<V> create(Runnable var0, @ParametricNullness V var1) {
      return new ListenableFutureTask<>(var0, (V)var1);
   }

   ListenableFutureTask(Callable<V> var1) {
      super(var1);
   }

   ListenableFutureTask(Runnable var1, @ParametricNullness V var2) {
      super(var1, (V)var2);
   }

   @Override
   public void addListener(Runnable var1, Executor var2) {
      this.executionList.add(var1, var2);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public V get(long var1, TimeUnit var3) throws TimeoutException, InterruptedException, ExecutionException {
      long var4 = var3.toNanos(var1);
      return var4 <= 2147483647999999999L ? super.get(var1, var3) : super.get(Math.min(var4, 2147483647999999999L), TimeUnit.NANOSECONDS);
   }

   @Override
   protected void done() {
      this.executionList.execute();
   }
}
