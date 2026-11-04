package com.lmax.disruptor;

final class ProcessingSequenceBarrier implements SequenceBarrier {
   private final WaitStrategy waitStrategy;
   private final Sequence dependentSequence;
   private volatile boolean alerted = false;
   private final Sequence cursorSequence;
   private final Sequencer sequencer;

   ProcessingSequenceBarrier(Sequencer var1, WaitStrategy var2, Sequence var3, Sequence[] var4) {
      this.sequencer = var1;
      this.waitStrategy = var2;
      this.cursorSequence = var3;
      if (0 == var4.length) {
         this.dependentSequence = var3;
      } else {
         this.dependentSequence = new FixedSequenceGroup(var4);
      }
   }

   @Override
   public long waitFor(long var1) throws AlertException, InterruptedException, TimeoutException {
      this.checkAlert();
      long var3 = this.waitStrategy.waitFor(var1, this.cursorSequence, this.dependentSequence, this);
      return var3 < var1 ? var3 : this.sequencer.getHighestPublishedSequence(var1, var3);
   }

   @Override
   public long getCursor() {
      return this.dependentSequence.get();
   }

   @Override
   public boolean isAlerted() {
      return this.alerted;
   }

   @Override
   public void alert() {
      this.alerted = true;
      this.waitStrategy.signalAllWhenBlocking();
   }

   @Override
   public void clearAlert() {
      this.alerted = false;
   }

   @Override
   public void checkAlert() throws AlertException {
      if (this.alerted) {
         throw AlertException.INSTANCE;
      }
   }
}
