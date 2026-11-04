package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.j2objc.annotations.Weak;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
final class SortedMultisets {
   private SortedMultisets() {
   }

   private static <E> E getElementOrThrow(Multiset.Entry<E> var0) {
      if (var0 == null) {
         throw new NoSuchElementException();
      } else {
         return (E)var0.getElement();
      }
   }

   private static <E> E getElementOrNull(Multiset.Entry<E> var0) {
      return (E)(var0 == null ? null : var0.getElement());
   }

   static class ElementSet<E> extends Multisets.ElementSet<E> implements SortedSet<E> {
      @Weak
      private final SortedMultiset<E> multiset;

      ElementSet(SortedMultiset<E> var1) {
         this.multiset = var1;
      }

      final SortedMultiset<E> multiset() {
         return this.multiset;
      }

      @Override
      public Iterator<E> iterator() {
         return Multisets.elementIterator(this.multiset().entrySet().iterator());
      }

      @Override
      public Comparator<? super E> comparator() {
         return this.multiset().comparator();
      }

      @Override
      public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
         return this.multiset().subMultiset((E)var1, BoundType.CLOSED, (E)var2, BoundType.OPEN).elementSet();
      }

      @Override
      public SortedSet<E> headSet(@ParametricNullness E var1) {
         return this.multiset().headMultiset((E)var1, BoundType.OPEN).elementSet();
      }

      @Override
      public SortedSet<E> tailSet(@ParametricNullness E var1) {
         return this.multiset().tailMultiset((E)var1, BoundType.CLOSED).elementSet();
      }

      @ParametricNullness
      @Override
      public E first() {
         return SortedMultisets.getElementOrThrow(this.multiset().firstEntry());
      }

      @ParametricNullness
      @Override
      public E last() {
         return SortedMultisets.getElementOrThrow(this.multiset().lastEntry());
      }
   }

   @GwtIncompatible
   static class NavigableElementSet<E> extends SortedMultisets.ElementSet<E> implements NavigableSet<E> {
      NavigableElementSet(SortedMultiset<E> var1) {
         super(var1);
      }

      @Override
      public E lower(@ParametricNullness E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().headMultiset((E)var1, BoundType.OPEN).lastEntry());
      }

      @Override
      public E floor(@ParametricNullness E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().headMultiset((E)var1, BoundType.CLOSED).lastEntry());
      }

      @Override
      public E ceiling(@ParametricNullness E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().tailMultiset((E)var1, BoundType.CLOSED).firstEntry());
      }

      @Override
      public E higher(@ParametricNullness E var1) {
         return SortedMultisets.getElementOrNull(this.multiset().tailMultiset((E)var1, BoundType.OPEN).firstEntry());
      }

      @Override
      public NavigableSet<E> descendingSet() {
         return new SortedMultisets.NavigableElementSet<>(this.multiset().descendingMultiset());
      }

      @Override
      public Iterator<E> descendingIterator() {
         return this.descendingSet().iterator();
      }

      @Override
      public E pollFirst() {
         return SortedMultisets.getElementOrNull(this.multiset().pollFirstEntry());
      }

      @Override
      public E pollLast() {
         return SortedMultisets.getElementOrNull(this.multiset().pollLastEntry());
      }

      @Override
      public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
         return new SortedMultisets.NavigableElementSet<>(this.multiset().subMultiset((E)var1, BoundType.forBoolean(var2), (E)var3, BoundType.forBoolean(var4)));
      }

      @Override
      public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
         return new SortedMultisets.NavigableElementSet<>(this.multiset().headMultiset((E)var1, BoundType.forBoolean(var2)));
      }

      @Override
      public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
         return new SortedMultisets.NavigableElementSet<>(this.multiset().tailMultiset((E)var1, BoundType.forBoolean(var2)));
      }
   }
}
