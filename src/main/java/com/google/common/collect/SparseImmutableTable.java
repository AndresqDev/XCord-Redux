package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.Immutable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

@Immutable(
   containerOf = {"R", "C", "V"}
)
@ElementTypesAreNonnullByDefault
@GwtCompatible
final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
   static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable<>(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());
   private final ImmutableMap<R, ImmutableMap<C, V>> rowMap;
   private final ImmutableMap<C, ImmutableMap<R, V>> columnMap;
   private final int[] cellRowIndices;
   private final int[] cellColumnInRowIndices;

   SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> var1, ImmutableSet<R> var2, ImmutableSet<C> var3) {
      ImmutableMap var4 = Maps.indexMap(var2);
      LinkedHashMap var5 = Maps.newLinkedHashMap();

      for (Object var7 : var2) {
         var5.put(var7, new LinkedHashMap());
      }

      LinkedHashMap var16 = Maps.newLinkedHashMap();

      for (Object var8 : var3) {
         var16.put(var8, new LinkedHashMap());
      }

      int[] var18 = new int[var1.size()];
      int[] var19 = new int[var1.size()];

      for (int var9 = 0; var9 < var1.size(); var9++) {
         Table.Cell var10 = (Table.Cell)var1.get(var9);
         Object var11 = var10.getRowKey();
         Object var12 = var10.getColumnKey();
         Object var13 = var10.getValue();
         var18[var9] = (Integer)Objects.requireNonNull((Integer)var4.get(var11));
         Map var14 = Objects.requireNonNull((Map)var5.get(var11));
         var19[var9] = var14.size();
         Object var15 = var14.put(var12, var13);
         this.checkNoDuplicate((R)var11, (C)var12, (V)var15, (V)var13);
         ((Map)Objects.requireNonNull((Map)var16.get(var12))).put(var11, var13);
      }

      this.cellRowIndices = var18;
      this.cellColumnInRowIndices = var19;
      ImmutableMap.Builder var20 = new ImmutableMap.Builder(var5.size());

      for (Entry var23 : var5.entrySet()) {
         var20.put(var23.getKey(), ImmutableMap.copyOf((Map)var23.getValue()));
      }

      this.rowMap = var20.build();
      ImmutableMap.Builder var22 = new ImmutableMap.Builder(var16.size());

      for (Entry var25 : var16.entrySet()) {
         var22.put(var25.getKey(), ImmutableMap.copyOf((Map)var25.getValue()));
      }

      this.columnMap = var22.build();
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
   public int size() {
      return this.cellRowIndices.length;
   }

   @Override
   Table.Cell<R, C, V> getCell(int var1) {
      int var2 = this.cellRowIndices[var1];
      Entry var3 = this.rowMap.entrySet().asList().get(var2);
      ImmutableMap var4 = (ImmutableMap)var3.getValue();
      int var5 = this.cellColumnInRowIndices[var1];
      Entry var6 = (Entry)var4.entrySet().asList().get(var5);
      return cellOf((R)var3.getKey(), (C)var6.getKey(), (V)var6.getValue());
   }

   @Override
   V getValue(int var1) {
      int var2 = this.cellRowIndices[var1];
      ImmutableMap var3 = this.rowMap.values().asList().get(var2);
      int var4 = this.cellColumnInRowIndices[var1];
      return (V)var3.values().asList().get(var4);
   }

   @Override
   ImmutableTable.SerializedForm createSerializedForm() {
      ImmutableMap var1 = Maps.indexMap(this.columnKeySet());
      int[] var2 = new int[this.cellSet().size()];
      int var3 = 0;

      for (Table.Cell var5 : this.cellSet()) {
         var2[var3++] = (Integer)Objects.requireNonNull((Integer)var1.get(var5.getColumnKey()));
      }

      return ImmutableTable.SerializedForm.create(this, this.cellRowIndices, var2);
   }
}
