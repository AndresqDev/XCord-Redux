package com.lmax.disruptor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutBlockingWaitStrategy implements WaitStrategy {
   private final Lock lock = new ReentrantLock();
   private final Condition processorNotifyCondition = this.lock.newCondition();
   private final long timeoutInNanos;

   public TimeoutBlockingWaitStrategy(long var1, TimeUnit var3) {
      this.timeoutInNanos = var3.toNanos(var1);
   }

   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException, TimeoutException {
      long var6 = this.timeoutInNanos;
      if (var3.get() < var1) {
         this.lock.lock();

         try {
            while (var3.get() < var1) {
               var5.checkAlert();
               var6 = this.processorNotifyCondition.awaitNanos(var6);
               if (var6 <= 0L) {
                  throw TimeoutException.INSTANCE;
               }
            }
         } finally {
            this.lock.unlock();
         }
      }

      long var8;
      while ((var8 = var4.get()) < var1) {
         var5.checkAlert();
      }

      return var8;
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
      return "TimeoutBlockingWaitStrategy{processorNotifyCondition=" + this.processorNotifyCondition + '}';
   }
}
