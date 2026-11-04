package com.lmax.disruptor;

public interface WaitStrategy {
   long waitFor(long var1, Sequence var3, Sequence var4, SequenceBarrier var5) throws AlertException, InterruptedException, TimeoutException;

   void signalAllWhenBlocking();
}
