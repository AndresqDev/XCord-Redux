package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class WebSocketServerHandshaker08 extends WebSocketServerHandshaker {
   public static final String WEBSOCKET_08_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

   public WebSocketServerHandshaker08(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerHandshaker08(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, WebSocketDecoderConfig.newBuilder().allowExtensions(var3).maxFramePayloadLength(var4).allowMaskMismatch(var5).build());
   }

   public WebSocketServerHandshaker08(String var1, String var2, WebSocketDecoderConfig var3) {
      super(WebSocketVersion.V08, var1, var2, var3);
   }

   @Override
   protected FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2) {
      String var3 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY);
      if (var3 == null) {
         throw new WebSocketServerHandshakeException("not a WebSocket request: missing key", var1);
      } else {
         DefaultFullHttpResponse var4 = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, var1.content().alloc().buffer(0)
         );
         if (var2 != null) {
            var4.headers().add(var2);
         }

         String var5 = var3 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
         byte[] var6 = WebSocketUtil.sha1(var5.getBytes(CharsetUtil.US_ASCII));
         String var7 = WebSocketUtil.base64(var6);
         if (logger.isDebugEnabled()) {
            logger.debug("WebSocket version 08 server handshake key: {}, response: {}", var3, var7);
         }

         var4.headers()
            .set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
            .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
            .set(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, var7);
         String var8 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
         if (var8 != null) {
            String var9 = this.selectSubprotocol(var8);
            if (var9 == null) {
               if (logger.isDebugEnabled()) {
                  logger.debug("Requested subprotocol(s) not supported: {}", var8);
               }
            } else {
               var4.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, var9);
            }
         }

         return var4;
      }
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket08FrameDecoder(this.decoderConfig());
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket08FrameEncoder(false);
   }
}
