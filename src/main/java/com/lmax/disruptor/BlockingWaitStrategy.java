package com.lmax.disruptor;

import com.lmax.disruptor.util.ThreadHints;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingWaitStrategy implements WaitStrategy {
   private final Lock lock = new ReentrantLock();
   private final Condition processorNotifyCondition = this.lock.newCondition();

   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException {
      if (var3.get() < var1) {
         this.lock.lock();

         try {
            while (var3.get() < var1) {
               var5.checkAlert();
               this.processorNotifyCondition.await();
            }
         } finally {
            this.lock.unlock();
         }
      }

      long var6;
      while ((var6 = var4.get()) < var1) {
         var5.checkAlert();
         ThreadHints.onSpinWait();
      }

      return var6;
   }

   @Override
   public void signalAllWhenBlocking() {
      this.lock.lock();

      try {
         this.processorNotifyCondition.signalAll();
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public String toString() {
      return "BlockingWaitStrategy{processorNotifyCondition=" + this.processorNotifyCondition + '}';
   }
}
