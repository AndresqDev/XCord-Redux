package io.netty.buffer;

import io.netty.util.internal.EmptyArrays;
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

public class UnpooledHeapByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   byte[] array;
   private ByteBuffer tmpNioBuf;

   public UnpooledHeapByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var3);
      if (var2 > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2, var3));
      } else {
         this.alloc = ObjectUtil.checkNotNull(var1, "alloc");
         this.setArray(this.allocateArray(var2));
         this.setIndex(0, 0);
      }
   }

   protected UnpooledHeapByteBuf(ByteBufAllocator var1, byte[] var2, int var3) {
      super(var3);
      ObjectUtil.checkNotNull(var1, "alloc");
      ObjectUtil.checkNotNull(var2, "initialArray");
      if (var2.length > var3) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", var2.length, var3));
      } else {
         this.alloc = var1;
         this.setArray(var2);
         this.setIndex(0, var2.length);
      }
   }

   protected byte[] allocateArray(int var1) {
      return new byte[var1];
   }

   protected void freeArray(byte[] var1) {
   }

   private void setArray(byte[] var1) {
      this.array = var1;
      this.tmpNioBuf = null;
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
   public boolean isDirect() {
      return false;
   }

   @Override
   public int capacity() {
      return this.array.length;
   }

   @Override
   public ByteBuf capacity(int var1) {
      this.checkNewCapacity(var1);
      byte[] var2 = this.array;
      int var3 = var2.length;
      if (var1 == var3) {
         return this;
      } else {
         int var4;
         if (var1 > var3) {
            var4 = var3;
         } else {
            this.trimIndicesToCapacity(var1);
            var4 = var1;
         }

         byte[] var5 = this.allocateArray(var1);
         System.arraycopy(var2, 0, var5, 0, var4);
         this.setArray(var5);
         this.freeArray(var2);
         return this;
      }
   }

   @Override
   public boolean hasArray() {
      return true;
   }

   @Override
   public byte[] array() {
      this.ensureAccessible();
      return this.array;
   }

   @Override
   public int arrayOffset() {
      return 0;
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
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(this.array, var1, var2.memoryAddress() + (long)var3, (long)var4);
      } else if (var2.hasArray()) {
         this.getBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.setBytes(var3, this.array, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkDstIndex(var1, var4, var3, var2.length);
      System.arraycopy(this.array, var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.ensureAccessible();
      var2.put(this.array, var1, var2.remaining());
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      var2.write(this.array, var1, var3);
      return this;
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();
      return this.getBytes(var1, var2, var3, false);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();
      return this.getBytes(var1, var2, var3, var5, false);
   }

   private int getBytes(int var1, GatheringByteChannel var2, int var3, boolean var4) throws IOException {
      this.ensureAccessible();
      ByteBuffer var5;
      if (var4) {
         var5 = this.internalNioBuffer();
      } else {
         var5 = ByteBuffer.wrap(this.array);
      }

      return var2.write((ByteBuffer)((Buffer)var5).clear().position(var1).limit(var1 + var3));
   }

   private int getBytes(int var1, FileChannel var2, long var3, int var5, boolean var6) throws IOException {
      this.ensureAccessible();
      ByteBuffer var7 = var6 ? this.internalNioBuffer() : ByteBuffer.wrap(this.array);
      return var2.write((ByteBuffer)((Buffer)var7).clear().position(var1).limit(var1 + var5), var3);
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
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.capacity());
      if (var2.hasMemoryAddress()) {
         PlatformDependent.copyMemory(var2.memoryAddress() + (long)var3, this.array, var1, (long)var4);
      } else if (var2.hasArray()) {
         this.setBytes(var1, var2.array(), var2.arrayOffset() + var3, var4);
      } else {
         var2.getBytes(var3, this.array, var1, var4);
      }

      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkSrcIndex(var1, var4, var3, var2.length);
      System.arraycopy(var2, var3, this.array, var1, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.ensureAccessible();
      var2.get(this.array, var1, var2.remaining());
      return this;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.ensureAccessible();
      return var2.read(this.array, var1, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.ensureAccessible();

      try {
         return var2.read((ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var3));
      } catch (ClosedChannelException var5) {
         return -1;
      }
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.ensureAccessible();

      try {
         return var2.read((ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var5), var3);
      } catch (ClosedChannelException var7) {
         return -1;
      }
   }

   @Override
   public int nioBufferCount() {
      return 1;
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.ensureAccessible();
      return ByteBuffer.wrap(this.array, var1, var2).slice();
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return new ByteBuffer[]{this.nioBuffer(var1, var2)};
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return (ByteBuffer)((Buffer)this.internalNioBuffer()).clear().position(var1).limit(var1 + var2);
   }

   @Override
   public final boolean isContiguous() {
      return true;
   }

   @Override
   public byte getByte(int var1) {
      this.ensureAccessible();
      return this._getByte(var1);
   }

   @Override
   protected byte _getByte(int var1) {
      return HeapByteBufUtil.getByte(this.array, var1);
   }

   @Override
   public short getShort(int var1) {
      this.ensureAccessible();
      return this._getShort(var1);
   }

   @Override
   protected short _getShort(int var1) {
      return HeapByteBufUtil.getShort(this.array, var1);
   }

   @Override
   public short getShortLE(int var1) {
      this.ensureAccessible();
      return this._getShortLE(var1);
   }

   @Override
   protected short _getShortLE(int var1) {
      return HeapByteBufUtil.getShortLE(this.array, var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMedium(var1);
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return HeapByteBufUtil.getUnsignedMedium(this.array, var1);
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      this.ensureAccessible();
      return this._getUnsignedMediumLE(var1);
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return HeapByteBufUtil.getUnsignedMediumLE(this.array, var1);
   }

   @Override
   public int getInt(int var1) {
      this.ensureAccessible();
      return this._getInt(var1);
   }

   @Override
   protected int _getInt(int var1) {
      return HeapByteBufUtil.getInt(this.array, var1);
   }

   @Override
   public int getIntLE(int var1) {
      this.ensureAccessible();
      return this._getIntLE(var1);
   }

   @Override
   protected int _getIntLE(int var1) {
      return HeapByteBufUtil.getIntLE(this.array, var1);
   }

   @Override
   public long getLong(int var1) {
      this.ensureAccessible();
      return this._getLong(var1);
   }

   @Override
   protected long _getLong(int var1) {
      return HeapByteBufUtil.getLong(this.array, var1);
   }

   @Override
   public long getLongLE(int var1) {
      this.ensureAccessible();
      return this._getLongLE(var1);
   }

   @Override
   protected long _getLongLE(int var1) {
      return HeapByteBufUtil.getLongLE(this.array, var1);
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      this.ensureAccessible();
      this._setByte(var1, var2);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      HeapByteBufUtil.setByte(this.array, var1, var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      this.ensureAccessible();
      this._setShort(var1, var2);
      return this;
   }

   @Override
   protected void _setShort(int var1, int var2) {
      HeapByteBufUtil.setShort(this.array, var1, var2);
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      this.ensureAccessible();
      this._setShortLE(var1, var2);
      return this;
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      HeapByteBufUtil.setShortLE(this.array, var1, var2);
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      this.ensureAccessible();
      this._setMedium(var1, var2);
      return this;
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      HeapByteBufUtil.setMedium(this.array, var1, var2);
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      this.ensureAccessible();
      this._setMediumLE(var1, var2);
      return this;
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      HeapByteBufUtil.setMediumLE(this.array, var1, var2);
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      this.ensureAccessible();
      this._setInt(var1, var2);
      return this;
   }

   @Override
   protected void _setInt(int var1, int var2) {
      HeapByteBufUtil.setInt(this.array, var1, var2);
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      this.ensureAccessible();
      this._setIntLE(var1, var2);
      return this;
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      HeapByteBufUtil.setIntLE(this.array, var1, var2);
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      this.ensureAccessible();
      this._setLong(var1, var2);
      return this;
   }

   @Override
   protected void _setLong(int var1, long var2) {
      HeapByteBufUtil.setLong(this.array, var1, var2);
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      this.ensureAccessible();
      this._setLongLE(var1, var2);
      return this;
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      HeapByteBufUtil.setLongLE(this.array, var1, var2);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.alloc().heapBuffer(var2, this.maxCapacity()).writeBytes(this.array, var1, var2);
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer var1 = this.tmpNioBuf;
      if (var1 == null) {
         this.tmpNioBuf = var1 = ByteBuffer.wrap(this.array);
      }

      return var1;
   }

   @Override
   protected void deallocate() {
      this.freeArray(this.array);
      this.array = EmptyArrays.EMPTY_BYTES;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }
}
