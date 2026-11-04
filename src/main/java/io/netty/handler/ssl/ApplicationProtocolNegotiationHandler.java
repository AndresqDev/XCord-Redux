package io.netty.handler.ssl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLException;

public abstract class ApplicationProtocolNegotiationHandler extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ApplicationProtocolNegotiationHandler.class);
   private final String fallbackProtocol;
   private final RecyclableArrayList bufferedMessages = RecyclableArrayList.newInstance();
   private ChannelHandlerContext ctx;
   private boolean sslHandlerChecked;

   protected ApplicationProtocolNegotiationHandler(String var1) {
      this.fallbackProtocol = ObjectUtil.checkNotNull(var1, "fallbackProtocol");
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      super.handlerAdded(var1);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.fireBufferedMessages();
      this.bufferedMessages.recycle();
      super.handlerRemoved(var1);
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      this.bufferedMessages.add(var2);
      if (!this.sslHandlerChecked) {
         this.sslHandlerChecked = true;
         if (var1.pipeline().get(SslHandler.class) == null) {
            this.removeSelfIfPresent(var1);
         }
      }
   }

   private void fireBufferedMessages() {
      if (!this.bufferedMessages.isEmpty()) {
         for (int var1 = 0; var1 < this.bufferedMessages.size(); var1++) {
            this.ctx.fireChannelRead(this.bufferedMessages.get(var1));
         }

         this.ctx.fireChannelReadComplete();
         this.bufferedMessages.clear();
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof SslHandshakeCompletionEvent) {
         SslHandshakeCompletionEvent var3 = (SslHandshakeCompletionEvent)var2;

         try {
            if (var3.isSuccess()) {
               SslHandler var4 = var1.pipeline().get(SslHandler.class);
               if (var4 == null) {
                  throw new IllegalStateException("cannot find an SslHandler in the pipeline (required for application-level protocol negotiation)");
               }

               String var5 = var4.applicationProtocol();
               this.configurePipeline(var1, var5 != null ? var5 : this.fallbackProtocol);
            }
         } catch (Throwable var9) {
            this.exceptionCaught(var1, var9);
         } finally {
            if (var3.isSuccess()) {
               this.removeSelfIfPresent(var1);
            }
         }
      }

      if (var2 instanceof ChannelInputShutdownEvent) {
         this.fireBufferedMessages();
      }

      var1.fireUserEventTriggered(var2);
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.fireBufferedMessages();
      super.channelInactive(var1);
   }

   private void removeSelfIfPresent(ChannelHandlerContext var1) {
      ChannelPipeline var2 = var1.pipeline();
      if (!var1.isRemoved()) {
         var2.remove(this);
      }
   }

   protected abstract void configurePipeline(ChannelHandlerContext var1, String var2) throws Exception;

   protected void handshakeFailure(ChannelHandlerContext var1, Throwable var2) throws Exception {
      logger.warn("{} TLS handshake failed:", var1.channel(), var2);
      var1.close();
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      Throwable var3;
      if (var2 instanceof DecoderException && (var3 = var2.getCause()) instanceof SSLException) {
         try {
            this.handshakeFailure(var1, var3);
         } finally {
            this.removeSelfIfPresent(var1);
         }
      } else {
         logger.warn("{} Failed to select the application-level protocol:", var1.channel(), var2);
         var1.fireExceptionCaught(var2);
         var1.close();
      }
   }
}
