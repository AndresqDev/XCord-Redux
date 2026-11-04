package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class CompoundOrdering<T> extends Ordering<T> implements Serializable {
   final Comparator<? super T>[] comparators;
   private static final long serialVersionUID = 0L;

   CompoundOrdering(Comparator<? super T> var1, Comparator<? super T> var2) {
      this.comparators = new Comparator[]{var1, var2};
   }

   CompoundOrdering(Iterable<? extends Comparator<? super T>> var1) {
      this.comparators = Iterables.toArray(var1, new Comparator[0]);
   }

   @Override
   public int compare(@ParametricNullness T var1, @ParametricNullness T var2) {
      for (int var3 = 0; var3 < this.comparators.length; var3++) {
         int var4 = this.comparators[var3].compare((T)var1, (T)var2);
         if (var4 != 0) {
            return var4;
         }
      }

      return 0;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof CompoundOrdering) {
         CompoundOrdering var2 = (CompoundOrdering)var1;
         return Arrays.equals((Object[])this.comparators, (Object[])var2.comparators);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.comparators);
   }

   @Override
   public String toString() {
      String var1 = Arrays.toString((Object[])this.comparators);
      return new StringBuilder(19 + String.valueOf(var1).length()).append("Ordering.compound(").append(var1).append(")").toString();
   }
}
