package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

@DoNotMock("Use ImmutableRangeMap or TreeRangeMap")
@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public interface RangeMap<K extends Comparable, V> {
   V get(K var1);

   Entry<Range<K>, V> getEntry(K var1);

   Range<K> span();

   void put(Range<K> var1, V var2);

   void putCoalescing(Range<K> var1, V var2);

   void putAll(RangeMap<K, V> var1);

   void clear();

   void remove(Range<K> var1);

   void merge(Range<K> var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3);

   Map<Range<K>, V> asMapOfRanges();

   Map<Range<K>, V> asDescendingMapOfRanges();

   RangeMap<K, V> subRangeMap(Range<K> var1);

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   @Override
   String toString();
}
