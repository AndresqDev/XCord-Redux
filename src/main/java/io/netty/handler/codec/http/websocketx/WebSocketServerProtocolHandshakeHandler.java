package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
   private final WebSocketServerProtocolConfig serverConfig;
   private ChannelHandlerContext ctx;
   private ChannelPromise handshakePromise;
   private boolean isWebSocketPath;

   WebSocketServerProtocolHandshakeHandler(WebSocketServerProtocolConfig var1) {
      this.serverConfig = ObjectUtil.checkNotNull(var1, "serverConfig");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) {
      this.ctx = var1;
      this.handshakePromise = var1.newPromise();
   }

   @Override
   public void channelRead(final ChannelHandlerContext var1, Object var2) throws Exception {
      HttpObject var3 = (HttpObject)var2;
      if (var3 instanceof HttpRequest) {
         final HttpRequest var4 = (HttpRequest)var3;
         this.isWebSocketPath = this.isWebSocketPath(var4);
         if (!this.isWebSocketPath) {
            var1.fireChannelRead(var2);
            return;
         }

         try {
            if (HttpMethod.GET.equals(var4.method())) {
               WebSocketServerHandshakerFactory var5 = new WebSocketServerHandshakerFactory(
                  getWebSocketLocation(var1.pipeline(), var4, this.serverConfig.websocketPath()),
                  this.serverConfig.subprotocols(),
                  this.serverConfig.decoderConfig()
               );
               final WebSocketServerHandshaker var6 = var5.newHandshaker(var4);
               final ChannelPromise var7 = this.handshakePromise;
               if (var6 == null) {
                  WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(var1.channel());
               } else {
                  WebSocketServerProtocolHandler.setHandshaker(var1.channel(), var6);
                  var1.pipeline().remove(this);
                  ChannelFuture var8 = var6.handshake(var1.channel(), var4);
                  var8.addListener(
                     new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture var1x) {
                           if (!var1x.isSuccess()) {
                              var7.tryFailure(var1x.cause());
                              var1.fireExceptionCaught(var1x.cause());
                           } else {
                              var7.trySuccess();
                              var1.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                              var1.fireUserEventTriggered(
                                 new WebSocketServerProtocolHandler.HandshakeComplete(var4.uri(), var4.headers(), var6.selectedSubprotocol())
                              );
                           }
                        }
                     }
                  );
                  this.applyHandshakeTimeout();
               }

               return;
            }

            sendHttpResponse(var1, var4, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, var1.alloc().buffer(0)));
         } finally {
            ReferenceCountUtil.release(var4);
         }

         return;
      } else if (!this.isWebSocketPath) {
         var1.fireChannelRead(var2);
      } else {
         ReferenceCountUtil.release(var2);
      }
   }

   private boolean isWebSocketPath(HttpRequest var1) {
      String var2 = this.serverConfig.websocketPath();
      String var3 = var1.uri();
      boolean var4 = var3.startsWith(var2);
      boolean var5 = "/".equals(var2) || this.checkNextUri(var3, var2);
      return this.serverConfig.checkStartsWith() ? var4 && var5 : var3.equals(var2);
   }

   private boolean checkNextUri(String var1, String var2) {
      int var3 = var2.length();
      if (var1.length() <= var3) {
         return true;
      } else {
         char var4 = var1.charAt(var3);
         return var4 == '/' || var4 == '?';
      }
   }

   private static void sendHttpResponse(ChannelHandlerContext var0, HttpRequest var1, HttpResponse var2) {
      ChannelFuture var3 = var0.writeAndFlush(var2);
      if (!HttpUtil.isKeepAlive(var1) || var2.status().code() != 200) {
         var3.addListener(ChannelFutureListener.CLOSE);
      }
   }

   private static String getWebSocketLocation(ChannelPipeline var0, HttpRequest var1, String var2) {
      String var3 = "ws";
      if (var0.get(SslHandler.class) != null) {
         var3 = "wss";
      }

      String var4 = var1.headers().get(HttpHeaderNames.HOST);
      return var3 + "://" + var4 + var2;
   }

   private void applyHandshakeTimeout() {
      final ChannelPromise var1 = this.handshakePromise;
      long var2 = this.serverConfig.handshakeTimeoutMillis();
      if (var2 > 0L && !var1.isDone()) {
         final ScheduledFuture var4 = this.ctx
            .executor()
            .schedule(
               new Runnable() {
                  @Override
                  public void run() {
                     if (!var1.isDone() && var1.tryFailure(new WebSocketServerHandshakeException("handshake timed out"))) {
                        WebSocketServerProtocolHandshakeHandler.this.ctx
                           .flush()
                           .fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT)
                           .close();
                     }
                  }
               },
               var2,
               TimeUnit.MILLISECONDS
            );
         var1.addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<Void> var1) {
               var4.cancel(false);
            }
         });
      }
   }
}
