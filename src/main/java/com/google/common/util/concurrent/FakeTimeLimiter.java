package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@Beta
@CanIgnoreReturnValue
@GwtIncompatible
public final class FakeTimeLimiter implements TimeLimiter {
   @Override
   public <T> T newProxy(T var1, Class<T> var2, long var3, TimeUnit var5) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var5);
      return (T)var1;
   }

   @ParametricNullness
   @Override
   public <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws ExecutionException {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);

      try {
         return (T)var1.call();
      } catch (RuntimeException var6) {
         throw new UncheckedExecutionException(var6);
      } catch (Exception var7) {
         throw new ExecutionException(var7);
      } catch (Error var8) {
         throw new ExecutionError(var8);
      } catch (Throwable var9) {
         throw new ExecutionException(var9);
      }
   }

   @ParametricNullness
   @Override
   public <T> T callUninterruptiblyWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws ExecutionException {
      return this.callWithTimeout(var1, var2, var4);
   }

   @Override
   public void runWithTimeout(Runnable var1, long var2, TimeUnit var4) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);

      try {
         var1.run();
      } catch (RuntimeException var6) {
         throw new UncheckedExecutionException(var6);
      } catch (Error var7) {
         throw new ExecutionError(var7);
      } catch (Throwable var8) {
         throw new UncheckedExecutionException(var8);
      }
   }

   @Override
   public void runUninterruptiblyWithTimeout(Runnable var1, long var2, TimeUnit var4) {
      this.runWithTimeout(var1, var2, var4);
   }
}
