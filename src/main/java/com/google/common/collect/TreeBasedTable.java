package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
public class TreeBasedTable<R, C, V> extends StandardRowSortedTable<R, C, V> {
   private final Comparator<? super C> columnComparator;
   private static final long serialVersionUID = 0L;

   public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create() {
      return new TreeBasedTable<>(Ordering.natural(), Ordering.natural());
   }

   public static <R, C, V> TreeBasedTable<R, C, V> create(Comparator<? super R> var0, Comparator<? super C> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new TreeBasedTable<>(var0, var1);
   }

   public static <R, C, V> TreeBasedTable<R, C, V> create(TreeBasedTable<R, C, ? extends V> var0) {
      TreeBasedTable var1 = new TreeBasedTable(var0.rowComparator(), var0.columnComparator());
      var1.putAll(var0);
      return var1;
   }

   TreeBasedTable(Comparator<? super R> var1, Comparator<? super C> var2) {
      super(new TreeMap<>(var1), new TreeBasedTable.Factory<>(var2));
      this.columnComparator = var2;
   }

   @Deprecated
   public Comparator<? super R> rowComparator() {
      return Objects.requireNonNull(this.rowKeySet().comparator());
   }

   @Deprecated
   public Comparator<? super C> columnComparator() {
      return this.columnComparator;
   }

   public SortedMap<C, V> row(R var1) {
      return new TreeBasedTable.TreeRow(var1);
   }

   @Override
   public SortedSet<R> rowKeySet() {
      return super.rowKeySet();
   }

   @Override
   public SortedMap<R, Map<C, V>> rowMap() {
      return super.rowMap();
   }

   @Override
   Iterator<C> createColumnKeyIterator() {
      final Comparator var1 = this.columnComparator();
      final UnmodifiableIterator var2 = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function<Map<C, V>, Iterator<C>>(this) {
         public Iterator<C> apply(Map<C, V> var1) {
            return var1.keySet().iterator();
         }
      }), var1);
      return new AbstractIterator<C>(this) {
         C lastValue;

         @Override
         protected C computeNext() {
            while (var2.hasNext()) {
               Object var1x = var2.next();
               boolean var2x = this.lastValue != null && var1.compare(var1x, this.lastValue) == 0;
               if (!var2x) {
                  this.lastValue = (C)var1x;
                  return this.lastValue;
               }
            }

            this.lastValue = null;
            return (C)this.endOfData();
         }
      };
   }

   private static class Factory<C, V> implements Supplier<TreeMap<C, V>>, Serializable {
      final Comparator<? super C> comparator;
      private static final long serialVersionUID = 0L;

      Factory(Comparator<? super C> var1) {
         this.comparator = var1;
      }

      public TreeMap<C, V> get() {
         return new TreeMap<>(this.comparator);
      }
   }

   private class TreeRow extends StandardTable<R, C, V>.Row implements SortedMap<C, V> {
      final C lowerBound;
      final C upperBound;
      transient SortedMap<C, V> wholeRow;

      TreeRow(R var2) {
         this(var2, null, null);
      }

      TreeRow(R var2, C var3, C var4) {
         super(var2);
         this.lowerBound = (C)var3;
         this.upperBound = (C)var4;
         Preconditions.checkArgument(var3 == null || var4 == null || this.compare(var3, var4) <= 0);
      }

      public SortedSet<C> keySet() {
         return new Maps.SortedKeySet<>(this);
      }

      @Override
      public Comparator<? super C> comparator() {
         return TreeBasedTable.this.columnComparator();
      }

      int compare(Object var1, Object var2) {
         Comparator var3 = this.comparator();
         return var3.compare(var1, var2);
      }

      boolean rangeContains(Object var1) {
         return var1 != null
            && (this.lowerBound == null || this.compare(this.lowerBound, var1) <= 0)
            && (this.upperBound == null || this.compare(this.upperBound, var1) > 0);
      }

      @Override
      public SortedMap<C, V> subMap(C var1, C var2) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(var1)) && this.rangeContains(Preconditions.checkNotNull(var2)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, var1, var2);
      }

      @Override
      public SortedMap<C, V> headMap(C var1) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(var1)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, this.lowerBound, var1);
      }

      @Override
      public SortedMap<C, V> tailMap(C var1) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(var1)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, var1, this.upperBound);
      }

      @Override
      public C firstKey() {
         this.updateBackingRowMapField();
         if (this.backingRowMap == null) {
            throw new NoSuchElementException();
         } else {
            return ((SortedMap)this.backingRowMap).firstKey();
         }
      }

      @Override
      public C lastKey() {
         this.updateBackingRowMapField();
         if (this.backingRowMap == null) {
            throw new NoSuchElementException();
         } else {
            return ((SortedMap)this.backingRowMap).lastKey();
         }
      }

      void updateWholeRowField() {
         if (this.wholeRow == null || this.wholeRow.isEmpty() && TreeBasedTable.this.backingMap.containsKey(this.rowKey)) {
            this.wholeRow = (SortedMap<C, V>)TreeBasedTable.this.backingMap.get(this.rowKey);
         }
      }

      SortedMap<C, V> computeBackingRowMap() {
         this.updateWholeRowField();
         SortedMap var1 = this.wholeRow;
         if (var1 != null) {
            if (this.lowerBound != null) {
               var1 = var1.tailMap(this.lowerBound);
            }

            if (this.upperBound != null) {
               var1 = var1.headMap(this.upperBound);
            }

            return var1;
         } else {
            return null;
         }
      }

      @Override
      void maintainEmptyInvariant() {
         this.updateWholeRowField();
         if (this.wholeRow != null && this.wholeRow.isEmpty()) {
            TreeBasedTable.this.backingMap.remove(this.rowKey);
            this.wholeRow = null;
            this.backingRowMap = null;
         }
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.rangeContains(var1) && super.containsKey(var1);
      }

      @Override
      public V put(C var1, V var2) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(var1)));
         return (V)super.put(var1, (V)var2);
      }
   }
}
