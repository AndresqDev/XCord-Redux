package io.netty.buffer;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

final class PoolThreadCache {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
   private static final int INTEGER_SIZE_MINUS_ONE = 31;
   final PoolArena<byte[]> heapArena;
   final PoolArena<ByteBuffer> directArena;
   private final PoolThreadCache.MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
   private final PoolThreadCache.MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
   private final PoolThreadCache.MemoryRegionCache<byte[]>[] normalHeapCaches;
   private final PoolThreadCache.MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
   private final int freeSweepAllocationThreshold;
   private final AtomicBoolean freed = new AtomicBoolean();
   private int allocations;

   PoolThreadCache(PoolArena<byte[]> var1, PoolArena<ByteBuffer> var2, int var3, int var4, int var5, int var6) {
      ObjectUtil.checkPositiveOrZero(var5, "maxCachedBufferCapacity");
      this.freeSweepAllocationThreshold = var6;
      this.heapArena = var1;
      this.directArena = var2;
      if (var2 != null) {
         this.smallSubPageDirectCaches = createSubPageCaches(var3, var2.numSmallSubpagePools);
         this.normalDirectCaches = createNormalCaches(var4, var5, var2);
         var2.numThreadCaches.getAndIncrement();
      } else {
         this.smallSubPageDirectCaches = null;
         this.normalDirectCaches = null;
      }

      if (var1 != null) {
         this.smallSubPageHeapCaches = createSubPageCaches(var3, var1.numSmallSubpagePools);
         this.normalHeapCaches = createNormalCaches(var4, var5, var1);
         var1.numThreadCaches.getAndIncrement();
      } else {
         this.smallSubPageHeapCaches = null;
         this.normalHeapCaches = null;
      }

      if ((this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null)
         && var6 < 1) {
         throw new IllegalArgumentException("freeSweepAllocationThreshold: " + var6 + " (expected: > 0)");
      }
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T>[] createSubPageCaches(int var0, int var1) {
      if (var0 > 0 && var1 > 0) {
         PoolThreadCache.MemoryRegionCache[] var2 = new PoolThreadCache.MemoryRegionCache[var1];

         for (int var3 = 0; var3 < var2.length; var3++) {
            var2[var3] = new PoolThreadCache.SubPageMemoryRegionCache(var0);
         }

         return var2;
      } else {
         return null;
      }
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T>[] createNormalCaches(int var0, int var1, PoolArena<T> var2) {
      if (var0 > 0 && var1 > 0) {
         int var3 = Math.min(var2.chunkSize, var1);
         ArrayList var4 = new ArrayList();

         for (int var5 = var2.numSmallSubpagePools; var5 < var2.nSizes && var2.sizeIdx2size(var5) <= var3; var5++) {
            var4.add(new PoolThreadCache.NormalMemoryRegionCache(var0));
         }

         return var4.toArray(new PoolThreadCache.MemoryRegionCache[0]);
      } else {
         return null;
      }
   }

   static int log2(int var0) {
      return 31 - Integer.numberOfLeadingZeros(var0);
   }

   boolean allocateSmall(PoolArena<?> var1, PooledByteBuf<?> var2, int var3, int var4) {
      return this.allocate(this.cacheForSmall(var1, var4), var2, var3);
   }

   boolean allocateNormal(PoolArena<?> var1, PooledByteBuf<?> var2, int var3, int var4) {
      return this.allocate(this.cacheForNormal(var1, var4), var2, var3);
   }

   private boolean allocate(PoolThreadCache.MemoryRegionCache<?> var1, PooledByteBuf var2, int var3) {
      if (var1 == null) {
         return false;
      } else {
         boolean var4 = var1.allocate(var2, var3, this);
         if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
         }

         return var4;
      }
   }

   boolean add(PoolArena<?> var1, PoolChunk var2, ByteBuffer var3, long var4, int var6, PoolArena.SizeClass var7) {
      int var8 = var1.size2SizeIdx(var6);
      PoolThreadCache.MemoryRegionCache var9 = this.cache(var1, var8, var7);
      return var9 == null ? false : var9.add(var2, var3, var4, var6);
   }

   private PoolThreadCache.MemoryRegionCache<?> cache(PoolArena<?> var1, int var2, PoolArena.SizeClass var3) {
      switch (var3) {
         case Normal:
            return this.cacheForNormal(var1, var2);
         case Small:
            return this.cacheForSmall(var1, var2);
         default:
            throw new Error();
      }
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         this.free(true);
      }
   }

   void free(boolean var1) {
      if (this.freed.compareAndSet(false, true)) {
         int var2 = free(this.smallSubPageDirectCaches, var1)
            + free(this.normalDirectCaches, var1)
            + free(this.smallSubPageHeapCaches, var1)
            + free(this.normalHeapCaches, var1);
         if (var2 > 0 && logger.isDebugEnabled()) {
            logger.debug("Freed {} thread-local buffer(s) from thread: {}", var2, Thread.currentThread().getName());
         }

         if (this.directArena != null) {
            this.directArena.numThreadCaches.getAndDecrement();
         }

         if (this.heapArena != null) {
            this.heapArena.numThreadCaches.getAndDecrement();
         }
      }
   }

   private static int free(PoolThreadCache.MemoryRegionCache<?>[] var0, boolean var1) {
      if (var0 == null) {
         return 0;
      } else {
         int var2 = 0;

         for (PoolThreadCache.MemoryRegionCache var6 : var0) {
            var2 += free(var6, var1);
         }

         return var2;
      }
   }

   private static int free(PoolThreadCache.MemoryRegionCache<?> var0, boolean var1) {
      return var0 == null ? 0 : var0.free(var1);
   }

   void trim() {
      trim(this.smallSubPageDirectCaches);
      trim(this.normalDirectCaches);
      trim(this.smallSubPageHeapCaches);
      trim(this.normalHeapCaches);
   }

   private static void trim(PoolThreadCache.MemoryRegionCache<?>[] var0) {
      if (var0 != null) {
         for (PoolThreadCache.MemoryRegionCache var4 : var0) {
            trim(var4);
         }
      }
   }

   private static void trim(PoolThreadCache.MemoryRegionCache<?> var0) {
      if (var0 != null) {
         var0.trim();
      }
   }

   private PoolThreadCache.MemoryRegionCache<?> cacheForSmall(PoolArena<?> var1, int var2) {
      return var1.isDirect() ? cache(this.smallSubPageDirectCaches, var2) : cache(this.smallSubPageHeapCaches, var2);
   }

   private PoolThreadCache.MemoryRegionCache<?> cacheForNormal(PoolArena<?> var1, int var2) {
      int var3 = var2 - var1.numSmallSubpagePools;
      return var1.isDirect() ? cache(this.normalDirectCaches, var3) : cache(this.normalHeapCaches, var3);
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T> cache(PoolThreadCache.MemoryRegionCache<T>[] var0, int var1) {
      return var0 != null && var1 <= var0.length - 1 ? var0[var1] : null;
   }

   private abstract static class MemoryRegionCache<T> {
      private final int size;
      private final Queue<PoolThreadCache.MemoryRegionCache.Entry<T>> queue;
      private final PoolArena.SizeClass sizeClass;
      private int allocations;
      private static final ObjectPool<PoolThreadCache.MemoryRegionCache.Entry> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<PoolThreadCache.MemoryRegionCache.Entry>() {
            public PoolThreadCache.MemoryRegionCache.Entry newObject(ObjectPool.Handle<PoolThreadCache.MemoryRegionCache.Entry> var1) {
               return new PoolThreadCache.MemoryRegionCache.Entry(var1);
            }
         }
      );

      MemoryRegionCache(int var1, PoolArena.SizeClass var2) {
         this.size = MathUtil.safeFindNextPositivePowerOfTwo(var1);
         this.queue = PlatformDependent.newFixedMpscQueue(this.size);
         this.sizeClass = var2;
      }

      protected abstract void initBuf(PoolChunk<T> var1, ByteBuffer var2, long var3, PooledByteBuf<T> var5, int var6, PoolThreadCache var7);

      public final boolean add(PoolChunk<T> var1, ByteBuffer var2, long var3, int var5) {
         PoolThreadCache.MemoryRegionCache.Entry var6 = newEntry(var1, var2, var3, var5);
         boolean var7 = this.queue.offer(var6);
         if (!var7) {
            var6.recycle();
         }

         return var7;
      }

      public final boolean allocate(PooledByteBuf<T> var1, int var2, PoolThreadCache var3) {
         PoolThreadCache.MemoryRegionCache.Entry var4 = this.queue.poll();
         if (var4 == null) {
            return false;
         } else {
            this.initBuf(var4.chunk, var4.nioBuffer, var4.handle, var1, var2, var3);
            var4.recycle();
            this.allocations++;
            return true;
         }
      }

      public final int free(boolean var1) {
         return this.free(Integer.MAX_VALUE, var1);
      }

      private int free(int var1, boolean var2) {
         int var3;
         for (var3 = 0; var3 < var1; var3++) {
            PoolThreadCache.MemoryRegionCache.Entry var4 = this.queue.poll();
            if (var4 == null) {
               return var3;
            }

            this.freeEntry(var4, var2);
         }

         return var3;
      }

      public final void trim() {
         int var1 = this.size - this.allocations;
         this.allocations = 0;
         if (var1 > 0) {
            this.free(var1, false);
         }
      }

      private void freeEntry(PoolThreadCache.MemoryRegionCache.Entry var1, boolean var2) {
         PoolChunk var3 = var1.chunk;
         long var4 = var1.handle;
         ByteBuffer var6 = var1.nioBuffer;
         int var7 = var1.normCapacity;
         if (!var2) {
            var1.recycle();
         }

         var3.arena.freeChunk(var3, var4, var7, this.sizeClass, var6, var2);
      }

      private static PoolThreadCache.MemoryRegionCache.Entry newEntry(PoolChunk<?> var0, ByteBuffer var1, long var2, int var4) {
         PoolThreadCache.MemoryRegionCache.Entry var5 = RECYCLER.get();
         var5.chunk = var0;
         var5.nioBuffer = var1;
         var5.handle = var2;
         var5.normCapacity = var4;
         return var5;
      }

      static final class Entry<T> {
         final ObjectPool.Handle<PoolThreadCache.MemoryRegionCache.Entry<?>> recyclerHandle;
         PoolChunk<T> chunk;
         ByteBuffer nioBuffer;
         long handle = -1L;
         int normCapacity;

         Entry(ObjectPool.Handle<PoolThreadCache.MemoryRegionCache.Entry<?>> var1) {
            this.recyclerHandle = var1;
         }

         void recycle() {
            this.chunk = null;
            this.nioBuffer = null;
            this.handle = -1L;
            this.recyclerHandle.recycle(this);
         }
      }
   }

   private static final class NormalMemoryRegionCache<T> extends PoolThreadCache.MemoryRegionCache<T> {
      NormalMemoryRegionCache(int var1) {
         super(var1, PoolArena.SizeClass.Normal);
      }

      @Override
      protected void initBuf(PoolChunk<T> var1, ByteBuffer var2, long var3, PooledByteBuf<T> var5, int var6, PoolThreadCache var7) {
         var1.initBuf(var5, var2, var3, var6, var7);
      }
   }

   private static final class SubPageMemoryRegionCache<T> extends PoolThreadCache.MemoryRegionCache<T> {
      SubPageMemoryRegionCache(int var1) {
         super(var1, PoolArena.SizeClass.Small);
      }

      @Override
      protected void initBuf(PoolChunk<T> var1, ByteBuffer var2, long var3, PooledByteBuf<T> var5, int var6, PoolThreadCache var7) {
         var1.initBufWithSubpage(var5, var2, var3, var6, var7);
      }
   }
}
