package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.github.waterfallmc.waterfall.event.ConnectionInitEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelHandler;
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
import xcord.AntiDdosFrameDecoder;
import xcord.ConnectionStats;
import xcord.EpollSocketCheck;
import xcord.NioSocketCheck;
import xcord.OptimizedReadTimeoutHandler;
import xcord.ProxyCheckJob;
import xcord.SocketCheck;
import xcord.VarIntFrameDecoder;
import xcord.XCord;

public class PipelineUtils {
   public static final AttributeKey<ListenerInfo> LISTENER = AttributeKey.newInstance("ListerInfo");
   public static final ChannelInitializer<Channel> SERVER_CHILD = new ChannelInitializer<Channel>() {
      protected void initChannel(Channel var1) throws Exception {
         SocketAddress var2 = var1.remoteAddress() == null ? var1.parent().localAddress() : var1.remoteAddress();
         InetAddress var3 = ((InetSocketAddress)var2).getAddress();
         String var4 = var3.getHostAddress();
         int var5 = XCord.getAntiBotManager().incrementConnections();
         if (!XCord.getConfig().getSharedGlobalBlacklistUseIpset()
            && XCord.getConfig().getSharedGlobalBlacklistEnabled()
            && XCord.getGlobalBlacklist().isBlacklisted(var4)) {
            if (XCord.getConfig().getDebugDebugMode()) {
               System.out.println("[DEBUG] GBlacklisted IP " + var4 + " was rejected.");
            }

            PipelineUtils.CHANNEL_UTILITY.close(var1);
         } else if (XCord.getConfig().getAntiDdosDosIpFilterEnabled()
            && XCord.getDosFilterBlacklist().isActive()
            && XCord.getDosFilterBlacklist().isBlacklisted(var4)) {
            if (XCord.getConfig().getDebugDebugMode()) {
               System.out.println("[DEBUG] DoS-Filtered " + var4);
            }

            PipelineUtils.CHANNEL_UTILITY.close(var1);
         } else {
            if (!XCord.getConfig().isIgnoredAddress(var3)) {
               if (XCord.getBlacklistManager().isBlacklisted(var4)) {
                  if (XCord.getConfig().getDebugDebugMode()) {
                     System.out.println("[DEBUG] Blacklisted IP " + var4 + " was rejected.");
                  }

                  PipelineUtils.CHANNEL_UTILITY.close(var1);
                  return;
               }

               ConnectionStats var6 = XCord.getAntiBotManager().getStats(var4);
               if (!((ListenerInfo)var1.attr(PipelineUtils.LISTENER).get()).isProxyProtocol() && PipelineUtils.CHANNEL_UTILITY.check(var1)) {
                  if (!XCord.getConfig().getAntiBotLinuxOnlySocketCheckLowerThresholdsOfBotChecksInsteadOfBlacklist()) {
                     PipelineUtils.CHANNEL_UTILITY.close(var1);
                     return;
                  }

                  var6.setFlagged(true);
               }

               if (var6.checkSpeedyConnection(var3)) {
                  PipelineUtils.CHANNEL_UTILITY.close(var1);
                  return;
               }

               if (var5 > XCord.getConfig().getGeneralNewConnectionSpeedThreshold()) {
                  if (XCord.getConfig().getDebugDebugMode()) {
                     System.out.println("[DEBUG] XCord throttled " + var4 + " (Change in xcord.yml) due to your 'new-connection-speed-threshold' threshold.");
                  }

                  PipelineUtils.CHANNEL_UTILITY.close(var1);
                  return;
               }

               if (BungeeCord.getInstance().getConnectionThrottle() != null && BungeeCord.getInstance().getConnectionThrottle().throttle(var2)) {
                  if (XCord.getConfig().getDebugDebugMode()) {
                     System.out.println("[DEBUG] Bungeecord throttled " + var4 + " (Change in bungee.yml)");
                  }

                  PipelineUtils.CHANNEL_UTILITY.close(var1);
                  return;
               }

               if (XCord.getConfig().getLoginAntiVpnEnabled()
                  && (
                     XCord.getConfig().getLoginAntiVpnInitialConnectionCheckenabled()
                        || XCord.getConfig().getLoginAntiVpnInitialConnectionCheckonlyDuringBotAttack() && XCord.getAntiBotManager().isAttackActive()
                  )) {
                  ProxyResult var7 = XCord.getAntiProxyManager().check("NA", var4, true);
                  if (var7 == ProxyResult.DETECTED) {
                     if (XCord.getConfig().getLoginAntiVpnBlacklistOnDetection()
                        || XCord.getConfig().getLoginAntiVpnOnlyBlacklistOnDetectionDuringAttack() && XCord.getAntiBotManager().isAttackActive()) {
                        XCord.getBlacklistManager().isBlacklisted(var3);
                     }

                     PipelineUtils.CHANNEL_UTILITY.close(var1);
                     return;
                  }

                  if (var7 == null && !XCord.getConfig().getLoginAntiVpnInitialConnectionCheckcacheOnly()) {
                     XCord.getAsyncJobService().submit(new ProxyCheckJob("NA", var4, (var1x, var2x) -> {
                        if (var1.isActive() && var1x == ProxyResult.DETECTED) {
                           var1.eventLoop().execute(() -> {
                              if (var1.isActive()) {
                                 PipelineUtils.CHANNEL_UTILITY.close(var1);
                              }
                           });
                        }
                     }));
                  }
               }
            }

            ListenerInfo var9 = (ListenerInfo)var1.attr(PipelineUtils.LISTENER).get();
            if (BungeeCord.getInstance().getPluginManager().getEventBus().isWatchedListenerActive(0)
               && BungeeCord.getInstance().getPluginManager().callEvent(new ClientConnectEvent(var2, var9)).isCancelled()) {
               PipelineUtils.CHANNEL_UTILITY.close(var1);
            } else {
               Callback var10 = (var2x, var3x) -> {
                  if (var2x != null && var2x.isCancelled()) {
                     PipelineUtils.CHANNEL_UTILITY.close(var1);
                  } else {
                     try {
                        PipelineUtils.CLIENT_BASE.initChannel(var1);
                     } catch (Exception var5x) {
                        var5x.printStackTrace();
                        var1.close();
                        return;
                     }

                     if (!XCord.getConfig().getAntiBotNullPingAndPingSpamDisableLegacyPings()) {
                        var1.pipeline().addBefore("frame-decoder", "legacy-decoder", new LegacyDecoder());
                     }

                     var1.pipeline()
                        .addAfter(
                           "frame-decoder",
                           "packet-decoder",
                           new MinecraftDecoder(
                              Protocol.HANDSHAKE,
                              true,
                              ProxyServer.getInstance().getProtocolVersion(),
                              XCord.getConfig().getPerformanceOptimizationsDontCopyPackets()
                           )
                        );
                     var1.pipeline()
                        .addAfter(
                           "frame-prepender", "packet-encoder", new MinecraftEncoder(Protocol.HANDSHAKE, true, ProxyServer.getInstance().getProtocolVersion())
                        );
                     var1.pipeline().addBefore("frame-prepender", "legacy-kick", PipelineUtils.legacyKicker);
                     if (!XCord.getConfig().getAntiBotAntiHangEnabled()
                        || XCord.getConfig().getAntiBotAntiHangOnlyDuringBotAttack() && !XCord.getAntiBotManager().isAttackActive()) {
                        PipelineUtils.setTimeoutHandler(var1, (long)XCord.getConfig().getGeneralInitialTimeout());
                     } else {
                        PipelineUtils.setTimeoutHandler(var1, (long)XCord.getConfig().getAntiBotAntiHangForceTimeoutTime());
                     }

                     ((HandlerBoss)var1.pipeline().get(HandlerBoss.class)).setHandler(new InitialHandler(BungeeCord.getInstance(), var9));
                     if (var9.isProxyProtocol()) {
                        var1.pipeline().addFirst(new ChannelHandler[]{new HAProxyMessageDecoder()});
                     }
                  }
               };
               if (BungeeCord.getInstance().getPluginManager().getEventBus().isWatchedListenerActive(1)) {
                  var10.done(null, null);
               } else {
                  ConnectionInitEvent var8 = new ConnectionInitEvent(var1.remoteAddress(), var9, var10);
                  BungeeCord.getInstance().getPluginManager().callEvent(var8);
               }
            }
         }
      }

      public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
         PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
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

   public static boolean isPublicIP(InetAddress var0) {
      return !var0.isAnyLocalAddress() && !var0.isLoopbackAddress();
   }

   public static boolean usingEpoll() {
      return epoll;
   }

   public static EventLoopGroup newEventLoopGroup(int var0, ThreadFactory var1) {
      if (var1 != null) {
         return (EventLoopGroup)(epoll ? new EpollEventLoopGroup(var0, var1) : new NioEventLoopGroup(var0, var1));
      } else {
         return (EventLoopGroup)(epoll ? new EpollEventLoopGroup(var0) : new NioEventLoopGroup(var0));
      }
   }

   public static Class<? extends ServerChannel> getServerChannel(SocketAddress var0) {
      if (var0 instanceof DomainSocketAddress) {
         Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
         return EpollServerDomainSocketChannel.class;
      } else {
         return epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
      }
   }

   public static Class<? extends Channel> getChannel(SocketAddress var0) {
      if (var0 instanceof DomainSocketAddress) {
         Preconditions.checkState(epoll, "Epoll required to have UNIX sockets");
         return EpollDomainSocketChannel.class;
      } else {
         return epoll ? EpollSocketChannel.class : NioSocketChannel.class;
      }
   }

   public static Class<? extends DatagramChannel> getDatagramChannel() {
      return epoll ? EpollDatagramChannel.class : NioDatagramChannel.class;
   }

   public static ChannelFactory<? extends DatagramChannel> getDatagramChannelFactory() {
      return epoll ? EpollDatagramChannel::new : NioDatagramChannel::new;
   }

   public static void setTimeoutHandler(Channel var0, long var1) {
      if (var0.pipeline().get("timeout") != null) {
         var0.pipeline()
            .replace(
               "timeout",
               "timeout",
               (ChannelHandler)(XCord.getConfig().getPerformanceOptimizationsUseOptimizedTimeoutHandler()
                  ? new OptimizedReadTimeoutHandler(var1, TimeUnit.MILLISECONDS)
                  : new ReadTimeoutHandler(var1, TimeUnit.MILLISECONDS))
            );
      }
   }

   static {
      if (!PlatformDependent.isWindows() && Boolean.parseBoolean(System.getProperty("bungee.epoll", "true"))) {
         ProxyServer.getInstance().getLogger().info("Not on Windows, attempting to use enhanced EpollEventLoop");
         if (epoll = Epoll.isAvailable()) {
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

      public Base(boolean var1) {
         this.fromClient = var1;
      }

      public Base() {
         this(false);
      }

      public void initChannel(Channel var1) throws Exception {
         try {
            var1.config().setOption(ChannelOption.IP_TOS, 24);
         } catch (ChannelException var3) {
         }

         var1.config().setOption(ChannelOption.TCP_NODELAY, true);
         var1.config().setWriteBufferWaterMark(PipelineUtils.MARK);
         if (XCord.getConfig().getAntiDdosDuringLoginEnabled() && this.fromClient) {
            var1.pipeline()
               .addLast(
                  "frame-decoder",
                  new AntiDdosFrameDecoder(
                     XCord.getConfig().getAntiDdosDuringLoginMaxBytesPerSecond(), XCord.getConfig().getAntiDdosDuringLoginMaxPacketsPerSecond()
                  )
               );
         } else {
            var1.pipeline().addLast("frame-decoder", new VarIntFrameDecoder());
         }

         var1.pipeline()
            .addLast(
               "timeout",
               (ChannelHandler)(XCord.getConfig().getPerformanceOptimizationsUseOptimizedTimeoutHandler()
                  ? new OptimizedReadTimeoutHandler((long)BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS)
                  : new ReadTimeoutHandler((long)BungeeCord.getInstance().config.getTimeout(), TimeUnit.MILLISECONDS))
            );
         var1.pipeline().addLast("frame-prepender", PipelineUtils.framePrepender);
         var1.pipeline().addLast("inbound-boss", new HandlerBoss());
      }
   }
}
