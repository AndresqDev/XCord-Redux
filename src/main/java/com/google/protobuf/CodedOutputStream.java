package com.google.protobuf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CodedOutputStream extends ByteOutput {
   private static final Logger logger = Logger.getLogger(CodedOutputStream.class.getName());
   private static final boolean HAS_UNSAFE_ARRAY_OPERATIONS = UnsafeUtil.hasUnsafeArrayOperations();
   CodedOutputStreamWriter wrapper;
   @Deprecated
   public static final int LITTLE_ENDIAN_32_SIZE = 4;
   public static final int DEFAULT_BUFFER_SIZE = 4096;
   private boolean serializationDeterministic;

   static int computePreferredBufferSize(int var0) {
      return var0 > 4096 ? 4096 : var0;
   }

   public static CodedOutputStream newInstance(OutputStream var0) {
      return newInstance(var0, 4096);
   }

   public static CodedOutputStream newInstance(OutputStream var0, int var1) {
      return new CodedOutputStream.OutputStreamEncoder(var0, var1);
   }

   public static CodedOutputStream newInstance(byte[] var0) {
      return newInstance(var0, 0, var0.length);
   }

   public static CodedOutputStream newInstance(byte[] var0, int var1, int var2) {
      return new CodedOutputStream.ArrayEncoder(var0, var1, var2);
   }

   public static CodedOutputStream newInstance(ByteBuffer var0) {
      if (var0.hasArray()) {
         return new CodedOutputStream.HeapNioEncoder(var0);
      } else if (var0.isDirect() && !var0.isReadOnly()) {
         return CodedOutputStream.UnsafeDirectNioEncoder.isSupported() ? newUnsafeInstance(var0) : newSafeInstance(var0);
      } else {
         throw new IllegalArgumentException("ByteBuffer is read-only");
      }
   }

   static CodedOutputStream newUnsafeInstance(ByteBuffer var0) {
      return new CodedOutputStream.UnsafeDirectNioEncoder(var0);
   }

   static CodedOutputStream newSafeInstance(ByteBuffer var0) {
      return new CodedOutputStream.SafeDirectNioEncoder(var0);
   }

   public void useDeterministicSerialization() {
      this.serializationDeterministic = true;
   }

   boolean isSerializationDeterministic() {
      return this.serializationDeterministic;
   }

   @Deprecated
   public static CodedOutputStream newInstance(ByteBuffer var0, int var1) {
      return newInstance(var0);
   }

   static CodedOutputStream newInstance(ByteOutput var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("bufferSize must be positive");
      } else {
         return new CodedOutputStream.ByteOutputEncoder(var0, var1);
      }
   }

   private CodedOutputStream() {
   }

   public abstract void writeTag(int var1, int var2) throws IOException;

   public abstract void writeInt32(int var1, int var2) throws IOException;

   public abstract void writeUInt32(int var1, int var2) throws IOException;

   public final void writeSInt32(int var1, int var2) throws IOException {
      this.writeUInt32(var1, encodeZigZag32(var2));
   }

   public abstract void writeFixed32(int var1, int var2) throws IOException;

   public final void writeSFixed32(int var1, int var2) throws IOException {
      this.writeFixed32(var1, var2);
   }

   public final void writeInt64(int var1, long var2) throws IOException {
      this.writeUInt64(var1, var2);
   }

   public abstract void writeUInt64(int var1, long var2) throws IOException;

   public final void writeSInt64(int var1, long var2) throws IOException {
      this.writeUInt64(var1, encodeZigZag64(var2));
   }

   public abstract void writeFixed64(int var1, long var2) throws IOException;

   public final void writeSFixed64(int var1, long var2) throws IOException {
      this.writeFixed64(var1, var2);
   }

   public final void writeFloat(int var1, float var2) throws IOException {
      this.writeFixed32(var1, Float.floatToRawIntBits(var2));
   }

   public final void writeDouble(int var1, double var2) throws IOException {
      this.writeFixed64(var1, Double.doubleToRawLongBits(var2));
   }

   public abstract void writeBool(int var1, boolean var2) throws IOException;

   public final void writeEnum(int var1, int var2) throws IOException {
      this.writeInt32(var1, var2);
   }

   public abstract void writeString(int var1, String var2) throws IOException;

   public abstract void writeBytes(int var1, ByteString var2) throws IOException;

   public abstract void writeByteArray(int var1, byte[] var2) throws IOException;

   public abstract void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException;

   public abstract void writeByteBuffer(int var1, ByteBuffer var2) throws IOException;

   public final void writeRawByte(byte var1) throws IOException {
      this.write(var1);
   }

   public final void writeRawByte(int var1) throws IOException {
      this.write((byte)var1);
   }

   public final void writeRawBytes(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public final void writeRawBytes(byte[] var1, int var2, int var3) throws IOException {
      this.write(var1, var2, var3);
   }

   public final void writeRawBytes(ByteString var1) throws IOException {
      var1.writeTo(this);
   }

   public abstract void writeRawBytes(ByteBuffer var1) throws IOException;

   public abstract void writeMessage(int var1, MessageLite var2) throws IOException;

   abstract void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException;

   public abstract void writeMessageSetExtension(int var1, MessageLite var2) throws IOException;

   public abstract void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException;

   public abstract void writeInt32NoTag(int var1) throws IOException;

   public abstract void writeUInt32NoTag(int var1) throws IOException;

   public final void writeSInt32NoTag(int var1) throws IOException {
      this.writeUInt32NoTag(encodeZigZag32(var1));
   }

   public abstract void writeFixed32NoTag(int var1) throws IOException;

   public final void writeSFixed32NoTag(int var1) throws IOException {
      this.writeFixed32NoTag(var1);
   }

   public final void writeInt64NoTag(long var1) throws IOException {
      this.writeUInt64NoTag(var1);
   }

   public abstract void writeUInt64NoTag(long var1) throws IOException;

   public final void writeSInt64NoTag(long var1) throws IOException {
      this.writeUInt64NoTag(encodeZigZag64(var1));
   }

   public abstract void writeFixed64NoTag(long var1) throws IOException;

   public final void writeSFixed64NoTag(long var1) throws IOException {
      this.writeFixed64NoTag(var1);
   }

   public final void writeFloatNoTag(float var1) throws IOException {
      this.writeFixed32NoTag(Float.floatToRawIntBits(var1));
   }

   public final void writeDoubleNoTag(double var1) throws IOException {
      this.writeFixed64NoTag(Double.doubleToRawLongBits(var1));
   }

   public final void writeBoolNoTag(boolean var1) throws IOException {
      this.write((byte)(var1 ? 1 : 0));
   }

   public final void writeEnumNoTag(int var1) throws IOException {
      this.writeInt32NoTag(var1);
   }

   public abstract void writeStringNoTag(String var1) throws IOException;

   public abstract void writeBytesNoTag(ByteString var1) throws IOException;

   public final void writeByteArrayNoTag(byte[] var1) throws IOException {
      this.writeByteArrayNoTag(var1, 0, var1.length);
   }

   public abstract void writeMessageNoTag(MessageLite var1) throws IOException;

   abstract void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException;

   @Override
   public abstract void write(byte var1) throws IOException;

   @Override
   public abstract void write(byte[] var1, int var2, int var3) throws IOException;

   @Override
   public abstract void writeLazy(byte[] var1, int var2, int var3) throws IOException;

   @Override
   public abstract void write(ByteBuffer var1) throws IOException;

   @Override
   public abstract void writeLazy(ByteBuffer var1) throws IOException;

   public static int computeInt32Size(int var0, int var1) {
      return computeTagSize(var0) + computeInt32SizeNoTag(var1);
   }

   public static int computeUInt32Size(int var0, int var1) {
      return computeTagSize(var0) + computeUInt32SizeNoTag(var1);
   }

   public static int computeSInt32Size(int var0, int var1) {
      return computeTagSize(var0) + computeSInt32SizeNoTag(var1);
   }

   public static int computeFixed32Size(int var0, int var1) {
      return computeTagSize(var0) + computeFixed32SizeNoTag(var1);
   }

   public static int computeSFixed32Size(int var0, int var1) {
      return computeTagSize(var0) + computeSFixed32SizeNoTag(var1);
   }

   public static int computeInt64Size(int var0, long var1) {
      return computeTagSize(var0) + computeInt64SizeNoTag(var1);
   }

   public static int computeUInt64Size(int var0, long var1) {
      return computeTagSize(var0) + computeUInt64SizeNoTag(var1);
   }

   public static int computeSInt64Size(int var0, long var1) {
      return computeTagSize(var0) + computeSInt64SizeNoTag(var1);
   }

   public static int computeFixed64Size(int var0, long var1) {
      return computeTagSize(var0) + computeFixed64SizeNoTag(var1);
   }

   public static int computeSFixed64Size(int var0, long var1) {
      return computeTagSize(var0) + computeSFixed64SizeNoTag(var1);
   }

   public static int computeFloatSize(int var0, float var1) {
      return computeTagSize(var0) + computeFloatSizeNoTag(var1);
   }

   public static int computeDoubleSize(int var0, double var1) {
      return computeTagSize(var0) + computeDoubleSizeNoTag(var1);
   }

   public static int computeBoolSize(int var0, boolean var1) {
      return computeTagSize(var0) + computeBoolSizeNoTag(var1);
   }

   public static int computeEnumSize(int var0, int var1) {
      return computeTagSize(var0) + computeEnumSizeNoTag(var1);
   }

   public static int computeStringSize(int var0, String var1) {
      return computeTagSize(var0) + computeStringSizeNoTag(var1);
   }

   public static int computeBytesSize(int var0, ByteString var1) {
      return computeTagSize(var0) + computeBytesSizeNoTag(var1);
   }

   public static int computeByteArraySize(int var0, byte[] var1) {
      return computeTagSize(var0) + computeByteArraySizeNoTag(var1);
   }

   public static int computeByteBufferSize(int var0, ByteBuffer var1) {
      return computeTagSize(var0) + computeByteBufferSizeNoTag(var1);
   }

   public static int computeLazyFieldSize(int var0, LazyFieldLite var1) {
      return computeTagSize(var0) + computeLazyFieldSizeNoTag(var1);
   }

   public static int computeMessageSize(int var0, MessageLite var1) {
      return computeTagSize(var0) + computeMessageSizeNoTag(var1);
   }

   static int computeMessageSize(int var0, MessageLite var1, Schema var2) {
      return computeTagSize(var0) + computeMessageSizeNoTag(var1, var2);
   }

   public static int computeMessageSetExtensionSize(int var0, MessageLite var1) {
      return computeTagSize(1) * 2 + computeUInt32Size(2, var0) + computeMessageSize(3, var1);
   }

   public static int computeRawMessageSetExtensionSize(int var0, ByteString var1) {
      return computeTagSize(1) * 2 + computeUInt32Size(2, var0) + computeBytesSize(3, var1);
   }

   public static int computeLazyFieldMessageSetExtensionSize(int var0, LazyFieldLite var1) {
      return computeTagSize(1) * 2 + computeUInt32Size(2, var0) + computeLazyFieldSize(3, var1);
   }

   public static int computeTagSize(int var0) {
      return computeUInt32SizeNoTag(WireFormat.makeTag(var0, 0));
   }

   public static int computeInt32SizeNoTag(int var0) {
      return var0 >= 0 ? computeUInt32SizeNoTag(var0) : 10;
   }

   public static int computeUInt32SizeNoTag(int var0) {
      if ((var0 & -128) == 0) {
         return 1;
      } else if ((var0 & -16384) == 0) {
         return 2;
      } else if ((var0 & -2097152) == 0) {
         return 3;
      } else {
         return (var0 & -268435456) == 0 ? 4 : 5;
      }
   }

   public static int computeSInt32SizeNoTag(int var0) {
      return computeUInt32SizeNoTag(encodeZigZag32(var0));
   }

   public static int computeFixed32SizeNoTag(int var0) {
      return 4;
   }

   public static int computeSFixed32SizeNoTag(int var0) {
      return 4;
   }

   public static int computeInt64SizeNoTag(long var0) {
      return computeUInt64SizeNoTag(var0);
   }

   public static int computeUInt64SizeNoTag(long var0) {
      if ((var0 & -128L) == 0L) {
         return 1;
      } else if (var0 < 0L) {
         return 10;
      } else {
         int var2 = 2;
         if ((var0 & -34359738368L) != 0L) {
            var2 += 4;
            var0 >>>= 28;
         }

         if ((var0 & -2097152L) != 0L) {
            var2 += 2;
            var0 >>>= 14;
         }

         if ((var0 & -16384L) != 0L) {
            var2++;
         }

         return var2;
      }
   }

   public static int computeSInt64SizeNoTag(long var0) {
      return computeUInt64SizeNoTag(encodeZigZag64(var0));
   }

   public static int computeFixed64SizeNoTag(long var0) {
      return 8;
   }

   public static int computeSFixed64SizeNoTag(long var0) {
      return 8;
   }

   public static int computeFloatSizeNoTag(float var0) {
      return 4;
   }

   public static int computeDoubleSizeNoTag(double var0) {
      return 8;
   }

   public static int computeBoolSizeNoTag(boolean var0) {
      return 1;
   }

   public static int computeEnumSizeNoTag(int var0) {
      return computeInt32SizeNoTag(var0);
   }

   public static int computeStringSizeNoTag(String var0) {
      int var1;
      try {
         var1 = Utf8.encodedLength(var0);
      } catch (Utf8.UnpairedSurrogateException var4) {
         byte[] var3 = var0.getBytes(Internal.UTF_8);
         var1 = var3.length;
      }

      return computeLengthDelimitedFieldSize(var1);
   }

   public static int computeLazyFieldSizeNoTag(LazyFieldLite var0) {
      return computeLengthDelimitedFieldSize(var0.getSerializedSize());
   }

   public static int computeBytesSizeNoTag(ByteString var0) {
      return computeLengthDelimitedFieldSize(var0.size());
   }

   public static int computeByteArraySizeNoTag(byte[] var0) {
      return computeLengthDelimitedFieldSize(var0.length);
   }

   public static int computeByteBufferSizeNoTag(ByteBuffer var0) {
      return computeLengthDelimitedFieldSize(var0.capacity());
   }

   public static int computeMessageSizeNoTag(MessageLite var0) {
      return computeLengthDelimitedFieldSize(var0.getSerializedSize());
   }

   static int computeMessageSizeNoTag(MessageLite var0, Schema var1) {
      return computeLengthDelimitedFieldSize(((AbstractMessageLite)var0).getSerializedSize(var1));
   }

   static int computeLengthDelimitedFieldSize(int var0) {
      return computeUInt32SizeNoTag(var0) + var0;
   }

   public static int encodeZigZag32(int var0) {
      return var0 << 1 ^ var0 >> 31;
   }

   public static long encodeZigZag64(long var0) {
      return var0 << 1 ^ var0 >> 63;
   }

   public abstract void flush() throws IOException;

   public abstract int spaceLeft();

   public final void checkNoSpaceLeft() {
      if (this.spaceLeft() != 0) {
         throw new IllegalStateException("Did not write as much data as expected.");
      }
   }

   public abstract int getTotalBytesWritten();

   abstract void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException;

   final void inefficientWriteStringNoTag(String var1, Utf8.UnpairedSurrogateException var2) throws IOException {
      logger.log(Level.WARNING, "Converting ill-formed UTF-16. Your Protocol Buffer will not round trip correctly!", (Throwable)var2);
      byte[] var3 = var1.getBytes(Internal.UTF_8);

      try {
         this.writeUInt32NoTag(var3.length);
         this.writeLazy(var3, 0, var3.length);
      } catch (IndexOutOfBoundsException var5) {
         throw new CodedOutputStream.OutOfSpaceException(var5);
      } catch (CodedOutputStream.OutOfSpaceException var6) {
         throw var6;
      }
   }

   @Deprecated
   public final void writeGroup(int var1, MessageLite var2) throws IOException {
      this.writeTag(var1, 3);
      this.writeGroupNoTag(var2);
      this.writeTag(var1, 4);
   }

   @Deprecated
   final void writeGroup(int var1, MessageLite var2, Schema var3) throws IOException {
      this.writeTag(var1, 3);
      this.writeGroupNoTag(var2, var3);
      this.writeTag(var1, 4);
   }

   @Deprecated
   public final void writeGroupNoTag(MessageLite var1) throws IOException {
      var1.writeTo(this);
   }

   @Deprecated
   final void writeGroupNoTag(MessageLite var1, Schema var2) throws IOException {
      var2.writeTo(var1, this.wrapper);
   }

   @Deprecated
   public static int computeGroupSize(int var0, MessageLite var1) {
      return computeTagSize(var0) * 2 + computeGroupSizeNoTag(var1);
   }

   @Deprecated
   static int computeGroupSize(int var0, MessageLite var1, Schema var2) {
      return computeTagSize(var0) * 2 + computeGroupSizeNoTag(var1, var2);
   }

   @Deprecated
   public static int computeGroupSizeNoTag(MessageLite var0) {
      return var0.getSerializedSize();
   }

   @Deprecated
   static int computeGroupSizeNoTag(MessageLite var0, Schema var1) {
      return ((AbstractMessageLite)var0).getSerializedSize(var1);
   }

   @Deprecated
   public final void writeRawVarint32(int var1) throws IOException {
      this.writeUInt32NoTag(var1);
   }

   @Deprecated
   public final void writeRawVarint64(long var1) throws IOException {
      this.writeUInt64NoTag(var1);
   }

   @Deprecated
   public static int computeRawVarint32Size(int var0) {
      return computeUInt32SizeNoTag(var0);
   }

   @Deprecated
   public static int computeRawVarint64Size(long var0) {
      return computeUInt64SizeNoTag(var0);
   }

   @Deprecated
   public final void writeRawLittleEndian32(int var1) throws IOException {
      this.writeFixed32NoTag(var1);
   }

   @Deprecated
   public final void writeRawLittleEndian64(long var1) throws IOException {
      this.writeFixed64NoTag(var1);
   }

   private abstract static class AbstractBufferedEncoder extends CodedOutputStream {
      final byte[] buffer;
      final int limit;
      int position;
      int totalBytesWritten;

      AbstractBufferedEncoder(int var1) {
         if (var1 < 0) {
            throw new IllegalArgumentException("bufferSize must be >= 0");
         } else {
            this.buffer = new byte[Math.max(var1, 20)];
            this.limit = this.buffer.length;
         }
      }

      @Override
      public final int spaceLeft() {
         throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array or ByteBuffer.");
      }

      @Override
      public final int getTotalBytesWritten() {
         return this.totalBytesWritten;
      }

      final void buffer(byte var1) {
         this.buffer[this.position++] = var1;
         this.totalBytesWritten++;
      }

      final void bufferTag(int var1, int var2) {
         this.bufferUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      final void bufferInt32NoTag(int var1) {
         if (var1 >= 0) {
            this.bufferUInt32NoTag(var1);
         } else {
            this.bufferUInt64NoTag((long)var1);
         }
      }

      final void bufferUInt32NoTag(int var1) {
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
            long var2 = (long)this.position;

            while ((var1 & -128) != 0) {
               UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)(var1 & 127 | 128));
               var1 >>>= 7;
            }

            UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
            int var4 = (int)((long)this.position - var2);
            this.totalBytesWritten += var4;
         } else {
            while ((var1 & -128) != 0) {
               this.buffer[this.position++] = (byte)(var1 & 127 | 128);
               this.totalBytesWritten++;
               var1 >>>= 7;
            }

            this.buffer[this.position++] = (byte)var1;
            this.totalBytesWritten++;
         }
      }

      final void bufferUInt64NoTag(long var1) {
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS) {
            long var3 = (long)this.position;

            while ((var1 & -128L) != 0L) {
               UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)((int)var1 & 127 | 128));
               var1 >>>= 7;
            }

            UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)((int)var1));
            int var5 = (int)((long)this.position - var3);
            this.totalBytesWritten += var5;
         } else {
            while ((var1 & -128L) != 0L) {
               this.buffer[this.position++] = (byte)((int)var1 & 127 | 128);
               this.totalBytesWritten++;
               var1 >>>= 7;
            }

            this.buffer[this.position++] = (byte)((int)var1);
            this.totalBytesWritten++;
         }
      }

      final void bufferFixed32NoTag(int var1) {
         this.buffer[this.position++] = (byte)(var1 & 0xFF);
         this.buffer[this.position++] = (byte)(var1 >> 8 & 0xFF);
         this.buffer[this.position++] = (byte)(var1 >> 16 & 0xFF);
         this.buffer[this.position++] = (byte)(var1 >> 24 & 0xFF);
         this.totalBytesWritten += 4;
      }

      final void bufferFixed64NoTag(long var1) {
         this.buffer[this.position++] = (byte)((int)(var1 & 255L));
         this.buffer[this.position++] = (byte)((int)(var1 >> 8 & 255L));
         this.buffer[this.position++] = (byte)((int)(var1 >> 16 & 255L));
         this.buffer[this.position++] = (byte)((int)(var1 >> 24 & 255L));
         this.buffer[this.position++] = (byte)((int)(var1 >> 32) & 0xFF);
         this.buffer[this.position++] = (byte)((int)(var1 >> 40) & 0xFF);
         this.buffer[this.position++] = (byte)((int)(var1 >> 48) & 0xFF);
         this.buffer[this.position++] = (byte)((int)(var1 >> 56) & 0xFF);
         this.totalBytesWritten += 8;
      }
   }

   private static class ArrayEncoder extends CodedOutputStream {
      private final byte[] buffer;
      private final int offset;
      private final int limit;
      private int position;

      ArrayEncoder(byte[] var1, int var2, int var3) {
         if (var1 == null) {
            throw new NullPointerException("buffer");
         } else if ((var2 | var3 | var1.length - (var2 + var3)) < 0) {
            throw new IllegalArgumentException(String.format("Array range is invalid. Buffer.length=%d, offset=%d, length=%d", var1.length, var2, var3));
         } else {
            this.buffer = var1;
            this.offset = var2;
            this.position = var2;
            this.limit = var2 + var3;
         }
      }

      @Override
      public final void writeTag(int var1, int var2) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      @Override
      public final void writeInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeInt32NoTag(var2);
      }

      @Override
      public final void writeUInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt32NoTag(var2);
      }

      @Override
      public final void writeFixed32(int var1, int var2) throws IOException {
         this.writeTag(var1, 5);
         this.writeFixed32NoTag(var2);
      }

      @Override
      public final void writeUInt64(int var1, long var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt64NoTag(var2);
      }

      @Override
      public final void writeFixed64(int var1, long var2) throws IOException {
         this.writeTag(var1, 1);
         this.writeFixed64NoTag(var2);
      }

      @Override
      public final void writeBool(int var1, boolean var2) throws IOException {
         this.writeTag(var1, 0);
         this.write((byte)(var2 ? 1 : 0));
      }

      @Override
      public final void writeString(int var1, String var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeStringNoTag(var2);
      }

      @Override
      public final void writeBytes(int var1, ByteString var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeBytesNoTag(var2);
      }

      @Override
      public final void writeByteArray(int var1, byte[] var2) throws IOException {
         this.writeByteArray(var1, var2, 0, var2.length);
      }

      @Override
      public final void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException {
         this.writeTag(var1, 2);
         this.writeByteArrayNoTag(var2, var3, var4);
      }

      @Override
      public final void writeByteBuffer(int var1, ByteBuffer var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(var2.capacity());
         this.writeRawBytes(var2);
      }

      @Override
      public final void writeBytesNoTag(ByteString var1) throws IOException {
         this.writeUInt32NoTag(var1.size());
         var1.writeTo(this);
      }

      @Override
      public final void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException {
         this.writeUInt32NoTag(var3);
         this.write(var1, var2, var3);
      }

      @Override
      public final void writeRawBytes(ByteBuffer var1) throws IOException {
         if (var1.hasArray()) {
            this.write(var1.array(), var1.arrayOffset(), var1.capacity());
         } else {
            ByteBuffer var2 = var1.duplicate();
            ((Buffer)var2).clear();
            this.write(var2);
         }
      }

      @Override
      public final void writeMessage(int var1, MessageLite var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2);
      }

      @Override
      final void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(((AbstractMessageLite)var2).getSerializedSize(var3));
         var3.writeTo(var2, this.wrapper);
      }

      @Override
      public final void writeMessageSetExtension(int var1, MessageLite var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeMessage(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public final void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeBytes(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public final void writeMessageNoTag(MessageLite var1) throws IOException {
         this.writeUInt32NoTag(var1.getSerializedSize());
         var1.writeTo(this);
      }

      @Override
      final void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException {
         this.writeUInt32NoTag(((AbstractMessageLite)var1).getSerializedSize(var2));
         var2.writeTo(var1, this.wrapper);
      }

      @Override
      public final void write(byte var1) throws IOException {
         try {
            this.buffer[this.position++] = var1;
         } catch (IndexOutOfBoundsException var3) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1), var3);
         }
      }

      @Override
      public final void writeInt32NoTag(int var1) throws IOException {
         if (var1 >= 0) {
            this.writeUInt32NoTag(var1);
         } else {
            this.writeUInt64NoTag((long)var1);
         }
      }

      @Override
      public final void writeUInt32NoTag(int var1) throws IOException {
         if (!CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS || Android.isOnAndroidDevice() || this.spaceLeft() < 5) {
            try {
               while ((var1 & -128) != 0) {
                  this.buffer[this.position++] = (byte)(var1 & 127 | 128);
                  var1 >>>= 7;
               }

               this.buffer[this.position++] = (byte)var1;
            } catch (IndexOutOfBoundsException var3) {
               throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1), var3);
            }
         } else if ((var1 & -128) == 0) {
            UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
         } else {
            UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)(var1 | 128));
            var1 >>>= 7;
            if ((var1 & -128) == 0) {
               UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
            } else {
               UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)(var1 | 128));
               var1 >>>= 7;
               if ((var1 & -128) == 0) {
                  UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
               } else {
                  UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)(var1 | 128));
                  var1 >>>= 7;
                  if ((var1 & -128) == 0) {
                     UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
                  } else {
                     UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)(var1 | 128));
                     var1 >>>= 7;
                     UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)var1);
                  }
               }
            }
         }
      }

      @Override
      public final void writeFixed32NoTag(int var1) throws IOException {
         try {
            this.buffer[this.position++] = (byte)(var1 & 0xFF);
            this.buffer[this.position++] = (byte)(var1 >> 8 & 0xFF);
            this.buffer[this.position++] = (byte)(var1 >> 16 & 0xFF);
            this.buffer[this.position++] = (byte)(var1 >> 24 & 0xFF);
         } catch (IndexOutOfBoundsException var3) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1), var3);
         }
      }

      @Override
      public final void writeUInt64NoTag(long var1) throws IOException {
         if (CodedOutputStream.HAS_UNSAFE_ARRAY_OPERATIONS && this.spaceLeft() >= 10) {
            while ((var1 & -128L) != 0L) {
               UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)((int)var1 & 127 | 128));
               var1 >>>= 7;
            }

            UnsafeUtil.putByte(this.buffer, (long)(this.position++), (byte)((int)var1));
         } else {
            try {
               while ((var1 & -128L) != 0L) {
                  this.buffer[this.position++] = (byte)((int)var1 & 127 | 128);
                  var1 >>>= 7;
               }

               this.buffer[this.position++] = (byte)((int)var1);
            } catch (IndexOutOfBoundsException var4) {
               throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1), var4);
            }
         }
      }

      @Override
      public final void writeFixed64NoTag(long var1) throws IOException {
         try {
            this.buffer[this.position++] = (byte)((int)var1 & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 8) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 16) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 24) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 32) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 40) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 48) & 0xFF);
            this.buffer[this.position++] = (byte)((int)(var1 >> 56) & 0xFF);
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1), var4);
         }
      }

      @Override
      public final void write(byte[] var1, int var2, int var3) throws IOException {
         try {
            System.arraycopy(var1, var2, this.buffer, this.position, var3);
            this.position += var3;
         } catch (IndexOutOfBoundsException var5) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, var3), var5);
         }
      }

      @Override
      public final void writeLazy(byte[] var1, int var2, int var3) throws IOException {
         this.write(var1, var2, var3);
      }

      @Override
      public final void write(ByteBuffer var1) throws IOException {
         int var2 = var1.remaining();

         try {
            var1.get(this.buffer, this.position, var2);
            this.position += var2;
         } catch (IndexOutOfBoundsException var4) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, var2), var4);
         }
      }

      @Override
      public final void writeLazy(ByteBuffer var1) throws IOException {
         this.write(var1);
      }

      @Override
      public final void writeStringNoTag(String var1) throws IOException {
         int var2 = this.position;

         try {
            int var3 = var1.length() * 3;
            int var4 = computeUInt32SizeNoTag(var3);
            int var5 = computeUInt32SizeNoTag(var1.length());
            if (var5 == var4) {
               this.position = var2 + var5;
               int var6 = Utf8.encode(var1, this.buffer, this.position, this.spaceLeft());
               this.position = var2;
               int var7 = var6 - var2 - var5;
               this.writeUInt32NoTag(var7);
               this.position = var6;
            } else {
               int var10 = Utf8.encodedLength(var1);
               this.writeUInt32NoTag(var10);
               this.position = Utf8.encode(var1, this.buffer, this.position, this.spaceLeft());
            }
         } catch (Utf8.UnpairedSurrogateException var8) {
            this.position = var2;
            this.inefficientWriteStringNoTag(var1, var8);
         } catch (IndexOutOfBoundsException var9) {
            throw new CodedOutputStream.OutOfSpaceException(var9);
         }
      }

      @Override
      public void flush() {
      }

      @Override
      public final int spaceLeft() {
         return this.limit - this.position;
      }

      @Override
      public final int getTotalBytesWritten() {
         return this.position - this.offset;
      }
   }

   private static final class ByteOutputEncoder extends CodedOutputStream.AbstractBufferedEncoder {
      private final ByteOutput out;

      ByteOutputEncoder(ByteOutput var1, int var2) {
         super(var2);
         if (var1 == null) {
            throw new NullPointerException("out");
         } else {
            this.out = var1;
         }
      }

      @Override
      public void writeTag(int var1, int var2) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      @Override
      public void writeInt32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferInt32NoTag(var2);
      }

      @Override
      public void writeUInt32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferUInt32NoTag(var2);
      }

      @Override
      public void writeFixed32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(14);
         this.bufferTag(var1, 5);
         this.bufferFixed32NoTag(var2);
      }

      @Override
      public void writeUInt64(int var1, long var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferUInt64NoTag(var2);
      }

      @Override
      public void writeFixed64(int var1, long var2) throws IOException {
         this.flushIfNotAvailable(18);
         this.bufferTag(var1, 1);
         this.bufferFixed64NoTag(var2);
      }

      @Override
      public void writeBool(int var1, boolean var2) throws IOException {
         this.flushIfNotAvailable(11);
         this.bufferTag(var1, 0);
         this.buffer((byte)(var2 ? 1 : 0));
      }

      @Override
      public void writeString(int var1, String var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeStringNoTag(var2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeBytesNoTag(var2);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2) throws IOException {
         this.writeByteArray(var1, var2, 0, var2.length);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException {
         this.writeTag(var1, 2);
         this.writeByteArrayNoTag(var2, var3, var4);
      }

      @Override
      public void writeByteBuffer(int var1, ByteBuffer var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(var2.capacity());
         this.writeRawBytes(var2);
      }

      @Override
      public void writeBytesNoTag(ByteString var1) throws IOException {
         this.writeUInt32NoTag(var1.size());
         var1.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException {
         this.writeUInt32NoTag(var3);
         this.write(var1, var2, var3);
      }

      @Override
      public void writeRawBytes(ByteBuffer var1) throws IOException {
         if (var1.hasArray()) {
            this.write(var1.array(), var1.arrayOffset(), var1.capacity());
         } else {
            ByteBuffer var2 = var1.duplicate();
            ((Buffer)var2).clear();
            this.write(var2);
         }
      }

      @Override
      public void writeMessage(int var1, MessageLite var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2);
      }

      @Override
      void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2, var3);
      }

      @Override
      public void writeMessageSetExtension(int var1, MessageLite var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeMessage(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeBytes(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(MessageLite var1) throws IOException {
         this.writeUInt32NoTag(var1.getSerializedSize());
         var1.writeTo(this);
      }

      @Override
      void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException {
         this.writeUInt32NoTag(((AbstractMessageLite)var1).getSerializedSize(var2));
         var2.writeTo(var1, this.wrapper);
      }

      @Override
      public void write(byte var1) throws IOException {
         if (this.position == this.limit) {
            this.doFlush();
         }

         this.buffer(var1);
      }

      @Override
      public void writeInt32NoTag(int var1) throws IOException {
         if (var1 >= 0) {
            this.writeUInt32NoTag(var1);
         } else {
            this.writeUInt64NoTag((long)var1);
         }
      }

      @Override
      public void writeUInt32NoTag(int var1) throws IOException {
         this.flushIfNotAvailable(5);
         this.bufferUInt32NoTag(var1);
      }

      @Override
      public void writeFixed32NoTag(int var1) throws IOException {
         this.flushIfNotAvailable(4);
         this.bufferFixed32NoTag(var1);
      }

      @Override
      public void writeUInt64NoTag(long var1) throws IOException {
         this.flushIfNotAvailable(10);
         this.bufferUInt64NoTag(var1);
      }

      @Override
      public void writeFixed64NoTag(long var1) throws IOException {
         this.flushIfNotAvailable(8);
         this.bufferFixed64NoTag(var1);
      }

      @Override
      public void writeStringNoTag(String var1) throws IOException {
         int var2 = var1.length() * 3;
         int var3 = computeUInt32SizeNoTag(var2);
         if (var3 + var2 > this.limit) {
            byte[] var10 = new byte[var2];
            int var11 = Utf8.encode(var1, var10, 0, var2);
            this.writeUInt32NoTag(var11);
            this.writeLazy(var10, 0, var11);
         } else {
            if (var3 + var2 > this.limit - this.position) {
               this.doFlush();
            }

            int var4 = this.position;

            try {
               int var5 = computeUInt32SizeNoTag(var1.length());
               if (var5 == var3) {
                  this.position = var4 + var5;
                  int var6 = Utf8.encode(var1, this.buffer, this.position, this.limit - this.position);
                  this.position = var4;
                  int var7 = var6 - var4 - var5;
                  this.bufferUInt32NoTag(var7);
                  this.position = var6;
                  this.totalBytesWritten += var7;
               } else {
                  int var12 = Utf8.encodedLength(var1);
                  this.bufferUInt32NoTag(var12);
                  this.position = Utf8.encode(var1, this.buffer, this.position, var12);
                  this.totalBytesWritten += var12;
               }
            } catch (Utf8.UnpairedSurrogateException var8) {
               this.totalBytesWritten = this.totalBytesWritten - (this.position - var4);
               this.position = var4;
               this.inefficientWriteStringNoTag(var1, var8);
            } catch (IndexOutOfBoundsException var9) {
               throw new CodedOutputStream.OutOfSpaceException(var9);
            }
         }
      }

      @Override
      public void flush() throws IOException {
         if (this.position > 0) {
            this.doFlush();
         }
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.flush();
         this.out.write(var1, var2, var3);
         this.totalBytesWritten += var3;
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) throws IOException {
         this.flush();
         this.out.writeLazy(var1, var2, var3);
         this.totalBytesWritten += var3;
      }

      @Override
      public void write(ByteBuffer var1) throws IOException {
         this.flush();
         int var2 = var1.remaining();
         this.out.write(var1);
         this.totalBytesWritten += var2;
      }

      @Override
      public void writeLazy(ByteBuffer var1) throws IOException {
         this.flush();
         int var2 = var1.remaining();
         this.out.writeLazy(var1);
         this.totalBytesWritten += var2;
      }

      private void flushIfNotAvailable(int var1) throws IOException {
         if (this.limit - this.position < var1) {
            this.doFlush();
         }
      }

      private void doFlush() throws IOException {
         this.out.write(this.buffer, 0, this.position);
         this.position = 0;
      }
   }

   private static final class HeapNioEncoder extends CodedOutputStream.ArrayEncoder {
      private final ByteBuffer byteBuffer;
      private int initialPosition;

      HeapNioEncoder(ByteBuffer var1) {
         super(var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
         this.byteBuffer = var1;
         this.initialPosition = var1.position();
      }

      @Override
      public void flush() {
         ((Buffer)this.byteBuffer).position(this.initialPosition + this.getTotalBytesWritten());
      }
   }

   public static class OutOfSpaceException extends IOException {
      private static final long serialVersionUID = -6947486886997889499L;
      private static final String MESSAGE = "CodedOutputStream was writing to a flat byte array and ran out of space.";

      OutOfSpaceException() {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.");
      }

      OutOfSpaceException(String var1) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + var1);
      }

      OutOfSpaceException(Throwable var1) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.", var1);
      }

      OutOfSpaceException(String var1, Throwable var2) {
         super("CodedOutputStream was writing to a flat byte array and ran out of space.: " + var1, var2);
      }
   }

   private static final class OutputStreamEncoder extends CodedOutputStream.AbstractBufferedEncoder {
      private final OutputStream out;

      OutputStreamEncoder(OutputStream var1, int var2) {
         super(var2);
         if (var1 == null) {
            throw new NullPointerException("out");
         } else {
            this.out = var1;
         }
      }

      @Override
      public void writeTag(int var1, int var2) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      @Override
      public void writeInt32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferInt32NoTag(var2);
      }

      @Override
      public void writeUInt32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferUInt32NoTag(var2);
      }

      @Override
      public void writeFixed32(int var1, int var2) throws IOException {
         this.flushIfNotAvailable(14);
         this.bufferTag(var1, 5);
         this.bufferFixed32NoTag(var2);
      }

      @Override
      public void writeUInt64(int var1, long var2) throws IOException {
         this.flushIfNotAvailable(20);
         this.bufferTag(var1, 0);
         this.bufferUInt64NoTag(var2);
      }

      @Override
      public void writeFixed64(int var1, long var2) throws IOException {
         this.flushIfNotAvailable(18);
         this.bufferTag(var1, 1);
         this.bufferFixed64NoTag(var2);
      }

      @Override
      public void writeBool(int var1, boolean var2) throws IOException {
         this.flushIfNotAvailable(11);
         this.bufferTag(var1, 0);
         this.buffer((byte)(var2 ? 1 : 0));
      }

      @Override
      public void writeString(int var1, String var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeStringNoTag(var2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeBytesNoTag(var2);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2) throws IOException {
         this.writeByteArray(var1, var2, 0, var2.length);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException {
         this.writeTag(var1, 2);
         this.writeByteArrayNoTag(var2, var3, var4);
      }

      @Override
      public void writeByteBuffer(int var1, ByteBuffer var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(var2.capacity());
         this.writeRawBytes(var2);
      }

      @Override
      public void writeBytesNoTag(ByteString var1) throws IOException {
         this.writeUInt32NoTag(var1.size());
         var1.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException {
         this.writeUInt32NoTag(var3);
         this.write(var1, var2, var3);
      }

      @Override
      public void writeRawBytes(ByteBuffer var1) throws IOException {
         if (var1.hasArray()) {
            this.write(var1.array(), var1.arrayOffset(), var1.capacity());
         } else {
            ByteBuffer var2 = var1.duplicate();
            ((Buffer)var2).clear();
            this.write(var2);
         }
      }

      @Override
      public void writeMessage(int var1, MessageLite var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2);
      }

      @Override
      void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2, var3);
      }

      @Override
      public void writeMessageSetExtension(int var1, MessageLite var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeMessage(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeBytes(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(MessageLite var1) throws IOException {
         this.writeUInt32NoTag(var1.getSerializedSize());
         var1.writeTo(this);
      }

      @Override
      void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException {
         this.writeUInt32NoTag(((AbstractMessageLite)var1).getSerializedSize(var2));
         var2.writeTo(var1, this.wrapper);
      }

      @Override
      public void write(byte var1) throws IOException {
         if (this.position == this.limit) {
            this.doFlush();
         }

         this.buffer(var1);
      }

      @Override
      public void writeInt32NoTag(int var1) throws IOException {
         if (var1 >= 0) {
            this.writeUInt32NoTag(var1);
         } else {
            this.writeUInt64NoTag((long)var1);
         }
      }

      @Override
      public void writeUInt32NoTag(int var1) throws IOException {
         this.flushIfNotAvailable(5);
         this.bufferUInt32NoTag(var1);
      }

      @Override
      public void writeFixed32NoTag(int var1) throws IOException {
         this.flushIfNotAvailable(4);
         this.bufferFixed32NoTag(var1);
      }

      @Override
      public void writeUInt64NoTag(long var1) throws IOException {
         this.flushIfNotAvailable(10);
         this.bufferUInt64NoTag(var1);
      }

      @Override
      public void writeFixed64NoTag(long var1) throws IOException {
         this.flushIfNotAvailable(8);
         this.bufferFixed64NoTag(var1);
      }

      @Override
      public void writeStringNoTag(String var1) throws IOException {
         try {
            int var2 = var1.length() * 3;
            int var3 = computeUInt32SizeNoTag(var2);
            if (var3 + var2 > this.limit) {
               byte[] var11 = new byte[var2];
               int var12 = Utf8.encode(var1, var11, 0, var2);
               this.writeUInt32NoTag(var12);
               this.writeLazy(var11, 0, var12);
               return;
            }

            if (var3 + var2 > this.limit - this.position) {
               this.doFlush();
            }

            int var4 = computeUInt32SizeNoTag(var1.length());
            int var5 = this.position;

            try {
               int var6;
               if (var4 == var3) {
                  this.position = var5 + var4;
                  int var7 = Utf8.encode(var1, this.buffer, this.position, this.limit - this.position);
                  this.position = var5;
                  var6 = var7 - var5 - var4;
                  this.bufferUInt32NoTag(var6);
                  this.position = var7;
               } else {
                  var6 = Utf8.encodedLength(var1);
                  this.bufferUInt32NoTag(var6);
                  this.position = Utf8.encode(var1, this.buffer, this.position, var6);
               }

               this.totalBytesWritten += var6;
            } catch (Utf8.UnpairedSurrogateException var8) {
               this.totalBytesWritten = this.totalBytesWritten - (this.position - var5);
               this.position = var5;
               throw var8;
            } catch (ArrayIndexOutOfBoundsException var9) {
               throw new CodedOutputStream.OutOfSpaceException(var9);
            }
         } catch (Utf8.UnpairedSurrogateException var10) {
            this.inefficientWriteStringNoTag(var1, var10);
         }
      }

      @Override
      public void flush() throws IOException {
         if (this.position > 0) {
            this.doFlush();
         }
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (this.limit - this.position >= var3) {
            System.arraycopy(var1, var2, this.buffer, this.position, var3);
            this.position += var3;
            this.totalBytesWritten += var3;
         } else {
            int var4 = this.limit - this.position;
            System.arraycopy(var1, var2, this.buffer, this.position, var4);
            var2 += var4;
            var3 -= var4;
            this.position = this.limit;
            this.totalBytesWritten += var4;
            this.doFlush();
            if (var3 <= this.limit) {
               System.arraycopy(var1, var2, this.buffer, 0, var3);
               this.position = var3;
            } else {
               this.out.write(var1, var2, var3);
            }

            this.totalBytesWritten += var3;
         }
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) throws IOException {
         this.write(var1, var2, var3);
      }

      @Override
      public void write(ByteBuffer var1) throws IOException {
         int var2 = var1.remaining();
         if (this.limit - this.position >= var2) {
            var1.get(this.buffer, this.position, var2);
            this.position += var2;
            this.totalBytesWritten += var2;
         } else {
            int var3 = this.limit - this.position;
            var1.get(this.buffer, this.position, var3);
            var2 -= var3;
            this.position = this.limit;
            this.totalBytesWritten += var3;
            this.doFlush();

            while (var2 > this.limit) {
               var1.get(this.buffer, 0, this.limit);
               this.out.write(this.buffer, 0, this.limit);
               var2 -= this.limit;
               this.totalBytesWritten = this.totalBytesWritten + this.limit;
            }

            var1.get(this.buffer, 0, var2);
            this.position = var2;
            this.totalBytesWritten += var2;
         }
      }

      @Override
      public void writeLazy(ByteBuffer var1) throws IOException {
         this.write(var1);
      }

      private void flushIfNotAvailable(int var1) throws IOException {
         if (this.limit - this.position < var1) {
            this.doFlush();
         }
      }

      private void doFlush() throws IOException {
         this.out.write(this.buffer, 0, this.position);
         this.position = 0;
      }
   }

   private static final class SafeDirectNioEncoder extends CodedOutputStream {
      private final ByteBuffer originalBuffer;
      private final ByteBuffer buffer;
      private final int initialPosition;

      SafeDirectNioEncoder(ByteBuffer var1) {
         this.originalBuffer = var1;
         this.buffer = var1.duplicate().order(ByteOrder.LITTLE_ENDIAN);
         this.initialPosition = var1.position();
      }

      @Override
      public void writeTag(int var1, int var2) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      @Override
      public void writeInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeInt32NoTag(var2);
      }

      @Override
      public void writeUInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt32NoTag(var2);
      }

      @Override
      public void writeFixed32(int var1, int var2) throws IOException {
         this.writeTag(var1, 5);
         this.writeFixed32NoTag(var2);
      }

      @Override
      public void writeUInt64(int var1, long var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt64NoTag(var2);
      }

      @Override
      public void writeFixed64(int var1, long var2) throws IOException {
         this.writeTag(var1, 1);
         this.writeFixed64NoTag(var2);
      }

      @Override
      public void writeBool(int var1, boolean var2) throws IOException {
         this.writeTag(var1, 0);
         this.write((byte)(var2 ? 1 : 0));
      }

      @Override
      public void writeString(int var1, String var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeStringNoTag(var2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeBytesNoTag(var2);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2) throws IOException {
         this.writeByteArray(var1, var2, 0, var2.length);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException {
         this.writeTag(var1, 2);
         this.writeByteArrayNoTag(var2, var3, var4);
      }

      @Override
      public void writeByteBuffer(int var1, ByteBuffer var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(var2.capacity());
         this.writeRawBytes(var2);
      }

      @Override
      public void writeMessage(int var1, MessageLite var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2);
      }

      @Override
      void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2, var3);
      }

      @Override
      public void writeMessageSetExtension(int var1, MessageLite var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeMessage(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeBytes(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(MessageLite var1) throws IOException {
         this.writeUInt32NoTag(var1.getSerializedSize());
         var1.writeTo(this);
      }

      @Override
      void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException {
         this.writeUInt32NoTag(((AbstractMessageLite)var1).getSerializedSize(var2));
         var2.writeTo(var1, this.wrapper);
      }

      @Override
      public void write(byte var1) throws IOException {
         try {
            this.buffer.put(var1);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeBytesNoTag(ByteString var1) throws IOException {
         this.writeUInt32NoTag(var1.size());
         var1.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException {
         this.writeUInt32NoTag(var3);
         this.write(var1, var2, var3);
      }

      @Override
      public void writeRawBytes(ByteBuffer var1) throws IOException {
         if (var1.hasArray()) {
            this.write(var1.array(), var1.arrayOffset(), var1.capacity());
         } else {
            ByteBuffer var2 = var1.duplicate();
            ((Buffer)var2).clear();
            this.write(var2);
         }
      }

      @Override
      public void writeInt32NoTag(int var1) throws IOException {
         if (var1 >= 0) {
            this.writeUInt32NoTag(var1);
         } else {
            this.writeUInt64NoTag((long)var1);
         }
      }

      @Override
      public void writeUInt32NoTag(int var1) throws IOException {
         try {
            while ((var1 & -128) != 0) {
               this.buffer.put((byte)(var1 & 127 | 128));
               var1 >>>= 7;
            }

            this.buffer.put((byte)var1);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeFixed32NoTag(int var1) throws IOException {
         try {
            this.buffer.putInt(var1);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeUInt64NoTag(long var1) throws IOException {
         try {
            while ((var1 & -128L) != 0L) {
               this.buffer.put((byte)((int)var1 & 127 | 128));
               var1 >>>= 7;
            }

            this.buffer.put((byte)((int)var1));
         } catch (BufferOverflowException var4) {
            throw new CodedOutputStream.OutOfSpaceException(var4);
         }
      }

      @Override
      public void writeFixed64NoTag(long var1) throws IOException {
         try {
            this.buffer.putLong(var1);
         } catch (BufferOverflowException var4) {
            throw new CodedOutputStream.OutOfSpaceException(var4);
         }
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         try {
            this.buffer.put(var1, var2, var3);
         } catch (IndexOutOfBoundsException var5) {
            throw new CodedOutputStream.OutOfSpaceException(var5);
         } catch (BufferOverflowException var6) {
            throw new CodedOutputStream.OutOfSpaceException(var6);
         }
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) throws IOException {
         this.write(var1, var2, var3);
      }

      @Override
      public void write(ByteBuffer var1) throws IOException {
         try {
            this.buffer.put(var1);
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeLazy(ByteBuffer var1) throws IOException {
         this.write(var1);
      }

      @Override
      public void writeStringNoTag(String var1) throws IOException {
         int var2 = this.buffer.position();

         try {
            int var3 = var1.length() * 3;
            int var4 = computeUInt32SizeNoTag(var3);
            int var5 = computeUInt32SizeNoTag(var1.length());
            if (var5 == var4) {
               int var6 = this.buffer.position() + var5;
               ((Buffer)this.buffer).position(var6);
               this.encode(var1);
               int var7 = this.buffer.position();
               ((Buffer)this.buffer).position(var2);
               this.writeUInt32NoTag(var7 - var6);
               ((Buffer)this.buffer).position(var7);
            } else {
               int var10 = Utf8.encodedLength(var1);
               this.writeUInt32NoTag(var10);
               this.encode(var1);
            }
         } catch (Utf8.UnpairedSurrogateException var8) {
            ((Buffer)this.buffer).position(var2);
            this.inefficientWriteStringNoTag(var1, var8);
         } catch (IllegalArgumentException var9) {
            throw new CodedOutputStream.OutOfSpaceException(var9);
         }
      }

      @Override
      public void flush() {
         ((Buffer)this.originalBuffer).position(this.buffer.position());
      }

      @Override
      public int spaceLeft() {
         return this.buffer.remaining();
      }

      @Override
      public int getTotalBytesWritten() {
         return this.buffer.position() - this.initialPosition;
      }

      private void encode(String var1) throws IOException {
         try {
            Utf8.encodeUtf8(var1, this.buffer);
         } catch (IndexOutOfBoundsException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }
   }

   private static final class UnsafeDirectNioEncoder extends CodedOutputStream {
      private final ByteBuffer originalBuffer;
      private final ByteBuffer buffer;
      private final long address;
      private final long initialPosition;
      private final long limit;
      private final long oneVarintLimit;
      private long position;

      UnsafeDirectNioEncoder(ByteBuffer var1) {
         this.originalBuffer = var1;
         this.buffer = var1.duplicate().order(ByteOrder.LITTLE_ENDIAN);
         this.address = UnsafeUtil.addressOffset(var1);
         this.initialPosition = this.address + (long)var1.position();
         this.limit = this.address + (long)var1.limit();
         this.oneVarintLimit = this.limit - 10L;
         this.position = this.initialPosition;
      }

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      @Override
      public void writeTag(int var1, int var2) throws IOException {
         this.writeUInt32NoTag(WireFormat.makeTag(var1, var2));
      }

      @Override
      public void writeInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeInt32NoTag(var2);
      }

      @Override
      public void writeUInt32(int var1, int var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt32NoTag(var2);
      }

      @Override
      public void writeFixed32(int var1, int var2) throws IOException {
         this.writeTag(var1, 5);
         this.writeFixed32NoTag(var2);
      }

      @Override
      public void writeUInt64(int var1, long var2) throws IOException {
         this.writeTag(var1, 0);
         this.writeUInt64NoTag(var2);
      }

      @Override
      public void writeFixed64(int var1, long var2) throws IOException {
         this.writeTag(var1, 1);
         this.writeFixed64NoTag(var2);
      }

      @Override
      public void writeBool(int var1, boolean var2) throws IOException {
         this.writeTag(var1, 0);
         this.write((byte)(var2 ? 1 : 0));
      }

      @Override
      public void writeString(int var1, String var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeStringNoTag(var2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeBytesNoTag(var2);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2) throws IOException {
         this.writeByteArray(var1, var2, 0, var2.length);
      }

      @Override
      public void writeByteArray(int var1, byte[] var2, int var3, int var4) throws IOException {
         this.writeTag(var1, 2);
         this.writeByteArrayNoTag(var2, var3, var4);
      }

      @Override
      public void writeByteBuffer(int var1, ByteBuffer var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeUInt32NoTag(var2.capacity());
         this.writeRawBytes(var2);
      }

      @Override
      public void writeMessage(int var1, MessageLite var2) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2);
      }

      @Override
      void writeMessage(int var1, MessageLite var2, Schema var3) throws IOException {
         this.writeTag(var1, 2);
         this.writeMessageNoTag(var2, var3);
      }

      @Override
      public void writeMessageSetExtension(int var1, MessageLite var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeMessage(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeRawMessageSetExtension(int var1, ByteString var2) throws IOException {
         this.writeTag(1, 3);
         this.writeUInt32(2, var1);
         this.writeBytes(3, var2);
         this.writeTag(1, 4);
      }

      @Override
      public void writeMessageNoTag(MessageLite var1) throws IOException {
         this.writeUInt32NoTag(var1.getSerializedSize());
         var1.writeTo(this);
      }

      @Override
      void writeMessageNoTag(MessageLite var1, Schema var2) throws IOException {
         this.writeUInt32NoTag(((AbstractMessageLite)var1).getSerializedSize(var2));
         var2.writeTo(var1, this.wrapper);
      }

      @Override
      public void write(byte var1) throws IOException {
         if (this.position >= this.limit) {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1));
         } else {
            UnsafeUtil.putByte(this.position++, var1);
         }
      }

      @Override
      public void writeBytesNoTag(ByteString var1) throws IOException {
         this.writeUInt32NoTag(var1.size());
         var1.writeTo(this);
      }

      @Override
      public void writeByteArrayNoTag(byte[] var1, int var2, int var3) throws IOException {
         this.writeUInt32NoTag(var3);
         this.write(var1, var2, var3);
      }

      @Override
      public void writeRawBytes(ByteBuffer var1) throws IOException {
         if (var1.hasArray()) {
            this.write(var1.array(), var1.arrayOffset(), var1.capacity());
         } else {
            ByteBuffer var2 = var1.duplicate();
            ((Buffer)var2).clear();
            this.write(var2);
         }
      }

      @Override
      public void writeInt32NoTag(int var1) throws IOException {
         if (var1 >= 0) {
            this.writeUInt32NoTag(var1);
         } else {
            this.writeUInt64NoTag((long)var1);
         }
      }

      @Override
      public void writeUInt32NoTag(int var1) throws IOException {
         if (this.position > this.oneVarintLimit) {
            while (this.position < this.limit) {
               if ((var1 & -128) == 0) {
                  UnsafeUtil.putByte(this.position++, (byte)var1);
                  return;
               }

               UnsafeUtil.putByte(this.position++, (byte)(var1 & 127 | 128));
               var1 >>>= 7;
            }

            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1));
         } else {
            while ((var1 & -128) != 0) {
               UnsafeUtil.putByte(this.position++, (byte)(var1 & 127 | 128));
               var1 >>>= 7;
            }

            UnsafeUtil.putByte(this.position++, (byte)var1);
         }
      }

      @Override
      public void writeFixed32NoTag(int var1) throws IOException {
         this.buffer.putInt(this.bufferPos(this.position), var1);
         this.position += 4L;
      }

      @Override
      public void writeUInt64NoTag(long var1) throws IOException {
         if (this.position > this.oneVarintLimit) {
            while (this.position < this.limit) {
               if ((var1 & -128L) == 0L) {
                  UnsafeUtil.putByte(this.position++, (byte)((int)var1));
                  return;
               }

               UnsafeUtil.putByte(this.position++, (byte)((int)var1 & 127 | 128));
               var1 >>>= 7;
            }

            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, 1));
         } else {
            while ((var1 & -128L) != 0L) {
               UnsafeUtil.putByte(this.position++, (byte)((int)var1 & 127 | 128));
               var1 >>>= 7;
            }

            UnsafeUtil.putByte(this.position++, (byte)((int)var1));
         }
      }

      @Override
      public void writeFixed64NoTag(long var1) throws IOException {
         this.buffer.putLong(this.bufferPos(this.position), var1);
         this.position += 8L;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (var1 != null && var2 >= 0 && var3 >= 0 && var1.length - var3 >= var2 && this.limit - (long)var3 >= this.position) {
            UnsafeUtil.copyMemory(var1, (long)var2, this.position, (long)var3);
            this.position += (long)var3;
         } else if (var1 == null) {
            throw new NullPointerException("value");
         } else {
            throw new CodedOutputStream.OutOfSpaceException(String.format("Pos: %d, limit: %d, len: %d", this.position, this.limit, var3));
         }
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) throws IOException {
         this.write(var1, var2, var3);
      }

      @Override
      public void write(ByteBuffer var1) throws IOException {
         try {
            int var2 = var1.remaining();
            this.repositionBuffer(this.position);
            this.buffer.put(var1);
            this.position += (long)var2;
         } catch (BufferOverflowException var3) {
            throw new CodedOutputStream.OutOfSpaceException(var3);
         }
      }

      @Override
      public void writeLazy(ByteBuffer var1) throws IOException {
         this.write(var1);
      }

      @Override
      public void writeStringNoTag(String var1) throws IOException {
         long var2 = this.position;

         try {
            int var4 = var1.length() * 3;
            int var5 = computeUInt32SizeNoTag(var4);
            int var6 = computeUInt32SizeNoTag(var1.length());
            if (var6 == var5) {
               int var7 = this.bufferPos(this.position) + var6;
               ((Buffer)this.buffer).position(var7);
               Utf8.encodeUtf8(var1, this.buffer);
               int var8 = this.buffer.position() - var7;
               this.writeUInt32NoTag(var8);
               this.position += (long)var8;
            } else {
               int var12 = Utf8.encodedLength(var1);
               this.writeUInt32NoTag(var12);
               this.repositionBuffer(this.position);
               Utf8.encodeUtf8(var1, this.buffer);
               this.position += (long)var12;
            }
         } catch (Utf8.UnpairedSurrogateException var9) {
            this.position = var2;
            this.repositionBuffer(this.position);
            this.inefficientWriteStringNoTag(var1, var9);
         } catch (IllegalArgumentException var10) {
            throw new CodedOutputStream.OutOfSpaceException(var10);
         } catch (IndexOutOfBoundsException var11) {
            throw new CodedOutputStream.OutOfSpaceException(var11);
         }
      }

      @Override
      public void flush() {
         ((Buffer)this.originalBuffer).position(this.bufferPos(this.position));
      }

      @Override
      public int spaceLeft() {
         return (int)(this.limit - this.position);
      }

      @Override
      public int getTotalBytesWritten() {
         return (int)(this.position - this.initialPosition);
      }

      private void repositionBuffer(long var1) {
         ((Buffer)this.buffer).position(this.bufferPos(var1));
      }

      private int bufferPos(long var1) {
         return (int)(var1 - this.address);
      }
   }
}
