package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class UnpooledUnsafeDirectByteBuf extends UnpooledDirectByteBuf {
   long memoryAddress;

   public UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3) {
      super(var1, var2, var3, false, true);
   }

   UnpooledUnsafeDirectByteBuf(ByteBufAllocator var1, ByteBuffer var2, int var3, boolean var4) {
      super(var1, var2, var3, var4, false);
   }

   @Override
   final void setByteBuffer(ByteBuffer var1, boolean var2) {
      super.setByteBuffer(var1, var2);
      this.memoryAddress = PlatformDependent.directBufferAddress(var1);
   }

   @Override
   public boolean hasMemoryAddress() {
      return true;
   }

   @Override
   public long memoryAddress() {
      this.ensureAccessible();
      return this.memoryAddress;
   }

   @Override
   public byte getByte(int var1) {
      this.checkIndex(var1);
      return this._getByte(var1);
   }

   @Override
   protected byte _getByte(int var1) {
      return UnsafeByteBufUtil.getByte(this.addr(var1));
   }

   @Override
   public short getShort(int var1) {
      this.checkIndex(var1, 2);
      return this._getShort(var1);
   }

   @Override
   protected short _getShort(int var1) {
      return UnsafeByteBufUtil.getShort(this.addr(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return UnsafeByteBufUtil.getShortLE(this.addr(var1));
   }

   @Override
   public int getUnsignedMedium(int var1) {
      this.checkIndex(var1, 3);
      return this._getUnsignedMedium(var1);
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.addr(var1));
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.addr(var1));
   }

   @Override
   public int getInt(int var1) {
      this.checkIndex(var1, 4);
      return this._getInt(var1);
   }

   @Override
   protected int _getInt(int var1) {
      return UnsafeByteBufUtil.getInt(this.addr(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return UnsafeByteBufUtil.getIntLE(this.addr(var1));
   }

   @Override
   public long getLong(int var1) {
      this.checkIndex(var1, 8);
      return this._getLong(var1);
   }

   @Override
   protected long _getLong(int var1) {
      return UnsafeByteBufUtil.getLong(this.addr(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return UnsafeByteBufUtil.getLongLE(this.addr(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   void getBytes(int var1, byte[] var2, int var3, int var4, boolean var5) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3, var4);
   }

   @Override
   void getBytes(int var1, ByteBuffer var2, boolean var3) {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2);
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      this.checkIndex(var1);
      this._setByte(var1, var2);
      return this;
   }

   @Override
   protected void _setByte(int var1, int var2) {
      UnsafeByteBufUtil.setByte(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      this.checkIndex(var1, 2);
      this._setShort(var1, var2);
      return this;
   }

   @Override
   protected void _setShort(int var1, int var2) {
      UnsafeByteBufUtil.setShort(this.addr(var1), var2);
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      UnsafeByteBufUtil.setShortLE(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      this.checkIndex(var1, 3);
      this._setMedium(var1, var2);
      return this;
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      UnsafeByteBufUtil.setMedium(this.addr(var1), var2);
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      UnsafeByteBufUtil.setMediumLE(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      this.checkIndex(var1, 4);
      this._setInt(var1, var2);
      return this;
   }

   @Override
   protected void _setInt(int var1, int var2) {
      UnsafeByteBufUtil.setInt(this.addr(var1), var2);
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      UnsafeByteBufUtil.setIntLE(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      this.checkIndex(var1, 8);
      this._setLong(var1, var2);
      return this;
   }

   @Override
   protected void _setLong(int var1, long var2) {
      UnsafeByteBufUtil.setLong(this.addr(var1), var2);
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      UnsafeByteBufUtil.setLongLE(this.addr(var1), var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2);
      return this;
   }

   @Override
   void getBytes(int var1, OutputStream var2, int var3, boolean var4) throws IOException {
      UnsafeByteBufUtil.getBytes(this, this.addr(var1), var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return UnsafeByteBufUtil.setBytes(this, this.addr(var1), var1, var2, var3);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return UnsafeByteBufUtil.copy(this, this.addr(var1), var1, var2);
   }

   final long addr(int var1) {
      return this.memoryAddress + (long)var1;
   }

   @Override
   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeDirectSwappedByteBuf(this) : super.newSwappedByteBuf());
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      this.checkIndex(var1, var2);
      UnsafeByteBufUtil.setZero(this.addr(var1), var2);
      return this;
   }

   @Override
   public ByteBuf writeZero(int var1) {
      this.ensureWritable(var1);
      int var2 = this.writerIndex;
      UnsafeByteBufUtil.setZero(this.addr(var2), var1);
      this.writerIndex = var2 + var1;
      return this;
   }
}
