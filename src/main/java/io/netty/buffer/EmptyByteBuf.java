package io.netty.buffer;

import io.netty.util.ByteProcessor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public final class EmptyByteBuf extends ByteBuf {
   static final int EMPTY_BYTE_BUF_HASH_CODE = 1;
   private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocateDirect(0);
   private static final long EMPTY_BYTE_BUFFER_ADDRESS;
   private final ByteBufAllocator alloc;
   private final ByteOrder order;
   private final String str;
   private EmptyByteBuf swapped;

   public EmptyByteBuf(ByteBufAllocator var1) {
      this(var1, ByteOrder.BIG_ENDIAN);
   }

   private EmptyByteBuf(ByteBufAllocator var1, ByteOrder var2) {
      this.alloc = ObjectUtil.checkNotNull(var1, "alloc");
      this.order = var2;
      this.str = StringUtil.simpleClassName(this) + (var2 == ByteOrder.BIG_ENDIAN ? "BE" : "LE");
   }

   @Override
   public int capacity() {
      return 0;
   }

   @Override
   public ByteBuf capacity(int var1) {
      throw new ReadOnlyBufferException();
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   @Override
   public ByteOrder order() {
      return this.order;
   }

   @Override
   public ByteBuf unwrap() {
      return null;
   }

   @Override
   public ByteBuf asReadOnly() {
      return Unpooled.unmodifiableBuffer(this);
   }

   @Override
   public boolean isReadOnly() {
      return false;
   }

   @Override
   public boolean isDirect() {
      return true;
   }

   @Override
   public int maxCapacity() {
      return 0;
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      if (ObjectUtil.checkNotNull(var1, "endianness") == this.order()) {
         return this;
      } else {
         EmptyByteBuf var2 = this.swapped;
         if (var2 != null) {
            return var2;
         } else {
            this.swapped = var2 = new EmptyByteBuf(this.alloc(), var1);
            return var2;
         }
      }
   }

   @Override
   public int readerIndex() {
      return 0;
   }

   @Override
   public ByteBuf readerIndex(int var1) {
      return this.checkIndex(var1);
   }

   @Override
   public int writerIndex() {
      return 0;
   }

   @Override
   public ByteBuf writerIndex(int var1) {
      return this.checkIndex(var1);
   }

   @Override
   public ByteBuf setIndex(int var1, int var2) {
      this.checkIndex(var1);
      this.checkIndex(var2);
      return this;
   }

   @Override
   public int readableBytes() {
      return 0;
   }

   @Override
   public int writableBytes() {
      return 0;
   }

   @Override
   public int maxWritableBytes() {
      return 0;
   }

   @Override
   public boolean isReadable() {
      return false;
   }

   @Override
   public boolean isWritable() {
      return false;
   }

   @Override
   public ByteBuf clear() {
      return this;
   }

   @Override
   public ByteBuf markReaderIndex() {
      return this;
   }

   @Override
   public ByteBuf resetReaderIndex() {
      return this;
   }

   @Override
   public ByteBuf markWriterIndex() {
      return this;
   }

   @Override
   public ByteBuf resetWriterIndex() {
      return this;
   }

   @Override
   public ByteBuf discardReadBytes() {
      return this;
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      return this;
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "minWritableBytes");
      if (var1 != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this;
      }
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      ObjectUtil.checkPositiveOrZero(var1, "minWritableBytes");
      return var1 == 0 ? 0 : 1;
   }

   @Override
   public boolean getBoolean(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public byte getByte(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short getUnsignedByte(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short getShort(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short getShortLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getUnsignedShort(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getUnsignedShortLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getMedium(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getMediumLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getUnsignedMedium(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getUnsignedMediumLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getInt(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int getIntLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long getUnsignedInt(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long getUnsignedIntLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long getLong(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long getLongLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public char getChar(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public float getFloat(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public double getDouble(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2) {
      return this.checkIndex(var1, var2.writableBytes());
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return this.checkIndex(var1, var3);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.checkIndex(var1, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2) {
      return this.checkIndex(var1, var2.length);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      return this.checkIndex(var1, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      return this.checkIndex(var1, var2.remaining());
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) {
      return this.checkIndex(var1, var3);
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) {
      this.checkIndex(var1, var3);
      return 0;
   }

   @Override
   public int getBytes(int var1, FileChannel var2, long var3, int var5) {
      this.checkIndex(var1, var5);
      return 0;
   }

   @Override
   public CharSequence getCharSequence(int var1, int var2, Charset var3) {
      this.checkIndex(var1, var2);
      return null;
   }

   @Override
   public ByteBuf setBoolean(int var1, boolean var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setShortLE(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setMediumLE(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setIntLE(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setLongLE(int var1, long var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setChar(int var1, int var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setFloat(int var1, float var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setDouble(int var1, double var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return this.checkIndex(var1, var3);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.checkIndex(var1, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2) {
      return this.checkIndex(var1, var2.length);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      return this.checkIndex(var1, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      return this.checkIndex(var1, var2.remaining());
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) {
      this.checkIndex(var1, var3);
      return 0;
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) {
      this.checkIndex(var1, var3);
      return 0;
   }

   @Override
   public int setBytes(int var1, FileChannel var2, long var3, int var5) {
      this.checkIndex(var1, var5);
      return 0;
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      return this.checkIndex(var1, var2);
   }

   @Override
   public int setCharSequence(int var1, CharSequence var2, Charset var3) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public boolean readBoolean() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public byte readByte() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short readUnsignedByte() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short readShort() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public short readShortLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readUnsignedShort() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readUnsignedShortLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readMedium() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readMediumLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readUnsignedMedium() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readUnsignedMediumLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readInt() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int readIntLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long readUnsignedInt() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long readUnsignedIntLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long readLong() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public long readLongLE() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public char readChar() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public float readFloat() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public double readDouble() {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf readBytes(int var1) {
      return this.checkLength(var1);
   }

   @Override
   public ByteBuf readSlice(int var1) {
      return this.checkLength(var1);
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      return this.checkLength(var1);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1) {
      return this.checkLength(var1.writableBytes());
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2) {
      return this.checkLength(var2);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return this.checkLength(var3);
   }

   @Override
   public ByteBuf readBytes(byte[] var1) {
      return this.checkLength(var1.length);
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      return this.checkLength(var3);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      return this.checkLength(var1.remaining());
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) {
      return this.checkLength(var2);
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) {
      this.checkLength(var2);
      return 0;
   }

   @Override
   public int readBytes(FileChannel var1, long var2, int var4) {
      this.checkLength(var4);
      return 0;
   }

   @Override
   public CharSequence readCharSequence(int var1, Charset var2) {
      this.checkLength(var1);
      return "";
   }

   @Override
   public ByteBuf skipBytes(int var1) {
      return this.checkLength(var1);
   }

   @Override
   public ByteBuf writeBoolean(boolean var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeByte(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeShort(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeShortLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeMedium(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeMediumLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeInt(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeIntLE(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeLong(long var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeLongLE(long var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeChar(int var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeFloat(float var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeDouble(double var1) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1) {
      return this.checkLength(var1.readableBytes());
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      return this.checkLength(var2);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return this.checkLength(var3);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1) {
      return this.checkLength(var1.length);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      return this.checkLength(var3);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer var1) {
      return this.checkLength(var1.remaining());
   }

   @Override
   public int writeBytes(InputStream var1, int var2) {
      this.checkLength(var2);
      return 0;
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) {
      this.checkLength(var2);
      return 0;
   }

   @Override
   public int writeBytes(FileChannel var1, long var2, int var4) {
      this.checkLength(var4);
      return 0;
   }

   @Override
   public ByteBuf writeZero(int var1) {
      return this.checkLength(var1);
   }

   @Override
   public int writeCharSequence(CharSequence var1, Charset var2) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      this.checkIndex(var1);
      this.checkIndex(var2);
      return -1;
   }

   @Override
   public int bytesBefore(byte var1) {
      return -1;
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      this.checkLength(var1);
      return -1;
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      this.checkIndex(var1, var2);
      return -1;
   }

   @Override
   public int forEachByte(ByteProcessor var1) {
      return -1;
   }

   @Override
   public int forEachByte(int var1, int var2, ByteProcessor var3) {
      this.checkIndex(var1, var2);
      return -1;
   }

   @Override
   public int forEachByteDesc(ByteProcessor var1) {
      return -1;
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteProcessor var3) {
      this.checkIndex(var1, var2);
      return -1;
   }

   @Override
   public ByteBuf copy() {
      return this;
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return this.checkIndex(var1, var2);
   }

   @Override
   public ByteBuf slice() {
      return this;
   }

   @Override
   public ByteBuf retainedSlice() {
      return this;
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return this.checkIndex(var1, var2);
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      return this.checkIndex(var1, var2);
   }

   @Override
   public ByteBuf duplicate() {
      return this;
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this;
   }

   @Override
   public int nioBufferCount() {
      return 1;
   }

   @Override
   public ByteBuffer nioBuffer() {
      return EMPTY_BYTE_BUFFER;
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.nioBuffer();
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return new ByteBuffer[]{EMPTY_BYTE_BUFFER};
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      this.checkIndex(var1, var2);
      return this.nioBuffers();
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return EMPTY_BYTE_BUFFER;
   }

   @Override
   public boolean hasArray() {
      return true;
   }

   @Override
   public byte[] array() {
      return EmptyArrays.EMPTY_BYTES;
   }

   @Override
   public int arrayOffset() {
      return 0;
   }

   @Override
   public boolean hasMemoryAddress() {
      return EMPTY_BYTE_BUFFER_ADDRESS != 0L;
   }

   @Override
   public long memoryAddress() {
      if (this.hasMemoryAddress()) {
         return EMPTY_BYTE_BUFFER_ADDRESS;
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public boolean isContiguous() {
      return true;
   }

   @Override
   public String toString(Charset var1) {
      return "";
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      this.checkIndex(var1, var2);
      return this.toString(var3);
   }

   @Override
   public int hashCode() {
      return 1;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof ByteBuf && !((ByteBuf)var1).isReadable();
   }

   @Override
   public int compareTo(ByteBuf var1) {
      return var1.isReadable() ? -1 : 0;
   }

   @Override
   public String toString() {
      return this.str;
   }

   @Override
   public boolean isReadable(int var1) {
      return false;
   }

   @Override
   public boolean isWritable(int var1) {
      return false;
   }

   @Override
   public int refCnt() {
      return 1;
   }

   @Override
   public ByteBuf retain() {
      return this;
   }

   @Override
   public ByteBuf retain(int var1) {
      return this;
   }

   @Override
   public ByteBuf touch() {
      return this;
   }

   @Override
   public ByteBuf touch(Object var1) {
      return this;
   }

   @Override
   public boolean release() {
      return false;
   }

   @Override
   public boolean release(int var1) {
      return false;
   }

   private ByteBuf checkIndex(int var1) {
      if (var1 != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this;
      }
   }

   private ByteBuf checkIndex(int var1, int var2) {
      ObjectUtil.checkPositiveOrZero(var2, "length");
      if (var1 == 0 && var2 == 0) {
         return this;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private ByteBuf checkLength(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "length");
      if (var1 != 0) {
         throw new IndexOutOfBoundsException();
      } else {
         return this;
      }
   }

   static {
      long var0 = 0L;

      try {
         if (PlatformDependent.hasUnsafe()) {
            var0 = PlatformDependent.directBufferAddress(EMPTY_BYTE_BUFFER);
         }
      } catch (Throwable var3) {
      }

      EMPTY_BYTE_BUFFER_ADDRESS = var0;
   }
}
