package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledDirectByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   ByteBuffer buffer;
   private ByteBuffer tmpNioBuf;
   private int capacity;
   private boolean doNotFree;

   public UnpooledDirectByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "alloc");
      ObjectUtil.checkPositiveOrZero(var2, "initialCapacity");
      ObjectUtil.checkPositiveOrZero(var3, "maxCapacity");
      if (var2 > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2, var3));
      } else {
         this.alloc = var1;
         this.setByteBuffer(this.allocateDirect(var2), false);
      }
   }

   protected UnpooledDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3) {
      this(var1, var2, var3, false, true);
   }

   UnpooledDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3, boolean var4, boolean var5) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "alloc");
      ObjectUtil.checkNotNull(var2, "initialBuffer");
      if (!var2.isDirect()) {
         throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
      } else if (var2.isReadOnly()) {
         throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
      } else {
         int var6 = var2.remaining();
         if (var6 > var3) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var6, var3));
         } else {
            this.alloc = var1;
            this.doNotFree = !var4;
            this.setByteBuffer((var5 ? var2.slice() : var2).order(ByteOrder.BIG_ENDIAN), false);
            this.writerIndex(var6);
         }
      }
   }

   protected ByteBuffer allocateDirect(int var1) {
      return ByteBuffer.allocateDirect(var1);
   }

   protected void freeDirect(ByteBuffer var1) {
      PlatformDependent.freeDirectBuffer(var1);
   }

   void setByteBuffer(ByteBuffer var1, boolean var2) {
      if (var2) {
         ByteBuffer var3 = this.buffer;
         if (var3 != null) {
            if (this.doNotFree) {
               this.doNotFree = false;
            } else {
               this.freeDirect(var3);
            }
         }
      }

      this.buffer = var1;
      this.tmpNioBuf = null;
      this.capacity = var1.remaining();
   }

   @Override
   public boolean isDirect() {
      return true;
   }

   @Override
   public int capacity() {
      return this.capacity;
   }

   @Override
   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      int var2 = this.capacity;
      if (var1 == var2) {
         return this;
      } else {
         int var3;
         if (var1 > var2) {
            var3 = var2;
         } else {
            this.trimIndicesToCapacity(var1);
            var3 = var1;
         }

         ByteBuffer var4 = this.buffer;
         ByteBuffer var5 = this.allocateDirect(var1);
         ((Buffer)var4).position(0).limit(var3);
         ((Buffer)var5).position(0).limit(var3);
         ((Buffer)var5.put(var4)).clear();
         this.setByteBuffer(var5, true);
         return this;
      }
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   @Override
   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   @Override
   public boolean hasArray() {
      return false;
   }

   @Override
   public byte[] array() {
      throw new UnsupportedOperationException("direct buffer");
   }

   @Override
   public int arrayOffset() {
      throw new UnsupportedOperationException("direct buffer");
   }

   @Override
   public boolean hasMemoryAddress() {
      return false;
   }

   @Override
   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte getByte(int var1) {
      this.ensureAccessible();
      return this._getByte(var1);
   }

   @Override
   protected byte _getByte(int var1) {
      return this.buffer.get(var1);
   }

   @Override
   public short getShort(int var1) {
      this.ensureAccessible();
      return this._getShort(var1);
   }

   @Override
   protected short _getShort(int var1) {
      return this.buffer.getShort(var1);
   }

   @Override
   protected short _getShortLE(int var1) {
      return ByteBufUtil.swapShort(this.buffer.getShort(var1));
   }

   @Override
   public int getUnsignedMedium(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMedium(var1);
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return (this.getByte(var1) & 0xFF) << 16 | (this.getByte(var1 + 1) & 0xFF) << 8 | this.getByte(var1 + 2) & 0xFF;
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return this.getByte(var1) & 0xFF | (this.getByte(var1 + 1) & 0xFF) << 8 | (this.getByte(var1 + 2) & 0xFF) << 16;
   }

   @Override
   public int getInt(int var1) {
      this.ensureAccessible();
      return this._getInt(var1);
   }

   @Override
   protected int _getInt(int var1) {
      return this.buffer.getInt(var1);
   }

   @Override
   protected int _getIntLE(int var1) {
      return ByteBufUtil.swapInt(this.buffer.getInt(var1));
   }

   @Override
   public long getLong(int var1) {
      this.ensureAccessible();
      return this._getLong(var1);
   }

   @Override
   protected long _getLong(int var1) {
      return this.buffer.getLong(var1);
   }

   @Override
   protected long _getLongLE(int var1) {
      return ByteBufUtil.swapLong(this.buffer.getLong(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else if (var2.nioBufferCount() > 0) {
         for (ByteBuffer var8 : var2.nioBuffers(var3, var4)) {
            int var9 = var8.remaining();
            this.getBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.setBytes(var3, this, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.getBytes(var1, var2, var3, var4, false);
      return this;
   }

   void getBytes(int var1, byte[] var2, int var3, int var4, boolean var5) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      ByteBuffer var6;
      if (var5) {
         var6 = this.internalNioBuffer();
      } else {
         var6 = this.buffer.duplicate();
      }

      ((Buffer)var6).clear().position(var1).limit(var1 + var4);
      var6.get(var2, var3, var4);
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.checkReadableBytes(var3);
      this.getBytes(this.readerIndex, var1, var2, var3, true);
      this.readerIndex += var3;
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.getBytes(var1, var2, false);
      return this;
   }

   void getBytes(int var1, ByteBuffer var2, boolean var3) {
      this.checkIndex(var1, var2.remaining());
      ByteBuffer var4;
      if (var3) {
         var4 = this.internalNioBuffer();
      } else {
         var4 = this.buffer.duplicate();
      }

      ((Buffer)var4).clear().position(var1).limit(var1 + var2.remaining());
      var2.put(var4);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      int var2 = var1.remaining();
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, true);
      this.readerIndex += var2;
      return this;
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      this.ensureAccessible();
      this._setByte(var1, var2);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      this.buffer.put(var1, (byte)var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      this.ensureAccessible();
      this._setShort(var1, var2);
      return this;
   }

   @Override
   protected void _setShort(int var1, int var2) {
      this.buffer.putShort(var1, (short)var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      this.buffer.putShort(var1, ByteBufUtil.swapShort((short)var2));
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      this.ensureAccessible();
      this._setMedium(var1, var2);
      return this;
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      this.setByte(var1, (byte)(var2 >>> 16));
      this.setByte(var1 + 1, (byte)(var2 >>> 8));
      this.setByte(var1 + 2, (byte)var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      this.setByte(var1, (byte)var2);
      this.setByte(var1 + 1, (byte)(var2 >>> 8));
      this.setByte(var1 + 2, (byte)(var2 >>> 16));
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      this.ensureAccessible();
      this._setInt(var1, var2);
      return this;
   }

   @Override
   protected void _setInt(int var1, int var2) {
      this.buffer.putInt(var1, var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      this.buffer.putInt(var1, ByteBufUtil.swapInt(var2));
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      this.ensureAccessible();
      this._setLong(var1, var2);
      return this;
   }

   @Override
   protected void _setLong(int var1, long var2) {
      this.buffer.putLong(var1, var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      this.buffer.putLong(var1, ByteBufUtil.swapLong(var2));
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.nioBufferCount() > 0) {
         for (ByteBuffer var8 : var2.nioBuffers(var3, var4)) {
            int var9 = var8.remaining();
            this.setBytes(var1, var8);
            var1 += var9;
         }
      } else {
         var2.getBytes(var3, this, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      ByteBuffer var5 = this.internalNioBuffer();
      ((Buffer)var5).clear().position(var1).limit(var1 + var4);
      var5.put(var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.ensureAccessible();
      ByteBuffer var3 = this.internalNioBuffer();
      if (var2 == var3) {
         var2 = var2.duplicate();
      }

      ((Buffer)var3).clear().position(var1).limit(var1 + var2.remaining());
      var3.put(var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.getBytes(var1, var2, var3, false);
      return this;
   }

   void getBytes(int var1, OutputStream var2, int var3, boolean var4) throws IOException {
      this.ensureAccessible();
      if (var3 != 0) {
         ByteBufUtil.readBytes(this.alloc(), var4 ? this.internalNioBuffer() : this.buffer.duplicate(), var1, var3, var2);
      }
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var2;
      return this;
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.getBytes(var1, var2, var3, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.ensureAccessible();
      if (var3 == 0) {
         return 0;
      } else {
         ByteBuffer var5;
         if (var4) {
            var5 = this.internalNioBuffer();
         } else {
            var5 = this.buffer.duplicate();
         }

         ((Buffer)var5).clear().position(var1).limit(var1 + var3);
         return var2.write(var5);
      }
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.ensureAccessible();
      if (var5 == 0) {
         return 0;
      } else {
         ByteBuffer var7 = var6 ? this.internalNioBuffer() : this.buffer.duplicate();
         ((Buffer)var7).clear().position(var1).limit(var1 + var5);
         return var2.write(var7, var3);
      }
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      this.checkReadableBytes(var2);
      int var3 = this.getBytes(this.readerIndex, var1, var2, true);
      this.readerIndex += var3;
      return var3;
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      this.checkReadableBytes(var4);
      int var5 = this.getBytes(this.readerIndex, var1, var2, var4, true);
      this.readerIndex += var5;
      return var5;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      if (this.buffer.hasArray()) {
         return var2.read(this.buffer.array(), this.buffer.arrayOffset() + var1, var3);
      } else {
         byte[] var4 = ByteBufUtil.threadLocalTempArray(var3);
         int var5 = var2.read(var4, 0, var3);
         if (var5 <= 0) {
            return var5;
         } else {
            ByteBuffer var6 = this.internalNioBuffer();
            ((Buffer)var6).clear().position(var1);
            var6.put(var4, 0, var5);
            return var5;
         }
      }
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();
      ByteBuffer var4 = this.internalNioBuffer();
      ((Buffer)var4).clear().position(var1).limit(var1 + var3);

      try {
         return var2.read(var4);
      } catch (ClosedChannelException var6) {
         return -1;
      }
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();
      ByteBuffer var6 = this.internalNioBuffer();
      ((Buffer)var6).clear().position(var1).limit(var1 + var5);

      try {
         return var2.read(var6, var3);
      } catch (ClosedChannelException var8) {
         return -1;
      }
   }

   @Override
   public int nioBufferCount() {
      return 1;
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   @Override
   public final boolean isContiguous() {
      return true;
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.ensureAccessible();

      ByteBuffer var3;
      try {
         var3 = (ByteBuffer)((Buffer)this.buffer.duplicate()).clear().position(var1).limit(var1 + var2);
      } catch (IllegalArgumentException var5) {
         throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (var1 + var2));
      }

      return this.alloc().directBuffer(var2, this.maxCapacity()).writeBytes(var3);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var2);
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = this.buffer.duplicate();
      }

      return var1;
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return ((ByteBuffer)((Buffer)this.buffer.duplicate()).position(var1).limit(var1 + var2)).slice();
   }

   @Override
   protected void deallocate() {
      ByteBuffer var1 = this.buffer;
      if (var1 != null) {
         this.buffer = null;
         if (!this.doNotFree) {
            this.freeDirect(var1);
         }
      }
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }
}
