package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class NullsLastOrdering<T> extends Ordering<T> implements Serializable {
   final Ordering<? super T> ordering;
   private static final long serialVersionUID = 0L;

   NullsLastOrdering(Ordering<? super T> var1) {
      this.ordering = var1;
   }

   @Override
   public int compare(T var1, T var2) {
      if (var1 == var2) {
         return 0;
      } else if (var1 == null) {
         return 1;
      } else {
         return var2 == null ? -1 : this.ordering.compare((T)var1, (T)var2);
      }
   }

   @Override
   public <S extends T> Ordering<S> reverse() {
      return this.ordering.reverse().nullsFirst();
   }

   @Override
   public <S extends T> Ordering<S> nullsFirst() {
      return this.ordering.nullsFirst();
   }

   @Override
   public <S extends T> Ordering<S> nullsLast() {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof NullsLastOrdering) {
         NullsLastOrdering var2 = (NullsLastOrdering)var1;
         return this.ordering.equals(var2.ordering);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.ordering.hashCode() ^ -921210296;
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.ordering);
      return new StringBuilder(12 + String.valueOf(var1).length()).append(var1).append(".nullsLast()").toString();
   }
}
