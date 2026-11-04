package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class ImmediateFuture<V> implements ListenableFuture<V> {
   static final ListenableFuture<?> NULL = new ImmediateFuture(null);
   private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
   @ParametricNullness
   private final V value;

   ImmediateFuture(@ParametricNullness V var1) {
      this.value = (V)var1;
   }

   @Override
   public void addListener(Runnable var1, Executor var2) {
      Preconditions.checkNotNull(var1, "Runnable was null.");
      Preconditions.checkNotNull(var2, "Executor was null.");

      try {
         var2.execute(var1);
      } catch (RuntimeException var6) {
         Logger var10000 = log;
         Level var10001 = Level.SEVERE;
         String var4 = String.valueOf(var1);
         String var5 = String.valueOf(var2);
         var10000.log(
            var10001,
            new StringBuilder(57 + String.valueOf(var4).length() + String.valueOf(var5).length())
               .append("RuntimeException while executing runnable ")
               .append(var4)
               .append(" with executor ")
               .append(var5)
               .toString(),
            (Throwable)var6
         );
      }
   }

   @Override
   public boolean cancel(boolean var1) {
      return false;
   }

   @ParametricNullness
   @Override
   public V get() {
      return this.value;
   }

   @ParametricNullness
   @Override
   public V get(long var1, TimeUnit var3) throws ExecutionException {
      Preconditions.checkNotNull(var3);
      return this.get();
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public boolean isDone() {
      return true;
   }

   @Override
   public String toString() {
      String var1 = super.toString();
      String var2 = String.valueOf(this.value);
      return new StringBuilder(27 + String.valueOf(var1).length() + String.valueOf(var2).length())
         .append(var1)
         .append("[status=SUCCESS, result=[")
         .append(var2)
         .append("]]")
         .toString();
   }

   static final class ImmediateCancelledFuture<V> extends AbstractFuture.TrustedFuture<V> {
      ImmediateCancelledFuture() {
         this.cancel(false);
      }
   }

   static final class ImmediateFailedFuture<V> extends AbstractFuture.TrustedFuture<V> {
      ImmediateFailedFuture(Throwable var1) {
         this.setException(var1);
      }
   }
}
