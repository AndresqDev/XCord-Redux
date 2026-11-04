package io.netty.buffer;

import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

@Deprecated
public class ReadOnlyByteBuf extends AbstractDerivedByteBuf {
   private final ByteBuf buffer;

   public ReadOnlyByteBuf(ByteBuf var1) {
      super(var1.maxCapacity());
      if (!(var1 instanceof ReadOnlyByteBuf) && !(var1 instanceof DuplicatedByteBuf)) {
         this.buffer = var1;
      } else {
         this.buffer = var1.unwrap();
      }

      this.setIndex(var1.readerIndex(), var1.writerIndex());
   }

   @Override
   public boolean isReadOnly() {
      return true;
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
   public int ensureWritable(int var1, boolean var2) {
      return 1;
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      throw new ReadOnlyBufferException();
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
   public boolean hasArray() {
      return false;
   }

   @Override
   public byte[] array() {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int arrayOffset() {
      throw new ReadOnlyBufferException();
   }

   @Override
   public boolean hasMemoryAddress() {
      return this.unwrap().hasMemoryAddress();
   }

   @Override
   public long memoryAddress() {
      return this.unwrap().memoryAddress();
   }

   @Override
   public ByteBuf discardReadBytes() {
      throw new ReadOnlyBufferException();
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
   public int setBytes(int var1, InputStream var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.unwrap().getBytes(var1, var2, var3, var5);
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.unwrap().getBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.unwrap().getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.unwrap().getBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf duplicate() {
      return new ReadOnlyByteBuf(this);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return this.unwrap().copy(var1, var2);
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return Unpooled.unmodifiableBuffer(this.unwrap().slice(var1, var2));
   }

   @Override
   public byte getByte(int var1) {
      return this.unwrap().getByte(var1);
   }

   @Override
   protected byte _getByte(int var1) {
      return this.unwrap().getByte(var1);
   }

   @Override
   public short getShort(int var1) {
      return this.unwrap().getShort(var1);
   }

   @Override
   protected short _getShort(int var1) {
      return this.unwrap().getShort(var1);
   }

   @Override
   public short getShortLE(int var1) {
      return this.unwrap().getShortLE(var1);
   }

   @Override
   protected short _getShortLE(int var1) {
      return this.unwrap().getShortLE(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(var1);
   }

   @Override
   protected int _getUnsignedMedium(int var1) {
      return this.unwrap().getUnsignedMedium(var1);
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(var1);
   }

   @Override
   protected int _getUnsignedMediumLE(int var1) {
      return this.unwrap().getUnsignedMediumLE(var1);
   }

   @Override
   public int getInt(int var1) {
      return this.unwrap().getInt(var1);
   }

   @Override
   protected int _getInt(int var1) {
      return this.unwrap().getInt(var1);
   }

   @Override
   public int getIntLE(int var1) {
      return this.unwrap().getIntLE(var1);
   }

   @Override
   protected int _getIntLE(int var1) {
      return this.unwrap().getIntLE(var1);
   }

   @Override
   public long getLong(int var1) {
      return this.unwrap().getLong(var1);
   }

   @Override
   protected long _getLong(int var1) {
      return this.unwrap().getLong(var1);
   }

   @Override
   public long getLongLE(int var1) {
      return this.unwrap().getLongLE(var1);
   }

   @Override
   protected long _getLongLE(int var1) {
      return this.unwrap().getLongLE(var1);
   }

   @Override
   public int nioBufferCount() {
      return this.unwrap().nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.unwrap().nioBuffer(var1, var2).asReadOnlyBuffer();
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.unwrap().nioBuffers(var1, var2);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.unwrap().forEachByteDesc(var1, var2, var3);
   }

   @Override
   public int capacity() {
      return this.unwrap().capacity();
   }

   @Override
   public ByteBuf capacity(int var1) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBuf asReadOnly() {
      return this;
   }
}
