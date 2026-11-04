package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V> {
   protected abstract SetMultimap<K, V> delegate();

   @Override
   public Set<Entry<K, V>> entries() {
      return this.delegate().entries();
   }

   @Override
   public Set<V> get(@ParametricNullness K var1) {
      return this.delegate().get((K)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public Set<V> removeAll(Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues((K)var1, var2);
   }
}
