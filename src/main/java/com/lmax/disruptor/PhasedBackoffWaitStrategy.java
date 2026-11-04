package com.lmax.disruptor;

import java.util.concurrent.TimeUnit;

public final class PhasedBackoffWaitStrategy implements WaitStrategy {
   private static final int SPIN_TRIES = 10000;
   private final long spinTimeoutNanos;
   private final long yieldTimeoutNanos;
   private final WaitStrategy fallbackStrategy;

   public PhasedBackoffWaitStrategy(long var1, long var3, TimeUnit var5, WaitStrategy var6) {
      this.spinTimeoutNanos = var5.toNanos(var1);
      this.yieldTimeoutNanos = this.spinTimeoutNanos + var5.toNanos(var3);
      this.fallbackStrategy = var6;
   }

   public static PhasedBackoffWaitStrategy withLock(long var0, long var2, TimeUnit var4) {
      return new PhasedBackoffWaitStrategy(var0, var2, var4, new BlockingWaitStrategy());
   }

   public static PhasedBackoffWaitStrategy withLiteLock(long var0, long var2, TimeUnit var4) {
      return new PhasedBackoffWaitStrategy(var0, var2, var4, new LiteBlockingWaitStrategy());
   }

   public static PhasedBackoffWaitStrategy withSleep(long var0, long var2, TimeUnit var4) {
      return new PhasedBackoffWaitStrategy(var0, var2, var4, new SleepingWaitStrategy(0));
   }

   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException, TimeoutException {
      long var8 = 0L;
      int var10 = 10000;

      long var6;
      while ((var6 = var4.get()) < var1) {
         if (0 == --var10) {
            if (0L == var8) {
               var8 = System.nanoTime();
            } else {
               long var11 = System.nanoTime() - var8;
               if (var11 > this.yieldTimeoutNanos) {
                  return this.fallbackStrategy.waitFor(var1, var3, var4, var5);
               }

               if (var11 > this.spinTimeoutNanos) {
                  Thread.yield();
               }
            }

            var10 = 10000;
         }
      }

      return var6;
   }

   @Override
   public void signalAllWhenBlocking() {
      this.fallbackStrategy.signalAllWhenBlocking();
   }
}
