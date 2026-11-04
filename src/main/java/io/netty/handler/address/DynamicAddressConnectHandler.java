package io.netty.handler.address;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public abstract class DynamicAddressConnectHandler extends ChannelOutboundHandlerAdapter {
   @Override
   public final void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) {
      SocketAddress var5;
      SocketAddress var6;
      try {
         var5 = this.remoteAddress(var2, var3);
         var6 = this.localAddress(var2, var3);
      } catch (Exception var8) {
         var4.setFailure(var8);
         return;
      }

      var1.connect(var5, var6, var4).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) {
            if (var1.isSuccess()) {
               var1.channel().pipeline().remove(DynamicAddressConnectHandler.this);
            }
         }
      });
   }

   protected SocketAddress localAddress(SocketAddress var1, SocketAddress var2) throws Exception {
      return var2;
   }

   protected SocketAddress remoteAddress(SocketAddress var1, SocketAddress var2) throws Exception {
      return var1;
   }
}
