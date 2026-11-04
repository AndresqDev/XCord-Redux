package org.apache.logging.log4j.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectArrayIterator<E> implements Iterator<E> {
   final E[] array;
   final int startIndex;
   final int endIndex;
   int index = 0;

   @SafeVarargs
   public ObjectArrayIterator(E... var1) {
      this((E[])var1, 0, var1.length);
   }

   public ObjectArrayIterator(E[] var1, int var2) {
      this((E[])var1, var2, var1.length);
   }

   public ObjectArrayIterator(E[] var1, int var2, int var3) {
      if (var2 < 0) {
         throw new ArrayIndexOutOfBoundsException("Start index must not be less than zero");
      } else if (var3 > var1.length) {
         throw new ArrayIndexOutOfBoundsException("End index must not be greater than the array length");
      } else if (var2 > var1.length) {
         throw new ArrayIndexOutOfBoundsException("Start index must not be greater than the array length");
      } else if (var3 < var2) {
         throw new IllegalArgumentException("End index must not be less than start index");
      } else {
         this.array = (E[])var1;
         this.startIndex = var2;
         this.endIndex = var3;
         this.index = var2;
      }
   }

   @Override
   public boolean hasNext() {
      return this.index < this.endIndex;
   }

   @Override
   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.array[this.index++];
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("remove() method is not supported for an ObjectArrayIterator");
   }

   public E[] getArray() {
      return this.array;
   }

   public int getStartIndex() {
      return this.startIndex;
   }

   public int getEndIndex() {
      return this.endIndex;
   }

   public void reset() {
      this.index = this.startIndex;
   }
}
