package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler {
   private static final AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
   private final WebSocketServerProtocolConfig serverConfig;

   public WebSocketServerProtocolHandler(WebSocketServerProtocolConfig var1) {
      super(ObjectUtil.checkNotNull(var1, "serverConfig").dropPongFrames(), var1.sendCloseFrame(), var1.forceCloseTimeoutMillis());
      this.serverConfig = var1;
   }

   public WebSocketServerProtocolHandler(String var1) {
      this(var1, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, long var2) {
      this(var1, false, var2);
   }

   public WebSocketServerProtocolHandler(String var1, boolean var2) {
      this(var1, var2, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, boolean var2, long var3) {
      this(var1, null, false, 65536, false, var2, var3);
   }

   public WebSocketServerProtocolHandler(String var1, String var2) {
      this(var1, var2, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, long var3) {
      this(var1, var2, false, var3);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3) {
      this(var1, var2, var3, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, long var4) {
      this(var1, var2, var3, 65536, var4);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4) {
      this(var1, var2, var3, var4, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, long var5) {
      this(var1, var2, var3, var4, false, var5);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, long var6) {
      this(var1, var2, var3, var4, var5, false, var6);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6) {
      this(var1, var2, var3, var4, var5, var6, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6, long var7) {
      this(var1, var2, var3, var4, var5, var6, true, var7);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, var7, 10000L);
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, int var4, boolean var5, boolean var6, boolean var7, long var8) {
      this(var1, var2, var6, var7, var8, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(var4).allowMaskMismatch(var5).allowExtensions(var3).build());
   }

   public WebSocketServerProtocolHandler(String var1, String var2, boolean var3, boolean var4, long var5, WebSocketDecoderConfig var7) {
      this(
         WebSocketServerProtocolConfig.newBuilder()
            .websocketPath(var1)
            .subprotocols(var2)
            .checkStartsWith(var3)
            .handshakeTimeoutMillis(var5)
            .dropPongFrames(var4)
            .decoderConfig(var7)
            .build()
      );
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      if (var2.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
         var2.addBefore(var1.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.serverConfig));
      }

      if (this.serverConfig.decoderConfig().withUTF8Validator() && var2.get(Utf8FrameValidator.class) == null) {
         var2.addBefore(var1.name(), Utf8FrameValidator.class.getName(), new Utf8FrameValidator(this.serverConfig.decoderConfig().closeOnProtocolViolation()));
      }
   }

   @Override
   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (this.serverConfig.handleCloseFrames() && var2 instanceof CloseWebSocketFrame) {
         WebSocketServerHandshaker var4 = getHandshaker(var1.channel());
         if (var4 != null) {
            var2.retain();
            ChannelPromise var5 = var1.newPromise();
            this.closeSent(var5);
            var4.close(var1, (CloseWebSocketFrame)var2, var5);
         } else {
            var1.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
         }
      } else {
         super.decode(var1, var2, var3);
      }
   }

   protected WebSocketServerHandshakeException buildHandshakeException(String var1) {
      return new WebSocketServerHandshakeException(var1);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (var2 instanceof WebSocketHandshakeException) {
         DefaultFullHttpResponse var3 = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(var2.getMessage().getBytes())
         );
         var1.channel().writeAndFlush(var3).addListener(ChannelFutureListener.CLOSE);
      } else {
         var1.fireExceptionCaught(var2);
         var1.close();
      }
   }

   static WebSocketServerHandshaker getHandshaker(Channel var0) {
      return var0.attr(HANDSHAKER_ATTR_KEY).get();
   }

   static void setHandshaker(Channel var0, WebSocketServerHandshaker var1) {
      var0.attr(HANDSHAKER_ATTR_KEY).set(var1);
   }

   public static final class HandshakeComplete {
      private final String requestUri;
      private final HttpHeaders requestHeaders;
      private final String selectedSubprotocol;

      HandshakeComplete(String var1, HttpHeaders var2, String var3) {
         this.requestUri = var1;
         this.requestHeaders = var2;
         this.selectedSubprotocol = var3;
      }

      public String requestUri() {
         return this.requestUri;
      }

      public HttpHeaders requestHeaders() {
         return this.requestHeaders;
      }

      public String selectedSubprotocol() {
         return this.selectedSubprotocol;
      }
   }

   public static enum ServerHandshakeStateEvent {
      @Deprecated
      HANDSHAKE_COMPLETE,
      HANDSHAKE_TIMEOUT;
   }
}
