package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class DatagramPacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
   private final MessageToMessageDecoder<ByteBuf> decoder;

   public DatagramPacketDecoder(MessageToMessageDecoder<ByteBuf> var1) {
      this.decoder = ObjectUtil.checkNotNull(var1, "decoder");
   }

   @Override
   public boolean acceptInboundMessage(Object var1) throws Exception {
      return var1 instanceof DatagramPacket ? this.decoder.acceptInboundMessage(((DatagramPacket)var1).content()) : false;
   }

   protected void decode(ChannelHandlerContext var1, DatagramPacket var2, List<Object> var3) throws Exception {
      this.decoder.decode(var1, var2.content(), var3);
   }

   @Override
   public void channelRegistered(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelRegistered(var1);
   }

   @Override
   public void channelUnregistered(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelUnregistered(var1);
   }

   @Override
   public void channelActive(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelActive(var1);
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelInactive(var1);
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelReadComplete(var1);
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      this.decoder.userEventTriggered(var1, var2);
   }

   @Override
   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      this.decoder.channelWritabilityChanged(var1);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      this.decoder.exceptionCaught(var1, var2);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.decoder.handlerAdded(var1);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.decoder.handlerRemoved(var1);
   }

   @Override
   public boolean isSharable() {
      return this.decoder.isSharable();
   }
}
