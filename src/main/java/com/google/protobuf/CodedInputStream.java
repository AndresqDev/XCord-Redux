package com.google.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CodedInputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private static final int DEFAULT_RECURSION_LIMIT = 100;
   private static final int DEFAULT_SIZE_LIMIT = Integer.MAX_VALUE;
   int recursionDepth;
   int recursionLimit = 100;
   int sizeLimit = Integer.MAX_VALUE;
   CodedInputStreamReader wrapper;
   private boolean shouldDiscardUnknownFields = false;

   public static CodedInputStream newInstance(InputStream var0) {
      return newInstance(var0, 4096);
   }

   public static CodedInputStream newInstance(InputStream var0, int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("bufferSize must be > 0");
      } else {
         return (CodedInputStream)(var0 == null ? newInstance(Internal.EMPTY_BYTE_ARRAY) : new CodedInputStream.StreamDecoder(var0, var1));
      }
   }

   public static CodedInputStream newInstance(Iterable<ByteBuffer> var0) {
      return !CodedInputStream.UnsafeDirectNioDecoder.isSupported() ? newInstance(new IterableByteBufferInputStream(var0)) : newInstance(var0, false);
   }

   static CodedInputStream newInstance(Iterable<ByteBuffer> var0, boolean var1) {
      byte var2 = 0;
      int var3 = 0;

      for (ByteBuffer var5 : var0) {
         var3 += var5.remaining();
         if (var5.hasArray()) {
            var2 |= 1;
         } else if (var5.isDirect()) {
            var2 |= 2;
         } else {
            var2 |= 4;
         }
      }

      return (CodedInputStream)(var2 == 2
         ? new CodedInputStream.IterableDirectByteBufferDecoder(var0, var3, var1)
         : newInstance(new IterableByteBufferInputStream(var0)));
   }

   public static CodedInputStream newInstance(byte[] var0) {
      return newInstance(var0, 0, var0.length);
   }

   public static CodedInputStream newInstance(byte[] var0, int var1, int var2) {
      return newInstance(var0, var1, var2, false);
   }

   static CodedInputStream newInstance(byte[] var0, int var1, int var2, boolean var3) {
      CodedInputStream.ArrayDecoder var4 = new CodedInputStream.ArrayDecoder(var0, var1, var2, var3);

      try {
         var4.pushLimit(var2);
         return var4;
      } catch (InvalidProtocolBufferException var6) {
         throw new IllegalArgumentException(var6);
      }
   }

   public static CodedInputStream newInstance(ByteBuffer var0) {
      return newInstance(var0, false);
   }

   static CodedInputStream newInstance(ByteBuffer var0, boolean var1) {
      if (var0.hasArray()) {
         return newInstance(var0.array(), var0.arrayOffset() + var0.position(), var0.remaining(), var1);
      } else if (var0.isDirect() && CodedInputStream.UnsafeDirectNioDecoder.isSupported()) {
         return new CodedInputStream.UnsafeDirectNioDecoder(var0, var1);
      } else {
         byte[] var2 = new byte[var0.remaining()];
         var0.duplicate().get(var2);
         return newInstance(var2, 0, var2.length, true);
      }
   }

   private CodedInputStream() {
   }

   public abstract int readTag() throws IOException;

   public abstract void checkLastTagWas(int var1) throws InvalidProtocolBufferException;

   public abstract int getLastTag();

   public abstract boolean skipField(int var1) throws IOException;

   @Deprecated
   public abstract boolean skipField(int var1, CodedOutputStream var2) throws IOException;

   public abstract void skipMessage() throws IOException;

   public abstract void skipMessage(CodedOutputStream var1) throws IOException;

   public abstract double readDouble() throws IOException;

   public abstract float readFloat() throws IOException;

   public abstract long readUInt64() throws IOException;

   public abstract long readInt64() throws IOException;

   public abstract int readInt32() throws IOException;

   public abstract long readFixed64() throws IOException;

   public abstract int readFixed32() throws IOException;

   public abstract boolean readBool() throws IOException;

   public abstract String readString() throws IOException;

   public abstract String readStringRequireUtf8() throws IOException;

   public abstract void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException;

   public abstract <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException;

   @Deprecated
   public abstract void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException;

   public abstract void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException;

   public abstract <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException;

   public abstract ByteString readBytes() throws IOException;

   public abstract byte[] readByteArray() throws IOException;

   public abstract ByteBuffer readByteBuffer() throws IOException;

   public abstract int readUInt32() throws IOException;

   public abstract int readEnum() throws IOException;

   public abstract int readSFixed32() throws IOException;

   public abstract long readSFixed64() throws IOException;

   public abstract int readSInt32() throws IOException;

   public abstract long readSInt64() throws IOException;

   public abstract int readRawVarint32() throws IOException;

   public abstract long readRawVarint64() throws IOException;

   abstract long readRawVarint64SlowPath() throws IOException;

   public abstract int readRawLittleEndian32() throws IOException;

   public abstract long readRawLittleEndian64() throws IOException;

   public abstract void enableAliasing(boolean var1);

   public final int setRecursionLimit(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Recursion limit cannot be negative: " + var1);
      } else {
         int var2 = this.recursionLimit;
         this.recursionLimit = var1;
         return var2;
      }
   }

   public final int setSizeLimit(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Size limit cannot be negative: " + var1);
      } else {
         int var2 = this.sizeLimit;
         this.sizeLimit = var1;
         return var2;
      }
   }

   final void discardUnknownFields() {
      this.shouldDiscardUnknownFields = true;
   }

   final void unsetDiscardUnknownFields() {
      this.shouldDiscardUnknownFields = false;
   }

   final boolean shouldDiscardUnknownFields() {
      return this.shouldDiscardUnknownFields;
   }

   public abstract void resetSizeCounter();

   public abstract int pushLimit(int var1) throws InvalidProtocolBufferException;

   public abstract void popLimit(int var1);

   public abstract int getBytesUntilLimit();

   public abstract boolean isAtEnd() throws IOException;

   public abstract int getTotalBytesRead();

   public abstract byte readRawByte() throws IOException;

   public abstract byte[] readRawBytes(int var1) throws IOException;

   public abstract void skipRawBytes(int var1) throws IOException;

   public static int decodeZigZag32(int var0) {
      return var0 >>> 1 ^ -(var0 & 1);
   }

   public static long decodeZigZag64(long var0) {
      return var0 >>> 1 ^ -(var0 & 1L);
   }

   public static int readRawVarint32(int var0, InputStream var1) throws IOException {
      if ((var0 & 128) == 0) {
         return var0;
      } else {
         int var2 = var0 & 127;

         byte var3;
         for (var3 = 7; var3 < 32; var3 += 7) {
            int var4 = var1.read();
            if (var4 == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            var2 |= (var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var2;
            }
         }

         while (var3 < 64) {
            int var5 = var1.read();
            if (var5 == -1) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            if ((var5 & 128) == 0) {
               return var2;
            }

            var3 += 7;
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }
   }

   static int readRawVarint32(InputStream var0) throws IOException {
      int var1 = var0.read();
      if (var1 == -1) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return readRawVarint32(var1, var0);
      }
   }

   private static final class ArrayDecoder extends CodedInputStream {
      private final byte[] buffer;
      private final boolean immutable;
      private int limit;
      private int bufferSizeAfterLimit;
      private int pos;
      private int startPos;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit = Integer.MAX_VALUE;

      private ArrayDecoder(byte[] var1, int var2, int var3, boolean var4) {
         this.buffer = var1;
         this.limit = var2 + var3;
         this.pos = var2;
         this.startPos = this.pos;
         this.immutable = var4;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(int var1) throws InvalidProtocolBufferException {
         if (this.lastTag != var1) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(int var1) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4));
               return true;
            case 4:
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               long var8 = this.readInt64();
               var2.writeRawVarint32(var1);
               var2.writeUInt64NoTag(var8);
               return true;
            case 1:
               long var7 = this.readRawLittleEndian64();
               var2.writeRawVarint32(var1);
               var2.writeFixed64NoTag(var7);
               return true;
            case 2:
               ByteString var6 = this.readBytes();
               var2.writeRawVarint32(var1);
               var2.writeBytesNoTag(var6);
               return true;
            case 3:
               var2.writeRawVarint32(var1);
               this.skipMessage(var2);
               int var5 = WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4);
               this.checkLastTagWas(var5);
               var2.writeRawVarint32(var5);
               return true;
            case 4:
               return false;
            case 5:
               int var3 = this.readRawLittleEndian32();
               var2.writeRawVarint32(var1);
               var2.writeFixed32NoTag(var3);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public void skipMessage() throws IOException {
         int var1;
         do {
            var1 = this.readTag();
         } while (var1 != 0 && this.skipField(var1));
      }

      @Override
      public void skipMessage(CodedOutputStream var1) throws IOException {
         int var2;
         do {
            var2 = this.readTag();
         } while (var2 != 0 && this.skipField(var2, var1));
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.limit - this.pos) {
            String var2 = new String(this.buffer, this.pos, var1, Internal.UTF_8);
            this.pos += var1;
            return var2;
         } else if (var1 == 0) {
            return "";
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.limit - this.pos) {
            String var2 = Utf8.decodeUtf8(this.buffer, this.pos, var1);
            this.pos += var1;
            return var2;
         } else if (var1 == 0) {
            return "";
         } else if (var1 <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            var2.mergeFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
         }
      }

      @Override
      public <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            MessageLite var4 = (MessageLite)var2.parsePartialFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
            return (T)var4;
         }
      }

      @Deprecated
      @Override
      public void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException {
         this.readGroup(var1, var2, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            var1.mergeFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            MessageLite var5 = (MessageLite)var1.parsePartialFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
            return (T)var5;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.limit - this.pos) {
            ByteString var2 = this.immutable && this.enableAliasing
               ? ByteString.wrap(this.buffer, this.pos, var1)
               : ByteString.copyFrom(this.buffer, this.pos, var1);
            this.pos += var1;
            return var2;
         } else {
            return var1 == 0 ? ByteString.EMPTY : ByteString.wrap(this.readRawBytes(var1));
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         int var1 = this.readRawVarint32();
         return this.readRawBytes(var1);
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.limit - this.pos) {
            ByteBuffer var2 = !this.immutable && this.enableAliasing
               ? ByteBuffer.wrap(this.buffer, this.pos, var1).slice()
               : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + var1));
            this.pos += var1;
            return var2;
         } else if (var1 == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         int var1;
         int var11;
         label47: {
            var1 = this.pos;
            if (this.limit != var1) {
               byte[] var2 = this.buffer;
               byte var3;
               if ((var3 = var2[var1++]) >= 0) {
                  this.pos = var1;
                  return var3;
               }

               if (this.limit - var1 >= 9) {
                  if ((var3 = var3 ^ var2[var1++] << 7) < 0) {
                     var11 = var3 ^ -128;
                     break label47;
                  }

                  if ((var3 = var3 ^ var2[var1++] << 14) >= 0) {
                     var11 = var3 ^ 16256;
                     break label47;
                  }

                  if ((var3 = var3 ^ var2[var1++] << 21) < 0) {
                     var11 = var3 ^ -2080896;
                     break label47;
                  }

                  byte var4 = var2[var1++];
                  var3 ^= var4 << 28;
                  var11 = var3 ^ 266354560;
                  if (var4 >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var11;
      }

      private void skipRawVarint() throws IOException {
         if (this.limit - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         int var1;
         long var12;
         label51: {
            var1 = this.pos;
            if (this.limit != var1) {
               byte[] var2 = this.buffer;
               byte var5;
               if ((var5 = var2[var1++]) >= 0) {
                  this.pos = var1;
                  return (long)var5;
               }

               if (this.limit - var1 >= 9) {
                  if ((var5 = var5 ^ var2[var1++] << 7) < 0) {
                     var12 = (long)(var5 ^ -128);
                     break label51;
                  }

                  if ((var5 = var5 ^ var2[var1++] << 14) >= 0) {
                     var12 = (long)(var5 ^ 16256);
                     break label51;
                  }

                  if ((var5 = var5 ^ var2[var1++] << 21) < 0) {
                     var12 = (long)(var5 ^ -2080896);
                     break label51;
                  }

                  if ((var12 = (long)var5 ^ (long)var2[var1++] << 28) >= 0L) {
                     var12 ^= 266354560L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 35) < 0L) {
                     var12 ^= -34093383808L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 42) >= 0L) {
                     var12 ^= 4363953127296L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 49) < 0L) {
                     var12 ^= -558586000294016L;
                     break label51;
                  }

                  var12 ^= (long)var2[var1++] << 56;
                  var12 ^= 71499008037633920L;
                  if (var12 >= 0L || (long)var2[var1++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var12;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long var1 = 0L;

         for (byte var3 = 0; var3 < 64; var3 += 7) {
            byte var4 = this.readRawByte();
            var1 |= (long)(var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var1;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         int var1 = this.pos;
         if (this.limit - var1 < 4) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] var2 = this.buffer;
            this.pos = var1 + 4;
            return var2[var1] & 0xFF | (var2[var1 + 1] & 0xFF) << 8 | (var2[var1 + 2] & 0xFF) << 16 | (var2[var1 + 3] & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         int var1 = this.pos;
         if (this.limit - var1 < 8) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] var2 = this.buffer;
            this.pos = var1 + 8;
            return (long)var2[var1] & 255L
               | ((long)var2[var1 + 1] & 255L) << 8
               | ((long)var2[var1 + 2] & 255L) << 16
               | ((long)var2[var1 + 3] & 255L) << 24
               | ((long)var2[var1 + 4] & 255L) << 32
               | ((long)var2[var1 + 5] & 255L) << 40
               | ((long)var2[var1 + 6] & 255L) << 48
               | ((long)var2[var1 + 7] & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean var1) {
         this.enableAliasing = var1;
      }

      @Override
      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      @Override
      public int pushLimit(int var1) throws InvalidProtocolBufferException {
         if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            var1 += this.getTotalBytesRead();
            int var2 = this.currentLimit;
            if (var1 > var2) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = var1;
               this.recomputeBufferSizeAfterLimit();
               return var2;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit = this.limit + this.bufferSizeAfterLimit;
         int var1 = this.limit - this.startPos;
         if (var1 > this.currentLimit) {
            this.bufferSizeAfterLimit = var1 - this.currentLimit;
            this.limit = this.limit - this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      @Override
      public void popLimit(int var1) {
         this.currentLimit = var1;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      @Override
      public int getTotalBytesRead() {
         return this.pos - this.startPos;
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return this.buffer[this.pos++];
         }
      }

      @Override
      public byte[] readRawBytes(int var1) throws IOException {
         if (var1 > 0 && var1 <= this.limit - this.pos) {
            int var2 = this.pos;
            this.pos += var1;
            return Arrays.copyOfRange(this.buffer, var2, this.pos);
         } else if (var1 <= 0) {
            if (var1 == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(int var1) throws IOException {
         if (var1 >= 0 && var1 <= this.limit - this.pos) {
            this.pos += var1;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }
   }

   private static final class IterableDirectByteBufferDecoder extends CodedInputStream {
      private Iterable<ByteBuffer> input;
      private Iterator<ByteBuffer> iterator;
      private ByteBuffer currentByteBuffer;
      private boolean immutable;
      private boolean enableAliasing;
      private int totalBufferSize;
      private int bufferSizeAfterCurrentLimit;
      private int currentLimit = Integer.MAX_VALUE;
      private int lastTag;
      private int totalBytesRead;
      private int startOffset;
      private long currentByteBufferPos;
      private long currentByteBufferStartPos;
      private long currentAddress;
      private long currentByteBufferLimit;

      private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> var1, int var2, boolean var3) {
         this.totalBufferSize = var2;
         this.input = var1;
         this.iterator = this.input.iterator();
         this.immutable = var3;
         this.startOffset = this.totalBytesRead = 0;
         if (var2 == 0) {
            this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
            this.currentByteBufferPos = 0L;
            this.currentByteBufferStartPos = 0L;
            this.currentByteBufferLimit = 0L;
            this.currentAddress = 0L;
         } else {
            this.tryGetNextByteBuffer();
         }
      }

      private void getNextByteBuffer() throws InvalidProtocolBufferException {
         if (!this.iterator.hasNext()) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.tryGetNextByteBuffer();
         }
      }

      private void tryGetNextByteBuffer() {
         this.currentByteBuffer = this.iterator.next();
         this.totalBytesRead = this.totalBytesRead + (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
         this.currentByteBufferPos = (long)this.currentByteBuffer.position();
         this.currentByteBufferStartPos = this.currentByteBufferPos;
         this.currentByteBufferLimit = (long)this.currentByteBuffer.limit();
         this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
         this.currentByteBufferPos = this.currentByteBufferPos + this.currentAddress;
         this.currentByteBufferStartPos = this.currentByteBufferStartPos + this.currentAddress;
         this.currentByteBufferLimit = this.currentByteBufferLimit + this.currentAddress;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(int var1) throws InvalidProtocolBufferException {
         if (this.lastTag != var1) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(int var1) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4));
               return true;
            case 4:
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               long var8 = this.readInt64();
               var2.writeRawVarint32(var1);
               var2.writeUInt64NoTag(var8);
               return true;
            case 1:
               long var7 = this.readRawLittleEndian64();
               var2.writeRawVarint32(var1);
               var2.writeFixed64NoTag(var7);
               return true;
            case 2:
               ByteString var6 = this.readBytes();
               var2.writeRawVarint32(var1);
               var2.writeBytesNoTag(var6);
               return true;
            case 3:
               var2.writeRawVarint32(var1);
               this.skipMessage(var2);
               int var5 = WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4);
               this.checkLastTagWas(var5);
               var2.writeRawVarint32(var5);
               return true;
            case 4:
               return false;
            case 5:
               int var3 = this.readRawLittleEndian32();
               var2.writeRawVarint32(var1);
               var2.writeFixed32NoTag(var3);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public void skipMessage() throws IOException {
         int var1;
         do {
            var1 = this.readTag();
         } while (var1 != 0 && this.skipField(var1));
      }

      @Override
      public void skipMessage(CodedOutputStream var1) throws IOException {
         int var2;
         do {
            var2 = this.readTag();
         } while (var2 != 0 && this.skipField(var2, var1));
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && (long)var1 <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            byte[] var4 = new byte[var1];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, var4, 0L, (long)var1);
            String var3 = new String(var4, Internal.UTF_8);
            this.currentByteBufferPos += (long)var1;
            return var3;
         } else if (var1 > 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.readRawBytesTo(var2, 0, var1);
            return new String(var2, Internal.UTF_8);
         } else if (var1 == 0) {
            return "";
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && (long)var1 <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            int var4 = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
            String var3 = Utf8.decodeUtf8(this.currentByteBuffer, var4, var1);
            this.currentByteBufferPos += (long)var1;
            return var3;
         } else if (var1 >= 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.readRawBytesTo(var2, 0, var1);
            return Utf8.decodeUtf8(var2, 0, var1);
         } else if (var1 == 0) {
            return "";
         } else if (var1 <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            var2.mergeFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
         }
      }

      @Override
      public <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            MessageLite var4 = (MessageLite)var2.parsePartialFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
            return (T)var4;
         }
      }

      @Deprecated
      @Override
      public void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException {
         this.readGroup(var1, var2, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            var1.mergeFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            MessageLite var5 = (MessageLite)var1.parsePartialFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
            return (T)var5;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && (long)var1 <= this.currentByteBufferLimit - this.currentByteBufferPos) {
            if (this.immutable && this.enableAliasing) {
               int var5 = (int)(this.currentByteBufferPos - this.currentAddress);
               ByteString var3 = ByteString.wrap(this.slice(var5, var5 + var1));
               this.currentByteBufferPos += (long)var1;
               return var3;
            } else {
               byte[] var4 = new byte[var1];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, var4, 0L, (long)var1);
               this.currentByteBufferPos += (long)var1;
               return ByteString.wrap(var4);
            }
         } else if (var1 > 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.readRawBytesTo(var2, 0, var1);
            return ByteString.wrap(var2);
         } else if (var1 == 0) {
            return ByteString.EMPTY;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && (long)var1 <= this.currentRemaining()) {
            if (!this.immutable && this.enableAliasing) {
               this.currentByteBufferPos += (long)var1;
               return this.slice((int)(this.currentByteBufferPos - this.currentAddress - (long)var1), (int)(this.currentByteBufferPos - this.currentAddress));
            } else {
               byte[] var3 = new byte[var1];
               UnsafeUtil.copyMemory(this.currentByteBufferPos, var3, 0L, (long)var1);
               this.currentByteBufferPos += (long)var1;
               return ByteBuffer.wrap(var3);
            }
         } else if (var1 > 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.readRawBytesTo(var2, 0, var1);
            return ByteBuffer.wrap(var2);
         } else if (var1 == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         long var1;
         int var11;
         label47: {
            var1 = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               byte var3;
               if ((var3 = UnsafeUtil.getByte(var1++)) >= 0) {
                  this.currentByteBufferPos++;
                  return var3;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 7) < 0) {
                     var11 = var3 ^ -128;
                     break label47;
                  }

                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 14) >= 0) {
                     var11 = var3 ^ 16256;
                     break label47;
                  }

                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 21) < 0) {
                     var11 = var3 ^ -2080896;
                     break label47;
                  }

                  byte var4 = UnsafeUtil.getByte(var1++);
                  var3 ^= var4 << 28;
                  var11 = var3 ^ 266354560;
                  if (var4 >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = var1;
         return var11;
      }

      @Override
      public long readRawVarint64() throws IOException {
         long var1;
         long var12;
         label51: {
            var1 = this.currentByteBufferPos;
            if (this.currentByteBufferLimit != this.currentByteBufferPos) {
               byte var5;
               if ((var5 = UnsafeUtil.getByte(var1++)) >= 0) {
                  this.currentByteBufferPos++;
                  return (long)var5;
               }

               if (this.currentByteBufferLimit - this.currentByteBufferPos >= 10L) {
                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 7) < 0) {
                     var12 = (long)(var5 ^ -128);
                     break label51;
                  }

                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 14) >= 0) {
                     var12 = (long)(var5 ^ 16256);
                     break label51;
                  }

                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 21) < 0) {
                     var12 = (long)(var5 ^ -2080896);
                     break label51;
                  }

                  if ((var12 = (long)var5 ^ (long)UnsafeUtil.getByte(var1++) << 28) >= 0L) {
                     var12 ^= 266354560L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 35) < 0L) {
                     var12 ^= -34093383808L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 42) >= 0L) {
                     var12 ^= 4363953127296L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 49) < 0L) {
                     var12 ^= -558586000294016L;
                     break label51;
                  }

                  var12 ^= (long)UnsafeUtil.getByte(var1++) << 56;
                  var12 ^= 71499008037633920L;
                  if (var12 >= 0L || (long)UnsafeUtil.getByte(var1++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.currentByteBufferPos = var1;
         return var12;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long var1 = 0L;

         for (byte var3 = 0; var3 < 64; var3 += 7) {
            byte var4 = this.readRawByte();
            var1 |= (long)(var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var1;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         if (this.currentRemaining() >= 4L) {
            long var1 = this.currentByteBufferPos;
            this.currentByteBufferPos += 4L;
            return UnsafeUtil.getByte(var1) & 0xFF
               | (UnsafeUtil.getByte(var1 + 1L) & 0xFF) << 8
               | (UnsafeUtil.getByte(var1 + 2L) & 0xFF) << 16
               | (UnsafeUtil.getByte(var1 + 3L) & 0xFF) << 24;
         } else {
            return this.readRawByte() & 0xFF | (this.readRawByte() & 0xFF) << 8 | (this.readRawByte() & 0xFF) << 16 | (this.readRawByte() & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         if (this.currentRemaining() >= 8L) {
            long var1 = this.currentByteBufferPos;
            this.currentByteBufferPos += 8L;
            return (long)UnsafeUtil.getByte(var1) & 255L
               | ((long)UnsafeUtil.getByte(var1 + 1L) & 255L) << 8
               | ((long)UnsafeUtil.getByte(var1 + 2L) & 255L) << 16
               | ((long)UnsafeUtil.getByte(var1 + 3L) & 255L) << 24
               | ((long)UnsafeUtil.getByte(var1 + 4L) & 255L) << 32
               | ((long)UnsafeUtil.getByte(var1 + 5L) & 255L) << 40
               | ((long)UnsafeUtil.getByte(var1 + 6L) & 255L) << 48
               | ((long)UnsafeUtil.getByte(var1 + 7L) & 255L) << 56;
         } else {
            return (long)this.readRawByte() & 255L
               | ((long)this.readRawByte() & 255L) << 8
               | ((long)this.readRawByte() & 255L) << 16
               | ((long)this.readRawByte() & 255L) << 24
               | ((long)this.readRawByte() & 255L) << 32
               | ((long)this.readRawByte() & 255L) << 40
               | ((long)this.readRawByte() & 255L) << 48
               | ((long)this.readRawByte() & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean var1) {
         this.enableAliasing = var1;
      }

      @Override
      public void resetSizeCounter() {
         this.startOffset = (int)((long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      @Override
      public int pushLimit(int var1) throws InvalidProtocolBufferException {
         if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            var1 += this.getTotalBytesRead();
            int var2 = this.currentLimit;
            if (var1 > var2) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = var1;
               this.recomputeBufferSizeAfterLimit();
               return var2;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.totalBufferSize = this.totalBufferSize + this.bufferSizeAfterCurrentLimit;
         int var1 = this.totalBufferSize - this.startOffset;
         if (var1 > this.currentLimit) {
            this.bufferSizeAfterCurrentLimit = var1 - this.currentLimit;
            this.totalBufferSize = this.totalBufferSize - this.bufferSizeAfterCurrentLimit;
         } else {
            this.bufferSizeAfterCurrentLimit = 0;
         }
      }

      @Override
      public void popLimit(int var1) {
         this.currentLimit = var1;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return (long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == (long)this.totalBufferSize;
      }

      @Override
      public int getTotalBytesRead() {
         return (int)((long)(this.totalBytesRead - this.startOffset) + this.currentByteBufferPos - this.currentByteBufferStartPos);
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.currentRemaining() == 0L) {
            this.getNextByteBuffer();
         }

         return UnsafeUtil.getByte(this.currentByteBufferPos++);
      }

      @Override
      public byte[] readRawBytes(int var1) throws IOException {
         if (var1 >= 0 && (long)var1 <= this.currentRemaining()) {
            byte[] var3 = new byte[var1];
            UnsafeUtil.copyMemory(this.currentByteBufferPos, var3, 0L, (long)var1);
            this.currentByteBufferPos += (long)var1;
            return var3;
         } else if (var1 >= 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.readRawBytesTo(var2, 0, var1);
            return var2;
         } else if (var1 <= 0) {
            if (var1 == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void readRawBytesTo(byte[] var1, int var2, int var3) throws IOException {
         if (var3 >= 0 && var3 <= this.remaining()) {
            int var4 = var3;

            while (var4 > 0) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               int var5 = Math.min(var4, (int)this.currentRemaining());
               UnsafeUtil.copyMemory(this.currentByteBufferPos, var1, (long)(var3 - var4 + var2), (long)var5);
               var4 -= var5;
               this.currentByteBufferPos += (long)var5;
            }
         } else if (var3 <= 0) {
            if (var3 != 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(int var1) throws IOException {
         if (var1 >= 0 && (long)var1 <= (long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos) {
            int var2 = var1;

            while (var2 > 0) {
               if (this.currentRemaining() == 0L) {
                  this.getNextByteBuffer();
               }

               int var3 = Math.min(var2, (int)this.currentRemaining());
               var2 -= var3;
               this.currentByteBufferPos += (long)var3;
            }
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void skipRawVarint() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private int remaining() {
         return (int)((long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos);
      }

      private long currentRemaining() {
         return this.currentByteBufferLimit - this.currentByteBufferPos;
      }

      private ByteBuffer slice(int var1, int var2) throws IOException {
         int var3 = this.currentByteBuffer.position();
         int var4 = this.currentByteBuffer.limit();

         ByteBuffer var5;
         try {
            ((Buffer)this.currentByteBuffer).position(var1);
            ((Buffer)this.currentByteBuffer).limit(var2);
            var5 = this.currentByteBuffer.slice();
         } catch (IllegalArgumentException var9) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } finally {
            ((Buffer)this.currentByteBuffer).position(var3);
            ((Buffer)this.currentByteBuffer).limit(var4);
         }

         return var5;
      }
   }

   private static final class StreamDecoder extends CodedInputStream {
      private final InputStream input;
      private final byte[] buffer;
      private int bufferSize;
      private int bufferSizeAfterLimit;
      private int pos;
      private int lastTag;
      private int totalBytesRetired;
      private int currentLimit = Integer.MAX_VALUE;
      private CodedInputStream.StreamDecoder.RefillCallback refillCallback = null;

      private StreamDecoder(InputStream var1, int var2) {
         Internal.checkNotNull(var1, "input");
         this.input = var1;
         this.buffer = new byte[var2];
         this.bufferSize = 0;
         this.pos = 0;
         this.totalBytesRetired = 0;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(int var1) throws InvalidProtocolBufferException {
         if (this.lastTag != var1) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(int var1) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4));
               return true;
            case 4:
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               long var8 = this.readInt64();
               var2.writeRawVarint32(var1);
               var2.writeUInt64NoTag(var8);
               return true;
            case 1:
               long var7 = this.readRawLittleEndian64();
               var2.writeRawVarint32(var1);
               var2.writeFixed64NoTag(var7);
               return true;
            case 2:
               ByteString var6 = this.readBytes();
               var2.writeRawVarint32(var1);
               var2.writeBytesNoTag(var6);
               return true;
            case 3:
               var2.writeRawVarint32(var1);
               this.skipMessage(var2);
               int var5 = WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4);
               this.checkLastTagWas(var5);
               var2.writeRawVarint32(var5);
               return true;
            case 4:
               return false;
            case 5:
               int var3 = this.readRawLittleEndian32();
               var2.writeRawVarint32(var1);
               var2.writeFixed32NoTag(var3);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public void skipMessage() throws IOException {
         int var1;
         do {
            var1 = this.readTag();
         } while (var1 != 0 && this.skipField(var1));
      }

      @Override
      public void skipMessage(CodedOutputStream var1) throws IOException {
         int var2;
         do {
            var2 = this.readTag();
         } while (var2 != 0 && this.skipField(var2, var1));
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.bufferSize - this.pos) {
            String var3 = new String(this.buffer, this.pos, var1, Internal.UTF_8);
            this.pos += var1;
            return var3;
         } else if (var1 == 0) {
            return "";
         } else if (var1 <= this.bufferSize) {
            this.refillBuffer(var1);
            String var2 = new String(this.buffer, this.pos, var1, Internal.UTF_8);
            this.pos += var1;
            return var2;
         } else {
            return new String(this.readRawBytesSlowPath(var1, false), Internal.UTF_8);
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int var1 = this.readRawVarint32();
         int var3 = this.pos;
         byte[] var2;
         int var4;
         if (var1 <= this.bufferSize - var3 && var1 > 0) {
            var2 = this.buffer;
            this.pos = var3 + var1;
            var4 = var3;
         } else {
            if (var1 == 0) {
               return "";
            }

            if (var1 <= this.bufferSize) {
               this.refillBuffer(var1);
               var2 = this.buffer;
               var4 = 0;
               this.pos = var4 + var1;
            } else {
               var2 = this.readRawBytesSlowPath(var1, false);
               var4 = 0;
            }
         }

         return Utf8.decodeUtf8(var2, var4, var1);
      }

      @Override
      public void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            var2.mergeFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
         }
      }

      @Override
      public <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            MessageLite var4 = (MessageLite)var2.parsePartialFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
            return (T)var4;
         }
      }

      @Deprecated
      @Override
      public void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException {
         this.readGroup(var1, var2, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            var1.mergeFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            MessageLite var5 = (MessageLite)var1.parsePartialFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
            return (T)var5;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 <= this.bufferSize - this.pos && var1 > 0) {
            ByteString var2 = ByteString.copyFrom(this.buffer, this.pos, var1);
            this.pos += var1;
            return var2;
         } else {
            return var1 == 0 ? ByteString.EMPTY : this.readBytesSlowPath(var1);
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 <= this.bufferSize - this.pos && var1 > 0) {
            byte[] var2 = Arrays.copyOfRange(this.buffer, this.pos, this.pos + var1);
            this.pos += var1;
            return var2;
         } else {
            return this.readRawBytesSlowPath(var1, false);
         }
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 <= this.bufferSize - this.pos && var1 > 0) {
            ByteBuffer var2 = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + var1));
            this.pos += var1;
            return var2;
         } else {
            return var1 == 0 ? Internal.EMPTY_BYTE_BUFFER : ByteBuffer.wrap(this.readRawBytesSlowPath(var1, true));
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         int var1;
         int var11;
         label47: {
            var1 = this.pos;
            if (this.bufferSize != var1) {
               byte[] var2 = this.buffer;
               byte var3;
               if ((var3 = var2[var1++]) >= 0) {
                  this.pos = var1;
                  return var3;
               }

               if (this.bufferSize - var1 >= 9) {
                  if ((var3 = var3 ^ var2[var1++] << 7) < 0) {
                     var11 = var3 ^ -128;
                     break label47;
                  }

                  if ((var3 = var3 ^ var2[var1++] << 14) >= 0) {
                     var11 = var3 ^ 16256;
                     break label47;
                  }

                  if ((var3 = var3 ^ var2[var1++] << 21) < 0) {
                     var11 = var3 ^ -2080896;
                     break label47;
                  }

                  byte var4 = var2[var1++];
                  var3 ^= var4 << 28;
                  var11 = var3 ^ 266354560;
                  if (var4 >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0 || var2[var1++] >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var11;
      }

      private void skipRawVarint() throws IOException {
         if (this.bufferSize - this.pos >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.buffer[this.pos++] >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         int var1;
         long var12;
         label51: {
            var1 = this.pos;
            if (this.bufferSize != var1) {
               byte[] var2 = this.buffer;
               byte var5;
               if ((var5 = var2[var1++]) >= 0) {
                  this.pos = var1;
                  return (long)var5;
               }

               if (this.bufferSize - var1 >= 9) {
                  if ((var5 = var5 ^ var2[var1++] << 7) < 0) {
                     var12 = (long)(var5 ^ -128);
                     break label51;
                  }

                  if ((var5 = var5 ^ var2[var1++] << 14) >= 0) {
                     var12 = (long)(var5 ^ 16256);
                     break label51;
                  }

                  if ((var5 = var5 ^ var2[var1++] << 21) < 0) {
                     var12 = (long)(var5 ^ -2080896);
                     break label51;
                  }

                  if ((var12 = (long)var5 ^ (long)var2[var1++] << 28) >= 0L) {
                     var12 ^= 266354560L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 35) < 0L) {
                     var12 ^= -34093383808L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 42) >= 0L) {
                     var12 ^= 4363953127296L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)var2[var1++] << 49) < 0L) {
                     var12 ^= -558586000294016L;
                     break label51;
                  }

                  var12 ^= (long)var2[var1++] << 56;
                  var12 ^= 71499008037633920L;
                  if (var12 >= 0L || (long)var2[var1++] >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var12;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long var1 = 0L;

         for (byte var3 = 0; var3 < 64; var3 += 7) {
            byte var4 = this.readRawByte();
            var1 |= (long)(var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var1;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         int var1 = this.pos;
         if (this.bufferSize - var1 < 4) {
            this.refillBuffer(4);
            var1 = this.pos;
         }

         byte[] var2 = this.buffer;
         this.pos = var1 + 4;
         return var2[var1] & 0xFF | (var2[var1 + 1] & 0xFF) << 8 | (var2[var1 + 2] & 0xFF) << 16 | (var2[var1 + 3] & 0xFF) << 24;
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         int var1 = this.pos;
         if (this.bufferSize - var1 < 8) {
            this.refillBuffer(8);
            var1 = this.pos;
         }

         byte[] var2 = this.buffer;
         this.pos = var1 + 8;
         return (long)var2[var1] & 255L
            | ((long)var2[var1 + 1] & 255L) << 8
            | ((long)var2[var1 + 2] & 255L) << 16
            | ((long)var2[var1 + 3] & 255L) << 24
            | ((long)var2[var1 + 4] & 255L) << 32
            | ((long)var2[var1 + 5] & 255L) << 40
            | ((long)var2[var1 + 6] & 255L) << 48
            | ((long)var2[var1 + 7] & 255L) << 56;
      }

      @Override
      public void enableAliasing(boolean var1) {
      }

      @Override
      public void resetSizeCounter() {
         this.totalBytesRetired = -this.pos;
      }

      @Override
      public int pushLimit(int var1) throws InvalidProtocolBufferException {
         if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            var1 += this.totalBytesRetired + this.pos;
            int var2 = this.currentLimit;
            if (var1 > var2) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = var1;
               this.recomputeBufferSizeAfterLimit();
               return var2;
            }
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.bufferSize = this.bufferSize + this.bufferSizeAfterLimit;
         int var1 = this.totalBytesRetired + this.bufferSize;
         if (var1 > this.currentLimit) {
            this.bufferSizeAfterLimit = var1 - this.currentLimit;
            this.bufferSize = this.bufferSize - this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      @Override
      public void popLimit(int var1) {
         this.currentLimit = var1;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         if (this.currentLimit == Integer.MAX_VALUE) {
            return -1;
         } else {
            int var1 = this.totalBytesRetired + this.pos;
            return this.currentLimit - var1;
         }
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.bufferSize && !this.tryRefillBuffer(1);
      }

      @Override
      public int getTotalBytesRead() {
         return this.totalBytesRetired + this.pos;
      }

      private void refillBuffer(int var1) throws IOException {
         if (!this.tryRefillBuffer(var1)) {
            if (var1 > this.sizeLimit - this.totalBytesRetired - this.pos) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else {
               throw InvalidProtocolBufferException.truncatedMessage();
            }
         }
      }

      private boolean tryRefillBuffer(int var1) throws IOException {
         if (this.pos + var1 <= this.bufferSize) {
            throw new IllegalStateException("refillBuffer() called when " + var1 + " bytes were already available in buffer");
         } else if (var1 > this.sizeLimit - this.totalBytesRetired - this.pos) {
            return false;
         } else if (this.totalBytesRetired + this.pos + var1 > this.currentLimit) {
            return false;
         } else {
            if (this.refillCallback != null) {
               this.refillCallback.onRefill();
            }

            int var2 = this.pos;
            if (var2 > 0) {
               if (this.bufferSize > var2) {
                  System.arraycopy(this.buffer, var2, this.buffer, 0, this.bufferSize - var2);
               }

               this.totalBytesRetired += var2;
               this.bufferSize -= var2;
               this.pos = 0;
            }

            int var3 = this.input
               .read(this.buffer, this.bufferSize, Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize));
            if (var3 == 0 || var3 < -1 || var3 > this.buffer.length) {
               throw new IllegalStateException(
                  this.input.getClass() + "#read(byte[]) returned invalid result: " + var3 + "\nThe InputStream implementation is buggy."
               );
            } else if (var3 > 0) {
               this.bufferSize += var3;
               this.recomputeBufferSizeAfterLimit();
               return this.bufferSize >= var1 ? true : this.tryRefillBuffer(var1);
            } else {
               return false;
            }
         }
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.bufferSize) {
            this.refillBuffer(1);
         }

         return this.buffer[this.pos++];
      }

      @Override
      public byte[] readRawBytes(int var1) throws IOException {
         int var2 = this.pos;
         if (var1 <= this.bufferSize - var2 && var1 > 0) {
            this.pos = var2 + var1;
            return Arrays.copyOfRange(this.buffer, var2, var2 + var1);
         } else {
            return this.readRawBytesSlowPath(var1, false);
         }
      }

      private byte[] readRawBytesSlowPath(int var1, boolean var2) throws IOException {
         byte[] var3 = this.readRawBytesSlowPathOneChunk(var1);
         if (var3 != null) {
            return var2 ? (byte[])var3.clone() : var3;
         } else {
            int var4 = this.pos;
            int var5 = this.bufferSize - this.pos;
            this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int var6 = var1 - var5;
            List var7 = this.readRawBytesSlowPathRemainingChunks(var6);
            byte[] var8 = new byte[var1];
            System.arraycopy(this.buffer, var4, var8, 0, var5);
            int var9 = var5;

            for (byte[] var11 : var7) {
               System.arraycopy(var11, 0, var8, var9, var11.length);
               var9 += var11.length;
            }

            return var8;
         }
      }

      private byte[] readRawBytesSlowPathOneChunk(int var1) throws IOException {
         if (var1 == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            int var2 = this.totalBytesRetired + this.pos + var1;
            if (var2 - this.sizeLimit > 0) {
               throw InvalidProtocolBufferException.sizeLimitExceeded();
            } else if (var2 > this.currentLimit) {
               this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               int var3 = this.bufferSize - this.pos;
               int var4 = var1 - var3;
               if (var4 >= 4096 && var4 > this.input.available()) {
                  return null;
               } else {
                  byte[] var5 = new byte[var1];
                  System.arraycopy(this.buffer, this.pos, var5, 0, var3);
                  this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
                  this.pos = 0;
                  this.bufferSize = 0;
                  int var6 = var3;

                  while (var6 < var5.length) {
                     int var7 = this.input.read(var5, var6, var1 - var6);
                     if (var7 == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                     }

                     this.totalBytesRetired += var7;
                     var6 += var7;
                  }

                  return var5;
               }
            }
         }
      }

      private List<byte[]> readRawBytesSlowPathRemainingChunks(int var1) throws IOException {
         ArrayList var2 = new ArrayList();

         while (var1 > 0) {
            byte[] var3 = new byte[Math.min(var1, 4096)];
            int var4 = 0;

            while (var4 < var3.length) {
               int var5 = this.input.read(var3, var4, var3.length - var4);
               if (var5 == -1) {
                  throw InvalidProtocolBufferException.truncatedMessage();
               }

               this.totalBytesRetired += var5;
               var4 += var5;
            }

            var1 -= var3.length;
            var2.add(var3);
         }

         return var2;
      }

      private ByteString readBytesSlowPath(int var1) throws IOException {
         byte[] var2 = this.readRawBytesSlowPathOneChunk(var1);
         if (var2 != null) {
            return ByteString.copyFrom(var2);
         } else {
            int var3 = this.pos;
            int var4 = this.bufferSize - this.pos;
            this.totalBytesRetired = this.totalBytesRetired + this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int var5 = var1 - var4;
            List var6 = this.readRawBytesSlowPathRemainingChunks(var5);
            byte[] var7 = new byte[var1];
            System.arraycopy(this.buffer, var3, var7, 0, var4);
            int var8 = var4;

            for (byte[] var10 : var6) {
               System.arraycopy(var10, 0, var7, var8, var10.length);
               var8 += var10.length;
            }

            return ByteString.wrap(var7);
         }
      }

      @Override
      public void skipRawBytes(int var1) throws IOException {
         if (var1 <= this.bufferSize - this.pos && var1 >= 0) {
            this.pos += var1;
         } else {
            this.skipRawBytesSlowPath(var1);
         }
      }

      private void skipRawBytesSlowPath(int var1) throws IOException {
         if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else if (this.totalBytesRetired + this.pos + var1 > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            int var2 = 0;
            if (this.refillCallback == null) {
               this.totalBytesRetired = this.totalBytesRetired + this.pos;
               var2 = this.bufferSize - this.pos;
               this.bufferSize = 0;
               this.pos = 0;

               try {
                  while (var2 < var1) {
                     int var3 = var1 - var2;
                     long var4 = this.input.skip((long)var3);
                     if (var4 < 0L || var4 > (long)var3) {
                        throw new IllegalStateException(
                           this.input.getClass() + "#skip returned invalid result: " + var4 + "\nThe InputStream implementation is buggy."
                        );
                     }

                     if (var4 == 0L) {
                        break;
                     }

                     var2 += (int)var4;
                  }
               } finally {
                  this.totalBytesRetired += var2;
                  this.recomputeBufferSizeAfterLimit();
               }
            }

            if (var2 < var1) {
               int var9 = this.bufferSize - this.pos;
               this.pos = this.bufferSize;
               this.refillBuffer(1);

               while (var1 - var9 > this.bufferSize) {
                  var9 += this.bufferSize;
                  this.pos = this.bufferSize;
                  this.refillBuffer(1);
               }

               this.pos = var1 - var9;
            }
         }
      }

      private interface RefillCallback {
         void onRefill();
      }

      private class SkippedDataSink implements CodedInputStream.StreamDecoder.RefillCallback {
         private int lastPos = StreamDecoder.this.pos;
         private ByteArrayOutputStream byteArrayStream;

         @Override
         public void onRefill() {
            if (this.byteArrayStream == null) {
               this.byteArrayStream = new ByteArrayOutputStream();
            }

            this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            this.lastPos = 0;
         }

         ByteBuffer getSkippedData() {
            if (this.byteArrayStream == null) {
               return ByteBuffer.wrap(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
            } else {
               this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos);
               return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
            }
         }
      }
   }

   private static final class UnsafeDirectNioDecoder extends CodedInputStream {
      private final ByteBuffer buffer;
      private final boolean immutable;
      private final long address;
      private long limit;
      private long pos;
      private long startPos;
      private int bufferSizeAfterLimit;
      private int lastTag;
      private boolean enableAliasing;
      private int currentLimit = Integer.MAX_VALUE;

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      private UnsafeDirectNioDecoder(ByteBuffer var1, boolean var2) {
         this.buffer = var1;
         this.address = UnsafeUtil.addressOffset(var1);
         this.limit = this.address + (long)var1.limit();
         this.pos = this.address + (long)var1.position();
         this.startPos = this.pos;
         this.immutable = var2;
      }

      @Override
      public int readTag() throws IOException {
         if (this.isAtEnd()) {
            this.lastTag = 0;
            return 0;
         } else {
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
               throw InvalidProtocolBufferException.invalidTag();
            } else {
               return this.lastTag;
            }
         }
      }

      @Override
      public void checkLastTagWas(int var1) throws InvalidProtocolBufferException {
         if (this.lastTag != var1) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      }

      @Override
      public int getLastTag() {
         return this.lastTag;
      }

      @Override
      public boolean skipField(int var1) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               this.skipRawVarint();
               return true;
            case 1:
               this.skipRawBytes(8);
               return true;
            case 2:
               this.skipRawBytes(this.readRawVarint32());
               return true;
            case 3:
               this.skipMessage();
               this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4));
               return true;
            case 4:
               return false;
            case 5:
               this.skipRawBytes(4);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public boolean skipField(int var1, CodedOutputStream var2) throws IOException {
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               long var8 = this.readInt64();
               var2.writeRawVarint32(var1);
               var2.writeUInt64NoTag(var8);
               return true;
            case 1:
               long var7 = this.readRawLittleEndian64();
               var2.writeRawVarint32(var1);
               var2.writeFixed64NoTag(var7);
               return true;
            case 2:
               ByteString var6 = this.readBytes();
               var2.writeRawVarint32(var1);
               var2.writeBytesNoTag(var6);
               return true;
            case 3:
               var2.writeRawVarint32(var1);
               this.skipMessage(var2);
               int var5 = WireFormat.makeTag(WireFormat.getTagFieldNumber(var1), 4);
               this.checkLastTagWas(var5);
               var2.writeRawVarint32(var5);
               return true;
            case 4:
               return false;
            case 5:
               int var3 = this.readRawLittleEndian32();
               var2.writeRawVarint32(var1);
               var2.writeFixed32NoTag(var3);
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      @Override
      public void skipMessage() throws IOException {
         int var1;
         do {
            var1 = this.readTag();
         } while (var1 != 0 && this.skipField(var1));
      }

      @Override
      public void skipMessage(CodedOutputStream var1) throws IOException {
         int var2;
         do {
            var2 = this.readTag();
         } while (var2 != 0 && this.skipField(var2, var1));
      }

      @Override
      public double readDouble() throws IOException {
         return Double.longBitsToDouble(this.readRawLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         return Float.intBitsToFloat(this.readRawLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         return this.readRawVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         return this.readRawVarint64() != 0L;
      }

      @Override
      public String readString() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            UnsafeUtil.copyMemory(this.pos, var2, 0L, (long)var1);
            String var3 = new String(var2, Internal.UTF_8);
            this.pos += (long)var1;
            return var3;
         } else if (var1 == 0) {
            return "";
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.remaining()) {
            int var2 = this.bufferPos(this.pos);
            String var3 = Utf8.decodeUtf8(this.buffer, var2, var1);
            this.pos += (long)var1;
            return var3;
         } else if (var1 == 0) {
            return "";
         } else if (var1 <= 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            var2.mergeFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
         }
      }

      @Override
      public <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            this.recursionDepth++;
            MessageLite var4 = (MessageLite)var2.parsePartialFrom(this, var3);
            this.checkLastTagWas(WireFormat.makeTag(var1, 4));
            this.recursionDepth--;
            return (T)var4;
         }
      }

      @Deprecated
      @Override
      public void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException {
         this.readGroup(var1, var2, ExtensionRegistryLite.getEmptyRegistry());
      }

      @Override
      public void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            var1.mergeFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
         }
      }

      @Override
      public <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readRawVarint32();
         if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
         } else {
            int var4 = this.pushLimit(var3);
            this.recursionDepth++;
            MessageLite var5 = (MessageLite)var1.parsePartialFrom(this, var2);
            this.checkLastTagWas(0);
            this.recursionDepth--;
            this.popLimit(var4);
            return (T)var5;
         }
      }

      @Override
      public ByteString readBytes() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.remaining()) {
            if (this.immutable && this.enableAliasing) {
               ByteBuffer var3 = this.slice(this.pos, this.pos + (long)var1);
               this.pos += (long)var1;
               return ByteString.wrap(var3);
            } else {
               byte[] var2 = new byte[var1];
               UnsafeUtil.copyMemory(this.pos, var2, 0L, (long)var1);
               this.pos += (long)var1;
               return ByteString.wrap(var2);
            }
         } else if (var1 == 0) {
            return ByteString.EMPTY;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public byte[] readByteArray() throws IOException {
         return this.readRawBytes(this.readRawVarint32());
      }

      @Override
      public ByteBuffer readByteBuffer() throws IOException {
         int var1 = this.readRawVarint32();
         if (var1 > 0 && var1 <= this.remaining()) {
            if (!this.immutable && this.enableAliasing) {
               ByteBuffer var3 = this.slice(this.pos, this.pos + (long)var1);
               this.pos += (long)var1;
               return var3;
            } else {
               byte[] var2 = new byte[var1];
               UnsafeUtil.copyMemory(this.pos, var2, 0L, (long)var1);
               this.pos += (long)var1;
               return ByteBuffer.wrap(var2);
            }
         } else if (var1 == 0) {
            return Internal.EMPTY_BYTE_BUFFER;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public int readUInt32() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         return this.readRawVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         return this.readRawLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         return this.readRawLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         return decodeZigZag32(this.readRawVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         return decodeZigZag64(this.readRawVarint64());
      }

      @Override
      public int readRawVarint32() throws IOException {
         long var1;
         int var11;
         label47: {
            var1 = this.pos;
            if (this.limit != var1) {
               byte var3;
               if ((var3 = UnsafeUtil.getByte(var1++)) >= 0) {
                  this.pos = var1;
                  return var3;
               }

               if (this.limit - var1 >= 9L) {
                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 7) < 0) {
                     var11 = var3 ^ -128;
                     break label47;
                  }

                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 14) >= 0) {
                     var11 = var3 ^ 16256;
                     break label47;
                  }

                  if ((var3 = var3 ^ UnsafeUtil.getByte(var1++) << 21) < 0) {
                     var11 = var3 ^ -2080896;
                     break label47;
                  }

                  byte var4 = UnsafeUtil.getByte(var1++);
                  var3 ^= var4 << 28;
                  var11 = var3 ^ 266354560;
                  if (var4 >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0
                     || UnsafeUtil.getByte(var1++) >= 0) {
                     break label47;
                  }
               }
            }

            return (int)this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var11;
      }

      private void skipRawVarint() throws IOException {
         if (this.remaining() >= 10) {
            this.skipRawVarintFastPath();
         } else {
            this.skipRawVarintSlowPath();
         }
      }

      private void skipRawVarintFastPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (UnsafeUtil.getByte(this.pos++) >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipRawVarintSlowPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.readRawByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public long readRawVarint64() throws IOException {
         long var1;
         long var12;
         label51: {
            var1 = this.pos;
            if (this.limit != var1) {
               byte var5;
               if ((var5 = UnsafeUtil.getByte(var1++)) >= 0) {
                  this.pos = var1;
                  return (long)var5;
               }

               if (this.limit - var1 >= 9L) {
                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 7) < 0) {
                     var12 = (long)(var5 ^ -128);
                     break label51;
                  }

                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 14) >= 0) {
                     var12 = (long)(var5 ^ 16256);
                     break label51;
                  }

                  if ((var5 = var5 ^ UnsafeUtil.getByte(var1++) << 21) < 0) {
                     var12 = (long)(var5 ^ -2080896);
                     break label51;
                  }

                  if ((var12 = (long)var5 ^ (long)UnsafeUtil.getByte(var1++) << 28) >= 0L) {
                     var12 ^= 266354560L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 35) < 0L) {
                     var12 ^= -34093383808L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 42) >= 0L) {
                     var12 ^= 4363953127296L;
                     break label51;
                  }

                  if ((var12 = var12 ^ (long)UnsafeUtil.getByte(var1++) << 49) < 0L) {
                     var12 ^= -558586000294016L;
                     break label51;
                  }

                  var12 ^= (long)UnsafeUtil.getByte(var1++) << 56;
                  var12 ^= 71499008037633920L;
                  if (var12 >= 0L || (long)UnsafeUtil.getByte(var1++) >= 0L) {
                     break label51;
                  }
               }
            }

            return this.readRawVarint64SlowPath();
         }

         this.pos = var1;
         return var12;
      }

      @Override
      long readRawVarint64SlowPath() throws IOException {
         long var1 = 0L;

         for (byte var3 = 0; var3 < 64; var3 += 7) {
            byte var4 = this.readRawByte();
            var1 |= (long)(var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var1;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      @Override
      public int readRawLittleEndian32() throws IOException {
         long var1 = this.pos;
         if (this.limit - var1 < 4L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = var1 + 4L;
            return UnsafeUtil.getByte(var1) & 0xFF
               | (UnsafeUtil.getByte(var1 + 1L) & 0xFF) << 8
               | (UnsafeUtil.getByte(var1 + 2L) & 0xFF) << 16
               | (UnsafeUtil.getByte(var1 + 3L) & 0xFF) << 24;
         }
      }

      @Override
      public long readRawLittleEndian64() throws IOException {
         long var1 = this.pos;
         if (this.limit - var1 < 8L) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            this.pos = var1 + 8L;
            return (long)UnsafeUtil.getByte(var1) & 255L
               | ((long)UnsafeUtil.getByte(var1 + 1L) & 255L) << 8
               | ((long)UnsafeUtil.getByte(var1 + 2L) & 255L) << 16
               | ((long)UnsafeUtil.getByte(var1 + 3L) & 255L) << 24
               | ((long)UnsafeUtil.getByte(var1 + 4L) & 255L) << 32
               | ((long)UnsafeUtil.getByte(var1 + 5L) & 255L) << 40
               | ((long)UnsafeUtil.getByte(var1 + 6L) & 255L) << 48
               | ((long)UnsafeUtil.getByte(var1 + 7L) & 255L) << 56;
         }
      }

      @Override
      public void enableAliasing(boolean var1) {
         this.enableAliasing = var1;
      }

      @Override
      public void resetSizeCounter() {
         this.startPos = this.pos;
      }

      @Override
      public int pushLimit(int var1) throws InvalidProtocolBufferException {
         if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            var1 += this.getTotalBytesRead();
            int var2 = this.currentLimit;
            if (var1 > var2) {
               throw InvalidProtocolBufferException.truncatedMessage();
            } else {
               this.currentLimit = var1;
               this.recomputeBufferSizeAfterLimit();
               return var2;
            }
         }
      }

      @Override
      public void popLimit(int var1) {
         this.currentLimit = var1;
         this.recomputeBufferSizeAfterLimit();
      }

      @Override
      public int getBytesUntilLimit() {
         return this.currentLimit == Integer.MAX_VALUE ? -1 : this.currentLimit - this.getTotalBytesRead();
      }

      @Override
      public boolean isAtEnd() throws IOException {
         return this.pos == this.limit;
      }

      @Override
      public int getTotalBytesRead() {
         return (int)(this.pos - this.startPos);
      }

      @Override
      public byte readRawByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return UnsafeUtil.getByte(this.pos++);
         }
      }

      @Override
      public byte[] readRawBytes(int var1) throws IOException {
         if (var1 >= 0 && var1 <= this.remaining()) {
            byte[] var2 = new byte[var1];
            this.slice(this.pos, this.pos + (long)var1).get(var2);
            this.pos += (long)var1;
            return var2;
         } else if (var1 <= 0) {
            if (var1 == 0) {
               return Internal.EMPTY_BYTE_ARRAY;
            } else {
               throw InvalidProtocolBufferException.negativeSize();
            }
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      @Override
      public void skipRawBytes(int var1) throws IOException {
         if (var1 >= 0 && var1 <= this.remaining()) {
            this.pos += (long)var1;
         } else if (var1 < 0) {
            throw InvalidProtocolBufferException.negativeSize();
         } else {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void recomputeBufferSizeAfterLimit() {
         this.limit = this.limit + (long)this.bufferSizeAfterLimit;
         int var1 = (int)(this.limit - this.startPos);
         if (var1 > this.currentLimit) {
            this.bufferSizeAfterLimit = var1 - this.currentLimit;
            this.limit = this.limit - (long)this.bufferSizeAfterLimit;
         } else {
            this.bufferSizeAfterLimit = 0;
         }
      }

      private int remaining() {
         return (int)(this.limit - this.pos);
      }

      private int bufferPos(long var1) {
         return (int)(var1 - this.address);
      }

      private ByteBuffer slice(long var1, long var3) throws IOException {
         int var5 = this.buffer.position();
         int var6 = this.buffer.limit();

         ByteBuffer var7;
         try {
            ((Buffer)this.buffer).position(this.bufferPos(var1));
            ((Buffer)this.buffer).limit(this.bufferPos(var3));
            var7 = this.buffer.slice();
         } catch (IllegalArgumentException var11) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } finally {
            ((Buffer)this.buffer).position(var5);
            ((Buffer)this.buffer).limit(var6);
         }

         return var7;
      }
   }
}
