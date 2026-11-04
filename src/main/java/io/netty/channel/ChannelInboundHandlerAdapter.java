package io.netty.channel;

public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler {
   @ChannelHandlerMask.Skip
   @Override
   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelRegistered();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelUnregistered();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelActive();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelInactive();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      var1.fireChannelRead(var2);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelReadComplete();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      var1.fireUserEventTriggered(var2);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      var1.fireChannelWritabilityChanged();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      var1.fireExceptionCaught(var2);
   }
}
