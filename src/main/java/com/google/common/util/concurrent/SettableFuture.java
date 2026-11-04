package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {
   public static <V> SettableFuture<V> create() {
      return new SettableFuture<>();
   }

   @CanIgnoreReturnValue
   @Override
   public boolean set(@ParametricNullness V var1) {
      return super.set((V)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean setException(Throwable var1) {
      return super.setException(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean setFuture(ListenableFuture<? extends V> var1) {
      return super.setFuture(var1);
   }

   private SettableFuture() {
   }
}
