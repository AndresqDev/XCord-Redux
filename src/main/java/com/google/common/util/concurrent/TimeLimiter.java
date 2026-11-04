package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@DoNotMock("Use FakeTimeLimiter")
@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public interface TimeLimiter {
   <T> T newProxy(T var1, Class<T> var2, long var3, TimeUnit var5);

   default <T> T newProxy(T var1, Class<T> var2, Duration var3) {
      return this.newProxy((T)var1, var2, Internal.toNanosSaturated(var3), TimeUnit.NANOSECONDS);
   }

   @CanIgnoreReturnValue
   <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException, ExecutionException;

   @CanIgnoreReturnValue
   default <T> T callWithTimeout(Callable<T> var1, Duration var2) throws TimeoutException, InterruptedException, ExecutionException {
      return this.callWithTimeout(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   @CanIgnoreReturnValue
   <T> T callUninterruptiblyWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, ExecutionException;

   @CanIgnoreReturnValue
   default <T> T callUninterruptiblyWithTimeout(Callable<T> var1, Duration var2) throws TimeoutException, ExecutionException {
      return this.callUninterruptiblyWithTimeout(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   void runWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException;

   default void runWithTimeout(Runnable var1, Duration var2) throws TimeoutException, InterruptedException {
      this.runWithTimeout(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }

   void runUninterruptiblyWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException;

   default void runUninterruptiblyWithTimeout(Runnable var1, Duration var2) throws TimeoutException {
      this.runUninterruptiblyWithTimeout(var1, Internal.toNanosSaturated(var2), TimeUnit.NANOSECONDS);
   }
}
