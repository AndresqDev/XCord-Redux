package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class ConcurrentCircularArrayQueue<E>
   extends ConcurrentCircularArrayQueueL0Pad<E>
   implements IndexedQueueSizeUtil.IndexedQueue,
   MessagePassingQueue<E>,
   QueueProgressIndicators,
   SupportsIterator {
   protected final long mask;
   protected final E[] buffer;

   ConcurrentCircularArrayQueue(int var1) {
      int var2 = Pow2.roundToPowerOfTwo(var1);
      this.mask = (long)(var2 - 1);
      this.buffer = (E[])UnsafeRefArrayAccess.allocateRefArray(var2);
   }

   @Override
   public int size() {
      return IndexedQueueSizeUtil.size(this);
   }

   @Override
   public boolean isEmpty() {
      return IndexedQueueSizeUtil.isEmpty(this);
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   @Override
   public void clear() {
      while (this.poll() != null) {
      }
   }

   @Override
   public int capacity() {
      return (int)(this.mask + 1L);
   }

   @Override
   public long currentProducerIndex() {
      return this.lvProducerIndex();
   }

   @Override
   public long currentConsumerIndex() {
      return this.lvConsumerIndex();
   }

   @Override
   public Iterator<E> iterator() {
      long var1 = this.lvConsumerIndex();
      long var3 = this.lvProducerIndex();
      return new ConcurrentCircularArrayQueue.WeakIterator<>(var1, var3, this.mask, this.buffer);
   }

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private final long mask;
      private final E[] buffer;
      private long nextIndex;
      private E nextElement;

      WeakIterator(long var1, long var3, long var5, E[] var7) {
         this.nextIndex = var1;
         this.pIndex = var3;
         this.mask = var5;
         this.buffer = (E[])var7;
         this.nextElement = this.getNext();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public boolean hasNext() {
         return this.nextElement != null;
      }

      @Override
      public E next() {
         Object var1 = this.nextElement;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            this.nextElement = this.getNext();
            return (E)var1;
         }
      }

      private E getNext() {
         while (this.nextIndex < this.pIndex) {
            long var1 = UnsafeRefArrayAccess.calcCircularRefElementOffset(this.nextIndex++, this.mask);
            Object var3 = UnsafeRefArrayAccess.lvRefElement(this.buffer, var1);
            if (var3 != null) {
               return (E)var3;
            }
         }

         return null;
      }
   }
}
