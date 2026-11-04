package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class ReadOnlyUnsafeDirectByteBuf extends ReadOnlyByteBufferBuf {
   private final long memoryAddress = PlatformDependent.directBufferAddress(this.buffer);

   ReadOnlyUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2) {
      super(var1, var2);
   }

   @Override
   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.addr(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.addr(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.addr(var1));
   }

   @Override
   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.addr(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.addr(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      ObjectUtil.checkNotNull(var2, "dst");
      if (var3 >= 0 && var3 <= var2.capacity() - var4) {
         if (var2.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(var1), var2.memoryAddress() + (long)var3, (long)var4);
         } else if (var2.hasArray()) {
            PlatformDependent.copyMemory(this.addr(var1), var2.array(), var2.arrayOffset() + var3, (long)var4);
         } else {
            var2.setBytes(var3, this, var1, var4);
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException("dstIndex: " + var3);
      }
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex(var1, var4);
      ObjectUtil.checkNotNull(var2, "dst");
      if (var3 >= 0 && var3 <= var2.length - var4) {
         if (var4 != 0) {
            PlatformDependent.copyMemory(this.addr(var1), var2, var3, (long)var4);
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", var3, var4, var2.length));
      }
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      ByteBuf var3 = this.alloc().directBuffer(var2, this.maxCapacity());
      if (var2 != 0) {
         if (var3.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(var1), var3.memoryAddress(), (long)var2);
            var3.setIndex(0, var2);
         } else {
            var3.writeBytes(this, var1, var2);
         }
      }

      return var3;
   }

   @Override
   public boolean hasMemoryAddress() {
      return true;
   }

   @Override
   public long memoryAddress() {
      return this.memoryAddress;
   }

   private long addr(int var1) {
      return this.memoryAddress + (long)var1;
   }
}
