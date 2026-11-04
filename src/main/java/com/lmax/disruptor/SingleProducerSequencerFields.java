package com.lmax.disruptor;

abstract class SingleProducerSequencerFields extends SingleProducerSequencerPad {
   long nextValue = -1L;
   long cachedValue = -1L;

   SingleProducerSequencerFields(int var1, WaitStrategy var2) {
      super(var1, var2);
   }
}
