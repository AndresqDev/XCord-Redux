package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.util.QuietException;
import xcord.XCord;
import xcord.antibot.CheckType;
import xcord.antibot.ConnectionStats;

public class HandlerBoss extends ChannelInboundHandlerAdapter {
    private ChannelWrapper channel;
    private PacketHandler handler;

    public void setHandler(PacketHandler handler) {
        Preconditions.checkArgument(handler != null, "handler");
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.handler != null) {
            this.channel = new ChannelWrapper(ctx);
            this.handler.connected(this.channel);
            if (!(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler) && !XCord.isLogFilterEnabled()) {
                ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has connected", this.handler);
            }
        }
    }

    public void replaceIPAddress(String address) throws Exception {
        InetSocketAddress currentAddress = (InetSocketAddress) this.channel.getRemoteAddress();
        InetSocketAddress replacementAddress = new InetSocketAddress(address, currentAddress.getPort());
        this.channel.setRemoteAddress(replacementAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.handler != null) {
            this.channel.markClosed();
            this.handler.disconnected(this.channel);
            if (!(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler) && !XCord.isLogFilterEnabled()) {
                ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has disconnected", this.handler);
            }
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (this.handler != null) {
            this.handler.writabilityChanged(this.channel);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            this.handleProxyMessage(ctx, (HAProxyMessage) msg);
            return;
        }
        if (this.handler == null) {
            return;
        }

        PacketWrapper wrapper = (PacketWrapper) msg;
        boolean shouldHandle = this.handler.shouldHandle(wrapper);
        try {
            if (shouldHandle && wrapper.packet != null) {
                try {
                    wrapper.packet.handle(this.handler);
                } catch (CancelSendSignal ex) {
                    shouldHandle = false;
                }
            }
            if (shouldHandle) {
                this.handler.handle(wrapper);
            }
        } finally {
            wrapper.trySingleRelease();
        }
    }

    private void handleProxyMessage(ChannelHandlerContext ctx, HAProxyMessage message) {
        this.channel.setBeingForward(true);
        if (XCord.getConfig().getSecurityManagerAutoWhitelistHaproxyAddresses()) {
            XCord.getConfig().addIgnoredAddress(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
        }

        try {
            if (message.sourceAddress() == null) {
                return;
            }

            InetSocketAddress remoteAddress = new InetSocketAddress(message.sourceAddress(), message.sourcePort());
            if (!XCord.getAntiBotManager().isAttackActive() || XCord.getConfig().getDebugDebugMode()) {
                ProxyServer.getInstance()
                    .getLogger()
                    .log(Level.FINE, "Set remote address via PROXY {0} -> {1}", new Object[]{this.channel.getRemoteAddress(), remoteAddress});
            }

            this.channel.setRemoteAddress(remoteAddress);
            InetAddress address = remoteAddress.getAddress();
            String hostAddress = address.getHostAddress();
            int connectionCount = XCord.getAntiBotManager().incrementConnections();
            if (!XCord.getConfig().getSharedGlobalBlacklistUseIpset()
                && XCord.getConfig().getSharedGlobalBlacklistEnabled()
                && XCord.getGlobalBlacklist().isBlacklisted(hostAddress)) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("[DEBUG] GBlacklisted IP " + hostAddress + " was rejected.");
                }
                PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
                return;
            }

            if (XCord.getConfig().getAntiDdosDosIpFilterEnabled()
                && XCord.getDosFilterBlacklist().isActive()
                && XCord.getDosFilterBlacklist().isBlacklisted(hostAddress)) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("[DEBUG] DoS-Filtered " + hostAddress);
                }
                PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
                return;
            }

            if (XCord.getConfig().isIgnoredAddress(address)) {
                return;
            }

            if (XCord.getBlacklistManager().isBlacklisted(hostAddress)) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("[DEBUG] Blacklisted IP " + hostAddress + " was rejected.");
                }
                PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
                return;
            }

            ConnectionStats stats = XCord.getAntiBotManager().getStats(hostAddress);
            if (stats.checkSpeedyConnection(address)) {
                PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
                return;
            }

            if (connectionCount <= XCord.getConfig().getGeneralNewConnectionSpeedThreshold()) {
                return;
            }

            if (XCord.getConfig().getDebugDebugMode()) {
                System.out.println("[DEBUG] XCord throttled " + hostAddress + " (Change in xcord.yml) due to your 'new-connection-speed-threshold' threshold.");
            }
            PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
        } finally {
            message.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.handler != null) {
            this.handler.readCompleted();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        if (ctx.channel().isActive()) {
            if (XCord.getConfig().getDebugDebugMode()) {
                throwable.printStackTrace();
            }

            if (throwable instanceof DecoderException
                || throwable instanceof IllegalStateException
                || throwable instanceof BadPacketException
                || throwable instanceof QuietException) {
                SocketAddress remoteAddress = this.channel.getRemoteAddress();
                if (remoteAddress != null) {
                    if ((this.handler != null && !(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler))
                        || (XCord.getConfig().getGeneralOnlyBlacklistPacketErrorsDuringAttack() && !XCord.getAntiBotManager().isAttackActive())) {
                        ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - exception caught Type: " + throwable.getClass().getSimpleName());
                    } else {
                        XCord.getBlacklistManager().isBlacklisted(remoteAddress, throwable.getClass().getSimpleName());
                    }
                }
            }

            if (!(this.handler instanceof PingHandler) && throwable instanceof ReadTimeoutException) {
                if (!XCord.getAntiBotManager().isAttackActive()) {
                    ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - read timed out", this.handler);
                }

                if (XCord.getConfig().getAntiBotTimeoutFloodEnabled()) {
                    String hostAddress = ((InetSocketAddress) this.channel.getRemoteAddress()).getAddress().getHostAddress();
                    ConnectionStats stats = XCord.getAntiBotManager().getStats(hostAddress);
                    long now = System.currentTimeMillis();
                    if (now - stats.getLastAction() <= XCord.getConfig().getAntiBotTimeoutFloodTimeBetweenTimeouts()) {
                        XCord.getAntiBotManager()
                            .checkViolation(
                                CheckType.TIMEOUT_FLOOD, this.channel, 1, XCord.getConfig().getAntiBotTimeoutFloodBlacklistViolations(), "Time: " + (now - stats.getLastAction())
                            );
                    }
                }
            }

            if (this.handler != null) {
                try {
                    this.handler.exception(throwable);
                } catch (Exception ex) {
                    ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - exception processing exception", ex);
                }
            }

            PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
        }
    }
}
