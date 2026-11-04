package io.netty.channel.unix;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class IovArray implements ChannelOutboundBuffer.MessageProcessor {
   private static final int ADDRESS_SIZE = Buffer.addressSize();
   public static final int IOV_SIZE = 2 * ADDRESS_SIZE;
   private static final int MAX_CAPACITY = Limits.IOV_MAX * IOV_SIZE;
   private final long memoryAddress;
   private final ByteBuf memory;
   private int count;
   private long size;
   private long maxBytes = Limits.SSIZE_MAX;

   public IovArray() {
      this(Unpooled.wrappedBuffer(Buffer.allocateDirectWithNativeOrder(MAX_CAPACITY)).setIndex(0, 0));
   }

   public IovArray(ByteBuf var1) {
      assert var1.writerIndex() == 0;

      assert var1.readerIndex() == 0;

      this.memory = PlatformDependent.hasUnsafe()
         ? var1
         : var1.order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      if (var1.hasMemoryAddress()) {
         this.memoryAddress = var1.memoryAddress();
      } else {
         this.memoryAddress = Buffer.memoryAddress(var1.internalNioBuffer(0, var1.capacity()));
      }
   }

   public void clear() {
      this.count = 0;
      this.size = 0L;
   }

   @Deprecated
   public boolean add(ByteBuf var1) {
      return this.add(var1, var1.readerIndex(), var1.readableBytes());
   }

   public boolean add(ByteBuf var1, int var2, int var3) {
      if (this.count == Limits.IOV_MAX) {
         return false;
      } else if (var1.nioBufferCount() == 1) {
         if (var3 == 0) {
            return true;
         } else if (var1.hasMemoryAddress()) {
            return this.add(this.memoryAddress, var1.memoryAddress() + (long)var2, var3);
         } else {
            ByteBuffer var10 = var1.internalNioBuffer(var2, var3);
            return this.add(this.memoryAddress, Buffer.memoryAddress(var10) + (long)var10.position(), var3);
         }
      } else {
         ByteBuffer[] var4 = var1.nioBuffers(var2, var3);

         for (ByteBuffer var8 : var4) {
            int var9 = var8.remaining();
            if (var9 != 0 && (!this.add(this.memoryAddress, Buffer.memoryAddress(var8) + (long)var8.position(), var9) || this.count == Limits.IOV_MAX)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean add(long var1, long var3, int var5) {
      assert var3 != 0L;

      if ((this.maxBytes - (long)var5 >= this.size || this.count <= 0) && this.memory.capacity() >= (this.count + 1) * IOV_SIZE) {
         int var6 = idx(this.count);
         int var7 = var6 + ADDRESS_SIZE;
         this.size += (long)var5;
         this.count++;
         if (ADDRESS_SIZE == 8) {
            if (PlatformDependent.hasUnsafe()) {
               PlatformDependent.putLong((long)var6 + var1, var3);
               PlatformDependent.putLong((long)var7 + var1, (long)var5);
            } else {
               this.memory.setLong(var6, var3);
               this.memory.setLong(var7, (long)var5);
            }
         } else {
            assert ADDRESS_SIZE == 4;

            if (PlatformDependent.hasUnsafe()) {
               PlatformDependent.putInt((long)var6 + var1, (int)var3);
               PlatformDependent.putInt((long)var7 + var1, var5);
            } else {
               this.memory.setInt(var6, (int)var3);
               this.memory.setInt(var7, var5);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public int count() {
      return this.count;
   }

   public long size() {
      return this.size;
   }

   public void maxBytes(long var1) {
      this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(var1, "maxBytes"));
   }

   public long maxBytes() {
      return this.maxBytes;
   }

   public long memoryAddress(int var1) {
      return this.memoryAddress + (long)idx(var1);
   }

   public void release() {
      this.memory.release();
   }

   @Override
   public boolean processMessage(Object var1) throws Exception {
      if (var1 instanceof ByteBuf) {
         ByteBuf var2 = (ByteBuf)var1;
         return this.add(var2, var2.readerIndex(), var2.readableBytes());
      } else {
         return false;
      }
   }

   private static int idx(int var0) {
      return IOV_SIZE * var0;
   }
}
