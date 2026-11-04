package io.netty.handler.codec.http.websocketx;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class WebSocketServerHandshakerFactory {
   private final String webSocketURL;
   private final String subprotocols;
   private final WebSocketDecoderConfig decoderConfig;

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3) {
      this(var1, var2, var3, 65536);
   }

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, false);
   }

   public WebSocketServerHandshakerFactory(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, WebSocketDecoderConfig.newBuilder().allowExtensions(var3).maxFramePayloadLength(var4).allowMaskMismatch(var5).build());
   }

   public WebSocketServerHandshakerFactory(String var1, String var2, WebSocketDecoderConfig var3) {
      this.webSocketURL = var1;
      this.subprotocols = var2;
      this.decoderConfig = ObjectUtil.checkNotNull(var3, "decoderConfig");
   }

   public WebSocketServerHandshaker newHandshaker(HttpRequest var1) {
      String var2 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
      if (var2 != null) {
         if (var2.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.decoderConfig);
         } else if (var2.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.decoderConfig);
         } else {
            return var2.equals(WebSocketVersion.V07.toHttpHeaderValue())
               ? new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.decoderConfig)
               : null;
         }
      } else {
         return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.decoderConfig);
      }
   }

   @Deprecated
   public static void sendUnsupportedWebSocketVersionResponse(Channel var0) {
      sendUnsupportedVersionResponse(var0);
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel var0) {
      return sendUnsupportedVersionResponse(var0, var0.newPromise());
   }

   public static ChannelFuture sendUnsupportedVersionResponse(Channel var0, ChannelPromise var1) {
      DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED, var0.alloc().buffer(0));
      var2.headers().set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
      HttpUtil.setContentLength(var2, 0L);
      return var0.writeAndFlush(var2, var1);
   }
}
