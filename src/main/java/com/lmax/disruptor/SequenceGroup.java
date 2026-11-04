package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public final class SequenceGroup extends Sequence {
   private static final AtomicReferenceFieldUpdater<SequenceGroup, Sequence[]> SEQUENCE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      SequenceGroup.class, Sequence[].class, "sequences"
   );
   private volatile Sequence[] sequences = new Sequence[0];

   public SequenceGroup() {
      super(-1L);
   }

   @Override
   public long get() {
      return Util.getMinimumSequence(this.sequences);
   }

   @Override
   public void set(long var1) {
      Sequence[] var3 = this.sequences;

      for (Sequence var7 : var3) {
         var7.set(var1);
      }
   }

   public void add(Sequence var1) {
      Sequence[] var2;
      Sequence[] var3;
      do {
         var2 = this.sequences;
         int var4 = var2.length;
         var3 = new Sequence[var4 + 1];
         System.arraycopy(var2, 0, var3, 0, var4);
         var3[var4] = var1;
      } while (!SEQUENCE_UPDATER.compareAndSet(this, var2, var3));
   }

   public boolean remove(Sequence var1) {
      return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, var1);
   }

   public int size() {
      return this.sequences.length;
   }

   public void addWhileRunning(Cursored var1, Sequence var2) {
      SequenceGroups.addSequences(this, SEQUENCE_UPDATER, var1, var2);
   }
}
