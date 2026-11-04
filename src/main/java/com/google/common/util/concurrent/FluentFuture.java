package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DoNotMock("Use FluentFuture.from(Futures.immediate*Future) or SettableFuture")
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible(
   emulated = true
)
public abstract class FluentFuture<V> extends GwtFluentFutureCatchingSpecialization<V> {
   FluentFuture() {
   }

   public static <V> FluentFuture<V> from(ListenableFuture<V> var0) {
      return (FluentFuture<V>)(var0 instanceof FluentFuture ? (FluentFuture)var0 : new ForwardingFluentFuture<>(var0));
   }

   @Deprecated
   public static <V> FluentFuture<V> from(FluentFuture<V> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public final <X extends Throwable> FluentFuture<V> catching(Class<X> var1, Function<? super X, ? extends V> var2, Executor var3) {
      return (FluentFuture<V>)Futures.catching(this, var1, var2, var3);
   }

   @Partially.GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
   public final <X extends Throwable> FluentFuture<V> catchingAsync(Class<X> var1, AsyncFunction<? super X, ? extends V> var2, Executor var3) {
      return (FluentFuture<V>)Futures.catchingAsync(this, var1, var2, var3);
   }

   @GwtIncompatible
   public final FluentFuture<V> withTimeout(Duration var1, ScheduledExecutorService var2) {
      return this.withTimeout(Internal.toNanosSaturated(var1), TimeUnit.NANOSECONDS, var2);
   }

   @GwtIncompatible
   public final FluentFuture<V> withTimeout(long var1, TimeUnit var3, ScheduledExecutorService var4) {
      return (FluentFuture<V>)Futures.<V>withTimeout(this, var1, var3, var4);
   }

   public final <T> FluentFuture<T> transformAsync(AsyncFunction<? super V, T> var1, Executor var2) {
      return (FluentFuture<T>)Futures.<V, T>transformAsync(this, var1, var2);
   }

   public final <T> FluentFuture<T> transform(Function<? super V, T> var1, Executor var2) {
      return (FluentFuture<T>)Futures.<V, T>transform(this, var1, var2);
   }

   public final void addCallback(FutureCallback<? super V> var1, Executor var2) {
      Futures.addCallback(this, var1, var2);
   }

   abstract static class TrustedFuture<V> extends FluentFuture<V> implements AbstractFuture.Trusted<V> {
      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public final V get() throws InterruptedException, ExecutionException {
         return super.get();
      }

      @ParametricNullness
      @CanIgnoreReturnValue
      @Override
      public final V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
         return super.get(var1, var3);
      }

      @Override
      public final boolean isDone() {
         return super.isDone();
      }

      @Override
      public final boolean isCancelled() {
         return super.isCancelled();
      }

      @Override
      public final void addListener(Runnable var1, Executor var2) {
         super.addListener(var1, var2);
      }

      @CanIgnoreReturnValue
      @Override
      public final boolean cancel(boolean var1) {
         return super.cancel(var1);
      }
   }
}
