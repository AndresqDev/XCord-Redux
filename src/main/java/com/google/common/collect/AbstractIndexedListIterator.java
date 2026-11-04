package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractIndexedListIterator<E> extends UnmodifiableListIterator<E> {
   private final int size;
   private int position;

   @ParametricNullness
   protected abstract E get(int var1);

   protected AbstractIndexedListIterator(int var1) {
      this(var1, 0);
   }

   protected AbstractIndexedListIterator(int var1, int var2) {
      Preconditions.checkPositionIndex(var2, var1);
      this.size = var1;
      this.position = var2;
   }

   @Override
   public final boolean hasNext() {
      return this.position < this.size;
   }

   @ParametricNullness
   @Override
   public final E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.position++);
      }
   }

   @Override
   public final int nextIndex() {
      return this.position;
   }

   @Override
   public final boolean hasPrevious() {
      return this.position > 0;
   }

   @ParametricNullness
   @Override
   public final E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.get(--this.position);
      }
   }

   @Override
   public final int previousIndex() {
      return this.position - 1;
   }
}
