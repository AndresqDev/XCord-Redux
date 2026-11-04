package com.lmax.disruptor;

public interface Sequencer extends Cursored, Sequenced {
   long INITIAL_CURSOR_VALUE = -1L;

   void claim(long var1);

   boolean isAvailable(long var1);

   void addGatingSequences(Sequence... var1);

   boolean removeGatingSequence(Sequence var1);

   SequenceBarrier newBarrier(Sequence... var1);

   long getMinimumSequence();

   long getHighestPublishedSequence(long var1, long var3);

   <T> EventPoller<T> newPoller(DataProvider<T> var1, Sequence... var2);
}
