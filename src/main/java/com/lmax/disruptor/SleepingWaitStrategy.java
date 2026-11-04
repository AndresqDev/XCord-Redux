package com.lmax.disruptor;

import java.util.concurrent.locks.LockSupport;

public final class SleepingWaitStrategy implements WaitStrategy {
   private static final int DEFAULT_RETRIES = 200;
   private static final long DEFAULT_SLEEP = 100L;
   private final int retries;
   private final long sleepTimeNs;

   public SleepingWaitStrategy() {
      this(200, 100L);
   }

   public SleepingWaitStrategy(int var1) {
      this(var1, 100L);
   }

   public SleepingWaitStrategy(int var1, long var2) {
      this.retries = var1;
      this.sleepTimeNs = var2;
   }

   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException {
      int var8 = this.retries;

      long var6;
      while ((var6 = var4.get()) < var1) {
         var8 = this.applyWaitMethod(var5, var8);
      }

      return var6;
   }

   @Override
   public void signalAllWhenBlocking() {
   }

   private int applyWaitMethod(SequenceBarrier var1, int var2) throws AlertException {
      var1.checkAlert();
      if (var2 > 100) {
         var2--;
      } else if (var2 > 0) {
         var2--;
         Thread.yield();
      } else {
         LockSupport.parkNanos(this.sleepTimeNs);
      }

      return var2;
   }
}
