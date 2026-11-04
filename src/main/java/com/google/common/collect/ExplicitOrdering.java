package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class ExplicitOrdering<T> extends Ordering<T> implements Serializable {
   final ImmutableMap<T, Integer> rankMap;
   private static final long serialVersionUID = 0L;

   ExplicitOrdering(List<T> var1) {
      this(Maps.indexMap(var1));
   }

   ExplicitOrdering(ImmutableMap<T, Integer> var1) {
      this.rankMap = var1;
   }

   @Override
   public int compare(T var1, T var2) {
      return this.rank((T)var1) - this.rank((T)var2);
   }

   private int rank(T var1) {
      Integer var2 = this.rankMap.get(var1);
      if (var2 == null) {
         throw new Ordering.IncomparableValueException(var1);
      } else {
         return var2;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof ExplicitOrdering) {
         ExplicitOrdering var2 = (ExplicitOrdering)var1;
         return this.rankMap.equals(var2.rankMap);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.rankMap.hashCode();
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.rankMap.keySet());
      return new StringBuilder(19 + String.valueOf(var1).length()).append("Ordering.explicit(").append(var1).append(")").toString();
   }
}
