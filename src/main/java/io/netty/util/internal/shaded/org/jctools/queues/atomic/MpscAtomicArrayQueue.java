package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicArrayQueue<E> extends MpscAtomicArrayQueueL3Pad<E> {
   public MpscAtomicArrayQueue(int var1) {
      super(var1);
   }

   public boolean offerIfBelowThreshold(E var1, int var2) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var3 = this.mask;
         long var4 = (long)(var3 + 1);
         long var6 = this.lvProducerLimit();

         long var8;
         do {
            var8 = this.lvProducerIndex();
            long var10 = var6 - var8;
            long var12 = var4 - var10;
            if (var12 >= (long)var2) {
               long var14 = this.lvConsumerIndex();
               var12 = var8 - var14;
               if (var12 >= (long)var2) {
                  return false;
               }

               var6 = var14 + var4;
               this.soProducerLimit(var6);
            }
         } while (!this.casProducerIndex(var8, var8 + 1L));

         int var16 = AtomicQueueUtil.calcCircularRefElementOffset(var8, (long)var3);
         AtomicQueueUtil.soRefElement(this.buffer, var16, var1);
         return true;
      }
   }

   @Override
   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var2 = this.mask;
         long var3 = this.lvProducerLimit();

         long var5;
         do {
            var5 = this.lvProducerIndex();
            if (var5 >= var3) {
               long var7 = this.lvConsumerIndex();
               var3 = var7 + (long)var2 + 1L;
               if (var5 >= var3) {
                  return false;
               }

               this.soProducerLimit(var3);
            }
         } while (!this.casProducerIndex(var5, var5 + 1L));

         int var9 = AtomicQueueUtil.calcCircularRefElementOffset(var5, (long)var2);
         AtomicQueueUtil.soRefElement(this.buffer, var9, var1);
         return true;
      }
   }

   public final int failFastOffer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var2 = this.mask;
         long var3 = (long)(var2 + 1);
         long var5 = this.lvProducerIndex();
         long var7 = this.lvProducerLimit();
         if (var5 >= var7) {
            long var9 = this.lvConsumerIndex();
            var7 = var9 + var3;
            if (var5 >= var7) {
               return 1;
            }

            this.soProducerLimit(var7);
         }

         if (!this.casProducerIndex(var5, var5 + 1L)) {
            return -1;
         } else {
            int var12 = AtomicQueueUtil.calcCircularRefElementOffset(var5, (long)var2);
            AtomicQueueUtil.soRefElement(this.buffer, var12, var1);
            return 0;
         }
      }
   }

   @Override
   public E poll() {
      long var1 = this.lpConsumerIndex();
      int var3 = AtomicQueueUtil.calcCircularRefElementOffset(var1, (long)this.mask);
      AtomicReferenceArray var4 = this.buffer;
      Object var5 = AtomicQueueUtil.lvRefElement(var4, var3);
      if (null == var5) {
         if (var1 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var5 = AtomicQueueUtil.lvRefElement(var4, var3);
         } while (var5 == null);
      }

      AtomicQueueUtil.spRefElement(var4, var3, null);
      this.soConsumerIndex(var1 + 1L);
      return (E)var5;
   }

   @Override
   public E peek() {
      AtomicReferenceArray var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      int var4 = AtomicQueueUtil.calcCircularRefElementOffset(var2, (long)this.mask);
      Object var5 = AtomicQueueUtil.lvRefElement(var1, var4);
      if (null == var5) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var5 = AtomicQueueUtil.lvRefElement(var1, var4);
         } while (var5 == null);
      }

      return (E)var5;
   }

   @Override
   public boolean relaxedOffer(E var1) {
      return this.offer((E)var1);
   }

   @Override
   public E relaxedPoll() {
      AtomicReferenceArray var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      int var4 = AtomicQueueUtil.calcCircularRefElementOffset(var2, (long)this.mask);
      Object var5 = AtomicQueueUtil.lvRefElement(var1, var4);
      if (null == var5) {
         return null;
      } else {
         AtomicQueueUtil.spRefElement(var1, var4, null);
         this.soConsumerIndex(var2 + 1L);
         return (E)var5;
      }
   }

   @Override
   public E relaxedPeek() {
      AtomicReferenceArray var1 = this.buffer;
      int var2 = this.mask;
      long var3 = this.lpConsumerIndex();
      return AtomicQueueUtil.lvRefElement(var1, AtomicQueueUtil.calcCircularRefElementOffset(var3, (long)var2));
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      if (null == var1) {
         throw new IllegalArgumentException("c is null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("limit is negative: " + var2);
      } else if (var2 == 0) {
         return 0;
      } else {
         AtomicReferenceArray var3 = this.buffer;
         int var4 = this.mask;
         long var5 = this.lpConsumerIndex();

         for (int var7 = 0; var7 < var2; var7++) {
            long var8 = var5 + (long)var7;
            int var10 = AtomicQueueUtil.calcCircularRefElementOffset(var8, (long)var4);
            Object var11 = AtomicQueueUtil.lvRefElement(var3, var10);
            if (null == var11) {
               return var7;
            }

            AtomicQueueUtil.spRefElement(var3, var10, null);
            this.soConsumerIndex(var8 + 1L);
            var1.accept(var11);
         }

         return var2;
      }
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
         int var3 = this.mask;
         long var4 = (long)(var3 + 1);
         long var6 = this.lvProducerLimit();
         int var10 = 0;

         long var8;
         do {
            var8 = this.lvProducerIndex();
            long var11 = var6 - var8;
            if (var11 <= 0L) {
               long var13 = this.lvConsumerIndex();
               var6 = var13 + var4;
               var11 = var6 - var8;
               if (var11 <= 0L) {
                  return 0;
               }

               this.soProducerLimit(var6);
            }

            var10 = Math.min((int)var11, var2);
         } while (!this.casProducerIndex(var8, var8 + (long)var10));

         AtomicReferenceArray var16 = this.buffer;

         for (int var12 = 0; var12 < var10; var12++) {
            int var17 = AtomicQueueUtil.calcCircularRefElementOffset(var8 + (long)var12, (long)var3);
            AtomicQueueUtil.soRefElement(var16, var17, var1.get());
         }

         return var10;
      }
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return this.drain(var1, this.capacity());
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> var1) {
      return MessagePassingQueueUtil.fillBounded(this, var1);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.drain(this, var1, var2, var3);
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      MessagePassingQueueUtil.fill(this, var1, var2, var3);
   }

   @Deprecated
   public int weakOffer(E var1) {
      return this.failFastOffer((E)var1);
   }
}
