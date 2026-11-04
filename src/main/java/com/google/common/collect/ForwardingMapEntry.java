package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Entry<K, V> {
   protected ForwardingMapEntry() {
   }

   protected abstract Entry<K, V> delegate();

   @ParametricNullness
   @Override
   public K getKey() {
      return this.delegate().getKey();
   }

   @ParametricNullness
   @Override
   public V getValue() {
      return this.delegate().getValue();
   }

   @ParametricNullness
   @Override
   public V setValue(@ParametricNullness V var1) {
      return this.delegate().setValue((V)var1);
   }

   @Override
   public boolean equals(Object var1) {
      return this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected boolean standardEquals(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         return Objects.equal(this.getKey(), var2.getKey()) && Objects.equal(this.getValue(), var2.getValue());
      }
   }

   protected int standardHashCode() {
      Object var1 = this.getKey();
      Object var2 = this.getValue();
      return (var1 == null ? 0 : var1.hashCode()) ^ (var2 == null ? 0 : var2.hashCode());
   }

   @Beta
   protected String standardToString() {
      String var1 = String.valueOf(this.getKey());
      String var2 = String.valueOf(this.getValue());
      return new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append("=").append(var2).toString();
   }
}
