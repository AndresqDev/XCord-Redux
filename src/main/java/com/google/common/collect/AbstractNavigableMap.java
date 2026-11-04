package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class AbstractNavigableMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements NavigableMap<K, V> {
   @Override
   public abstract V get(Object var1);

   @Override
   public Entry<K, V> firstEntry() {
      return Iterators.getNext(this.entryIterator(), null);
   }

   @Override
   public Entry<K, V> lastEntry() {
      return Iterators.getNext(this.descendingEntryIterator(), null);
   }

   @Override
   public Entry<K, V> pollFirstEntry() {
      return Iterators.pollNext(this.entryIterator());
   }

   @Override
   public Entry<K, V> pollLastEntry() {
      return Iterators.pollNext(this.descendingEntryIterator());
   }

   @ParametricNullness
   @Override
   public K firstKey() {
      Entry var1 = this.firstEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (K)var1.getKey();
      }
   }

   @ParametricNullness
   @Override
   public K lastKey() {
      Entry var1 = this.lastEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (K)var1.getKey();
      }
   }

   @Override
   public Entry<K, V> lowerEntry(@ParametricNullness K var1) {
      return this.headMap((K)var1, false).lastEntry();
   }

   @Override
   public Entry<K, V> floorEntry(@ParametricNullness K var1) {
      return this.headMap((K)var1, true).lastEntry();
   }

   @Override
   public Entry<K, V> ceilingEntry(@ParametricNullness K var1) {
      return this.tailMap((K)var1, true).firstEntry();
   }

   @Override
   public Entry<K, V> higherEntry(@ParametricNullness K var1) {
      return this.tailMap((K)var1, false).firstEntry();
   }

   @Override
   public K lowerKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.lowerEntry((K)var1));
   }

   @Override
   public K floorKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.floorEntry((K)var1));
   }

   @Override
   public K ceilingKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.ceilingEntry((K)var1));
   }

   @Override
   public K higherKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.higherEntry((K)var1));
   }

   abstract Iterator<Entry<K, V>> descendingEntryIterator();

   @Override
   public SortedMap<K, V> subMap(@ParametricNullness K var1, @ParametricNullness K var2) {
      return this.subMap((K)var1, true, (K)var2, false);
   }

   @Override
   public SortedMap<K, V> headMap(@ParametricNullness K var1) {
      return this.headMap((K)var1, false);
   }

   @Override
   public SortedMap<K, V> tailMap(@ParametricNullness K var1) {
      return this.tailMap((K)var1, true);
   }

   @Override
   public NavigableSet<K> navigableKeySet() {
      return new Maps.NavigableKeySet<>(this);
   }

   @Override
   public Set<K> keySet() {
      return this.navigableKeySet();
   }

   @Override
   public NavigableSet<K> descendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   @Override
   public NavigableMap<K, V> descendingMap() {
      return new AbstractNavigableMap.DescendingMap();
   }

   private final class DescendingMap extends Maps.DescendingMap<K, V> {
      private DescendingMap() {
      }

      @Override
      NavigableMap<K, V> forward() {
         return AbstractNavigableMap.this;
      }

      @Override
      Iterator<Entry<K, V>> entryIterator() {
         return AbstractNavigableMap.this.descendingEntryIterator();
      }
   }
}
