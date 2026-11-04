package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import java.util.concurrent.Executor;

class EventProcessorInfo<T> implements ConsumerInfo {
   private final EventProcessor eventprocessor;
   private final EventHandler<? super T> handler;
   private final SequenceBarrier barrier;
   private boolean endOfChain = true;

   EventProcessorInfo(EventProcessor var1, EventHandler<? super T> var2, SequenceBarrier var3) {
      this.eventprocessor = var1;
      this.handler = var2;
      this.barrier = var3;
   }

   public EventProcessor getEventProcessor() {
      return this.eventprocessor;
   }

   @Override
   public Sequence[] getSequences() {
      return new Sequence[]{this.eventprocessor.getSequence()};
   }

   public EventHandler<? super T> getHandler() {
      return this.handler;
   }

   @Override
   public SequenceBarrier getBarrier() {
      return this.barrier;
   }

   @Override
   public boolean isEndOfChain() {
      return this.endOfChain;
   }

   @Override
   public void start(Executor var1) {
      var1.execute(this.eventprocessor);
   }

   @Override
   public void halt() {
      this.eventprocessor.halt();
   }

   @Override
   public void markAsUsedInBarrier() {
      this.endOfChain = false;
   }

   @Override
   public boolean isRunning() {
      return this.eventprocessor.isRunning();
   }
}
