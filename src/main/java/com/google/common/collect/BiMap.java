package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface BiMap<K, V> extends Map<K, V> {
   @CanIgnoreReturnValue
   @Override
   V put(@ParametricNullness K var1, @ParametricNullness V var2);

   @CanIgnoreReturnValue
   V forcePut(@ParametricNullness K var1, @ParametricNullness V var2);

   @Override
   void putAll(Map<? extends K, ? extends V> var1);

   Set<V> values();

   BiMap<V, K> inverse();
}
