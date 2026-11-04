package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {
   final R singleRowKey;
   final C singleColumnKey;
   final V singleValue;

   SingletonImmutableTable(R var1, C var2, V var3) {
      this.singleRowKey = Preconditions.checkNotNull((R)var1);
      this.singleColumnKey = Preconditions.checkNotNull((C)var2);
      this.singleValue = Preconditions.checkNotNull((V)var3);
   }

   SingletonImmutableTable(Table.Cell<R, C, V> var1) {
      this((R)var1.getRowKey(), (C)var1.getColumnKey(), (V)var1.getValue());
   }

   @Override
   public ImmutableMap<R, V> column(C var1) {
      Preconditions.checkNotNull(var1);
      return this.containsColumn(var1) ? ImmutableMap.of(this.singleRowKey, this.singleValue) : ImmutableMap.of();
   }

   @Override
   public ImmutableMap<C, Map<R, V>> columnMap() {
      return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
   }

   @Override
   public ImmutableMap<R, Map<C, V>> rowMap() {
      return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
      return ImmutableSet.of(cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
   }

   @Override
   ImmutableCollection<V> createValues() {
      return ImmutableSet.of(this.singleValue);
   }

   @Override
   ImmutableTable.SerializedForm createSerializedForm() {
      return ImmutableTable.SerializedForm.create(this, new int[]{0}, new int[]{0});
   }
}
