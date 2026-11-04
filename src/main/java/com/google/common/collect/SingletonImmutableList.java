package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Spliterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableList<E> extends ImmutableList<E> {
   final transient E element;

   SingletonImmutableList(E var1) {
      this.element = Preconditions.checkNotNull((E)var1);
   }

   @Override
   public E get(int var1) {
      Preconditions.checkElementIndex(var1, 1);
      return this.element;
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      return Iterators.singletonIterator(this.element);
   }

   @Override
   public Spliterator<E> spliterator() {
      return Collections.singleton(this.element).spliterator();
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public ImmutableList<E> subList(int var1, int var2) {
      Preconditions.checkPositionIndexes(var1, var2, 1);
      return (ImmutableList<E>)(var1 == var2 ? ImmutableList.of() : this);
   }

   @Override
   public String toString() {
      String var1 = this.element.toString();
      return new StringBuilder(2 + String.valueOf(var1).length()).append('[').append(var1).append(']').toString();
   }

   @Override
   boolean isPartialView() {
      return false;
   }
}
