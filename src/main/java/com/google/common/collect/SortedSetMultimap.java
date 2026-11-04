package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V> {
   SortedSet<V> get(@ParametricNullness K var1);

   @CanIgnoreReturnValue
   SortedSet<V> removeAll(Object var1);

   @CanIgnoreReturnValue
   SortedSet<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

   @Override
   Map<K, Collection<V>> asMap();

   Comparator<? super V> valueComparator();
}
