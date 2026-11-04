package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {
   abstract Table.Cell<R, C, V> getCell(int var1);

   @Override
   final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
      return (ImmutableSet<Table.Cell<R, C, V>>)(this.isEmpty() ? ImmutableSet.of() : new RegularImmutableTable.CellSet());
   }

   abstract V getValue(int var1);

   @Override
   final ImmutableCollection<V> createValues() {
      return (ImmutableCollection<V>)(this.isEmpty() ? ImmutableList.of() : new RegularImmutableTable.Values());
   }

   static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> var0, final Comparator<? super R> var1, final Comparator<? super C> var2) {
      Preconditions.checkNotNull(var0);
      if (var1 != null || var2 != null) {
         Comparator var3 = new Comparator<Table.Cell<R, C, V>>() {
            public int compare(Table.Cell<R, C, V> var1x, Table.Cell<R, C, V> var2x) {
               int var3 = var1 == null ? 0 : var1.compare(var1x.getRowKey(), var2x.getRowKey());
               if (var3 != 0) {
                  return var3;
               } else {
                  return var2 == null ? 0 : var2.compare(var1x.getColumnKey(), var2x.getColumnKey());
               }
            }
         };
         Collections.sort(var0, var3);
      }

      return forCellsInternal(var0, var1, var2);
   }

   static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> var0) {
      return forCellsInternal(var0, null, null);
   }

   private static <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(
      Iterable<Table.Cell<R, C, V>> var0, Comparator<? super R> var1, Comparator<? super C> var2
   ) {
      LinkedHashSet var3 = new LinkedHashSet();
      LinkedHashSet var4 = new LinkedHashSet();
      ImmutableList var5 = ImmutableList.copyOf(var0);

      for (Table.Cell var7 : var0) {
         var3.add(var7.getRowKey());
         var4.add(var7.getColumnKey());
      }

      ImmutableSet var8 = var1 == null ? ImmutableSet.copyOf(var3) : ImmutableSet.copyOf(ImmutableList.sortedCopyOf(var1, var3));
      ImmutableSet var9 = var2 == null ? ImmutableSet.copyOf(var4) : ImmutableSet.copyOf(ImmutableList.sortedCopyOf(var2, var4));
      return forOrderedComponents(var5, var8, var9);
   }

   static <R, C, V> RegularImmutableTable<R, C, V> forOrderedComponents(ImmutableList<Table.Cell<R, C, V>> var0, ImmutableSet<R> var1, ImmutableSet<C> var2) {
      return (RegularImmutableTable<R, C, V>)((long)var0.size() > (long)var1.size() * (long)var2.size() / 2L
         ? new DenseImmutableTable<>(var0, var1, var2)
         : new SparseImmutableTable<>(var0, var1, var2));
   }

   final void checkNoDuplicate(R var1, C var2, V var3, V var4) {
      Preconditions.checkArgument(var3 == null, "Duplicate key: (row=%s, column=%s), values: [%s, %s].", var1, var2, var4, var3);
   }

   private final class CellSet extends IndexedImmutableSet<Table.Cell<R, C, V>> {
      private CellSet() {
      }

      @Override
      public int size() {
         return RegularImmutableTable.this.size();
      }

      Table.Cell<R, C, V> get(int var1) {
         return RegularImmutableTable.this.getCell(var1);
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            Object var3 = RegularImmutableTable.this.get(var2.getRowKey(), var2.getColumnKey());
            return var3 != null && var3.equals(var2.getValue());
         }
      }

      @Override
      boolean isPartialView() {
         return false;
      }
   }

   private final class Values extends ImmutableList<V> {
      private Values() {
      }

      @Override
      public int size() {
         return RegularImmutableTable.this.size();
      }

      @Override
      public V get(int var1) {
         return RegularImmutableTable.this.getValue(var1);
      }

      @Override
      boolean isPartialView() {
         return true;
      }
   }
}
