package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true
)
final class ReverseOrdering<T> extends Ordering<T> implements Serializable {
   final Ordering<? super T> forwardOrder;
   private static final long serialVersionUID = 0L;

   ReverseOrdering(Ordering<? super T> var1) {
      this.forwardOrder = Preconditions.checkNotNull(var1);
   }

   @Override
   public int compare(@ParametricNullness T var1, @ParametricNullness T var2) {
      return this.forwardOrder.compare((T)var2, (T)var1);
   }

   @Override
   public <S extends T> Ordering<S> reverse() {
      return this.forwardOrder;
   }

   @Override
   public <E extends T> E min(@ParametricNullness E var1, @ParametricNullness E var2) {
      return this.forwardOrder.max((E)var1, (E)var2);
   }

   @Override
   public <E extends T> E min(@ParametricNullness E var1, @ParametricNullness E var2, @ParametricNullness E var3, E... var4) {
      return this.forwardOrder.max((E)var1, (E)var2, (E)var3, (E[])var4);
   }

   @Override
   public <E extends T> E min(Iterator<E> var1) {
      return this.forwardOrder.max(var1);
   }

   @Override
   public <E extends T> E min(Iterable<E> var1) {
      return this.forwardOrder.max(var1);
   }

   @Override
   public <E extends T> E max(@ParametricNullness E var1, @ParametricNullness E var2) {
      return this.forwardOrder.min((E)var1, (E)var2);
   }

   @Override
   public <E extends T> E max(@ParametricNullness E var1, @ParametricNullness E var2, @ParametricNullness E var3, E... var4) {
      return this.forwardOrder.min((E)var1, (E)var2, (E)var3, (E[])var4);
   }

   @Override
   public <E extends T> E max(Iterator<E> var1) {
      return this.forwardOrder.min(var1);
   }

   @Override
   public <E extends T> E max(Iterable<E> var1) {
      return this.forwardOrder.min(var1);
   }

   @Override
   public int hashCode() {
      return -this.forwardOrder.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof ReverseOrdering) {
         ReverseOrdering var2 = (ReverseOrdering)var1;
         return this.forwardOrder.equals(var2.forwardOrder);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      String var1 = String.valueOf(this.forwardOrder);
      return new StringBuilder(10 + String.valueOf(var1).length()).append(var1).append(".reverse()").toString();
   }
}
