package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class StandardTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {
   @GwtTransient
   final Map<R, Map<C, V>> backingMap;
   @GwtTransient
   final Supplier<? extends Map<C, V>> factory;
   private transient Set<C> columnKeySet;
   private transient Map<R, Map<C, V>> rowMap;
   private transient StandardTable<R, C, V>.ColumnMap columnMap;
   private static final long serialVersionUID = 0L;

   StandardTable(Map<R, Map<C, V>> var1, Supplier<? extends Map<C, V>> var2) {
      this.backingMap = var1;
      this.factory = var2;
   }

   @Override
   public boolean contains(Object var1, Object var2) {
      return var1 != null && var2 != null && super.contains(var1, var2);
   }

   @Override
   public boolean containsColumn(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         for (Map var3 : this.backingMap.values()) {
            if (Maps.safeContainsKey(var3, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean containsRow(Object var1) {
      return var1 != null && Maps.safeContainsKey(this.backingMap, var1);
   }

   @Override
   public boolean containsValue(Object var1) {
      return var1 != null && super.containsValue(var1);
   }

   @Override
   public V get(Object var1, Object var2) {
      return var1 != null && var2 != null ? super.get(var1, var2) : null;
   }

   @Override
   public boolean isEmpty() {
      return this.backingMap.isEmpty();
   }

   @Override
   public int size() {
      int var1 = 0;

      for (Map var3 : this.backingMap.values()) {
         var1 += var3.size();
      }

      return var1;
   }

   @Override
   public void clear() {
      this.backingMap.clear();
   }

   private Map<C, V> getOrCreate(R var1) {
      Map var2 = this.backingMap.get(var1);
      if (var2 == null) {
         var2 = this.factory.get();
         this.backingMap.put((R)var1, var2);
      }

      return var2;
   }

   @CanIgnoreReturnValue
   @Override
   public V put(R var1, C var2, V var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return this.getOrCreate((R)var1).put((C)var2, (V)var3);
   }

   @CanIgnoreReturnValue
   @Override
   public V remove(Object var1, Object var2) {
      if (var1 != null && var2 != null) {
         Map var3 = Maps.safeGet(this.backingMap, var1);
         if (var3 == null) {
            return null;
         } else {
            Object var4 = var3.remove(var2);
            if (var3.isEmpty()) {
               this.backingMap.remove(var1);
            }

            return (V)var4;
         }
      } else {
         return null;
      }
   }

   @CanIgnoreReturnValue
   private Map<R, V> removeColumn(Object var1) {
      LinkedHashMap var2 = new LinkedHashMap();
      Iterator var3 = this.backingMap.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Object var5 = ((Map)var4.getValue()).remove(var1);
         if (var5 != null) {
            var2.put(var4.getKey(), var5);
            if (((Map)var4.getValue()).isEmpty()) {
               var3.remove();
            }
         }
      }

      return var2;
   }

   private boolean containsMapping(Object var1, Object var2, Object var3) {
      return var3 != null && var3.equals(this.get(var1, var2));
   }

   private boolean removeMapping(Object var1, Object var2, Object var3) {
      if (this.containsMapping(var1, var2, var3)) {
         this.remove(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Set<Table.Cell<R, C, V>> cellSet() {
      return super.cellSet();
   }

   @Override
   Iterator<Table.Cell<R, C, V>> cellIterator() {
      return new StandardTable.CellIterator();
   }

   @Override
   Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
      return CollectSpliterators.flatMap(
         this.backingMap.entrySet().spliterator(),
         var0 -> CollectSpliterators.map(var0.getValue().entrySet().spliterator(), var1 -> Tables.immutableCell(var0.getKey(), var1.getKey(), var1.getValue())),
         65,
         (long)this.size()
      );
   }

   @Override
   public Map<C, V> row(R var1) {
      return new StandardTable.Row(var1);
   }

   @Override
   public Map<R, V> column(C var1) {
      return new StandardTable.Column(var1);
   }

   @Override
   public Set<R> rowKeySet() {
      return this.rowMap().keySet();
   }

   @Override
   public Set<C> columnKeySet() {
      Set var1 = this.columnKeySet;
      return var1 == null ? (this.columnKeySet = new StandardTable.ColumnKeySet()) : var1;
   }

   Iterator<C> createColumnKeyIterator() {
      return new StandardTable.ColumnKeyIterator();
   }

   @Override
   public Collection<V> values() {
      return super.values();
   }

   @Override
   public Map<R, Map<C, V>> rowMap() {
      Map var1 = this.rowMap;
      return var1 == null ? (this.rowMap = this.createRowMap()) : var1;
   }

   Map<R, Map<C, V>> createRowMap() {
      return new StandardTable.RowMap();
   }

   @Override
   public Map<C, Map<R, V>> columnMap() {
      StandardTable.ColumnMap var1 = this.columnMap;
      return var1 == null ? (this.columnMap = new StandardTable.ColumnMap()) : var1;
   }

   private class CellIterator implements Iterator<Table.Cell<R, C, V>> {
      final Iterator<Entry<R, Map<C, V>>> rowIterator = StandardTable.this.backingMap.entrySet().iterator();
      Entry<R, Map<C, V>> rowEntry;
      Iterator<Entry<C, V>> columnIterator = Iterators.emptyModifiableIterator();

      private CellIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.rowIterator.hasNext() || this.columnIterator.hasNext();
      }

      public Table.Cell<R, C, V> next() {
         if (!this.columnIterator.hasNext()) {
            this.rowEntry = this.rowIterator.next();
            this.columnIterator = this.rowEntry.getValue().entrySet().iterator();
         }

         Objects.requireNonNull(this.rowEntry);
         Entry var1 = this.columnIterator.next();
         return Tables.immutableCell(this.rowEntry.getKey(), (C)var1.getKey(), (V)var1.getValue());
      }

      @Override
      public void remove() {
         this.columnIterator.remove();
         if (Objects.requireNonNull(this.rowEntry).getValue().isEmpty()) {
            this.rowIterator.remove();
            this.rowEntry = null;
         }
      }
   }

   private class Column extends Maps.ViewCachingAbstractMap<R, V> {
      final C columnKey;

      Column(C var2) {
         this.columnKey = Preconditions.checkNotNull((C)var2);
      }

      @Override
      public V put(R var1, V var2) {
         return StandardTable.this.put((R)var1, this.columnKey, (V)var2);
      }

      @Override
      public V get(Object var1) {
         return StandardTable.this.get(var1, this.columnKey);
      }

      @Override
      public boolean containsKey(Object var1) {
         return StandardTable.this.contains(var1, this.columnKey);
      }

      @Override
      public V remove(Object var1) {
         return StandardTable.this.remove(var1, this.columnKey);
      }

      @CanIgnoreReturnValue
      boolean removeFromColumnIf(Predicate<? super Entry<R, V>> var1) {
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.entrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            Map var5 = (Map)var4.getValue();
            Object var6 = var5.get(this.columnKey);
            if (var6 != null && var1.apply(Maps.immutableEntry(var4.getKey(), var6))) {
               var5.remove(this.columnKey);
               var2 = true;
               if (var5.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      @Override
      Set<Entry<R, V>> createEntrySet() {
         return new StandardTable.Column.EntrySet();
      }

      @Override
      Set<R> createKeySet() {
         return new StandardTable.Column.KeySet();
      }

      @Override
      Collection<V> createValues() {
         return new StandardTable.Column.Values();
      }

      private class EntrySet extends Sets.ImprovedAbstractSet<Entry<R, V>> {
         private EntrySet() {
         }

         @Override
         public Iterator<Entry<R, V>> iterator() {
            return Column.this.new EntrySetIterator();
         }

         @Override
         public int size() {
            int var1 = 0;

            for (Map var3 : StandardTable.this.backingMap.values()) {
               if (var3.containsKey(Column.this.columnKey)) {
                  var1++;
               }
            }

            return var1;
         }

         @Override
         public boolean isEmpty() {
            return !StandardTable.this.containsColumn(Column.this.columnKey);
         }

         @Override
         public void clear() {
            Column.this.removeFromColumnIf(Predicates.alwaysTrue());
         }

         @Override
         public boolean contains(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               return StandardTable.this.containsMapping(var2.getKey(), Column.this.columnKey, var2.getValue());
            } else {
               return false;
            }
         }

         @Override
         public boolean remove(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               return StandardTable.this.removeMapping(var2.getKey(), Column.this.columnKey, var2.getValue());
            } else {
               return false;
            }
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Predicates.not(Predicates.in(var1)));
         }
      }

      private class EntrySetIterator extends AbstractIterator<Entry<R, V>> {
         final Iterator<Entry<R, Map<C, V>>> iterator = StandardTable.this.backingMap.entrySet().iterator();

         private EntrySetIterator() {
         }

         protected Entry<R, V> computeNext() {
            while (this.iterator.hasNext()) {
               final Entry var1 = this.iterator.next();
               if (((Map)var1.getValue()).containsKey(Column.this.columnKey)) {
                  class 1EntryImpl extends AbstractMapEntry<R, V> {
                     @Override
                     public R getKey() {
                        return (R)var1.getKey();
                     }

                     @Override
                     public V getValue() {
                        return (V)((Map)var1.getValue()).get(Column.this.columnKey);
                     }

                     @Override
                     public V setValue(V var1x) {
                        return NullnessCasts.uncheckedCastNullableTToT(
                           (V)((Map)var1.getValue()).put(Column.this.columnKey, Preconditions.checkNotNull((V)var1x))
                        );
                     }
                  }

                  return new 1EntryImpl();
               }
            }

            return this.endOfData();
         }
      }

      private class KeySet extends Maps.KeySet<R, V> {
         KeySet() {
            super(Column.this);
         }

         @Override
         public boolean contains(Object var1) {
            return StandardTable.this.contains(var1, Column.this.columnKey);
         }

         @Override
         public boolean remove(Object var1) {
            return StandardTable.this.remove(var1, Column.this.columnKey) != null;
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(var1))));
         }
      }

      private class Values extends Maps.Values<R, V> {
         Values() {
            super(Column.this);
         }

         @Override
         public boolean remove(Object var1) {
            return var1 != null && Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.equalTo((V)var1)));
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.in(var1)));
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(var1))));
         }
      }
   }

   private class ColumnKeyIterator extends AbstractIterator<C> {
      final Map<C, V> seen = (Map<C, V>)StandardTable.this.factory.get();
      final Iterator<Map<C, V>> mapIterator = StandardTable.this.backingMap.values().iterator();
      Iterator<Entry<C, V>> entryIterator = Iterators.emptyIterator();

      private ColumnKeyIterator() {
      }

      @Override
      protected C computeNext() {
         while (true) {
            if (this.entryIterator.hasNext()) {
               Entry var1 = this.entryIterator.next();
               if (!this.seen.containsKey(var1.getKey())) {
                  this.seen.put((C)var1.getKey(), (V)var1.getValue());
                  return (C)var1.getKey();
               }
            } else {
               if (!this.mapIterator.hasNext()) {
                  return (C)this.endOfData();
               }

               this.entryIterator = this.mapIterator.next().entrySet().iterator();
            }
         }
      }
   }

   private class ColumnKeySet extends StandardTable<R, C, V>.TableSet<C> {
      private ColumnKeySet() {
      }

      @Override
      public Iterator<C> iterator() {
         return StandardTable.this.createColumnKeyIterator();
      }

      @Override
      public int size() {
         return Iterators.size(this.iterator());
      }

      @Override
      public boolean remove(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            boolean var2 = false;
            Iterator var3 = StandardTable.this.backingMap.values().iterator();

            while (var3.hasNext()) {
               Map var4 = (Map)var3.next();
               if (var4.keySet().remove(var1)) {
                  var2 = true;
                  if (var4.isEmpty()) {
                     var3.remove();
                  }
               }
            }

            return var2;
         }
      }

      @Override
      public boolean removeAll(Collection<?> var1) {
         Preconditions.checkNotNull(var1);
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.values().iterator();

         while (var3.hasNext()) {
            Map var4 = (Map)var3.next();
            if (Iterators.removeAll(var4.keySet().iterator(), var1)) {
               var2 = true;
               if (var4.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         Preconditions.checkNotNull(var1);
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.values().iterator();

         while (var3.hasNext()) {
            Map var4 = (Map)var3.next();
            if (var4.keySet().retainAll(var1)) {
               var2 = true;
               if (var4.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      @Override
      public boolean contains(Object var1) {
         return StandardTable.this.containsColumn(var1);
      }
   }

   private class ColumnMap extends Maps.ViewCachingAbstractMap<C, Map<R, V>> {
      private ColumnMap() {
      }

      public Map<R, V> get(Object var1) {
         return StandardTable.this.containsColumn(var1) ? StandardTable.this.column(Objects.requireNonNull((C)var1)) : null;
      }

      @Override
      public boolean containsKey(Object var1) {
         return StandardTable.this.containsColumn(var1);
      }

      public Map<R, V> remove(Object var1) {
         return StandardTable.this.containsColumn(var1) ? StandardTable.this.removeColumn(var1) : null;
      }

      @Override
      public Set<Entry<C, Map<R, V>>> createEntrySet() {
         return new StandardTable.ColumnMap.ColumnMapEntrySet();
      }

      @Override
      public Set<C> keySet() {
         return StandardTable.this.columnKeySet();
      }

      @Override
      Collection<Map<R, V>> createValues() {
         return new StandardTable.ColumnMap.ColumnMapValues();
      }

      class ColumnMapEntrySet extends StandardTable<R, C, V>.TableSet<Entry<C, Map<R, V>>> {
         @Override
         public Iterator<Entry<C, Map<R, V>>> iterator() {
            return Maps.asMapEntryIterator(StandardTable.this.columnKeySet(), new Function<C, Map<R, V>>() {
               public Map<R, V> apply(C var1) {
                  return StandardTable.this.column((C)var1);
               }
            });
         }

         @Override
         public int size() {
            return StandardTable.this.columnKeySet().size();
         }

         @Override
         public boolean contains(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               if (StandardTable.this.containsColumn(var2.getKey())) {
                  return Objects.requireNonNull(ColumnMap.this.get(var2.getKey())).equals(var2.getValue());
               }
            }

            return false;
         }

         @Override
         public boolean remove(Object var1) {
            if (this.contains(var1) && var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               StandardTable.this.removeColumn(var2.getKey());
               return true;
            } else {
               return false;
            }
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            return Sets.removeAllImpl(this, var1.iterator());
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;

            for (Object var4 : Lists.newArrayList(StandardTable.this.columnKeySet().iterator())) {
               if (!var1.contains(Maps.immutableEntry(var4, StandardTable.this.column((C)var4)))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }
      }

      private class ColumnMapValues extends Maps.Values<C, Map<R, V>> {
         ColumnMapValues() {
            super(ColumnMap.this);
         }

         @Override
         public boolean remove(Object var1) {
            for (Entry var3 : ColumnMap.this.entrySet()) {
               if (((Map)var3.getValue()).equals(var1)) {
                  StandardTable.this.removeColumn(var3.getKey());
                  return true;
               }
            }

            return false;
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;

            for (Object var4 : Lists.newArrayList(StandardTable.this.columnKeySet().iterator())) {
               if (var1.contains(StandardTable.this.column((C)var4))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;

            for (Object var4 : Lists.newArrayList(StandardTable.this.columnKeySet().iterator())) {
               if (!var1.contains(StandardTable.this.column((C)var4))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }
      }
   }

   class Row extends Maps.IteratorBasedAbstractMap<C, V> {
      final R rowKey;
      Map<C, V> backingRowMap;

      Row(R var2) {
         this.rowKey = Preconditions.checkNotNull((R)var2);
      }

      final void updateBackingRowMapField() {
         if (this.backingRowMap == null || this.backingRowMap.isEmpty() && StandardTable.this.backingMap.containsKey(this.rowKey)) {
            this.backingRowMap = this.computeBackingRowMap();
         }
      }

      Map<C, V> computeBackingRowMap() {
         return StandardTable.this.backingMap.get(this.rowKey);
      }

      void maintainEmptyInvariant() {
         this.updateBackingRowMapField();
         if (this.backingRowMap != null && this.backingRowMap.isEmpty()) {
            StandardTable.this.backingMap.remove(this.rowKey);
            this.backingRowMap = null;
         }
      }

      @Override
      public boolean containsKey(Object var1) {
         this.updateBackingRowMapField();
         return var1 != null && this.backingRowMap != null && Maps.safeContainsKey(this.backingRowMap, var1);
      }

      @Override
      public V get(Object var1) {
         this.updateBackingRowMapField();
         return var1 != null && this.backingRowMap != null ? Maps.safeGet(this.backingRowMap, var1) : null;
      }

      @Override
      public V put(C var1, V var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         return this.backingRowMap != null && !this.backingRowMap.isEmpty()
            ? this.backingRowMap.put((C)var1, (V)var2)
            : StandardTable.this.put(this.rowKey, (C)var1, (V)var2);
      }

      @Override
      public V remove(Object var1) {
         this.updateBackingRowMapField();
         if (this.backingRowMap == null) {
            return null;
         } else {
            Object var2 = Maps.safeRemove(this.backingRowMap, var1);
            this.maintainEmptyInvariant();
            return (V)var2;
         }
      }

      @Override
      public void clear() {
         this.updateBackingRowMapField();
         if (this.backingRowMap != null) {
            this.backingRowMap.clear();
         }

         this.maintainEmptyInvariant();
      }

      @Override
      public int size() {
         this.updateBackingRowMapField();
         return this.backingRowMap == null ? 0 : this.backingRowMap.size();
      }

      @Override
      Iterator<Entry<C, V>> entryIterator() {
         this.updateBackingRowMapField();
         if (this.backingRowMap == null) {
            return Iterators.emptyModifiableIterator();
         } else {
            final Iterator var1 = this.backingRowMap.entrySet().iterator();
            return new Iterator<Entry<C, V>>() {
               @Override
               public boolean hasNext() {
                  return var1.hasNext();
               }

               public Entry<C, V> next() {
                  return Row.this.wrapEntry((Entry<C, V>)var1.next());
               }

               @Override
               public void remove() {
                  var1.remove();
                  Row.this.maintainEmptyInvariant();
               }
            };
         }
      }

      @Override
      Spliterator<Entry<C, V>> entrySpliterator() {
         this.updateBackingRowMapField();
         return this.backingRowMap == null
            ? Spliterators.emptySpliterator()
            : CollectSpliterators.map(this.backingRowMap.entrySet().spliterator(), this::wrapEntry);
      }

      Entry<C, V> wrapEntry(final Entry<C, V> var1) {
         return new ForwardingMapEntry<C, V>(this) {
            @Override
            protected Entry<C, V> delegate() {
               return var1;
            }

            @Override
            public V setValue(V var1x) {
               return (V)super.setValue(Preconditions.checkNotNull((V)var1x));
            }

            @Override
            public boolean equals(Object var1x) {
               return this.standardEquals(var1x);
            }
         };
      }
   }

   class RowMap extends Maps.ViewCachingAbstractMap<R, Map<C, V>> {
      @Override
      public boolean containsKey(Object var1) {
         return StandardTable.this.containsRow(var1);
      }

      public Map<C, V> get(Object var1) {
         return StandardTable.this.containsRow(var1) ? StandardTable.this.row(Objects.requireNonNull((R)var1)) : null;
      }

      public Map<C, V> remove(Object var1) {
         return var1 == null ? null : StandardTable.this.backingMap.remove(var1);
      }

      @Override
      protected Set<Entry<R, Map<C, V>>> createEntrySet() {
         return new StandardTable.RowMap.EntrySet();
      }

      class EntrySet extends StandardTable<R, C, V>.TableSet<Entry<R, Map<C, V>>> {
         @Override
         public Iterator<Entry<R, Map<C, V>>> iterator() {
            return Maps.asMapEntryIterator(StandardTable.this.backingMap.keySet(), new Function<R, Map<C, V>>() {
               public Map<C, V> apply(R var1) {
                  return StandardTable.this.row((R)var1);
               }
            });
         }

         @Override
         public int size() {
            return StandardTable.this.backingMap.size();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return var2.getKey() != null && var2.getValue() instanceof Map && Collections2.safeContains(StandardTable.this.backingMap.entrySet(), var2);
            }
         }

         @Override
         public boolean remove(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return var2.getKey() != null && var2.getValue() instanceof Map && StandardTable.this.backingMap.entrySet().remove(var2);
            }
         }
      }
   }

   private abstract class TableSet<T> extends Sets.ImprovedAbstractSet<T> {
      private TableSet() {
      }

      @Override
      public boolean isEmpty() {
         return StandardTable.this.backingMap.isEmpty();
      }

      @Override
      public void clear() {
         StandardTable.this.backingMap.clear();
      }
   }
}
