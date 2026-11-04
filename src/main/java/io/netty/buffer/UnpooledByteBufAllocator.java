package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator implements ByteBufAllocatorMetricProvider {
   private final UnpooledByteBufAllocator.UnpooledByteBufAllocatorMetric metric = new UnpooledByteBufAllocator.UnpooledByteBufAllocatorMetric();
   private final boolean disableLeakDetector;
   private final boolean noCleaner;
   public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());

   public UnpooledByteBufAllocator(boolean var1) {
      this(var1, false);
   }

   public UnpooledByteBufAllocator(boolean var1, boolean var2) {
      this(var1, var2, PlatformDependent.useDirectBufferNoCleaner());
   }

   public UnpooledByteBufAllocator(boolean var1, boolean var2, boolean var3) {
      super(var1);
      this.disableLeakDetector = var2;
      this.noCleaner = var3 && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor();
   }

   @Override
   protected ByteBuf newHeapBuffer(int var1, int var2) {
      return (ByteBuf)(PlatformDependent.hasUnsafe()
         ? new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeHeapByteBuf(this, var1, var2)
         : new UnpooledByteBufAllocator.InstrumentedUnpooledHeapByteBuf(this, var1, var2));
   }

   @Override
   protected ByteBuf newDirectBuffer(int var1, int var2) {
      Object var3;
      if (PlatformDependent.hasUnsafe()) {
         var3 = this.noCleaner
            ? new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(this, var1, var2)
            : new UnpooledByteBufAllocator.InstrumentedUnpooledUnsafeDirectByteBuf(this, var1, var2);
      } else {
         var3 = new UnpooledByteBufAllocator.InstrumentedUnpooledDirectByteBuf(this, var1, var2);
      }

      return (ByteBuf)(this.disableLeakDetector ? var3 : toLeakAwareBuffer((ByteBuf)var3));
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer(int var1) {
      CompositeByteBuf var2 = new CompositeByteBuf(this, false, var1);
      return this.disableLeakDetector ? var2 : toLeakAwareBuffer(var2);
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer(int var1) {
      CompositeByteBuf var2 = new CompositeByteBuf(this, true, var1);
      return this.disableLeakDetector ? var2 : toLeakAwareBuffer(var2);
   }

   @Override
   public boolean isDirectBufferPooled() {
      return false;
   }

   @Override
   public ByteBufAllocatorMetric metric() {
      return this.metric;
   }

   void incrementDirect(int var1) {
      this.metric.directCounter.add((long)var1);
   }

   void decrementDirect(int var1) {
      this.metric.directCounter.add((long)(-var1));
   }

   void incrementHeap(int var1) {
      this.metric.heapCounter.add((long)var1);
   }

   void decrementHeap(int var1) {
      this.metric.heapCounter.add((long)(-var1));
   }

   private static final class InstrumentedUnpooledDirectByteBuf extends UnpooledDirectByteBuf {
      InstrumentedUnpooledDirectByteBuf(UnpooledByteBufAllocator var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      @Override
      protected ByteBuffer allocateDirect(int var1) {
         ByteBuffer var2 = super.allocateDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(var2.capacity());
         return var2;
      }

      @Override
      protected void freeDirect(ByteBuffer var1) {
         int var2 = var1.capacity();
         super.freeDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(var2);
      }
   }

   private static final class InstrumentedUnpooledHeapByteBuf extends UnpooledHeapByteBuf {
      InstrumentedUnpooledHeapByteBuf(UnpooledByteBufAllocator var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      @Override
      protected byte[] allocateArray(int var1) {
         byte[] var2 = super.allocateArray(var1);
         ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(var2.length);
         return var2;
      }

      @Override
      protected void freeArray(byte[] var1) {
         int var2 = var1.length;
         super.freeArray(var1);
         ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(var2);
      }
   }

   private static final class InstrumentedUnpooledUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      InstrumentedUnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      @Override
      protected ByteBuffer allocateDirect(int var1) {
         ByteBuffer var2 = super.allocateDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(var2.capacity());
         return var2;
      }

      @Override
      protected void freeDirect(ByteBuffer var1) {
         int var2 = var1.capacity();
         super.freeDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(var2);
      }
   }

   private static final class InstrumentedUnpooledUnsafeHeapByteBuf extends UnpooledUnsafeHeapByteBuf {
      InstrumentedUnpooledUnsafeHeapByteBuf(UnpooledByteBufAllocator var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      @Override
      protected byte[] allocateArray(int var1) {
         byte[] var2 = super.allocateArray(var1);
         ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(var2.length);
         return var2;
      }

      @Override
      protected void freeArray(byte[] var1) {
         int var2 = var1.length;
         super.freeArray(var1);
         ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(var2);
      }
   }

   private static final class InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeNoCleanerDirectByteBuf {
      InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(UnpooledByteBufAllocator var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      @Override
      protected ByteBuffer allocateDirect(int var1) {
         ByteBuffer var2 = super.allocateDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(var2.capacity());
         return var2;
      }

      @Override
      ByteBuffer reallocateDirect(ByteBuffer var1, int var2) {
         int var3 = var1.capacity();
         ByteBuffer var4 = super.reallocateDirect(var1, var2);
         ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(var4.capacity() - var3);
         return var4;
      }

      @Override
      protected void freeDirect(ByteBuffer var1) {
         int var2 = var1.capacity();
         super.freeDirect(var1);
         ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(var2);
      }
   }

   private static final class UnpooledByteBufAllocatorMetric implements ByteBufAllocatorMetric {
      final LongCounter directCounter = PlatformDependent.newLongCounter();
      final LongCounter heapCounter = PlatformDependent.newLongCounter();

      private UnpooledByteBufAllocatorMetric() {
      }

      @Override
      public long usedHeapMemory() {
         return this.heapCounter.value();
      }

      @Override
      public long usedDirectMemory() {
         return this.directCounter.value();
      }

      @Override
      public String toString() {
         return StringUtil.simpleClassName(this) + "(usedHeapMemory: " + this.usedHeapMemory() + "; usedDirectMemory: " + this.usedDirectMemory() + ')';
      }
   }
}
