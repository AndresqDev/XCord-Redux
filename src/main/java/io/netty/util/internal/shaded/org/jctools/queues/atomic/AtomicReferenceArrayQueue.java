package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.SupportsIterator;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceArray;

abstract class AtomicReferenceArrayQueue<E>
   extends AbstractQueue<E>
   implements IndexedQueueSizeUtil.IndexedQueue,
   MessagePassingQueue<E>,
   QueueProgressIndicators,
   SupportsIterator {
   protected final AtomicReferenceArray<E> buffer;
   protected final int mask;

   public AtomicReferenceArrayQueue(int var1) {
      int var2 = Pow2.roundToPowerOfTwo(var1);
      this.mask = var2 - 1;
      this.buffer = new AtomicReferenceArray<>(var2);
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
   public final int capacity() {
      return this.mask + 1;
   }

   @Override
   public final int size() {
      return IndexedQueueSizeUtil.size(this);
   }

   @Override
   public final boolean isEmpty() {
      return IndexedQueueSizeUtil.isEmpty(this);
   }

   @Override
   public final long currentProducerIndex() {
      return this.lvProducerIndex();
   }

   @Override
   public final long currentConsumerIndex() {
      return this.lvConsumerIndex();
   }

   @Override
   public final Iterator<E> iterator() {
      long var1 = this.lvConsumerIndex();
      long var3 = this.lvProducerIndex();
      return new AtomicReferenceArrayQueue.WeakIterator<>(var1, var3, this.mask, this.buffer);
   }

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private final int mask;
      private final AtomicReferenceArray<E> buffer;
      private long nextIndex;
      private E nextElement;

      WeakIterator(long var1, long var3, int var5, AtomicReferenceArray<E> var6) {
         this.nextIndex = var1;
         this.pIndex = var3;
         this.mask = var5;
         this.buffer = var6;
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
         int var1 = this.mask;
         AtomicReferenceArray var2 = this.buffer;

         while (this.nextIndex < this.pIndex) {
            int var3 = AtomicQueueUtil.calcCircularRefElementOffset(this.nextIndex++, (long)var1);
            Object var4 = AtomicQueueUtil.lvRefElement(var2, var3);
            if (var4 != null) {
               return (E)var4;
            }
         }

         return null;
      }
   }
}
