package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class TableCollectors {
   static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(
      Function<? super T, ? extends R> var0, Function<? super T, ? extends C> var1, Function<? super T, ? extends V> var2
   ) {
      Preconditions.checkNotNull(var0, "rowFunction");
      Preconditions.checkNotNull(var1, "columnFunction");
      Preconditions.checkNotNull(var2, "valueFunction");
      return Collector.of(
         ImmutableTable.Builder::new,
         (var3, var4) -> var3.put((R)var0.apply(var4), (C)var1.apply(var4), (V)var2.apply(var4)),
         ImmutableTable.Builder::combine,
         ImmutableTable.Builder::build
      );
   }

   static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(
      Function<? super T, ? extends R> var0, Function<? super T, ? extends C> var1, Function<? super T, ? extends V> var2, BinaryOperator<V> var3
   ) {
      Preconditions.checkNotNull(var0, "rowFunction");
      Preconditions.checkNotNull(var1, "columnFunction");
      Preconditions.checkNotNull(var2, "valueFunction");
      Preconditions.checkNotNull(var3, "mergeFunction");
      return Collector.of(
         () -> new TableCollectors.ImmutableTableCollectorState(),
         (var4, var5) -> var4.put(var0.apply(var5), var1.apply(var5), var2.apply(var5), var3),
         (var1x, var2x) -> var1x.combine(var2x, var3),
         var0x -> var0x.toTable()
      );
   }

   static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(
      Function<? super T, ? extends R> var0, Function<? super T, ? extends C> var1, Function<? super T, ? extends V> var2, Supplier<I> var3
   ) {
      return toTable(
         var0,
         var1,
         var2,
         (var0x, var1x) -> {
            String var2x = String.valueOf(var0x);
            String var3x = String.valueOf(var1x);
            throw new IllegalStateException(
               new StringBuilder(24 + String.valueOf(var2x).length() + String.valueOf(var3x).length())
                  .append("Conflicting values ")
                  .append(var2x)
                  .append(" and ")
                  .append(var3x)
                  .toString()
            );
         },
         var3
      );
   }

   static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(
      Function<? super T, ? extends R> var0,
      Function<? super T, ? extends C> var1,
      Function<? super T, ? extends V> var2,
      BinaryOperator<V> var3,
      Supplier<I> var4
   ) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      Preconditions.checkNotNull(var4);
      return (Collector<T, ?, I>)Collector.of(
         var4, (var4x, var5) -> mergeTables(var4x, var0.apply(var5), var1.apply(var5), var2.apply(var5), var3), (var1x, var2x) -> {
            for (Table.Cell var4x : var2x.cellSet()) {
               mergeTables(var1x, var4x.getRowKey(), var4x.getColumnKey(), var4x.getValue(), var3);
            }

            return var1x;
         }
      );
   }

   private static <R, C, V> void mergeTables(
      Table<R, C, V> var0, @ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3, BinaryOperator<V> var4
   ) {
      Preconditions.checkNotNull(var3);
      Object var5 = var0.get(var1, var2);
      if (var5 == null) {
         var0.put(var1, var2, var3);
      } else {
         Object var6 = var4.apply(var5, var3);
         if (var6 == null) {
            var0.remove(var1, var2);
         } else {
            var0.put(var1, var2, var6);
         }
      }
   }

   private TableCollectors() {
   }

   private static final class ImmutableTableCollectorState<R, C, V> {
      final List<TableCollectors.MutableCell<R, C, V>> insertionOrder = new ArrayList<>();
      final Table<R, C, TableCollectors.MutableCell<R, C, V>> table = HashBasedTable.create();

      private ImmutableTableCollectorState() {
      }

      void put(R var1, C var2, V var3, BinaryOperator<V> var4) {
         TableCollectors.MutableCell var5 = this.table.get(var1, var2);
         if (var5 == null) {
            TableCollectors.MutableCell var6 = new TableCollectors.MutableCell<>(var1, var2, var3);
            this.insertionOrder.add(var6);
            this.table.put((R)var1, (C)var2, var6);
         } else {
            var5.merge(var3, var4);
         }
      }

      TableCollectors.ImmutableTableCollectorState<R, C, V> combine(TableCollectors.ImmutableTableCollectorState<R, C, V> var1, BinaryOperator<V> var2) {
         for (TableCollectors.MutableCell var4 : var1.insertionOrder) {
            this.put((R)var4.getRowKey(), (C)var4.getColumnKey(), (V)var4.getValue(), var2);
         }

         return this;
      }

      ImmutableTable<R, C, V> toTable() {
         return ImmutableTable.copyOf(this.insertionOrder);
      }
   }

   private static final class MutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> {
      private final R row;
      private final C column;
      private V value;

      MutableCell(R var1, C var2, V var3) {
         this.row = Preconditions.checkNotNull((R)var1, "row");
         this.column = Preconditions.checkNotNull((C)var2, "column");
         this.value = Preconditions.checkNotNull((V)var3, "value");
      }

      @Override
      public R getRowKey() {
         return this.row;
      }

      @Override
      public C getColumnKey() {
         return this.column;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      void merge(V var1, BinaryOperator<V> var2) {
         Preconditions.checkNotNull(var1, "value");
         this.value = Preconditions.checkNotNull(var2.apply(this.value, (V)var1), "mergeFunction.apply");
      }
   }
}
