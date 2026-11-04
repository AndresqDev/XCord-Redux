package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.jni.zlib.BungeeZlib;
import net.md_5.bungee.protocol.MutedCorruptedFrameException;

public class CompressionDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final int threshold;
   private final BungeeZlib zlib;

   public CompressionDecoder(int var1, BungeeZlib var2) {
      this.threshold = var1;
      this.zlib = var2;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      int var4 = VarInts.readVarInt(var2);
      if (var4 == 0) {
         var3.add(var2.retainedSlice());
      } else {
         if (var4 > 2097152) {
            throw new MutedCorruptedFrameException("Packet exceeds 16MB in size: " + var4);
         }

         if (var4 < this.threshold) {
            throw new MutedCorruptedFrameException("Packet smaller then threshold " + var4 + " / " + this.threshold);
         }

         boolean var5 = false;
         ByteBuf var6;
         if (this.zlib.requiresDirectMemory()) {
            if (!var2.hasMemoryAddress()) {
               ByteBuf var7 = var1.alloc().directBuffer(var2.readableBytes());
               var7.writeBytes(var2);
               var2 = var7;
               var5 = true;
            }

            var6 = var1.alloc().directBuffer(var4);
         } else {
            var6 = var1.alloc().heapBuffer(var4);
         }

         try {
            this.zlib.inflate(var2, var6, var4);
            var3.add(var6);
         } catch (Exception var11) {
            var6.release();
            throw var11;
         } finally {
            if (var5) {
               var2.release();
            }
         }
      }
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.zlib.free();
   }
}
