package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuf extends SimpleLeakAwareByteBuf {
   private static final String PROP_ACQUIRE_AND_RELEASE_ONLY = "io.netty.leakDetection.acquireAndReleaseOnly";
   private static final boolean ACQUIRE_AND_RELEASE_ONLY = SystemPropertyUtil.getBoolean("io.netty.leakDetection.acquireAndReleaseOnly", false);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AdvancedLeakAwareByteBuf.class);

   AdvancedLeakAwareByteBuf(ByteBuf var1, ResourceLeakTracker<ByteBuf> var2) {
      super(var1, var2);
   }

   AdvancedLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      super(var1, var2, var3);
   }

   static void recordLeakNonRefCountingOperation(ResourceLeakTracker<ByteBuf> var0) {
      if (!ACQUIRE_AND_RELEASE_ONLY) {
         var0.record();
      }
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.order(var1);
   }

   @Override
   public ByteBuf slice() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.slice();
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.slice(var1, var2);
   }

   @Override
   public ByteBuf retainedSlice() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice();
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedSlice(var1, var2);
   }

   @Override
   public ByteBuf retainedDuplicate() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.retainedDuplicate();
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readRetainedSlice(var1);
   }

   @Override
   public ByteBuf duplicate() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.duplicate();
   }

   @Override
   public ByteBuf readSlice(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readSlice(var1);
   }

   @Override
   public ByteBuf discardReadBytes() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.discardReadBytes();
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.discardSomeReadBytes();
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(var1);
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.ensureWritable(var1, var2);
   }

   @Override
   public boolean getBoolean(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBoolean(var1);
   }

   @Override
   public byte getByte(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getByte(var1);
   }

   @Override
   public short getUnsignedByte(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedByte(var1);
   }

   @Override
   public short getShort(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getShort(var1);
   }

   @Override
   public int getUnsignedShort(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShort(var1);
   }

   @Override
   public int getMedium(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getMedium(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMedium(var1);
   }

   @Override
   public int getInt(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getInt(var1);
   }

   @Override
   public long getUnsignedInt(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedInt(var1);
   }

   @Override
   public long getLong(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getLong(var1);
   }

   @Override
   public char getChar(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getChar(var1);
   }

   @Override
   public float getFloat(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getFloat(var1);
   }

   @Override
   public double getDouble(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getDouble(var1);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3);
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getCharSequence(var1, var2, var3);
   }

   @Override
   public ByteBuf setBoolean(int var1, boolean var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBoolean(var1, var2);
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setByte(var1, var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setShort(var1, var2);
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setMedium(var1, var2);
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setInt(var1, var2);
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setLong(var1, var2);
   }

   @Override
   public ByteBuf setChar(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setChar(var1, var2);
   }

   @Override
   public ByteBuf setFloat(int var1, float var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setFloat(var1, var2);
   }

   @Override
   public ByteBuf setDouble(int var1, double var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setDouble(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2);
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setZero(var1, var2);
   }

   @Override
   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setCharSequence(var1, var2, var3);
   }

   @Override
   public boolean readBoolean() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBoolean();
   }

   @Override
   public byte readByte() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readByte();
   }

   @Override
   public short readUnsignedByte() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedByte();
   }

   @Override
   public short readShort() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readShort();
   }

   @Override
   public int readUnsignedShort() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShort();
   }

   @Override
   public int readMedium() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readMedium();
   }

   @Override
   public int readUnsignedMedium() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMedium();
   }

   @Override
   public int readInt() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readInt();
   }

   @Override
   public long readUnsignedInt() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedInt();
   }

   @Override
   public long readLong() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readLong();
   }

   @Override
   public char readChar() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readChar();
   }

   @Override
   public float readFloat() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readFloat();
   }

   @Override
   public double readDouble() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readDouble();
   }

   @Override
   public ByteBuf readBytes(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf readBytes(byte[] var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2);
   }

   @Override
   public CharSequence readCharSequence(int var1, Charset var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readCharSequence(var1, var2);
   }

   @Override
   public ByteBuf skipBytes(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.skipBytes(var1);
   }

   @Override
   public ByteBuf writeBoolean(boolean var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBoolean(var1);
   }

   @Override
   public ByteBuf writeByte(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeByte(var1);
   }

   @Override
   public ByteBuf writeShort(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeShort(var1);
   }

   @Override
   public ByteBuf writeMedium(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeMedium(var1);
   }

   @Override
   public ByteBuf writeInt(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeInt(var1);
   }

   @Override
   public ByteBuf writeLong(long var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeLong(var1);
   }

   @Override
   public ByteBuf writeChar(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeChar(var1);
   }

   @Override
   public ByteBuf writeFloat(float var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeFloat(var1);
   }

   @Override
   public ByteBuf writeDouble(double var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeDouble(var1);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1);
   }

   @Override
   public int writeBytes(InputStream var1, int var2) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2);
   }

   @Override
   public ByteBuf writeZero(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeZero(var1);
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.indexOf(var1, var2, var3);
   }

   @Override
   public int bytesBefore(byte var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1);
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1, var2);
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.bytesBefore(var1, var2, var3);
   }

   @Override
   public int forEachByte(ByteProcessor var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(var1);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(ByteProcessor var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(var1);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.forEachByteDesc(var1, var2, var3);
   }

   @Override
   public ByteBuf copy() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.copy();
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.copy(var1, var2);
   }

   @Override
   public int nioBufferCount() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer();
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers();
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.nioBuffers(var1, var2);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.internalNioBuffer(var1, var2);
   }

   @Override
   public String toString(Charset var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.toString(var1);
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.toString(var1, var2, var3);
   }

   @Override
   public ByteBuf capacity(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.capacity(var1);
   }

   @Override
   public short getShortLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getShortLE(var1);
   }

   @Override
   public int getUnsignedShortLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedShortLE(var1);
   }

   @Override
   public int getMediumLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getMediumLE(var1);
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedMediumLE(var1);
   }

   @Override
   public int getIntLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getIntLE(var1);
   }

   @Override
   public long getUnsignedIntLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getUnsignedIntLE(var1);
   }

   @Override
   public long getLongLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getLongLE(var1);
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setShortLE(var1, var2);
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setIntLE(var1, var2);
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setMediumLE(var1, var2);
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setLongLE(var1, var2);
   }

   @Override
   public short readShortLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readShortLE();
   }

   @Override
   public int readUnsignedShortLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedShortLE();
   }

   @Override
   public int readMediumLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readMediumLE();
   }

   @Override
   public int readUnsignedMediumLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedMediumLE();
   }

   @Override
   public int readIntLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readIntLE();
   }

   @Override
   public long readUnsignedIntLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readUnsignedIntLE();
   }

   @Override
   public long readLongLE() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readLongLE();
   }

   @Override
   public ByteBuf writeShortLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeShortLE(var1);
   }

   @Override
   public ByteBuf writeMediumLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeMediumLE(var1);
   }

   @Override
   public ByteBuf writeIntLE(int var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeIntLE(var1);
   }

   @Override
   public ByteBuf writeLongLE(long var1) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeLongLE(var1);
   }

   @Override
   public int writeCharSequence(CharSequence var1, Charset var2) {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeCharSequence(var1, var2);
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.getBytes(var1, var2, var3, var5);
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.setBytes(var1, var2, var3, var5);
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.readBytes(var1, var2, var4);
   }

   @Override
   public int writeBytes(FileChannel var1, long var2, int var4) throws IOException {
      recordLeakNonRefCountingOperation(this.leak);
      return super.writeBytes(var1, var2, var4);
   }

   @Override
   public ByteBuf asReadOnly() {
      recordLeakNonRefCountingOperation(this.leak);
      return super.asReadOnly();
   }

   @Override
   public ByteBuf retain() {
      this.leak.record();
      return super.retain();
   }

   @Override
   public ByteBuf retain(int var1) {
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
   public ByteBuf touch() {
      this.leak.record();
      return this;
   }

   @Override
   public ByteBuf touch(Object var1) {
      this.leak.record(var1);
      return this;
   }

   protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf var1, ByteBuf var2, ResourceLeakTracker<ByteBuf> var3) {
      return new AdvancedLeakAwareByteBuf(var1, var2, var3);
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.leakDetection.acquireAndReleaseOnly", ACQUIRE_AND_RELEASE_ONLY);
      }

      ResourceLeakDetector.addExclusions(AdvancedLeakAwareByteBuf.class, "touch", "recordLeakNonRefCountingOperation");
   }
}
