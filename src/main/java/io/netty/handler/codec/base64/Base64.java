package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteOrder;

public final class Base64 {
   private static final int MAX_LINE_LENGTH = 76;
   private static final byte EQUALS_SIGN = 61;
   private static final byte NEW_LINE = 10;
   private static final byte WHITE_SPACE_ENC = -5;
   private static final byte EQUALS_SIGN_ENC = -1;

   private static byte[] alphabet(Base64Dialect var0) {
      return ObjectUtil.checkNotNull(var0, "dialect").alphabet;
   }

   private static byte[] decodabet(Base64Dialect var0) {
      return ObjectUtil.checkNotNull(var0, "dialect").decodabet;
   }

   private static boolean breakLines(Base64Dialect var0) {
      return ObjectUtil.checkNotNull(var0, "dialect").breakLinesByDefault;
   }

   public static ByteBuf encode(ByteBuf var0) {
      return encode(var0, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf var0, Base64Dialect var1) {
      return encode(var0, breakLines(var1), var1);
   }

   public static ByteBuf encode(ByteBuf var0, boolean var1) {
      return encode(var0, var1, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf var0, boolean var1, Base64Dialect var2) {
      ObjectUtil.checkNotNull(var0, "src");
      ByteBuf var3 = encode(var0, var0.readerIndex(), var0.readableBytes(), var1, var2);
      var0.readerIndex(var0.writerIndex());
      return var3;
   }

   public static ByteBuf encode(ByteBuf var0, int var1, int var2) {
      return encode(var0, var1, var2, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf var0, int var1, int var2, Base64Dialect var3) {
      return encode(var0, var1, var2, breakLines(var3), var3);
   }

   public static ByteBuf encode(ByteBuf var0, int var1, int var2, boolean var3) {
      return encode(var0, var1, var2, var3, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf var0, int var1, int var2, boolean var3, Base64Dialect var4) {
      return encode(var0, var1, var2, var3, var4, var0.alloc());
   }

   public static ByteBuf encode(ByteBuf var0, int var1, int var2, boolean var3, Base64Dialect var4, ByteBufAllocator var5) {
      ObjectUtil.checkNotNull(var0, "src");
      ObjectUtil.checkNotNull(var4, "dialect");
      ByteBuf var6 = var5.buffer(encodedBufferSize(var2, var3)).order(var0.order());
      byte[] var7 = alphabet(var4);
      byte var8 = 0;
      int var9 = 0;
      int var10 = var2 - 2;

      for (byte var11 = 0; var8 < var10; var9 += 4) {
         encode3to4(var0, var8 + var1, 3, var6, var9, var7);
         var11 += 4;
         if (var3 && var11 == 76) {
            var6.setByte(var9 + 4, 10);
            var9++;
            var11 = 0;
         }

         var8 += 3;
      }

      if (var8 < var2) {
         encode3to4(var0, var8 + var1, var2 - var8, var6, var9, var7);
         var9 += 4;
      }

      if (var9 > 1 && var6.getByte(var9 - 1) == 10) {
         var9--;
      }

      return var6.slice(0, var9);
   }

   private static void encode3to4(ByteBuf var0, int var1, int var2, ByteBuf var3, int var4, byte[] var5) {
      if (var0.order() == ByteOrder.BIG_ENDIAN) {
         int var6;
         switch (var2) {
            case 1:
               var6 = toInt(var0.getByte(var1));
               break;
            case 2:
               var6 = toIntBE(var0.getShort(var1));
               break;
            default:
               var6 = var2 <= 0 ? 0 : toIntBE(var0.getMedium(var1));
         }

         encode3to4BigEndian(var6, var2, var3, var4, var5);
      } else {
         int var7;
         switch (var2) {
            case 1:
               var7 = toInt(var0.getByte(var1));
               break;
            case 2:
               var7 = toIntLE(var0.getShort(var1));
               break;
            default:
               var7 = var2 <= 0 ? 0 : toIntLE(var0.getMedium(var1));
         }

         encode3to4LittleEndian(var7, var2, var3, var4, var5);
      }
   }

   static int encodedBufferSize(int var0, boolean var1) {
      long var2 = ((long)var0 << 2) / 3L;
      long var4 = var2 + 3L & -4L;
      if (var1) {
         var4 += var2 / 76L;
      }

      return var4 < 2147483647L ? (int)var4 : Integer.MAX_VALUE;
   }

   private static int toInt(byte var0) {
      return (var0 & 0xFF) << 16;
   }

   private static int toIntBE(short var0) {
      return (var0 & 0xFF00) << 8 | (var0 & 0xFF) << 8;
   }

   private static int toIntLE(short var0) {
      return (var0 & 0xFF) << 16 | var0 & 0xFF00;
   }

   private static int toIntBE(int var0) {
      return var0 & 0xFF0000 | var0 & 0xFF00 | var0 & 0xFF;
   }

   private static int toIntLE(int var0) {
      return (var0 & 0xFF) << 16 | var0 & 0xFF00 | (var0 & 0xFF0000) >>> 16;
   }

   private static void encode3to4BigEndian(int var0, int var1, ByteBuf var2, int var3, byte[] var4) {
      switch (var1) {
         case 1:
            var2.setInt(var3, var4[var0 >>> 18] << 24 | var4[var0 >>> 12 & 63] << 16 | 15616 | 61);
            break;
         case 2:
            var2.setInt(var3, var4[var0 >>> 18] << 24 | var4[var0 >>> 12 & 63] << 16 | var4[var0 >>> 6 & 63] << 8 | 61);
            break;
         case 3:
            var2.setInt(var3, var4[var0 >>> 18] << 24 | var4[var0 >>> 12 & 63] << 16 | var4[var0 >>> 6 & 63] << 8 | var4[var0 & 63]);
      }
   }

   private static void encode3to4LittleEndian(int var0, int var1, ByteBuf var2, int var3, byte[] var4) {
      switch (var1) {
         case 1:
            var2.setInt(var3, var4[var0 >>> 18] | var4[var0 >>> 12 & 63] << 8 | 3997696 | 1023410176);
            break;
         case 2:
            var2.setInt(var3, var4[var0 >>> 18] | var4[var0 >>> 12 & 63] << 8 | var4[var0 >>> 6 & 63] << 16 | 1023410176);
            break;
         case 3:
            var2.setInt(var3, var4[var0 >>> 18] | var4[var0 >>> 12 & 63] << 8 | var4[var0 >>> 6 & 63] << 16 | var4[var0 & 63] << 24);
      }
   }

   public static ByteBuf decode(ByteBuf var0) {
      return decode(var0, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf var0, Base64Dialect var1) {
      ObjectUtil.checkNotNull(var0, "src");
      ByteBuf var2 = decode(var0, var0.readerIndex(), var0.readableBytes(), var1);
      var0.readerIndex(var0.writerIndex());
      return var2;
   }

   public static ByteBuf decode(ByteBuf var0, int var1, int var2) {
      return decode(var0, var1, var2, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf var0, int var1, int var2, Base64Dialect var3) {
      return decode(var0, var1, var2, var3, var0.alloc());
   }

   public static ByteBuf decode(ByteBuf var0, int var1, int var2, Base64Dialect var3, ByteBufAllocator var4) {
      ObjectUtil.checkNotNull(var0, "src");
      ObjectUtil.checkNotNull(var3, "dialect");
      return new Base64.Decoder().decode(var0, var1, var2, var4, var3);
   }

   static int decodedBufferSize(int var0) {
      return var0 - (var0 >>> 2);
   }

   private Base64() {
   }

   private static final class Decoder implements ByteProcessor {
      private final byte[] b4 = new byte[4];
      private int b4Posn;
      private byte[] decodabet;
      private int outBuffPosn;
      private ByteBuf dest;

      private Decoder() {
      }

      ByteBuf decode(ByteBuf var1, int var2, int var3, ByteBufAllocator var4, Base64Dialect var5) {
         this.dest = var4.buffer(Base64.decodedBufferSize(var3)).order(var1.order());
         this.decodabet = Base64.decodabet(var5);

         try {
            var1.forEachByte(var2, var3, this);
            return this.dest.slice(0, this.outBuffPosn);
         } catch (Throwable var7) {
            this.dest.release();
            PlatformDependent.throwException(var7);
            return null;
         }
      }

      @Override
      public boolean process(byte var1) throws Exception {
         if (var1 > 0) {
            byte var2 = this.decodabet[var1];
            if (var2 >= -5) {
               if (var2 >= -1) {
                  this.b4[this.b4Posn++] = var1;
                  if (this.b4Posn > 3) {
                     this.outBuffPosn = this.outBuffPosn + decode4to3(this.b4, this.dest, this.outBuffPosn, this.decodabet);
                     this.b4Posn = 0;
                     return var1 != 61;
                  }
               }

               return true;
            }
         }

         throw new IllegalArgumentException("invalid Base64 input character: " + (short)(var1 & 255) + " (decimal)");
      }

      private static int decode4to3(byte[] var0, ByteBuf var1, int var2, byte[] var3) {
         byte var4 = var0[0];
         byte var5 = var0[1];
         byte var6 = var0[2];
         if (var6 == 61) {
            int var15;
            try {
               var15 = (var3[var4] & 255) << 2 | (var3[var5] & 255) >>> 4;
            } catch (IndexOutOfBoundsException var11) {
               throw new IllegalArgumentException("not encoded in Base64");
            }

            var1.setByte(var2, var15);
            return 1;
         } else {
            byte var8 = var0[3];
            if (var8 == 61) {
               byte var16 = var3[var5];

               int var14;
               try {
                  if (var1.order() == ByteOrder.BIG_ENDIAN) {
                     var14 = ((var3[var4] & 63) << 2 | (var16 & 240) >> 4) << 8 | (var16 & 15) << 4 | (var3[var6] & 252) >>> 2;
                  } else {
                     var14 = (var3[var4] & 63) << 2 | (var16 & 240) >> 4 | ((var16 & 15) << 4 | (var3[var6] & 252) >>> 2) << 8;
                  }
               } catch (IndexOutOfBoundsException var12) {
                  throw new IllegalArgumentException("not encoded in Base64");
               }

               var1.setShort(var2, var14);
               return 2;
            } else {
               int var7;
               try {
                  if (var1.order() == ByteOrder.BIG_ENDIAN) {
                     var7 = (var3[var4] & 63) << 18 | (var3[var5] & 255) << 12 | (var3[var6] & 255) << 6 | var3[var8] & 255;
                  } else {
                     byte var9 = var3[var5];
                     byte var10 = var3[var6];
                     var7 = (var3[var4] & 63) << 2 | (var9 & 15) << 12 | (var9 & 240) >>> 4 | (var10 & 3) << 22 | (var10 & 252) << 6 | (var3[var8] & 255) << 16;
                  }
               } catch (IndexOutOfBoundsException var13) {
                  throw new IllegalArgumentException("not encoded in Base64");
               }

               var1.setMedium(var2, var7);
               return 3;
            }
         }
      }
   }
}
