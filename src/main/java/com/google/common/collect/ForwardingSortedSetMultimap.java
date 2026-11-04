package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V> {
   protected ForwardingSortedSetMultimap() {
   }

   protected abstract SortedSetMultimap<K, V> delegate();

   @Override
   public SortedSet<V> get(@ParametricNullness K var1) {
      return this.delegate().get((K)var1);
   }

   @Override
   public SortedSet<V> removeAll(Object var1) {
      return this.delegate().removeAll(var1);
   }

   @Override
   public SortedSet<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues((K)var1, var2);
   }

   @Override
   public Comparator<? super V> valueComparator() {
      return this.delegate().valueComparator();
   }
}
