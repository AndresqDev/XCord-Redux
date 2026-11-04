package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Utf8FrameValidator extends ChannelInboundHandlerAdapter {
   private final boolean closeOnProtocolViolation;
   private int fragmentedFramesCount;
   private Utf8Validator utf8Validator;

   public Utf8FrameValidator() {
      this(true);
   }

   public Utf8FrameValidator(boolean var1) {
      this.closeOnProtocolViolation = var1;
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof WebSocketFrame) {
         WebSocketFrame var3 = (WebSocketFrame)var2;

         try {
            if (((WebSocketFrame)var2).isFinalFragment()) {
               if (!(var3 instanceof PingWebSocketFrame)) {
                  this.fragmentedFramesCount = 0;
                  if (var3 instanceof TextWebSocketFrame || this.utf8Validator != null && this.utf8Validator.isChecking()) {
                     this.checkUTF8String(var3.content());
                     this.utf8Validator.finish();
                  }
               }
            } else {
               if (this.fragmentedFramesCount == 0) {
                  if (var3 instanceof TextWebSocketFrame) {
                     this.checkUTF8String(var3.content());
                  }
               } else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                  this.checkUTF8String(var3.content());
               }

               this.fragmentedFramesCount++;
            }
         } catch (CorruptedWebSocketFrameException var5) {
            this.protocolViolation(var1, var3, var5);
         }
      }

      super.channelRead(var1, var2);
   }

   private void checkUTF8String(ByteBuf var1) {
      if (this.utf8Validator == null) {
         this.utf8Validator = new Utf8Validator();
      }

      this.utf8Validator.check(var1);
   }

   private void protocolViolation(ChannelHandlerContext var1, WebSocketFrame var2, CorruptedWebSocketFrameException var3) {
      var2.release();
      if (this.closeOnProtocolViolation && var1.channel().isOpen()) {
         WebSocketCloseStatus var4 = var3.closeStatus();
         String var5 = var3.getMessage();
         if (var5 == null) {
            var5 = var4.reasonText();
         }

         CloseWebSocketFrame var6 = new CloseWebSocketFrame(var4.code(), var5);
         var1.writeAndFlush(var6).addListener(ChannelFutureListener.CLOSE);
      }

      throw var3;
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      super.exceptionCaught(var1, var2);
   }
}
