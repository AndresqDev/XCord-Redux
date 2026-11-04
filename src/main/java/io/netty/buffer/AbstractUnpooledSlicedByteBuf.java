package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.internal.MathUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

abstract class AbstractUnpooledSlicedByteBuf extends AbstractDerivedByteBuf {
   private final ByteBuf buffer;
   private final int adjustment;

   AbstractUnpooledSlicedByteBuf(ByteBuf var1, int var2, int var3) {
      super(var3);
      checkSliceOutOfBounds(var2, var3, var1);
      if (var1 instanceof AbstractUnpooledSlicedByteBuf) {
         this.buffer = ((AbstractUnpooledSlicedByteBuf)var1).buffer;
         this.adjustment = ((AbstractUnpooledSlicedByteBuf)var1).adjustment + var2;
      } else if (var1 instanceof DuplicatedByteBuf) {
         this.buffer = var1.unwrap();
         this.adjustment = var2;
      } else {
         this.buffer = var1;
         this.adjustment = var2;
      }

      this.initLength(var3);
      this.writerIndex(var3);
   }

   void initLength(int var1) {
   }

   int length() {
      return this.capacity();
   }

   @Override
   public ByteBuf unwrap() {
      return this.buffer;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.unwrap().alloc();
   }

   @Deprecated
   @Override
   public ByteOrder order() {
      return this.unwrap().order();
   }

   @Override
   public boolean isDirect() {
      return this.unwrap().isDirect();
   }

   @Override
   public ByteBuf capacity(int var1) {
      throw new UnsupportedOperationException("sliced buffer");
   }

   @Override
   public boolean hasArray() {
      return this.unwrap().hasArray();
   }

   @Override
   public byte[] array() {
      return this.unwrap().array();
   }

   @Override
   public int arrayOffset() {
      return this.idx(this.unwrap().arrayOffset());
   }

   @Override
   public boolean hasMemoryAddress() {
      return this.unwrap().hasMemoryAddress();
   }

   @Override
   public long memoryAddress() {
      return this.unwrap().memoryAddress() + (long)this.adjustment;
   }

   @Override
   public byte getByte(int var1) {
      this.checkIndex0(var1, 1);
      return this.unwrap().getByte(this.idx(var1));
   }

   @Override
   protected byte _getByte(int var1) {
      return this.unwrap().getByte(this.idx(var1));
   }

   @Override
   public short getShort(int var1) {
      this.checkIndex0(var1, 2);
      return this.unwrap().getShort(this.idx(var1));
   }

   @Override
   protected short _getShort(int var1) {
      return this.unwrap().getShort(this.idx(var1));
   }

   @Override
   public short getShortLE(int var1) {
      this.checkIndex0(var1, 2);
      return this.unwrap().getShortLE(this.idx(var1));
   }

   @Override
   protected short _getShortLE(int var1) {
      return this.unwrap().getShortLE(this.idx(var1));
   }

   @Override
   public int getUnsignedMedium(int var1) {
      this.checkIndex0(var1, 3);
      return this.unwrap().getUnsignedMedium(this.idx(var1));
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(this.idx(var1));
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      this.checkIndex0(var1, 3);
      return this.unwrap().getUnsignedMediumLE(this.idx(var1));
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(this.idx(var1));
   }

   @Override
   public int getInt(int var1) {
      this.checkIndex0(var1, 4);
      return this.unwrap().getInt(this.idx(var1));
   }

   @Override
   protected int _getInt(int var1) {
      return this.unwrap().getInt(this.idx(var1));
   }

   @Override
   public int getIntLE(int var1) {
      this.checkIndex0(var1, 4);
      return this.unwrap().getIntLE(this.idx(var1));
   }

   @Override
   protected int _getIntLE(int var1) {
      return this.unwrap().getIntLE(this.idx(var1));
   }

   @Override
   public long getLong(int var1) {
      this.checkIndex0(var1, 8);
      return this.unwrap().getLong(this.idx(var1));
   }

   @Override
   protected long _getLong(int var1) {
      return this.unwrap().getLong(this.idx(var1));
   }

   @Override
   public long getLongLE(int var1) {
      this.checkIndex0(var1, 8);
      return this.unwrap().getLongLE(this.idx(var1));
   }

   @Override
   protected long _getLongLE(int var1) {
      return this.unwrap().getLongLE(this.idx(var1));
   }

   @Override
   public ByteBuf duplicate() {
      return this.unwrap().duplicate().setIndex(this.idx(this.readerIndex()), this.idx(this.writerIndex()));
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().copy(this.idx(var1), var2);
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().slice(this.idx(var1), var2);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().getBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().getBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.checkIndex0(var1, var2.remaining());
      this.unwrap().getBytes(this.idx(var1), var2);
      return this;
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      this.checkIndex0(var1, 1);
      this.unwrap().setByte(this.idx(var1), var2);
      return this;
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      this.checkIndex0(var1, var2);
      return this.unwrap().getCharSequence(this.idx(var1), var2, var3);
   }

   @Override
   protected void _setByte(int var1, int var2) {
      this.unwrap().setByte(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      this.checkIndex0(var1, 2);
      this.unwrap().setShort(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setShort(int var1, int var2) {
      this.unwrap().setShort(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      this.checkIndex0(var1, 2);
      this.unwrap().setShortLE(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setShortLE(int var1, int var2) {
      this.unwrap().setShortLE(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      this.checkIndex0(var1, 3);
      this.unwrap().setMedium(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setMedium(int var1, int var2) {
      this.unwrap().setMedium(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      this.checkIndex0(var1, 3);
      this.unwrap().setMediumLE(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setMediumLE(int var1, int var2) {
      this.unwrap().setMediumLE(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      this.checkIndex0(var1, 4);
      this.unwrap().setInt(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setInt(int var1, int var2) {
      this.unwrap().setInt(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      this.checkIndex0(var1, 4);
      this.unwrap().setIntLE(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setIntLE(int var1, int var2) {
      this.unwrap().setIntLE(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      this.checkIndex0(var1, 8);
      this.unwrap().setLong(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setLong(int var1, long var2) {
      this.unwrap().setLong(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      this.checkIndex0(var1, 8);
      this.unwrap().setLongLE(this.idx(var1), var2);
      return this;
   }

   @Override
   protected void _setLongLE(int var1, long var2) {
      this.unwrap().setLongLE(this.idx(var1), var2);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().setBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.checkIndex0(var1, var4);
      this.unwrap().setBytes(this.idx(var1), var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.checkIndex0(var1, var2.remaining());
      this.unwrap().setBytes(this.idx(var1), var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      this.unwrap().getBytes(this.idx(var1), var2, var3);
      return this;
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().getBytes(this.idx(var1), var2, var3);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex0(var1, var5);
      return this.unwrap().getBytes(this.idx(var1), var2, var3, var5);
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().setBytes(this.idx(var1), var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      this.checkIndex0(var1, var3);
      return this.unwrap().setBytes(this.idx(var1), var2, var3);
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      this.checkIndex0(var1, var5);
      return this.unwrap().setBytes(this.idx(var1), var2, var3, var5);
   }

   @Override
   public int nioBufferCount() {
      return this.unwrap().nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().nioBuffer(this.idx(var1), var2);
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex0(var1, var2);
      return this.unwrap().nioBuffers(this.idx(var1), var2);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      this.checkIndex0(var1, var2);
      int var4 = this.unwrap().forEachByte(this.idx(var1), var2, var3);
      return var4 >= this.adjustment ? var4 - this.adjustment : -1;
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      this.checkIndex0(var1, var2);
      int var4 = this.unwrap().forEachByteDesc(this.idx(var1), var2, var3);
      return var4 >= this.adjustment ? var4 - this.adjustment : -1;
   }

   final int idx(int var1) {
      return var1 + this.adjustment;
   }

   static void checkSliceOutOfBounds(int var0, int var1, ByteBuf var2) {
      if (MathUtil.isOutOfBounds(var0, var1, var2.capacity())) {
         throw new IndexOutOfBoundsException(var2 + ".slice(" + var0 + ", " + var1 + ')');
      }
   }
}
