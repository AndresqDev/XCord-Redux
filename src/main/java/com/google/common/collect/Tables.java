package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Tables {
   private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER = new Function<Map<Object, Object>, Map<Object, Object>>() {
      public Map<Object, Object> apply(Map<Object, Object> var1) {
         return Collections.unmodifiableMap(var1);
      }
   };

   private Tables() {
   }

   @Beta
   public static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(
      java.util.function.Function<? super T, ? extends R> var0,
      java.util.function.Function<? super T, ? extends C> var1,
      java.util.function.Function<? super T, ? extends V> var2,
      Supplier<I> var3
   ) {
      return TableCollectors.toTable(var0, var1, var2, var3);
   }

   public static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(
      java.util.function.Function<? super T, ? extends R> var0,
      java.util.function.Function<? super T, ? extends C> var1,
      java.util.function.Function<? super T, ? extends V> var2,
      BinaryOperator<V> var3,
      Supplier<I> var4
   ) {
      return TableCollectors.toTable(var0, var1, var2, var3, var4);
   }

   public static <R, C, V> Table.Cell<R, C, V> immutableCell(@ParametricNullness R var0, @ParametricNullness C var1, @ParametricNullness V var2) {
      return new Tables.ImmutableCell<>((R)var0, (C)var1, (V)var2);
   }

   public static <R, C, V> Table<C, R, V> transpose(Table<R, C, V> var0) {
      return (Table<C, R, V>)(var0 instanceof Tables.TransposeTable ? ((Tables.TransposeTable)var0).original : new Tables.TransposeTable<>(var0));
   }

   @Beta
   public static <R, C, V> Table<R, C, V> newCustomTable(Map<R, Map<C, V>> var0, com.google.common.base.Supplier<? extends Map<C, V>> var1) {
      Preconditions.checkArgument(var0.isEmpty());
      Preconditions.checkNotNull(var1);
      return new StandardTable<>(var0, var1);
   }

   @Beta
   public static <R, C, V1, V2> Table<R, C, V2> transformValues(Table<R, C, V1> var0, Function<? super V1, V2> var1) {
      return new Tables.TransformedTable<>(var0, var1);
   }

   public static <R, C, V> Table<R, C, V> unmodifiableTable(Table<? extends R, ? extends C, ? extends V> var0) {
      return new Tables.UnmodifiableTable<>(var0);
   }

   @Beta
   public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(RowSortedTable<R, ? extends C, ? extends V> var0) {
      return new Tables.UnmodifiableRowSortedMap<>(var0);
   }

   private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper() {
      return (Function<Map<K, V>, Map<K, V>>)UNMODIFIABLE_WRAPPER;
   }

   public static <R, C, V> Table<R, C, V> synchronizedTable(Table<R, C, V> var0) {
      return Synchronized.table(var0, null);
   }

   static boolean equalsImpl(Table<?, ?, ?> var0, Object var1) {
      if (var1 == var0) {
         return true;
      } else if (var1 instanceof Table) {
         Table var2 = (Table)var1;
         return var0.cellSet().equals(var2.cellSet());
      } else {
         return false;
      }
   }

   abstract static class AbstractCell<R, C, V> implements Table.Cell<R, C, V> {
      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            return Objects.equal(this.getRowKey(), var2.getRowKey())
               && Objects.equal(this.getColumnKey(), var2.getColumnKey())
               && Objects.equal(this.getValue(), var2.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.getRowKey(), this.getColumnKey(), this.getValue());
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.getRowKey());
         String var2 = String.valueOf(this.getColumnKey());
         String var3 = String.valueOf(this.getValue());
         return new StringBuilder(4 + String.valueOf(var1).length() + String.valueOf(var2).length() + String.valueOf(var3).length())
            .append("(")
            .append(var1)
            .append(",")
            .append(var2)
            .append(")=")
            .append(var3)
            .toString();
      }
   }

   static final class ImmutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> implements Serializable {
      @ParametricNullness
      private final R rowKey;
      @ParametricNullness
      private final C columnKey;
      @ParametricNullness
      private final V value;
      private static final long serialVersionUID = 0L;

      ImmutableCell(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3) {
         this.rowKey = (R)var1;
         this.columnKey = (C)var2;
         this.value = (V)var3;
      }

      @ParametricNullness
      @Override
      public R getRowKey() {
         return this.rowKey;
      }

      @ParametricNullness
      @Override
      public C getColumnKey() {
         return this.columnKey;
      }

      @ParametricNullness
      @Override
      public V getValue() {
         return this.value;
      }
   }

   private static class TransformedTable<R, C, V1, V2> extends AbstractTable<R, C, V2> {
      final Table<R, C, V1> fromTable;
      final Function<? super V1, V2> function;

      TransformedTable(Table<R, C, V1> var1, Function<? super V1, V2> var2) {
         this.fromTable = Preconditions.checkNotNull(var1);
         this.function = Preconditions.checkNotNull(var2);
      }

      @Override
      public boolean contains(Object var1, Object var2) {
         return this.fromTable.contains(var1, var2);
      }

      @Override
      public V2 get(Object var1, Object var2) {
         return this.contains(var1, var2) ? this.function.apply(NullnessCasts.uncheckedCastNullableTToT(this.fromTable.get(var1, var2))) : null;
      }

      @Override
      public int size() {
         return this.fromTable.size();
      }

      @Override
      public void clear() {
         this.fromTable.clear();
      }

      @Override
      public V2 put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V2 var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putAll(Table<? extends R, ? extends C, ? extends V2> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V2 remove(Object var1, Object var2) {
         return this.contains(var1, var2) ? this.function.apply(NullnessCasts.uncheckedCastNullableTToT(this.fromTable.remove(var1, var2))) : null;
      }

      @Override
      public Map<C, V2> row(@ParametricNullness R var1) {
         return Maps.transformValues(this.fromTable.row((R)var1), this.function);
      }

      @Override
      public Map<R, V2> column(@ParametricNullness C var1) {
         return Maps.transformValues(this.fromTable.column((C)var1), this.function);
      }

      Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>> cellFunction() {
         return new Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>>() {
            public Table.Cell<R, C, V2> apply(Table.Cell<R, C, V1> var1) {
               return Tables.immutableCell((R)var1.getRowKey(), (C)var1.getColumnKey(), TransformedTable.this.function.apply((V1)var1.getValue()));
            }
         };
      }

      @Override
      Iterator<Table.Cell<R, C, V2>> cellIterator() {
         return Iterators.transform(this.fromTable.cellSet().iterator(), this.cellFunction());
      }

      @Override
      Spliterator<Table.Cell<R, C, V2>> cellSpliterator() {
         return CollectSpliterators.map(this.fromTable.cellSet().spliterator(), this.cellFunction());
      }

      @Override
      public Set<R> rowKeySet() {
         return this.fromTable.rowKeySet();
      }

      @Override
      public Set<C> columnKeySet() {
         return this.fromTable.columnKeySet();
      }

      @Override
      Collection<V2> createValues() {
         return Collections2.transform(this.fromTable.values(), this.function);
      }

      @Override
      public Map<R, Map<C, V2>> rowMap() {
         Function var1 = new Function<Map<C, V1>, Map<C, V2>>() {
            public Map<C, V2> apply(Map<C, V1> var1) {
               return Maps.transformValues(var1, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.rowMap(), var1);
      }

      @Override
      public Map<C, Map<R, V2>> columnMap() {
         Function var1 = new Function<Map<R, V1>, Map<R, V2>>() {
            public Map<R, V2> apply(Map<R, V1> var1) {
               return Maps.transformValues(var1, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.columnMap(), var1);
      }
   }

   private static class TransposeTable<C, R, V> extends AbstractTable<C, R, V> {
      final Table<R, C, V> original;
      private static final Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>> TRANSPOSE_CELL = new Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>>() {
         public Table.Cell<?, ?, ?> apply(Table.Cell<?, ?, ?> var1) {
            return Tables.immutableCell(var1.getColumnKey(), var1.getRowKey(), var1.getValue());
         }
      };

      TransposeTable(Table<R, C, V> var1) {
         this.original = Preconditions.checkNotNull(var1);
      }

      @Override
      public void clear() {
         this.original.clear();
      }

      @Override
      public Map<C, V> column(@ParametricNullness R var1) {
         return this.original.row((R)var1);
      }

      @Override
      public Set<R> columnKeySet() {
         return this.original.rowKeySet();
      }

      @Override
      public Map<R, Map<C, V>> columnMap() {
         return this.original.rowMap();
      }

      @Override
      public boolean contains(Object var1, Object var2) {
         return this.original.contains(var2, var1);
      }

      @Override
      public boolean containsColumn(Object var1) {
         return this.original.containsRow(var1);
      }

      @Override
      public boolean containsRow(Object var1) {
         return this.original.containsColumn(var1);
      }

      @Override
      public boolean containsValue(Object var1) {
         return this.original.containsValue(var1);
      }

      @Override
      public V get(Object var1, Object var2) {
         return this.original.get(var2, var1);
      }

      @Override
      public V put(@ParametricNullness C var1, @ParametricNullness R var2, @ParametricNullness V var3) {
         return this.original.put((R)var2, (C)var1, (V)var3);
      }

      @Override
      public void putAll(Table<? extends C, ? extends R, ? extends V> var1) {
         this.original.putAll(Tables.transpose(var1));
      }

      @Override
      public V remove(Object var1, Object var2) {
         return this.original.remove(var2, var1);
      }

      @Override
      public Map<R, V> row(@ParametricNullness C var1) {
         return this.original.column((C)var1);
      }

      @Override
      public Set<C> rowKeySet() {
         return this.original.columnKeySet();
      }

      @Override
      public Map<C, Map<R, V>> rowMap() {
         return this.original.columnMap();
      }

      @Override
      public int size() {
         return this.original.size();
      }

      @Override
      public Collection<V> values() {
         return this.original.values();
      }

      @Override
      Iterator<Table.Cell<C, R, V>> cellIterator() {
         return Iterators.transform(this.original.cellSet().iterator(), TRANSPOSE_CELL);
      }

      @Override
      Spliterator<Table.Cell<C, R, V>> cellSpliterator() {
         return CollectSpliterators.map(this.original.cellSet().spliterator(), TRANSPOSE_CELL);
      }
   }

   static final class UnmodifiableRowSortedMap<R, C, V> extends Tables.UnmodifiableTable<R, C, V> implements RowSortedTable<R, C, V> {
      private static final long serialVersionUID = 0L;

      public UnmodifiableRowSortedMap(RowSortedTable<R, ? extends C, ? extends V> var1) {
         super(var1);
      }

      protected RowSortedTable<R, C, V> delegate() {
         return (RowSortedTable<R, C, V>)super.delegate();
      }

      @Override
      public SortedMap<R, Map<C, V>> rowMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableSortedMap(Maps.transformValues(this.delegate().rowMap(), var1));
      }

      @Override
      public SortedSet<R> rowKeySet() {
         return Collections.unmodifiableSortedSet(this.delegate().rowKeySet());
      }
   }

   private static class UnmodifiableTable<R, C, V> extends ForwardingTable<R, C, V> implements Serializable {
      final Table<? extends R, ? extends C, ? extends V> delegate;
      private static final long serialVersionUID = 0L;

      UnmodifiableTable(Table<? extends R, ? extends C, ? extends V> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
      }

      @Override
      protected Table<R, C, V> delegate() {
         return (Table<R, C, V>)this.delegate;
      }

      @Override
      public Set<Table.Cell<R, C, V>> cellSet() {
         return Collections.unmodifiableSet(super.cellSet());
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Map<R, V> column(@ParametricNullness C var1) {
         return Collections.unmodifiableMap(super.column((C)var1));
      }

      @Override
      public Set<C> columnKeySet() {
         return Collections.unmodifiableSet(super.columnKeySet());
      }

      @Override
      public Map<C, Map<R, V>> columnMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), var1));
      }

      @Override
      public V put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public V remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Map<C, V> row(@ParametricNullness R var1) {
         return Collections.unmodifiableMap(super.row((R)var1));
      }

      @Override
      public Set<R> rowKeySet() {
         return Collections.unmodifiableSet(super.rowKeySet());
      }

      @Override
      public Map<R, Map<C, V>> rowMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), var1));
      }

      @Override
      public Collection<V> values() {
         return Collections.unmodifiableCollection(super.values());
      }
   }
}
