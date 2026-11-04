package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoders;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;

@ChannelHandler.Sharable
public final class BrotliEncoder extends MessageToByteEncoder<ByteBuf> {
   private final Parameters parameters;

   public BrotliEncoder() {
      this(BrotliOptions.DEFAULT);
   }

   public BrotliEncoder(Parameters var1) {
      this.parameters = ObjectUtil.checkNotNull(var1, "Parameters");
   }

   public BrotliEncoder(BrotliOptions var1) {
      this(var1.parameters());
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, ByteBuf var2, boolean var3) throws Exception {
      if (!var2.isReadable()) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         try {
            ByteBuf var4;
            if (var3) {
               var4 = var1.alloc().ioBuffer();
            } else {
               var4 = var1.alloc().buffer();
            }

            Encoders.compress(var2, var4, this.parameters);
            return var4;
         } catch (Exception var5) {
            ReferenceCountUtil.release(var2);
            throw var5;
         }
      }
   }
}
