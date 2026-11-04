package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscArrayQueue<E> extends MpscArrayQueueL3Pad<E> {
   public MpscArrayQueue(int var1) {
      super(var1);
   }

   public boolean offerIfBelowThreshold(E var1, int var2) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var3 = this.mask;
         long var5 = var3 + 1L;
         long var7 = this.lvProducerLimit();

         long var9;
         do {
            var9 = this.lvProducerIndex();
            long var11 = var7 - var9;
            long var13 = var5 - var11;
            if (var13 >= (long)var2) {
               long var15 = this.lvConsumerIndex();
               var13 = var9 - var15;
               if (var13 >= (long)var2) {
                  return false;
               }

               var7 = var15 + var5;
               this.soProducerLimit(var7);
            }
         } while (!this.casProducerIndex(var9, var9 + 1L));

         long var17 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var9, var3);
         UnsafeRefArrayAccess.soRefElement(this.buffer, var17, var1);
         return true;
      }
   }

   @Override
   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var2 = this.mask;
         long var4 = this.lvProducerLimit();

         long var6;
         do {
            var6 = this.lvProducerIndex();
            if (var6 >= var4) {
               long var8 = this.lvConsumerIndex();
               var4 = var8 + var2 + 1L;
               if (var6 >= var4) {
                  return false;
               }

               this.soProducerLimit(var4);
            }
         } while (!this.casProducerIndex(var6, var6 + 1L));

         long var10 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var6, var2);
         UnsafeRefArrayAccess.soRefElement(this.buffer, var10, var1);
         return true;
      }
   }

   public final int failFastOffer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var2 = this.mask;
         long var4 = var2 + 1L;
         long var6 = this.lvProducerIndex();
         long var8 = this.lvProducerLimit();
         if (var6 >= var8) {
            long var10 = this.lvConsumerIndex();
            var8 = var10 + var4;
            if (var6 >= var8) {
               return 1;
            }

            this.soProducerLimit(var8);
         }

         if (!this.casProducerIndex(var6, var6 + 1L)) {
            return -1;
         } else {
            long var13 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var6, var2);
            UnsafeRefArrayAccess.soRefElement(this.buffer, var13, var1);
            return 0;
         }
      }
   }

   @Override
   public E poll() {
      long var1 = this.lpConsumerIndex();
      long var3 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var1, this.mask);
      Object[] var5 = this.buffer;
      Object var6 = UnsafeRefArrayAccess.lvRefElement(var5, var3);
      if (null == var6) {
         if (var1 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var6 = UnsafeRefArrayAccess.lvRefElement(var5, var3);
         } while (var6 == null);
      }

      UnsafeRefArrayAccess.spRefElement(var5, var3, null);
      this.soConsumerIndex(var1 + 1L);
      return (E)var6;
   }

   @Override
   public E peek() {
      Object[] var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      long var4 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var2, this.mask);
      Object var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
      if (null == var6) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
         } while (var6 == null);
      }

      return (E)var6;
   }

   @Override
   public boolean relaxedOffer(E var1) {
      return this.offer((E)var1);
   }

   @Override
   public E relaxedPoll() {
      Object[] var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      long var4 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var2, this.mask);
      Object var6 = UnsafeRefArrayAccess.lvRefElement(var1, var4);
      if (null == var6) {
         return null;
      } else {
         UnsafeRefArrayAccess.spRefElement(var1, var4, null);
         this.soConsumerIndex(var2 + 1L);
         return (E)var6;
      }
   }

   @Override
   public E relaxedPeek() {
      Object[] var1 = this.buffer;
      long var2 = this.mask;
      long var4 = this.lpConsumerIndex();
      return UnsafeRefArrayAccess.lvRefElement((E[])var1, UnsafeRefArrayAccess.calcCircularRefElementOffset(var4, var2));
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
         Object[] var3 = this.buffer;
         long var4 = this.mask;
         long var6 = this.lpConsumerIndex();

         for (int var8 = 0; var8 < var2; var8++) {
            long var9 = var6 + (long)var8;
            long var11 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var9, var4);
            Object var13 = UnsafeRefArrayAccess.lvRefElement(var3, var11);
            if (null == var13) {
               return var8;
            }

            UnsafeRefArrayAccess.spRefElement(var3, var11, null);
            this.soConsumerIndex(var9 + 1L);
            var1.accept(var13);
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
         long var3 = this.mask;
         long var5 = var3 + 1L;
         long var7 = this.lvProducerLimit();

         long var9;
         int var11;
         do {
            var9 = this.lvProducerIndex();
            long var12 = var7 - var9;
            if (var12 <= 0L) {
               long var14 = this.lvConsumerIndex();
               var7 = var14 + var5;
               var12 = var7 - var9;
               if (var12 <= 0L) {
                  return 0;
               }

               this.soProducerLimit(var7);
            }

            var11 = Math.min((int)var12, var2);
         } while (!this.casProducerIndex(var9, var9 + (long)var11));

         Object[] var16 = this.buffer;

         for (int var13 = 0; var13 < var11; var13++) {
            long var17 = UnsafeRefArrayAccess.calcCircularRefElementOffset(var9 + (long)var13, var3);
            UnsafeRefArrayAccess.soRefElement(var16, var17, var1.get());
         }

         return var11;
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
}
