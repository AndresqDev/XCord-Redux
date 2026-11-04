package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageDecoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
   }

   protected MessageToMessageDecoder(Class<? extends I> var1) {
      this.matcher = TypeParameterMatcher.get(var1);
   }

   public boolean acceptInboundMessage(Object var1) throws Exception {
      return this.matcher.match(var1);
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      CodecOutputList var3 = CodecOutputList.newInstance();
      boolean var29 = false /* VF: Semaphore variable */;

      try {
         var29 = true;
         if (this.acceptInboundMessage(var2)) {
            Object var4 = var2;

            try {
               this.decode(var1, (I)var4, var3);
            } finally {
               ReferenceCountUtil.release(var2);
            }

            var29 = false;
         } else {
            var3.add(var2);
            var29 = false;
         }
      } catch (DecoderException var37) {
         throw var37;
      } catch (Exception var38) {
         throw new DecoderException(var38);
      } finally {
         if (var29) {
            try {
               int var8 = var3.size();

               for (int var9 = 0; var9 < var8; var9++) {
                  var1.fireChannelRead(var3.getUnsafe(var9));
               }
            } finally {
               var3.recycle();
            }
         }
      }

      try {
         int var42 = var3.size();

         for (int var5 = 0; var5 < var42; var5++) {
            var1.fireChannelRead(var3.getUnsafe(var5));
         }
      } finally {
         var3.recycle();
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, I var2, List<Object> var3) throws Exception;
}
