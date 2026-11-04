package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Entry<K, V> {
   @ParametricNullness
   @Override
   public abstract K getKey();

   @ParametricNullness
   @Override
   public abstract V getValue();

   @ParametricNullness
   @Override
   public V setValue(@ParametricNullness V var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         return Objects.equal(this.getKey(), var2.getKey()) && Objects.equal(this.getValue(), var2.getValue());
      }
   }

   @Override
   public int hashCode() {
      Object var1 = this.getKey();
      Object var2 = this.getValue();
      return (var1 == null ? 0 : var1.hashCode()) ^ (var2 == null ? 0 : var2.hashCode());
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.getKey());
      String var2 = String.valueOf(this.getValue());
      return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append("=").append(var2).toString();
   }
}
