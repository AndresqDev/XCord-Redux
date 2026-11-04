package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.regex.Pattern;

public class WebSocketServerHandshaker00 extends WebSocketServerHandshaker {
   private static final Pattern BEGINNING_DIGIT = Pattern.compile("[^0-9]");
   private static final Pattern BEGINNING_SPACE = Pattern.compile("[^ ]");

   public WebSocketServerHandshaker00(String var1, String var2, int var3) {
      this(var1, var2, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(var3).build());
   }

   public WebSocketServerHandshaker00(String var1, String var2, WebSocketDecoderConfig var3) {
      super(WebSocketVersion.V00, var1, var2, var3);
   }

   @Override
   protected FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2) {
      if (var1.headers().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)
         && HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(var1.headers().get(HttpHeaderNames.UPGRADE))) {
         boolean var3 = var1.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY1) && var1.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
         String var4 = var1.headers().get(HttpHeaderNames.ORIGIN);
         if (var4 == null && !var3) {
            throw new WebSocketServerHandshakeException("Missing origin header, got only " + var1.headers().names(), var1);
         } else {
            DefaultFullHttpResponse var5 = new DefaultFullHttpResponse(
               HttpVersion.HTTP_1_1,
               new HttpResponseStatus(101, var3 ? "WebSocket Protocol Handshake" : "Web Socket Protocol Handshake"),
               var1.content().alloc().buffer(0)
            );
            if (var2 != null) {
               var5.headers().add(var2);
            }

            var5.headers().set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET).set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
            if (var3) {
               var5.headers().add(HttpHeaderNames.SEC_WEBSOCKET_ORIGIN, var4);
               var5.headers().add(HttpHeaderNames.SEC_WEBSOCKET_LOCATION, this.uri());
               String var6 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
               if (var6 != null) {
                  String var7 = this.selectSubprotocol(var6);
                  if (var7 == null) {
                     if (logger.isDebugEnabled()) {
                        logger.debug("Requested subprotocol(s) not supported: {}", var6);
                     }
                  } else {
                     var5.headers().set(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL, var7);
                  }
               }

               String var15 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY1);
               String var8 = var1.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY2);
               int var9 = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(var15).replaceAll("")) / (long)BEGINNING_SPACE.matcher(var15).replaceAll("").length());
               int var10 = (int)(Long.parseLong(BEGINNING_DIGIT.matcher(var8).replaceAll("")) / (long)BEGINNING_SPACE.matcher(var8).replaceAll("").length());
               long var11 = var1.content().readLong();
               ByteBuf var13 = Unpooled.wrappedBuffer(new byte[16]).setIndex(0, 0);
               var13.writeInt(var9);
               var13.writeInt(var10);
               var13.writeLong(var11);
               var5.content().writeBytes(WebSocketUtil.md5(var13.array()));
            } else {
               var5.headers().add(HttpHeaderNames.WEBSOCKET_ORIGIN, var4);
               var5.headers().add(HttpHeaderNames.WEBSOCKET_LOCATION, this.uri());
               String var14 = var1.headers().get(HttpHeaderNames.WEBSOCKET_PROTOCOL);
               if (var14 != null) {
                  var5.headers().set(HttpHeaderNames.WEBSOCKET_PROTOCOL, this.selectSubprotocol(var14));
               }
            }

            return var5;
         }
      } else {
         throw new WebSocketServerHandshakeException("not a WebSocket handshake request: missing upgrade", var1);
      }
   }

   @Override
   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      return var1.writeAndFlush(var2, var3);
   }

   @Override
   public ChannelFuture close(ChannelHandlerContext var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      return var1.writeAndFlush(var2, var3);
   }

   @Override
   protected WebSocketFrameDecoder newWebsocketDecoder() {
      return new WebSocket00FrameDecoder(this.decoderConfig());
   }

   @Override
   protected WebSocketFrameEncoder newWebSocketEncoder() {
      return new WebSocket00FrameEncoder();
   }
}
