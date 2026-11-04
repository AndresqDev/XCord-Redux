package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameEncoder extends MessageToByteEncoder<ByteBuf> {
   private final int level;
   private final ByteBufChecksum checksum;

   public FastLzFrameEncoder() {
      this(0, null);
   }

   public FastLzFrameEncoder(int var1) {
      this(var1, null);
   }

   public FastLzFrameEncoder(boolean var1) {
      this(0, var1 ? new Adler32() : null);
   }

   public FastLzFrameEncoder(int var1, Checksum var2) {
      if (var1 != 0 && var1 != 1 && var1 != 2) {
         throw new IllegalArgumentException(String.format("level: %d (expected: %d or %d or %d)", var1, 0, 1, 2));
      } else {
         this.level = var1;
         this.checksum = var2 == null ? null : ByteBufChecksum.wrapChecksum(var2);
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      ByteBufChecksum var4 = this.checksum;

      while (var2.isReadable()) {
         int var5 = var2.readerIndex();
         int var6 = Math.min(var2.readableBytes(), 65535);
         int var7 = var3.writerIndex();
         var3.setMedium(var7, 4607066);
         int var8 = var7 + 4 + (var4 != null ? 4 : 0);
         byte var9;
         int var10;
         if (var6 < 32) {
            var9 = 0;
            var3.ensureWritable(var8 + 2 + var6);
            int var11 = var8 + 2;
            if (var4 != null) {
               var4.reset();
               var4.update(var2, var5, var6);
               var3.setInt(var7 + 4, (int)var4.getValue());
            }

            var3.setBytes(var11, var2, var5, var6);
            var10 = var6;
         } else {
            if (var4 != null) {
               var4.reset();
               var4.update(var2, var5, var6);
               var3.setInt(var7 + 4, (int)var4.getValue());
            }

            int var14 = FastLz.calculateOutputBufferLength(var6);
            var3.ensureWritable(var8 + 4 + var14);
            int var12 = var8 + 4;
            int var13 = FastLz.compress(var2, var2.readerIndex(), var6, var3, var12, this.level);
            if (var13 < var6) {
               var9 = 1;
               var10 = var13;
               var3.setShort(var8, var13);
               var8 += 2;
            } else {
               var9 = 0;
               var3.setBytes(var8 + 2, var2, var5, var6);
               var10 = var6;
            }
         }

         var3.setShort(var8, var6);
         var3.setByte(var7 + 3, var9 | (var4 != null ? 16 : 0));
         var3.writerIndex(var8 + 2 + var10);
         var2.skipBytes(var6);
      }
   }
}
