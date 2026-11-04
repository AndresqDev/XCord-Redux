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

public class WebSocketServerHandshaker13 extends WebSocketServerHandshaker {
   public static final String WEBSOCKET_13_ACCEPT_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

   public WebSocketServerHandshaker13(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerHandshaker13(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, WebSocketDecoderConfig.newBuilder().allowExtensions(var3).maxFramePayloadLength(var4).allowMaskMismatch(var5).build());
   }

   public WebSocketServerHandshaker13(String var1, String var2, WebSocketDecoderConfig var3) {
      super(WebSocketVersion.V13, var1, var2, var3);
   }

   @Override
   protected FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2) {
      HttpHeaders var3 = var1.headers();
      if (!var3.contains(HttpHeaderNames.CONNECTION) || !var3.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
         throw new WebSocketServerHandshakeException("not a WebSocket request: a |Connection| header must includes a token 'Upgrade'", var1);
      } else if (!var3.contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true)) {
         throw new WebSocketServerHandshakeException("not a WebSocket request: a |Upgrade| header must containing the value 'websocket'", var1);
      } else {
         String var4 = var3.get(HttpHeaderNames.SEC_WEBSOCKET_KEY);
         if (var4 == null) {
            throw new WebSocketServerHandshakeException("not a WebSocket request: missing key", var1);
         } else {
            DefaultFullHttpResponse var5 = new DefaultFullHttpResponse(
               HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, var1.content().alloc().buffer(0)
            );
            if (var2 != null) {
               var5.headers().add(var2);
            }

            String var6 = var4 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            byte[] var7 = WebSocketUtil.sha1(var6.getBytes(CharsetUtil.US_ASCII));
            String var8 = WebSocketUtil.base64(var7);
            if (logger.isDebugEnabled()) {
               logger.debug("WebSocket version 13 server handshake key: {}, response: {}", var4, var8);
            }

            var5.headers()
               .set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
               .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
               .set(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT, var8);
            String var9 = var3.get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
            if (var9 != null) {
               String var10 = this.selectSubprotocol(var9);
               if (var10 == null) {
                  if (logger.isDebugEnabled()) {
                     logger.debug("Requested subprotocol(s) not supported: {}", var9);
                  }
               } else {
                  var5.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, var10);
               }
            }

            return var5;
         }
      }
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket13FrameDecoder(this.decoderConfig());
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket13FrameEncoder(false);
   }
}
