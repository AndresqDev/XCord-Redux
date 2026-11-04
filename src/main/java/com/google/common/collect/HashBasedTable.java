package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
public class HashBasedTable<R, C, V> extends StandardTable<R, C, V> {
   private static final long serialVersionUID = 0L;

   public static <R, C, V> HashBasedTable<R, C, V> create() {
      return new HashBasedTable<>(new LinkedHashMap<>(), new HashBasedTable.Factory<>(0));
   }

   public static <R, C, V> HashBasedTable<R, C, V> create(int var0, int var1) {
      CollectPreconditions.checkNonnegative(var1, "expectedCellsPerRow");
      LinkedHashMap var2 = Maps.newLinkedHashMapWithExpectedSize(var0);
      return new HashBasedTable<>(var2, new HashBasedTable.Factory<>(var1));
   }

   public static <R, C, V> HashBasedTable<R, C, V> create(Table<? extends R, ? extends C, ? extends V> var0) {
      HashBasedTable var1 = create();
      var1.putAll(var0);
      return var1;
   }

   HashBasedTable(Map<R, Map<C, V>> var1, HashBasedTable.Factory<C, V> var2) {
      super(var1, var2);
   }

   private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable {
      final int expectedSize;
      private static final long serialVersionUID = 0L;

      Factory(int var1) {
         this.expectedSize = var1;
      }

      public Map<C, V> get() {
         return Maps.newLinkedHashMapWithExpectedSize(this.expectedSize);
      }
   }
}
