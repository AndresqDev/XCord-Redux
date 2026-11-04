package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class SimpleTimeLimiter implements TimeLimiter {
   private final ExecutorService executor;

   private SimpleTimeLimiter(ExecutorService var1) {
      this.executor = Preconditions.checkNotNull(var1);
   }

   public static SimpleTimeLimiter create(ExecutorService var0) {
      return new SimpleTimeLimiter(var0);
   }

   @Override
   public <T> T newProxy(final T var1, Class<T> var2, final long var3, final TimeUnit var5) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var5);
      checkPositiveTimeout(var3);
      Preconditions.checkArgument(var2.isInterface(), "interfaceType must be an interface type");
      final Set var6 = findInterruptibleMethods(var2);
      InvocationHandler var7 = new InvocationHandler() {
         @Override
         public Object invoke(Object var1x, final Method var2, final Object[] var3x) throws Throwable {
            Callable var4 = new Callable<Object>() {
               @Override
               public Object call() throws Exception {
                  try {
                     return var2.invoke(var1, var3x);
                  } catch (InvocationTargetException var2x) {
                     throw SimpleTimeLimiter.throwCause(var2x, false);
                  }
               }
            };
            return SimpleTimeLimiter.this.callWithTimeout(var4, var3, var5, var6.contains(var2));
         }
      };
      return newProxy(var2, var7);
   }

   private static <T> T newProxy(Class<T> var0, InvocationHandler var1) {
      Object var2 = Proxy.newProxyInstance(var0.getClassLoader(), new Class[]{var0}, var1);
      return (T)var0.cast(var2);
   }

   private <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4, boolean var5) throws Exception {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);
      checkPositiveTimeout(var2);
      Future var6 = this.executor.submit(var1);

      try {
         if (var5) {
            try {
               return (T)var6.get(var2, var4);
            } catch (InterruptedException var8) {
               var6.cancel(true);
               throw var8;
            }
         } else {
            return Uninterruptibles.getUninterruptibly(var6, var2, var4);
         }
      } catch (ExecutionException var9) {
         throw throwCause(var9, true);
      } catch (TimeoutException var10) {
         var6.cancel(true);
         throw new UncheckedTimeoutException(var10);
      }
   }

   @CanIgnoreReturnValue
   @Override
   public <T> T callWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException, ExecutionException {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);
      checkPositiveTimeout(var2);
      Future var5 = this.executor.submit(var1);

      try {
         return (T)var5.get(var2, var4);
      } catch (TimeoutException | InterruptedException var7) {
         var5.cancel(true);
         throw var7;
      } catch (ExecutionException var8) {
         this.wrapAndThrowExecutionExceptionOrError(var8.getCause());
         throw new AssertionError();
      }
   }

   @CanIgnoreReturnValue
   @Override
   public <T> T callUninterruptiblyWithTimeout(Callable<T> var1, long var2, TimeUnit var4) throws TimeoutException, ExecutionException {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);
      checkPositiveTimeout(var2);
      Future var5 = this.executor.submit(var1);

      try {
         return Uninterruptibles.getUninterruptibly(var5, var2, var4);
      } catch (TimeoutException var7) {
         var5.cancel(true);
         throw var7;
      } catch (ExecutionException var8) {
         this.wrapAndThrowExecutionExceptionOrError(var8.getCause());
         throw new AssertionError();
      }
   }

   @Override
   public void runWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException, InterruptedException {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);
      checkPositiveTimeout(var2);
      Future var5 = this.executor.submit(var1);

      try {
         var5.get(var2, var4);
      } catch (TimeoutException | InterruptedException var7) {
         var5.cancel(true);
         throw var7;
      } catch (ExecutionException var8) {
         this.wrapAndThrowRuntimeExecutionExceptionOrError(var8.getCause());
         throw new AssertionError();
      }
   }

   @Override
   public void runUninterruptiblyWithTimeout(Runnable var1, long var2, TimeUnit var4) throws TimeoutException {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var4);
      checkPositiveTimeout(var2);
      Future var5 = this.executor.submit(var1);

      try {
         Uninterruptibles.getUninterruptibly(var5, var2, var4);
      } catch (TimeoutException var7) {
         var5.cancel(true);
         throw var7;
      } catch (ExecutionException var8) {
         this.wrapAndThrowRuntimeExecutionExceptionOrError(var8.getCause());
         throw new AssertionError();
      }
   }

   private static Exception throwCause(Exception var0, boolean var1) throws Exception {
      Throwable var2 = var0.getCause();
      if (var2 == null) {
         throw var0;
      } else {
         if (var1) {
            StackTraceElement[] var3 = ObjectArrays.concat(var2.getStackTrace(), var0.getStackTrace(), StackTraceElement.class);
            var2.setStackTrace(var3);
         }

         if (var2 instanceof Exception) {
            throw (Exception)var2;
         } else if (var2 instanceof Error) {
            throw (Error)var2;
         } else {
            throw var0;
         }
      }
   }

   private static Set<Method> findInterruptibleMethods(Class<?> var0) {
      HashSet var1 = Sets.newHashSet();

      for (Method var5 : var0.getMethods()) {
         if (declaresInterruptedEx(var5)) {
            var1.add(var5);
         }
      }

      return var1;
   }

   private static boolean declaresInterruptedEx(Method var0) {
      for (Class var4 : var0.getExceptionTypes()) {
         if (var4 == InterruptedException.class) {
            return true;
         }
      }

      return false;
   }

   private void wrapAndThrowExecutionExceptionOrError(Throwable var1) throws ExecutionException {
      if (var1 instanceof Error) {
         throw new ExecutionError((Error)var1);
      } else if (var1 instanceof RuntimeException) {
         throw new UncheckedExecutionException(var1);
      } else {
         throw new ExecutionException(var1);
      }
   }

   private void wrapAndThrowRuntimeExecutionExceptionOrError(Throwable var1) {
      if (var1 instanceof Error) {
         throw new ExecutionError((Error)var1);
      } else {
         throw new UncheckedExecutionException(var1);
      }
   }

   private static void checkPositiveTimeout(long var0) {
      Preconditions.checkArgument(var0 > 0L, "timeout must be positive: %s", var0);
   }
}
