package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class AbstractSequencer implements Sequencer {
   private static final AtomicReferenceFieldUpdater<AbstractSequencer, Sequence[]> SEQUENCE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      AbstractSequencer.class, Sequence[].class, "gatingSequences"
   );
   protected final int bufferSize;
   protected final WaitStrategy waitStrategy;
   protected final Sequence cursor = new Sequence(-1L);
   protected volatile Sequence[] gatingSequences = new Sequence[0];

   public AbstractSequencer(int var1, WaitStrategy var2) {
      if (var1 < 1) {
         throw new IllegalArgumentException("bufferSize must not be less than 1");
      } else if (Integer.bitCount(var1) != 1) {
         throw new IllegalArgumentException("bufferSize must be a power of 2");
      } else {
         this.bufferSize = var1;
         this.waitStrategy = var2;
      }
   }

   @Override
   public final long getCursor() {
      return this.cursor.get();
   }

   @Override
   public final int getBufferSize() {
      return this.bufferSize;
   }

   @Override
   public final void addGatingSequences(Sequence... var1) {
      SequenceGroups.addSequences(this, SEQUENCE_UPDATER, this, var1);
   }

   @Override
   public boolean removeGatingSequence(Sequence var1) {
      return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, var1);
   }

   @Override
   public long getMinimumSequence() {
      return Util.getMinimumSequence(this.gatingSequences, this.cursor.get());
   }

   @Override
   public SequenceBarrier newBarrier(Sequence... var1) {
      return new ProcessingSequenceBarrier(this, this.waitStrategy, this.cursor, var1);
   }

   @Override
   public <T> EventPoller<T> newPoller(DataProvider<T> var1, Sequence... var2) {
      return EventPoller.newInstance(var1, this, new Sequence(), this.cursor, var2);
   }

   @Override
   public String toString() {
      return "AbstractSequencer{waitStrategy="
         + this.waitStrategy
         + ", cursor="
         + this.cursor
         + ", gatingSequences="
         + Arrays.toString((Object[])this.gatingSequences)
         + '}';
   }
}
