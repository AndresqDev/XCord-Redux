package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder extends SpdyHeaderBlockRawEncoder {
   private final Deflater compressor;
   private boolean finished;

   SpdyHeaderBlockZlibEncoder(SpdyVersion var1, int var2) {
      super(var1);
      if (var2 >= 0 && var2 <= 9) {
         this.compressor = new Deflater(var2);
         this.compressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var2 + " (expected: 0-9)");
      }
   }

   private int setInput(ByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var1.hasArray()) {
         this.compressor.setInput(var1.array(), var1.arrayOffset() + var1.readerIndex(), var2);
      } else {
         byte[] var3 = new byte[var2];
         var1.getBytes(var1.readerIndex(), var3);
         this.compressor.setInput(var3, 0, var3.length);
      }

      return var2;
   }

   private ByteBuf encode(ByteBufAllocator var1, int var2) {
      ByteBuf var3 = var1.heapBuffer(var2);
      boolean var4 = true;

      ByteBuf var5;
      try {
         while (this.compressInto(var3)) {
            var3.ensureWritable(var3.capacity() << 1);
         }

         var4 = false;
         var5 = var3;
      } finally {
         if (var4) {
            var3.release();
         }
      }

      return var5;
   }

   @SuppressJava6Requirement(
      reason = "Guarded by java version check"
   )
   private boolean compressInto(ByteBuf var1) {
      byte[] var2 = var1.array();
      int var3 = var1.arrayOffset() + var1.writerIndex();
      int var4 = var1.writableBytes();
      int var5;
      if (PlatformDependent.javaVersion() >= 7) {
         var5 = this.compressor.deflate(var2, var3, var4, 2);
      } else {
         var5 = this.compressor.deflate(var2, var3, var4);
      }

      var1.writerIndex(var1.writerIndex() + var5);
      return var5 == var4;
   }

   @Override
   public ByteBuf encode(ByteBufAllocator var1, SpdyHeadersFrame var2) throws Exception {
      ObjectUtil.checkNotNullWithIAE(var1, "alloc");
      ObjectUtil.checkNotNullWithIAE(var2, "frame");
      if (this.finished) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBuf var3 = super.encode(var1, var2);

         ByteBuf var4;
         try {
            if (var3.isReadable()) {
               int var9 = this.setInput(var3);
               return this.encode(var1, var9);
            }

            var4 = Unpooled.EMPTY_BUFFER;
         } finally {
            var3.release();
         }

         return var4;
      }
   }

   @Override
   public void end() {
      if (!this.finished) {
         this.finished = true;
         this.compressor.end();
         super.end();
      }
   }
}
