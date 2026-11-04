package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import java.util.List;

class PerMessageDeflateEncoder extends DeflateEncoder {
   private boolean compressing;

   PerMessageDeflateEncoder(int var1, int var2, boolean var3) {
      super(var1, var2, var3, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerMessageDeflateEncoder(int var1, int var2, boolean var3, WebSocketExtensionFilter var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public boolean acceptOutboundMessage(Object var1) throws Exception {
      if (!super.acceptOutboundMessage(var1)) {
         return false;
      } else {
         WebSocketFrame var2 = (WebSocketFrame)var1;
         if (this.extensionEncoderFilter().mustSkip(var2)) {
            if (this.compressing) {
               throw new IllegalStateException("Cannot skip per message deflate encoder, compression in progress");
            } else {
               return false;
            }
         } else {
            return (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) && (var2.rsv() & 4) == 0
               || var2 instanceof ContinuationWebSocketFrame && this.compressing;
         }
      }
   }

   @Override
   protected int rsv(WebSocketFrame var1) {
      return !(var1 instanceof TextWebSocketFrame) && !(var1 instanceof BinaryWebSocketFrame) ? var1.rsv() : var1.rsv() | 4;
   }

   @Override
   protected boolean removeFrameTail(WebSocketFrame var1) {
      return var1.isFinalFragment();
   }

   @Override
   protected void encode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      super.encode(var1, var2, var3);
      if (var2.isFinalFragment()) {
         this.compressing = false;
      } else if (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }
   }
}
