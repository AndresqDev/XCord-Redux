package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

public final class NoOpEventProcessor implements EventProcessor {
   private final NoOpEventProcessor.SequencerFollowingSequence sequence;
   private final AtomicBoolean running = new AtomicBoolean(false);

   public NoOpEventProcessor(RingBuffer<?> var1) {
      this.sequence = new NoOpEventProcessor.SequencerFollowingSequence(var1);
   }

   @Override
   public Sequence getSequence() {
      return this.sequence;
   }

   @Override
   public void halt() {
      this.running.set(false);
   }

   @Override
   public boolean isRunning() {
      return this.running.get();
   }

   @Override
   public void run() {
      if (!this.running.compareAndSet(false, true)) {
         throw new IllegalStateException("Thread is already running");
      }
   }

   private static final class SequencerFollowingSequence extends Sequence {
      private final RingBuffer<?> sequencer;

      private SequencerFollowingSequence(RingBuffer<?> var1) {
         super(-1L);
         this.sequencer = var1;
      }

      @Override
      public long get() {
         return this.sequencer.getCursor();
      }
   }
}
