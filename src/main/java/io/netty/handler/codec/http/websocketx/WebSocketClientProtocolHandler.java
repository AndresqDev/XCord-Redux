package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;
import java.util.List;

public class WebSocketClientProtocolHandler extends WebSocketProtocolHandler {
   private final WebSocketClientHandshaker handshaker;
   private final WebSocketClientProtocolConfig clientConfig;

   public WebSocketClientHandshaker handshaker() {
      return this.handshaker;
   }

   public WebSocketClientProtocolHandler(WebSocketClientProtocolConfig var1) {
      super(ObjectUtil.checkNotNull(var1, "clientConfig").dropPongFrames(), var1.sendCloseFrame(), var1.forceCloseTimeoutMillis());
      this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(
         var1.webSocketUri(),
         var1.version(),
         var1.subprotocol(),
         var1.allowExtensions(),
         var1.customHeaders(),
         var1.maxFramePayloadLength(),
         var1.performMasking(),
         var1.allowMaskMismatch(),
         var1.forceCloseTimeoutMillis(),
         var1.absoluteUpgradeUrl()
      );
      this.clientConfig = var1;
   }

   public WebSocketClientProtocolHandler(
      URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8, boolean var9
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, 10000L);
   }

   public WebSocketClientProtocolHandler(
      URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8, boolean var9, long var10
   ) {
      this(WebSocketClientHandshakerFactory.newHandshaker(var1, var2, var3, var4, var5, var6, var8, var9), var7, var10);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, 10000L);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, long var8) {
      this(var1, var2, var3, var4, var5, var6, var7, true, false, var8);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, 10000L);
   }

   public WebSocketClientProtocolHandler(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, long var7) {
      this(var1, var2, var3, var4, var5, var6, true, var7);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, boolean var2) {
      this(var1, var2, 10000L);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, boolean var2, long var3) {
      this(var1, var2, true, var3);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, boolean var2, boolean var3) {
      this(var1, var2, var3, 10000L);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, boolean var2, boolean var3, long var4) {
      super(var3);
      this.handshaker = var1;
      this.clientConfig = WebSocketClientProtocolConfig.newBuilder().handleCloseFrames(var2).handshakeTimeoutMillis(var4).build();
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1) {
      this(var1, 10000L);
   }

   public WebSocketClientProtocolHandler(WebSocketClientHandshaker var1, long var2) {
      this(var1, true, var2);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (this.clientConfig.handleCloseFrames() && var2 instanceof CloseWebSocketFrame) {
         var1.close();
      } else {
         super.decode(var1, var2, var3);
      }
   }

   protected WebSocketClientHandshakeException buildHandshakeException(String var1) {
      return new WebSocketClientHandshakeException(var1);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      if (var2.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
         var1.pipeline()
            .addBefore(
               var1.name(),
               WebSocketClientProtocolHandshakeHandler.class.getName(),
               new WebSocketClientProtocolHandshakeHandler(this.handshaker, this.clientConfig.handshakeTimeoutMillis())
            );
      }

      if (var2.get(Utf8FrameValidator.class) == null) {
         var1.pipeline().addBefore(var1.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator());
      }
   }

   public static enum ClientHandshakeStateEvent {
      HANDSHAKE_TIMEOUT,
      HANDSHAKE_ISSUED,
      HANDSHAKE_COMPLETE;
   }
}
