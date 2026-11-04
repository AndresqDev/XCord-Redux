package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.ResourceLeakTracker;
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

final class AdvancedLeakAwareCompositeByteBuf extends SimpleLeakAwareCompositeByteBuf {
   AdvancedLeakAwareCompositeByteBuf(CompositeByteBuf var1, ResourceLeakTracker<ByteBuf> var2) {
      super(var1, var2);
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.order(var1);
   }

   @Override
   public ByteBuf slice() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.slice();
   }

   @Override
   public ByteBuf retainedSlice() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice();
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.slice(var1, var2);
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice(var1, var2);
   }

   @Override
   public ByteBuf duplicate() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.duplicate();
   }

   @Override
   public ByteBuf retainedDuplicate() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.retainedDuplicate();
   }

   @Override
   public ByteBuf readSlice(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readSlice(var1);
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readRetainedSlice(var1);
   }

   @Override
   public ByteBuf asReadOnly() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.asReadOnly();
   }

   @Override
   public boolean isReadOnly() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.isReadOnly();
   }

   @Override
   public CompositeByteBuf discardReadBytes() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.discardReadBytes();
   }

   @Override
   public CompositeByteBuf discardSomeReadBytes() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.discardSomeReadBytes();
   }

   @Override
   public CompositeByteBuf ensureWritable(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(var1);
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(var1, var2);
   }

   @Override
   public boolean getBoolean(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBoolean(var1);
   }

   @Override
   public byte getByte(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getByte(var1);
   }

   @Override
   public short getUnsignedByte(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedByte(var1);
   }

   @Override
   public short getShort(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getShort(var1);
   }

   @Override
   public int getUnsignedShort(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShort(var1);
   }

   @Override
   public int getMedium(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getMedium(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMedium(var1);
   }

   @Override
   public int getInt(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getInt(var1);
   }

   @Override
   public long getUnsignedInt(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedInt(var1);
   }

   @Override
   public long getLong(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getLong(var1);
   }

   @Override
   public char getChar(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getChar(var1);
   }

   @Override
   public float getFloat(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getFloat(var1);
   }

   @Override
   public double getDouble(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getDouble(var1);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var4);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, byte[] var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var4);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, ByteBuffer var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getCharSequence(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf setBoolean(int var1, boolean var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBoolean(var1, var2);
   }

   @Override
   public CompositeByteBuf setByte(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setByte(var1, var2);
   }

   @Override
   public CompositeByteBuf setShort(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setShort(var1, var2);
   }

   @Override
   public CompositeByteBuf setMedium(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setMedium(var1, var2);
   }

   @Override
   public CompositeByteBuf setInt(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setInt(var1, var2);
   }

   @Override
   public CompositeByteBuf setLong(int var1, long var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setLong(var1, var2);
   }

   @Override
   public CompositeByteBuf setChar(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setChar(var1, var2);
   }

   @Override
   public CompositeByteBuf setFloat(int var1, float var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setFloat(var1, var2);
   }

   @Override
   public CompositeByteBuf setDouble(int var1, double var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setDouble(var1, var2);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var4);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, byte[] var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var4);
   }

   @Override
   public CompositeByteBuf setBytes(int var1, ByteBuffer var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf setZero(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setZero(var1, var2);
   }

   @Override
   public boolean readBoolean() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBoolean();
   }

   @Override
   public byte readByte() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readByte();
   }

   @Override
   public short readUnsignedByte() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedByte();
   }

   @Override
   public short readShort() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readShort();
   }

   @Override
   public int readUnsignedShort() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShort();
   }

   @Override
   public int readMedium() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readMedium();
   }

   @Override
   public int readUnsignedMedium() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMedium();
   }

   @Override
   public int readInt() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readInt();
   }

   @Override
   public long readUnsignedInt() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedInt();
   }

   @Override
   public long readLong() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readLong();
   }

   @Override
   public char readChar() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readChar();
   }

   @Override
   public float readFloat() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readFloat();
   }

   @Override
   public double readDouble() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readDouble();
   }

   @Override
   public ByteBuf readBytes(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf readBytes(byte[] var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public CompositeByteBuf readBytes(byte[] var1, int var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf readBytes(ByteBuffer var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public CompositeByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public CharSequence readCharSequence(int var1, Charset var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readCharSequence(var1, var2);
   }

   @Override
   public CompositeByteBuf skipBytes(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.skipBytes(var1);
   }

   @Override
   public CompositeByteBuf writeBoolean(boolean var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBoolean(var1);
   }

   @Override
   public CompositeByteBuf writeByte(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeByte(var1);
   }

   @Override
   public CompositeByteBuf writeShort(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeShort(var1);
   }

   @Override
   public CompositeByteBuf writeMedium(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeMedium(var1);
   }

   @Override
   public CompositeByteBuf writeInt(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeInt(var1);
   }

   @Override
   public CompositeByteBuf writeLong(long var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeLong(var1);
   }

   @Override
   public CompositeByteBuf writeChar(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeChar(var1);
   }

   @Override
   public CompositeByteBuf writeFloat(float var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeFloat(var1);
   }

   @Override
   public CompositeByteBuf writeDouble(double var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeDouble(var1);
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf writeBytes(byte[] var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public CompositeByteBuf writeBytes(byte[] var1, int var2, int var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf writeBytes(ByteBuffer var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public int writeBytes(InputStream var1, int var2) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public CompositeByteBuf writeZero(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeZero(var1);
   }

   @Override
   public int writeCharSequence(CharSequence var1, Charset var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeCharSequence(var1, var2);
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.indexOf(var1, var2, var3);
   }

   @Override
   public int bytesBefore(byte var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1);
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1, var2);
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1, var2, var3);
   }

   @Override
   public int forEachByte(ByteProcessor var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(var1);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(ByteProcessor var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(var1);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(var1, var2, var3);
   }

   @Override
   public ByteBuf copy() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.copy();
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.copy(var1, var2);
   }

   @Override
   public int nioBufferCount() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer();
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers();
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers(var1, var2);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.internalNioBuffer(var1, var2);
   }

   @Override
   public String toString(Charset var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.toString(var1);
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.toString(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf capacity(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.capacity(var1);
   }

   @Override
   public short getShortLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getShortLE(var1);
   }

   @Override
   public int getUnsignedShortLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShortLE(var1);
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMediumLE(var1);
   }

   @Override
   public int getMediumLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getMediumLE(var1);
   }

   @Override
   public int getIntLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getIntLE(var1);
   }

   @Override
   public long getUnsignedIntLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedIntLE(var1);
   }

   @Override
   public long getLongLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getLongLE(var1);
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setShortLE(var1, var2);
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setMediumLE(var1, var2);
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setIntLE(var1, var2);
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setLongLE(var1, var2);
   }

   @Override
   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setCharSequence(var1, var2, var3);
   }

   @Override
   public short readShortLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readShortLE();
   }

   @Override
   public int readUnsignedShortLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShortLE();
   }

   @Override
   public int readMediumLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readMediumLE();
   }

   @Override
   public int readUnsignedMediumLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMediumLE();
   }

   @Override
   public int readIntLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readIntLE();
   }

   @Override
   public long readUnsignedIntLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedIntLE();
   }

   @Override
   public long readLongLE() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readLongLE();
   }

   @Override
   public ByteBuf writeShortLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeShortLE(var1);
   }

   @Override
   public ByteBuf writeMediumLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeMediumLE(var1);
   }

   @Override
   public ByteBuf writeIntLE(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeIntLE(var1);
   }

   @Override
   public ByteBuf writeLongLE(long var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeLongLE(var1);
   }

   @Override
   public CompositeByteBuf addComponent(ByteBuf var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponent(var1);
   }

   @Override
   public CompositeByteBuf addComponents(ByteBuf... var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1);
   }

   @Override
   public CompositeByteBuf addComponents(Iterable<ByteBuf> var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1);
   }

   @Override
   public CompositeByteBuf addComponent(int var1, ByteBuf var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponent(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponents(int var1, ByteBuf... var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponents(int var1, Iterable<ByteBuf> var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponent(boolean var1, ByteBuf var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponent(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponents(boolean var1, ByteBuf... var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponents(boolean var1, Iterable<ByteBuf> var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponents(var1, var2);
   }

   @Override
   public CompositeByteBuf addComponent(boolean var1, int var2, ByteBuf var3) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addComponent(var1, var2, var3);
   }

   @Override
   public CompositeByteBuf addFlattenedComponents(boolean var1, ByteBuf var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.addFlattenedComponents(var1, var2);
   }

   @Override
   public CompositeByteBuf removeComponent(int var1) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.removeComponent(var1);
   }

   @Override
   public CompositeByteBuf removeComponents(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.removeComponents(var1, var2);
   }

   @Override
   public Iterator<ByteBuf> iterator() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.iterator();
   }

   @Override
   public List<ByteBuf> decompose(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.decompose(var1, var2);
   }

   @Override
   public CompositeByteBuf consolidate() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.consolidate();
   }

   @Override
   public CompositeByteBuf discardReadComponents() {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.discardReadComponents();
   }

   @Override
   public CompositeByteBuf consolidate(int var1, int var2) {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.consolidate(var1, var2);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var5);
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var5);
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var4);
   }

   @Override
   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var4);
   }

   @Override
   public CompositeByteBuf retain() {
      this.leak.record();
      return super.retain();
   }

   @Override
   public CompositeByteBuf retain(int var1) {
      this.leak.record();
      return super.retain(var1);
   }

   @Override
   public boolean release() {
      this.leak.record();
      return super.release();
   }

   @Override
   public boolean release(int var1) {
      this.leak.record();
      return super.release(var1);
   }

   @Override
   public CompositeByteBuf touch() {
      this.leak.record();
      return this;
   }

   @Override
   public CompositeByteBuf touch(Object var1) {
      this.leak.record(var1);
      return this;
   }

   protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      return new AdvancedLeakAwareByteBuf(var1, var2, var3);
   }
}
