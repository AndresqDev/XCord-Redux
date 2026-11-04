package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class TreeRangeMap<K extends Comparable, V> implements RangeMap<K, V> {
   private final NavigableMap<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> entriesByLowerBound = Maps.newTreeMap();
   private static final RangeMap<Comparable<?>, Object> EMPTY_SUB_RANGE_MAP = new RangeMap<Comparable<?>, Object>() {
      @Override
      public Object get(Comparable<?> var1) {
         return null;
      }

      @Override
      public Entry<Range<Comparable<?>>, Object> getEntry(Comparable<?> var1) {
         return null;
      }

      @Override
      public Range<Comparable<?>> span() {
         throw new NoSuchElementException();
      }

      @Override
      public void put(Range<Comparable<?>> var1, Object var2) {
         Preconditions.checkNotNull(var1);
         String var3 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(46 + String.valueOf(var3).length()).append("Cannot insert range ").append(var3).append(" into an empty subRangeMap").toString()
         );
      }

      @Override
      public void putCoalescing(Range<Comparable<?>> var1, Object var2) {
         Preconditions.checkNotNull(var1);
         String var3 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(46 + String.valueOf(var3).length()).append("Cannot insert range ").append(var3).append(" into an empty subRangeMap").toString()
         );
      }

      @Override
      public void putAll(RangeMap<Comparable<?>, Object> var1) {
         if (!var1.asMapOfRanges().isEmpty()) {
            throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
         }
      }

      @Override
      public void clear() {
      }

      @Override
      public void remove(Range<Comparable<?>> var1) {
         Preconditions.checkNotNull(var1);
      }

      @Override
      public void merge(Range<Comparable<?>> var1, Object var2, BiFunction<? super Object, ? super Object, ? extends Object> var3) {
         Preconditions.checkNotNull(var1);
         String var4 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(45 + String.valueOf(var4).length()).append("Cannot merge range ").append(var4).append(" into an empty subRangeMap").toString()
         );
      }

      @Override
      public Map<Range<Comparable<?>>, Object> asMapOfRanges() {
         return Collections.emptyMap();
      }

      @Override
      public Map<Range<Comparable<?>>, Object> asDescendingMapOfRanges() {
         return Collections.emptyMap();
      }

      @Override
      public RangeMap<Comparable<?>, Object> subRangeMap(Range<Comparable<?>> var1) {
         Preconditions.checkNotNull(var1);
         return this;
      }
   };

   public static <K extends Comparable, V> TreeRangeMap<K, V> create() {
      return new TreeRangeMap<>();
   }

   private TreeRangeMap() {
   }

   @Override
   public V get(K var1) {
      Entry var2 = this.getEntry((K)var1);
      return (V)(var2 == null ? null : var2.getValue());
   }

   @Override
   public Entry<Range<K>, V> getEntry(K var1) {
      Entry var2 = this.entriesByLowerBound.floorEntry(Cut.belowValue((K)var1));
      return var2 != null && ((TreeRangeMap.RangeMapEntry)var2.getValue()).contains(var1) ? (Entry)var2.getValue() : null;
   }

   @Override
   public void put(Range<K> var1, V var2) {
      if (!var1.isEmpty()) {
         Preconditions.checkNotNull(var2);
         this.remove(var1);
         this.entriesByLowerBound.put(var1.lowerBound, new TreeRangeMap.RangeMapEntry<>(var1, (V)var2));
      }
   }

   @Override
   public void putCoalescing(Range<K> var1, V var2) {
      if (this.entriesByLowerBound.isEmpty()) {
         this.put(var1, (V)var2);
      } else {
         Range var3 = this.coalescedRange(var1, Preconditions.checkNotNull((V)var2));
         this.put(var3, (V)var2);
      }
   }

   private Range<K> coalescedRange(Range<K> var1, V var2) {
      Entry var4 = this.entriesByLowerBound.lowerEntry(var1.lowerBound);
      Range var3 = coalesce(var1, var2, var4);
      Entry var5 = this.entriesByLowerBound.floorEntry(var1.upperBound);
      return coalesce(var3, var2, var5);
   }

   private static <K extends Comparable, V> Range<K> coalesce(Range<K> var0, V var1, Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> var2) {
      return var2 != null
            && ((TreeRangeMap.RangeMapEntry)var2.getValue()).getKey().isConnected(var0)
            && ((TreeRangeMap.RangeMapEntry)var2.getValue()).getValue().equals(var1)
         ? var0.span(((TreeRangeMap.RangeMapEntry)var2.getValue()).getKey())
         : var0;
   }

   @Override
   public void putAll(RangeMap<K, V> var1) {
      for (Entry var3 : var1.asMapOfRanges().entrySet()) {
         this.put((Range<K>)var3.getKey(), (V)var3.getValue());
      }
   }

   @Override
   public void clear() {
      this.entriesByLowerBound.clear();
   }

   @Override
   public Range<K> span() {
      Entry var1 = this.entriesByLowerBound.firstEntry();
      Entry var2 = this.entriesByLowerBound.lastEntry();
      if (var1 != null && var2 != null) {
         return Range.create(
            ((TreeRangeMap.RangeMapEntry)var1.getValue()).getKey().lowerBound, ((TreeRangeMap.RangeMapEntry)var2.getValue()).getKey().upperBound
         );
      } else {
         throw new NoSuchElementException();
      }
   }

   private void putRangeMapEntry(Cut<K> var1, Cut<K> var2, V var3) {
      this.entriesByLowerBound.put(var1, new TreeRangeMap.RangeMapEntry<>(var1, var2, (V)var3));
   }

   @Override
   public void remove(Range<K> var1) {
      if (!var1.isEmpty()) {
         Entry var2 = this.entriesByLowerBound.lowerEntry(var1.lowerBound);
         if (var2 != null) {
            TreeRangeMap.RangeMapEntry var3 = (TreeRangeMap.RangeMapEntry)var2.getValue();
            if (var3.getUpperBound().compareTo(var1.lowerBound) > 0) {
               if (var3.getUpperBound().compareTo(var1.upperBound) > 0) {
                  this.putRangeMapEntry(var1.upperBound, var3.getUpperBound(), (V)((TreeRangeMap.RangeMapEntry)var2.getValue()).getValue());
               }

               this.putRangeMapEntry(var3.getLowerBound(), var1.lowerBound, (V)((TreeRangeMap.RangeMapEntry)var2.getValue()).getValue());
            }
         }

         Entry var5 = this.entriesByLowerBound.lowerEntry(var1.upperBound);
         if (var5 != null) {
            TreeRangeMap.RangeMapEntry var4 = (TreeRangeMap.RangeMapEntry)var5.getValue();
            if (var4.getUpperBound().compareTo(var1.upperBound) > 0) {
               this.putRangeMapEntry(var1.upperBound, var4.getUpperBound(), (V)((TreeRangeMap.RangeMapEntry)var5.getValue()).getValue());
            }
         }

         this.entriesByLowerBound.subMap(var1.lowerBound, var1.upperBound).clear();
      }
   }

   private void split(Cut<K> var1) {
      Entry var2 = this.entriesByLowerBound.lowerEntry(var1);
      if (var2 != null) {
         TreeRangeMap.RangeMapEntry var3 = (TreeRangeMap.RangeMapEntry)var2.getValue();
         if (var3.getUpperBound().compareTo(var1) > 0) {
            this.putRangeMapEntry(var3.getLowerBound(), var1, (V)var3.getValue());
            this.putRangeMapEntry(var1, var3.getUpperBound(), (V)var3.getValue());
         }
      }
   }

   @Override
   public void merge(Range<K> var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var3);
      if (!var1.isEmpty()) {
         this.split(var1.lowerBound);
         this.split(var1.upperBound);
         Set var4 = this.entriesByLowerBound.subMap(var1.lowerBound, var1.upperBound).entrySet();
         ImmutableMap.Builder var5 = ImmutableMap.builder();
         if (var2 != null) {
            Iterator var6 = var4.iterator();
            Cut var7 = var1.lowerBound;

            while (var6.hasNext()) {
               TreeRangeMap.RangeMapEntry var8 = (TreeRangeMap.RangeMapEntry)((Entry)var6.next()).getValue();
               Cut var9 = var8.getLowerBound();
               if (!var7.equals(var9)) {
                  var5.put(var7, new TreeRangeMap.RangeMapEntry<>(var7, var9, var2));
               }

               var7 = var8.getUpperBound();
            }

            if (!var7.equals(var1.upperBound)) {
               var5.put(var7, new TreeRangeMap.RangeMapEntry<>(var7, var1.upperBound, var2));
            }
         }

         Iterator var10 = var4.iterator();

         while (var10.hasNext()) {
            Entry var11 = (Entry)var10.next();
            Object var12 = var3.apply(((TreeRangeMap.RangeMapEntry)var11.getValue()).getValue(), var2);
            if (var12 == null) {
               var10.remove();
            } else {
               var11.setValue(
                  new TreeRangeMap.RangeMapEntry<>(
                     ((TreeRangeMap.RangeMapEntry)var11.getValue()).getLowerBound(), ((TreeRangeMap.RangeMapEntry)var11.getValue()).getUpperBound(), var12
                  )
               );
            }
         }

         this.entriesByLowerBound.putAll(var5.build());
      }
   }

   @Override
   public Map<Range<K>, V> asMapOfRanges() {
      return new TreeRangeMap.AsMapOfRanges(this.entriesByLowerBound.values());
   }

   @Override
   public Map<Range<K>, V> asDescendingMapOfRanges() {
      return new TreeRangeMap.AsMapOfRanges(this.entriesByLowerBound.descendingMap().values());
   }

   @Override
   public RangeMap<K, V> subRangeMap(Range<K> var1) {
      return (RangeMap<K, V>)(var1.equals(Range.all()) ? this : new TreeRangeMap.SubRangeMap(var1));
   }

   private RangeMap<K, V> emptySubRangeMap() {
      return (RangeMap<K, V>)EMPTY_SUB_RANGE_MAP;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof RangeMap) {
         RangeMap var2 = (RangeMap)var1;
         return this.asMapOfRanges().equals(var2.asMapOfRanges());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.asMapOfRanges().hashCode();
   }

   @Override
   public String toString() {
      return this.entriesByLowerBound.values().toString();
   }

   private final class AsMapOfRanges extends Maps.IteratorBasedAbstractMap<Range<K>, V> {
      final Iterable<Entry<Range<K>, V>> entryIterable;

      AsMapOfRanges(Iterable<TreeRangeMap.RangeMapEntry<K, V>> var2) {
         this.entryIterable = var2;
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.get(var1) != null;
      }

      @Override
      public V get(Object var1) {
         if (var1 instanceof Range) {
            Range var2 = (Range)var1;
            TreeRangeMap.RangeMapEntry var3 = TreeRangeMap.this.entriesByLowerBound.get(var2.lowerBound);
            if (var3 != null && var3.getKey().equals(var2)) {
               return (V)var3.getValue();
            }
         }

         return null;
      }

      @Override
      public int size() {
         return TreeRangeMap.this.entriesByLowerBound.size();
      }

      @Override
      Iterator<Entry<Range<K>, V>> entryIterator() {
         return this.entryIterable.iterator();
      }
   }

   private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V> {
      private final Range<K> range;
      private final V value;

      RangeMapEntry(Cut<K> var1, Cut<K> var2, V var3) {
         this(Range.create(var1, var2), (V)var3);
      }

      RangeMapEntry(Range<K> var1, V var2) {
         this.range = var1;
         this.value = (V)var2;
      }

      public Range<K> getKey() {
         return this.range;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      public boolean contains(K var1) {
         return this.range.contains((K)var1);
      }

      Cut<K> getLowerBound() {
         return this.range.lowerBound;
      }

      Cut<K> getUpperBound() {
         return this.range.upperBound;
      }
   }

   private class SubRangeMap implements RangeMap<K, V> {
      private final Range<K> subRange;

      SubRangeMap(Range<K> var2) {
         this.subRange = var2;
      }

      @Override
      public V get(K var1) {
         return this.subRange.contains((K)var1) ? TreeRangeMap.this.get((K)var1) : null;
      }

      @Override
      public Entry<Range<K>, V> getEntry(K var1) {
         if (this.subRange.contains((K)var1)) {
            Entry var2 = TreeRangeMap.this.getEntry((K)var1);
            if (var2 != null) {
               return Maps.immutableEntry(((Range)var2.getKey()).intersection(this.subRange), (V)var2.getValue());
            }
         }

         return null;
      }

      @Override
      public Range<K> span() {
         Entry var2 = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
         Cut var1;
         if (var2 != null && ((TreeRangeMap.RangeMapEntry)var2.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0) {
            var1 = this.subRange.lowerBound;
         } else {
            var1 = TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
            if (var1 == null || var1.compareTo(this.subRange.upperBound) >= 0) {
               throw new NoSuchElementException();
            }
         }

         Entry var4 = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
         if (var4 == null) {
            throw new NoSuchElementException();
         } else {
            Cut var3;
            if (((TreeRangeMap.RangeMapEntry)var4.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
               var3 = this.subRange.upperBound;
            } else {
               var3 = ((TreeRangeMap.RangeMapEntry)var4.getValue()).getUpperBound();
            }

            return Range.create(var1, var3);
         }
      }

      @Override
      public void put(Range<K> var1, V var2) {
         Preconditions.checkArgument(this.subRange.encloses(var1), "Cannot put range %s into a subRangeMap(%s)", var1, this.subRange);
         TreeRangeMap.this.put(var1, (V)var2);
      }

      @Override
      public void putCoalescing(Range<K> var1, V var2) {
         if (!TreeRangeMap.this.entriesByLowerBound.isEmpty() && this.subRange.encloses(var1)) {
            Range var3 = TreeRangeMap.this.coalescedRange(var1, Preconditions.checkNotNull((V)var2));
            this.put(var3.intersection(this.subRange), (V)var2);
         } else {
            this.put(var1, (V)var2);
         }
      }

      @Override
      public void putAll(RangeMap<K, V> var1) {
         if (!var1.asMapOfRanges().isEmpty()) {
            Range var2 = var1.span();
            Preconditions.checkArgument(this.subRange.encloses(var2), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", var2, this.subRange);
            TreeRangeMap.this.putAll(var1);
         }
      }

      @Override
      public void clear() {
         TreeRangeMap.this.remove(this.subRange);
      }

      @Override
      public void remove(Range<K> var1) {
         if (var1.isConnected(this.subRange)) {
            TreeRangeMap.this.remove(var1.intersection(this.subRange));
         }
      }

      @Override
      public void merge(Range<K> var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         Preconditions.checkArgument(this.subRange.encloses(var1), "Cannot merge range %s into a subRangeMap(%s)", var1, this.subRange);
         TreeRangeMap.this.merge(var1, (V)var2, var3);
      }

      @Override
      public RangeMap<K, V> subRangeMap(Range<K> var1) {
         return !var1.isConnected(this.subRange) ? TreeRangeMap.this.emptySubRangeMap() : TreeRangeMap.this.subRangeMap(var1.intersection(this.subRange));
      }

      @Override
      public Map<Range<K>, V> asMapOfRanges() {
         return new TreeRangeMap.SubRangeMap.SubRangeMapAsMap();
      }

      @Override
      public Map<Range<K>, V> asDescendingMapOfRanges() {
         return new TreeRangeMap<K, V>.SubRangeMap.SubRangeMapAsMap() {
            @Override
            Iterator<Entry<Range<K>, V>> entryIterator() {
               if (SubRangeMap.this.subRange.isEmpty()) {
                  return Iterators.emptyIterator();
               } else {
                  final Iterator var1 = TreeRangeMap.this.entriesByLowerBound
                     .headMap(SubRangeMap.this.subRange.upperBound, false)
                     .descendingMap()
                     .values()
                     .iterator();
                  return new AbstractIterator<Entry<Range<K>, V>>() {
                     protected Entry<Range<K>, V> computeNext() {
                        if (var1.hasNext()) {
                           TreeRangeMap.RangeMapEntry var1x = (TreeRangeMap.RangeMapEntry)var1.next();
                           return var1x.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) <= 0
                              ? this.endOfData()
                              : Maps.immutableEntry(var1x.getKey().intersection(SubRangeMap.this.subRange), (V)var1x.getValue());
                        } else {
                           return this.endOfData();
                        }
                     }
                  };
               }
            }
         };
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof RangeMap) {
            RangeMap var2 = (RangeMap)var1;
            return this.asMapOfRanges().equals(var2.asMapOfRanges());
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.asMapOfRanges().hashCode();
      }

      @Override
      public String toString() {
         return this.asMapOfRanges().toString();
      }

      class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {
         @Override
         public boolean containsKey(Object var1) {
            return this.get(var1) != null;
         }

         @Override
         public V get(Object var1) {
            try {
               if (var1 instanceof Range) {
                  Range var2 = (Range)var1;
                  if (!SubRangeMap.this.subRange.encloses(var2) || var2.isEmpty()) {
                     return null;
                  }

                  TreeRangeMap.RangeMapEntry var3 = null;
                  if (var2.lowerBound.compareTo(SubRangeMap.this.subRange.lowerBound) == 0) {
                     Entry var4 = TreeRangeMap.this.entriesByLowerBound.floorEntry(var2.lowerBound);
                     if (var4 != null) {
                        var3 = (TreeRangeMap.RangeMapEntry)var4.getValue();
                     }
                  } else {
                     var3 = TreeRangeMap.this.entriesByLowerBound.get(var2.lowerBound);
                  }

                  if (var3 != null
                     && var3.getKey().isConnected(SubRangeMap.this.subRange)
                     && var3.getKey().intersection(SubRangeMap.this.subRange).equals(var2)) {
                     return (V)var3.getValue();
                  }
               }

               return null;
            } catch (ClassCastException var5) {
               return null;
            }
         }

         @Override
         public V remove(Object var1) {
            Object var2 = this.get(var1);
            if (var2 != null) {
               Range var3 = Objects.requireNonNull((Range)var1);
               TreeRangeMap.this.remove(var3);
               return (V)var2;
            } else {
               return null;
            }
         }

         @Override
         public void clear() {
            SubRangeMap.this.clear();
         }

         private boolean removeEntryIf(Predicate<? super Entry<Range<K>, V>> var1) {
            ArrayList var2 = Lists.newArrayList();

            for (Entry var4 : this.entrySet()) {
               if (var1.apply(var4)) {
                  var2.add((Range)var4.getKey());
               }
            }

            for (Range var6 : var2) {
               TreeRangeMap.this.remove(var6);
            }

            return !var2.isEmpty();
         }

         @Override
         public Set<Range<K>> keySet() {
            return new Maps.KeySet<Range<K>, V>(this) {
               @Override
               public boolean remove(Object var1) {
                  return SubRangeMapAsMap.this.remove(var1) != null;
               }

               @Override
               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(var1)), Maps.keyFunction()));
               }
            };
         }

         @Override
         public Set<Entry<Range<K>, V>> entrySet() {
            return new Maps.EntrySet<Range<K>, V>() {
               @Override
               Map<Range<K>, V> map() {
                  return SubRangeMapAsMap.this;
               }

               @Override
               public Iterator<Entry<Range<K>, V>> iterator() {
                  return SubRangeMapAsMap.this.entryIterator();
               }

               @Override
               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(var1)));
               }

               @Override
               public int size() {
                  return Iterators.size(this.iterator());
               }

               @Override
               public boolean isEmpty() {
                  return !this.iterator().hasNext();
               }
            };
         }

         Iterator<Entry<Range<K>, V>> entryIterator() {
            if (SubRangeMap.this.subRange.isEmpty()) {
               return Iterators.emptyIterator();
            } else {
               Cut var1 = MoreObjects.firstNonNull(
                  TreeRangeMap.this.entriesByLowerBound.floorKey(SubRangeMap.this.subRange.lowerBound), SubRangeMap.this.subRange.lowerBound
               );
               final Iterator var2 = TreeRangeMap.this.entriesByLowerBound.tailMap(var1, true).values().iterator();
               return new AbstractIterator<Entry<Range<K>, V>>() {
                  protected Entry<Range<K>, V> computeNext() {
                     while (var2.hasNext()) {
                        TreeRangeMap.RangeMapEntry var1 = (TreeRangeMap.RangeMapEntry)var2.next();
                        if (var1.getLowerBound().compareTo(SubRangeMap.this.subRange.upperBound) >= 0) {
                           return this.endOfData();
                        }

                        if (var1.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) > 0) {
                           return Maps.immutableEntry(var1.getKey().intersection(SubRangeMap.this.subRange), (V)var1.getValue());
                        }
                     }

                     return this.endOfData();
                  }
               };
            }
         }

         @Override
         public Collection<V> values() {
            return new Maps.Values<Range<K>, V>(this) {
               @Override
               public boolean removeAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(var1), Maps.valueFunction()));
               }

               @Override
               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(var1)), Maps.valueFunction()));
               }
            };
         }
      }
   }
}
