package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.concurrent.TimeUnit;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder<WebSocketFrame> implements ChannelOutboundHandler {
   private final boolean dropPongFrames;
   private final WebSocketCloseStatus closeStatus;
   private final long forceCloseTimeoutMillis;
   private ChannelPromise closeSent;

   WebSocketProtocolHandler() {
      this(true);
   }

   WebSocketProtocolHandler(boolean var1) {
      this(var1, null, 0L);
   }

   WebSocketProtocolHandler(boolean var1, WebSocketCloseStatus var2, long var3) {
      this.dropPongFrames = var1;
      this.closeStatus = var2;
      this.forceCloseTimeoutMillis = var3;
   }

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      if (var2 instanceof PingWebSocketFrame) {
         var2.content().retain();
         var1.writeAndFlush(new PongWebSocketFrame(var2.content()));
         readIfNeeded(var1);
      } else if (var2 instanceof PongWebSocketFrame && this.dropPongFrames) {
         readIfNeeded(var1);
      } else {
         var3.add(var2.retain());
      }
   }

   private static void readIfNeeded(ChannelHandlerContext var0) {
      if (!var0.channel().config().isAutoRead()) {
         var0.read();
      }
   }

   @Override
   public void close(final ChannelHandlerContext var1, final ChannelPromise var2) throws Exception {
      if (this.closeStatus != null && var1.channel().isActive()) {
         if (this.closeSent == null) {
            this.write(var1, new CloseWebSocketFrame(this.closeStatus), var1.newPromise());
         }

         this.flush(var1);
         this.applyCloseSentTimeout(var1);
         this.closeSent.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) {
               var1.close(var2);
            }
         });
      } else {
         var1.close(var2);
      }
   }

   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (this.closeSent != null) {
         ReferenceCountUtil.release(var2);
         var3.setFailure(new ClosedChannelException());
      } else if (var2 instanceof CloseWebSocketFrame) {
         this.closeSent(var3.unvoid());
         var1.write(var2).addListener(new PromiseNotifier<>(false, this.closeSent));
      } else {
         var1.write(var2, var3);
      }
   }

   void closeSent(ChannelPromise var1) {
      this.closeSent = var1;
   }

   private void applyCloseSentTimeout(ChannelHandlerContext var1) {
      if (!this.closeSent.isDone() && this.forceCloseTimeoutMillis >= 0L) {
         final ScheduledFuture var2 = var1.executor().schedule(new Runnable() {
            @Override
            public void run() {
               if (!WebSocketProtocolHandler.this.closeSent.isDone()) {
                  WebSocketProtocolHandler.this.closeSent.tryFailure(WebSocketProtocolHandler.this.buildHandshakeException("send close frame timed out"));
               }
            }
         }, this.forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
         this.closeSent.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) {
               var2.cancel(false);
            }
         });
      }
   }

   protected WebSocketHandshakeException buildHandshakeException(String var1) {
      return new WebSocketHandshakeException(var1);
   }

   @Override
   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   @Override
   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   @Override
   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   @Override
   public void read(ChannelHandlerContext var1) throws Exception {
      var1.read();
   }

   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      var1.fireExceptionCaught(var2);
      var1.close();
   }
}
