package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableSet<E> extends ImmutableSet<E> {
   final transient E element;

   SingletonImmutableSet(E var1) {
      this.element = Preconditions.checkNotNull((E)var1);
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public boolean contains(Object var1) {
      return this.element.equals(var1);
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      return Iterators.singletonIterator(this.element);
   }

   @Override
   public ImmutableList<E> asList() {
      return ImmutableList.of(this.element);
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   int copyIntoArray(Object[] var1, int var2) {
      var1[var2] = this.element;
      return var2 + 1;
   }

   @Override
   public final int hashCode() {
      return this.element.hashCode();
   }

   @Override
   public String toString() {
      String var1 = this.element.toString();
      return new StringBuilder(2 + String.valueOf(var1).length()).append('[').append(var1).append(']').toString();
   }
}
