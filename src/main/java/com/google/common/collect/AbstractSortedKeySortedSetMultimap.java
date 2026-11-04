package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V> {
   AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> var1) {
      super(var1);
   }

   public SortedMap<K, Collection<V>> asMap() {
      return (SortedMap<K, Collection<V>>)super.asMap();
   }

   SortedMap<K, Collection<V>> backingMap() {
      return (SortedMap<K, Collection<V>>)super.backingMap();
   }

   public SortedSet<K> keySet() {
      return (SortedSet<K>)super.keySet();
   }

   @Override
   Set<K> createKeySet() {
      return this.createMaybeNavigableKeySet();
   }
}
