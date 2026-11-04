package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E> {
   protected ForwardingNavigableSet() {
   }

   protected abstract NavigableSet<E> delegate();

   @Override
   public E lower(@ParametricNullness E var1) {
      return this.delegate().lower((E)var1);
   }

   protected E standardLower(@ParametricNullness E var1) {
      return Iterators.getNext(this.headSet((E)var1, false).descendingIterator(), null);
   }

   @Override
   public E floor(@ParametricNullness E var1) {
      return this.delegate().floor((E)var1);
   }

   protected E standardFloor(@ParametricNullness E var1) {
      return Iterators.getNext(this.headSet((E)var1, true).descendingIterator(), null);
   }

   @Override
   public E ceiling(@ParametricNullness E var1) {
      return this.delegate().ceiling((E)var1);
   }

   protected E standardCeiling(@ParametricNullness E var1) {
      return Iterators.getNext(this.tailSet((E)var1, true).iterator(), null);
   }

   @Override
   public E higher(@ParametricNullness E var1) {
      return this.delegate().higher((E)var1);
   }

   protected E standardHigher(@ParametricNullness E var1) {
      return Iterators.getNext(this.tailSet((E)var1, false).iterator(), null);
   }

   @Override
   public E pollFirst() {
      return this.delegate().pollFirst();
   }

   protected E standardPollFirst() {
      return Iterators.pollNext(this.iterator());
   }

   @Override
   public E pollLast() {
      return this.delegate().pollLast();
   }

   protected E standardPollLast() {
      return Iterators.pollNext(this.descendingIterator());
   }

   @ParametricNullness
   protected E standardFirst() {
      return this.iterator().next();
   }

   @ParametricNullness
   protected E standardLast() {
      return this.descendingIterator().next();
   }

   @Override
   public NavigableSet<E> descendingSet() {
      return this.delegate().descendingSet();
   }

   @Override
   public Iterator<E> descendingIterator() {
      return this.delegate().descendingIterator();
   }

   @Override
   public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
      return this.delegate().subSet((E)var1, var2, (E)var3, var4);
   }

   @Beta
   protected NavigableSet<E> standardSubSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
      return this.tailSet((E)var1, var2).headSet((E)var3, var4);
   }

   @Override
   protected SortedSet<E> standardSubSet(@ParametricNullness E var1, @ParametricNullness E var2) {
      return this.subSet((E)var1, true, (E)var2, false);
   }

   @Override
   public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
      return this.delegate().headSet((E)var1, var2);
   }

   protected SortedSet<E> standardHeadSet(@ParametricNullness E var1) {
      return this.headSet((E)var1, false);
   }

   @Override
   public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
      return this.delegate().tailSet((E)var1, var2);
   }

   protected SortedSet<E> standardTailSet(@ParametricNullness E var1) {
      return this.tailSet((E)var1, true);
   }

   @Beta
   protected class StandardDescendingSet extends Sets.DescendingSet<E> {
      public StandardDescendingSet(ForwardingNavigableSet var1) {
         super(var1);
      }
   }
}
