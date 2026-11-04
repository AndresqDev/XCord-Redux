package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.Spliterator;
import java.util.Spliterators;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableList<E> extends ImmutableList<E> {
   static final ImmutableList<Object> EMPTY = new RegularImmutableList<>(new Object[0]);
   @VisibleForTesting
   final transient Object[] array;

   RegularImmutableList(Object[] var1) {
      this.array = var1;
   }

   @Override
   public int size() {
      return this.array.length;
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   Object[] internalArray() {
      return this.array;
   }

   @Override
   int internalArrayStart() {
      return 0;
   }

   @Override
   int internalArrayEnd() {
      return this.array.length;
   }

   @Override
   int copyIntoArray(Object[] var1, int var2) {
      System.arraycopy(this.array, 0, var1, var2, this.array.length);
      return var2 + this.array.length;
   }

   @Override
   public E get(int var1) {
      return (E)this.array[var1];
   }

   @Override
   public UnmodifiableListIterator<E> listIterator(int var1) {
      return Iterators.forArray((E[])this.array, 0, this.array.length, var1);
   }

   @Override
   public Spliterator<E> spliterator() {
      return Spliterators.spliterator(this.array, 1296);
   }
}
