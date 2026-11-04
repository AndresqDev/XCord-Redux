package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

final class HttpPostBodyUtil {
   public static final int chunkSize = 8096;
   public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
   public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

   private HttpPostBodyUtil() {
   }

   static int findNonWhitespace(String var0, int var1) {
      int var2 = var1;

      while (var2 < var0.length() && Character.isWhitespace(var0.charAt(var2))) {
         var2++;
      }

      return var2;
   }

   static int findEndOfString(String var0) {
      int var1 = var0.length();

      while (var1 > 0 && Character.isWhitespace(var0.charAt(var1 - 1))) {
         var1--;
      }

      return var1;
   }

   static int findLineBreak(ByteBuf var0, int var1) {
      int var2 = var0.readableBytes() - (var1 - var0.readerIndex());
      int var3 = var0.bytesBefore(var1, var2, (byte)10);
      if (var3 == -1) {
         return -1;
      } else {
         if (var3 > 0 && var0.getByte(var1 + var3 - 1) == 13) {
            var3--;
         }

         return var3;
      }
   }

   static int findLastLineBreak(ByteBuf var0, int var1) {
      int var2 = findLineBreak(var0, var1);
      byte var3 = 0;
      if (var2 >= 0) {
         if (var0.getByte(var1 + var2) == 13) {
            var3 = 2;
         } else {
            var3 = 1;
         }

         var2 += var3;
      }

      int var4;
      while (var2 > 0 && (var4 = findLineBreak(var0, var1 + var2)) >= 0) {
         var2 += var4;
         if (var0.getByte(var1 + var2) == 13) {
            var3 = 2;
         } else {
            var3 = 1;
         }

         var2 += var3;
      }

      return var2 - var3;
   }

   static int findDelimiter(ByteBuf var0, int var1, byte[] var2, boolean var3) {
      int var4 = var2.length;
      int var5 = var0.readerIndex();
      int var6 = var0.writerIndex();
      int var7 = var6 - var1;
      int var8 = var1;
      boolean var9 = true;

      while (var9 && var4 <= var7) {
         int var10 = var0.bytesBefore(var8, var7, var2[0]);
         if (var10 < 0) {
            return -1;
         }

         var8 += var10;
         var7 -= var10;
         if (var7 >= var4) {
            var9 = false;

            for (int var11 = 0; var11 < var4; var11++) {
               if (var0.getByte(var8 + var11) != var2[var11]) {
                  var8++;
                  var7--;
                  var9 = true;
                  break;
               }
            }
         }

         if (!var9) {
            if (var3 && var8 > var5) {
               if (var0.getByte(var8 - 1) != 10) {
                  var8++;
                  var7--;
                  var9 = true;
                  continue;
               }

               var8--;
               if (var8 > var5 && var0.getByte(var8 - 1) == 13) {
                  var8--;
               }
            }

            return var8 - var5;
         }
      }

      return -1;
   }

   static class SeekAheadOptimize {
      byte[] bytes;
      int readerIndex;
      int pos;
      int origPos;
      int limit;
      ByteBuf buffer;

      SeekAheadOptimize(ByteBuf var1) {
         if (!var1.hasArray()) {
            throw new IllegalArgumentException("buffer hasn't backing byte array");
         } else {
            this.buffer = var1;
            this.bytes = var1.array();
            this.readerIndex = var1.readerIndex();
            this.origPos = this.pos = var1.arrayOffset() + this.readerIndex;
            this.limit = var1.arrayOffset() + var1.writerIndex();
         }
      }

      void setReadPosition(int var1) {
         this.pos -= var1;
         this.readerIndex = this.getReadPosition(this.pos);
         this.buffer.readerIndex(this.readerIndex);
      }

      int getReadPosition(int var1) {
         return var1 - this.origPos + this.readerIndex;
      }
   }

   public static enum TransferEncodingMechanism {
      BIT7("7bit"),
      BIT8("8bit"),
      BINARY("binary");

      private final String value;

      private TransferEncodingMechanism(String var3) {
         this.value = var3;
      }

      public String value() {
         return this.value;
      }

      @Override
      public String toString() {
         return this.value;
      }
   }
}
