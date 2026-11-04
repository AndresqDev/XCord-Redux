package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.URI;

public class WebSocketClientHandshaker13 extends WebSocketClientHandshaker {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketClientHandshaker13.class);
   public static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private String expectedChallengeResponseString;
   private final boolean allowExtensions;
   private final boolean performMasking;
   private final boolean allowMaskMismatch;

   public WebSocketClientHandshaker13(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, true, false);
   }

   public WebSocketClientHandshaker13(URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, 10000L);
   }

   public WebSocketClientHandshaker13(
      URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8, long var9
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, false);
   }

   WebSocketClientHandshaker13(
      URI var1, WebSocketVersion var2, String var3, boolean var4, HttpHeaders var5, int var6, boolean var7, boolean var8, long var9, boolean var11
   ) {
      super(var1, var2, var3, var5, var6, var9, var11);
      this.allowExtensions = var4;
      this.performMasking = var7;
      this.allowMaskMismatch = var8;
   }

   @Override
   protected FullHttpRequest newHandshakeRequest() {
      URI var1 = this.uri();
      byte[] var2 = WebSocketUtil.randomBytes(16);
      String var3 = WebSocketUtil.base64(var2);
      String var4 = var3 + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      byte[] var5 = WebSocketUtil.sha1(var4.getBytes(CharsetUtil.US_ASCII));
      this.expectedChallengeResponseString = WebSocketUtil.base64(var5);
      if (logger.isDebugEnabled()) {
         logger.debug("WebSocket version 13 client handshake key: {}, expected response: {}", var3, this.expectedChallengeResponseString);
      }

      DefaultFullHttpRequest var6 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(var1), Unpooled.EMPTY_BUFFER);
      HttpHeaders var7 = var6.headers();
      if (this.customHeaders != null) {
         var7.add(this.customHeaders);
         if (!var7.contains(HttpHeaderNames.HOST)) {
            var7.set(HttpHeaderNames.HOST, websocketHostValue(var1));
         }
      } else {
         var7.set(HttpHeaderNames.HOST, websocketHostValue(var1));
      }

      var7.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
         .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
         .set(HttpHeaderNames.SEC_WEBSOCKET_KEY, var3);
      if (!var7.contains(HttpHeaderNames.ORIGIN)) {
         var7.set(HttpHeaderNames.ORIGIN, websocketOriginValue(var1));
      }

      String var8 = this.expectedSubprotocol();
      if (var8 != null && !var8.isEmpty()) {
         var7.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, var8);
      }

      var7.set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, this.version().toAsciiString());
      return var6;
   }

   @Override
   protected void verify(FullHttpResponse var1) {
      HttpResponseStatus var2 = var1.status();
      if (!HttpResponseStatus.SWITCHING_PROTOCOLS.equals(var2)) {
         throw new WebSocketClientHandshakeException("Invalid handshake response getStatus: " + var2, var1);
      } else {
         HttpHeaders var3 = var1.headers();
         String var4 = var3.get(HttpHeaderNames.UPGRADE);
         if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(var4)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response upgrade: " + var4, var1);
         } else if (!var3.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketClientHandshakeException("Invalid handshake response connection: " + var3.get(HttpHeaderNames.CONNECTION), var1);
         } else {
            String var5 = var3.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
            if (var5 == null || !var5.equals(this.expectedChallengeResponseString)) {
               throw new WebSocketClientHandshakeException(
                  String.format("Invalid challenge. Actual: %s. Expected: %s", var5, this.expectedChallengeResponseString), var1
               );
            }
         }
      }
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket13FrameDecoder(false, this.allowExtensions, this.maxFramePayloadLength(), this.allowMaskMismatch);
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket13FrameEncoder(this.performMasking);
   }

   public WebSocketClientHandshaker13 setForceCloseTimeoutMillis(long var1) {
      super.setForceCloseTimeoutMillis(var1);
      return this;
   }
}
