package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

class UnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
   UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   protected ByteBuffer allocateDirect(int var1) {
      return PlatformDependent.allocateDirectNoCleaner(var1);
   }

   ByteBuffer reallocateDirect(ByteBuffer var1, int var2) {
      return PlatformDependent.reallocateDirectNoCleaner(var1, var2);
   }

   @Override
   protected void freeDirect(ByteBuffer var1) {
      PlatformDependent.freeDirectNoCleaner(var1);
   }

   @Override
   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.capacity();
      if (var1 == var2) {
         return this;
      } else {
         this.trimIndicesToCapacity(var1);
         this.setByteBuffer(this.reallocateDirect(this.buffer, var1), false);
         return this;
      }
   }
}
