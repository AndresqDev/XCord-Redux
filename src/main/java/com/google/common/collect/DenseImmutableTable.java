package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.Immutable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

@Immutable(
   containerOf = {"R", "C", "V"}
)
@ElementTypesAreNonnullByDefault
@GwtCompatible
final class DenseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
   private final ImmutableMap<R, Integer> rowKeyToIndex;
   private final ImmutableMap<C, Integer> columnKeyToIndex;
   private final ImmutableMap<R, ImmutableMap<C, V>> rowMap;
   private final ImmutableMap<C, ImmutableMap<R, V>> columnMap;
   private final int[] rowCounts;
   private final int[] columnCounts;
   private final V[][] values;
   private final int[] cellRowIndices;
   private final int[] cellColumnIndices;

   DenseImmutableTable(ImmutableList<Table.Cell<R, C, V>> var1, ImmutableSet<R> var2, ImmutableSet<C> var3) {
      Object[][] var4 = new Object[var2.size()][var3.size()];
      this.values = (V[][])var4;
      this.rowKeyToIndex = Maps.indexMap(var2);
      this.columnKeyToIndex = Maps.indexMap(var3);
      this.rowCounts = new int[this.rowKeyToIndex.size()];
      this.columnCounts = new int[this.columnKeyToIndex.size()];
      int[] var5 = new int[var1.size()];
      int[] var6 = new int[var1.size()];

      for (int var7 = 0; var7 < var1.size(); var7++) {
         Table.Cell var8 = (Table.Cell)var1.get(var7);
         Object var9 = var8.getRowKey();
         Object var10 = var8.getColumnKey();
         int var11 = Objects.requireNonNull(this.rowKeyToIndex.get(var9));
         int var12 = Objects.requireNonNull(this.columnKeyToIndex.get(var10));
         Object var13 = this.values[var11][var12];
         this.checkNoDuplicate((R)var9, (C)var10, (V)var13, (V)var8.getValue());
         this.values[var11][var12] = (V)var8.getValue();
         this.rowCounts[var11]++;
         this.columnCounts[var12]++;
         var5[var7] = var11;
         var6[var7] = var12;
      }

      this.cellRowIndices = var5;
      this.cellColumnIndices = var6;
      this.rowMap = new DenseImmutableTable.RowMap();
      this.columnMap = new DenseImmutableTable.ColumnMap();
   }

   @Override
   public ImmutableMap<C, Map<R, V>> columnMap() {
      ImmutableMap var1 = this.columnMap;
      return ImmutableMap.copyOf(var1);
   }

   @Override
   public ImmutableMap<R, Map<C, V>> rowMap() {
      ImmutableMap var1 = this.rowMap;
      return ImmutableMap.copyOf(var1);
   }

   @Override
   public V get(Object var1, Object var2) {
      Integer var3 = this.rowKeyToIndex.get(var1);
      Integer var4 = this.columnKeyToIndex.get(var2);
      return var3 != null && var4 != null ? this.values[var3][var4] : null;
   }

   @Override
   public int size() {
      return this.cellRowIndices.length;
   }

   @Override
   Table.Cell<R, C, V> getCell(int var1) {
      int var2 = this.cellRowIndices[var1];
      int var3 = this.cellColumnIndices[var1];
      Object var4 = this.rowKeySet().asList().get(var2);
      Object var5 = this.columnKeySet().asList().get(var3);
      Object var6 = Objects.requireNonNull(this.values[var2][var3]);
      return cellOf((R)var4, (C)var5, (V)var6);
   }

   @Override
   V getValue(int var1) {
      return Objects.requireNonNull(this.values[this.cellRowIndices[var1]][this.cellColumnIndices[var1]]);
   }

   @Override
   ImmutableTable.SerializedForm createSerializedForm() {
      return ImmutableTable.SerializedForm.create(this, this.cellRowIndices, this.cellColumnIndices);
   }

   private final class Column extends DenseImmutableTable.ImmutableArrayMap<R, V> {
      private final int columnIndex;

      Column(int var2) {
         super(DenseImmutableTable.this.columnCounts[var2]);
         this.columnIndex = var2;
      }

      @Override
      ImmutableMap<R, Integer> keyToIndex() {
         return DenseImmutableTable.this.rowKeyToIndex;
      }

      @Override
      V getValue(int var1) {
         return DenseImmutableTable.this.values[var1][this.columnIndex];
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }

   private final class ColumnMap extends DenseImmutableTable.ImmutableArrayMap<C, ImmutableMap<R, V>> {
      private ColumnMap() {
         super(DenseImmutableTable.this.columnCounts.length);
      }

      @Override
      ImmutableMap<C, Integer> keyToIndex() {
         return DenseImmutableTable.this.columnKeyToIndex;
      }

      ImmutableMap<R, V> getValue(int var1) {
         return DenseImmutableTable.this.new Column(var1);
      }

      @Override
      boolean isPartialView() {
         return false;
      }
   }

   private abstract static class ImmutableArrayMap<K, V> extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
      private final int size;

      ImmutableArrayMap(int var1) {
         this.size = var1;
      }

      abstract ImmutableMap<K, Integer> keyToIndex();

      private boolean isFull() {
         return this.size == this.keyToIndex().size();
      }

      K getKey(int var1) {
         return this.keyToIndex().keySet().asList().get(var1);
      }

      abstract V getValue(int var1);

      @Override
      ImmutableSet<K> createKeySet() {
         return this.isFull() ? this.keyToIndex().keySet() : super.createKeySet();
      }

      @Override
      public int size() {
         return this.size;
      }

      @Override
      public V get(Object var1) {
         Integer var2 = this.keyToIndex().get(var1);
         return var2 == null ? null : this.getValue(var2);
      }

      @Override
      UnmodifiableIterator<Entry<K, V>> entryIterator() {
         return new AbstractIterator<Entry<K, V>>() {
            private int index = -1;
            private final int maxIndex = ImmutableArrayMap.this.keyToIndex().size();

            protected Entry<K, V> computeNext() {
               this.index++;

               while (this.index < this.maxIndex) {
                  Object var1 = ImmutableArrayMap.this.getValue(this.index);
                  if (var1 != null) {
                     return Maps.immutableEntry((K)ImmutableArrayMap.this.getKey(this.index), (V)var1);
                  }

                  this.index++;
               }

               return this.endOfData();
            }
         };
      }
   }

   private final class Row extends DenseImmutableTable.ImmutableArrayMap<C, V> {
      private final int rowIndex;

      Row(int var2) {
         super(DenseImmutableTable.this.rowCounts[var2]);
         this.rowIndex = var2;
      }

      @Override
      ImmutableMap<C, Integer> keyToIndex() {
         return DenseImmutableTable.this.columnKeyToIndex;
      }

      @Override
      V getValue(int var1) {
         return DenseImmutableTable.this.values[this.rowIndex][var1];
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }

   private final class RowMap extends DenseImmutableTable.ImmutableArrayMap<R, ImmutableMap<C, V>> {
      private RowMap() {
         super(DenseImmutableTable.this.rowCounts.length);
      }

      @Override
      ImmutableMap<R, Integer> keyToIndex() {
         return DenseImmutableTable.this.rowKeyToIndex;
      }

      ImmutableMap<C, V> getValue(int var1) {
         return DenseImmutableTable.this.new Row(var1);
      }

      @Override
      boolean isPartialView() {
         return false;
      }
   }
}
