package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class WebSocketClientHandshaker {
   private static final String HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
   private static final String HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";
   protected static final int DEFAULT_FORCE_CLOSE_TIMEOUT_MILLIS = 10000;
   private final URI uri;
   private final WebSocketVersion version;
   private volatile boolean handshakeComplete;
   private volatile long forceCloseTimeoutMillis = 10000L;
   private volatile int forceCloseInit;
   private static final AtomicIntegerFieldUpdater<WebSocketClientHandshaker> FORCE_CLOSE_INIT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      WebSocketClientHandshaker.class, "forceCloseInit"
   );
   private volatile boolean forceCloseComplete;
   private final String expectedSubprotocol;
   private volatile String actualSubprotocol;
   protected final HttpHeaders customHeaders;
   private final int maxFramePayloadLength;
   private final boolean absoluteUpgradeUrl;

   protected WebSocketClientHandshaker(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5) {
      this(var1, var2, var3, var4, var5, 10000L);
   }

   protected WebSocketClientHandshaker(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5, long var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   protected WebSocketClientHandshaker(URI var1, WebSocketVersion var2, String var3, HttpHeaders var4, int var5, long var6, boolean var8) {
      this.uri = var1;
      this.version = var2;
      this.expectedSubprotocol = var3;
      this.customHeaders = var4;
      this.maxFramePayloadLength = var5;
      this.forceCloseTimeoutMillis = var6;
      this.absoluteUpgradeUrl = var8;
   }

   public URI uri() {
      return this.uri;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean isHandshakeComplete() {
      return this.handshakeComplete;
   }

   private void setHandshakeComplete() {
      this.handshakeComplete = true;
   }

   public String expectedSubprotocol() {
      return this.expectedSubprotocol;
   }

   public String actualSubprotocol() {
      return this.actualSubprotocol;
   }

   private void setActualSubprotocol(String var1) {
      this.actualSubprotocol = var1;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   protected boolean isForceCloseComplete() {
      return this.forceCloseComplete;
   }

   public WebSocketClientHandshaker setForceCloseTimeoutMillis(long var1) {
      this.forceCloseTimeoutMillis = var1;
      return this;
   }

   public ChannelFuture handshake(Channel var1) {
      ObjectUtil.checkNotNull(var1, "channel");
      return this.handshake(var1, var1.newPromise());
   }

   public final ChannelFuture handshake(Channel var1, final ChannelPromise var2) {
      ChannelPipeline var3 = var1.pipeline();
      HttpResponseDecoder var4 = var3.get(HttpResponseDecoder.class);
      if (var4 == null) {
         HttpClientCodec var5 = var3.get(HttpClientCodec.class);
         if (var5 == null) {
            var2.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
            return var2;
         }
      }

      FullHttpRequest var6 = this.newHandshakeRequest();
      var1.writeAndFlush(var6).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) {
            if (var1.isSuccess()) {
               ChannelPipeline var2x = var1.channel().pipeline();
               ChannelHandlerContext var3 = var2x.context(HttpRequestEncoder.class);
               if (var3 == null) {
                  var3 = var2x.context(HttpClientCodec.class);
               }

               if (var3 == null) {
                  var2.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec"));
                  return;
               }

               var2x.addAfter(var3.name(), "ws-encoder", WebSocketClientHandshaker.this.newWebSocketEncoder());
               var2.setSuccess();
            } else {
               var2.setFailure(var1.cause());
            }
         }
      });
      return var2;
   }

   protected abstract FullHttpRequest newHandshakeRequest();

   public final void finishHandshake(Channel var1, FullHttpResponse var2) {
      this.verify(var2);
      String var3 = var2.headers().get(HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL);
      var3 = var3 != null ? var3.trim() : null;
      String var4 = this.expectedSubprotocol != null ? this.expectedSubprotocol : "";
      boolean var5 = false;
      if (var4.isEmpty() && var3 == null) {
         var5 = true;
         this.setActualSubprotocol(this.expectedSubprotocol);
      } else if (!var4.isEmpty() && var3 != null && !var3.isEmpty()) {
         for (String var9 : var4.split(",")) {
            if (var9.trim().equals(var3)) {
               var5 = true;
               this.setActualSubprotocol(var3);
               break;
            }
         }
      }

      if (!var5) {
         throw new WebSocketClientHandshakeException(
            String.format("Invalid subprotocol. Actual: %s. Expected one of: %s", var3, this.expectedSubprotocol), var2
         );
      } else {
         this.setHandshakeComplete();
         final ChannelPipeline var12 = var1.pipeline();
         HttpContentDecompressor var13 = var12.get(HttpContentDecompressor.class);
         if (var13 != null) {
            var12.remove(var13);
         }

         HttpObjectAggregator var14 = var12.get(HttpObjectAggregator.class);
         if (var14 != null) {
            var12.remove(var14);
         }

         final ChannelHandlerContext var15 = var12.context(HttpResponseDecoder.class);
         if (var15 == null) {
            var15 = var12.context(HttpClientCodec.class);
            if (var15 == null) {
               throw new IllegalStateException("ChannelPipeline does not contain an HttpRequestEncoder or HttpClientCodec");
            }

            final HttpClientCodec var10 = (HttpClientCodec)var15.handler();
            var10.removeOutboundHandler();
            var12.addAfter(var15.name(), "ws-decoder", this.newWebsocketDecoder());
            var1.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  var12.remove(var10);
               }
            });
         } else {
            if (var12.get(HttpRequestEncoder.class) != null) {
               var12.remove(HttpRequestEncoder.class);
            }

            var12.addAfter(var15.name(), "ws-decoder", this.newWebsocketDecoder());
            var1.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  var12.remove(var15.handler());
               }
            });
         }
      }
   }

   public final ChannelFuture processHandshake(Channel var1, HttpResponse var2) {
      return this.processHandshake(var1, var2, var1.newPromise());
   }

   public final ChannelFuture processHandshake(final Channel var1, HttpResponse var2, final ChannelPromise var3) {
      if (var2 instanceof FullHttpResponse) {
         try {
            this.finishHandshake(var1, (FullHttpResponse)var2);
            var3.setSuccess();
         } catch (Throwable var9) {
            var3.setFailure(var9);
         }
      } else {
         ChannelPipeline var4 = var1.pipeline();
         ChannelHandlerContext var5 = var4.context(HttpResponseDecoder.class);
         if (var5 == null) {
            var5 = var4.context(HttpClientCodec.class);
            if (var5 == null) {
               return var3.setFailure(new IllegalStateException("ChannelPipeline does not contain an HttpResponseDecoder or HttpClientCodec"));
            }
         }

         String var6 = var5.name();
         if (this.version == WebSocketVersion.V00) {
            var6 = "httpAggregator";
            var4.addAfter(var5.name(), var6, new HttpObjectAggregator(8192));
         }

         var4.addAfter(
            var6,
            "handshaker",
            new ChannelInboundHandlerAdapter() {
               private FullHttpResponse fullHttpResponse;

               @Override
               public void channelRead(ChannelHandlerContext var1x, Object var2) throws Exception {
                  if (var2 instanceof HttpObject) {
                     try {
                        this.handleHandshakeResponse(var1x, (HttpObject)var2);
                     } finally {
                        ReferenceCountUtil.release(var2);
                     }
                  } else {
                     super.channelRead(var1x, var2);
                  }
               }

               @Override
               public void exceptionCaught(ChannelHandlerContext var1x, Throwable var2) throws Exception {
                  var1x.pipeline().remove(this);
                  var3.setFailure(var2);
               }

               @Override
               public void channelInactive(ChannelHandlerContext var1x) throws Exception {
                  try {
                     if (!var3.isDone()) {
                        var3.tryFailure(new ClosedChannelException());
                     }

                     var1x.fireChannelInactive();
                  } finally {
                     this.releaseFullHttpResponse();
                  }
               }

               @Override
               public void handlerRemoved(ChannelHandlerContext var1x) throws Exception {
                  this.releaseFullHttpResponse();
               }

               private void handleHandshakeResponse(ChannelHandlerContext var1x, HttpObject var2) {
                  if (var2 instanceof FullHttpResponse) {
                     var1x.pipeline().remove(this);
                     this.tryFinishHandshake((FullHttpResponse)var2);
                  } else if (var2 instanceof LastHttpContent) {
                     assert this.fullHttpResponse != null;

                     FullHttpResponse var7 = this.fullHttpResponse;
                     this.fullHttpResponse = null;

                     try {
                        var1x.pipeline().remove(this);
                        this.tryFinishHandshake(var7);
                     } finally {
                        var7.release();
                     }
                  } else {
                     if (var2 instanceof HttpResponse) {
                        HttpResponse var3x = (HttpResponse)var2;
                        this.fullHttpResponse = new DefaultFullHttpResponse(
                           var3x.protocolVersion(), var3x.status(), Unpooled.EMPTY_BUFFER, var3x.headers(), EmptyHttpHeaders.INSTANCE
                        );
                        if (var3x.decoderResult().isFailure()) {
                           this.fullHttpResponse.setDecoderResult(var3x.decoderResult());
                        }
                     }
                  }
               }

               private void tryFinishHandshake(FullHttpResponse var1x) {
                  try {
                     WebSocketClientHandshaker.this.finishHandshake(var1, var1x);
                     var3.setSuccess();
                  } catch (Throwable var3x) {
                     var3.setFailure(var3x);
                  }
               }

               private void releaseFullHttpResponse() {
                  if (this.fullHttpResponse != null) {
                     this.fullHttpResponse.release();
                     this.fullHttpResponse = null;
                  }
               }
            }
         );

         try {
            var5.fireChannelRead(ReferenceCountUtil.retain(var2));
         } catch (Throwable var8) {
            var3.setFailure(var8);
         }
      }

      return var3;
   }

   protected abstract void verify(FullHttpResponse var1);

   protected abstract WebSocketFrameDecoder newWebsocketDecoder();

   protected abstract WebSocketFrameEncoder newWebSocketEncoder();

   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2) {
      ObjectUtil.checkNotNull(var1, "channel");
      return this.close(var1, var2, var1.newPromise());
   }

   public ChannelFuture close(Channel var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      ObjectUtil.checkNotNull(var1, "channel");
      return this.close0(var1, var1, var2, var3);
   }

   public ChannelFuture close(ChannelHandlerContext var1, CloseWebSocketFrame var2) {
      ObjectUtil.checkNotNull(var1, "ctx");
      return this.close(var1, var2, var1.newPromise());
   }

   public ChannelFuture close(ChannelHandlerContext var1, CloseWebSocketFrame var2, ChannelPromise var3) {
      ObjectUtil.checkNotNull(var1, "ctx");
      return this.close0(var1, var1.channel(), var2, var3);
   }

   private ChannelFuture close0(final ChannelOutboundInvoker var1, final Channel var2, CloseWebSocketFrame var3, ChannelPromise var4) {
      var1.writeAndFlush(var3, var4);
      final long var5 = this.forceCloseTimeoutMillis;
      if (var5 > 0L && var2.isActive() && this.forceCloseInit == 0) {
         var4.addListener(
            new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) {
                  if (var1x.isSuccess()
                     && var2.isActive()
                     && WebSocketClientHandshaker.FORCE_CLOSE_INIT_UPDATER.compareAndSet(WebSocketClientHandshaker.this, 0, 1)) {
                     final ScheduledFuture var2x = var2.eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                           if (var2.isActive()) {
                              var1.close();
                              WebSocketClientHandshaker.this.forceCloseComplete = true;
                           }
                        }
                     }, var5, TimeUnit.MILLISECONDS);
                     var2.closeFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture var1x) throws Exception {
                           var2x.cancel(false);
                        }
                     });
                  }
               }
            }
         );
         return var4;
      } else {
         return var4;
      }
   }

   protected String upgradeUrl(URI var1) {
      if (this.absoluteUpgradeUrl) {
         return var1.toString();
      } else {
         String var2 = var1.getRawPath();
         var2 = var2 != null && !var2.isEmpty() ? var2 : "/";
         String var3 = var1.getRawQuery();
         return var3 != null && !var3.isEmpty() ? var2 + '?' + var3 : var2;
      }
   }

   static CharSequence websocketHostValue(URI var0) {
      int var1 = var0.getPort();
      if (var1 == -1) {
         return var0.getHost();
      } else {
         String var2 = var0.getHost();
         String var3 = var0.getScheme();
         if (var1 == HttpScheme.HTTP.port()) {
            return !HttpScheme.HTTP.name().contentEquals(var3) && !WebSocketScheme.WS.name().contentEquals(var3)
               ? NetUtil.toSocketAddressString(var2, var1)
               : var2;
         } else if (var1 != HttpScheme.HTTPS.port()) {
            return NetUtil.toSocketAddressString(var2, var1);
         } else {
            return !HttpScheme.HTTPS.name().contentEquals(var3) && !WebSocketScheme.WSS.name().contentEquals(var3)
               ? NetUtil.toSocketAddressString(var2, var1)
               : var2;
         }
      }
   }

   static CharSequence websocketOriginValue(URI var0) {
      String var1 = var0.getScheme();
      int var3 = var0.getPort();
      String var2;
      int var4;
      if (!WebSocketScheme.WSS.name().contentEquals(var1)
         && !HttpScheme.HTTPS.name().contentEquals(var1)
         && (var1 != null || var3 != WebSocketScheme.WSS.port())) {
         var2 = HTTP_SCHEME_PREFIX;
         var4 = WebSocketScheme.WS.port();
      } else {
         var2 = HTTPS_SCHEME_PREFIX;
         var4 = WebSocketScheme.WSS.port();
      }

      String var5 = var0.getHost().toLowerCase(Locale.US);
      return var3 != var4 && var3 != -1 ? var2 + NetUtil.toSocketAddressString(var5, var3) : var2 + var5;
   }
}
