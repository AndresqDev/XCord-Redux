package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class StandardRowSortedTable<R, C, V> extends StandardTable<R, C, V> implements RowSortedTable<R, C, V> {
   private static final long serialVersionUID = 0L;

   StandardRowSortedTable(SortedMap<R, Map<C, V>> var1, Supplier<? extends Map<C, V>> var2) {
      super(var1, var2);
   }

   private SortedMap<R, Map<C, V>> sortedBackingMap() {
      return (SortedMap<R, Map<C, V>>)this.backingMap;
   }

   @Override
   public SortedSet<R> rowKeySet() {
      return (SortedSet<R>)this.rowMap().keySet();
   }

   @Override
   public SortedMap<R, Map<C, V>> rowMap() {
      return (SortedMap<R, Map<C, V>>)super.rowMap();
   }

   SortedMap<R, Map<C, V>> createRowMap() {
      return new StandardRowSortedTable.RowSortedMap();
   }

   private class RowSortedMap extends StandardTable<R, C, V>.RowMap implements SortedMap<R, Map<C, V>> {
      private RowSortedMap() {
      }

      public SortedSet<R> keySet() {
         return (SortedSet<R>)super.keySet();
      }

      SortedSet<R> createKeySet() {
         return new Maps.SortedKeySet<>(this);
      }

      @Override
      public Comparator<? super R> comparator() {
         return StandardRowSortedTable.this.sortedBackingMap().comparator();
      }

      @Override
      public R firstKey() {
         return StandardRowSortedTable.this.sortedBackingMap().firstKey();
      }

      @Override
      public R lastKey() {
         return StandardRowSortedTable.this.sortedBackingMap().lastKey();
      }

      @Override
      public SortedMap<R, Map<C, V>> headMap(R var1) {
         Preconditions.checkNotNull(var1);
         return new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().headMap((R)var1), StandardRowSortedTable.this.factory).rowMap();
      }

      @Override
      public SortedMap<R, Map<C, V>> subMap(R var1, R var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         return new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().subMap((R)var1, (R)var2), StandardRowSortedTable.this.factory)
            .rowMap();
      }

      @Override
      public SortedMap<R, Map<C, V>> tailMap(R var1) {
         Preconditions.checkNotNull(var1);
         return new StandardRowSortedTable<>(StandardRowSortedTable.this.sortedBackingMap().tailMap((R)var1), StandardRowSortedTable.this.factory).rowMap();
      }
   }
}
