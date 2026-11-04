package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import com.ning.compress.lzf.LZFChunk;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final int MIN_BLOCK_TO_COMPRESS = 16;
   private final int compressThreshold;
   private final ChunkEncoder encoder;
   private final BufferRecycler recycler;

   public LzfEncoder() {
      this(false);
   }

   public LzfEncoder(boolean var1) {
      this(var1, 65535);
   }

   public LzfEncoder(boolean var1, int var2) {
      this(var1, var2, 16);
   }

   public LzfEncoder(int var1) {
      this(false, var1);
   }

   public LzfEncoder(boolean var1, int var2, int var3) {
      super(false);
      if (var2 < 16 || var2 > 65535) {
         throw new IllegalArgumentException("totalLength: " + var2 + " (expected: " + 16 + '-' + '\uffff' + ')');
      } else if (var3 < 16) {
         throw new IllegalArgumentException("compressThreshold:" + var3 + " expected >=" + 16);
      } else {
         this.compressThreshold = var3;
         this.encoder = var1 ? ChunkEncoderFactory.safeNonAllocatingInstance(var2) : ChunkEncoderFactory.optimalNonAllocatingInstance(var2);
         this.recycler = BufferRecycler.instance();
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var4 = var2.readableBytes();
      int var5 = var2.readerIndex();
      byte[] var6;
      int var7;
      if (var2.hasArray()) {
         var6 = var2.array();
         var7 = var2.arrayOffset() + var5;
      } else {
         var6 = this.recycler.allocInputBuffer(var4);
         var2.getBytes(var5, var6, 0, var4);
         var7 = 0;
      }

      int var8 = LZFEncoder.estimateMaxWorkspaceSize(var4) + 1;
      var3.ensureWritable(var8);
      byte[] var9;
      int var10;
      if (var3.hasArray()) {
         var9 = var3.array();
         var10 = var3.arrayOffset() + var3.writerIndex();
      } else {
         var9 = new byte[var8];
         var10 = 0;
      }

      int var11;
      if (var4 >= this.compressThreshold) {
         var11 = this.encodeCompress(var6, var7, var4, var9, var10);
      } else {
         var11 = encodeNonCompress(var6, var7, var4, var9, var10);
      }

      if (var3.hasArray()) {
         var3.writerIndex(var3.writerIndex() + var11);
      } else {
         var3.writeBytes(var9, 0, var11);
      }

      var2.skipBytes(var4);
      if (!var2.hasArray()) {
         this.recycler.releaseInputBuffer(var6);
      }
   }

   private int encodeCompress(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      return LZFEncoder.appendEncoded(this.encoder, var1, var2, var3, var4, var5) - var5;
   }

   private static int lzfEncodeNonCompress(byte[] var0, int var1, int var2, byte[] var3, int var4) {
      int var6 = Math.min(65535, var2);
      var4 = LZFChunk.appendNonCompressed(var0, var1, var6, var3, var4);
      int var5 = var2 - var6;
      if (var5 < 1) {
         return var4;
      } else {
         var1 += var6;

         do {
            var6 = Math.min(var5, 65535);
            var4 = LZFChunk.appendNonCompressed(var0, var1, var6, var3, var4);
            var1 += var6;
            var5 -= var6;
         } while (var5 > 0);

         return var4;
      }
   }

   private static int encodeNonCompress(byte[] var0, int var1, int var2, byte[] var3, int var4) {
      return lzfEncodeNonCompress(var0, var1, var2, var3, var4) - var4;
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.encoder.close();
      super.handlerRemoved(var1);
   }
}
