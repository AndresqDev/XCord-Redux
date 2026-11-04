package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

@DoNotMock("Use ImmutableMultimap, HashMultimap, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multimap<K, V> {
   int size();

   boolean isEmpty();

   boolean containsKey(@CompatibleWith("K") Object var1);

   boolean containsValue(@CompatibleWith("V") Object var1);

   boolean containsEntry(@CompatibleWith("K") Object var1, @CompatibleWith("V") Object var2);

   @CanIgnoreReturnValue
   boolean put(@ParametricNullness K var1, @ParametricNullness V var2);

   @CanIgnoreReturnValue
   boolean remove(@CompatibleWith("K") Object var1, @CompatibleWith("V") Object var2);

   @CanIgnoreReturnValue
   boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2);

   @CanIgnoreReturnValue
   boolean putAll(Multimap<? extends K, ? extends V> var1);

   @CanIgnoreReturnValue
   Collection<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

   @CanIgnoreReturnValue
   Collection<V> removeAll(@CompatibleWith("K") Object var1);

   void clear();

   Collection<V> get(@ParametricNullness K var1);

   Set<K> keySet();

   Multiset<K> keys();

   Collection<V> values();

   Collection<Entry<K, V>> entries();

   default void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.entries().forEach(var1x -> var1.accept(var1x.getKey(), var1x.getValue()));
   }

   Map<K, Collection<V>> asMap();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();
}
