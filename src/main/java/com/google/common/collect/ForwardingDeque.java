package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Deque;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public abstract class ForwardingDeque<E> extends ForwardingQueue<E> implements Deque<E> {
   protected ForwardingDeque() {
   }

   protected abstract Deque<E> delegate();

   @Override
   public void addFirst(@ParametricNullness E var1) {
      this.delegate().addFirst((E)var1);
   }

   @Override
   public void addLast(@ParametricNullness E var1) {
      this.delegate().addLast((E)var1);
   }

   @Override
   public Iterator<E> descendingIterator() {
      return this.delegate().descendingIterator();
   }

   @ParametricNullness
   @Override
   public E getFirst() {
      return this.delegate().getFirst();
   }

   @ParametricNullness
   @Override
   public E getLast() {
      return this.delegate().getLast();
   }

   @CanIgnoreReturnValue
   @Override
   public boolean offerFirst(@ParametricNullness E var1) {
      return this.delegate().offerFirst((E)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean offerLast(@ParametricNullness E var1) {
      return this.delegate().offerLast((E)var1);
   }

   @Override
   public E peekFirst() {
      return this.delegate().peekFirst();
   }

   @Override
   public E peekLast() {
      return this.delegate().peekLast();
   }

   @CanIgnoreReturnValue
   @Override
   public E pollFirst() {
      return this.delegate().pollFirst();
   }

   @CanIgnoreReturnValue
   @Override
   public E pollLast() {
      return this.delegate().pollLast();
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public E pop() {
      return this.delegate().pop();
   }

   @Override
   public void push(@ParametricNullness E var1) {
      this.delegate().push((E)var1);
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public E removeFirst() {
      return this.delegate().removeFirst();
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public E removeLast() {
      return this.delegate().removeLast();
   }

   @CanIgnoreReturnValue
   @Override
   public boolean removeFirstOccurrence(Object var1) {
      return this.delegate().removeFirstOccurrence(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean removeLastOccurrence(Object var1) {
      return this.delegate().removeLastOccurrence(var1);
   }
}
