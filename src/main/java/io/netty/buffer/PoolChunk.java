package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

final class PoolChunk<T> implements PoolChunkMetric {
   private static final int SIZE_BIT_LENGTH = 15;
   private static final int INUSED_BIT_LENGTH = 1;
   private static final int SUBPAGE_BIT_LENGTH = 1;
   private static final int BITMAP_IDX_BIT_LENGTH = 32;
   static final int IS_SUBPAGE_SHIFT = 32;
   static final int IS_USED_SHIFT = 33;
   static final int SIZE_SHIFT = 34;
   static final int RUN_OFFSET_SHIFT = 49;
   final PoolArena<T> arena;
   final Object base;
   final T memory;
   final boolean unpooled;
   private final LongLongHashMap runsAvailMap;
   private final LongPriorityQueue[] runsAvail;
   private final ReentrantLock runsAvailLock;
   private final PoolSubpage<T>[] subpages;
   private final LongCounter pinnedBytes = PlatformDependent.newLongCounter();
   private final int pageSize;
   private final int pageShifts;
   private final int chunkSize;
   private final Deque<ByteBuffer> cachedNioBuffers;
   int freeBytes;
   PoolChunkList<T> parent;
   PoolChunk<T> prev;
   PoolChunk<T> next;

   PoolChunk(PoolArena<T> var1, Object var2, T var3, int var4, int var5, int var6, int var7) {
      this.unpooled = false;
      this.arena = var1;
      this.base = var2;
      this.memory = (T)var3;
      this.pageSize = var4;
      this.pageShifts = var5;
      this.chunkSize = var6;
      this.freeBytes = var6;
      this.runsAvail = newRunsAvailqueueArray(var7);
      this.runsAvailLock = new ReentrantLock();
      this.runsAvailMap = new LongLongHashMap(-1L);
      this.subpages = new PoolSubpage[var6 >> var5];
      int var8 = var6 >> var5;
      long var9 = (long)var8 << 34;
      this.insertAvailRun(0, var8, var9);
      this.cachedNioBuffers = new ArrayDeque<>(8);
   }

   PoolChunk(PoolArena<T> var1, Object var2, T var3, int var4) {
      this.unpooled = true;
      this.arena = var1;
      this.base = var2;
      this.memory = (T)var3;
      this.pageSize = 0;
      this.pageShifts = 0;
      this.runsAvailMap = null;
      this.runsAvail = null;
      this.runsAvailLock = null;
      this.subpages = null;
      this.chunkSize = var4;
      this.cachedNioBuffers = null;
   }

   private static LongPriorityQueue[] newRunsAvailqueueArray(int var0) {
      LongPriorityQueue[] var1 = new LongPriorityQueue[var0];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = new LongPriorityQueue();
      }

      return var1;
   }

   private void insertAvailRun(int var1, int var2, long var3) {
      int var5 = this.arena.pages2pageIdxFloor(var2);
      LongPriorityQueue var6 = this.runsAvail[var5];
      var6.offer(var3);
      this.insertAvailRun0(var1, var3);
      if (var2 > 1) {
         this.insertAvailRun0(lastPage(var1, var2), var3);
      }
   }

   private void insertAvailRun0(int var1, long var2) {
      long var4 = this.runsAvailMap.put((long)var1, var2);

      assert var4 == -1L;
   }

   private void removeAvailRun(long var1) {
      int var3 = this.arena.pages2pageIdxFloor(runPages(var1));
      LongPriorityQueue var4 = this.runsAvail[var3];
      this.removeAvailRun(var4, var1);
   }

   private void removeAvailRun(LongPriorityQueue var1, long var2) {
      var1.remove(var2);
      int var4 = runOffset(var2);
      int var5 = runPages(var2);
      this.runsAvailMap.remove((long)var4);
      if (var5 > 1) {
         this.runsAvailMap.remove((long)lastPage(var4, var5));
      }
   }

   private static int lastPage(int var0, int var1) {
      return var0 + var1 - 1;
   }

   private long getAvailRunByOffset(int var1) {
      return this.runsAvailMap.get((long)var1);
   }

   @Override
   public int usage() {
      this.arena.lock();

      int var1;
      try {
         var1 = this.freeBytes;
      } finally {
         this.arena.unlock();
      }

      return this.usage(var1);
   }

   private int usage(int var1) {
      if (var1 == 0) {
         return 100;
      } else {
         int var2 = (int)((long)var1 * 100L / (long)this.chunkSize);
         return var2 == 0 ? 99 : 100 - var2;
      }
   }

   boolean allocate(PooledByteBuf<T> var1, int var2, int var3, PoolThreadCache var4) {
      long var5;
      if (var3 <= this.arena.smallMaxSizeIdx) {
         var5 = this.allocateSubpage(var3);
         if (var5 < 0L) {
            return false;
         }

         assert isSubpage(var5);
      } else {
         int var7 = this.arena.sizeIdx2size(var3);
         var5 = this.allocateRun(var7);
         if (var5 < 0L) {
            return false;
         }

         assert !isSubpage(var5);
      }

      ByteBuffer var8 = this.cachedNioBuffers != null ? this.cachedNioBuffers.pollLast() : null;
      this.initBuf(var1, var8, var5, var2, var4);
      return true;
   }

   private long allocateRun(int var1) {
      int var2 = var1 >> this.pageShifts;
      int var3 = this.arena.pages2pageIdx(var2);
      this.runsAvailLock.lock();

      long var9;
      try {
         int var4 = this.runFirstBestFit(var3);
         if (var4 == -1) {
            return -1L;
         }

         LongPriorityQueue var5 = this.runsAvail[var4];
         long var6 = var5.poll();

         assert var6 != -1L && !isUsed(var6) : "invalid handle: " + var6;

         this.removeAvailRun(var5, var6);
         if (var6 != -1L) {
            var6 = this.splitLargeRun(var6, var2);
         }

         int var8 = runSize(this.pageShifts, var6);
         this.freeBytes -= var8;
         var9 = var6;
      } finally {
         this.runsAvailLock.unlock();
      }

      return var9;
   }

   private int calculateRunSize(int var1) {
      int var2 = 1 << this.pageShifts - 4;
      int var3 = 0;
      int var5 = this.arena.sizeIdx2size(var1);

      int var4;
      do {
         var3 += this.pageSize;
         var4 = var3 / var5;
      } while (var4 < var2 && var3 != var4 * var5);

      while (var4 > var2) {
         var3 -= this.pageSize;
         var4 = var3 / var5;
      }

      assert var4 > 0;

      assert var3 <= this.chunkSize;

      assert var3 >= var5;

      return var3;
   }

   private int runFirstBestFit(int var1) {
      if (this.freeBytes == this.chunkSize) {
         return this.arena.nPSizes - 1;
      } else {
         for (int var2 = var1; var2 < this.arena.nPSizes; var2++) {
            LongPriorityQueue var3 = this.runsAvail[var2];
            if (var3 != null && !var3.isEmpty()) {
               return var2;
            }
         }

         return -1;
      }
   }

   private long splitLargeRun(long var1, int var3) {
      assert var3 > 0;

      int var4 = runPages(var1);

      assert var3 <= var4;

      int var5 = var4 - var3;
      if (var5 > 0) {
         int var6 = runOffset(var1);
         int var7 = var6 + var3;
         long var8 = toRunHandle(var7, var5, 0);
         this.insertAvailRun(var7, var5, var8);
         return toRunHandle(var6, var3, 1);
      } else {
         return var1 | 8589934592L;
      }
   }

   private long allocateSubpage(int var1) {
      PoolSubpage var2 = this.arena.findSubpagePoolHead(var1);
      var2.lock();

      long var6;
      try {
         int var3 = this.calculateRunSize(var1);
         long var4 = this.allocateRun(var3);
         if (var4 >= 0L) {
            int var14 = runOffset(var4);

            assert this.subpages[var14] == null;

            int var7 = this.arena.sizeIdx2size(var1);
            PoolSubpage var8 = new PoolSubpage<>(var2, this, this.pageShifts, var14, runSize(this.pageShifts, var4), var7);
            this.subpages[var14] = var8;
            return var8.allocate();
         }

         var6 = -1L;
      } finally {
         var2.unlock();
      }

      return var6;
   }

   void free(long var1, int var3, ByteBuffer var4) {
      int var5 = runSize(this.pageShifts, var1);
      if (isSubpage(var1)) {
         int var6 = this.arena.size2SizeIdx(var3);
         PoolSubpage var7 = this.arena.findSubpagePoolHead(var6);
         int var8 = runOffset(var1);
         PoolSubpage var9 = this.subpages[var8];

         assert var9 != null && var9.doNotDestroy;

         var7.lock();

         try {
            if (var9.free(var7, bitmapIdx(var1))) {
               return;
            }

            assert !var9.doNotDestroy;

            this.subpages[var8] = null;
         } finally {
            var7.unlock();
         }
      }

      this.runsAvailLock.lock();

      try {
         long var18 = this.collapseRuns(var1);
         var18 &= -8589934593L;
         var18 &= -4294967297L;
         this.insertAvailRun(runOffset(var18), runPages(var18), var18);
         this.freeBytes += var5;
      } finally {
         this.runsAvailLock.unlock();
      }

      if (var4 != null && this.cachedNioBuffers != null && this.cachedNioBuffers.size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
         this.cachedNioBuffers.offer(var4);
      }
   }

   private long collapseRuns(long var1) {
      return this.collapseNext(this.collapsePast(var1));
   }

   private long collapsePast(long var1) {
      while (true) {
         int var3 = runOffset(var1);
         int var4 = runPages(var1);
         long var5 = this.getAvailRunByOffset(var3 - 1);
         if (var5 == -1L) {
            return var1;
         }

         int var7 = runOffset(var5);
         int var8 = runPages(var5);
         if (var5 == var1 || var7 + var8 != var3) {
            return var1;
         }

         this.removeAvailRun(var5);
         var1 = toRunHandle(var7, var8 + var4, 0);
      }
   }

   private long collapseNext(long var1) {
      while (true) {
         int var3 = runOffset(var1);
         int var4 = runPages(var1);
         long var5 = this.getAvailRunByOffset(var3 + var4);
         if (var5 == -1L) {
            return var1;
         }

         int var7 = runOffset(var5);
         int var8 = runPages(var5);
         if (var5 == var1 || var3 + var4 != var7) {
            return var1;
         }

         this.removeAvailRun(var5);
         var1 = toRunHandle(var3, var4 + var8, 0);
      }
   }

   private static long toRunHandle(int var0, int var1, int var2) {
      return (long)var0 << 49 | (long)var1 << 34 | (long)var2 << 33;
   }

   void initBuf(PooledByteBuf<T> var1, ByteBuffer var2, long var3, int var5, PoolThreadCache var6) {
      if (isSubpage(var3)) {
         this.initBufWithSubpage(var1, var2, var3, var5, var6);
      } else {
         int var7 = runSize(this.pageShifts, var3);
         var1.init(this, var2, var3, runOffset(var3) << this.pageShifts, var5, var7, this.arena.parent.threadCache());
      }
   }

   void initBufWithSubpage(PooledByteBuf<T> var1, ByteBuffer var2, long var3, int var5, PoolThreadCache var6) {
      int var7 = runOffset(var3);
      int var8 = bitmapIdx(var3);
      PoolSubpage var9 = this.subpages[var7];

      assert var9.doNotDestroy;

      assert var5 <= var9.elemSize : var5 + "<=" + var9.elemSize;

      int var10 = (var7 << this.pageShifts) + var8 * var9.elemSize;
      var1.init(this, var2, var3, var10, var5, var9.elemSize, var6);
   }

   void incrementPinnedMemory(int var1) {
      assert var1 > 0;

      this.pinnedBytes.add((long)var1);
   }

   void decrementPinnedMemory(int var1) {
      assert var1 > 0;

      this.pinnedBytes.add((long)(-var1));
   }

   @Override
   public int chunkSize() {
      return this.chunkSize;
   }

   @Override
   public int freeBytes() {
      this.arena.lock();

      int var1;
      try {
         var1 = this.freeBytes;
      } finally {
         this.arena.unlock();
      }

      return var1;
   }

   public int pinnedBytes() {
      return (int)this.pinnedBytes.value();
   }

   @Override
   public String toString() {
      this.arena.lock();

      int var1;
      try {
         var1 = this.freeBytes;
      } finally {
         this.arena.unlock();
      }

      return "Chunk("
         + Integer.toHexString(System.identityHashCode(this))
         + ": "
         + this.usage(var1)
         + "%, "
         + (this.chunkSize - var1)
         + '/'
         + this.chunkSize
         + ')';
   }

   void destroy() {
      this.arena.destroyChunk(this);
   }

   static int runOffset(long var0) {
      return (int)(var0 >> 49);
   }

   static int runSize(int var0, long var1) {
      return runPages(var1) << var0;
   }

   static int runPages(long var0) {
      return (int)(var0 >> 34 & 32767L);
   }

   static boolean isUsed(long var0) {
      return (var0 >> 33 & 1L) == 1L;
   }

   static boolean isRun(long var0) {
      return !isSubpage(var0);
   }

   static boolean isSubpage(long var0) {
      return (var0 >> 32 & 1L) == 1L;
   }

   static int bitmapIdx(long var0) {
      return (int)var0;
   }
}
