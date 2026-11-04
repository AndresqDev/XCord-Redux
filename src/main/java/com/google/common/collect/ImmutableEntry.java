package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable {
   @ParametricNullness
   final K key;
   @ParametricNullness
   final V value;
   private static final long serialVersionUID = 0L;

   ImmutableEntry(@ParametricNullness K var1, @ParametricNullness V var2) {
      this.key = (K)var1;
      this.value = (V)var2;
   }

   @ParametricNullness
   @Override
   public final K getKey() {
      return this.key;
   }

   @ParametricNullness
   @Override
   public final V getValue() {
      return this.value;
   }

   @ParametricNullness
   @Override
   public final V setValue(@ParametricNullness V var1) {
      throw new UnsupportedOperationException();
   }
}
