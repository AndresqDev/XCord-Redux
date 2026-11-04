package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
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
import xcord.XCord;
import xcord.network.DnsResolver;

public class BungeeServerInfo implements ServerInfo {
    private final String name;
    private final SocketAddress socketAddress;
    private final Collection<ProxiedPlayer> players = new ArrayList<>();
    private final String motd;
    private final boolean restricted;
    private final Queue<DefinedPacket> packetQueue = new LinkedList<>();
    private long lastPing;
    private ServerPing cachedPing;

    public void addPlayer(ProxiedPlayer player) {
        synchronized (this.players) {
            this.players.add(player);
        }
    }

    public void removePlayer(ProxiedPlayer player) {
        synchronized (this.players) {
            this.players.remove(player);
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
    public boolean canAccess(CommandSender sender) {
        Preconditions.checkNotNull(sender, "player");
        return !this.restricted || sender.hasPermission(this.getPermission());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ServerInfo && Objects.equals(this.getAddress(), ((ServerInfo) other).getAddress());
    }

    @Override
    public int hashCode() {
        return this.socketAddress.hashCode();
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.sendData(channel, data, true);
    }

    @Override
    public boolean sendData(String channel, byte[] data, boolean queue) {
        Preconditions.checkNotNull(channel, "channel");
        Preconditions.checkNotNull(data, "data");
        Server server;
        synchronized (this.players) {
            server = this.players.isEmpty() ? null : this.players.iterator().next().getServer();
        }

        if (server != null) {
            server.sendData(channel, data);
            return true;
        }

        if (queue) {
            synchronized (this.packetQueue) {
                this.packetQueue.add(new PluginMessage(channel, data, false));
            }
        }

        return false;
    }

    public void cachePing(ServerPing ping) {
        if (ProxyServer.getInstance().getConfig().getRemotePingCache() > 0) {
            this.cachedPing = ping;
            this.lastPing = System.currentTimeMillis();
        }
    }

    @Override
    public InetSocketAddress getAddress() {
        return (InetSocketAddress) this.socketAddress;
    }

    @Override
    public void ping(Callback<ServerPing> callback) {
        this.ping(callback, ProxyServer.getInstance().getProtocolVersion());
    }

    public void ping(final Callback<ServerPing> callback, final int protocolVersion) {
        Preconditions.checkNotNull(callback, "callback");
        int cacheTime = ProxyServer.getInstance().getConfig().getRemotePingCache();
        if (cacheTime > 0 && this.cachedPing != null && System.currentTimeMillis() - this.lastPing > (long) cacheTime) {
            this.cachedPing = null;
        }

        if (this.cachedPing != null) {
            callback.done(this.cachedPing, null);
            return;
        }

        ChannelFutureListener listener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    future.channel().pipeline().get(HandlerBoss.class)
                            .setHandler(new PingHandler(BungeeServerInfo.this, callback, protocolVersion));
                } else {
                    callback.done(null, future.cause());
                }
            }
        };
        Bootstrap bootstrap = new Bootstrap()
                .channel(PipelineUtils.getChannel(this.socketAddress))
                .group(BungeeCord.getInstance().workerEventLoopGroup)
                .handler(PipelineUtils.BASE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, BungeeCord.getInstance().getConfig().getRemotePingTimeout())
                .remoteAddress(this.socketAddress);
        if (this.socketAddress instanceof InetSocketAddress && ((InetSocketAddress) this.socketAddress).isUnresolved()) {
            bootstrap.resolver(DnsResolver.getResolverGroup());
        }

        if (XCord.getConfig().getPerformanceOptimizationsUseTcpFastOpenOnServers() && PipelineUtils.usingEpoll()) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        bootstrap.connect().addListener(listener);
    }

    public BungeeServerInfo(String name, SocketAddress socketAddress, String motd, boolean restricted) {
        this.name = name;
        this.socketAddress = socketAddress;
        this.motd = motd;
        this.restricted = restricted;
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
