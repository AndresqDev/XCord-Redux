package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V> {
   FilteredKeyListMultimap(ListMultimap<K, V> var1, Predicate<? super K> var2) {
      super(var1, var2);
   }

   public ListMultimap<K, V> unfiltered() {
      return (ListMultimap<K, V>)super.unfiltered();
   }

   @Override
   public List<V> get(@ParametricNullness K var1) {
      return (List<V>)super.get((K)var1);
   }

   @Override
   public List<V> removeAll(Object var1) {
      return (List<V>)super.removeAll(var1);
   }

   @Override
   public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return (List<V>)super.replaceValues((K)var1, var2);
   }
}
