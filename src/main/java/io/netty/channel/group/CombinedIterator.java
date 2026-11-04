package io.netty.channel.group;

import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator<E> implements Iterator<E> {
   private final Iterator<E> i1;
   private final Iterator<E> i2;
   private Iterator<E> currentIterator;

   CombinedIterator(Iterator<E> var1, Iterator<E> var2) {
      this.i1 = ObjectUtil.checkNotNull(var1, "i1");
      this.i2 = ObjectUtil.checkNotNull(var2, "i2");
      this.currentIterator = var1;
   }

   @Override
   public boolean hasNext() {
      while (!this.currentIterator.hasNext()) {
         if (this.currentIterator != this.i1) {
            return false;
         }

         this.currentIterator = this.i2;
      }

      return true;
   }

   @Override
   public E next() {
      while (true) {
         try {
            return this.currentIterator.next();
         } catch (NoSuchElementException var2) {
            if (this.currentIterator != this.i1) {
               throw var2;
            }

            this.currentIterator = this.i2;
         }
      }
   }

   @Override
   public void remove() {
      this.currentIterator.remove();
   }
}
