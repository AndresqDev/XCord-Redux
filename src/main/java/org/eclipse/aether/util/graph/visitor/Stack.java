package org.eclipse.aether.util.graph.visitor;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

class Stack<E> extends AbstractList<E> implements RandomAccess {
   private E[] elements = (E[])(new Object[96]);
   private int size;

   public void push(E var1) {
      if (this.size >= this.elements.length) {
         Object[] var2 = new Object[this.size + 64];
         System.arraycopy(this.elements, 0, var2, 0, this.elements.length);
         this.elements = (E[])var2;
      }

      this.elements[this.size++] = (E)var1;
   }

   public E pop() {
      if (this.size <= 0) {
         throw new NoSuchElementException();
      } else {
         return this.elements[--this.size];
      }
   }

   public E peek() {
      return this.size <= 0 ? null : this.elements[this.size - 1];
   }

   @Override
   public E get(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         return this.elements[this.size - var1 - 1];
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size);
      }
   }

   @Override
   public int size() {
      return this.size;
   }
}
