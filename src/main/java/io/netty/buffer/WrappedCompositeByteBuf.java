package io.netty.buffer;

import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

class WrappedCompositeByteBuf extends CompositeByteBuf {
   private final CompositeByteBuf wrapped;

   WrappedCompositeByteBuf(CompositeByteBuf var1) {
      super(var1.alloc());
      this.wrapped = var1;
   }

   @Override
   public boolean release() {
      return this.wrapped.release();
   }

   @Override
   public boolean release(int var1) {
      return this.wrapped.release(var1);
   }

   @Override
   public final int maxCapacity() {
      return this.wrapped.maxCapacity();
   }

   @Override
   public final int readerIndex() {
      return this.wrapped.readerIndex();
   }

   @Override
   public final int writerIndex() {
      return this.wrapped.writerIndex();
   }

   @Override
   public final boolean isReadable() {
      return this.wrapped.isReadable();
   }

   @Override
   public final boolean isReadable(int var1) {
      return this.wrapped.isReadable(var1);
   }

   @Override
   public final boolean isWritable() {
      return this.wrapped.isWritable();
   }

   @Override
   public final boolean isWritable(int var1) {
      return this.wrapped.isWritable(var1);
   }

   @Override
   public final int readableBytes() {
      return this.wrapped.readableBytes();
   }

   @Override
   public final int writableBytes() {
      return this.wrapped.writableBytes();
   }

   @Override
   public final int maxWritableBytes() {
      return this.wrapped.maxWritableBytes();
   }

   @Override
   public int maxFastWritableBytes() {
      return this.wrapped.maxFastWritableBytes();
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      return this.wrapped.ensureWritable(var1, var2);
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      return this.wrapped.order(var1);
   }

   @Override
   public boolean getBoolean(int var1) {
      return this.wrapped.getBoolean(var1);
   }

   @Override
   public short getUnsignedByte(int var1) {
      return this.wrapped.getUnsignedByte(var1);
   }

   @Override
   public short getShort(int var1) {
      return this.wrapped.getShort(var1);
   }

   @Override
   public short getShortLE(int var1) {
      return this.wrapped.getShortLE(var1);
   }

   @Override
   public int getUnsignedShort(int var1) {
      return this.wrapped.getUnsignedShort(var1);
   }

   @Override
   public int getUnsignedShortLE(int var1) {
      return this.wrapped.getUnsignedShortLE(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      return this.wrapped.getUnsignedMedium(var1);
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      return this.wrapped.getUnsignedMediumLE(var1);
   }

   @Override
   public int getMedium(int var1) {
      return this.wrapped.getMedium(var1);
   }

   @Override
   public int getMediumLE(int var1) {
      return this.wrapped.getMediumLE(var1);
   }

   @Override
   public int getInt(int var1) {
      return this.wrapped.getInt(var1);
   }

   @Override
   public int getIntLE(int var1) {
      return this.wrapped.getIntLE(var1);
   }

   @Override
   public long getUnsignedInt(int var1) {
      return this.wrapped.getUnsignedInt(var1);
   }

   @Override
   public long getUnsignedIntLE(int var1) {
      return this.wrapped.getUnsignedIntLE(var1);
   }

   @Override
   public long getLong(int var1) {
      return this.wrapped.getLong(var1);
   }

   @Override
   public long getLongLE(int var1) {
      return this.wrapped.getLongLE(var1);
   }

   @Override
   public char getChar(int var1) {
      return this.wrapped.getChar(var1);
   }

   @Override
   public float getFloat(int var1) {
      return this.wrapped.getFloat(var1);
   }

   @Override
   public double getDouble(int var1) {
      return this.wrapped.getDouble(var1);
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      return this.wrapped.setShortLE(var1, var2);
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      return this.wrapped.setMediumLE(var1, var2);
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      return this.wrapped.setIntLE(var1, var2);
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      return this.wrapped.setLongLE(var1, var2);
   }

   @Override
   public byte readByte() {
      return this.wrapped.readByte();
   }

   @Override
   public boolean readBoolean() {
      return this.wrapped.readBoolean();
   }

   @Override
   public short readUnsignedByte() {
      return this.wrapped.readUnsignedByte();
   }

   @Override
   public short readShort() {
      return this.wrapped.readShort();
   }

   @Override
   public short readShortLE() {
      return this.wrapped.readShortLE();
   }

   @Override
   public int readUnsignedShort() {
      return this.wrapped.readUnsignedShort();
   }

   @Override
   public int readUnsignedShortLE() {
      return this.wrapped.readUnsignedShortLE();
   }

   @Override
   public int readMedium() {
      return this.wrapped.readMedium();
   }

   @Override
   public int readMediumLE() {
      return this.wrapped.readMediumLE();
   }

   @Override
   public int readUnsignedMedium() {
      return this.wrapped.readUnsignedMedium();
   }

   @Override
   public int readUnsignedMediumLE() {
      return this.wrapped.readUnsignedMediumLE();
   }

   @Override
   public int readInt() {
      return this.wrapped.readInt();
   }

   @Override
   public int readIntLE() {
      return this.wrapped.readIntLE();
   }

   @Override
   public long readUnsignedInt() {
      return this.wrapped.readUnsignedInt();
   }

   @Override
   public long readUnsignedIntLE() {
      return this.wrapped.readUnsignedIntLE();
   }

   @Override
   public long readLong() {
      return this.wrapped.readLong();
   }

   @Override
   public long readLongLE() {
      return this.wrapped.readLongLE();
   }

   @Override
   public char readChar() {
      return this.wrapped.readChar();
   }

   @Override
   public float readFloat() {
      return this.wrapped.readFloat();
   }

   @Override
   public double readDouble() {
      return this.wrapped.readDouble();
   }

   @Override
   public ByteBuf readBytes(int var1) {
      return this.wrapped.readBytes(var1);
   }

   @Override
   public ByteBuf slice() {
      return this.wrapped.slice();
   }

   @Override
   public ByteBuf retainedSlice() {
      return this.wrapped.retainedSlice();
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return this.wrapped.slice(var1, var2);
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      return this.wrapped.retainedSlice(var1, var2);
   }

   @Override
   public ByteBuffer nioBuffer() {
      return this.wrapped.nioBuffer();
   }

   @Override
   public String toString(Charset var1) {
      return this.wrapped.toString(var1);
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      return this.wrapped.toString(var1, var2, var3);
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      return this.wrapped.indexOf(var1, var2, var3);
   }

   @Override
   public int bytesBefore(byte var1) {
      return this.wrapped.bytesBefore(var1);
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      return this.wrapped.bytesBefore(var1, var2);
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      return this.wrapped.bytesBefore(var1, var2, var3);
   }

   @Override
   public int forEachByte(ByteProcessor var1) {
      return this.wrapped.forEachByte(var1);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      return this.wrapped.forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(ByteProcessor var1) {
      return this.wrapped.forEachByteDesc(var1);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      return this.wrapped.forEachByteDesc(var1, var2, var3);
   }

   @Override
   public final int hashCode() {
      return this.wrapped.hashCode();
   }

   @Override
   public final boolean equals(Object var1) {
      return this.wrapped.equals(var1);
   }

   @Override
   public final int compareTo(ByteBuf var1) {
      return this.wrapped.compareTo(var1);
   }

   @Override
   public final int refCnt() {
      return this.wrapped.refCnt();
   }

   @Override
   final boolean isAccessible() {
      return this.wrapped.isAccessible();
   }

   @Override
   public ByteBuf duplicate() {
      return this.wrapped.duplicate();
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this.wrapped.retainedDuplicate();
   }

   @Override
   public ByteBuf readSlice(int var1) {
      return this.wrapped.readSlice(var1);
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      return this.wrapped.readRetainedSlice(var1);
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.wrapped.readBytes(var1, var2);
   }

   @Override
   public ByteBuf writeShortLE(int var1) {
      return this.wrapped.writeShortLE(var1);
   }

   @Override
   public ByteBuf writeMediumLE(int var1) {
      return this.wrapped.writeMediumLE(var1);
   }

   @Override
   public ByteBuf writeIntLE(int var1) {
      return this.wrapped.writeIntLE(var1);
   }

   @Override
   public ByteBuf writeLongLE(long var1) {
      return this.wrapped.writeLongLE(var1);
   }

   @Override
   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.wrapped.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.wrapped.writeBytes(var1, var2);
   }

   @Override
   public ByteBuf copy() {
      return this.wrapped.copy();
   }

   @Override
   public CompositeByteBuf addComponent(ByteBuf var1) {
      this.wrapped.addComponent(var1);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(ByteBuf... var1) {
      this.wrapped.addComponents(var1);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(Iterable<ByteBuf> var1) {
      this.wrapped.addComponents(var1);
      return this;
   }

   @Override
   public CompositeByteBuf addComponent(int var1, ByteBuf var2) {
      this.wrapped.addComponent(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(int var1, ByteBuf... var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(int var1, Iterable<ByteBuf> var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponent(boolean var1, ByteBuf var2) {
      this.wrapped.addComponent(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(boolean var1, ByteBuf... var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponents(boolean var1, Iterable<ByteBuf> var2) {
      this.wrapped.addComponents(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf addComponent(boolean var1, int var2, ByteBuf var3) {
      this.wrapped.addComponent(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf addFlattenedComponents(boolean var1, ByteBuf var2) {
      this.wrapped.addFlattenedComponents(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf removeComponent(int var1) {
      this.wrapped.removeComponent(var1);
      return this;
   }

   @Override
   public CompositeByteBuf removeComponents(int var1, int var2) {
      this.wrapped.removeComponents(var1, var2);
      return this;
   }

   @Override
   public Iterator<ByteBuf> iterator() {
      return this.wrapped.iterator();
   }

   @Override
   public List<ByteBuf> decompose(int var1, int var2) {
      return this.wrapped.decompose(var1, var2);
   }

   @Override
   public final boolean isDirect() {
      return this.wrapped.isDirect();
   }

   @Override
   public final boolean hasArray() {
      return this.wrapped.hasArray();
   }

   @Override
   public final byte[] array() {
      return this.wrapped.array();
   }

   @Override
   public final int arrayOffset() {
      return this.wrapped.arrayOffset();
   }

   @Override
   public final boolean hasMemoryAddress() {
      return this.wrapped.hasMemoryAddress();
   }

   @Override
   public final long memoryAddress() {
      return this.wrapped.memoryAddress();
   }

   @Override
   public final int capacity() {
      return this.wrapped.capacity();
   }

   @Override
   public CompositeByteBuf capacity(int var1) {
      this.wrapped.capacity(var1);
      return this;
   }

   @Override
   public final ByteBufAllocator alloc() {
      return this.wrapped.alloc();
   }

   @Override
   public final ByteOrder order() {
      return this.wrapped.order();
   }

   @Override
   public final int numComponents() {
      return this.wrapped.numComponents();
   }

   @Override
   public final int maxNumComponents() {
      return this.wrapped.maxNumComponents();
   }

   @Override
   public final int toComponentIndex(int var1) {
      return this.wrapped.toComponentIndex(var1);
   }

   @Override
   public final int toByteIndex(int var1) {
      return this.wrapped.toByteIndex(var1);
   }

   @Override
   public byte getByte(int var1) {
      return this.wrapped.getByte(var1);
   }

   @Override
   protected final byte _getByte(int var1) {
      return this.wrapped._getByte(var1);
   }

   @Override
   protected final short _getShort(int var1) {
      return this.wrapped._getShort(var1);
   }

   @Override
   protected final short _getShortLE(int var1) {
      return this.wrapped._getShortLE(var1);
   }

   @Override
   protected final int _getUnsignedMedium(int var1) {
      return this.wrapped._getUnsignedMedium(var1);
   }

   @Override
   protected final int _getUnsignedMediumLE(int var1) {
      return this.wrapped._getUnsignedMediumLE(var1);
   }

   @Override
   protected final int _getInt(int var1) {
      return this.wrapped._getInt(var1);
   }

   @Override
   protected final int _getIntLE(int var1) {
      return this.wrapped._getIntLE(var1);
   }

   @Override
   protected final long _getLong(int var1) {
      return this.wrapped._getLong(var1);
   }

   @Override
   protected final long _getLongLE(int var1) {
      return this.wrapped._getLongLE(var1);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      this.wrapped.getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuffer var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.wrapped.getBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.wrapped.getBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      this.wrapped.getBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf setByte(int var1, int var2) {
      this.wrapped.setByte(var1, var2);
      return this;
   }

   @Override
   protected final void _setByte(int var1, int var2) {
      this.wrapped._setByte(var1, var2);
   }

   @Override
   public CompositeByteBuf setShort(int var1, int var2) {
      this.wrapped.setShort(var1, var2);
      return this;
   }

   @Override
   protected final void _setShort(int var1, int var2) {
      this.wrapped._setShort(var1, var2);
   }

   @Override
   protected final void _setShortLE(int var1, int var2) {
      this.wrapped._setShortLE(var1, var2);
   }

   @Override
   public CompositeByteBuf setMedium(int var1, int var2) {
      this.wrapped.setMedium(var1, var2);
      return this;
   }

   @Override
   protected final void _setMedium(int var1, int var2) {
      this.wrapped._setMedium(var1, var2);
   }

   @Override
   protected final void _setMediumLE(int var1, int var2) {
      this.wrapped._setMediumLE(var1, var2);
   }

   @Override
   public CompositeByteBuf setInt(int var1, int var2) {
      this.wrapped.setInt(var1, var2);
      return this;
   }

   @Override
   protected final void _setInt(int var1, int var2) {
      this.wrapped._setInt(var1, var2);
   }

   @Override
   protected final void _setIntLE(int var1, int var2) {
      this.wrapped._setIntLE(var1, var2);
   }

   @Override
   public CompositeByteBuf setLong(int var1, long var2) {
      this.wrapped.setLong(var1, var2);
      return this;
   }

   @Override
   protected final void _setLong(int var1, long var2) {
      this.wrapped._setLong(var1, var2);
   }

   @Override
   protected final void _setLongLE(int var1, long var2) {
      this.wrapped._setLongLE(var1, var2);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      this.wrapped.setBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuffer var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      this.wrapped.setBytes(var1, var2, var3, var4);
      return this;
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return this.wrapped.copy(var1, var2);
   }

   @Override
   public final ByteBuf component(int var1) {
      return this.wrapped.component(var1);
   }

   @Override
   public final ByteBuf componentAtOffset(int var1) {
      return this.wrapped.componentAtOffset(var1);
   }

   @Override
   public final ByteBuf internalComponent(int var1) {
      return this.wrapped.internalComponent(var1);
   }

   @Override
   public final ByteBuf internalComponentAtOffset(int var1) {
      return this.wrapped.internalComponentAtOffset(var1);
   }

   @Override
   public int nioBufferCount() {
      return this.wrapped.nioBufferCount();
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.wrapped.internalNioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.wrapped.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.wrapped.nioBuffers(var1, var2);
   }

   @Override
   public CompositeByteBuf consolidate() {
      this.wrapped.consolidate();
      return this;
   }

   @Override
   public CompositeByteBuf consolidate(int var1, int var2) {
      this.wrapped.consolidate(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf discardReadComponents() {
      this.wrapped.discardReadComponents();
      return this;
   }

   @Override
   public CompositeByteBuf discardReadBytes() {
      this.wrapped.discardReadBytes();
      return this;
   }

   @Override
   public final String toString() {
      return this.wrapped.toString();
   }

   @Override
   public final CompositeByteBuf readerIndex(int var1) {
      this.wrapped.readerIndex(var1);
      return this;
   }

   @Override
   public final CompositeByteBuf writerIndex(int var1) {
      this.wrapped.writerIndex(var1);
      return this;
   }

   @Override
   public final CompositeByteBuf setIndex(int var1, int var2) {
      this.wrapped.setIndex(var1, var2);
      return this;
   }

   @Override
   public final CompositeByteBuf clear() {
      this.wrapped.clear();
      return this;
   }

   @Override
   public final CompositeByteBuf markReaderIndex() {
      this.wrapped.markReaderIndex();
      return this;
   }

   @Override
   public final CompositeByteBuf resetReaderIndex() {
      this.wrapped.resetReaderIndex();
      return this;
   }

   @Override
   public final CompositeByteBuf markWriterIndex() {
      this.wrapped.markWriterIndex();
      return this;
   }

   @Override
   public final CompositeByteBuf resetWriterIndex() {
      this.wrapped.resetWriterIndex();
      return this;
   }

   @Override
   public CompositeByteBuf ensureWritable(int var1) {
      this.wrapped.ensureWritable(var1);
      return this;
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      this.wrapped.getBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf getBytes(int var1, byte[] var2) {
      this.wrapped.getBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setBoolean(int var1, boolean var2) {
      this.wrapped.setBoolean(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setChar(int var1, int var2) {
      this.wrapped.setChar(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setFloat(int var1, float var2) {
      this.wrapped.setFloat(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setDouble(int var1, double var2) {
      this.wrapped.setDouble(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      this.wrapped.setBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf setBytes(int var1, byte[] var2) {
      this.wrapped.setBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf setZero(int var1, int var2) {
      this.wrapped.setZero(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1, int var2) {
      this.wrapped.readBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      this.wrapped.readBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(byte[] var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(byte[] var1, int var2, int var3) {
      this.wrapped.readBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuffer var1) {
      this.wrapped.readBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      this.wrapped.readBytes(var1, var2);
      return this;
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.wrapped.getBytes(var1, var2, var3, var5);
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      return this.wrapped.setBytes(var1, var2, var3, var5);
   }

   @Override
   public boolean isReadOnly() {
      return this.wrapped.isReadOnly();
   }

   @Override
   public ByteBuf asReadOnly() {
      return this.wrapped.asReadOnly();
   }

   @Override
   protected SwappedByteBuf newSwappedByteBuf() {
      return this.wrapped.newSwappedByteBuf();
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      return this.wrapped.getCharSequence(var1, var2, var3);
   }

   @Override
   public CharSequence readCharSequence(int var1, Charset var2) {
      return this.wrapped.readCharSequence(var1, var2);
   }

   @Override
   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      return this.wrapped.setCharSequence(var1, var2, var3);
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.wrapped.readBytes(var1, var2, var4);
   }

   @Override
   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      return this.wrapped.writeBytes(var1, var2, var4);
   }

   @Override
   public int writeCharSequence(CharSequence var1, Charset var2) {
      return this.wrapped.writeCharSequence(var1, var2);
   }

   @Override
   public CompositeByteBuf skipBytes(int var1) {
      this.wrapped.skipBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeBoolean(boolean var1) {
      this.wrapped.writeBoolean(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeByte(int var1) {
      this.wrapped.writeByte(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeShort(int var1) {
      this.wrapped.writeShort(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeMedium(int var1) {
      this.wrapped.writeMedium(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeInt(int var1) {
      this.wrapped.writeInt(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeLong(long var1) {
      this.wrapped.writeLong(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeChar(int var1) {
      this.wrapped.writeChar(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeFloat(float var1) {
      this.wrapped.writeFloat(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeDouble(double var1) {
      this.wrapped.writeDouble(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1, int var2) {
      this.wrapped.writeBytes(var1, var2);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      this.wrapped.writeBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(byte[] var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(byte[] var1, int var2, int var3) {
      this.wrapped.writeBytes(var1, var2, var3);
      return this;
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuffer var1) {
      this.wrapped.writeBytes(var1);
      return this;
   }

   @Override
   public CompositeByteBuf writeZero(int var1) {
      this.wrapped.writeZero(var1);
      return this;
   }

   @Override
   public CompositeByteBuf retain(int var1) {
      this.wrapped.retain(var1);
      return this;
   }

   @Override
   public CompositeByteBuf retain() {
      this.wrapped.retain();
      return this;
   }

   @Override
   public CompositeByteBuf touch() {
      this.wrapped.touch();
      return this;
   }

   @Override
   public CompositeByteBuf touch(Object var1) {
      this.wrapped.touch(var1);
      return this;
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.wrapped.nioBuffers();
   }

   @Override
   public CompositeByteBuf discardSomeReadBytes() {
      this.wrapped.discardSomeReadBytes();
      return this;
   }

   @Override
   public final void deallocate() {
      this.wrapped.deallocate();
   }

   @Override
   public final ByteBuf unwrap() {
      return this.wrapped;
   }
}
