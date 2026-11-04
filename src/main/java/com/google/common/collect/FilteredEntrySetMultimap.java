package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredEntrySetMultimap<K, V> extends FilteredEntryMultimap<K, V> implements FilteredSetMultimap<K, V> {
   FilteredEntrySetMultimap(SetMultimap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
      super(var1, var2);
   }

   @Override
   public SetMultimap<K, V> unfiltered() {
      return (SetMultimap<K, V>)this.unfiltered;
   }

   @Override
   public Set<V> get(@ParametricNullness K var1) {
      return (Set<V>)super.get((K)var1);
   }

   @Override
   public Set<V> removeAll(Object var1) {
      return (Set<V>)super.removeAll(var1);
   }

   @Override
   public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return (Set<V>)super.replaceValues((K)var1, var2);
   }

   Set<Entry<K, V>> createEntries() {
      return Sets.filter(this.unfiltered().entries(), this.entryPredicate());
   }

   @Override
   public Set<Entry<K, V>> entries() {
      return (Set<Entry<K, V>>)super.entries();
   }
}
