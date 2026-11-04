package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {
   final Comparator<T> comparator;
   private static final long serialVersionUID = 0L;

   ComparatorOrdering(Comparator<T> var1) {
      this.comparator = Preconditions.checkNotNull(var1);
   }

   @Override
   public int compare(@ParametricNullness T var1, @ParametricNullness T var2) {
      return this.comparator.compare((T)var1, (T)var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof ComparatorOrdering) {
         ComparatorOrdering var2 = (ComparatorOrdering)var1;
         return this.comparator.equals(var2.comparator);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.comparator.hashCode();
   }

   @Override
   public String toString() {
      return this.comparator.toString();
   }
}
