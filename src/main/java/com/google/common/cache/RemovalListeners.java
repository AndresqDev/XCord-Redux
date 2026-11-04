package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class RemovalListeners {
   private RemovalListeners() {
   }

   public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> var0, final Executor var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new RemovalListener<K, V>() {
         @Override
         public void onRemoval(final RemovalNotification<K, V> var1x) {
            var1.execute(new Runnable() {
               @Override
               public void run() {
                  var0.onRemoval(var1x);
               }
            });
         }
      };
   }
}
