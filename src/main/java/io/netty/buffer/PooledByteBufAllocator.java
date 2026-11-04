package io.netty.buffer;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadExecutorMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PooledByteBufAllocator extends AbstractByteBufAllocator implements ByteBufAllocatorMetricProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
   private static final int DEFAULT_NUM_HEAP_ARENA;
   private static final int DEFAULT_NUM_DIRECT_ARENA;
   private static final int DEFAULT_PAGE_SIZE;
   private static final int DEFAULT_MAX_ORDER;
   private static final int DEFAULT_SMALL_CACHE_SIZE;
   private static final int DEFAULT_NORMAL_CACHE_SIZE;
   static final int DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
   private static final int DEFAULT_CACHE_TRIM_INTERVAL;
   private static final long DEFAULT_CACHE_TRIM_INTERVAL_MILLIS;
   private static final boolean DEFAULT_USE_CACHE_FOR_ALL_THREADS;
   private static final int DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT;
   static final int DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK;
   private static final int MIN_PAGE_SIZE = 4096;
   private static final int MAX_CHUNK_SIZE = 1073741824;
   private static final int CACHE_NOT_USED = 0;
   private final Runnable trimTask = new Runnable() {
      @Override
      public void run() {
         PooledByteBufAllocator.this.trimCurrentThreadCache();
      }
   };
   public static final PooledByteBufAllocator DEFAULT;
   private final PoolArena<byte[]>[] heapArenas;
   private final PoolArena<ByteBuffer>[] directArenas;
   private final int smallCacheSize;
   private final int normalCacheSize;
   private final List<PoolArenaMetric> heapArenaMetrics;
   private final List<PoolArenaMetric> directArenaMetrics;
   private final PooledByteBufAllocator.PoolThreadLocalCache threadCache;
   private final int chunkSize;
   private final PooledByteBufAllocatorMetric metric;

   public PooledByteBufAllocator() {
      this(false);
   }

   public PooledByteBufAllocator(boolean var1) {
      this(var1, DEFAULT_NUM_HEAP_ARENA, DEFAULT_NUM_DIRECT_ARENA, DEFAULT_PAGE_SIZE, DEFAULT_MAX_ORDER);
   }

   public PooledByteBufAllocator(int var1, int var2, int var3, int var4) {
      this(false, var1, var2, var3, var4);
   }

   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, 0, DEFAULT_SMALL_CACHE_SIZE, DEFAULT_NORMAL_CACHE_SIZE);
   }

   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this(var1, var2, var3, var4, var5, var7, var8, DEFAULT_USE_CACHE_FOR_ALL_THREADS, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
   }

   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      this(var1, var2, var3, var4, var5, var7, var8, var9);
   }

   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
   }

   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, int var10) {
      this(var1, var2, var3, var4, var5, var7, var8, var9, var10);
   }

   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, int var9) {
      super(var1);
      this.threadCache = new PooledByteBufAllocator.PoolThreadLocalCache(var8);
      this.smallCacheSize = var6;
      this.normalCacheSize = var7;
      if (var9 != 0) {
         if (!PlatformDependent.hasAlignDirectByteBuffer()) {
            throw new UnsupportedOperationException("Buffer alignment is not supported. Either Unsafe or ByteBuffer.alignSlice() must be available.");
         }

         var4 = (int)PlatformDependent.align((long)var4, var9);
      }

      this.chunkSize = validateAndCalculateChunkSize(var4, var5);
      ObjectUtil.checkPositiveOrZero(var2, "nHeapArena");
      ObjectUtil.checkPositiveOrZero(var3, "nDirectArena");
      ObjectUtil.checkPositiveOrZero(var9, "directMemoryCacheAlignment");
      if (var9 > 0 && !isDirectMemoryCacheAlignmentSupported()) {
         throw new IllegalArgumentException("directMemoryCacheAlignment is not supported");
      } else if ((var9 & -var9) != var9) {
         throw new IllegalArgumentException("directMemoryCacheAlignment: " + var9 + " (expected: power of two)");
      } else {
         int var10 = validateAndCalculatePageShifts(var4, var9);
         if (var2 > 0) {
            this.heapArenas = newArenaArray(var2);
            ArrayList var11 = new ArrayList(this.heapArenas.length);

            for (int var12 = 0; var12 < this.heapArenas.length; var12++) {
               PoolArena.HeapArena var13 = new PoolArena.HeapArena(this, var4, var10, this.chunkSize);
               this.heapArenas[var12] = var13;
               var11.add(var13);
            }

            this.heapArenaMetrics = Collections.unmodifiableList(var11);
         } else {
            this.heapArenas = null;
            this.heapArenaMetrics = Collections.emptyList();
         }

         if (var3 > 0) {
            this.directArenas = newArenaArray(var3);
            ArrayList var14 = new ArrayList(this.directArenas.length);

            for (int var15 = 0; var15 < this.directArenas.length; var15++) {
               PoolArena.DirectArena var16 = new PoolArena.DirectArena(this, var4, var10, this.chunkSize, var9);
               this.directArenas[var15] = var16;
               var14.add(var16);
            }

            this.directArenaMetrics = Collections.unmodifiableList(var14);
         } else {
            this.directArenas = null;
            this.directArenaMetrics = Collections.emptyList();
         }

         this.metric = new PooledByteBufAllocatorMetric(this);
      }
   }

   private static <T> PoolArena<T>[] newArenaArray(int var0) {
      return new PoolArena[var0];
   }

   private static int validateAndCalculatePageShifts(int var0, int var1) {
      if (var0 < 4096) {
         throw new IllegalArgumentException("pageSize: " + var0 + " (expected: " + 4096 + 41);
      } else if ((var0 & var0 - 1) != 0) {
         throw new IllegalArgumentException("pageSize: " + var0 + " (expected: power of 2)");
      } else if (var0 < var1) {
         throw new IllegalArgumentException("Alignment cannot be greater than page size. Alignment: " + var1 + ", page size: " + var0 + 46);
      } else {
         return 31 - Integer.numberOfLeadingZeros(var0);
      }
   }

   private static int validateAndCalculateChunkSize(int var0, int var1) {
      if (var1 > 14) {
         throw new IllegalArgumentException("maxOrder: " + var1 + " (expected: 0-14)");
      } else {
         int var2 = var0;

         for (int var3 = var1; var3 > 0; var3--) {
            if (var2 > 536870912) {
               throw new IllegalArgumentException(String.format("pageSize (%d) << maxOrder (%d) must not exceed %d", var0, var1, 1073741824));
            }

            var2 <<= 1;
         }

         return var2;
      }
   }

   @Override
   protected ByteBuf newHeapBuffer(int var1, int var2) {
      PoolThreadCache var3 = this.threadCache.get();
      PoolArena var4 = var3.heapArena;
      Object var5;
      if (var4 != null) {
         var5 = var4.allocate(var3, var1, var2);
      } else {
         var5 = PlatformDependent.hasUnsafe() ? new UnpooledUnsafeHeapByteBuf(this, var1, var2) : new UnpooledHeapByteBuf(this, var1, var2);
      }

      return toLeakAwareBuffer((ByteBuf)var5);
   }

   @Override
   protected ByteBuf newDirectBuffer(int var1, int var2) {
      PoolThreadCache var3 = this.threadCache.get();
      PoolArena var4 = var3.directArena;
      Object var5;
      if (var4 != null) {
         var5 = var4.allocate(var3, var1, var2);
      } else {
         var5 = PlatformDependent.hasUnsafe() ? UnsafeByteBufUtil.newUnsafeDirectByteBuf(this, var1, var2) : new UnpooledDirectByteBuf(this, var1, var2);
      }

      return toLeakAwareBuffer((ByteBuf)var5);
   }

   public static int defaultNumHeapArena() {
      return DEFAULT_NUM_HEAP_ARENA;
   }

   public static int defaultNumDirectArena() {
      return DEFAULT_NUM_DIRECT_ARENA;
   }

   public static int defaultPageSize() {
      return DEFAULT_PAGE_SIZE;
   }

   public static int defaultMaxOrder() {
      return DEFAULT_MAX_ORDER;
   }

   public static boolean defaultUseCacheForAllThreads() {
      return DEFAULT_USE_CACHE_FOR_ALL_THREADS;
   }

   public static boolean defaultPreferDirect() {
      return PlatformDependent.directBufferPreferred();
   }

   @Deprecated
   public static int defaultTinyCacheSize() {
      return 0;
   }

   public static int defaultSmallCacheSize() {
      return DEFAULT_SMALL_CACHE_SIZE;
   }

   public static int defaultNormalCacheSize() {
      return DEFAULT_NORMAL_CACHE_SIZE;
   }

   public static boolean isDirectMemoryCacheAlignmentSupported() {
      return PlatformDependent.hasUnsafe();
   }

   @Override
   public boolean isDirectBufferPooled() {
      return this.directArenas != null;
   }

   @Deprecated
   public boolean hasThreadLocalCache() {
      return this.threadCache.isSet();
   }

   @Deprecated
   public void freeThreadLocalCache() {
      this.threadCache.remove();
   }

   public PooledByteBufAllocatorMetric metric() {
      return this.metric;
   }

   @Deprecated
   public int numHeapArenas() {
      return this.heapArenaMetrics.size();
   }

   @Deprecated
   public int numDirectArenas() {
      return this.directArenaMetrics.size();
   }

   @Deprecated
   public List<PoolArenaMetric> heapArenas() {
      return this.heapArenaMetrics;
   }

   @Deprecated
   public List<PoolArenaMetric> directArenas() {
      return this.directArenaMetrics;
   }

   @Deprecated
   public int numThreadLocalCaches() {
      PoolArena[] var1 = this.heapArenas != null ? this.heapArenas : this.directArenas;
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;

         for (PoolArena var6 : var1) {
            var2 += var6.numThreadCaches.get();
         }

         return var2;
      }
   }

   @Deprecated
   public int tinyCacheSize() {
      return 0;
   }

   @Deprecated
   public int smallCacheSize() {
      return this.smallCacheSize;
   }

   @Deprecated
   public int normalCacheSize() {
      return this.normalCacheSize;
   }

   @Deprecated
   public final int chunkSize() {
      return this.chunkSize;
   }

   final long usedHeapMemory() {
      return usedMemory(this.heapArenas);
   }

   final long usedDirectMemory() {
      return usedMemory(this.directArenas);
   }

   private static long usedMemory(PoolArena<?>[] var0) {
      if (var0 == null) {
         return -1L;
      } else {
         long var1 = 0L;

         for (PoolArena var6 : var0) {
            var1 += var6.numActiveBytes();
            if (var1 < 0L) {
               return Long.MAX_VALUE;
            }
         }

         return var1;
      }
   }

   public final long pinnedHeapMemory() {
      return pinnedMemory(this.heapArenas);
   }

   public final long pinnedDirectMemory() {
      return pinnedMemory(this.directArenas);
   }

   private static long pinnedMemory(PoolArena<?>[] var0) {
      if (var0 == null) {
         return -1L;
      } else {
         long var1 = 0L;

         for (PoolArena var6 : var0) {
            var1 += var6.numPinnedBytes();
            if (var1 < 0L) {
               return Long.MAX_VALUE;
            }
         }

         return var1;
      }
   }

   final PoolThreadCache threadCache() {
      PoolThreadCache var1 = this.threadCache.get();

      assert var1 != null;

      return var1;
   }

   public boolean trimCurrentThreadCache() {
      PoolThreadCache var1 = this.threadCache.getIfExists();
      if (var1 != null) {
         var1.trim();
         return true;
      } else {
         return false;
      }
   }

   public String dumpStats() {
      int var1 = this.heapArenas == null ? 0 : this.heapArenas.length;
      StringBuilder var2 = new StringBuilder(512).append(var1).append(" heap arena(s):").append(StringUtil.NEWLINE);
      if (var1 > 0) {
         for (PoolArena var6 : this.heapArenas) {
            var2.append(var6);
         }
      }

      int var8 = this.directArenas == null ? 0 : this.directArenas.length;
      var2.append(var8).append(" direct arena(s):").append(StringUtil.NEWLINE);
      if (var8 > 0) {
         for (PoolArena var7 : this.directArenas) {
            var2.append(var7);
         }
      }

      return var2.toString();
   }

   static {
      int var0 = SystemPropertyUtil.getInt("io.netty.allocator.directMemoryCacheAlignment", 0);
      int var1 = SystemPropertyUtil.getInt("io.netty.allocator.pageSize", 8192);
      Throwable var2 = null;

      try {
         validateAndCalculatePageShifts(var1, var0);
      } catch (Throwable var9) {
         var2 = var9;
         var1 = 8192;
         var0 = 0;
      }

      DEFAULT_PAGE_SIZE = var1;
      DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT = var0;
      int var3 = SystemPropertyUtil.getInt("io.netty.allocator.maxOrder", 9);
      Throwable var4 = null;

      try {
         validateAndCalculateChunkSize(DEFAULT_PAGE_SIZE, var3);
      } catch (Throwable var8) {
         var4 = var8;
         var3 = 9;
      }

      DEFAULT_MAX_ORDER = var3;
      Runtime var5 = Runtime.getRuntime();
      int var6 = NettyRuntime.availableProcessors() * 2;
      int var7 = DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER;
      DEFAULT_NUM_HEAP_ARENA = Math.max(
         0, SystemPropertyUtil.getInt("io.netty.allocator.numHeapArenas", (int)Math.min((long)var6, var5.maxMemory() / (long)var7 / 2L / 3L))
      );
      DEFAULT_NUM_DIRECT_ARENA = Math.max(
         0,
         SystemPropertyUtil.getInt("io.netty.allocator.numDirectArenas", (int)Math.min((long)var6, PlatformDependent.maxDirectMemory() / (long)var7 / 2L / 3L))
      );
      DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.smallCacheSize", 256);
      DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.normalCacheSize", 64);
      DEFAULT_MAX_CACHED_BUFFER_CAPACITY = SystemPropertyUtil.getInt("io.netty.allocator.maxCachedBufferCapacity", 32768);
      DEFAULT_CACHE_TRIM_INTERVAL = SystemPropertyUtil.getInt("io.netty.allocator.cacheTrimInterval", 8192);
      if (SystemPropertyUtil.contains("io.netty.allocation.cacheTrimIntervalMillis")) {
         logger.warn("-Dio.netty.allocation.cacheTrimIntervalMillis is deprecated, use -Dio.netty.allocator.cacheTrimIntervalMillis");
         if (SystemPropertyUtil.contains("io.netty.allocator.cacheTrimIntervalMillis")) {
            DEFAULT_CACHE_TRIM_INTERVAL_MILLIS = SystemPropertyUtil.getLong("io.netty.allocator.cacheTrimIntervalMillis", 0L);
         } else {
            DEFAULT_CACHE_TRIM_INTERVAL_MILLIS = SystemPropertyUtil.getLong("io.netty.allocation.cacheTrimIntervalMillis", 0L);
         }
      } else {
         DEFAULT_CACHE_TRIM_INTERVAL_MILLIS = SystemPropertyUtil.getLong("io.netty.allocator.cacheTrimIntervalMillis", 0L);
      }

      DEFAULT_USE_CACHE_FOR_ALL_THREADS = SystemPropertyUtil.getBoolean("io.netty.allocator.useCacheForAllThreads", false);
      DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK = SystemPropertyUtil.getInt("io.netty.allocator.maxCachedByteBuffersPerChunk", 1023);
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.allocator.numHeapArenas: {}", DEFAULT_NUM_HEAP_ARENA);
         logger.debug("-Dio.netty.allocator.numDirectArenas: {}", DEFAULT_NUM_DIRECT_ARENA);
         if (var2 == null) {
            logger.debug("-Dio.netty.allocator.pageSize: {}", DEFAULT_PAGE_SIZE);
         } else {
            logger.debug("-Dio.netty.allocator.pageSize: {}", DEFAULT_PAGE_SIZE, var2);
         }

         if (var4 == null) {
            logger.debug("-Dio.netty.allocator.maxOrder: {}", DEFAULT_MAX_ORDER);
         } else {
            logger.debug("-Dio.netty.allocator.maxOrder: {}", DEFAULT_MAX_ORDER, var4);
         }

         logger.debug("-Dio.netty.allocator.chunkSize: {}", DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER);
         logger.debug("-Dio.netty.allocator.smallCacheSize: {}", DEFAULT_SMALL_CACHE_SIZE);
         logger.debug("-Dio.netty.allocator.normalCacheSize: {}", DEFAULT_NORMAL_CACHE_SIZE);
         logger.debug("-Dio.netty.allocator.maxCachedBufferCapacity: {}", DEFAULT_MAX_CACHED_BUFFER_CAPACITY);
         logger.debug("-Dio.netty.allocator.cacheTrimInterval: {}", DEFAULT_CACHE_TRIM_INTERVAL);
         logger.debug("-Dio.netty.allocator.cacheTrimIntervalMillis: {}", DEFAULT_CACHE_TRIM_INTERVAL_MILLIS);
         logger.debug("-Dio.netty.allocator.useCacheForAllThreads: {}", DEFAULT_USE_CACHE_FOR_ALL_THREADS);
         logger.debug("-Dio.netty.allocator.maxCachedByteBuffersPerChunk: {}", DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK);
      }

      DEFAULT = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
   }

   private final class PoolThreadLocalCache extends FastThreadLocal<PoolThreadCache> {
      private final boolean useCacheForAllThreads;

      PoolThreadLocalCache(boolean var2) {
         this.useCacheForAllThreads = var2;
      }

      protected synchronized PoolThreadCache initialValue() {
         PoolArena var1 = this.leastUsedArena(PooledByteBufAllocator.this.heapArenas);
         PoolArena var2 = this.leastUsedArena(PooledByteBufAllocator.this.directArenas);
         Thread var3 = Thread.currentThread();
         EventExecutor var4 = ThreadExecutorMap.currentExecutor();
         if (!this.useCacheForAllThreads && !(var3 instanceof FastThreadLocalThread) && var4 == null) {
            return new PoolThreadCache(var1, var2, 0, 0, 0, 0);
         } else {
            PoolThreadCache var5 = new PoolThreadCache(
               var1,
               var2,
               PooledByteBufAllocator.this.smallCacheSize,
               PooledByteBufAllocator.this.normalCacheSize,
               PooledByteBufAllocator.DEFAULT_MAX_CACHED_BUFFER_CAPACITY,
               PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL
            );
            if (PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL_MILLIS > 0L && var4 != null) {
               var4.scheduleAtFixedRate(
                  PooledByteBufAllocator.this.trimTask,
                  PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL_MILLIS,
                  PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL_MILLIS,
                  TimeUnit.MILLISECONDS
               );
            }

            return var5;
         }
      }

      protected void onRemoval(PoolThreadCache var1) {
         var1.free(false);
      }

      private <T> PoolArena<T> leastUsedArena(PoolArena<T>[] var1) {
         if (var1 != null && var1.length != 0) {
            PoolArena var2 = var1[0];
            if (var2.numThreadCaches.get() == 0) {
               return var2;
            } else {
               for (int var3 = 1; var3 < var1.length; var3++) {
                  PoolArena var4 = var1[var3];
                  if (var4.numThreadCaches.get() < var2.numThreadCaches.get()) {
                     var2 = var4;
                  }
               }

               return var2;
            }
         } else {
            return null;
         }
      }
   }
}
