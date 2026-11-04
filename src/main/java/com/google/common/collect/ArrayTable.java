package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible(
   emulated = true
)
public final class ArrayTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {
   private final ImmutableList<R> rowList;
   private final ImmutableList<C> columnList;
   private final ImmutableMap<R, Integer> rowKeyToIndex;
   private final ImmutableMap<C, Integer> columnKeyToIndex;
   private final V[][] array;
   private transient ArrayTable<R, C, V>.ColumnMap columnMap;
   private transient ArrayTable<R, C, V>.RowMap rowMap;
   private static final long serialVersionUID = 0L;

   public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> var0, Iterable<? extends C> var1) {
      return new ArrayTable<>(var0, var1);
   }

   public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, ? extends V> var0) {
      return var0 instanceof ArrayTable ? new ArrayTable<>((ArrayTable<R, C, V>)var0) : new ArrayTable<>(var0);
   }

   private ArrayTable(Iterable<? extends R> var1, Iterable<? extends C> var2) {
      this.rowList = ImmutableList.copyOf(var1);
      this.columnList = ImmutableList.copyOf(var2);
      Preconditions.checkArgument(this.rowList.isEmpty() == this.columnList.isEmpty());
      this.rowKeyToIndex = Maps.indexMap(this.rowList);
      this.columnKeyToIndex = Maps.indexMap(this.columnList);
      Object[][] var3 = new Object[this.rowList.size()][this.columnList.size()];
      this.array = (V[][])var3;
      this.eraseAll();
   }

   private ArrayTable(Table<R, C, ? extends V> var1) {
      this(var1.rowKeySet(), var1.columnKeySet());
      this.putAll(var1);
   }

   private ArrayTable(ArrayTable<R, C, V> var1) {
      this.rowList = var1.rowList;
      this.columnList = var1.columnList;
      this.rowKeyToIndex = var1.rowKeyToIndex;
      this.columnKeyToIndex = var1.columnKeyToIndex;
      Object[][] var2 = new Object[this.rowList.size()][this.columnList.size()];
      this.array = (V[][])var2;

      for (int var3 = 0; var3 < this.rowList.size(); var3++) {
         System.arraycopy(var1.array[var3], 0, var2[var3], 0, var1.array[var3].length);
      }
   }

   public ImmutableList<R> rowKeyList() {
      return this.rowList;
   }

   public ImmutableList<C> columnKeyList() {
      return this.columnList;
   }

   public V at(int var1, int var2) {
      Preconditions.checkElementIndex(var1, this.rowList.size());
      Preconditions.checkElementIndex(var2, this.columnList.size());
      return this.array[var1][var2];
   }

   @CanIgnoreReturnValue
   public V set(int var1, int var2, V var3) {
      Preconditions.checkElementIndex(var1, this.rowList.size());
      Preconditions.checkElementIndex(var2, this.columnList.size());
      Object var4 = this.array[var1][var2];
      this.array[var1][var2] = (V)var3;
      return (V)var4;
   }

   @GwtIncompatible
   public V[][] toArray(Class<V> var1) {
      Object[][] var2 = (Object[][])Array.newInstance(var1, this.rowList.size(), this.columnList.size());

      for (int var3 = 0; var3 < this.rowList.size(); var3++) {
         System.arraycopy(this.array[var3], 0, var2[var3], 0, this.array[var3].length);
      }

      return (V[][])var2;
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   public void eraseAll() {
      for (Object[] var4 : this.array) {
         Arrays.fill(var4, null);
      }
   }

   @Override
   public boolean contains(Object var1, Object var2) {
      return this.containsRow(var1) && this.containsColumn(var2);
   }

   @Override
   public boolean containsColumn(Object var1) {
      return this.columnKeyToIndex.containsKey(var1);
   }

   @Override
   public boolean containsRow(Object var1) {
      return this.rowKeyToIndex.containsKey(var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      for (Object[] var5 : this.array) {
         for (Object var9 : var5) {
            if (Objects.equal(var1, var9)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public V get(Object var1, Object var2) {
      Integer var3 = this.rowKeyToIndex.get(var1);
      Integer var4 = this.columnKeyToIndex.get(var2);
      return var3 != null && var4 != null ? this.at(var3, var4) : null;
   }

   @Override
   public boolean isEmpty() {
      return this.rowList.isEmpty() || this.columnList.isEmpty();
   }

   @CanIgnoreReturnValue
   @Override
   public V put(R var1, C var2, V var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Integer var4 = this.rowKeyToIndex.get(var1);
      Preconditions.checkArgument(var4 != null, "Row %s not in %s", var1, this.rowList);
      Integer var5 = this.columnKeyToIndex.get(var2);
      Preconditions.checkArgument(var5 != null, "Column %s not in %s", var2, this.columnList);
      return this.set(var4, var5, (V)var3);
   }

   @Override
   public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
      super.putAll(var1);
   }

   @Deprecated
   @DoNotCall("Always throws UnsupportedOperationException")
   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   @CanIgnoreReturnValue
   public V erase(Object var1, Object var2) {
      Integer var3 = this.rowKeyToIndex.get(var1);
      Integer var4 = this.columnKeyToIndex.get(var2);
      return var3 != null && var4 != null ? this.set(var3, var4, null) : null;
   }

   @Override
   public int size() {
      return this.rowList.size() * this.columnList.size();
   }

   @Override
   public Set<Table.Cell<R, C, V>> cellSet() {
      return super.cellSet();
   }

   @Override
   Iterator<Table.Cell<R, C, V>> cellIterator() {
      return new AbstractIndexedListIterator<Table.Cell<R, C, V>>(this.size()) {
         protected Table.Cell<R, C, V> get(int var1) {
            return ArrayTable.this.getCell(var1);
         }
      };
   }

   @Override
   Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
      return CollectSpliterators.indexed(this.size(), 273, this::getCell);
   }

   private Table.Cell<R, C, V> getCell(final int var1) {
      return new Tables.AbstractCell<R, C, V>() {
         final int rowIndex = var1 / ArrayTable.this.columnList.size();
         final int columnIndex = var1 % ArrayTable.this.columnList.size();

         @Override
         public R getRowKey() {
            return ArrayTable.this.rowList.get(this.rowIndex);
         }

         @Override
         public C getColumnKey() {
            return ArrayTable.this.columnList.get(this.columnIndex);
         }

         @Override
         public V getValue() {
            return (V)ArrayTable.this.at(this.rowIndex, this.columnIndex);
         }
      };
   }

   private V getValue(int var1) {
      int var2 = var1 / this.columnList.size();
      int var3 = var1 % this.columnList.size();
      return this.at(var2, var3);
   }

   @Override
   public Map<R, V> column(C var1) {
      Preconditions.checkNotNull(var1);
      Integer var2 = this.columnKeyToIndex.get(var1);
      return (Map<R, V>)(var2 == null ? Collections.emptyMap() : new ArrayTable.Column(var2));
   }

   public ImmutableSet<C> columnKeySet() {
      return this.columnKeyToIndex.keySet();
   }

   @Override
   public Map<C, Map<R, V>> columnMap() {
      ArrayTable.ColumnMap var1 = this.columnMap;
      return var1 == null ? (this.columnMap = new ArrayTable.ColumnMap()) : var1;
   }

   @Override
   public Map<C, V> row(R var1) {
      Preconditions.checkNotNull(var1);
      Integer var2 = this.rowKeyToIndex.get(var1);
      return (Map<C, V>)(var2 == null ? Collections.emptyMap() : new ArrayTable.Row(var2));
   }

   public ImmutableSet<R> rowKeySet() {
      return this.rowKeyToIndex.keySet();
   }

   @Override
   public Map<R, Map<C, V>> rowMap() {
      ArrayTable.RowMap var1 = this.rowMap;
      return var1 == null ? (this.rowMap = new ArrayTable.RowMap()) : var1;
   }

   @Override
   public Collection<V> values() {
      return super.values();
   }

   @Override
   Iterator<V> valuesIterator() {
      return new AbstractIndexedListIterator<V>(this.size()) {
         @Override
         protected V get(int var1) {
            return (V)ArrayTable.this.getValue(var1);
         }
      };
   }

   @Override
   Spliterator<V> valuesSpliterator() {
      return CollectSpliterators.indexed(this.size(), 16, this::getValue);
   }

   private abstract static class ArrayMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> {
      private final ImmutableMap<K, Integer> keyIndex;

      private ArrayMap(ImmutableMap<K, Integer> var1) {
         this.keyIndex = var1;
      }

      @Override
      public Set<K> keySet() {
         return this.keyIndex.keySet();
      }

      K getKey(int var1) {
         return this.keyIndex.keySet().asList().get(var1);
      }

      abstract String getKeyRole();

      @ParametricNullness
      abstract V getValue(int var1);

      @ParametricNullness
      abstract V setValue(int var1, @ParametricNullness V var2);

      @Override
      public int size() {
         return this.keyIndex.size();
      }

      @Override
      public boolean isEmpty() {
         return this.keyIndex.isEmpty();
      }

      Entry<K, V> getEntry(final int var1) {
         Preconditions.checkElementIndex(var1, this.size());
         return new AbstractMapEntry<K, V>() {
            @Override
            public K getKey() {
               return (K)ArrayMap.this.getKey(var1);
            }

            @ParametricNullness
            @Override
            public V getValue() {
               return (V)ArrayMap.this.getValue(var1);
            }

            @ParametricNullness
            @Override
            public V setValue(@ParametricNullness V var1x) {
               return (V)ArrayMap.this.setValue(var1, var1x);
            }
         };
      }

      @Override
      Iterator<Entry<K, V>> entryIterator() {
         return new AbstractIndexedListIterator<Entry<K, V>>(this.size()) {
            protected Entry<K, V> get(int var1) {
               return ArrayMap.this.getEntry(var1);
            }
         };
      }

      @Override
      Spliterator<Entry<K, V>> entrySpliterator() {
         return CollectSpliterators.indexed(this.size(), 16, this::getEntry);
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.keyIndex.containsKey(var1);
      }

      @Override
      public V get(Object var1) {
         Integer var2 = this.keyIndex.get(var1);
         return var2 == null ? null : this.getValue(var2);
      }

      @Override
      public V put(K var1, @ParametricNullness V var2) {
         Integer var3 = this.keyIndex.get(var1);
         if (var3 == null) {
            String var4 = this.getKeyRole();
            String var5 = String.valueOf(var1);
            String var6 = String.valueOf(this.keyIndex.keySet());
            throw new IllegalArgumentException(
               new StringBuilder(9 + String.valueOf(var4).length() + String.valueOf(var5).length() + String.valueOf(var6).length())
                  .append(var4)
                  .append(" ")
                  .append(var5)
                  .append(" not in ")
                  .append(var6)
                  .toString()
            );
         } else {
            return this.setValue(var3, (V)var2);
         }
      }

      @Override
      public V remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }
   }

   private class Column extends ArrayTable.ArrayMap<R, V> {
      final int columnIndex;

      Column(int var2) {
         super(ArrayTable.this.rowKeyToIndex);
         this.columnIndex = var2;
      }

      @Override
      String getKeyRole() {
         return "Row";
      }

      @Override
      V getValue(int var1) {
         return ArrayTable.this.at(var1, this.columnIndex);
      }

      @Override
      V setValue(int var1, V var2) {
         return ArrayTable.this.set(var1, this.columnIndex, (V)var2);
      }
   }

   private class ColumnMap extends ArrayTable.ArrayMap<C, Map<R, V>> {
      private ColumnMap() {
         super(ArrayTable.this.columnKeyToIndex);
      }

      @Override
      String getKeyRole() {
         return "Column";
      }

      Map<R, V> getValue(int var1) {
         return ArrayTable.this.new Column(var1);
      }

      Map<R, V> setValue(int var1, Map<R, V> var2) {
         throw new UnsupportedOperationException();
      }

      public Map<R, V> put(C var1, Map<R, V> var2) {
         throw new UnsupportedOperationException();
      }
   }

   private class Row extends ArrayTable.ArrayMap<C, V> {
      final int rowIndex;

      Row(int var2) {
         super(ArrayTable.this.columnKeyToIndex);
         this.rowIndex = var2;
      }

      @Override
      String getKeyRole() {
         return "Column";
      }

      @Override
      V getValue(int var1) {
         return ArrayTable.this.at(this.rowIndex, var1);
      }

      @Override
      V setValue(int var1, V var2) {
         return ArrayTable.this.set(this.rowIndex, var1, (V)var2);
      }
   }

   private class RowMap extends ArrayTable.ArrayMap<R, Map<C, V>> {
      private RowMap() {
         super(ArrayTable.this.rowKeyToIndex);
      }

      @Override
      String getKeyRole() {
         return "Row";
      }

      Map<C, V> getValue(int var1) {
         return ArrayTable.this.new Row(var1);
      }

      Map<C, V> setValue(int var1, Map<C, V> var2) {
         throw new UnsupportedOperationException();
      }

      public Map<C, V> put(R var1, Map<C, V> var2) {
         throw new UnsupportedOperationException();
      }
   }
}
