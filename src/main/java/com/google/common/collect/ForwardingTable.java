package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingTable<R, C, V> extends ForwardingObject implements Table<R, C, V> {
   protected ForwardingTable() {
   }

   protected abstract Table<R, C, V> delegate();

   @Override
   public Set<Table.Cell<R, C, V>> cellSet() {
      return this.delegate().cellSet();
   }

   @Override
   public void clear() {
      this.delegate().clear();
   }

   @Override
   public Map<R, V> column(@ParametricNullness C var1) {
      return this.delegate().column((C)var1);
   }

   @Override
   public Set<C> columnKeySet() {
      return this.delegate().columnKeySet();
   }

   @Override
   public Map<C, Map<R, V>> columnMap() {
      return this.delegate().columnMap();
   }

   @Override
   public boolean contains(Object var1, Object var2) {
      return this.delegate().contains(var1, var2);
   }

   @Override
   public boolean containsColumn(Object var1) {
      return this.delegate().containsColumn(var1);
   }

   @Override
   public boolean containsRow(Object var1) {
      return this.delegate().containsRow(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.delegate().containsValue(var1);
   }

   @Override
   public V get(Object var1, Object var2) {
      return this.delegate().get(var1, var2);
   }

   @Override
   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   @CanIgnoreReturnValue
   @Override
   public V put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3) {
      return this.delegate().put((R)var1, (C)var2, (V)var3);
   }

   @Override
   public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
      this.delegate().putAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1, Object var2) {
      return this.delegate().remove(var1, var2);
   }

   @Override
   public Map<C, V> row(@ParametricNullness R var1) {
      return this.delegate().row((R)var1);
   }

   @Override
   public Set<R> rowKeySet() {
      return this.delegate().rowKeySet();
   }

   @Override
   public Map<R, Map<C, V>> rowMap() {
      return this.delegate().rowMap();
   }

   @Override
   public int size() {
      return this.delegate().size();
   }

   @Override
   public Collection<V> values() {
      return this.delegate().values();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }
}
