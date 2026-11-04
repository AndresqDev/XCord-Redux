package io.netty.buffer;

import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public final class ByteBufUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
   private static final FastThreadLocal<byte[]> BYTE_ARRAYS = new FastThreadLocal<byte[]>() {
      protected byte[] initialValue() throws Exception {
         return PlatformDependent.allocateUninitializedArray(1024);
      }
   };
   private static final byte WRITE_UTF_UNKNOWN = 63;
   private static final int MAX_CHAR_BUFFER_SIZE;
   private static final int THREAD_LOCAL_BUFFER_SIZE;
   private static final int MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
   static final int WRITE_CHUNK_SIZE = 8192;
   static final ByteBufAllocator DEFAULT_ALLOCATOR;
   static final int MAX_TL_ARRAY_LEN = 1024;
   private static final ByteProcessor FIND_NON_ASCII;

   static byte[] threadLocalTempArray(int var0) {
      return var0 <= 1024 ? BYTE_ARRAYS.get() : PlatformDependent.allocateUninitializedArray(var0);
   }

   public static boolean isAccessible(ByteBuf var0) {
      return var0.isAccessible();
   }

   public static ByteBuf ensureAccessible(ByteBuf var0) {
      if (!var0.isAccessible()) {
         throw new IllegalReferenceCountException(var0.refCnt());
      } else {
         return var0;
      }
   }

   public static String hexDump(ByteBuf var0) {
      return hexDump(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static String hexDump(ByteBuf var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.hexDump(var0, var1, var2);
   }

   public static String hexDump(byte[] var0) {
      return hexDump(var0, 0, var0.length);
   }

   public static String hexDump(byte[] var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.hexDump(var0, var1, var2);
   }

   public static byte decodeHexByte(CharSequence var0, int var1) {
      return StringUtil.decodeHexByte(var0, var1);
   }

   public static byte[] decodeHexDump(CharSequence var0) {
      return StringUtil.decodeHexDump(var0, 0, var0.length());
   }

   public static byte[] decodeHexDump(CharSequence var0, int var1, int var2) {
      return StringUtil.decodeHexDump(var0, var1, var2);
   }

   public static boolean ensureWritableSuccess(int var0) {
      return var0 == 0 || var0 == 2;
   }

   public static int hashCode(ByteBuf var0) {
      int var1 = var0.readableBytes();
      int var2 = var1 >>> 2;
      int var3 = var1 & 3;
      int var4 = 1;
      int var5 = var0.readerIndex();
      if (var0.order() == ByteOrder.BIG_ENDIAN) {
         for (int var6 = var2; var6 > 0; var6--) {
            var4 = 31 * var4 + var0.getInt(var5);
            var5 += 4;
         }
      } else {
         for (int var7 = var2; var7 > 0; var7--) {
            var4 = 31 * var4 + swapInt(var0.getInt(var5));
            var5 += 4;
         }
      }

      for (int var8 = var3; var8 > 0; var8--) {
         var4 = 31 * var4 + var0.getByte(var5++);
      }

      if (var4 == 0) {
         var4 = 1;
      }

      return var4;
   }

   public static int indexOf(ByteBuf var0, ByteBuf var1) {
      if (var1 != null && var0 != null) {
         if (var0.readableBytes() > var1.readableBytes()) {
            return -1;
         } else {
            int var2 = var1.readableBytes();
            int var3 = var0.readableBytes();
            if (var3 == 0) {
               return 0;
            } else if (var3 == 1) {
               return var1.indexOf(var1.readerIndex(), var1.writerIndex(), var0.getByte(var0.readerIndex()));
            } else {
               int var5 = 0;
               int var6 = var0.readerIndex();
               int var7 = var1.readerIndex();
               long var8 = maxSuf(var0, var3, var6, true);
               long var10 = maxSuf(var0, var3, var6, false);
               int var12 = Math.max((int)(var8 >> 32), (int)(var10 >> 32));
               int var13 = Math.max((int)var8, (int)var10);
               int var15 = Math.min(var3 - var13, var12 + 1);
               if (equals(var0, var6, var0, var6 + var13, var15)) {
                  int var14 = -1;

                  while (var5 <= var2 - var3) {
                     int var4 = Math.max(var12, var14) + 1;

                     while (var4 < var3 && var0.getByte(var4 + var6) == var1.getByte(var4 + var5 + var7)) {
                        var4++;
                     }

                     if (var4 > var2) {
                        return -1;
                     }

                     if (var4 >= var3) {
                        var4 = var12;

                        while (var4 > var14 && var0.getByte(var4 + var6) == var1.getByte(var4 + var5 + var7)) {
                           var4--;
                        }

                        if (var4 <= var14) {
                           return var5 + var7;
                        }

                        var5 += var13;
                        var14 = var3 - var13 - 1;
                     } else {
                        var5 += var4 - var12;
                        var14 = -1;
                     }
                  }
               } else {
                  var13 = Math.max(var12 + 1, var3 - var12 - 1) + 1;

                  while (var5 <= var2 - var3) {
                     int var17 = var12 + 1;

                     while (var17 < var3 && var0.getByte(var17 + var6) == var1.getByte(var17 + var5 + var7)) {
                        var17++;
                     }

                     if (var17 > var2) {
                        return -1;
                     }

                     if (var17 >= var3) {
                        var17 = var12;

                        while (var17 >= 0 && var0.getByte(var17 + var6) == var1.getByte(var17 + var5 + var7)) {
                           var17--;
                        }

                        if (var17 < 0) {
                           return var5 + var7;
                        }

                        var5 += var13;
                     } else {
                        var5 += var17 - var12;
                     }
                  }
               }

               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static long maxSuf(ByteBuf var0, int var1, int var2, boolean var3) {
      int var4 = 1;
      int var5 = -1;
      int var6 = var2;
      int var7 = 1;

      while (var6 + var7 < var1) {
         byte var8 = var0.getByte(var6 + var7);
         byte var9 = var0.getByte(var5 + var7);
         boolean var10 = var3 ? var8 < var9 : var8 > var9;
         if (var10) {
            var6 += var7;
            var7 = 1;
            var4 = var6 - var5;
         } else if (var8 == var9) {
            if (var7 != var4) {
               var7++;
            } else {
               var6 += var4;
               var7 = 1;
            }
         } else {
            var5 = var6++;
            var4 = 1;
            var7 = 1;
         }
      }

      return ((long)var5 << 32) + (long)var4;
   }

   public static boolean equals(ByteBuf var0, int var1, ByteBuf var2, int var3, int var4) {
      ObjectUtil.checkNotNull(var0, "a");
      ObjectUtil.checkNotNull(var2, "b");
      ObjectUtil.checkPositiveOrZero(var1, "aStartIndex");
      ObjectUtil.checkPositiveOrZero(var3, "bStartIndex");
      ObjectUtil.checkPositiveOrZero(var4, "length");
      if (var0.writerIndex() - var4 >= var1 && var2.writerIndex() - var4 >= var3) {
         int var5 = var4 >>> 3;
         int var6 = var4 & 7;
         if (var0.order() == var2.order()) {
            for (int var7 = var5; var7 > 0; var7--) {
               if (var0.getLong(var1) != var2.getLong(var3)) {
                  return false;
               }

               var1 += 8;
               var3 += 8;
            }
         } else {
            for (int var8 = var5; var8 > 0; var8--) {
               if (var0.getLong(var1) != swapLong(var2.getLong(var3))) {
                  return false;
               }

               var1 += 8;
               var3 += 8;
            }
         }

         for (int var9 = var6; var9 > 0; var9--) {
            if (var0.getByte(var1) != var2.getByte(var3)) {
               return false;
            }

            var1++;
            var3++;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean equals(ByteBuf var0, ByteBuf var1) {
      if (var0 == var1) {
         return true;
      } else {
         int var2 = var0.readableBytes();
         return var2 != var1.readableBytes() ? false : equals(var0, var0.readerIndex(), var1, var1.readerIndex(), var2);
      }
   }

   public static int compare(ByteBuf var0, ByteBuf var1) {
      if (var0 == var1) {
         return 0;
      } else {
         int var2 = var0.readableBytes();
         int var3 = var1.readableBytes();
         int var4 = Math.min(var2, var3);
         int var5 = var4 >>> 2;
         int var6 = var4 & 3;
         int var7 = var0.readerIndex();
         int var8 = var1.readerIndex();
         if (var5 > 0) {
            boolean var9 = var0.order() == ByteOrder.BIG_ENDIAN;
            int var12 = var5 << 2;
            long var10;
            if (var0.order() == var1.order()) {
               var10 = var9 ? compareUintBigEndian(var0, var1, var7, var8, var12) : compareUintLittleEndian(var0, var1, var7, var8, var12);
            } else {
               var10 = var9 ? compareUintBigEndianA(var0, var1, var7, var8, var12) : compareUintBigEndianB(var0, var1, var7, var8, var12);
            }

            if (var10 != 0L) {
               return (int)Math.min(2147483647L, Math.max(-2147483648L, var10));
            }

            var7 += var12;
            var8 += var12;
         }

         for (int var13 = var7 + var6; var7 < var13; var8++) {
            int var14 = var0.getUnsignedByte(var7) - var1.getUnsignedByte(var8);
            if (var14 != 0) {
               return var14;
            }

            var7++;
         }

         return var2 - var3;
      }
   }

   private static long compareUintBigEndian(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      int var5 = var2 + var4;

      while (var2 < var5) {
         long var6 = var0.getUnsignedInt(var2) - var1.getUnsignedInt(var3);
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
         var3 += 4;
      }

      return 0L;
   }

   private static long compareUintLittleEndian(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      int var5 = var2 + var4;

      while (var2 < var5) {
         long var6 = uintFromLE(var0.getUnsignedIntLE(var2)) - uintFromLE(var1.getUnsignedIntLE(var3));
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
         var3 += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianA(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      int var5 = var2 + var4;

      while (var2 < var5) {
         long var6 = var0.getUnsignedInt(var2);
         long var8 = uintFromLE(var1.getUnsignedIntLE(var3));
         long var10 = var6 - var8;
         if (var10 != 0L) {
            return var10;
         }

         var2 += 4;
         var3 += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianB(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      int var5 = var2 + var4;

      while (var2 < var5) {
         long var6 = uintFromLE(var0.getUnsignedIntLE(var2));
         long var8 = var1.getUnsignedInt(var3);
         long var10 = var6 - var8;
         if (var10 != 0L) {
            return var10;
         }

         var2 += 4;
         var3 += 4;
      }

      return 0L;
   }

   private static long uintFromLE(long var0) {
      return Long.reverseBytes(var0) >>> 32;
   }

   private static int unrolledFirstIndexOf(AbstractByteBuf var0, int var1, int var2, byte var3) {
      assert var2 > 0 && var2 < 8;

      if (var0._getByte(var1) == var3) {
         return var1;
      } else if (var2 == 1) {
         return -1;
      } else if (var0._getByte(var1 + 1) == var3) {
         return var1 + 1;
      } else if (var2 == 2) {
         return -1;
      } else if (var0._getByte(var1 + 2) == var3) {
         return var1 + 2;
      } else if (var2 == 3) {
         return -1;
      } else if (var0._getByte(var1 + 3) == var3) {
         return var1 + 3;
      } else if (var2 == 4) {
         return -1;
      } else if (var0._getByte(var1 + 4) == var3) {
         return var1 + 4;
      } else if (var2 == 5) {
         return -1;
      } else if (var0._getByte(var1 + 5) == var3) {
         return var1 + 5;
      } else if (var2 == 6) {
         return -1;
      } else {
         return var0._getByte(var1 + 6) == var3 ? var1 + 6 : -1;
      }
   }

   static int firstIndexOf(AbstractByteBuf var0, int var1, int var2, byte var3) {
      var1 = Math.max(var1, 0);
      if (var1 < var2 && var0.capacity() != 0) {
         int var4 = var2 - var1;
         var0.checkIndex(var1, var4);
         if (!PlatformDependent.isUnaligned()) {
            return linearFirstIndexOf(var0, var1, var2, var3);
         } else {
            assert PlatformDependent.isUnaligned();

            int var5 = var1;
            int var6 = var4 & 7;
            if (var6 > 0) {
               int var7 = unrolledFirstIndexOf(var0, var1, var6, var3);
               if (var7 != -1) {
                  return var7;
               }

               var5 = var1 + var6;
               if (var5 == var2) {
                  return -1;
               }
            }

            int var18 = var4 >>> 3;
            ByteOrder var8 = ByteOrder.nativeOrder();
            boolean var9 = var8 == var0.order();
            boolean var10 = var8 == ByteOrder.LITTLE_ENDIAN;
            long var11 = ByteBufUtil.SWARByteSearch.compilePattern(var3);

            for (int var13 = 0; var13 < var18; var13++) {
               long var14 = var10 ? var0._getLongLE(var5) : var0._getLong(var5);
               int var16 = ByteBufUtil.SWARByteSearch.firstAnyPattern(var14, var11, var9);
               if (var16 < 8) {
                  return var5 + var16;
               }

               var5 += 8;
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   private static int linearFirstIndexOf(AbstractByteBuf var0, int var1, int var2, byte var3) {
      for (int var4 = var1; var4 < var2; var4++) {
         if (var0._getByte(var4) == var3) {
            return var4;
         }
      }

      return -1;
   }

   public static int indexOf(ByteBuf var0, int var1, int var2, byte var3) {
      return var0.indexOf(var1, var2, var3);
   }

   public static short swapShort(short var0) {
      return Short.reverseBytes(var0);
   }

   public static int swapMedium(int var0) {
      int var1 = var0 << 16 & 0xFF0000 | var0 & 0xFF00 | var0 >>> 16 & 0xFF;
      if ((var1 & 8388608) != 0) {
         var1 |= -16777216;
      }

      return var1;
   }

   public static int swapInt(int var0) {
      return Integer.reverseBytes(var0);
   }

   public static long swapLong(long var0) {
      return Long.reverseBytes(var0);
   }

   public static ByteBuf writeShortBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.writeShort(var1) : var0.writeShort(swapShort((short)var1));
   }

   public static ByteBuf setShortBE(ByteBuf var0, int var1, int var2) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.setShort(var1, var2) : var0.setShort(var1, swapShort((short)var2));
   }

   public static ByteBuf writeMediumBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.writeMedium(var1) : var0.writeMedium(swapMedium(var1));
   }

   public static ByteBuf readBytes(ByteBufAllocator var0, ByteBuf var1, int var2) {
      boolean var3 = true;
      ByteBuf var4 = var0.buffer(var2);

      ByteBuf var5;
      try {
         var1.readBytes(var4);
         var3 = false;
         var5 = var4;
      } finally {
         if (var3) {
            var4.release();
         }
      }

      return var5;
   }

   static int lastIndexOf(AbstractByteBuf var0, int var1, int var2, byte var3) {
      assert var1 > var2;

      int var4 = var0.capacity();
      var1 = Math.min(var1, var4);
      if (var1 >= 0 && var4 != 0) {
         var0.checkIndex(var2, var1 - var2);

         for (int var5 = var1 - 1; var5 >= var2; var5--) {
            if (var0._getByte(var5) == var3) {
               return var5;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static CharSequence checkCharSequenceBounds(CharSequence var0, int var1, int var2) {
      if (MathUtil.isOutOfBounds(var1, var2 - var1, var0.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= end (" + var2 + ") <= seq.length(" + var0.length() + ')');
      } else {
         return var0;
      }
   }

   public static ByteBuf writeUtf8(ByteBufAllocator var0, CharSequence var1) {
      ByteBuf var2 = var0.buffer(utf8MaxBytes(var1));
      writeUtf8(var2, var1);
      return var2;
   }

   public static int writeUtf8(ByteBuf var0, CharSequence var1) {
      int var2 = var1.length();
      return reserveAndWriteUtf8Seq(var0, var1, 0, var2, utf8MaxBytes(var2));
   }

   public static int writeUtf8(ByteBuf var0, CharSequence var1, int var2, int var3) {
      checkCharSequenceBounds(var1, var2, var3);
      return reserveAndWriteUtf8Seq(var0, var1, var2, var3, utf8MaxBytes(var3 - var2));
   }

   public static int reserveAndWriteUtf8(ByteBuf var0, CharSequence var1, int var2) {
      return reserveAndWriteUtf8Seq(var0, var1, 0, var1.length(), var2);
   }

   public static int reserveAndWriteUtf8(ByteBuf var0, CharSequence var1, int var2, int var3, int var4) {
      return reserveAndWriteUtf8Seq(var0, checkCharSequenceBounds(var1, var2, var3), var2, var3, var4);
   }

   private static int reserveAndWriteUtf8Seq(ByteBuf var0, CharSequence var1, int var2, int var3, int var4) {
      while (true) {
         if (var0 instanceof WrappedCompositeByteBuf) {
            var0 = var0.unwrap();
         } else {
            if (var0 instanceof AbstractByteBuf) {
               AbstractByteBuf var7 = (AbstractByteBuf)var0;
               var7.ensureWritable0(var4);
               int var6 = writeUtf8(var7, var7.writerIndex, var4, var1, var2, var3);
               var7.writerIndex += var6;
               return var6;
            }

            if (!(var0 instanceof WrappedByteBuf)) {
               byte[] var5 = var1.subSequence(var2, var3).toString().getBytes(CharsetUtil.UTF_8);
               var0.writeBytes(var5);
               return var5.length;
            }

            var0 = var0.unwrap();
         }
      }
   }

   static int writeUtf8(AbstractByteBuf var0, int var1, int var2, CharSequence var3, int var4) {
      return writeUtf8(var0, var1, var2, var3, 0, var4);
   }

   static int writeUtf8(AbstractByteBuf var0, int var1, int var2, CharSequence var3, int var4, int var5) {
      if (var3 instanceof AsciiString) {
         writeAsciiString(var0, var1, (AsciiString)var3, var4, var5);
         return var5 - var4;
      } else {
         if (PlatformDependent.hasUnsafe()) {
            if (var0.hasArray()) {
               return unsafeWriteUtf8(var0.array(), PlatformDependent.byteArrayBaseOffset(), var0.arrayOffset() + var1, var3, var4, var5);
            }

            if (var0.hasMemoryAddress()) {
               return unsafeWriteUtf8(null, var0.memoryAddress(), var1, var3, var4, var5);
            }
         } else {
            if (var0.hasArray()) {
               return safeArrayWriteUtf8(var0.array(), var0.arrayOffset() + var1, var3, var4, var5);
            }

            if (var0.isDirect()) {
               assert var0.nioBufferCount() == 1;

               ByteBuffer var6 = var0.internalNioBuffer(var1, var2);
               int var7 = var6.position();
               return safeDirectWriteUtf8(var6, var7, var3, var4, var5);
            }
         }

         return safeWriteUtf8(var0, var1, var3, var4, var5);
      }
   }

   static void writeAsciiString(AbstractByteBuf var0, int var1, AsciiString var2, int var3, int var4) {
      int var5 = var2.arrayOffset() + var3;
      int var6 = var4 - var3;
      if (PlatformDependent.hasUnsafe()) {
         if (var0.hasArray()) {
            PlatformDependent.copyMemory(var2.array(), var5, var0.array(), var0.arrayOffset() + var1, (long)var6);
            return;
         }

         if (var0.hasMemoryAddress()) {
            PlatformDependent.copyMemory(var2.array(), var5, var0.memoryAddress() + (long)var1, (long)var6);
            return;
         }
      }

      if (var0.hasArray()) {
         System.arraycopy(var2.array(), var5, var0.array(), var0.arrayOffset() + var1, var6);
      } else {
         var0.setBytes(var1, var2.array(), var5, var6);
      }
   }

   private static int safeDirectWriteUtf8(ByteBuffer var0, int var1, CharSequence var2, int var3, int var4) {
      assert !(var2 instanceof AsciiString);

      int var5 = var1;

      for (int var6 = var3; var6 < var4; var6++) {
         char var7 = var2.charAt(var6);
         if (var7 < 128) {
            var0.put(var1++, (byte)var7);
         } else if (var7 < 2048) {
            var0.put(var1++, (byte)(192 | var7 >> 6));
            var0.put(var1++, (byte)(128 | var7 & '?'));
         } else if (StringUtil.isSurrogate(var7)) {
            if (!Character.isHighSurrogate(var7)) {
               var0.put(var1++, (byte)63);
            } else {
               if (++var6 == var4) {
                  var0.put(var1++, (byte)63);
                  break;
               }

               char var8 = var2.charAt(var6);
               if (!Character.isLowSurrogate(var8)) {
                  var0.put(var1++, (byte)63);
                  var0.put(var1++, Character.isHighSurrogate(var8) ? 63 : (byte)var8);
               } else {
                  int var9 = Character.toCodePoint(var7, var8);
                  var0.put(var1++, (byte)(240 | var9 >> 18));
                  var0.put(var1++, (byte)(128 | var9 >> 12 & 63));
                  var0.put(var1++, (byte)(128 | var9 >> 6 & 63));
                  var0.put(var1++, (byte)(128 | var9 & 63));
               }
            }
         } else {
            var0.put(var1++, (byte)(224 | var7 >> '\f'));
            var0.put(var1++, (byte)(128 | var7 >> 6 & 63));
            var0.put(var1++, (byte)(128 | var7 & '?'));
         }
      }

      return var1 - var5;
   }

   private static int safeWriteUtf8(AbstractByteBuf var0, int var1, CharSequence var2, int var3, int var4) {
      assert !(var2 instanceof AsciiString);

      int var5 = var1;

      for (int var6 = var3; var6 < var4; var6++) {
         char var7 = var2.charAt(var6);
         if (var7 < 128) {
            var0._setByte(var1++, (byte)var7);
         } else if (var7 < 2048) {
            var0._setByte(var1++, (byte)(192 | var7 >> 6));
            var0._setByte(var1++, (byte)(128 | var7 & '?'));
         } else if (StringUtil.isSurrogate(var7)) {
            if (!Character.isHighSurrogate(var7)) {
               var0._setByte(var1++, 63);
            } else {
               if (++var6 == var4) {
                  var0._setByte(var1++, 63);
                  break;
               }

               char var8 = var2.charAt(var6);
               if (!Character.isLowSurrogate(var8)) {
                  var0._setByte(var1++, 63);
                  var0._setByte(var1++, Character.isHighSurrogate(var8) ? 63 : var8);
               } else {
                  int var9 = Character.toCodePoint(var7, var8);
                  var0._setByte(var1++, (byte)(240 | var9 >> 18));
                  var0._setByte(var1++, (byte)(128 | var9 >> 12 & 63));
                  var0._setByte(var1++, (byte)(128 | var9 >> 6 & 63));
                  var0._setByte(var1++, (byte)(128 | var9 & 63));
               }
            }
         } else {
            var0._setByte(var1++, (byte)(224 | var7 >> '\f'));
            var0._setByte(var1++, (byte)(128 | var7 >> 6 & 63));
            var0._setByte(var1++, (byte)(128 | var7 & '?'));
         }
      }

      return var1 - var5;
   }

   private static int safeArrayWriteUtf8(byte[] var0, int var1, CharSequence var2, int var3, int var4) {
      int var5 = var1;

      for (int var6 = var3; var6 < var4; var6++) {
         char var7 = var2.charAt(var6);
         if (var7 < 128) {
            var0[var1++] = (byte)var7;
         } else if (var7 < 2048) {
            var0[var1++] = (byte)(192 | var7 >> 6);
            var0[var1++] = (byte)(128 | var7 & '?');
         } else if (StringUtil.isSurrogate(var7)) {
            if (!Character.isHighSurrogate(var7)) {
               var0[var1++] = 63;
            } else {
               if (++var6 == var4) {
                  var0[var1++] = 63;
                  break;
               }

               char var8 = var2.charAt(var6);
               if (!Character.isLowSurrogate(var8)) {
                  var0[var1++] = 63;
                  var0[var1++] = (byte)(Character.isHighSurrogate(var8) ? 63 : var8);
               } else {
                  int var9 = Character.toCodePoint(var7, var8);
                  var0[var1++] = (byte)(240 | var9 >> 18);
                  var0[var1++] = (byte)(128 | var9 >> 12 & 63);
                  var0[var1++] = (byte)(128 | var9 >> 6 & 63);
                  var0[var1++] = (byte)(128 | var9 & 63);
               }
            }
         } else {
            var0[var1++] = (byte)(224 | var7 >> '\f');
            var0[var1++] = (byte)(128 | var7 >> 6 & 63);
            var0[var1++] = (byte)(128 | var7 & '?');
         }
      }

      return var1 - var5;
   }

   private static int unsafeWriteUtf8(byte[] var0, long var1, int var3, CharSequence var4, int var5, int var6) {
      assert !(var4 instanceof AsciiString);

      long var7 = var1 + (long)var3;
      long var9 = var7;

      for (int var11 = var5; var11 < var6; var11++) {
         char var12 = var4.charAt(var11);
         if (var12 < 128) {
            PlatformDependent.putByte(var0, var7++, (byte)var12);
         } else if (var12 < 2048) {
            PlatformDependent.putByte(var0, var7++, (byte)(192 | var12 >> 6));
            PlatformDependent.putByte(var0, var7++, (byte)(128 | var12 & '?'));
         } else if (StringUtil.isSurrogate(var12)) {
            if (!Character.isHighSurrogate(var12)) {
               PlatformDependent.putByte(var0, var7++, (byte)63);
            } else {
               if (++var11 == var6) {
                  PlatformDependent.putByte(var0, var7++, (byte)63);
                  break;
               }

               char var13 = var4.charAt(var11);
               if (!Character.isLowSurrogate(var13)) {
                  PlatformDependent.putByte(var0, var7++, (byte)63);
                  PlatformDependent.putByte(var0, var7++, (byte)(Character.isHighSurrogate(var13) ? 63 : var13));
               } else {
                  int var14 = Character.toCodePoint(var12, var13);
                  PlatformDependent.putByte(var0, var7++, (byte)(240 | var14 >> 18));
                  PlatformDependent.putByte(var0, var7++, (byte)(128 | var14 >> 12 & 63));
                  PlatformDependent.putByte(var0, var7++, (byte)(128 | var14 >> 6 & 63));
                  PlatformDependent.putByte(var0, var7++, (byte)(128 | var14 & 63));
               }
            }
         } else {
            PlatformDependent.putByte(var0, var7++, (byte)(224 | var12 >> '\f'));
            PlatformDependent.putByte(var0, var7++, (byte)(128 | var12 >> 6 & 63));
            PlatformDependent.putByte(var0, var7++, (byte)(128 | var12 & '?'));
         }
      }

      return (int)(var7 - var9);
   }

   public static int utf8MaxBytes(int var0) {
      return var0 * MAX_BYTES_PER_CHAR_UTF8;
   }

   public static int utf8MaxBytes(CharSequence var0) {
      return utf8MaxBytes(var0.length());
   }

   public static int utf8Bytes(CharSequence var0) {
      return utf8ByteCount(var0, 0, var0.length());
   }

   public static int utf8Bytes(CharSequence var0, int var1, int var2) {
      return utf8ByteCount(checkCharSequenceBounds(var0, var1, var2), var1, var2);
   }

   private static int utf8ByteCount(CharSequence var0, int var1, int var2) {
      if (var0 instanceof AsciiString) {
         return var2 - var1;
      } else {
         int var3 = var1;

         while (var3 < var2 && var0.charAt(var3) < 128) {
            var3++;
         }

         return var3 < var2 ? var3 - var1 + utf8BytesNonAscii(var0, var3, var2) : var3 - var1;
      }
   }

   private static int utf8BytesNonAscii(CharSequence var0, int var1, int var2) {
      int var3 = 0;

      for (int var4 = var1; var4 < var2; var4++) {
         char var5 = var0.charAt(var4);
         if (var5 < 2048) {
            var3 += (127 - var5 >>> 31) + 1;
         } else if (StringUtil.isSurrogate(var5)) {
            if (!Character.isHighSurrogate(var5)) {
               var3++;
            } else {
               if (++var4 == var2) {
                  var3++;
                  break;
               }

               if (!Character.isLowSurrogate(var0.charAt(var4))) {
                  var3 += 2;
               } else {
                  var3 += 4;
               }
            }
         } else {
            var3 += 3;
         }
      }

      return var3;
   }

   public static ByteBuf writeAscii(ByteBufAllocator var0, CharSequence var1) {
      ByteBuf var2 = var0.buffer(var1.length());
      writeAscii(var2, var1);
      return var2;
   }

   public static int writeAscii(ByteBuf var0, CharSequence var1) {
      while (true) {
         if (var0 instanceof WrappedCompositeByteBuf) {
            var0 = var0.unwrap();
         } else {
            if (var0 instanceof AbstractByteBuf) {
               int var5 = var1.length();
               AbstractByteBuf var3 = (AbstractByteBuf)var0;
               var3.ensureWritable0(var5);
               if (var1 instanceof AsciiString) {
                  writeAsciiString(var3, var3.writerIndex, (AsciiString)var1, 0, var5);
               } else {
                  int var4 = writeAscii(var3, var3.writerIndex, var1, var5);

                  assert var4 == var5;
               }

               var3.writerIndex += var5;
               return var5;
            }

            if (!(var0 instanceof WrappedByteBuf)) {
               byte[] var2 = var1.toString().getBytes(CharsetUtil.US_ASCII);
               var0.writeBytes(var2);
               return var2.length;
            }

            var0 = var0.unwrap();
         }
      }
   }

   static int writeAscii(AbstractByteBuf var0, int var1, CharSequence var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         var0._setByte(var1++, AsciiString.c2b(var2.charAt(var4)));
      }

      return var3;
   }

   public static ByteBuf encodeString(ByteBufAllocator var0, CharBuffer var1, Charset var2) {
      return encodeString0(var0, false, var1, var2, 0);
   }

   public static ByteBuf encodeString(ByteBufAllocator var0, CharBuffer var1, Charset var2, int var3) {
      return encodeString0(var0, false, var1, var2, var3);
   }

   static ByteBuf encodeString0(ByteBufAllocator var0, boolean var1, CharBuffer var2, Charset var3, int var4) {
      CharsetEncoder var5 = CharsetUtil.encoder(var3);
      int var6 = (int)((double)var2.remaining() * (double)var5.maxBytesPerChar()) + var4;
      boolean var7 = true;
      ByteBuf var8;
      if (var1) {
         var8 = var0.heapBuffer(var6);
      } else {
         var8 = var0.buffer(var6);
      }

      ByteBuf var12;
      try {
         ByteBuffer var9 = var8.internalNioBuffer(var8.readerIndex(), var6);
         int var10 = var9.position();
         CoderResult var11 = var5.encode(var2, var9, true);
         if (!var11.isUnderflow()) {
            var11.throwException();
         }

         var11 = var5.flush(var9);
         if (!var11.isUnderflow()) {
            var11.throwException();
         }

         var8.writerIndex(var8.writerIndex() + var9.position() - var10);
         var7 = false;
         var12 = var8;
      } catch (CharacterCodingException var16) {
         throw new IllegalStateException(var16);
      } finally {
         if (var7) {
            var8.release();
         }
      }

      return var12;
   }

   static String decodeString(ByteBuf var0, int var1, int var2, Charset var3) {
      if (var2 == 0) {
         return "";
      } else {
         byte[] var4;
         int var5;
         if (var0.hasArray()) {
            var4 = var0.array();
            var5 = var0.arrayOffset() + var1;
         } else {
            var4 = threadLocalTempArray(var2);
            var5 = 0;
            var0.getBytes(var1, var4, 0, var2);
         }

         return CharsetUtil.US_ASCII.equals(var3) ? new String(var4, 0, var5, var2) : new String(var4, var5, var2, var3);
      }
   }

   public static ByteBuf threadLocalDirectBuffer() {
      if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
         return null;
      } else {
         return (ByteBuf)(PlatformDependent.hasUnsafe()
            ? ByteBufUtil.ThreadLocalUnsafeDirectByteBuf.newInstance()
            : ByteBufUtil.ThreadLocalDirectByteBuf.newInstance());
      }
   }

   public static byte[] getBytes(ByteBuf var0) {
      return getBytes(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static byte[] getBytes(ByteBuf var0, int var1, int var2) {
      return getBytes(var0, var1, var2, true);
   }

   public static byte[] getBytes(ByteBuf var0, int var1, int var2, boolean var3) {
      int var4 = var0.capacity();
      if (MathUtil.isOutOfBounds(var1, var2, var4)) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= start + length(" + var2 + ") <= buf.capacity(" + var4 + ')');
      } else if (var0.hasArray()) {
         int var7 = var0.arrayOffset() + var1;
         byte[] var6 = var0.array();
         return !var3 && var7 == 0 && var2 == var6.length ? var6 : Arrays.copyOfRange(var6, var7, var7 + var2);
      } else {
         byte[] var5 = PlatformDependent.allocateUninitializedArray(var2);
         var0.getBytes(var1, var5);
         return var5;
      }
   }

   public static void copy(AsciiString var0, ByteBuf var1) {
      copy(var0, 0, var1, var0.length());
   }

   public static void copy(AsciiString var0, int var1, ByteBuf var2, int var3, int var4) {
      if (MathUtil.isOutOfBounds(var1, var4, var0.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var4 + ") <= srcLen(" + var0.length() + ')');
      } else {
         ObjectUtil.checkNotNull(var2, "dst").setBytes(var3, var0.array(), var1 + var0.arrayOffset(), var4);
      }
   }

   public static void copy(AsciiString var0, int var1, ByteBuf var2, int var3) {
      if (MathUtil.isOutOfBounds(var1, var3, var0.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var3 + ") <= srcLen(" + var0.length() + ')');
      } else {
         ObjectUtil.checkNotNull(var2, "dst").writeBytes(var0.array(), var1 + var0.arrayOffset(), var3);
      }
   }

   public static String prettyHexDump(ByteBuf var0) {
      return prettyHexDump(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static String prettyHexDump(ByteBuf var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.prettyHexDump(var0, var1, var2);
   }

   public static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1) {
      appendPrettyHexDump(var0, var1, var1.readerIndex(), var1.readableBytes());
   }

   public static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1, int var2, int var3) {
      ByteBufUtil.HexUtil.appendPrettyHexDump(var0, var1, var2, var3);
   }

   public static boolean isText(ByteBuf var0, Charset var1) {
      return isText(var0, var0.readerIndex(), var0.readableBytes(), var1);
   }

   public static boolean isText(ByteBuf var0, int var1, int var2, Charset var3) {
      ObjectUtil.checkNotNull(var0, "buf");
      ObjectUtil.checkNotNull(var3, "charset");
      int var4 = var0.readerIndex() + var0.readableBytes();
      if (var1 < 0 || var2 < 0 || var1 > var4 - var2) {
         throw new IndexOutOfBoundsException("index: " + var1 + " length: " + var2);
      } else if (var3.equals(CharsetUtil.UTF_8)) {
         return isUtf8(var0, var1, var2);
      } else if (var3.equals(CharsetUtil.US_ASCII)) {
         return isAscii(var0, var1, var2);
      } else {
         CharsetDecoder var5 = CharsetUtil.decoder(var3, CodingErrorAction.REPORT, CodingErrorAction.REPORT);

         try {
            if (var0.nioBufferCount() == 1) {
               var5.decode(var0.nioBuffer(var1, var2));
            } else {
               ByteBuf var6 = var0.alloc().heapBuffer(var2);

               try {
                  var6.writeBytes(var0, var1, var2);
                  var5.decode(var6.internalNioBuffer(var6.readerIndex(), var2));
               } finally {
                  var6.release();
               }
            }

            return true;
         } catch (CharacterCodingException var11) {
            return false;
         }
      }
   }

   private static boolean isAscii(ByteBuf var0, int var1, int var2) {
      return var0.forEachByte(var1, var2, FIND_NON_ASCII) == -1;
   }

   private static boolean isUtf8(ByteBuf var0, int var1, int var2) {
      int var3 = var1 + var2;

      while (var1 < var3) {
         byte var4 = var0.getByte(var1++);
         if ((var4 & 128) != 0) {
            if ((var4 & 224) != 192) {
               if ((var4 & 240) == 224) {
                  if (var1 > var3 - 2) {
                     return false;
                  }

                  byte var12 = var0.getByte(var1++);
                  byte var13 = var0.getByte(var1++);
                  if ((var12 & 192) != 128 || (var13 & 192) != 128) {
                     return false;
                  }

                  if ((var4 & 15) == 0 && (var12 & 255) < 160) {
                     return false;
                  }

                  if ((var4 & 15) == 13 && (var12 & 255) > 159) {
                     return false;
                  }
               } else {
                  if ((var4 & 248) != 240) {
                     return false;
                  }

                  if (var1 > var3 - 3) {
                     return false;
                  }

                  byte var11 = var0.getByte(var1++);
                  byte var6 = var0.getByte(var1++);
                  byte var7 = var0.getByte(var1++);
                  if ((var11 & 192) == 128 && (var6 & 192) == 128 && (var7 & 192) == 128) {
                     if ((var4 & 255) <= 244 && ((var4 & 255) != 240 || (var11 & 255) >= 144) && ((var4 & 255) != 244 || (var11 & 255) <= 143)) {
                        continue;
                     }

                     return false;
                  }

                  return false;
               }
            } else {
               if (var1 >= var3) {
                  return false;
               }

               byte var5 = var0.getByte(var1++);
               if ((var5 & 192) != 128) {
                  return false;
               }

               if ((var4 & 255) < 194) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   static void readBytes(ByteBufAllocator var0, ByteBuffer var1, int var2, int var3, OutputStream var4) throws IOException {
      if (var1.hasArray()) {
         var4.write(var1.array(), var2 + var1.arrayOffset(), var3);
      } else {
         int var5 = Math.min(var3, 8192);
         ((Buffer)var1).clear().position(var2);
         if (var3 > 1024 && var0.isDirectBufferPooled()) {
            ByteBuf var6 = var0.heapBuffer(var5);

            try {
               byte[] var7 = var6.array();
               int var8 = var6.arrayOffset();
               getBytes(var1, var7, var8, var5, var4, var3);
            } finally {
               var6.release();
            }
         } else {
            getBytes(var1, threadLocalTempArray(var5), 0, var5, var4, var3);
         }
      }
   }

   private static void getBytes(ByteBuffer var0, byte[] var1, int var2, int var3, OutputStream var4, int var5) throws IOException {
      do {
         int var6 = Math.min(var3, var5);
         var0.get(var1, var2, var6);
         var4.write(var1, var2, var6);
         var5 -= var6;
      } while (var5 > 0);
   }

   private ByteBufUtil() {
   }

   static {
      String var0 = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
      var0 = var0.toLowerCase(Locale.US).trim();
      Object var1;
      if ("unpooled".equals(var0)) {
         var1 = UnpooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", var0);
      } else if ("pooled".equals(var0)) {
         var1 = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", var0);
      } else {
         var1 = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", var0);
      }

      DEFAULT_ALLOCATOR = (ByteBufAllocator)var1;
      THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 0);
      logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", THREAD_LOCAL_BUFFER_SIZE);
      MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
      logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", MAX_CHAR_BUFFER_SIZE);
      FIND_NON_ASCII = new ByteProcessor() {
         @Override
         public boolean process(byte var1) {
            return var1 >= 0;
         }
      };
   }

   private static final class HexUtil {
      private static final char[] BYTE2CHAR = new char[256];
      private static final char[] HEXDUMP_TABLE = new char[1024];
      private static final String[] HEXPADDING = new String[16];
      private static final String[] HEXDUMP_ROWPREFIXES = new String[4096];
      private static final String[] BYTE2HEX = new String[256];
      private static final String[] BYTEPADDING = new String[16];

      private static String hexDump(ByteBuf var0, int var1, int var2) {
         ObjectUtil.checkPositiveOrZero(var2, "length");
         if (var2 == 0) {
            return "";
         } else {
            int var3 = var1 + var2;
            char[] var4 = new char[var2 << 1];
            int var5 = var1;

            for (byte var6 = 0; var5 < var3; var6 += 2) {
               System.arraycopy(HEXDUMP_TABLE, var0.getUnsignedByte(var5) << 1, var4, var6, 2);
               var5++;
            }

            return new String(var4);
         }
      }

      private static String hexDump(byte[] var0, int var1, int var2) {
         ObjectUtil.checkPositiveOrZero(var2, "length");
         if (var2 == 0) {
            return "";
         } else {
            int var3 = var1 + var2;
            char[] var4 = new char[var2 << 1];
            int var5 = var1;

            for (byte var6 = 0; var5 < var3; var6 += 2) {
               System.arraycopy(HEXDUMP_TABLE, (var0[var5] & 255) << 1, var4, var6, 2);
               var5++;
            }

            return new String(var4);
         }
      }

      private static String prettyHexDump(ByteBuf var0, int var1, int var2) {
         if (var2 == 0) {
            return "";
         } else {
            int var3 = var2 / 16 + ((var2 & 15) == 0 ? 0 : 1) + 4;
            StringBuilder var4 = new StringBuilder(var3 * 80);
            appendPrettyHexDump(var4, var0, var1, var2);
            return var4.toString();
         }
      }

      private static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1, int var2, int var3) {
         if (MathUtil.isOutOfBounds(var2, var3, var1.capacity())) {
            throw new IndexOutOfBoundsException("expected: 0 <= offset(" + var2 + ") <= offset + length(" + var3 + ") <= buf.capacity(" + var1.capacity() + ')');
         } else if (var3 != 0) {
            var0.append(
               "         +-------------------------------------------------+"
                  + StringUtil.NEWLINE
                  + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |"
                  + StringUtil.NEWLINE
                  + "+--------+-------------------------------------------------+----------------+"
            );
            int var4 = var3 >>> 4;
            int var5 = var3 & 15;

            for (int var6 = 0; var6 < var4; var6++) {
               int var7 = (var6 << 4) + var2;
               appendHexDumpRowPrefix(var0, var6, var7);
               int var8 = var7 + 16;

               for (int var9 = var7; var9 < var8; var9++) {
                  var0.append(BYTE2HEX[var1.getUnsignedByte(var9)]);
               }

               var0.append(" |");

               for (int var14 = var7; var14 < var8; var14++) {
                  var0.append(BYTE2CHAR[var1.getUnsignedByte(var14)]);
               }

               var0.append('|');
            }

            if (var5 != 0) {
               int var10 = (var4 << 4) + var2;
               appendHexDumpRowPrefix(var0, var4, var10);
               int var11 = var10 + var5;

               for (int var12 = var10; var12 < var11; var12++) {
                  var0.append(BYTE2HEX[var1.getUnsignedByte(var12)]);
               }

               var0.append(HEXPADDING[var5]);
               var0.append(" |");

               for (int var13 = var10; var13 < var11; var13++) {
                  var0.append(BYTE2CHAR[var1.getUnsignedByte(var13)]);
               }

               var0.append(BYTEPADDING[var5]);
               var0.append('|');
            }

            var0.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
         }
      }

      private static void appendHexDumpRowPrefix(StringBuilder var0, int var1, int var2) {
         if (var1 < HEXDUMP_ROWPREFIXES.length) {
            var0.append(HEXDUMP_ROWPREFIXES[var1]);
         } else {
            var0.append(StringUtil.NEWLINE);
            var0.append(Long.toHexString((long)var2 & 4294967295L | 4294967296L));
            var0.setCharAt(var0.length() - 9, '|');
            var0.append('|');
         }
      }

      static {
         char[] var0 = "0123456789abcdef".toCharArray();

         for (int var1 = 0; var1 < 256; var1++) {
            HEXDUMP_TABLE[var1 << 1] = var0[var1 >>> 4 & 15];
            HEXDUMP_TABLE[(var1 << 1) + 1] = var0[var1 & 15];
         }

         for (int var5 = 0; var5 < HEXPADDING.length; var5++) {
            int var2 = HEXPADDING.length - var5;
            StringBuilder var3 = new StringBuilder(var2 * 3);

            for (int var4 = 0; var4 < var2; var4++) {
               var3.append("   ");
            }

            HEXPADDING[var5] = var3.toString();
         }

         for (int var6 = 0; var6 < HEXDUMP_ROWPREFIXES.length; var6++) {
            StringBuilder var10 = new StringBuilder(12);
            var10.append(StringUtil.NEWLINE);
            var10.append(Long.toHexString((long)(var6 << 4) & 4294967295L | 4294967296L));
            var10.setCharAt(var10.length() - 9, '|');
            var10.append('|');
            HEXDUMP_ROWPREFIXES[var6] = var10.toString();
         }

         for (int var7 = 0; var7 < BYTE2HEX.length; var7++) {
            BYTE2HEX[var7] = ' ' + StringUtil.byteToHexStringPadded(var7);
         }

         for (int var8 = 0; var8 < BYTEPADDING.length; var8++) {
            int var11 = BYTEPADDING.length - var8;
            StringBuilder var12 = new StringBuilder(var11);

            for (int var13 = 0; var13 < var11; var13++) {
               var12.append(' ');
            }

            BYTEPADDING[var8] = var12.toString();
         }

         for (int var9 = 0; var9 < BYTE2CHAR.length; var9++) {
            if (var9 > 31 && var9 < 127) {
               BYTE2CHAR[var9] = (char)var9;
            } else {
               BYTE2CHAR[var9] = '.';
            }
         }
      }
   }

   private static final class SWARByteSearch {
      private static long compilePattern(byte var0) {
         return ((long)var0 & 255L) * 72340172838076673L;
      }

      private static int firstAnyPattern(long var0, long var2, boolean var4) {
         long var5 = var0 ^ var2;
         long var7 = (var5 & 9187201950435737471L) + 9187201950435737471L;
         var7 = ~(var7 | var5 | 9187201950435737471L);
         int var9 = var4 ? Long.numberOfLeadingZeros(var7) : Long.numberOfTrailingZeros(var7);
         return var9 >>> 3;
      }
   }

   static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf {
      private static final ObjectPool<ByteBufUtil.ThreadLocalDirectByteBuf> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<ByteBufUtil.ThreadLocalDirectByteBuf>() {
            public ByteBufUtil.ThreadLocalDirectByteBuf newObject(ObjectPool.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> var1) {
               return new ByteBufUtil.ThreadLocalDirectByteBuf(var1);
            }
         }
      );
      private final ObjectPool.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalDirectByteBuf var0 = RECYCLER.get();
         var0.resetRefCnt();
         return var0;
      }

      private ThreadLocalDirectByteBuf(ObjectPool.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> var1) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = var1;
      }

      @Override
      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.recycle(this);
         }
      }
   }

   static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      private static final ObjectPool<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> RECYCLER = ObjectPool.newPool(
         new ObjectPool.ObjectCreator<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf>() {
            public ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newObject(ObjectPool.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> var1) {
               return new ByteBufUtil.ThreadLocalUnsafeDirectByteBuf(var1);
            }
         }
      );
      private final ObjectPool.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalUnsafeDirectByteBuf var0 = RECYCLER.get();
         var0.resetRefCnt();
         return var0;
      }

      private ThreadLocalUnsafeDirectByteBuf(ObjectPool.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> var1) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, Integer.MAX_VALUE);
         this.handle = var1;
      }

      @Override
      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.recycle(this);
         }
      }
   }
}
