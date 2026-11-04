package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.jni.zlib.BungeeZlib;

public class CompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final int threshold;
   private final BungeeZlib zlib;

   public CompressionEncoder(int var1, BungeeZlib var2) {
      this.threshold = var1;
      this.zlib = var2;
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var4 = var2.readableBytes();
      if (var4 < this.threshold) {
         VarInts.writeVarInt(var3, 0);
         var3.writeBytes(var2);
      } else {
         VarInts.writeVarInt(var3, var4);
         boolean var5 = false;
         if (this.zlib.requiresDirectMemory() && !var2.hasMemoryAddress()) {
            ByteBuf var6 = var1.alloc().directBuffer(var2.readableBytes());
            var6.writeBytes(var2);
            var2 = var6;
            var5 = true;
         }

         try {
            this.zlib.deflate(var2, var3);
         } finally {
            if (var5) {
               var2.release();
            }
         }
      }
   }

   protected ByteBuf a(ChannelHandlerContext var1, ByteBuf var2, boolean var3) throws Exception {
      return this.zlib.requiresDirectMemory() ? var1.alloc().directBuffer(var2.readableBytes() + 1) : var1.alloc().heapBuffer(var2.readableBytes() + 1);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.zlib.free();
   }
}
