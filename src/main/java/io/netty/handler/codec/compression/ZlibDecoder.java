package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;

public abstract class ZlibDecoder extends ByteToMessageDecoder {
   protected final int maxAllocation;

   public ZlibDecoder() {
      this(0);
   }

   public ZlibDecoder(int var1) {
      this.maxAllocation = ObjectUtil.checkPositiveOrZero(var1, "maxAllocation");
   }

   public abstract boolean isClosed();

   protected ByteBuf prepareDecompressBuffer(ChannelHandlerContext var1, ByteBuf var2, int var3) {
      if (var2 == null) {
         return this.maxAllocation == 0 ? var1.alloc().heapBuffer(var3) : var1.alloc().heapBuffer(Math.min(var3, this.maxAllocation), this.maxAllocation);
      } else if (var2.ensureWritable(var3, true) == 1) {
         this.decompressionBufferExhausted(var2.duplicate());
         var2.skipBytes(var2.readableBytes());
         throw new DecompressionException("Decompression buffer has reached maximum size: " + var2.maxCapacity());
      } else {
         return var2;
      }
   }

   protected void decompressionBufferExhausted(ByteBuf var1) {
   }
}
