package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.github.waterfallmc.waterfall.event.ConnectionInitEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.protocol.KickStringWriter;
import net.md_5.bungee.protocol.LegacyDecoder;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.Varint21LengthFieldPrepender;
import xcord.XCord;
import xcord.antibot.ConnectionStats;
import xcord.antivpn.ProxyCheckJob;
import xcord.network.AntiDdosFrameDecoder;
import xcord.network.EpollSocketCheck;
import xcord.network.NioSocketCheck;
import xcord.network.OptimizedReadTimeoutHandler;
import xcord.network.SocketCheck;
import xcord.protocol.VarIntFrameDecoder;

public class PipelineUtils {
    public static final AttributeKey<ListenerInfo> LISTENER = AttributeKey.newInstance("ListerInfo");
    public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel channel) throws Exception {
            SocketAddress remoteAddress = channel.remoteAddress() == null ? channel.parent().localAddress() : channel.remoteAddress();
            InetAddress address = ((InetSocketAddress) remoteAddress).getAddress();
            String hostAddress = address.getHostAddress();
            int connectionCount = XCord.getAntiBotManager().incrementConnections();

            if (!XCord.getConfig().getSharedGlobalBlacklistUseIpset()
                && XCord.getConfig().getSharedGlobalBlacklistEnabled()
                && XCord.getGlobalBlacklist().isBlacklisted(hostAddress)) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("[DEBUG] GBlacklisted IP " + hostAddress + " was rejected.");
                }
                PipelineUtils.CHANNEL_UTILITY.close(channel);
                return;
            }

            if (XCord.getConfig().getAntiDdosDosIpFilterEnabled()
                && XCord.getDosFilterBlacklist().isActive()
                && XCord.getDosFilterBlacklist().isBlacklisted(hostAddress)) {
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("[DEBUG] DoS-Filtered " + hostAddress);
                }
                PipelineUtils.CHANNEL_UTILITY.close(channel);
                return;
            }

            if (!XCord.getConfig().isIgnoredAddress(address)) {
                if (XCord.getBlacklistManager().isBlacklisted(hostAddress)) {
                    if (XCord.getConfig().getDebugDebugMode()) {
                        System.out.println("[DEBUG] Blacklisted IP " + hostAddress + " was rejected.");
                    }
                    PipelineUtils.CHANNEL_UTILITY.close(channel);
                    return;
                }

                ConnectionStats stats = XCord.getAntiBotManager().getStats(hostAddress);
                if (!channel.attr(PipelineUtils.LISTENER).get().isProxyProtocol() && PipelineUtils.CHANNEL_UTILITY.check(channel)) {
                    if (!XCord.getConfig().getAntiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist()) {
                        PipelineUtils.CHANNEL_UTILITY.close(channel);
                        return;
                    }
                    stats.setFlagged(true);
                }

                if (stats.checkSpeedyConnection(address)) {
                    PipelineUtils.CHANNEL_UTILITY.close(channel);
                    return;
                }

                if (connectionCount > XCord.getConfig().getGeneralNewConnectionSpeedThreshold()) {
                    if (XCord.getConfig().getDebugDebugMode()) {
                        System.out.println("[DEBUG] XCord throttled " + hostAddress + " (Change in xcord.yml) due to your 'new-connection-speed-threshold' threshold.");
                    }
                    PipelineUtils.CHANNEL_UTILITY.close(channel);
                    return;
                }

                if (BungeeCord.getInstance().getConnectionThrottle() != null && BungeeCord.getInstance().getConnectionThrottle().throttle(remoteAddress)) {
                    if (XCord.getConfig().getDebugDebugMode()) {
                        System.out.println("[DEBUG] Bungeecord throttled " + hostAddress + " (Change in bungee.yml)");
                    }
                    PipelineUtils.CHANNEL_UTILITY.close(channel);
                    return;
                }

                if (XCord.getConfig().getLoginAntiVpnEnabled()
                    && (XCord.getConfig().getLoginAntiVpnInitialConnectionCheckEnabled()
                        || (XCord.getConfig().getLoginAntiVpnInitialConnectionCheckOnlyDuringBotAttack()
                            && XCord.getAntiBotManager().isAttackActive()))) {
                    ProxyResult result = XCord.getAntiProxyManager().check("NA", hostAddress, true);
                    if (result == ProxyResult.DETECTED) {
                        if (XCord.getConfig().getLoginAntiVpnBlacklistOnDetection()
                            || (XCord.getConfig().getLoginAntiVpnOnlyBlacklistOnDetectionDuringAttack()
                                && XCord.getAntiBotManager().isAttackActive())) {
                            XCord.getBlacklistManager().isBlacklisted(address);
                        }
                        PipelineUtils.CHANNEL_UTILITY.close(channel);
                        return;
                    }

                    if (result == null && !XCord.getConfig().getLoginAntiVpnInitialConnectionCheckCacheOnly()) {
                        XCord.getAsyncJobService().submit(new ProxyCheckJob("NA", hostAddress, (proxyResult, throwable) -> {
                            if (channel.isActive() && proxyResult == ProxyResult.DETECTED) {
                                channel.eventLoop().execute(() -> {
                                    if (channel.isActive()) {
                                        PipelineUtils.CHANNEL_UTILITY.close(channel);
                                    }
                                });
                            }
                        }));
                    }
                }
            }

            ListenerInfo listener = channel.attr(PipelineUtils.LISTENER).get();
            if (BungeeCord.getInstance().getPluginManager().getEventBus().isWatchedListenerActive(0)
                && BungeeCord.getInstance().getPluginManager().callEvent(new ClientConnectEvent(remoteAddress, listener)).isCancelled()) {
                PipelineUtils.CHANNEL_UTILITY.close(channel);
                return;
            }

            Callback<ConnectionInitEvent> callback = (connectionInitEvent, throwable) -> {
                if (connectionInitEvent != null && connectionInitEvent.isCancelled()) {
                    PipelineUtils.CHANNEL_UTILITY.close(channel);
                    return;
                }

                try {
                    PipelineUtils.CLIENT_BASE.initChannel(channel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    channel.close();
                    return;
                }

                if (!XCord.getConfig().getAntiBotNullPingAndPingSpamDisableLegacyPings()) {
                    channel.pipeline().addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.LEGACY_DECODER, new LegacyDecoder());
                }

                channel.pipeline().addAfter(
                    PipelineUtils.FRAME_DECODER,
                    PipelineUtils.PACKET_DECODER,
                    new MinecraftDecoder(
                        Protocol.HANDSHAKE,
                        true,
                        ProxyServer.getInstance().getProtocolVersion(),
                        XCord.getConfig().getPerformanceOptimizationsDontCopyPackets()
                    )
                );
                channel.pipeline().addAfter(
                    PipelineUtils.FRAME_PREPENDER,
                    PipelineUtils.PACKET_ENCODER,
                    new MinecraftEncoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion())
                );
                channel.pipeline().addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.LEGACY_KICKER, PipelineUtils.legacyKicker);

                if (!XCord.getConfig().getAntiBotAntiHangEnabled()
                    || (XCord.getConfig().getAntiBotAntiHangOnlyDuringBotAttack() && !XCord.getAntiBotManager().isAttackActive())) {
                    PipelineUtils.setTimeoutHandler(channel, XCord.getConfig().getGeneralInitialTimeout());
                } else {
                    PipelineUtils.setTimeoutHandler(channel, XCord.getConfig().getAntiBotAntiHangForceTimeoutTime());
                }

                channel.pipeline().get(HandlerBoss.class).setHandler(new InitialHandler(BungeeCord.getInstance(), listener));
                if (listener.isProxyProtocol()) {
                    channel.pipeline().addFirst(new HAProxyMessageDecoder());
                }
            };

            if (BungeeCord.getInstance().getPluginManager().getEventBus().isWatchedListenerActive(1)) {
                callback.done(null, null);
            } else {
                BungeeCord.getInstance().getPluginManager().callEvent(new ConnectionInitEvent(channel.remoteAddress(), listener, callback));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
            PipelineUtils.CHANNEL_UTILITY.close(ctx.channel());
        }
    };
    public static final PipelineUtils.Base BASE = new PipelineUtils.Base();
    public static final PipelineUtils.Base CLIENT_BASE = new PipelineUtils.Base(true);
    private static final KickStringWriter legacyKicker = new KickStringWriter();
    public static final Varint21LengthFieldPrepender framePrepender = new Varint21LengthFieldPrepender();
    public static final String TIMEOUT_HANDLER = "timeout";
    public static final String PACKET_DECODER = "packet-decoder";
    public static final String PACKET_ENCODER = "packet-encoder";
    public static final String BOSS_HANDLER = "inbound-boss";
    public static final String ENCRYPT_HANDLER = "encrypt";
    public static final String DECRYPT_HANDLER = "decrypt";
    public static final String FRAME_DECODER = "frame-decoder";
    public static final String FRAME_PREPENDER = "frame-prepender";
    public static final String LEGACY_DECODER = "legacy-decoder";
    public static final String LEGACY_KICKER = "legacy-kick";
    private static boolean epoll;
    public static SocketCheck CHANNEL_UTILITY;
    private static final int LOW_MARK;
    private static final int HIGH_MARK;
    private static final WriteBufferWaterMark MARK;

    public static boolean isPublicIP(InetAddress address) {
        return !address.isAnyLocalAddress() && !address.isLoopbackAddress();
    }

    public static boolean usingEpoll() {
        return epoll;
    }

    public static EventLoopGroup newEventLoopGroup(int threads, ThreadFactory factory) {
        if (factory != null) {
            return epoll ? new EpollEventLoopGroup(threads, factory) : new NioEventLoopGroup(threads, factory);
        }
        return epoll ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads);
    }

    public static Class<? extends ServerChannel> getServerChannel(SocketAddress address) {
        if (address instanceof DomainSocketAddress) {
            Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
            return EpollServerDomainSocketChannel.class;
        }
        return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel(SocketAddress address) {
        if (address instanceof DomainSocketAddress) {
            Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
            return EpollDomainSocketChannel.class;
        }
        return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends DatagramChannel> getDatagramChannel() {
        return epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
    }

    public static ChannelFactory<? extends DatagramChannel> getDatagramChannelFactory() {
        return epoll ? EpollDatagramChannel::new : NioDatagramChannel::new;
    }

    public static void setTimeoutHandler(Channel channel, long timeout) {
        if (channel.pipeline().get(PipelineUtils.TIMEOUT_HANDLER) != null) {
            channel.pipeline()
                .replace(
                    PipelineUtils.TIMEOUT_HANDLER,
                    PipelineUtils.TIMEOUT_HANDLER,
                    XCord.getConfig().getPerformanceOptimizationsUseOptimizedTimeoutHandler()
                        ? new OptimizedReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS)
                        : new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS)
                );
        }
    }

    static {
        if (!PlatformDependent.isWindows() && Boolean.parseBoolean(System.getProperty("bungee.epoll", "true"))) {
            ProxyServer.getInstance().getLogger().info("Not on Windows, attempting to use enhanced EpollEventLoop");
            epoll = Epoll.isAvailable();
            if (epoll) {
                ProxyServer.getInstance().getLogger().info("Epoll is working, utilising it!");
                CHANNEL_UTILITY = new EpollSocketCheck();
            } else {
                ProxyServer.getInstance()
                    .getLogger()
                    .log(Level.WARNING, "Epoll is not working, falling back to NIO: {0}", Util.exception(Epoll.unavailabilityCause()));
                CHANNEL_UTILITY = new NioSocketCheck();
            }
        } else {
            CHANNEL_UTILITY = new NioSocketCheck();
        }

        LOW_MARK = Integer.getInteger("net.md_5.bungee.low_mark", 524288);
        HIGH_MARK = Integer.getInteger("net.md_5.bungee.high_mark", 2097152);
        MARK = new WriteBufferWaterMark(LOW_MARK, HIGH_MARK);
    }

    public static final class Base extends ChannelInitializer<Channel> {
        private final boolean fromClient;

        public Base(boolean fromClient) {
            this.fromClient = fromClient;
        }

        public Base() {
            this(false);
        }

        @Override
        public void initChannel(Channel channel) throws Exception {
            try {
                channel.config().setOption(ChannelOption.IP_TOS, 24);
            } catch (ChannelException ignored) {
            }

            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
            channel.config().setWriteBufferWaterMark(PipelineUtils.MARK);
            if (XCord.getConfig().getAntiDdosDuringLoginEnabled() && this.fromClient) {
                channel.pipeline()
                    .addLast(
                        PipelineUtils.FRAME_DECODER,
                        new AntiDdosFrameDecoder(
                            XCord.getConfig().getAntiDdosDuringLoginMaxBytesPerSecond(), XCord.getConfig().getAntiDdosDuringLoginMaxPacketsPerSecond()
                        )
                    );
            } else {
                channel.pipeline().addLast(PipelineUtils.FRAME_DECODER, new VarIntFrameDecoder());
            }

            channel.pipeline()
                .addLast(
                    PipelineUtils.TIMEOUT_HANDLER,
                    XCord.getConfig().getPerformanceOptimizationsUseOptimizedTimeoutHandler()
                        ? new OptimizedReadTimeoutHandler(BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS)
                        : new ReadTimeoutHandler(BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS)
                );
            channel.pipeline().addLast(PipelineUtils.FRAME_PREPENDER, PipelineUtils.framePrepender);
            channel.pipeline().addLast(PipelineUtils.BOSS_HANDLER, new HandlerBoss());
        }
    }
}
