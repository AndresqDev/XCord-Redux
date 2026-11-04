package com.lmax.disruptor;

public final class YieldingWaitStrategy implements WaitStrategy {
   private static final int SPIN_TRIES = 100;

   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException {
      int var8 = 100;

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
      if (0 == var2) {
         Thread.yield();
      } else {
         var2--;
      }

      return var2;
   }
}
