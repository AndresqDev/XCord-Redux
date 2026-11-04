package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerFrameDeflateEncoder extends DeflateEncoder {
   PerFrameDeflateEncoder(int var1, int var2, boolean var3) {
      super(var1, var2, var3, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerFrameDeflateEncoder(int var1, int var2, boolean var3, WebSocketExtensionFilter var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public boolean acceptOutboundMessage(Object var1) throws Exception {
      if (!super.acceptOutboundMessage(var1)) {
         return false;
      } else {
         WebSocketFrame var2 = (WebSocketFrame)var1;
         return this.extensionEncoderFilter().mustSkip(var2)
            ? false
            : (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame || var1 instanceof ContinuationWebSocketFrame)
               && var2.content().readableBytes() > 0
               && (var2.rsv() & 4) == 0;
      }
   }

   @Override
   protected int rsv(WebSocketFrame var1) {
      return var1.rsv() | 4;
   }

   @Override
   protected boolean removeFrameTail(WebSocketFrame var1) {
      return true;
   }
}
