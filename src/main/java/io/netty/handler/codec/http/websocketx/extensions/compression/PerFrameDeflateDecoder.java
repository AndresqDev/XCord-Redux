package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;

class PerFrameDeflateDecoder extends DeflateDecoder {
   PerFrameDeflateDecoder(boolean var1) {
      super(var1, WebSocketExtensionFilter.NEVER_SKIP);
   }

   PerFrameDeflateDecoder(boolean var1, WebSocketExtensionFilter var2) {
      super(var1, var2);
   }

   @Override
   public boolean acceptInboundMessage(Object var1) throws Exception {
      if (!super.acceptInboundMessage(var1)) {
         return false;
      } else {
         WebSocketFrame var2 = (WebSocketFrame)var1;
         return this.extensionDecoderFilter().mustSkip(var2)
            ? false
            : (var1 instanceof TextWebSocketFrame || var1 instanceof BinaryWebSocketFrame || var1 instanceof ContinuationWebSocketFrame)
               && (var2.rsv() & 4) > 0;
      }
   }

   @Override
   protected int newRsv(WebSocketFrame var1) {
      return var1.rsv() ^ 4;
   }

   @Override
   protected boolean appendFrameTail(WebSocketFrame var1) {
      return true;
   }
}
