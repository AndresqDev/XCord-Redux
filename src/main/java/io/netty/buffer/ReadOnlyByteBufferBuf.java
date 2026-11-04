package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class ReadOnlyByteBufferBuf extends AbstractReferenceCountedByteBuf {
   protected final ByteBuffer buffer;
   private final ByteBufAllocator allocator;
   private ByteBuffer tmpNioBuf;

   ReadOnlyByteBufferBuf(ByteBufAllocator var1, ByteBuffer var2) {
      super(var2.remaining());
      if (!var2.isReadOnly()) {
         throw new IllegalArgumentException("must be a readonly buffer: " + StringUtil.simpleClassName(var2));
      } else {
         this.allocator = var1;
         this.buffer = var2.slice().order(ByteOrder.BIG_ENDIAN);
         this.writerIndex(this.buffer.limit());
      }
   }

   @Override
   protected void deallocate() {
   }

   @Override
   public boolean isWritable() {
      return false;
   }

   @Override
   public boolean isWritable(int var1) {
      return false;
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      return 1;
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
   public short getShortLE(int var1) {
      this.ensureAccessible();
      return this._getShortLE(var1);
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
   public int getUnsignedMediumLE(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMediumLE(var1);
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
   public int getIntLE(int var1) {
      this.ensureAccessible();
      return this._getIntLE(var1);
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
   public long getLongLE(int var1) {
      this.ensureAccessible();
      return this._getLongLE(var1);
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
      this.checkDstIndex(var1, var4, var3, var2.length);
      ByteBuffer var5 = this.internalNioBuffer();
      ((Buffer)var5).clear().position(var1).limit(var1 + var4);
      var5.get(var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex(var1, var2.remaining());
      ByteBuffer var3 = this.internalNioBuffer();
      ((Buffer)var3).clear().position(var1).limit(var1 + var2.remaining());
      var2.put(var3);
      return this;
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setByte(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setShort(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setInt(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setLong(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int capacity() {
      return this.maxCapacity();
   }

   @Override
   public ByteBuf capacity(int var1) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.allocator;
   }

   @Override
   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }

   @Override
   public boolean isReadOnly() {
      return this.buffer.isReadOnly();
   }

   @Override
   public boolean isDirect() {
      return this.buffer.isDirect();
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      if (var3 == 0) {
         return this;
      } else {
         if (this.buffer.hasArray()) {
            var2.write(this.buffer.array(), var1 + this.buffer.arrayOffset(), var3);
         } else {
            byte[] var4 = ByteBufUtil.threadLocalTempArray(var3);
            ByteBuffer var5 = this.internalNioBuffer();
            ((Buffer)var5).clear().position(var1);
            var5.get(var4, 0, var3);
            var2.write(var4, 0, var3);
         }

         return this;
      }
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();
      if (var3 == 0) {
         return 0;
      } else {
         ByteBuffer var4 = this.internalNioBuffer();
         ((Buffer)var4).clear().position(var1).limit(var1 + var3);
         return var2.write(var4);
      }
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();
      if (var5 == 0) {
         return 0;
      } else {
         ByteBuffer var6 = this.internalNioBuffer();
         ((Buffer)var6).clear().position(var1).limit(var1 + var5);
         return var2.write(var6, var3);
      }
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      throw new ReadOnlyBufferException();
   }

   protected final ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = this.buffer.duplicate();
      }

      return var1;
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.ensureAccessible();

      ByteBuffer var3;
      try {
         var3 = (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var2);
      } catch (IllegalArgumentException var5) {
         throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (var1 + var2));
      }

      ByteBuf var4 = var3.isDirect() ? this.alloc().directBuffer(var2) : this.alloc().heapBuffer(var2);
      var4.writeBytes(var3);
      return var4;
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
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return (ByteBuffer)((Buffer)this.buffer.duplicate()).position(var1).limit(var1 + var2);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.ensureAccessible();
      return (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var2);
   }

   @Override
   public final boolean isContiguous() {
      return true;
   }

   @Override
   public boolean hasArray() {
      return this.buffer.hasArray();
   }

   @Override
   public byte[] array() {
      return this.buffer.array();
   }

   @Override
   public int arrayOffset() {
      return this.buffer.arrayOffset();
   }

   @Override
   public boolean hasMemoryAddress() {
      return false;
   }

   @Override
   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }
}
