package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ListIterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingListIterator<E> extends ForwardingIterator<E> implements ListIterator<E> {
   protected ForwardingListIterator() {
   }

   protected abstract ListIterator<E> delegate();

   @Override
   public void add(@ParametricNullness E var1) {
      this.delegate().add((E)var1);
   }

   @Override
   public boolean hasPrevious() {
      return this.delegate().hasPrevious();
   }

   @Override
   public int nextIndex() {
      return this.delegate().nextIndex();
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public E previous() {
      return this.delegate().previous();
   }

   @Override
   public int previousIndex() {
      return this.delegate().previousIndex();
   }

   @Override
   public void set(@ParametricNullness E var1) {
      this.delegate().set((E)var1);
   }
}
