package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C> {
   EmptyContiguousSet(DiscreteDomain<C> var1) {
      super(var1);
   }

   public C first() {
      throw new NoSuchElementException();
   }

   public C last() {
      throw new NoSuchElementException();
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public ContiguousSet<C> intersection(ContiguousSet<C> var1) {
      return this;
   }

   @Override
   public Range<C> range() {
      throw new NoSuchElementException();
   }

   @Override
   public Range<C> range(BoundType var1, BoundType var2) {
      throw new NoSuchElementException();
   }

   @Override
   ContiguousSet<C> headSetImpl(C var1, boolean var2) {
      return this;
   }

   @Override
   ContiguousSet<C> subSetImpl(C var1, boolean var2, C var3, boolean var4) {
      return this;
   }

   @Override
   ContiguousSet<C> tailSetImpl(C var1, boolean var2) {
      return this;
   }

   @Override
   public boolean contains(Object var1) {
      return false;
   }

   @GwtIncompatible
   @Override
   int indexOf(Object var1) {
      return -1;
   }

   @Override
   public UnmodifiableIterator<C> iterator() {
      return Iterators.emptyIterator();
   }

   @GwtIncompatible
   @Override
   public UnmodifiableIterator<C> descendingIterator() {
      return Iterators.emptyIterator();
   }

   @Override
   boolean isPartialView() {
      return false;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public ImmutableList<C> asList() {
      return ImmutableList.of();
   }

   @Override
   public String toString() {
      return "[]";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof Set) {
         Set var2 = (Set)var1;
         return var2.isEmpty();
      } else {
         return false;
      }
   }

   @GwtIncompatible
   @Override
   boolean isHashCodeFast() {
      return true;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @GwtIncompatible
   @Override
   Object writeReplace() {
      return new EmptyContiguousSet.SerializedForm(this.domain);
   }

   @GwtIncompatible
   @Override
   ImmutableSortedSet<C> createDescendingSet() {
      return ImmutableSortedSet.emptySet(Ordering.natural().reverse());
   }

   @GwtIncompatible
   private static final class SerializedForm<C extends Comparable> implements Serializable {
      private final DiscreteDomain<C> domain;
      private static final long serialVersionUID = 0L;

      private SerializedForm(DiscreteDomain<C> var1) {
         this.domain = var1;
      }

      private Object readResolve() {
         return new EmptyContiguousSet<>(this.domain);
      }
   }
}
