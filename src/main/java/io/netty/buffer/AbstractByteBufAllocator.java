package io.netty.buffer;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

public abstract class AbstractByteBufAllocator implements ByteBufAllocator {
   static final int DEFAULT_INITIAL_CAPACITY = 256;
   static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;
   static final int DEFAULT_MAX_COMPONENTS = 16;
   static final int CALCULATE_THRESHOLD = 4194304;
   private final boolean directByDefault;
   private final ByteBuf emptyBuf;

   protected static ByteBuf toLeakAwareBuffer(ByteBuf var0) {
      switch (ResourceLeakDetector.getLevel()) {
         case SIMPLE:
            ResourceLeakTracker var2 = AbstractByteBuf.leakDetector.track((ByteBuf)var0);
            if (var2 != null) {
               var0 = new SimpleLeakAwareByteBuf((ByteBuf)var0, var2);
            }
            break;
         case ADVANCED:
         case PARANOID:
            ResourceLeakTracker var1 = AbstractByteBuf.leakDetector.track((ByteBuf)var0);
            if (var1 != null) {
               var0 = new AdvancedLeakAwareByteBuf((ByteBuf)var0, var1);
            }
      }

      return (ByteBuf)var0;
   }

   protected static CompositeByteBuf toLeakAwareBuffer(CompositeByteBuf var0) {
      switch (ResourceLeakDetector.getLevel()) {
         case SIMPLE:
            ResourceLeakTracker var2 = AbstractByteBuf.leakDetector.track((ByteBuf)var0);
            if (var2 != null) {
               var0 = new SimpleLeakAwareCompositeByteBuf((CompositeByteBuf)var0, var2);
            }
            break;
         case ADVANCED:
         case PARANOID:
            ResourceLeakTracker var1 = AbstractByteBuf.leakDetector.track((ByteBuf)var0);
            if (var1 != null) {
               var0 = new AdvancedLeakAwareCompositeByteBuf((CompositeByteBuf)var0, var1);
            }
      }

      return (CompositeByteBuf)var0;
   }

   protected AbstractByteBufAllocator() {
      this(false);
   }

   protected AbstractByteBufAllocator(boolean var1) {
      this.directByDefault = var1 && PlatformDependent.hasUnsafe();
      this.emptyBuf = new EmptyByteBuf(this);
   }

   @Override
   public ByteBuf buffer() {
      return this.directByDefault ? this.directBuffer() : this.heapBuffer();
   }

   @Override
   public ByteBuf buffer(int var1) {
      return this.directByDefault ? this.directBuffer(var1) : this.heapBuffer(var1);
   }

   @Override
   public ByteBuf buffer(int var1, int var2) {
      return this.directByDefault ? this.directBuffer(var1, var2) : this.heapBuffer(var1, var2);
   }

   @Override
   public ByteBuf ioBuffer() {
      return !PlatformDependent.hasUnsafe() && !this.isDirectBufferPooled() ? this.heapBuffer(256) : this.directBuffer(256);
   }

   @Override
   public ByteBuf ioBuffer(int var1) {
      return !PlatformDependent.hasUnsafe() && !this.isDirectBufferPooled() ? this.heapBuffer(var1) : this.directBuffer(var1);
   }

   @Override
   public ByteBuf ioBuffer(int var1, int var2) {
      return !PlatformDependent.hasUnsafe() && !this.isDirectBufferPooled() ? this.heapBuffer(var1, var2) : this.directBuffer(var1, var2);
   }

   @Override
   public ByteBuf heapBuffer() {
      return this.heapBuffer(256, Integer.MAX_VALUE);
   }

   @Override
   public ByteBuf heapBuffer(int var1) {
      return this.heapBuffer(var1, Integer.MAX_VALUE);
   }

   @Override
   public ByteBuf heapBuffer(int var1, int var2) {
      if (var1 == 0 && var2 == 0) {
         return this.emptyBuf;
      } else {
         validate(var1, var2);
         return this.newHeapBuffer(var1, var2);
      }
   }

   @Override
   public ByteBuf directBuffer() {
      return this.directBuffer(256, Integer.MAX_VALUE);
   }

   @Override
   public ByteBuf directBuffer(int var1) {
      return this.directBuffer(var1, Integer.MAX_VALUE);
   }

   @Override
   public ByteBuf directBuffer(int var1, int var2) {
      if (var1 == 0 && var2 == 0) {
         return this.emptyBuf;
      } else {
         validate(var1, var2);
         return this.newDirectBuffer(var1, var2);
      }
   }

   @Override
   public CompositeByteBuf compositeBuffer() {
      return this.directByDefault ? this.compositeDirectBuffer() : this.compositeHeapBuffer();
   }

   @Override
   public CompositeByteBuf compositeBuffer(int var1) {
      return this.directByDefault ? this.compositeDirectBuffer(var1) : this.compositeHeapBuffer(var1);
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer() {
      return this.compositeHeapBuffer(16);
   }

   @Override
   public CompositeByteBuf compositeHeapBuffer(int var1) {
      return toLeakAwareBuffer(new CompositeByteBuf(this, false, var1));
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer() {
      return this.compositeDirectBuffer(16);
   }

   @Override
   public CompositeByteBuf compositeDirectBuffer(int var1) {
      return toLeakAwareBuffer(new CompositeByteBuf(this, true, var1));
   }

   private static void validate(int var0, int var1) {
      ObjectUtil.checkPositiveOrZero(var0, "initialCapacity");
      if (var0 > var1) {
         throw new IllegalArgumentException(String.format("initialCapacity: %d (expected: not greater than maxCapacity(%d)", var0, var1));
      }
   }

   protected abstract ByteBuf newHeapBuffer(int var1, int var2);

   protected abstract ByteBuf newDirectBuffer(int var1, int var2);

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(directByDefault: " + this.directByDefault + ')';
   }

   @Override
   public int calculateNewCapacity(int var1, int var2) {
      ObjectUtil.checkPositiveOrZero(var1, "minNewCapacity");
      if (var1 > var2) {
         throw new IllegalArgumentException(String.format("minNewCapacity: %d (expected: not greater than maxCapacity(%d)", var1, var2));
      } else {
         int var3 = 4194304;
         if (var1 == 4194304) {
            return 4194304;
         } else if (var1 > 4194304) {
            int var5 = var1 / 4194304 * 4194304;
            if (var5 > var2 - 4194304) {
               var5 = var2;
            } else {
               var5 += 4194304;
            }

            return var5;
         } else {
            int var4 = MathUtil.findNextPositivePowerOfTwo(Math.max(var1, 64));
            return Math.min(var4, var2);
         }
      }
   }

   static {
      ResourceLeakDetector.addExclusions(AbstractByteBufAllocator.class, "toLeakAwareBuffer");
   }
}
