package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import xcord.DnsResolver;
import xcord.XCord;

public class BungeeServerInfo implements ServerInfo {
   private final String name;
   private final SocketAddress socketAddress;
   private final Collection<ProxiedPlayer> players = new ArrayList<>();
   private final String motd;
   private final boolean restricted;
   private final Queue<DefinedPacket> packetQueue = new LinkedList<>();
   private long lastPing;
   private ServerPing cachedPing;

   public void addPlayer(ProxiedPlayer var1) {
      synchronized (this.players) {
         this.players.add(var1);
      }
   }

   public void removePlayer(ProxiedPlayer var1) {
      synchronized (this.players) {
         this.players.remove(var1);
      }
   }

   @Override
   public Collection<ProxiedPlayer> getPlayers() {
      synchronized (this.players) {
         return Collections.unmodifiableCollection(new HashSet<>(this.players));
      }
   }

   @Override
   public String getPermission() {
      return "bungeecord.server." + this.name;
   }

   @Override
   public boolean canAccess(CommandSender var1) {
      Preconditions.checkNotNull(var1, "player");
      return !this.restricted || var1.hasPermission(this.getPermission());
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof ServerInfo && Objects.equals(this.getAddress(), ((ServerInfo)var1).getAddress());
   }

   @Override
   public int hashCode() {
      return this.socketAddress.hashCode();
   }

   @Override
   public void sendData(String var1, byte[] var2) {
      this.sendData(var1, var2, true);
   }

   @Override
   public boolean sendData(String var1, byte[] var2, boolean var3) {
      Preconditions.checkNotNull(var1, "channel");
      Preconditions.checkNotNull(var2, "data");
      Server var4;
      synchronized (this.players) {
         var4 = this.players.isEmpty() ? null : this.players.iterator().next().getServer();
      }

      if (var4 != null) {
         var4.sendData(var1, var2);
         return true;
      } else {
         if (var3) {
            synchronized (this.packetQueue) {
               this.packetQueue.add(new PluginMessage(var1, var2, false));
            }
         }

         return false;
      }
   }

   public void cachePing(ServerPing var1) {
      if (ProxyServer.getInstance().getConfig().getRemotePingCache() > 0) {
         this.cachedPing = var1;
         this.lastPing = System.currentTimeMillis();
      }
   }

   @Override
   public InetSocketAddress getAddress() {
      return (InetSocketAddress)this.socketAddress;
   }

   @Override
   public void ping(Callback<ServerPing> var1) {
      this.ping(var1, ProxyServer.getInstance().getProtocolVersion());
   }

   public void ping(final Callback<ServerPing> var1, final int var2) {
      Preconditions.checkNotNull(var1, "callback");
      int var3 = ProxyServer.getInstance().getConfig().getRemotePingCache();
      if (var3 > 0 && this.cachedPing != null && System.currentTimeMillis() - this.lastPing > (long)var3) {
         this.cachedPing = null;
      }

      if (this.cachedPing != null) {
         var1.done(this.cachedPing, null);
      } else {
         ChannelFutureListener var4 = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               if (var1x.isSuccess()) {
                  ((HandlerBoss)var1x.channel().pipeline().get(HandlerBoss.class)).setHandler(new PingHandler(BungeeServerInfo.this, var1, var2));
               } else {
                  var1.done(null, var1x.cause());
               }
            }
         };
         io.netty.bootstrap.Bootstrap var5 = ((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)((io.netty.bootstrap.Bootstrap)new io.netty.bootstrap.Bootstrap()
                        .channel(PipelineUtils.getChannel(this.socketAddress)))
                     .group(BungeeCord.getInstance().workerEventLoopGroup))
                  .handler(PipelineUtils.BASE))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, BungeeCord.getInstance().getConfig().getRemotePingTimeout()))
            .remoteAddress(this.socketAddress);
         if (this.socketAddress instanceof InetSocketAddress && ((InetSocketAddress)this.socketAddress).isUnresolved()) {
            var5.resolver(DnsResolver.getResolverGroup());
         }

         if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastOpenOnServers() && PipelineUtils.usingEpoll()) {
            var5.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
         }

         var5.connect().addListener(var4);
      }
   }

   public BungeeServerInfo(String var1, SocketAddress var2, String var3, boolean var4) {
      this.name = var1;
      this.socketAddress = var2;
      this.motd = var3;
      this.restricted = var4;
   }

   @Override
   public String toString() {
      return "BungeeServerInfo(name=" + this.getName() + ", socketAddress=" + this.getSocketAddress() + ", restricted=" + this.isRestricted() + ")";
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public SocketAddress getSocketAddress() {
      return this.socketAddress;
   }

   @Override
   public String getMotd() {
      return this.motd;
   }

   @Override
   public boolean isRestricted() {
      return this.restricted;
   }

   public Queue<DefinedPacket> getPacketQueue() {
      return this.packetQueue;
   }
}
