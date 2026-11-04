package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.Arrays;

public final class FixedSequenceGroup extends Sequence {
   private final Sequence[] sequences;

   public FixedSequenceGroup(Sequence[] var1) {
      this.sequences = Arrays.copyOf(var1, var1.length);
   }

   @Override
   public long get() {
      return Util.getMinimumSequence(this.sequences);
   }

   @Override
   public String toString() {
      return Arrays.toString((Object[])this.sequences);
   }

   @Override
   public void set(long var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean compareAndSet(long var1, long var3) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long incrementAndGet() {
      throw new UnsupportedOperationException();
   }

   @Override
   public long addAndGet(long var1) {
      throw new UnsupportedOperationException();
   }
}
