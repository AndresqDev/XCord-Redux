package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;

final class DnsCodecUtil {
   private DnsCodecUtil() {
   }

   static void encodeDomainName(String var0, ByteBuf var1) {
      if (".".equals(var0)) {
         var1.writeByte(0);
      } else {
         String[] var2 = var0.split("\\.");

         for (String var6 : var2) {
            int var7 = var6.length();
            if (var7 == 0) {
               break;
            }

            var1.writeByte(var7);
            ByteBufUtil.writeAscii(var1, var6);
         }

         var1.writeByte(0);
      }
   }

   static String decodeDomainName(ByteBuf var0) {
      int var1 = -1;
      byte var2 = 0;
      int var3 = var0.writerIndex();
      int var4 = var0.readableBytes();
      if (var4 == 0) {
         return ".";
      } else {
         StringBuilder var5 = new StringBuilder(var4 << 1);

         while (var0.isReadable()) {
            short var6 = var0.readUnsignedByte();
            boolean var7 = (var6 & 192) == 192;
            if (var7) {
               if (var1 == -1) {
                  var1 = var0.readerIndex() + 1;
               }

               if (!var0.isReadable()) {
                  throw new CorruptedFrameException("truncated pointer in a name");
               }

               int var8 = (var6 & 63) << 8 | var0.readUnsignedByte();
               if (var8 >= var3) {
                  throw new CorruptedFrameException("name has an out-of-range pointer");
               }

               var0.readerIndex(var8);
               var2 += 2;
               if (var2 >= var3) {
                  throw new CorruptedFrameException("name contains a loop.");
               }
            } else {
               if (var6 == 0) {
                  break;
               }

               if (!var0.isReadable(var6)) {
                  throw new CorruptedFrameException("truncated label in a name");
               }

               var5.append(var0.toString(var0.readerIndex(), var6, CharsetUtil.UTF_8)).append('.');
               var0.skipBytes(var6);
            }
         }

         if (var1 != -1) {
            var0.readerIndex(var1);
         }

         if (var5.length() == 0) {
            return ".";
         } else {
            if (var5.charAt(var5.length() - 1) != '.') {
               var5.append('.');
            }

            return var5.toString();
         }
      }
   }

   static ByteBuf decompressDomainName(ByteBuf var0) {
      String var1 = decodeDomainName(var0);
      ByteBuf var2 = var0.alloc().buffer(var1.length() << 1);
      encodeDomainName(var1, var2);
      return var2;
   }
}
