package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractTransformFuture<I, O, F, T> extends FluentFuture.TrustedFuture<O> implements Runnable {
   ListenableFuture<? extends I> inputFuture;
   F function;

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, AsyncFunction<? super I, ? extends O> var1, Executor var2) {
      Preconditions.checkNotNull(var2);
      AbstractTransformFuture.AsyncTransformFuture var3 = new AbstractTransformFuture.AsyncTransformFuture(var0, var1);
      var0.addListener(var3, MoreExecutors.rejectionPropagatingExecutor(var2, var3));
      return var3;
   }

   static <I, O> ListenableFuture<O> create(ListenableFuture<I> var0, Function<? super I, ? extends O> var1, Executor var2) {
      Preconditions.checkNotNull(var1);
      AbstractTransformFuture.TransformFuture var3 = new AbstractTransformFuture.TransformFuture(var0, var1);
      var0.addListener(var3, MoreExecutors.rejectionPropagatingExecutor(var2, var3));
      return var3;
   }

   AbstractTransformFuture(ListenableFuture<? extends I> var1, F var2) {
      this.inputFuture = Preconditions.checkNotNull(var1);
      this.function = Preconditions.checkNotNull((F)var2);
   }

   @Override
   public final void run() {
      ListenableFuture var1 = this.inputFuture;
      Object var2 = this.function;
      if (!(this.isCancelled() | var1 == null | var2 == null)) {
         this.inputFuture = null;
         if (var1.isCancelled()) {
            boolean var19 = this.setFuture(var1);
         } else {
            Object var3;
            try {
               var3 = Futures.getDone(var1);
            } catch (CancellationException var13) {
               this.cancel(false);
               return;
            } catch (ExecutionException var14) {
               this.setException(var14.getCause());
               return;
            } catch (RuntimeException var15) {
               this.setException(var15);
               return;
            } catch (Error var16) {
               this.setException(var16);
               return;
            }

            Object var4;
            label65: {
               try {
                  var4 = this.doTransform((F)var2, (I)var3);
                  break label65;
               } catch (Throwable var17) {
                  this.setException(var17);
               } finally {
                  this.function = null;
               }

               return;
            }

            this.setResult((T)var4);
         }
      }
   }

   @ParametricNullness
   @ForOverride
   abstract T doTransform(F var1, @ParametricNullness I var2) throws Exception;

   @ForOverride
   abstract void setResult(@ParametricNullness T var1);

   @Override
   protected final void afterDone() {
      this.maybePropagateCancellationTo(this.inputFuture);
      this.inputFuture = null;
      this.function = null;
   }

   @Override
   protected String pendingToString() {
      ListenableFuture var1 = this.inputFuture;
      Object var2 = this.function;
      String var3 = super.pendingToString();
      String var4 = "";
      if (var1 != null) {
         String var5 = String.valueOf(var1);
         var4 = new StringBuilder(16 + String.valueOf(var5).length()).append("inputFuture=[").append(var5).append("], ").toString();
      }

      if (var2 != null) {
         String var6 = String.valueOf(var2);
         return new StringBuilder(11 + String.valueOf(var4).length() + String.valueOf(var6).length())
            .append(var4)
            .append("function=[")
            .append(var6)
            .append("]")
            .toString();
      } else if (var3 != null) {
         String var10000 = String.valueOf(var4);
         String var10001 = String.valueOf(var3);
         return var10001.length() != 0 ? var10000.concat(var10001) : new String(var10000);
      } else {
         return null;
      }
   }

   private static final class AsyncTransformFuture<I, O>
      extends AbstractTransformFuture<I, O, AsyncFunction<? super I, ? extends O>, ListenableFuture<? extends O>> {
      AsyncTransformFuture(ListenableFuture<? extends I> var1, AsyncFunction<? super I, ? extends O> var2) {
         super(var1, var2);
      }

      ListenableFuture<? extends O> doTransform(AsyncFunction<? super I, ? extends O> var1, @ParametricNullness I var2) throws Exception {
         ListenableFuture var3 = var1.apply(var2);
         Preconditions.checkNotNull(var3, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", var1);
         return var3;
      }

      void setResult(ListenableFuture<? extends O> var1) {
         this.setFuture(var1);
      }
   }

   private static final class TransformFuture<I, O> extends AbstractTransformFuture<I, O, Function<? super I, ? extends O>, O> {
      TransformFuture(ListenableFuture<? extends I> var1, Function<? super I, ? extends O> var2) {
         super(var1, var2);
      }

      @ParametricNullness
      O doTransform(Function<? super I, ? extends O> var1, @ParametricNullness I var2) {
         return (O)var1.apply(var2);
      }

      @Override
      void setResult(@ParametricNullness O var1) {
         this.set((O)var1);
      }
   }
}
