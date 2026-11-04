package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
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
import io.netty.util.internal.PlatformDependent;
import java.net.URI;
import java.nio.ByteBuffer;

public class WebSocketClientHandshaker00 extends WebSocketClientHandshaker {
   private ByteBuf expectedChallengeResponseBytes;

   public WebSocketClientHandshaker00(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5) {
      this(var1, var2, var3, var4, var5, 10000L);
   }

   public WebSocketClientHandshaker00(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5, long var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   WebSocketClientHandshaker00(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5, long var6, boolean var8) {
      super(var1, var2, var3, var4, var5, var6, var8);
   }

   @Override
   protected FullHttpRequest newHandshakeRequest() {
      int var1 = WebSocketUtil.randomNumber(1, 12);
      int var2 = WebSocketUtil.randomNumber(1, 12);
      int var3 = Integer.MAX_VALUE / var1;
      int var4 = Integer.MAX_VALUE / var2;
      int var5 = WebSocketUtil.randomNumber(0, var3);
      int var6 = WebSocketUtil.randomNumber(0, var4);
      int var7 = var5 * var1;
      int var8 = var6 * var2;
      String var9 = Integer.toString(var7);
      String var10 = Integer.toString(var8);
      var9 = insertRandomCharacters(var9);
      var10 = insertRandomCharacters(var10);
      var9 = insertSpaces(var9, var1);
      var10 = insertSpaces(var10, var2);
      byte[] var11 = WebSocketUtil.randomBytes(8);
      ByteBuffer var12 = ByteBuffer.allocate(4);
      var12.putInt(var5);
      byte[] var13 = var12.array();
      var12 = ByteBuffer.allocate(4);
      var12.putInt(var6);
      byte[] var14 = var12.array();
      byte[] var15 = new byte[16];
      System.arraycopy(var13, 0, var15, 0, 4);
      System.arraycopy(var14, 0, var15, 4, 4);
      System.arraycopy(var11, 0, var15, 8, 8);
      this.expectedChallengeResponseBytes = Unpooled.wrappedBuffer(WebSocketUtil.md5(var15));
      URI var16 = this.uri();
      DefaultFullHttpRequest var17 = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.upgradeUrl(var16), Unpooled.wrappedBuffer(var11));
      HttpHeaders var18 = var17.headers();
      if (this.customHeaders != null) {
         var18.add(this.customHeaders);
      }

      var18.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
         .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
         .set(HttpHeaderNames.HOST, websocketHostValue(var16))
         .set(HttpHeaderNames.SEC_WEBSOCKET_KEY1, var9)
         .set(HttpHeaderNames.SEC_WEBSOCKET_KEY2, var10);
      if (!var18.contains(HttpHeaderNames.ORIGIN)) {
         var18.set(HttpHeaderNames.ORIGIN, websocketOriginValue(var16));
      }

      String var19 = this.expectedSubprotocol();
      if (var19 != null && !var19.isEmpty()) {
         var18.set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, var19);
      }

      var18.set(HttpHeaderNames.CONTENT_LENGTH, var11.length);
      return var17;
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
            ByteBuf var5 = var1.content();
            if (!var5.equals(this.expectedChallengeResponseBytes)) {
               throw new WebSocketClientHandshakeException("Invalid challenge", var1);
            }
         }
      }
   }

   private static String insertRandomCharacters(String var0) {
      int var1 = WebSocketUtil.randomNumber(1, 12);
      char[] var2 = new char[var1];
      int var3 = 0;

      while (var3 < var1) {
         int var4 = PlatformDependent.threadLocalRandom().nextInt(126) + 33;
         if (33 < var4 && var4 < 47 || 58 < var4 && var4 < 126) {
            var2[var3] = (char)var4;
            var3++;
         }
      }

      for (int var8 = 0; var8 < var1; var8++) {
         int var5 = WebSocketUtil.randomNumber(0, var0.length());
         String var6 = var0.substring(0, var5);
         String var7 = var0.substring(var5);
         var0 = var6 + var2[var8] + var7;
      }

      return var0;
   }

   private static String insertSpaces(String var0, int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         int var3 = WebSocketUtil.randomNumber(1, var0.length() - 1);
         String var4 = var0.substring(0, var3);
         String var5 = var0.substring(var3);
         var0 = var4 + ' ' + var5;
      }

      return var0;
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket00FrameDecoder(this.maxFramePayloadLength());
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket00FrameEncoder();
   }

   public WebSocketClientHandshaker00 setForceCloseTimeoutMillis(long var1) {
      super.setForceCloseTimeoutMillis(var1);
      return this;
   }
}
