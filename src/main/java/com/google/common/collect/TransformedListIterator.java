package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T> {
   TransformedListIterator(ListIterator<? extends F> var1) {
      super(var1);
   }

   private ListIterator<? extends F> backingIterator() {
      return Iterators.cast(this.backingIterator);
   }

   @Override
   public final boolean hasPrevious() {
      return this.backingIterator().hasPrevious();
   }

   @ParametricNullness
   @Override
   public final T previous() {
      return this.transform((F)this.backingIterator().previous());
   }

   @Override
   public final int nextIndex() {
      return this.backingIterator().nextIndex();
   }

   @Override
   public final int previousIndex() {
      return this.backingIterator().previousIndex();
   }

   @Override
   public void set(@ParametricNullness T var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void add(@ParametricNullness T var1) {
      throw new UnsupportedOperationException();
   }
}
