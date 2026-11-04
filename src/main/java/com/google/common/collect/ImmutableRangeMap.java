package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.DoNotMock;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V>, Serializable {
   private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY = new ImmutableRangeMap<>(ImmutableList.of(), ImmutableList.of());
   private final transient ImmutableList<Range<K>> ranges;
   private final transient ImmutableList<V> values;
   private static final long serialVersionUID = 0L;

   public static <T, K extends Comparable<? super K>, V> Collector<T, ?, ImmutableRangeMap<K, V>> toImmutableRangeMap(
      Function<? super T, Range<K>> var0, Function<? super T, ? extends V> var1
   ) {
      return CollectCollectors.toImmutableRangeMap(var0, var1);
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
      return (ImmutableRangeMap<K, V>)EMPTY;
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> var0, V var1) {
      return new ImmutableRangeMap<>(ImmutableList.of(var0), ImmutableList.of((V)var1));
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(RangeMap<K, ? extends V> var0) {
      if (var0 instanceof ImmutableRangeMap) {
         return (ImmutableRangeMap<K, V>)var0;
      } else {
         Map var1 = var0.asMapOfRanges();
         ImmutableList.Builder var2 = new ImmutableList.Builder(var1.size());
         ImmutableList.Builder var3 = new ImmutableList.Builder(var1.size());

         for (Entry var5 : var1.entrySet()) {
            var2.add((Range)var5.getKey());
            var3.add(var5.getValue());
         }

         return new ImmutableRangeMap<>(var2.build(), var3.build());
      }
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap.Builder<K, V> builder() {
      return new ImmutableRangeMap.Builder<>();
   }

   ImmutableRangeMap(ImmutableList<Range<K>> var1, ImmutableList<V> var2) {
      this.ranges = var1;
      this.values = var2;
   }

   @Override
   public V get(K var1) {
      int var2 = SortedLists.binarySearch(
         this.ranges, Range.lowerBoundFn(), Cut.belowValue((K)var1), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER
      );
      if (var2 == -1) {
         return null;
      } else {
         Range var3 = this.ranges.get(var2);
         return var3.contains(var1) ? this.values.get(var2) : null;
      }
   }

   @Override
   public Entry<Range<K>, V> getEntry(K var1) {
      int var2 = SortedLists.binarySearch(
         this.ranges, Range.lowerBoundFn(), Cut.belowValue((K)var1), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER
      );
      if (var2 == -1) {
         return null;
      } else {
         Range var3 = this.ranges.get(var2);
         return var3.contains(var1) ? Maps.immutableEntry(var3, this.values.get(var2)) : null;
      }
   }

   @Override
   public Range<K> span() {
      if (this.ranges.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         Range var1 = this.ranges.get(0);
         Range var2 = this.ranges.get(this.ranges.size() - 1);
         return Range.create(var1.lowerBound, var2.upperBound);
      }
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void put(Range<K> var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void putCoalescing(Range<K> var1, V var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void putAll(RangeMap<K, V> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void remove(Range<K> var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final void merge(Range<K> var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      throw new UnsupportedOperationException();
   }

   public ImmutableMap<Range<K>, V> asMapOfRanges() {
      if (this.ranges.isEmpty()) {
         return ImmutableMap.of();
      } else {
         RegularImmutableSortedSet var1 = new RegularImmutableSortedSet<>(this.ranges, Range.rangeLexOrdering());
         return new ImmutableSortedMap<>(var1, this.values);
      }
   }

   public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
      if (this.ranges.isEmpty()) {
         return ImmutableMap.of();
      } else {
         RegularImmutableSortedSet var1 = new RegularImmutableSortedSet<>(this.ranges.reverse(), Range.rangeLexOrdering().reverse());
         return new ImmutableSortedMap<>(var1, this.values.reverse());
      }
   }

   public ImmutableRangeMap<K, V> subRangeMap(final Range<K> var1) {
      if (Preconditions.checkNotNull(var1).isEmpty()) {
         return of();
      } else if (!this.ranges.isEmpty() && !var1.encloses(this.span())) {
         final int var2 = SortedLists.binarySearch(
            this.ranges, Range.upperBoundFn(), var1.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER
         );
         int var3 = SortedLists.binarySearch(
            this.ranges, Range.lowerBoundFn(), var1.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER
         );
         if (var2 >= var3) {
            return of();
         } else {
            final int var5 = var3 - var2;
            ImmutableList var6 = new ImmutableList<Range<K>>() {
               @Override
               public int size() {
                  return var5;
               }

               public Range<K> get(int var1x) {
                  Preconditions.checkElementIndex(var1x, var5);
                  return var1x != 0 && var1x != var5 - 1
                     ? ImmutableRangeMap.this.ranges.get(var1x + var2)
                     : ImmutableRangeMap.this.ranges.get(var1x + var2).intersection(var1);
               }

               @Override
               boolean isPartialView() {
                  return true;
               }
            };
            return new ImmutableRangeMap<K, V>(this, var6, this.values.subList(var2, var3)) {
               @Override
               public ImmutableRangeMap<K, V> subRangeMap(Range<K> var1x) {
                  return var1.isConnected(var1x) ? ImmutableRangeMap.this.subRangeMap(var1x.intersection(var1)) : ImmutableRangeMap.of();
               }
            };
         }
      } else {
         return this;
      }
   }

   @Override
   public int hashCode() {
      return this.asMapOfRanges().hashCode();
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
   public String toString() {
      return this.asMapOfRanges().toString();
   }

   Object writeReplace() {
      return new ImmutableRangeMap.SerializedForm<>(this.asMapOfRanges());
   }

   @DoNotMock
   public static final class Builder<K extends Comparable<?>, V> {
      private final List<Entry<Range<K>, V>> entries = Lists.newArrayList();

      @CanIgnoreReturnValue
      public ImmutableRangeMap.Builder<K, V> put(Range<K> var1, V var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         Preconditions.checkArgument(!var1.isEmpty(), "Range must not be empty, but was %s", var1);
         this.entries.add(Maps.immutableEntry(var1, (V)var2));
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableRangeMap.Builder<K, V> putAll(RangeMap<K, ? extends V> var1) {
         for (Entry var3 : var1.asMapOfRanges().entrySet()) {
            this.put((Range<K>)var3.getKey(), (V)var3.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      ImmutableRangeMap.Builder<K, V> combine(ImmutableRangeMap.Builder<K, V> var1) {
         this.entries.addAll(var1.entries);
         return this;
      }

      public ImmutableRangeMap<K, V> build() {
         Collections.sort(this.entries, Range.rangeLexOrdering().onKeys());
         ImmutableList.Builder var1 = new ImmutableList.Builder(this.entries.size());
         ImmutableList.Builder var2 = new ImmutableList.Builder(this.entries.size());

         for (int var3 = 0; var3 < this.entries.size(); var3++) {
            Range var4 = this.entries.get(var3).getKey();
            if (var3 > 0) {
               Range var5 = this.entries.get(var3 - 1).getKey();
               if (var4.isConnected(var5) && !var4.intersection(var5).isEmpty()) {
                  String var6 = String.valueOf(var5);
                  String var7 = String.valueOf(var4);
                  throw new IllegalArgumentException(
                     new StringBuilder(47 + String.valueOf(var6).length() + String.valueOf(var7).length())
                        .append("Overlapping ranges: range ")
                        .append(var6)
                        .append(" overlaps with entry ")
                        .append(var7)
                        .toString()
                  );
               }
            }

            var1.add(var4);
            var2.add(this.entries.get(var3).getValue());
         }

         return new ImmutableRangeMap<>(var1.build(), var2.build());
      }
   }

   private static class SerializedForm<K extends Comparable<?>, V> implements Serializable {
      private final ImmutableMap<Range<K>, V> mapOfRanges;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap<Range<K>, V> var1) {
         this.mapOfRanges = var1;
      }

      Object readResolve() {
         return this.mapOfRanges.isEmpty() ? ImmutableRangeMap.of() : this.createRangeMap();
      }

      Object createRangeMap() {
         ImmutableRangeMap.Builder var1 = new ImmutableRangeMap.Builder();

         for (Entry var3 : this.mapOfRanges.entrySet()) {
            var1.put((Range<K>)var3.getKey(), var3.getValue());
         }

         return var1.build();
      }
   }
}
