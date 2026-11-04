package net.md_5.bungee.query;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;

public class RemoteQuery {
   private final ProxyServer bungee;
   private final ListenerInfo listener;

   public void start(Class<? extends Channel> var1, InetSocketAddress var2, EventLoopGroup var3, ChannelFutureListener var4) {
      ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().channel(var1)).group(var3)).handler(new QueryHandler(this.bungee, this.listener)))
            .localAddress(var2))
         .bind()
         .addListener(var4);
   }

   public RemoteQuery(ProxyServer var1, ListenerInfo var2) {
      this.bungee = var1;
      this.listener = var2;
   }
}
