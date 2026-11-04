package io.netty.buffer;

import java.util.concurrent.locks.ReentrantLock;

final class PoolSubpage<T> implements PoolSubpageMetric {
   final PoolChunk<T> chunk;
   final int elemSize;
   private final int pageShifts;
   private final int runOffset;
   private final int runSize;
   private final long[] bitmap;
   PoolSubpage<T> prev;
   PoolSubpage<T> next;
   boolean doNotDestroy;
   private int maxNumElems;
   private int bitmapLength;
   private int nextAvail;
   private int numAvail;
   private final ReentrantLock lock = new ReentrantLock();

   PoolSubpage() {
      this.chunk = null;
      this.pageShifts = -1;
      this.runOffset = -1;
      this.elemSize = -1;
      this.runSize = -1;
      this.bitmap = null;
   }

   PoolSubpage(PoolSubpage<T> var1, PoolChunk<T> var2, int var3, int var4, int var5, int var6) {
      this.chunk = var2;
      this.pageShifts = var3;
      this.runOffset = var4;
      this.runSize = var5;
      this.elemSize = var6;
      this.bitmap = new long[var5 >>> 10];
      this.doNotDestroy = true;
      if (var6 != 0) {
         this.maxNumElems = this.numAvail = var5 / var6;
         this.nextAvail = 0;
         this.bitmapLength = this.maxNumElems >>> 6;
         if ((this.maxNumElems & 63) != 0) {
            this.bitmapLength++;
         }

         for (int var7 = 0; var7 < this.bitmapLength; var7++) {
            this.bitmap[var7] = 0L;
         }
      }

      this.addToPool(var1);
   }

   long allocate() {
      if (this.numAvail != 0 && this.doNotDestroy) {
         int var1 = this.getNextAvail();
         int var2 = var1 >>> 6;
         int var3 = var1 & 63;

         assert (this.bitmap[var2] >>> var3 & 1L) == 0L;

         this.bitmap[var2] = this.bitmap[var2] | 1L << var3;
         if (--this.numAvail == 0) {
            this.removeFromPool();
         }

         return this.toHandle(var1);
      } else {
         return -1L;
      }
   }

   boolean free(PoolSubpage<T> var1, int var2) {
      if (this.elemSize == 0) {
         return true;
      } else {
         int var3 = var2 >>> 6;
         int var4 = var2 & 63;

         assert (this.bitmap[var3] >>> var4 & 1L) != 0L;

         this.bitmap[var3] = this.bitmap[var3] ^ 1L << var4;
         this.setNextAvail(var2);
         if (this.numAvail++ == 0) {
            this.addToPool(var1);
            if (this.maxNumElems > 1) {
               return true;
            }
         }

         if (this.numAvail != this.maxNumElems) {
            return true;
         } else if (this.prev == this.next) {
            return true;
         } else {
            this.doNotDestroy = false;
            this.removeFromPool();
            return false;
         }
      }
   }

   private void addToPool(PoolSubpage<T> var1) {
      assert this.prev == null && this.next == null;

      this.prev = var1;
      this.next = var1.next;
      this.next.prev = this;
      var1.next = this;
   }

   private void removeFromPool() {
      assert this.prev != null && this.next != null;

      this.prev.next = this.next;
      this.next.prev = this.prev;
      this.next = null;
      this.prev = null;
   }

   private void setNextAvail(int var1) {
      this.nextAvail = var1;
   }

   private int getNextAvail() {
      int var1 = this.nextAvail;
      if (var1 >= 0) {
         this.nextAvail = -1;
         return var1;
      } else {
         return this.findNextAvail();
      }
   }

   private int findNextAvail() {
      long[] var1 = this.bitmap;
      int var2 = this.bitmapLength;

      for (int var3 = 0; var3 < var2; var3++) {
         long var4 = var1[var3];
         if (~var4 != 0L) {
            return this.findNextAvail0(var3, var4);
         }
      }

      return -1;
   }

   private int findNextAvail0(int var1, long var2) {
      int var4 = this.maxNumElems;
      int var5 = var1 << 6;

      for (int var6 = 0; var6 < 64; var6++) {
         if ((var2 & 1L) == 0L) {
            int var7 = var5 | var6;
            if (var7 < var4) {
               return var7;
            }
            break;
         }

         var2 >>>= 1;
      }

      return -1;
   }

   private long toHandle(int var1) {
      int var2 = this.runSize >> this.pageShifts;
      return (long)this.runOffset << 49 | (long)var2 << 34 | 8589934592L | 4294967296L | (long)var1;
   }

   @Override
   public String toString() {
      boolean var1;
      int var2;
      int var3;
      int var4;
      if (this.chunk == null) {
         var1 = true;
         var2 = 0;
         var3 = 0;
         var4 = -1;
      } else {
         this.chunk.arena.lock();

         try {
            if (!this.doNotDestroy) {
               var1 = false;
               var4 = -1;
               var3 = -1;
               var2 = -1;
            } else {
               var1 = true;
               var2 = this.maxNumElems;
               var3 = this.numAvail;
               var4 = this.elemSize;
            }
         } finally {
            this.chunk.arena.unlock();
         }
      }

      return !var1
         ? "(" + this.runOffset + ": not in use)"
         : "(" + this.runOffset + ": " + (var2 - var3) + '/' + var2 + ", offset: " + this.runOffset + ", length: " + this.runSize + ", elemSize: " + var4 + ')';
   }

   @Override
   public int maxNumElements() {
      if (this.chunk == null) {
         return 0;
      } else {
         this.chunk.arena.lock();

         int var1;
         try {
            var1 = this.maxNumElems;
         } finally {
            this.chunk.arena.unlock();
         }

         return var1;
      }
   }

   @Override
   public int numAvailable() {
      if (this.chunk == null) {
         return 0;
      } else {
         this.chunk.arena.lock();

         int var1;
         try {
            var1 = this.numAvail;
         } finally {
            this.chunk.arena.unlock();
         }

         return var1;
      }
   }

   @Override
   public int elementSize() {
      if (this.chunk == null) {
         return -1;
      } else {
         this.chunk.arena.lock();

         int var1;
         try {
            var1 = this.elemSize;
         } finally {
            this.chunk.arena.unlock();
         }

         return var1;
      }
   }

   @Override
   public int pageSize() {
      return 1 << this.pageShifts;
   }

   void destroy() {
      if (this.chunk != null) {
         this.chunk.destroy();
      }
   }

   void lock() {
      this.lock.lock();
   }

   void unlock() {
      this.lock.unlock();
   }
}
