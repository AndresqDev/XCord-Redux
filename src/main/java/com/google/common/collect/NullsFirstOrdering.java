package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class NullsFirstOrdering<T> extends Ordering<T> implements Serializable {
   final Ordering<? super T> ordering;
   private static final long serialVersionUID = 0L;

   NullsFirstOrdering(Ordering<? super T> var1) {
      this.ordering = var1;
   }

   @Override
   public int compare(T var1, T var2) {
      if (var1 == var2) {
         return 0;
      } else if (var1 == null) {
         return -1;
      } else {
         return var2 == null ? 1 : this.ordering.compare((T)var1, (T)var2);
      }
   }

   @Override
   public <S extends T> Ordering<S> reverse() {
      return this.ordering.reverse().nullsLast();
   }

   @Override
   public <S extends T> Ordering<S> nullsFirst() {
      return this;
   }

   @Override
   public <S extends T> Ordering<S> nullsLast() {
      return this.ordering.nullsLast();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof NullsFirstOrdering) {
         NullsFirstOrdering var2 = (NullsFirstOrdering)var1;
         return this.ordering.equals(var2.ordering);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.ordering.hashCode() ^ 957692532;
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.ordering);
      return new StringBuilder(13 + String.valueOf(var1).length()).append(var1).append(".nullsFirst()").toString();
   }
}
