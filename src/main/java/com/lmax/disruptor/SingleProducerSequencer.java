package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.locks.LockSupport;

public final class SingleProducerSequencer extends SingleProducerSequencerFields {
   protected long p1;
   protected long p2;
   protected long p3;
   protected long p4;
   protected long p5;
   protected long p6;
   protected long p7;

   public SingleProducerSequencer(int var1, WaitStrategy var2) {
      super(var1, var2);
   }

   @Override
   public boolean hasAvailableCapacity(int var1) {
      return this.hasAvailableCapacity(var1, false);
   }

   private boolean hasAvailableCapacity(int var1, boolean var2) {
      long var3 = this.nextValue;
      long var5 = var3 + (long)var1 - (long)this.bufferSize;
      long var7 = this.cachedValue;
      if (var5 > var7 || var7 > var3) {
         if (var2) {
            this.cursor.setVolatile(var3);
         }

         long var9 = Util.getMinimumSequence(this.gatingSequences, var3);
         this.cachedValue = var9;
         if (var5 > var9) {
            return false;
         }
      }

      return true;
   }

   @Override
   public long next() {
      return this.next(1);
   }

   @Override
   public long next(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("n must be > 0");
      } else {
         long var2 = this.nextValue;
         long var4 = var2 + (long)var1;
         long var6 = var4 - (long)this.bufferSize;
         long var8 = this.cachedValue;
         if (var6 > var8 || var8 > var2) {
            this.cursor.setVolatile(var2);

            long var10;
            while (var6 > (var10 = Util.getMinimumSequence(this.gatingSequences, var2))) {
               LockSupport.parkNanos(1L);
            }

            this.cachedValue = var10;
         }

         this.nextValue = var4;
         return var4;
      }
   }

   @Override
   public long tryNext() throws InsufficientCapacityException {
      return this.tryNext(1);
   }

   @Override
   public long tryNext(int var1) throws InsufficientCapacityException {
      if (var1 < 1) {
         throw new IllegalArgumentException("n must be > 0");
      } else if (!this.hasAvailableCapacity(var1, true)) {
         throw InsufficientCapacityException.INSTANCE;
      } else {
         return this.nextValue += (long)var1;
      }
   }

   @Override
   public long remainingCapacity() {
      long var1 = this.nextValue;
      long var3 = Util.getMinimumSequence(this.gatingSequences, var1);
      return (long)this.getBufferSize() - (var1 - var3);
   }

   @Override
   public void claim(long var1) {
      this.nextValue = var1;
   }

   @Override
   public void publish(long var1) {
      this.cursor.set(var1);
      this.waitStrategy.signalAllWhenBlocking();
   }

   @Override
   public void publish(long var1, long var3) {
      this.publish(var3);
   }

   @Override
   public boolean isAvailable(long var1) {
      return var1 <= this.cursor.get();
   }

   @Override
   public long getHighestPublishedSequence(long var1, long var3) {
      return var3;
   }
}
