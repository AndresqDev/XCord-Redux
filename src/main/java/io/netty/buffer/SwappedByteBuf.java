package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

@Deprecated
public class SwappedByteBuf extends ByteBuf {
   private final ByteBuf buf;
   private final ByteOrder order;

   public SwappedByteBuf(ByteBuf var1) {
      this.buf = ObjectUtil.checkNotNull(var1, "buf");
      if (var1.order() == ByteOrder.BIG_ENDIAN) {
         this.order = ByteOrder.LITTLE_ENDIAN;
      } else {
         this.order = ByteOrder.BIG_ENDIAN;
      }
   }

   @Override
   public ByteOrder order() {
      return this.order;
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      return (ByteBuf)(ObjectUtil.checkNotNull(var1, "endianness") == this.order ? this : this.buf);
   }

   @Override
   public ByteBuf unwrap() {
      return this.buf;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.buf.alloc();
   }

   @Override
   public int capacity() {
      return this.buf.capacity();
   }

   @Override
   public ByteBuf capacity(int var1) {
      this.buf.capacity(var1);
      return this;
   }

   @Override
   public int maxCapacity() {
      return this.buf.maxCapacity();
   }

   @Override
   public boolean isReadOnly() {
      return this.buf.isReadOnly();
   }

   @Override
   public ByteBuf asReadOnly() {
      return Unpooled.unmodifiableBuffer(this);
   }

   @Override
   public boolean isDirect() {
      return this.buf.isDirect();
   }

   @Override
   public int readerIndex() {
      return this.buf.readerIndex();
   }

   @Override
   public ByteBuf readerIndex(int var1) {
      this.buf.readerIndex(var1);
      return this;
   }

   @Override
   public int writerIndex() {
      return this.buf.writerIndex();
   }

   @Override
   public ByteBuf writerIndex(int var1) {
      this.buf.writerIndex(var1);
      return this;
   }

   @Override
   public ByteBuf setIndex(int var1, int var2) {
      this.buf.setIndex(var1, var2);
      return this;
   }

   @Override
   public int readableBytes() {
      return this.buf.readableBytes();
   }

   @Override
   public int writableBytes() {
      return this.buf.writableBytes();
   }

   @Override
   public int maxWritableBytes() {
      return this.buf.maxWritableBytes();
   }

   @Override
   public int maxFastWritableBytes() {
      return this.buf.maxFastWritableBytes();
   }

   @Override
   public boolean isReadable() {
      return this.buf.isReadable();
   }

   @Override
   public boolean isReadable(int var1) {
      return this.buf.isReadable(var1);
   }

   @Override
   public boolean isWritable() {
      return this.buf.isWritable();
   }

   @Override
   public boolean isWritable(int var1) {
      return this.buf.isWritable(var1);
   }

   @Override
   public ByteBuf clear() {
      this.buf.clear();
      return this;
   }

   @Override
   public ByteBuf markReaderIndex() {
      this.buf.markReaderIndex();
      return this;
   }

   @Override
   public ByteBuf resetReaderIndex() {
      this.buf.resetReaderIndex();
      return this;
   }

   @Override
   public ByteBuf markWriterIndex() {
      this.buf.markWriterIndex();
      return this;
   }

   @Override
   public ByteBuf resetWriterIndex() {
      this.buf.resetWriterIndex();
      return this;
   }

   @Override
   public ByteBuf discardReadBytes() {
      this.buf.discardReadBytes();
      return this;
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      this.buf.discardSomeReadBytes();
      return this;
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      this.buf.ensureWritable(var1);
      return this;
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      return this.buf.ensureWritable(var1, var2);
   }

   @Override
   public boolean getBoolean(int var1) {
      return this.buf.getBoolean(var1);
   }

   @Override
   public byte getByte(int var1) {
      return this.buf.getByte(var1);
   }

   @Override
   public short getUnsignedByte(int var1) {
      return this.buf.getUnsignedByte(var1);
   }

   @Override
   public short getShort(int var1) {
      return ByteBufUtil.swapShort(this.buf.getShort(var1));
   }

   @Override
   public short getShortLE(int var1) {
      return this.buf.getShortLE(var1);
   }

   @Override
   public int getUnsignedShort(int var1) {
      return this.getShort(var1) & 65535;
   }

   @Override
   public int getUnsignedShortLE(int var1) {
      return this.getShortLE(var1) & 65535;
   }

   @Override
   public int getMedium(int var1) {
      return ByteBufUtil.swapMedium(this.buf.getMedium(var1));
   }

   @Override
   public int getMediumLE(int var1) {
      return this.buf.getMediumLE(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      return this.getMedium(var1) & 16777215;
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      return this.getMediumLE(var1) & 16777215;
   }

   @Override
   public int getInt(int var1) {
      return ByteBufUtil.swapInt(this.buf.getInt(var1));
   }

   @Override
   public int getIntLE(int var1) {
      return this.buf.getIntLE(var1);
   }

   @Override
   public long getUnsignedInt(int var1) {
      return (long)this.getInt(var1) & 4294967295L;
   }

   @Override
   public long getUnsignedIntLE(int var1) {
      return (long)this.getIntLE(var1) & 4294967295L;
   }

   @Override
   public long getLong(int var1) {
      return ByteBufUtil.swapLong(this.buf.getLong(var1));
   }

   @Override
   public long getLongLE(int var1) {
      return this.buf.getLongLE(var1);
   }

   @Override
   public char getChar(int var1) {
      return (char)this.getShort(var1);
   }

   @Override
   public float getFloat(int var1) {
      return Float.intBitsToFloat(this.getInt(var1));
   }

   @Override
   public double getDouble(int var1) {
      return Double.longBitsToDouble(this.getLong(var1));
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2) {
      this.buf.getBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.buf.getBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.buf.getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2) {
      this.buf.getBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.buf.getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      this.buf.getBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.buf.getBytes(var1, var2, var3);
      return this;
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.buf.getBytes(var1, var2, var3);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.buf.getBytes(var1, var2, var3, var5);
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.buf.getCharSequence(var1, var2, var3);
   }

   @Override
   public ByteBuf setBoolean(int var1, boolean var2) {
      this.buf.setBoolean(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      this.buf.setByte(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      this.buf.setShort(var1, ByteBufUtil.swapShort((short)var2));
      return this;
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      this.buf.setShortLE(var1, (short)var2);
      return this;
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      this.buf.setMedium(var1, ByteBufUtil.swapMedium(var2));
      return this;
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      this.buf.setMediumLE(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      this.buf.setInt(var1, ByteBufUtil.swapInt(var2));
      return this;
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      this.buf.setIntLE(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      this.buf.setLong(var1, ByteBufUtil.swapLong(var2));
      return this;
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      this.buf.setLongLE(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setChar(int var1, int var2) {
      this.setShort(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setFloat(int var1, float var2) {
      this.setInt(var1, Float.floatToRawIntBits(var2));
      return this;
   }

   @Override
   public ByteBuf setDouble(int var1, double var2) {
      this.setLong(var1, Double.doubleToRawLongBits(var2));
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2) {
      this.buf.setBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      this.buf.setBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.buf.setBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2) {
      this.buf.setBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.buf.setBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      this.buf.setBytes(var1, var2);
      return this;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.buf.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.buf.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.buf.setBytes(var1, var2, var3, var5);
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      this.buf.setZero(var1, var2);
      return this;
   }

   @Override
   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.buf.setCharSequence(var1, var2, var3);
   }

   @Override
   public boolean readBoolean() {
      return this.buf.readBoolean();
   }

   @Override
   public byte readByte() {
      return this.buf.readByte();
   }

   @Override
   public short readUnsignedByte() {
      return this.buf.readUnsignedByte();
   }

   @Override
   public short readShort() {
      return ByteBufUtil.swapShort(this.buf.readShort());
   }

   @Override
   public short readShortLE() {
      return this.buf.readShortLE();
   }

   @Override
   public int readUnsignedShort() {
      return this.readShort() & 65535;
   }

   @Override
   public int readUnsignedShortLE() {
      return this.readShortLE() & 65535;
   }

   @Override
   public int readMedium() {
      return ByteBufUtil.swapMedium(this.buf.readMedium());
   }

   @Override
   public int readMediumLE() {
      return this.buf.readMediumLE();
   }

   @Override
   public int readUnsignedMedium() {
      return this.readMedium() & 16777215;
   }

   @Override
   public int readUnsignedMediumLE() {
      return this.readMediumLE() & 16777215;
   }

   @Override
   public int readInt() {
      return ByteBufUtil.swapInt(this.buf.readInt());
   }

   @Override
   public int readIntLE() {
      return this.buf.readIntLE();
   }

   @Override
   public long readUnsignedInt() {
      return (long)this.readInt() & 4294967295L;
   }

   @Override
   public long readUnsignedIntLE() {
      return (long)this.readIntLE() & 4294967295L;
   }

   @Override
   public long readLong() {
      return ByteBufUtil.swapLong(this.buf.readLong());
   }

   @Override
   public long readLongLE() {
      return this.buf.readLongLE();
   }

   @Override
   public char readChar() {
      return (char)this.readShort();
   }

   @Override
   public float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   @Override
   public double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   @Override
   public ByteBuf readBytes(int var1) {
      return this.buf.readBytes(var1).order(this.order());
   }

   @Override
   public ByteBuf readSlice(int var1) {
      return this.buf.readSlice(var1).order(this.order);
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      return this.buf.readRetainedSlice(var1).order(this.order);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1) {
      this.buf.readBytes(var1);
      return this;
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2) {
      this.buf.readBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.buf.readBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf readBytes(byte[] var1) {
      this.buf.readBytes(var1);
      return this;
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.buf.readBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      this.buf.readBytes(var1);
      return this;
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.buf.readBytes(var1, var2);
      return this;
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.buf.readBytes(var1, var2);
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.buf.readBytes(var1, var2, var4);
   }

   @Override
   public CharSequence readCharSequence(int var1, Charset var2) {
      return this.buf.readCharSequence(var1, var2);
   }

   @Override
   public ByteBuf skipBytes(int var1) {
      this.buf.skipBytes(var1);
      return this;
   }

   @Override
   public ByteBuf writeBoolean(boolean var1) {
      this.buf.writeBoolean(var1);
      return this;
   }

   @Override
   public ByteBuf writeByte(int var1) {
      this.buf.writeByte(var1);
      return this;
   }

   @Override
   public ByteBuf writeShort(int var1) {
      this.buf.writeShort(ByteBufUtil.swapShort((short)var1));
      return this;
   }

   @Override
   public ByteBuf writeShortLE(int var1) {
      this.buf.writeShortLE((short)var1);
      return this;
   }

   @Override
   public ByteBuf writeMedium(int var1) {
      this.buf.writeMedium(ByteBufUtil.swapMedium(var1));
      return this;
   }

   @Override
   public ByteBuf writeMediumLE(int var1) {
      this.buf.writeMediumLE(var1);
      return this;
   }

   @Override
   public ByteBuf writeInt(int var1) {
      this.buf.writeInt(ByteBufUtil.swapInt(var1));
      return this;
   }

   @Override
   public ByteBuf writeIntLE(int var1) {
      this.buf.writeIntLE(var1);
      return this;
   }

   @Override
   public ByteBuf writeLong(long var1) {
      this.buf.writeLong(ByteBufUtil.swapLong(var1));
      return this;
   }

   @Override
   public ByteBuf writeLongLE(long var1) {
      this.buf.writeLongLE(var1);
      return this;
   }

   @Override
   public ByteBuf writeChar(int var1) {
      this.writeShort(var1);
      return this;
   }

   @Override
   public ByteBuf writeFloat(float var1) {
      this.writeInt(Float.floatToRawIntBits(var1));
      return this;
   }

   @Override
   public ByteBuf writeDouble(double var1) {
      this.writeLong(Double.doubleToRawLongBits(var1));
      return this;
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1) {
      this.buf.writeBytes(var1);
      return this;
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      this.buf.writeBytes(var1, var2);
      return this;
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      this.buf.writeBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf writeBytes(byte[] var1) {
      this.buf.writeBytes(var1);
      return this;
   }

   @Override
   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      this.buf.writeBytes(var1, var2, var3);
      return this;
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer var1) {
      this.buf.writeBytes(var1);
      return this;
   }

   @Override
   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.buf.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.buf.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.buf.writeBytes(var1, var2, var4);
   }

   @Override
   public ByteBuf writeZero(int var1) {
      this.buf.writeZero(var1);
      return this;
   }

   @Override
   public int writeCharSequence(CharSequence var1, Charset var2) {
      return this.buf.writeCharSequence(var1, var2);
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      return this.buf.indexOf(var1, var2, var3);
   }

   @Override
   public int bytesBefore(byte var1) {
      return this.buf.bytesBefore(var1);
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      return this.buf.bytesBefore(var1, var2);
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      return this.buf.bytesBefore(var1, var2, var3);
   }

   @Override
   public int forEachByte(ByteProcessor var1) {
      return this.buf.forEachByte(var1);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.buf.forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(ByteProcessor var1) {
      return this.buf.forEachByteDesc(var1);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.buf.forEachByteDesc(var1, var2, var3);
   }

   @Override
   public ByteBuf copy() {
      return this.buf.copy().order(this.order);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return this.buf.copy(var1, var2).order(this.order);
   }

   @Override
   public ByteBuf slice() {
      return this.buf.slice().order(this.order);
   }

   @Override
   public ByteBuf retainedSlice() {
      return this.buf.retainedSlice().order(this.order);
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return this.buf.slice(var1, var2).order(this.order);
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      return this.buf.retainedSlice(var1, var2).order(this.order);
   }

   @Override
   public ByteBuf duplicate() {
      return this.buf.duplicate().order(this.order);
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this.buf.retainedDuplicate().order(this.order);
   }

   @Override
   public int nioBufferCount() {
      return this.buf.nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer() {
      return this.buf.nioBuffer().order(this.order);
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.buf.nioBuffer(var1, var2).order(this.order);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      ByteBuffer[] var1 = this.buf.nioBuffers();

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var1[var2].order(this.order);
      }

      return var1;
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      ByteBuffer[] var3 = this.buf.nioBuffers(var1, var2);

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = var3[var4].order(this.order);
      }

      return var3;
   }

   @Override
   public boolean hasArray() {
      return this.buf.hasArray();
   }

   @Override
   public byte[] array() {
      return this.buf.array();
   }

   @Override
   public int arrayOffset() {
      return this.buf.arrayOffset();
   }

   @Override
   public boolean hasMemoryAddress() {
      return this.buf.hasMemoryAddress();
   }

   @Override
   public boolean isContiguous() {
      return this.buf.isContiguous();
   }

   @Override
   public long memoryAddress() {
      return this.buf.memoryAddress();
   }

   @Override
   public String toString(Charset var1) {
      return this.buf.toString(var1);
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      return this.buf.toString(var1, var2, var3);
   }

   @Override
   public int refCnt() {
      return this.buf.refCnt();
   }

   @Override
   final boolean isAccessible() {
      return this.buf.isAccessible();
   }

   @Override
   public ByteBuf retain() {
      this.buf.retain();
      return this;
   }

   @Override
   public ByteBuf retain(int var1) {
      this.buf.retain(var1);
      return this;
   }

   @Override
   public ByteBuf touch() {
      this.buf.touch();
      return this;
   }

   @Override
   public ByteBuf touch(Object var1) {
      this.buf.touch(var1);
      return this;
   }

   @Override
   public boolean release() {
      return this.buf.release();
   }

   @Override
   public boolean release(int var1) {
      return this.buf.release(var1);
   }

   @Override
   public int hashCode() {
      return this.buf.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof ByteBuf ? ByteBufUtil.equals(this, (ByteBuf)var1) : false;
   }

   @Override
   public int compareTo(ByteBuf var1) {
      return ByteBufUtil.compare(this, var1);
   }

   @Override
   public String toString() {
      return "Swapped(" + this.buf + ')';
   }
}
