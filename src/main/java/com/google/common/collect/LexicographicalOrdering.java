package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class LexicographicalOrdering<T> extends Ordering<Iterable<T>> implements Serializable {
   final Comparator<? super T> elementOrder;
   private static final long serialVersionUID = 0L;

   LexicographicalOrdering(Comparator<? super T> var1) {
      this.elementOrder = var1;
   }

   public int compare(Iterable<T> var1, Iterable<T> var2) {
      Iterator var3 = var1.iterator();
      Iterator var4 = var2.iterator();

      while (var3.hasNext()) {
         if (!var4.hasNext()) {
            return 1;
         }

         int var5 = this.elementOrder.compare((T)var3.next(), (T)var4.next());
         if (var5 != 0) {
            return var5;
         }
      }

      return var4.hasNext() ? -1 : 0;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof LexicographicalOrdering) {
         LexicographicalOrdering var2 = (LexicographicalOrdering)var1;
         return this.elementOrder.equals(var2.elementOrder);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.elementOrder.hashCode() ^ 2075626741;
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.elementOrder);
      return new StringBuilder(18 + String.valueOf(var1).length()).append(var1).append(".lexicographical()").toString();
   }
}
