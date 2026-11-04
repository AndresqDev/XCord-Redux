package io.netty.handler.address;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

@ChannelHandler.Sharable
public class ResolveAddressHandler extends ChannelOutboundHandlerAdapter {
   private final AddressResolverGroup<? extends SocketAddress> resolverGroup;

   public ResolveAddressHandler(AddressResolverGroup<? extends SocketAddress> var1) {
      this.resolverGroup = ObjectUtil.checkNotNull(var1, "resolverGroup");
   }

   @Override
   public void connect(final ChannelHandlerContext var1, SocketAddress var2, final SocketAddress var3, final ChannelPromise var4) {
      AddressResolver var5 = this.resolverGroup.getResolver(var1.executor());
      if (var5.isSupported(var2) && !var5.isResolved(var2)) {
         var5.resolve(var2).addListener(new FutureListener<SocketAddress>() {
            @Override
            public void operationComplete(Future<SocketAddress> var1x) {
               Throwable var2 = var1x.cause();
               if (var2 != null) {
                  var4.setFailure(var2);
               } else {
                  var1.connect((SocketAddress)var1x.getNow(), var3, var4);
               }

               var1.pipeline().remove(ResolveAddressHandler.this);
            }
         });
      } else {
         var1.connect(var2, var3, var4);
         var1.pipeline().remove(this);
      }
   }
}
