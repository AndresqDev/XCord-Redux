package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Callables {
   private Callables() {
   }

   public static <T> Callable<T> returning(@ParametricNullness final T var0) {
      return new Callable<T>() {
         @ParametricNullness
         @Override
         public T call() {
            return (T)var0;
         }
      };
   }

   @Beta
   @GwtIncompatible
   public static <T> AsyncCallable<T> asAsyncCallable(final Callable<T> var0, final ListeningExecutorService var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new AsyncCallable<T>() {
         @Override
         public ListenableFuture<T> call() throws Exception {
            return var1.submit(var0);
         }
      };
   }

   @GwtIncompatible
   static <T> Callable<T> threadRenaming(final Callable<T> var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var0);
      return new Callable<T>() {
         @ParametricNullness
         @Override
         public T call() throws Exception {
            Thread var1x = Thread.currentThread();
            String var2 = var1x.getName();
            boolean var3 = Callables.trySetName((String)var1.get(), var1x);

            Object var4;
            try {
               var4 = var0.call();
            } finally {
               if (var3) {
                  boolean var7 = Callables.trySetName(var2, var1x);
               }
            }

            return (T)var4;
         }
      };
   }

   @GwtIncompatible
   static Runnable threadRenaming(final Runnable var0, final Supplier<String> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var0);
      return new Runnable() {
         @Override
         public void run() {
            Thread var1x = Thread.currentThread();
            String var2 = var1x.getName();
            boolean var3 = Callables.trySetName((String)var1.get(), var1x);

            try {
               var0.run();
            } finally {
               if (var3) {
                  boolean var6 = Callables.trySetName(var2, var1x);
               }
            }
         }
      };
   }

   @GwtIncompatible
   private static boolean trySetName(String var0, Thread var1) {
      try {
         var1.setName(var0);
         return true;
      } catch (SecurityException var3) {
         return false;
      }
   }
}
