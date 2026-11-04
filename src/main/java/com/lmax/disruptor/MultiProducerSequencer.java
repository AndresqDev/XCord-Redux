package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public final class MultiProducerSequencer extends AbstractSequencer {
   private static final Unsafe UNSAFE = Util.getUnsafe();
   private static final long BASE = (long)UNSAFE.arrayBaseOffset(int[].class);
   private static final long SCALE = (long)UNSAFE.arrayIndexScale(int[].class);
   private final Sequence gatingSequenceCache = new Sequence(-1L);
   private final int[] availableBuffer;
   private final int indexMask;
   private final int indexShift;

   public MultiProducerSequencer(int var1, WaitStrategy var2) {
      super(var1, var2);
      this.availableBuffer = new int[var1];
      this.indexMask = var1 - 1;
      this.indexShift = Util.log2(var1);
      this.initialiseAvailableBuffer();
   }

   @Override
   public boolean hasAvailableCapacity(int var1) {
      return this.hasAvailableCapacity(this.gatingSequences, var1, this.cursor.get());
   }

   private boolean hasAvailableCapacity(Sequence[] var1, int var2, long var3) {
      long var5 = var3 + (long)var2 - (long)this.bufferSize;
      long var7 = this.gatingSequenceCache.get();
      if (var5 > var7 || var7 > var3) {
         long var9 = Util.getMinimumSequence(var1, var3);
         this.gatingSequenceCache.set(var9);
         if (var5 > var9) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void claim(long var1) {
      this.cursor.set(var1);
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
         while (true) {
            long var2 = this.cursor.get();
            long var4 = var2 + (long)var1;
            long var6 = var4 - (long)this.bufferSize;
            long var8 = this.gatingSequenceCache.get();
            if (var6 <= var8 && var8 <= var2) {
               if (this.cursor.compareAndSet(var2, var4)) {
                  return var4;
               }
            } else {
               long var10 = Util.getMinimumSequence(this.gatingSequences, var2);
               if (var6 > var10) {
                  LockSupport.parkNanos(1L);
               } else {
                  this.gatingSequenceCache.set(var10);
               }
            }
         }
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
      } else {
         long var2;
         long var4;
         do {
            var2 = this.cursor.get();
            var4 = var2 + (long)var1;
            if (!this.hasAvailableCapacity(this.gatingSequences, var1, var2)) {
               throw InsufficientCapacityException.INSTANCE;
            }
         } while (!this.cursor.compareAndSet(var2, var4));

         return var4;
      }
   }

   @Override
   public long remainingCapacity() {
      long var1 = Util.getMinimumSequence(this.gatingSequences, this.cursor.get());
      long var3 = this.cursor.get();
      return (long)this.getBufferSize() - (var3 - var1);
   }

   private void initialiseAvailableBuffer() {
      for (int var1 = this.availableBuffer.length - 1; var1 != 0; var1--) {
         this.setAvailableBufferValue(var1, -1);
      }

      this.setAvailableBufferValue(0, -1);
   }

   @Override
   public void publish(long var1) {
      this.setAvailable(var1);
      this.waitStrategy.signalAllWhenBlocking();
   }

   @Override
   public void publish(long var1, long var3) {
      for (long var5 = var1; var5 <= var3; var5++) {
         this.setAvailable(var5);
      }

      this.waitStrategy.signalAllWhenBlocking();
   }

   private void setAvailable(long var1) {
      this.setAvailableBufferValue(this.calculateIndex(var1), this.calculateAvailabilityFlag(var1));
   }

   private void setAvailableBufferValue(int var1, int var2) {
      long var3 = (long)var1 * SCALE + BASE;
      UNSAFE.putOrderedInt(this.availableBuffer, var3, var2);
   }

   @Override
   public boolean isAvailable(long var1) {
      int var3 = this.calculateIndex(var1);
      int var4 = this.calculateAvailabilityFlag(var1);
      long var5 = (long)var3 * SCALE + BASE;
      return UNSAFE.getIntVolatile(this.availableBuffer, var5) == var4;
   }

   @Override
   public long getHighestPublishedSequence(long var1, long var3) {
      for (long var5 = var1; var5 <= var3; var5++) {
         if (!this.isAvailable(var5)) {
            return var5 - 1L;
         }
      }

      return var3;
   }

   private int calculateAvailabilityFlag(long var1) {
      return (int)(var1 >>> this.indexShift);
   }

   private int calculateIndex(long var1) {
      return (int)var1 & this.indexMask;
   }
}
