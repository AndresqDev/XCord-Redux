package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V> implements LoadingCache<K, V> {
   protected AbstractLoadingCache() {
   }

   @Override
   public V getUnchecked(K var1) {
      try {
         return this.get((K)var1);
      } catch (ExecutionException var3) {
         throw new UncheckedExecutionException(var3.getCause());
      }
   }

   @Override
   public ImmutableMap<K, V> getAll(Iterable<? extends K> var1) throws ExecutionException {
      LinkedHashMap var2 = Maps.newLinkedHashMap();

      for (Object var4 : var1) {
         if (!var2.containsKey(var4)) {
            var2.put(var4, this.get((K)var4));
         }
      }

      return ImmutableMap.copyOf(var2);
   }

   @Override
   public final V apply(K var1) {
      return this.getUnchecked((K)var1);
   }

   @Override
   public void refresh(K var1) {
      throw new UnsupportedOperationException();
   }
}
