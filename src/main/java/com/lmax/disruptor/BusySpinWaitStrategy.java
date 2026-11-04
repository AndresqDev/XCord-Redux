package com.lmax.disruptor;

import com.lmax.disruptor.util.ThreadHints;

public final class BusySpinWaitStrategy implements WaitStrategy {
   @Override
   public long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException {
      long var6;
      while ((var6 = var4.get()) < var1) {
         var5.checkAlert();
         ThreadHints.onSpinWait();
      }

      return var6;
   }

   @Override
   public void signalAllWhenBlocking() {
   }
}
