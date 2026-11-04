package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V> {
   Set<V> get(@ParametricNullness K var1);

   @CanIgnoreReturnValue
   Set<V> removeAll(Object var1);

   @CanIgnoreReturnValue
   Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

   Set<Entry<K, V>> entries();

   @Override
   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);
}
