package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractTable<R, C, V> implements Table<R, C, V> {
   @LazyInit
   private transient Set<Table.Cell<R, C, V>> cellSet;
   @LazyInit
   private transient Collection<V> values;

   @Override
   public boolean containsRow(Object var1) {
      return Maps.safeContainsKey(this.rowMap(), var1);
   }

   @Override
   public boolean containsColumn(Object var1) {
      return Maps.safeContainsKey(this.columnMap(), var1);
   }

   @Override
   public Set<R> rowKeySet() {
      return this.rowMap().keySet();
   }

   @Override
   public Set<C> columnKeySet() {
      return this.columnMap().keySet();
   }

   @Override
   public boolean containsValue(Object var1) {
      for (Map var3 : this.rowMap().values()) {
         if (var3.containsValue(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean contains(Object var1, Object var2) {
      Map var3 = Maps.safeGet(this.rowMap(), var1);
      return var3 != null && Maps.safeContainsKey(var3, var2);
   }

   @Override
   public V get(Object var1, Object var2) {
      Map var3 = Maps.safeGet(this.rowMap(), var1);
      return var3 == null ? null : Maps.safeGet(var3, var2);
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public void clear() {
      Iterators.clear(this.cellSet().iterator());
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1, Object var2) {
      Map var3 = Maps.safeGet(this.rowMap(), var1);
      return var3 == null ? null : Maps.safeRemove(var3, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public V put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3) {
      return this.row((R)var1).put((C)var2, (V)var3);
   }

   @Override
   public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
      for (Table.Cell var3 : var1.cellSet()) {
         this.put((R)var3.getRowKey(), (C)var3.getColumnKey(), (V)var3.getValue());
      }
   }

   @Override
   public Set<Table.Cell<R, C, V>> cellSet() {
      Set var1 = this.cellSet;
      return var1 == null ? (this.cellSet = this.createCellSet()) : var1;
   }

   Set<Table.Cell<R, C, V>> createCellSet() {
      return new AbstractTable.CellSet();
   }

   abstract Iterator<Table.Cell<R, C, V>> cellIterator();

   abstract Spliterator<Table.Cell<R, C, V>> cellSpliterator();

   @Override
   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 == null ? (this.values = this.createValues()) : var1;
   }

   Collection<V> createValues() {
      return new AbstractTable.Values();
   }

   Iterator<V> valuesIterator() {
      return new TransformedIterator<Table.Cell<R, C, V>, V>(this, this.cellSet().iterator()) {
         @ParametricNullness
         V transform(Table.Cell<R, C, V> var1) {
            return (V)var1.getValue();
         }
      };
   }

   Spliterator<V> valuesSpliterator() {
      return CollectSpliterators.map(this.cellSpliterator(), Table.Cell::getValue);
   }

   @Override
   public boolean equals(Object var1) {
      return Tables.equalsImpl(this, var1);
   }

   @Override
   public int hashCode() {
      return this.cellSet().hashCode();
   }

   @Override
   public String toString() {
      return this.rowMap().toString();
   }

   class CellSet extends AbstractSet<Table.Cell<R, C, V>> {
      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            Map var3 = Maps.safeGet(AbstractTable.this.rowMap(), var2.getRowKey());
            return var3 != null && Collections2.safeContains(var3.entrySet(), Maps.immutableEntry(var2.getColumnKey(), var2.getValue()));
         }
      }

      @Override
      public boolean remove(Object var1) {
         if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            Map var3 = Maps.safeGet(AbstractTable.this.rowMap(), var2.getRowKey());
            return var3 != null && Collections2.safeRemove(var3.entrySet(), Maps.immutableEntry(var2.getColumnKey(), var2.getValue()));
         }
      }

      @Override
      public void clear() {
         AbstractTable.this.clear();
      }

      @Override
      public Iterator<Table.Cell<R, C, V>> iterator() {
         return AbstractTable.this.cellIterator();
      }

      @Override
      public Spliterator<Table.Cell<R, C, V>> spliterator() {
         return AbstractTable.this.cellSpliterator();
      }

      @Override
      public int size() {
         return AbstractTable.this.size();
      }
   }

   class Values extends AbstractCollection<V> {
      @Override
      public Iterator<V> iterator() {
         return AbstractTable.this.valuesIterator();
      }

      @Override
      public Spliterator<V> spliterator() {
         return AbstractTable.this.valuesSpliterator();
      }

      @Override
      public boolean contains(Object var1) {
         return AbstractTable.this.containsValue(var1);
      }

      @Override
      public void clear() {
         AbstractTable.this.clear();
      }

      @Override
      public int size() {
         return AbstractTable.this.size();
      }
   }
}
