package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

abstract class PoolArena<T> extends SizeClasses implements PoolArenaMetric {
   static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
   final PooledByteBufAllocator parent;
   final int numSmallSubpagePools;
   final int directMemoryCacheAlignment;
   private final PoolSubpage<T>[] smallSubpagePools;
   private final PoolChunkList<T> q050;
   private final PoolChunkList<T> q025;
   private final PoolChunkList<T> q000;
   private final PoolChunkList<T> qInit;
   private final PoolChunkList<T> q075;
   private final PoolChunkList<T> q100;
   private final List<PoolChunkListMetric> chunkListMetrics;
   private long allocationsNormal;
   private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
   private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
   private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
   private long deallocationsSmall;
   private long deallocationsNormal;
   private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
   final AtomicInteger numThreadCaches = new AtomicInteger();
   private final ReentrantLock lock = new ReentrantLock();

   protected PoolArena(PooledByteBufAllocator var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.parent = var1;
      this.directMemoryCacheAlignment = var5;
      this.numSmallSubpagePools = this.nSubpages;
      this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);

      for (int var6 = 0; var6 < this.smallSubpagePools.length; var6++) {
         this.smallSubpagePools[var6] = this.newSubpagePoolHead();
      }

      this.q100 = new PoolChunkList<>(this, null, 100, Integer.MAX_VALUE, var4);
      this.q075 = new PoolChunkList<>(this, this.q100, 75, 100, var4);
      this.q050 = new PoolChunkList<>(this, this.q075, 50, 100, var4);
      this.q025 = new PoolChunkList<>(this, this.q050, 25, 75, var4);
      this.q000 = new PoolChunkList<>(this, this.q025, 1, 50, var4);
      this.qInit = new PoolChunkList<>(this, this.q000, Integer.MIN_VALUE, 25, var4);
      this.q100.prevList(this.q075);
      this.q075.prevList(this.q050);
      this.q050.prevList(this.q025);
      this.q025.prevList(this.q000);
      this.q000.prevList(null);
      this.qInit.prevList(this.qInit);
      ArrayList var7 = new ArrayList(6);
      var7.add(this.qInit);
      var7.add(this.q000);
      var7.add(this.q025);
      var7.add(this.q050);
      var7.add(this.q075);
      var7.add(this.q100);
      this.chunkListMetrics = Collections.unmodifiableList(var7);
   }

   private PoolSubpage<T> newSubpagePoolHead() {
      PoolSubpage var1 = new PoolSubpage();
      var1.prev = var1;
      var1.next = var1;
      return var1;
   }

   private PoolSubpage<T>[] newSubpagePoolArray(int var1) {
      return new PoolSubpage[var1];
   }

   abstract boolean isDirect();

   PooledByteBuf<T> allocate(PoolThreadCache var1, int var2, int var3) {
      PooledByteBuf var4 = this.newByteBuf(var3);
      this.allocate(var1, var4, var2);
      return var4;
   }

   private void allocate(PoolThreadCache var1, PooledByteBuf<T> var2, int var3) {
      int var4 = this.size2SizeIdx(var3);
      if (var4 <= this.smallMaxSizeIdx) {
         this.tcacheAllocateSmall(var1, var2, var3, var4);
      } else if (var4 < this.nSizes) {
         this.tcacheAllocateNormal(var1, var2, var3, var4);
      } else {
         int var5 = this.directMemoryCacheAlignment > 0 ? this.normalizeSize(var3) : var3;
         this.allocateHuge(var2, var5);
      }
   }

   private void tcacheAllocateSmall(PoolThreadCache var1, PooledByteBuf<T> var2, int var3, int var4) {
      if (!var1.allocateSmall(this, var2, var3, var4)) {
         PoolSubpage var5 = this.smallSubpagePools[var4];
         var5.lock();

         boolean var6;
         try {
            PoolSubpage var7 = var5.next;
            var6 = var7 == var5;
            if (!var6) {
               assert var7.doNotDestroy && var7.elemSize == this.sizeIdx2size(var4) : "doNotDestroy="
                  + var7.doNotDestroy
                  + ", elemSize="
                  + var7.elemSize
                  + ", sizeIdx="
                  + var4;

               long var8 = var7.allocate();

               assert var8 >= 0L;

               var7.chunk.initBufWithSubpage(var2, null, var8, var3, var1);
            }
         } finally {
            var5.unlock();
         }

         if (var6) {
            this.lock();

            try {
               this.allocateNormal(var2, var3, var4, var1);
            } finally {
               this.unlock();
            }
         }

         this.incSmallAllocation();
      }
   }

   private void tcacheAllocateNormal(PoolThreadCache var1, PooledByteBuf<T> var2, int var3, int var4) {
      if (!var1.allocateNormal(this, var2, var3, var4)) {
         this.lock();

         try {
            this.allocateNormal(var2, var3, var4, var1);
            this.allocationsNormal++;
         } finally {
            this.unlock();
         }
      }
   }

   private void allocateNormal(PooledByteBuf<T> var1, int var2, int var3, PoolThreadCache var4) {
      assert this.lock.isHeldByCurrentThread();

      if (!this.q050.allocate(var1, var2, var3, var4)
         && !this.q025.allocate(var1, var2, var3, var4)
         && !this.q000.allocate(var1, var2, var3, var4)
         && !this.qInit.allocate(var1, var2, var3, var4)
         && !this.q075.allocate(var1, var2, var3, var4)) {
         PoolChunk var5 = this.newChunk(this.pageSize, this.nPSizes, this.pageShifts, this.chunkSize);
         boolean var6 = var5.allocate(var1, var2, var3, var4);

         assert var6;

         this.qInit.add(var5);
      }
   }

   private void incSmallAllocation() {
      this.allocationsSmall.increment();
   }

   private void allocateHuge(PooledByteBuf<T> var1, int var2) {
      PoolChunk var3 = this.newUnpooledChunk(var2);
      this.activeBytesHuge.add((long)var3.chunkSize());
      var1.initUnpooled(var3, var2);
      this.allocationsHuge.increment();
   }

   void free(PoolChunk<T> var1, ByteBuffer var2, long var3, int var5, PoolThreadCache var6) {
      if (var1.unpooled) {
         int var7 = var1.chunkSize();
         this.destroyChunk(var1);
         this.activeBytesHuge.add((long)(-var7));
         this.deallocationsHuge.increment();
      } else {
         PoolArena.SizeClass var8 = sizeClass(var3);
         if (var6 != null && var6.add(this, var1, var2, var3, var5, var8)) {
            return;
         }

         this.freeChunk(var1, var3, var5, var8, var2, false);
      }
   }

   private static PoolArena.SizeClass sizeClass(long var0) {
      return PoolChunk.isSubpage(var0) ? PoolArena.SizeClass.Small : PoolArena.SizeClass.Normal;
   }

   void freeChunk(PoolChunk<T> var1, long var2, int var4, PoolArena.SizeClass var5, ByteBuffer var6, boolean var7) {
      this.lock();

      boolean var8;
      try {
         if (!var7) {
            switch (var5) {
               case Normal:
                  this.deallocationsNormal++;
                  break;
               case Small:
                  this.deallocationsSmall++;
                  break;
               default:
                  throw new Error();
            }
         }

         var8 = !var1.parent.free(var1, var2, var4, var6);
      } finally {
         this.unlock();
      }

      if (var8) {
         this.destroyChunk(var1);
      }
   }

   PoolSubpage<T> findSubpagePoolHead(int var1) {
      return this.smallSubpagePools[var1];
   }

   void reallocate(PooledByteBuf<T> var1, int var2, boolean var3) {
      assert var2 >= 0 && var2 <= var1.maxCapacity();

      int var4 = var1.length;
      if (var4 != var2) {
         PoolChunk var5 = var1.chunk;
         ByteBuffer var6 = var1.tmpNioBuf;
         long var7 = var1.handle;
         Object var9 = var1.memory;
         int var10 = var1.offset;
         int var11 = var1.maxLength;
         this.allocate(this.parent.threadCache(), var1, var2);
         int var12;
         if (var2 > var4) {
            var12 = var4;
         } else {
            var1.trimIndicesToCapacity(var2);
            var12 = var2;
         }

         this.memoryCopy((T)var9, var10, var1, var12);
         if (var3) {
            this.free(var5, var6, var7, var11, var1.cache);
         }
      }
   }

   @Override
   public int numThreadCaches() {
      return this.numThreadCaches.get();
   }

   @Override
   public int numTinySubpages() {
      return 0;
   }

   @Override
   public int numSmallSubpages() {
      return this.smallSubpagePools.length;
   }

   @Override
   public int numChunkLists() {
      return this.chunkListMetrics.size();
   }

   @Override
   public List<PoolSubpageMetric> tinySubpages() {
      return Collections.emptyList();
   }

   @Override
   public List<PoolSubpageMetric> smallSubpages() {
      return subPageMetricList(this.smallSubpagePools);
   }

   @Override
   public List<PoolChunkListMetric> chunkLists() {
      return this.chunkListMetrics;
   }

   private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] var0) {
      ArrayList var1 = new ArrayList();

      for (PoolSubpage var5 : var0) {
         if (var5.next != var5) {
            PoolSubpage var6 = var5.next;

            while (true) {
               var1.add(var6);
               var6 = var6.next;
               if (var6 == var5) {
                  break;
               }
            }
         }
      }

      return var1;
   }

   @Override
   public long numAllocations() {
      this.lock();

      long var1;
      try {
         var1 = this.allocationsNormal;
      } finally {
         this.unlock();
      }

      return this.allocationsSmall.value() + var1 + this.allocationsHuge.value();
   }

   @Override
   public long numTinyAllocations() {
      return 0L;
   }

   @Override
   public long numSmallAllocations() {
      return this.allocationsSmall.value();
   }

   @Override
   public long numNormalAllocations() {
      this.lock();

      long var1;
      try {
         var1 = this.allocationsNormal;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.deallocationsSmall + this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return var1 + this.deallocationsHuge.value();
   }

   @Override
   public long numTinyDeallocations() {
      return 0L;
   }

   @Override
   public long numSmallDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.deallocationsSmall;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numNormalDeallocations() {
      this.lock();

      long var1;
      try {
         var1 = this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return var1;
   }

   @Override
   public long numHugeAllocations() {
      return this.allocationsHuge.value();
   }

   @Override
   public long numHugeDeallocations() {
      return this.deallocationsHuge.value();
   }

   @Override
   public long numActiveAllocations() {
      long var1 = this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
      this.lock();

      try {
         var1 += this.allocationsNormal - (this.deallocationsSmall + this.deallocationsNormal);
      } finally {
         this.unlock();
      }

      return Math.max(var1, 0L);
   }

   @Override
   public long numActiveTinyAllocations() {
      return 0L;
   }

   @Override
   public long numActiveSmallAllocations() {
      return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
   }

   @Override
   public long numActiveNormalAllocations() {
      this.lock();

      long var1;
      try {
         var1 = this.allocationsNormal - this.deallocationsNormal;
      } finally {
         this.unlock();
      }

      return Math.max(var1, 0L);
   }

   @Override
   public long numActiveHugeAllocations() {
      return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
   }

   @Override
   public long numActiveBytes() {
      long var1 = this.activeBytesHuge.value();
      this.lock();

      try {
         for (int var3 = 0; var3 < this.chunkListMetrics.size(); var3++) {
            for (PoolChunkMetric var5 : this.chunkListMetrics.get(var3)) {
               var1 += (long)var5.chunkSize();
            }
         }
      } finally {
         this.unlock();
      }

      return Math.max(0L, var1);
   }

   public long numPinnedBytes() {
      long var1 = this.activeBytesHuge.value();
      this.lock();

      try {
         for (int var3 = 0; var3 < this.chunkListMetrics.size(); var3++) {
            for (PoolChunkMetric var5 : this.chunkListMetrics.get(var3)) {
               var1 += (long)((PoolChunk)var5).pinnedBytes();
            }
         }
      } finally {
         this.unlock();
      }

      return Math.max(0L, var1);
   }

   protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

   protected abstract PoolChunk<T> newUnpooledChunk(int var1);

   protected abstract PooledByteBuf<T> newByteBuf(int var1);

   protected abstract void memoryCopy(T var1, int var2, PooledByteBuf<T> var3, int var4);

   protected abstract void destroyChunk(PoolChunk<T> var1);

   @Override
   public String toString() {
      this.lock();

      String var2;
      try {
         StringBuilder var1 = new StringBuilder()
            .append("Chunk(s) at 0~25%:")
            .append(StringUtil.NEWLINE)
            .append(this.qInit)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 0~50%:")
            .append(StringUtil.NEWLINE)
            .append(this.q000)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 25~75%:")
            .append(StringUtil.NEWLINE)
            .append(this.q025)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 50~100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q050)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 75~100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q075)
            .append(StringUtil.NEWLINE)
            .append("Chunk(s) at 100%:")
            .append(StringUtil.NEWLINE)
            .append(this.q100)
            .append(StringUtil.NEWLINE)
            .append("small subpages:");
         appendPoolSubPages(var1, this.smallSubpagePools);
         var1.append(StringUtil.NEWLINE);
         var2 = var1.toString();
      } finally {
         this.unlock();
      }

      return var2;
   }

   private static void appendPoolSubPages(StringBuilder var0, PoolSubpage<?>[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         PoolSubpage var3 = var1[var2];
         if (var3.next != var3) {
            var0.append(StringUtil.NEWLINE).append(var2).append(": ");
            PoolSubpage var4 = var3.next;

            while (true) {
               var0.append(var4);
               var4 = var4.next;
               if (var4 == var3) {
                  break;
               }
            }
         }
      }
   }

   @Override
   protected final void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         destroyPoolSubPages(this.smallSubpagePools);
         this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
      }
   }

   private static void destroyPoolSubPages(PoolSubpage<?>[] var0) {
      for (PoolSubpage var4 : var0) {
         var4.destroy();
      }
   }

   private void destroyPoolChunkLists(PoolChunkList<T>... var1) {
      for (PoolChunkList var5 : var1) {
         var5.destroy(this);
      }
   }

   void lock() {
      this.lock.lock();
   }

   void unlock() {
      this.lock.unlock();
   }

   static final class DirectArena extends PoolArena<ByteBuffer> {
      DirectArena(PooledByteBufAllocator var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
      }

      @Override
      boolean isDirect() {
         return true;
      }

      @Override
      protected PoolChunk<ByteBuffer> newChunk(int var1, int var2, int var3, int var4) {
         if (this.directMemoryCacheAlignment == 0) {
            ByteBuffer var7 = allocateDirect(var4);
            return new PoolChunk<>(this, var7, var7, var1, var3, var4, var2);
         } else {
            ByteBuffer var5 = allocateDirect(var4 + this.directMemoryCacheAlignment);
            ByteBuffer var6 = PlatformDependent.alignDirectBuffer(var5, this.directMemoryCacheAlignment);
            return new PoolChunk<>(this, var5, var6, var1, var3, var4, var2);
         }
      }

      @Override
      protected PoolChunk<ByteBuffer> newUnpooledChunk(int var1) {
         if (this.directMemoryCacheAlignment == 0) {
            ByteBuffer var4 = allocateDirect(var1);
            return new PoolChunk<>(this, var4, var4, var1);
         } else {
            ByteBuffer var2 = allocateDirect(var1 + this.directMemoryCacheAlignment);
            ByteBuffer var3 = PlatformDependent.alignDirectBuffer(var2, this.directMemoryCacheAlignment);
            return new PoolChunk<>(this, var2, var3, var1);
         }
      }

      private static ByteBuffer allocateDirect(int var0) {
         return PlatformDependent.useDirectBufferNoCleaner() ? PlatformDependent.allocateDirectNoCleaner(var0) : ByteBuffer.allocateDirect(var0);
      }

      @Override
      protected void destroyChunk(PoolChunk<ByteBuffer> var1) {
         if (PlatformDependent.useDirectBufferNoCleaner()) {
            PlatformDependent.freeDirectNoCleaner((ByteBuffer)var1.base);
         } else {
            PlatformDependent.freeDirectBuffer((ByteBuffer)var1.base);
         }
      }

      @Override
      protected PooledByteBuf<ByteBuffer> newByteBuf(int var1) {
         return (PooledByteBuf<ByteBuffer>)(HAS_UNSAFE ? PooledUnsafeDirectByteBuf.newInstance(var1) : PooledDirectByteBuf.newInstance(var1));
      }

      protected void memoryCopy(ByteBuffer var1, int var2, PooledByteBuf<ByteBuffer> var3, int var4) {
         if (var4 != 0) {
            if (HAS_UNSAFE) {
               PlatformDependent.copyMemory(
                  PlatformDependent.directBufferAddress(var1) + (long)var2,
                  PlatformDependent.directBufferAddress((ByteBuffer)var3.memory) + (long)var3.offset,
                  (long)var4
               );
            } else {
               var1 = var1.duplicate();
               ByteBuffer var5 = var3.internalNioBuffer();
               ((Buffer)var1).position(var2).limit(var2 + var4);
               ((Buffer)var5).position(var3.offset);
               var5.put(var1);
            }
         }
      }
   }

   static final class HeapArena extends PoolArena<byte[]> {
      HeapArena(PooledByteBufAllocator var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4, 0);
      }

      private static byte[] newByteArray(int var0) {
         return PlatformDependent.allocateUninitializedArray(var0);
      }

      @Override
      boolean isDirect() {
         return false;
      }

      @Override
      protected PoolChunk<byte[]> newChunk(int var1, int var2, int var3, int var4) {
         return new PoolChunk<>(this, null, newByteArray(var4), var1, var3, var4, var2);
      }

      @Override
      protected PoolChunk<byte[]> newUnpooledChunk(int var1) {
         return new PoolChunk<>(this, null, newByteArray(var1), var1);
      }

      @Override
      protected void destroyChunk(PoolChunk<byte[]> var1) {
      }

      @Override
      protected PooledByteBuf<byte[]> newByteBuf(int var1) {
         return (PooledByteBuf<byte[]>)(HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(var1) : PooledHeapByteBuf.newInstance(var1));
      }

      protected void memoryCopy(byte[] var1, int var2, PooledByteBuf<byte[]> var3, int var4) {
         if (var4 != 0) {
            System.arraycopy(var1, var2, var3.memory, var3.offset, var4);
         }
      }
   }

   static enum SizeClass {
      Small,
      Normal;
   }
}
