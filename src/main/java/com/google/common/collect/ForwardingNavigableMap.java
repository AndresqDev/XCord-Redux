package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V> {
   protected ForwardingNavigableMap() {
   }

   protected abstract NavigableMap<K, V> delegate();

   @Override
   public Entry<K, V> lowerEntry(@ParametricNullness K var1) {
      return this.delegate().lowerEntry((K)var1);
   }

   protected Entry<K, V> standardLowerEntry(@ParametricNullness K var1) {
      return this.headMap((K)var1, false).lastEntry();
   }

   @Override
   public K lowerKey(@ParametricNullness K var1) {
      return this.delegate().lowerKey((K)var1);
   }

   protected K standardLowerKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.lowerEntry((K)var1));
   }

   @Override
   public Entry<K, V> floorEntry(@ParametricNullness K var1) {
      return this.delegate().floorEntry((K)var1);
   }

   protected Entry<K, V> standardFloorEntry(@ParametricNullness K var1) {
      return this.headMap((K)var1, true).lastEntry();
   }

   @Override
   public K floorKey(@ParametricNullness K var1) {
      return this.delegate().floorKey((K)var1);
   }

   protected K standardFloorKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.floorEntry((K)var1));
   }

   @Override
   public Entry<K, V> ceilingEntry(@ParametricNullness K var1) {
      return this.delegate().ceilingEntry((K)var1);
   }

   protected Entry<K, V> standardCeilingEntry(@ParametricNullness K var1) {
      return this.tailMap((K)var1, true).firstEntry();
   }

   @Override
   public K ceilingKey(@ParametricNullness K var1) {
      return this.delegate().ceilingKey((K)var1);
   }

   protected K standardCeilingKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.ceilingEntry((K)var1));
   }

   @Override
   public Entry<K, V> higherEntry(@ParametricNullness K var1) {
      return this.delegate().higherEntry((K)var1);
   }

   protected Entry<K, V> standardHigherEntry(@ParametricNullness K var1) {
      return this.tailMap((K)var1, false).firstEntry();
   }

   @Override
   public K higherKey(@ParametricNullness K var1) {
      return this.delegate().higherKey((K)var1);
   }

   protected K standardHigherKey(@ParametricNullness K var1) {
      return Maps.keyOrNull(this.higherEntry((K)var1));
   }

   @Override
   public Entry<K, V> firstEntry() {
      return this.delegate().firstEntry();
   }

   protected Entry<K, V> standardFirstEntry() {
      return Iterables.getFirst(this.entrySet(), null);
   }

   protected K standardFirstKey() {
      Entry var1 = this.firstEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (K)var1.getKey();
      }
   }

   @Override
   public Entry<K, V> lastEntry() {
      return this.delegate().lastEntry();
   }

   protected Entry<K, V> standardLastEntry() {
      return Iterables.getFirst(this.descendingMap().entrySet(), null);
   }

   protected K standardLastKey() {
      Entry var1 = this.lastEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (K)var1.getKey();
      }
   }

   @Override
   public Entry<K, V> pollFirstEntry() {
      return this.delegate().pollFirstEntry();
   }

   protected Entry<K, V> standardPollFirstEntry() {
      return Iterators.pollNext(this.entrySet().iterator());
   }

   @Override
   public Entry<K, V> pollLastEntry() {
      return this.delegate().pollLastEntry();
   }

   protected Entry<K, V> standardPollLastEntry() {
      return Iterators.pollNext(this.descendingMap().entrySet().iterator());
   }

   @Override
   public NavigableMap<K, V> descendingMap() {
      return this.delegate().descendingMap();
   }

   @Override
   public NavigableSet<K> navigableKeySet() {
      return this.delegate().navigableKeySet();
   }

   @Override
   public NavigableSet<K> descendingKeySet() {
      return this.delegate().descendingKeySet();
   }

   @Beta
   protected NavigableSet<K> standardDescendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   @Override
   protected SortedMap<K, V> standardSubMap(@ParametricNullness K var1, @ParametricNullness K var2) {
      return this.subMap((K)var1, true, (K)var2, false);
   }

   @Override
   public NavigableMap<K, V> subMap(@ParametricNullness K var1, boolean var2, @ParametricNullness K var3, boolean var4) {
      return this.delegate().subMap((K)var1, var2, (K)var3, var4);
   }

   @Override
   public NavigableMap<K, V> headMap(@ParametricNullness K var1, boolean var2) {
      return this.delegate().headMap((K)var1, var2);
   }

   @Override
   public NavigableMap<K, V> tailMap(@ParametricNullness K var1, boolean var2) {
      return this.delegate().tailMap((K)var1, var2);
   }

   protected SortedMap<K, V> standardHeadMap(@ParametricNullness K var1) {
      return this.headMap((K)var1, false);
   }

   protected SortedMap<K, V> standardTailMap(@ParametricNullness K var1) {
      return this.tailMap((K)var1, true);
   }

   @Beta
   protected class StandardDescendingMap extends Maps.DescendingMap<K, V> {
      public StandardDescendingMap() {
      }

      @Override
      NavigableMap<K, V> forward() {
         return ForwardingNavigableMap.this;
      }

      @Override
      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         this.forward().replaceAll(var1);
      }

      @Override
      protected Iterator<Entry<K, V>> entryIterator() {
         return new Iterator<Entry<K, V>>() {
            private Entry<K, V> toRemove = null;
            private Entry<K, V> nextOrNull = StandardDescendingMap.this.forward().lastEntry();

            @Override
            public boolean hasNext() {
               return this.nextOrNull != null;
            }

            public Entry<K, V> next() {
               if (this.nextOrNull == null) {
                  throw new NoSuchElementException();
               } else {
                  Entry var1;
                  try {
                     var1 = this.nextOrNull;
                  } finally {
                     this.toRemove = this.nextOrNull;
                     this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                  }

                  return var1;
               }
            }

            @Override
            public void remove() {
               if (this.toRemove == null) {
                  throw new IllegalStateException("no calls to next() since the last call to remove()");
               } else {
                  StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
                  this.toRemove = null;
               }
            }
         };
      }
   }

   @Beta
   protected class StandardNavigableKeySet extends Maps.NavigableKeySet<K, V> {
      public StandardNavigableKeySet(ForwardingNavigableMap var1) {
         super(var1);
      }
   }
}
