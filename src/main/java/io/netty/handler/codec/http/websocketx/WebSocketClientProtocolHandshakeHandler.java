package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketClientProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
   private static final long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10000L;
   private final WebSocketClientHandshaker handshaker;
   private final long handshakeTimeoutMillis;
   private ChannelHandlerContext ctx;
   private ChannelPromise handshakePromise;

   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker var1) {
      this(var1, 10000L);
   }

   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker var1, long var2) {
      this.handshaker = var1;
      this.handshakeTimeoutMillis = ObjectUtil.checkPositive(var2, "handshakeTimeoutMillis");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      this.handshakePromise = var1.newPromise();
   }

   @Override
   public void channelActive(final ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.handshaker.handshake(var1.channel()).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1x) throws Exception {
            if (!var1x.isSuccess()) {
               WebSocketClientProtocolHandshakeHandler.this.handshakePromise.tryFailure(var1x.cause());
               var1.fireExceptionCaught(var1x.cause());
            } else {
               var1.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
            }
         }
      });
      this.applyHandshakeTimeout();
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      if (!this.handshakePromise.isDone()) {
         this.handshakePromise.tryFailure(new WebSocketClientHandshakeException("channel closed with handshake in progress"));
      }

      super.channelInactive(var1);
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (!(var2 instanceof FullHttpResponse)) {
         var1.fireChannelRead(var2);
      } else {
         FullHttpResponse var3 = (FullHttpResponse)var2;

         try {
            if (this.handshaker.isHandshakeComplete()) {
               throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
            }

            this.handshaker.finishHandshake(var1.channel(), var3);
            this.handshakePromise.trySuccess();
            var1.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
            var1.pipeline().remove(this);
         } finally {
            var3.release();
         }
      }
   }

   private void applyHandshakeTimeout() {
      final ChannelPromise var1 = this.handshakePromise;
      if (this.handshakeTimeoutMillis > 0L && !var1.isDone()) {
         final ScheduledFuture var2 = this.ctx
            .executor()
            .schedule(
               new Runnable() {
                  @Override
                  public void run() {
                     if (!var1.isDone()) {
                        if (var1.tryFailure(new WebSocketClientHandshakeException("handshake timed out"))) {
                           WebSocketClientProtocolHandshakeHandler.this.ctx
                              .flush()
                              .fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT)
                              .close();
                        }
                     }
                  }
               },
               this.handshakeTimeoutMillis,
               TimeUnit.MILLISECONDS
            );
         var1.addListener(new FutureListener<Void>() {
            @Override
            public void operationComplete(Future<Void> var1) throws Exception {
               var2.cancel(false);
            }
         });
      }
   }

   ChannelFuture getHandshakeFuture() {
      return this.handshakePromise;
   }
}
