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
import xcord.CheckType;
import xcord.ConnectionStats;
import xcord.XCord;

public class HandlerBoss extends ChannelInboundHandlerAdapter {
   private ChannelWrapper channel;
   private PacketHandler handler;

   public void setHandler(PacketHandler var1) {
      Preconditions.checkArgument(var1 != null, "handler");
      this.handler = var1;
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (this.handler != null) {
         this.channel = new ChannelWrapper(var1);
         this.handler.connected(this.channel);
         if (!(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler) && !XCord.isLogFilterEnabled()) {
            ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has connected", this.handler);
         }
      }
   }

   public void replaceIPAddress(String var1) throws Exception {
      InetSocketAddress var2 = (InetSocketAddress)this.channel.getRemoteAddress();
      InetSocketAddress var3 = new InetSocketAddress(var1, var2.getPort());
      this.channel.setRemoteAddress(var3);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      if (this.handler != null) {
         this.channel.markClosed();
         this.handler.disconnected(this.channel);
         if (!(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler) && !XCord.isLogFilterEnabled()) {
            ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has disconnected", this.handler);
         }
      }
   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      if (this.handler != null) {
         this.handler.writabilityChanged(this.channel);
      }
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      if (!(var2 instanceof HAProxyMessage)) {
         if (this.handler != null) {
            PacketWrapper var20 = (PacketWrapper)var2;
            boolean var21 = this.handler.shouldHandle(var20);

            try {
               if (var21 && var20.packet != null) {
                  try {
                     var20.packet.handle(this.handler);
                  } catch (CancelSendSignal var17) {
                     var21 = false;
                  }
               }

               if (var21) {
                  this.handler.handle(var20);
               }
            } finally {
               var20.trySingleRelease();
            }
         }
      } else {
         this.channel.setBeingForward(true);
         if (XCord.getConfig().getSecurityManagerAutoWhitelistHaproxyAddresses()) {
            XCord.getConfig().addIgnoredAddress(((InetSocketAddress)var1.channel().remoteAddress()).getAddress().getHostAddress());
         }

         HAProxyMessage var3 = (HAProxyMessage)var2;

         try {
            if (var3.sourceAddress() == null) {
               return;
            }

            InetSocketAddress var4 = new InetSocketAddress(var3.sourceAddress(), var3.sourcePort());
            if (!XCord.getAntiBotManager().isAttackActive() || XCord.getConfig().getDebugDebugMode()) {
               ProxyServer.getInstance()
                  .getLogger()
                  .log(Level.FINE, "Set remote address via PROXY {0} -> {1}", new Object[]{this.channel.getRemoteAddress(), var4});
            }

            this.channel.setRemoteAddress(var4);
            InetAddress var5 = var4.getAddress();
            String var6 = var5.getHostAddress();
            int var7 = XCord.getAntiBotManager().incrementConnections();
            if (!XCord.getConfig().getSharedGlobalBlacklistUseIpset()
               && XCord.getConfig().getSharedGlobalBlacklistEnabled()
               && XCord.getGlobalBlacklist().isBlacklisted(var6)) {
               if (XCord.getConfig().getDebugDebugMode()) {
                  System.out.println("[DEBUG] GBlacklisted IP " + var6 + " was rejected.");
               }

               PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
               return;
            }

            if (XCord.getConfig().getAntiDdosDosIpFilterEnabled()
               && XCord.getDosFilterBlacklist().isActive()
               && XCord.getDosFilterBlacklist().isBlacklisted(var6)) {
               if (XCord.getConfig().getDebugDebugMode()) {
                  System.out.println("[DEBUG] DoS-Filtered " + var6);
               }

               PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
               return;
            }

            if (XCord.getConfig().isIgnoredAddress(var5)) {
               return;
            }

            if (XCord.getBlacklistManager().isBlacklisted(var6)) {
               if (XCord.getConfig().getDebugDebugMode()) {
                  System.out.println("[DEBUG] Blacklisted IP " + var6 + " was rejected.");
               }

               PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
               return;
            }

            ConnectionStats var8 = XCord.getAntiBotManager().getStats(var6);
            if (var8.checkSpeedyConnection(var5)) {
               PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
               return;
            }

            if (var7 <= XCord.getConfig().getGeneralNewConnectionSpeedThreshold()) {
               return;
            }

            if (XCord.getConfig().getDebugDebugMode()) {
               System.out.println("[DEBUG] XCord throttled " + var6 + " (Change in xcord.yml) due to your 'new-connection-speed-threshold' threshold.");
            }

            PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
         } finally {
            var3.release();
         }
      }
   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      if (this.handler != null) {
         this.handler.readCompleted();
      }
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (var1.channel().isActive()) {
         if (XCord.getConfig().getDebugDebugMode()) {
            var2.printStackTrace();
         }

         if (var2 instanceof DecoderException || var2 instanceof IllegalStateException || var2 instanceof BadPacketException || var2 instanceof QuietException) {
            SocketAddress var3 = this.channel.getRemoteAddress();
            if (var3 != null) {
               if (this.handler != null && !(this.handler instanceof InitialHandler) && !(this.handler instanceof PingHandler)
                  || XCord.getConfig().getGeneralOnlyBlacklistPacketErrorsDuringAttack() && !XCord.getAntiBotManager().isAttackActive()) {
                  ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - exception caught Type: " + var2.getClass().getSimpleName());
               } else {
                  XCord.getBlacklistManager().isBlacklisted(var3, var2.getClass().getSimpleName());
               }
            }
         }

         if (!(this.handler instanceof PingHandler) && var2 instanceof ReadTimeoutException) {
            if (!XCord.getAntiBotManager().isAttackActive()) {
               ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - read timed out", this.handler);
            }

            if (XCord.getConfig().getAntiBotTimeoutFloodEnabled()) {
               String var8 = ((InetSocketAddress)this.channel.getRemoteAddress()).getAddress().getHostAddress();
               ConnectionStats var4 = XCord.getAntiBotManager().getStats(var8);
               long var5 = System.currentTimeMillis();
               if (var5 - var4.getLastAction() <= XCord.getConfig().getAntiBotTimeoutFloodTimeBetweenTimeouts()) {
                  XCord.getAntiBotManager()
                     .checkViolation(
                        CheckType.l, this.channel, 1, XCord.getConfig().getAntiBotTimeoutFloodBlacklistViolations(), "Time: " + (var5 - var4.getLastAction())
                     );
               }
            }
         }

         if (this.handler != null) {
            try {
               this.handler.exception(var2);
            } catch (Exception var7) {
               ProxyServer.getInstance().getLogger().log(Level.SEVERE, this.handler + " - exception processing exception", (Throwable)var7);
            }
         }

         PipelineUtils.CHANNEL_UTILITY.close(var1.channel());
      }
   }
}
