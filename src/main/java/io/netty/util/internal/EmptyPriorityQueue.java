package io.netty.util.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyPriorityQueue<T> implements PriorityQueue<T> {
   private static final PriorityQueue<Object> INSTANCE = new EmptyPriorityQueue<>();

   private EmptyPriorityQueue() {
   }

   public static <V> EmptyPriorityQueue<V> instance() {
      return (EmptyPriorityQueue<V>)INSTANCE;
   }

   @Override
   public boolean removeTyped(T var1) {
      return false;
   }

   @Override
   public boolean containsTyped(T var1) {
      return false;
   }

   @Override
   public void priorityChanged(T var1) {
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean contains(Object var1) {
      return false;
   }

   @Override
   public Iterator<T> iterator() {
      return Collections.<T>emptyList().iterator();
   }

   @Override
   public Object[] toArray() {
      return EmptyArrays.EMPTY_OBJECTS;
   }

   @Override
   public <T1> T1[] toArray(T1[] var1) {
      if (var1.length > 0) {
         var1[0] = null;
      }

      return (T1[])var1;
   }

   @Override
   public boolean add(T var1) {
      return false;
   }

   @Override
   public boolean remove(Object var1) {
      return false;
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      return false;
   }

   @Override
   public boolean addAll(Collection<? extends T> var1) {
      return false;
   }

   @Override
   public boolean removeAll(Collection<?> var1) {
      return false;
   }

   @Override
   public boolean retainAll(Collection<?> var1) {
      return false;
   }

   @Override
   public void clear() {
   }

   @Override
   public void clearIgnoringIndexes() {
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof PriorityQueue && ((PriorityQueue)var1).isEmpty();
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public boolean offer(T var1) {
      return false;
   }

   @Override
   public T remove() {
      throw new NoSuchElementException();
   }

   @Override
   public T poll() {
      return null;
   }

   @Override
   public T element() {
      throw new NoSuchElementException();
   }

   @Override
   public T peek() {
      return null;
   }

   @Override
   public String toString() {
      return EmptyPriorityQueue.class.getSimpleName();
   }
}
