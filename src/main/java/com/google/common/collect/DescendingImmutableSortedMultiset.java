package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
   private final transient ImmutableSortedMultiset<E> forward;

   DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> var1) {
      this.forward = var1;
   }

   @Override
   public int count(Object var1) {
      return this.forward.count(var1);
   }

   @Override
   public Multiset.Entry<E> firstEntry() {
      return this.forward.lastEntry();
   }

   @Override
   public Multiset.Entry<E> lastEntry() {
      return this.forward.firstEntry();
   }

   @Override
   public int size() {
      return this.forward.size();
   }

   @Override
   public ImmutableSortedSet<E> elementSet() {
      return this.forward.elementSet().descendingSet();
   }

   @Override
   Multiset.Entry<E> getEntry(int var1) {
      return this.forward.entrySet().asList().reverse().get(var1);
   }

   @Override
   public ImmutableSortedMultiset<E> descendingMultiset() {
      return this.forward;
   }

   @Override
   public ImmutableSortedMultiset<E> headMultiset(E var1, BoundType var2) {
      return this.forward.tailMultiset((E)var1, var2).descendingMultiset();
   }

   @Override
   public ImmutableSortedMultiset<E> tailMultiset(E var1, BoundType var2) {
      return this.forward.headMultiset((E)var1, var2).descendingMultiset();
   }

   @Override
   boolean isPartialView() {
      return this.forward.isPartialView();
   }
}
