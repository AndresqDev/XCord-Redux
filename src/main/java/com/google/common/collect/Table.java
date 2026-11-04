package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@DoNotMock("Use ImmutableTable, HashBasedTable, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Table<R, C, V> {
   boolean contains(@CompatibleWith("R") Object var1, @CompatibleWith("C") Object var2);

   boolean containsRow(@CompatibleWith("R") Object var1);

   boolean containsColumn(@CompatibleWith("C") Object var1);

   boolean containsValue(@CompatibleWith("V") Object var1);

   V get(@CompatibleWith("R") Object var1, @CompatibleWith("C") Object var2);

   boolean isEmpty();

   int size();

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();

   void clear();

   @CanIgnoreReturnValue
   V put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3);

   void putAll(Table<? extends R, ? extends C, ? extends V> var1);

   @CanIgnoreReturnValue
   V remove(@CompatibleWith("R") Object var1, @CompatibleWith("C") Object var2);

   Map<C, V> row(@ParametricNullness R var1);

   Map<R, V> column(@ParametricNullness C var1);

   Set<Table.Cell<R, C, V>> cellSet();

   Set<R> rowKeySet();

   Set<C> columnKeySet();

   Collection<V> values();

   Map<R, Map<C, V>> rowMap();

   Map<C, Map<R, V>> columnMap();

   public interface Cell<R, C, V> {
      @ParametricNullness
      R getRowKey();

      @ParametricNullness
      C getColumnKey();

      @ParametricNullness
      V getValue();

      @Override
      boolean equals(Object var1);

      @Override
      int hashCode();
   }
}
