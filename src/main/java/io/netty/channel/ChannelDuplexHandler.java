package io.netty.channel;

import java.net.SocketAddress;

public class ChannelDuplexHandler extends ChannelInboundHandlerAdapter implements ChannelOutboundHandler {
   @ChannelHandlerMask.Skip
   @Override
   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.close(var2);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void read(ChannelHandlerContext var1) throws Exception {
      var1.read();
   }

   @ChannelHandlerMask.Skip
   @Override
   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      var1.write(var2, var3);
   }

   @ChannelHandlerMask.Skip
   @Override
   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }
}
