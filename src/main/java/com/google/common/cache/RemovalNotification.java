package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.AbstractMap.SimpleImmutableEntry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class RemovalNotification<K, V> extends SimpleImmutableEntry<K, V> {
   private final RemovalCause cause;
   private static final long serialVersionUID = 0L;

   public static <K, V> RemovalNotification<K, V> create(K var0, V var1, RemovalCause var2) {
      return new RemovalNotification<>((K)var0, (V)var1, var2);
   }

   private RemovalNotification(K var1, V var2, RemovalCause var3) {
      super((K)var1, (V)var2);
      this.cause = Preconditions.checkNotNull(var3);
   }

   public RemovalCause getCause() {
      return this.cause;
   }

   public boolean wasEvicted() {
      return this.cause.wasEvicted();
   }
}
