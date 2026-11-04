package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

public final class ZstdEncoder extends MessageToByteEncoder<ByteBuf> {
   private final int blockSize;
   private final int compressionLevel;
   private final int maxEncodeSize;
   private ByteBuf buffer;

   public ZstdEncoder() {
      this(3, 65536, 33554432);
   }

   public ZstdEncoder(int var1) {
      this(var1, 65536, 33554432);
   }

   public ZstdEncoder(int var1, int var2) {
      this(3, var1, var2);
   }

   public ZstdEncoder(int var1, int var2, int var3) {
      super(true);
      this.compressionLevel = ObjectUtil.checkInRange(var1, 0, 22, "compressionLevel");
      this.blockSize = ObjectUtil.checkPositive(var2, "blockSize");
      this.maxEncodeSize = ObjectUtil.checkPositive(var3, "maxEncodeSize");
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) {
      if (this.buffer == null) {
         throw new IllegalStateException("not added to a pipeline,or has been removed,buffer is null");
      } else {
         int var4 = var2.readableBytes() + this.buffer.readableBytes();
         if (var4 < 0) {
            throw new EncoderException("too much data to allocate a buffer for compression");
         } else {
            long var5 = 0L;

            while (var4 > 0) {
               int var7 = Math.min(this.blockSize, var4);
               var4 -= var7;
               var5 += com.github.luben.zstd.Zstd.compressBound((long)var7);
            }

            if (var5 <= (long)this.maxEncodeSize && 0L <= var5) {
               return var1.alloc().directBuffer((int)var5);
            } else {
               throw new EncoderException(
                  "requested encode buffer size (" + var5 + " bytes) exceeds the maximum allowable size (" + this.maxEncodeSize + " bytes)"
               );
            }
         }
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) {
      if (this.buffer == null) {
         throw new IllegalStateException("not added to a pipeline,or has been removed,buffer is null");
      } else {
         ByteBuf var4 = this.buffer;

         int var5;
         while ((var5 = var2.readableBytes()) > 0) {
            int var6 = Math.min(var5, var4.writableBytes());
            var2.readBytes(var4, var6);
            if (!var4.isWritable()) {
               this.flushBufferedData(var3);
            }
         }
      }
   }

   private void flushBufferedData(ByteBuf var1) {
      int var2 = this.buffer.readableBytes();
      if (var2 != 0) {
         int var3 = (int)com.github.luben.zstd.Zstd.compressBound((long)var2);
         var1.ensureWritable(var3);
         int var4 = var1.writerIndex();

         int var5;
         try {
            ByteBuffer var6 = var1.internalNioBuffer(var4, var1.writableBytes());
            var5 = com.github.luben.zstd.Zstd.compress(var6, this.buffer.internalNioBuffer(this.buffer.readerIndex(), var2), this.compressionLevel);
         } catch (Exception var7) {
            throw new CompressionException(var7);
         }

         var1.writerIndex(var4 + var5);
         this.buffer.clear();
      }
   }

   @Override
   public void flush(ChannelHandlerContext var1) {
      if (this.buffer != null && this.buffer.isReadable()) {
         ByteBuf var2 = this.allocateBuffer(var1, Unpooled.EMPTY_BUFFER, this.isPreferDirect());
         this.flushBufferedData(var2);
         var1.write(var2);
      }

      var1.flush();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) {
      this.buffer = var1.alloc().directBuffer(this.blockSize);
      this.buffer.clear();
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved(var1);
      if (this.buffer != null) {
         this.buffer.release();
         this.buffer = null;
      }
   }
}
