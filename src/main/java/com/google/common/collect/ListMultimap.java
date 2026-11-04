package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V> {
   List<V> get(@ParametricNullness K var1);

   @CanIgnoreReturnValue
   List<V> removeAll(Object var1);

   @CanIgnoreReturnValue
   List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

   @Override
   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);
}
