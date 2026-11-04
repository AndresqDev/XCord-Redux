package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E> implements MessagePassingQueue<E>, QueueProgressIndicators {
   private static final Object JUMP = new Object();
   private static final Object BUFFER_CONSUMED = new Object();
   private static final int CONTINUE_TO_P_INDEX_CAS = 0;
   private static final int RETRY = 1;
   private static final int QUEUE_FULL = 2;
   private static final int QUEUE_RESIZE = 3;

   public BaseMpscLinkedArrayQueue(int var1) {
      RangeUtil.checkGreaterThanOrEqual(var1, 2, "initialCapacity");
      int var2 = Pow2.roundToPowerOfTwo(var1);
      long var3 = (long)(var2 - 1 << 1);
      Object[] var5 = UnsafeRefArrayAccess.allocateRefArray(var2 + 1);
      this.producerBuffer = (E[])var5;
      this.producerMask = var3;
      this.consumerBuffer = (E[])var5;
      this.consumerMask = var3;
      this.soProducerLimit(var3);
   }

   @Override
   public int size() {
      long var1 = this.lvConsumerIndex();

      long var5;
      long var7;
      do {
         var5 = var1;
         var7 = this.lvProducerIndex();
         var1 = this.lvConsumerIndex();
      } while (var5 != var1);

      long var3 = var7 - var1 >> 1;
      return var3 > 2147483647L ? Integer.MAX_VALUE : (int)var3;
   }

   @Override
   public boolean isEmpty() {
      return this.lvConsumerIndex() == this.lvProducerIndex();
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   @Override
   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         while (true) {
            long var7 = this.lvProducerLimit();
            long var5 = this.lvProducerIndex();
            if ((var5 & 1L) != 1L) {
               long var2 = this.producerMask;
               Object[] var4 = this.producerBuffer;
               if (var7 <= var5) {
                  int var9 = this.offerSlowPath(var2, var5, var7);
                  switch (var9) {
                     case 0:
                     default:
                        break;
                     case 1:
                        continue;
                     case 2:
                        return false;
                     case 3:
                        this.resize(var2, (E[])var4, var5, (E)var1, null);
                        return true;
                  }
               }

               if (this.casProducerIndex(var5, var5 + 2L)) {
                  var7 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var5, var2);
                  UnsafeRefArrayAccess.soRefElement(var4, var7, var1);
                  return true;
               }
            }
         }
      }
   }

   @Override
   public E poll() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
      if (var8 == null) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
         } while (var8 == null);
      }

      if (var8 == JUMP) {
         Object[] var9 = this.nextBuffer((E[])var1, var4);
         return this.newBufferPoll((E[])var9, var2);
      } else {
         UnsafeRefArrayAccess.soRefElement(var1, var6, null);
         this.soConsumerIndex(var2 + 2L);
         return (E)var8;
      }
   }

   @Override
   public E peek() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
      if (var8 == null && var2 != this.lvProducerIndex()) {
         do {
            var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
         } while (var8 == null);
      }

      return (E)(var8 == JUMP ? this.newBufferPeek(this.nextBuffer((E[])var1, var4), var2) : var8);
   }

   private int offerSlowPath(long var1, long var3, long var5) {
      long var7 = this.lvConsumerIndex();
      long var9 = this.getCurrentBufferCapacity(var1);
      if (var7 + var9 > var3) {
         return !this.casProducerLimit(var5, var7 + var9) ? 1 : 0;
      } else if (this.availableInQueue(var3, var7) <= 0L) {
         return 2;
      } else {
         return this.casProducerIndex(var3, var3 + 1L) ? 3 : 1;
      }
   }

   protected abstract long availableInQueue(long var1, long var3);

   private E[] nextBuffer(E[] var1, long var2) {
      long var4 = nextArrayOffset(var2);
      Object[] var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
      this.consumerBuffer = (E[])var6;
      this.consumerMask = (long)(LinkedArrayQueueUtil.length(var6) - 2 << 1);
      UnsafeRefArrayAccess.soRefElement(var1, var4, BUFFER_CONSUMED);
      return (E[])var6;
   }

   private static long nextArrayOffset(long var0) {
      return LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var0 + 2L, Long.MAX_VALUE);
   }

   private E newBufferPoll(E[] var1, long var2) {
      long var4 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, this.consumerMask);
      Object var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
      if (var6 == null) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         UnsafeRefArrayAccess.soRefElement(var1, var4, null);
         this.soConsumerIndex(var2 + 2L);
         return (E)var6;
      }
   }

   private E newBufferPeek(E[] var1, long var2) {
      long var4 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, this.consumerMask);
      Object var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
      if (null == var6) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         return (E)var6;
      }
   }

   @Override
   public long currentProducerIndex() {
      return this.lvProducerIndex() / 2L;
   }

   @Override
   public long currentConsumerIndex() {
      return this.lvConsumerIndex() / 2L;
   }

   @Override
   public abstract int capacity();

   @Override
   public boolean relaxedOffer(E var1) {
      return this.offer((E)var1);
   }

   @Override
   public E relaxedPoll() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
      if (var8 == null) {
         return null;
      } else if (var8 == JUMP) {
         Object[] var9 = this.nextBuffer((E[])var1, var4);
         return this.newBufferPoll((E[])var9, var2);
      } else {
         UnsafeRefArrayAccess.soRefElement(var1, var6, null);
         this.soConsumerIndex(var2 + 2L);
         return (E)var8;
      }
   }

   @Override
   public E relaxedPeek() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvRefElement(var1, var6);
      return (E)(var8 == JUMP ? this.newBufferPeek(this.nextBuffer((E[])var1, var4), var2) : var8);
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> var1) {
      long var2 = 0L;
      int var4 = this.capacity();

      do {
         int var5 = this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
         if (var5 == 0) {
            return (int)var2;
         }

         var2 += (long)var5;
      } while (var2 <= (long)var4);

      return (int)var2;
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      if (null == var1) {
         throw new IllegalArgumentException("supplier is null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("limit is negative:" + var2);
      } else if (var2 == 0) {
         return 0;
      } else {
         while (true) {
            long var9 = this.lvProducerLimit();
            long var6 = this.lvProducerIndex();
            if ((var6 & 1L) != 1L) {
               long var3 = this.producerMask;
               Object[] var5 = this.producerBuffer;
               long var11 = Math.min(var9, var6 + 2L * (long)var2);
               if (var6 >= var9) {
                  int var13 = this.offerSlowPath(var3, var6, var9);
                  switch (var13) {
                     case 0:
                     case 1:
                        continue;
                     case 2:
                        return 0;
                     case 3:
                        this.resize(var3, (E[])var5, var6, null, var1);
                        return 1;
                  }
               }

               if (this.casProducerIndex(var6, var11)) {
                  int var8 = (int)((var11 - var6) / 2L);

                  for (int var14 = 0; var14 < var8; var14++) {
                     long var10 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var6 + 2L * (long)var14, var3);
                     UnsafeRefArrayAccess.soRefElement(var5, var10, var1.get());
                  }

                  return var8;
               }
            }
         }
      }
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.fill(this, var1, var2, var3);
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return this.drain(var1, this.capacity());
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      return MessagePassingQueueUtil.drain(this, var1, var2);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.drain(this, var1, var2, var3);
   }

   @Override
   public Iterator<E> iterator() {
      return new BaseMpscLinkedArrayQueue.WeakIterator<>(this.consumerBuffer, this.lvConsumerIndex(), this.lvProducerIndex());
   }

   private void resize(long var1, E[] var3, long var4, E var6, MessagePassingQueue.Supplier<E> var7) {
      assert var6 != null && var7 == null || var6 == null || var7 != null;

      int var8 = this.getNextBufferSize((E[])var3);

      Object[] var9;
      try {
         var9 = UnsafeRefArrayAccess.allocateRefArray(var8);
      } catch (OutOfMemoryError var19) {
         assert this.lvProducerIndex() == var4 + 1L;

         this.soProducerIndex(var4);
         throw var19;
      }

      this.producerBuffer = (E[])var9;
      int var10 = var8 - 2 << 1;
      this.producerMask = (long)var10;
      long var11 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var4, var1);
      long var13 = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(var4, (long)var10);
      UnsafeRefArrayAccess.soRefElement(var9, var13, var6 == null ? var7.get() : var6);
      UnsafeRefArrayAccess.soRefElement(var3, nextArrayOffset(var1), var9);
      long var15 = this.lvConsumerIndex();
      long var17 = this.availableInQueue(var4, var15);
      RangeUtil.checkPositive(var17, "availableInQueue");
      this.soProducerLimit(var4 + Math.min((long)var10, var17));
      this.soProducerIndex(var4 + 2L);
      UnsafeRefArrayAccess.soRefElement(var3, var11, JUMP);
   }

   protected abstract int getNextBufferSize(E[] var1);

   protected abstract long getCurrentBufferCapacity(long var1);

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private long nextIndex;
      private E nextElement;
      private E[] currentBuffer;
      private int mask;

      WeakIterator(E[] var1, long var2, long var4) {
         this.pIndex = var4 >> 1;
         this.nextIndex = var2 >> 1;
         this.setBuffer((E[])var1);
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

      private void setBuffer(E[] var1) {
         this.currentBuffer = (E[])var1;
         this.mask = LinkedArrayQueueUtil.length(var1) - 2;
      }

      private E getNext() {
         while (this.nextIndex < this.pIndex) {
            long var1 = this.nextIndex++;
            Object var3 = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(var1, (long)this.mask));
            if (var3 != null) {
               if (var3 != BaseMpscLinkedArrayQueue.JUMP) {
                  return (E)var3;
               }

               int var4 = this.mask + 1;
               Object var5 = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcRefElementOffset((long)var4));
               if (var5 != BaseMpscLinkedArrayQueue.BUFFER_CONSUMED && var5 != null) {
                  this.setBuffer((E[])((Object[])var5));
                  var3 = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(var1, (long)this.mask));
                  if (var3 == null) {
                     continue;
                  }

                  return (E)var3;
               }

               return null;
            }
         }

         return null;
      }
   }
}
