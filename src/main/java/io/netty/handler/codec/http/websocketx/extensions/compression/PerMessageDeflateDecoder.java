package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import java.util.List;

class PerMessageDeflateDecoder extends DeflateDecoder {
   private boolean compressing;

   PerMessageDeflateDecoder(boolean var1) {
      super(var1, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerMessageDeflateDecoder(boolean var1, WebSocketExtensionFilter var2) {
      super(var1, var2);
   }

   @Override
   public boolean acceptInboundMessage(Object var1) throws Exception {
      if (!super.acceptInboundMessage(var1)) {
         return false;
      } else {
         WebSocketFrame var2 = (WebSocketFrame)var1;
         if (this.extensionDecoderFilter().mustSkip(var2)) {
            if (this.compressing) {
               throw new IllegalStateException("Cannot skip per message deflate decoder, compression in progress");
            } else {
               return false;
            }
         } else {
            return (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) && (var2.rsv() & 4) > 0
               || var2 instanceof ContinuationWebSocketFrame && this.compressing;
         }
      }
   }

   @Override
   protected int newRsv(WebSocketFrame var1) {
      return (var1.rsv() & 4) > 0 ? var1.rsv() ^ 4 : var1.rsv();
   }

   @Override
   protected boolean appendFrameTail(WebSocketFrame var1) {
      return var1.isFinalFragment();
   }

   @Override
   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      super.decode(var1, var2, var3);
      if (var2.isFinalFragment()) {
         this.compressing = false;
      } else if (var2 instanceof TextWebSocketFrame || var2 instanceof BinaryWebSocketFrame) {
         this.compressing = true;
      }
   }
}
