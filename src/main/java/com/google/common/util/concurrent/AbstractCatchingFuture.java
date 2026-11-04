package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractCatchingFuture<V, X extends Throwable, F, T> extends FluentFuture.TrustedFuture<V> implements Runnable {
   ListenableFuture<? extends V> inputFuture;
   Class<X> exceptionType;
   F fallback;

   static <V, X extends Throwable> ListenableFuture<V> create(
      ListenableFuture<? extends V> var0, Class<X> var1, Function<? super X, ? extends V> var2, Executor var3
   ) {
      AbstractCatchingFuture.CatchingFuture var4 = new AbstractCatchingFuture.CatchingFuture(var0, var1, var2);
      var0.addListener(var4, MoreExecutors.rejectionPropagatingExecutor(var3, var4));
      return var4;
   }

   static <X extends Throwable, V> ListenableFuture<V> create(
      ListenableFuture<? extends V> var0, Class<X> var1, AsyncFunction<? super X, ? extends V> var2, Executor var3
   ) {
      AbstractCatchingFuture.AsyncCatchingFuture var4 = new AbstractCatchingFuture.AsyncCatchingFuture(var0, var1, var2);
      var0.addListener(var4, MoreExecutors.rejectionPropagatingExecutor(var3, var4));
      return var4;
   }

   AbstractCatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, F var3) {
      this.inputFuture = Preconditions.checkNotNull(var1);
      this.exceptionType = Preconditions.checkNotNull(var2);
      this.fallback = Preconditions.checkNotNull((F)var3);
   }

   @Override
   public final void run() {
      ListenableFuture var1 = this.inputFuture;
      Class var2 = this.exceptionType;
      Object var3 = this.fallback;
      if (!(var1 == null | var2 == null | var3 == null) && !this.isCancelled()) {
         this.inputFuture = null;
         Object var4 = null;
         Object var5 = null;

         try {
            if (var1 instanceof InternalFutureFailureAccess) {
               var5 = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)var1);
            }

            if (var5 == null) {
               var4 = Futures.getDone(var1);
            }
         } catch (ExecutionException var16) {
            var5 = var16.getCause();
            if (var5 == null) {
               String var7 = String.valueOf(var1.getClass());
               String var8 = String.valueOf(var16.getClass());
               var5 = new NullPointerException(
                  new StringBuilder(35 + String.valueOf(var7).length() + String.valueOf(var8).length())
                     .append("Future type ")
                     .append(var7)
                     .append(" threw ")
                     .append(var8)
                     .append(" without a cause")
                     .toString()
               );
            }
         } catch (Throwable var17) {
            var5 = var17;
         }

         if (var5 == null) {
            this.set(NullnessCasts.uncheckedCastNullableTToT((V)var4));
         } else if (!Platform.isInstanceOfThrowableClass((Throwable)var5, var2)) {
            this.setFuture(var1);
         } else {
            Object var6 = var5;

            Object var18;
            label91: {
               try {
                  var18 = this.doFallback((F)var3, (X)var6);
                  break label91;
               } catch (Throwable var14) {
                  this.setException(var14);
               } finally {
                  this.exceptionType = null;
                  this.fallback = null;
               }

               return;
            }

            this.setResult((T)var18);
         }
      }
   }

   @Override
   protected String pendingToString() {
      ListenableFuture var1 = this.inputFuture;
      Class var2 = this.exceptionType;
      Object var3 = this.fallback;
      String var4 = super.pendingToString();
      String var5 = "";
      if (var1 != null) {
         String var6 = String.valueOf(var1);
         var5 = new StringBuilder(16 + String.valueOf(var6).length()).append("inputFuture=[").append(var6).append("], ").toString();
      }

      if (var2 != null && var3 != null) {
         String var7 = String.valueOf(var2);
         String var8 = String.valueOf(var3);
         return new StringBuilder(29 + String.valueOf(var5).length() + String.valueOf(var7).length() + String.valueOf(var8).length())
            .append(var5)
            .append("exceptionType=[")
            .append(var7)
            .append("], fallback=[")
            .append(var8)
            .append("]")
            .toString();
      } else if (var4 != null) {
         String var10000 = String.valueOf(var5);
         String var10001 = String.valueOf(var4);
         return var10001.length() != 0 ? var10000.concat(var10001) : new String(var10000);
      } else {
         return null;
      }
   }

   @ParametricNullness
   @ForOverride
   abstract T doFallback(F var1, X var2) throws Exception;

   @ForOverride
   abstract void setResult(@ParametricNullness T var1);

   @Override
   protected final void afterDone() {
      this.maybePropagateCancellationTo(this.inputFuture);
      this.inputFuture = null;
      this.exceptionType = null;
      this.fallback = null;
   }

   private static final class AsyncCatchingFuture<V, X extends Throwable>
      extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>, ListenableFuture<? extends V>> {
      AsyncCatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, AsyncFunction<? super X, ? extends V> var3) {
         super(var1, var2, var3);
      }

      ListenableFuture<? extends V> doFallback(AsyncFunction<? super X, ? extends V> var1, X var2) throws Exception {
         ListenableFuture var3 = var1.apply(var2);
         Preconditions.checkNotNull(var3, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", var1);
         return var3;
      }

      void setResult(ListenableFuture<? extends V> var1) {
         this.setFuture(var1);
      }
   }

   private static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>, V> {
      CatchingFuture(ListenableFuture<? extends V> var1, Class<X> var2, Function<? super X, ? extends V> var3) {
         super(var1, var2, var3);
      }

      @ParametricNullness
      V doFallback(Function<? super X, ? extends V> var1, X var2) throws Exception {
         return (V)var1.apply(var2);
      }

      @Override
      void setResult(@ParametricNullness V var1) {
         this.set((V)var1);
      }
   }
}
