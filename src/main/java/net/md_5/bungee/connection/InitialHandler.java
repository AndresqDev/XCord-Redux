package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.waterfallmc.waterfall.utils.UUIDUtils;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.EpollTcpInfo;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.cipher.CipherDecoder;
import net.md_5.bungee.cipher.CipherEncoder;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;
import xcord.AntiDdosFrameDecoder;
import xcord.BotHandler;
import xcord.CheckType;
import xcord.ConnectionStats;
import xcord.DelayedJob;
import xcord.ExploitMonitor;
import xcord.LegacyBotHandler;
import xcord.MessageTemplate;
import xcord.NameValidator;
import xcord.ProxyCheckJob;
import xcord.SilentException;
import xcord.SpoofedServerConnection;
import xcord.VarIntFrameDecoder;
import xcord.XCord;
import xcord.XCordConfig;
import xcord.SimilarityCache.Match;

public class InitialHandler extends PacketHandler implements PendingConnection {
   private static final Random RANDOM = new Random();
   private final BungeeCord bungee;
   private ChannelWrapper ch;
   private final ListenerInfo listener;
   private Handshake handshake;
   private LoginRequest loginRequest;
   private EncryptionRequest request;
   private final Set<String> registeredChannels = new HashSet<>();
   private PluginMessage brandMessage;
   private InitialHandler.State thisState = InitialHandler.State.HANDSHAKE;
   private final Connection.Unsafe unsafe = new Connection.Unsafe() {
      @Override
      public void sendPacket(DefinedPacket var1) {
         InitialHandler.this.ch.write(var1);
      }
   };
   private boolean onlineMode = BungeeCord.getInstance().config.isOnlineMode();
   private InetSocketAddress virtualHost;
   private String name;
   private UUID uniqueId;
   private UUID offlineId;
   private LoginResult loginProfile;
   private boolean legacy;
   private String extraDataInHandshake = "";
   private BaseComponent[] delayedKickReason = null;
   private final long connectionTimestamp = System.currentTimeMillis();
   private int latency = -1;
   private boolean forge = false;
   private boolean passthroughThenKick = false;
   private transient long lastPreLegacyPingTime = -1L;
   private transient String preLegacyPingCachedMessage = null;
   private transient String prePreLegacyPingCachedMessage = null;
   private static volatile long cachedPingAge = -1L;
   private static volatile long cachedLegacyPingAge = -1L;
   private static volatile StatusResponse cachedStatusResponse = null;
   private static volatile StatusResponse cachedLegacyStatusResponse = null;
   private static final boolean ACCEPT_INVALID_PACKETS = Boolean.parseBoolean(System.getProperty("waterfall.acceptInvalidPackets", "false"));
   private static final String MOJANG_AUTH_URL = System.getProperty(
      "waterfall.auth.url", "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s%s"
   );

   @Override
   public boolean shouldHandle(PacketWrapper var1) throws Exception {
      return !this.ch.isClosing();
   }

   public boolean isPassthroughThenKick() {
      return this.passthroughThenKick;
   }

   private boolean canSendKickMessage() {
      return this.thisState == InitialHandler.State.USERNAME
         || this.thisState == InitialHandler.State.ENCRYPT
         || this.thisState == InitialHandler.State.FINISHED;
   }

   @Override
   public void connected(ChannelWrapper var1) throws Exception {
      this.ch = var1;
   }

   public void relayMessage(PluginMessage var1) throws Exception {
      if (!var1.getTag().equals("REGISTER") && !var1.getTag().equals("minecraft:register")) {
         if (var1.getTag().equals("UNREGISTER") || var1.getTag().equals("minecraft:unregister")) {
            String var7 = new String(var1.getData(), StandardCharsets.UTF_8);

            for (String var11 : var7.split("\u0000")) {
               this.registeredChannels.remove(var11);
            }
         } else if (var1.getTag().equals("MC|Brand") || var1.getTag().equals("minecraft:brand")) {
            this.brandMessage = var1;
         }
      } else {
         String var2 = new String(var1.getData(), StandardCharsets.UTF_8);

         for (String var6 : var2.split("\u0000")) {
            Preconditions.checkState(this.registeredChannels.size() < XCord.getConfig().getCompatibilityPayloadChannelLimit(), "Too many registered channels");
            Preconditions.checkArgument(var6.length() < 128, "Channel name too long");
            this.registeredChannels.add(var6);
         }
      }
   }

   @Override
   public void exception(Throwable var1) throws Exception {
      if (this.canSendKickMessage()) {
         this.disconnect(ChatColor.RED + Util.exception(var1));
      } else {
         this.ch.close();
      }
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      if (var1.packet == null) {
         throw new QuietException("Unexpected packet received during login process! " + BufUtil.dump(var1.buf, 16));
      }
   }

   @Override
   public void handle(PluginMessage var1) throws Exception {
      this.relayMessage(var1);
   }

   @Override
   public void handle(LegacyHandshake var1) throws Exception {
      this.legacy = true;
      this.ch.close(this.bungee.getTranslation("outdated_client", this.bungee.getGameVersion()));
   }

   @Override
   public void handle(LegacyPing var1) throws Exception {
      this.legacy = true;
      final boolean var2 = var1.isV1_5();
      if (this.lastPreLegacyPingTime + XCord.getConfig().getAntiBotNullPingAndPingSpamCacheMilliseconds() > System.currentTimeMillis()) {
         this.ch.close(var2 ? this.preLegacyPingCachedMessage : this.prePreLegacyPingCachedMessage);
      } else {
         ServerPing var3 = new ServerPing(
            new ServerPing.Protocol(this.bungee.getName() + " " + this.bungee.getGameVersion(), this.bungee.getProtocolVersion()),
            new ServerPing.Players(this.listener.getMaxPlayers(), this.bungee.getOnlineCount(), null),
            new TextComponent(TextComponent.fromLegacyText(this.listener.getMotd())),
            (Favicon)null
         );
         if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
            PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)XCord.getConfig().getGeneralInitialTimeout());
         }

         Callback var4 = new Callback<ProxyPingEvent>() {
            public void done(ProxyPingEvent var1, Throwable var2x) {
               if (!InitialHandler.this.ch.isClosed() && !InitialHandler.this.ch.isClosing()) {
                  ServerPing var3 = var1.getResponse();
                  InitialHandler.this.preLegacyPingCachedMessage = ChatColor.DARK_BLUE
                     + "\u0000"
                     + 127
                     + '\u0000'
                     + var3.getVersion().getName()
                     + '\u0000'
                     + InitialHandler.getFirstLine(var3.getDescription())
                     + '\u0000'
                     + (var3.getPlayers() != null ? var3.getPlayers().getOnline() : "-1")
                     + '\u0000'
                     + (var3.getPlayers() != null ? var3.getPlayers().getMax() : "-1");
                  InitialHandler.this.prePreLegacyPingCachedMessage = ChatColor.stripColor(InitialHandler.getFirstLine(var3.getDescription()))
                     + '§'
                     + (var3.getPlayers() != null ? var3.getPlayers().getOnline() : "-1")
                     + '§'
                     + (var3.getPlayers() != null ? var3.getPlayers().getMax() : "-1");
                  InitialHandler.this.lastPreLegacyPingTime = System.currentTimeMillis();
                  InitialHandler.this.ch.close(var2 ? InitialHandler.this.preLegacyPingCachedMessage : InitialHandler.this.prePreLegacyPingCachedMessage);
               }
            }
         };
         this.bungee.getPluginManager().callEvent(new ProxyPingEvent(this, var3, var4));
      }
   }

   private static String getFirstLine(String var0) {
      int var1 = var0.indexOf(10);
      return var1 == -1 ? var0 : var0.substring(0, var1);
   }

   private ServerPing getPingInfo(String var1, int var2) {
      return new ServerPing(
         new ServerPing.Protocol(this.bungee.getName() + " " + this.bungee.getGameVersion(), var2),
         new ServerPing.Players(this.listener.getMaxPlayers(), this.bungee.getOnlineCount(), null),
         var1,
         BungeeCord.getInstance().config.getFaviconObject()
      );
   }

   @Override
   public void handle(StatusRequest var1) throws Exception {
      Preconditions.checkState(this.thisState == InitialHandler.State.STATUS, "Not expecting STATUS");
      if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
         PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)XCord.getConfig().getGeneralInitialTimeout());
      }

      ServerInfo var2 = AbstractReconnectHandler.getForcedHost(this);
      String var3 = var2 != null ? var2.getMotd() : this.listener.getMotd();
      final int var4 = ProtocolConstants.SUPPORTED_VERSION_IDS.contains(this.handshake.getProtocolVersion())
         ? this.handshake.getProtocolVersion()
         : this.bungee.getProtocolVersion();
      boolean var5 = ProtocolConstants.isBeforeOrEq(this.handshake.getProtocolVersion(), 47);
      boolean var6 = XCord.getConfig().getAntiBotNullPingAndPingSpamCachePingMotd()
         && (!XCord.getConfig().getAntiBotNullPingAndPingSpamOnlyCacheDuringBotAttack() || XCord.getAntiBotManager().isAttackActive());
      final boolean var7;
      final boolean var8;
      if (var6
         && (var5 ? cachedLegacyPingAge : cachedPingAge) + XCord.getConfig().getAntiBotNullPingAndPingSpamCacheMilliseconds() < System.currentTimeMillis()) {
         var7 = true;
         var8 = false;
      } else {
         var7 = false;
         var8 = var6;
      }

      Callback var9 = new Callback<ServerPing>() {
         public void done(ServerPing var1, Throwable var2) {
            if (var2 != null) {
               var1 = InitialHandler.this.getPingInfo(InitialHandler.this.bungee.getTranslation("ping_cannot_connect"), var4);
               InitialHandler.this.bungee.getLogger().log(Level.WARNING, "Error pinging remote server", var2);
            }

            Callback var3 = new Callback<ProxyPingEvent>() {
               public void done(ProxyPingEvent var1, Throwable var2) {
                  Gson var3 = InitialHandler.this.handshake.getProtocolVersion() == 4 ? BungeeCord.getInstance().gsonLegacy : BungeeCord.getInstance().gson;
                  if (InitialHandler.this.bungee.getConnectionThrottle() != null) {
                     InitialHandler.this.bungee.getConnectionThrottle().unthrottle(InitialHandler.this.getSocketAddress());
                  }

                  StatusResponse var4x;
                  if (ProtocolConstants.isBeforeOrEq(InitialHandler.this.handshake.getProtocolVersion(), 47)) {
                     if (var8) {
                        var4x = new StatusResponse(InitialHandler.this.swapJSONProtocol(InitialHandler.cachedLegacyStatusResponse.getResponse(), var4));
                     } else {
                        JsonElement var5 = var3.toJsonTree(var1.getResponse());
                        Preconditions.checkArgument(var5.isJsonObject(), "Response is not a JSON object");
                        JsonObject var6 = var5.getAsJsonObject();
                        var6.addProperty("description", var1.getResponse().getDescription());
                        var4x = new StatusResponse(var3.toJson(var5));
                        if (var7) {
                           InitialHandler.cachedLegacyPingAge = System.currentTimeMillis();
                           InitialHandler.cachedLegacyStatusResponse = var4x;
                        }
                     }
                  } else if (var8) {
                     var4x = new StatusResponse(InitialHandler.this.swapJSONProtocol(InitialHandler.cachedStatusResponse.getResponse(), var4));
                  } else {
                     var4x = new StatusResponse(var3.toJson(var1.getResponse()));
                     if (var7) {
                        InitialHandler.cachedPingAge = System.currentTimeMillis();
                        InitialHandler.cachedStatusResponse = var4x;
                     }
                  }

                  InitialHandler.this.unsafe.sendPacket(var4x);
               }
            };
            if (!var8) {
               InitialHandler.this.bungee.getPluginManager().callEvent(new ProxyPingEvent(InitialHandler.this, var1, var3));
            } else {
               var3.done(new ProxyPingEvent(InitialHandler.this, var1, var3), null);
            }
         }
      };
      if (var2 != null && this.listener.isPingPassthrough() && !var8) {
         ((BungeeServerInfo)var2).ping(var9, this.handshake.getProtocolVersion());
      } else {
         var9.done(this.getPingInfo(var3, var4), null);
      }

      this.thisState = InitialHandler.State.PING;
   }

   private String swapJSONProtocol(String var1, int var2) {
      String var3 = "\"protocol\":";
      int var4 = var1.indexOf("\"protocol\":");
      if (var4 <= 0) {
         return "none";
      } else {
         var4 += "\"protocol\":".length();
         String var5 = var1.substring(0, var4);
         int var6 = var5.length() + 1;

         while (Character.isDigit(var1.charAt(var6))) {
            var6++;
         }

         return var5 + var2 + var1.substring(var6);
      }
   }

   @Override
   public void handle(PingPacket var1) throws Exception {
      if (!ACCEPT_INVALID_PACKETS) {
         Preconditions.checkState(this.thisState == InitialHandler.State.PING, "Not expecting PING");
      }

      this.unsafe.sendPacket(var1);
      this.disconnect("");
   }

   @Override
   public void handle(Handshake var1) throws Exception {
      Preconditions.checkState(this.thisState == InitialHandler.State.HANDSHAKE, "Not expecting HANDSHAKE");
      String var2 = ((InetSocketAddress)this.ch.getRemoteAddress()).getAddress().getHostAddress();
      if (XCord.getConfig().getAntiBotAntiHangEnabled()) {
         PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)XCord.getConfig().getGeneralInitialTimeout());
      }

      this.latency = (int)(System.currentTimeMillis() - this.connectionTimestamp);
      this.handshake = var1;
      this.ch.setVersion(var1.getProtocolVersion());
      XCordConfig var3 = XCord.getConfig();
      if (var3.getAntiBotDnsCheck()
         && (
            var1.getHost().contains(BungeeCord.serverAddress)
               || XCord.getConfig().getAntiBotDnsCheckIsStrict() && (var1.getHost().contains("localhost") || var1.getHost().contains("127.0.0.1"))
         )) {
         if (XCord.getAntiBotManager().isAttackActive()) {
            if (this.ch.getRemoteAddress() != null) {
               XCord.getBlacklistManager()
                  .isBlacklisted(
                     this.ch.getRemoteAddress(),
                     "failed DNS check (Strict: " + (var1.getHost().contains("localhost") || var1.getHost().contains("127.0.0.1")) + ")"
                  );
               throw new SilentException("DNS check");
            }
         } else if (!XCord.getConfig().isIgnoredAddress(((InetSocketAddress)this.ch.getRemoteAddress()).getAddress())) {
            BungeeCord.getInstance().getLogger().info(this.ch.getRemoteAddress().toString() + " connected through your IP (Not domain, Abnormal behavior)");
         }
      }

      if (var3.getAntiBotUnstableCheckEnabled() && this.latency >= var3.getAntiBotUnstableCheckLatencyThreshold()) {
         if (XCord.getAntiBotManager().isAttackActive()) {
            if (XCord.getAntiBotManager()
               .checkViolation(CheckType.g, this.ch, 1, XCord.getConfig().getAntiBotUnstableCheckBlacklistViolations(), "Latency: " + this.latency)) {
               if (!var3.getAntiBotUnstableCheckDelayKickForReason() || this.canSendKickMessage()) {
                  this.ch.close();
                  return;
               }

               this.delayedKickReason = XCord.getMessages().getFailedConnection().getComponents();
            }
         } else if (!XCord.isLogFilterEnabled()) {
            BungeeCord.getInstance().getLogger().info(var2 + " connected abnormally> block-latency: " + this.latency);
         }
      }

      if (var1.getHost().contains("\u0000")) {
         if (var1.getHost().contains("FML")) {
            this.forge = true;
         }

         String[] var4 = var1.getHost().split("\u0000", 2);
         var1.setHost(var4[0]);
         this.extraDataInHandshake = "\u0000" + var4[1];
      }

      if (var1.getHost().endsWith(".")) {
         var1.setHost(var1.getHost().substring(0, var1.getHost().length() - 1));
      }

      this.virtualHost = InetSocketAddress.createUnresolved(var1.getHost(), var1.getPort());
      if (this.bungee.getPluginManager().getEventBus().isWatchedListenerActive(2)) {
         this.bungee.getPluginManager().callEvent(new PlayerHandshakeEvent(this, var1));
         if (!var2.equals(((InetSocketAddress)this.ch.getRemoteAddress()).getAddress().getHostAddress())) {
            this.ch.setBeingForward(true);
         }
      }

      switch (var1.getRequestedProtocol()) {
         case 1:
            if (this.bungee.getConfig().isLogPings() && !XCord.isLogFilterEnabled()) {
               this.bungee.getLogger().log(Level.INFO, "{0} has pinged", this);
            }

            this.thisState = InitialHandler.State.STATUS;
            this.ch.setProtocol(Protocol.STATUS);
            break;
         case 2:
            if (BungeeCord.getInstance().getConfig().isLogInitialHandlerConnections() && !XCord.isLogFilterEnabled()) {
               this.bungee.getLogger().log(Level.INFO, "{0} has connected", this);
            }

            this.thisState = InitialHandler.State.USERNAME;
            this.ch.setProtocol(Protocol.LOGIN);
            if (this.delayedKickReason != null) {
               this.disconnect(this.delayedKickReason);
               return;
            }

            SocketAddress var10 = this.ch.getRemoteAddress();
            if (var10 != null && !this.ch.isBeingForward()) {
               InetSocketAddress var5 = (InetSocketAddress)var10;
               InetAddress var6 = ((InetSocketAddress)var10).getAddress();
               ConnectionStats var7 = XCord.getAntiBotManager().getStats(var6.getHostAddress());
               if (var7.getLastActionInt() > 0 && XCord.getAntiBotManager().isAttackActive()) {
                  int var8 = Math.abs(var5.getPort() - var7.getLastActionInt());
                  if (var8 > 4000
                     && var8 < 10000
                     && !XCord.getAntiBotManager().isWhitelisted(var6.getHostAddress())
                     && System.currentTimeMillis() - var7.getLastSeen() < 10000L
                     && XCord.getConfig().getAntiBotLinuxCheck()) {
                     InetAddress var9 = var5.getAddress();
                     this.disconnect(XCord.getMessages().getLinuxCheckFail(), XCord.getConfig().getGeneralKickTimeout());
                     XCord.getBlacklistManager().disconnect(var9, XCord.getMessages().getLinuxCheckFail());
                     BungeeCord.getInstance().getLogger().info(var5.getAddress().getHostAddress() + " Suspected-Bot: Linux-OS(" + var8 + ")");
                     return;
                  }
               }

               if (var7.checkSpeedyLogin(var6)) {
                  this.disconnect(XCord.getMessages().getSpeedJoinFail(), XCord.getConfig().getGeneralKickTimeout());
                  return;
               }

               var7.setLastAction(var5.getPort());
            }

            if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(var1.getProtocolVersion())) {
               if (var1.getProtocolVersion() > this.bungee.getProtocolVersion()) {
                  this.disconnect(this.bungee.getTranslation("outdated_server", this.bungee.getGameVersion()));
               } else {
                  this.disconnect(this.bungee.getTranslation("outdated_client", this.bungee.getGameVersion()));
               }

               return;
            }
            break;
         default:
            throw new QuietException("Cannot request protocol " + var1.getRequestedProtocol());
      }
   }

   @Override
   public void handle(LoginRequest var1) throws Exception {
      Preconditions.checkState(this.thisState == InitialHandler.State.USERNAME, "Not expecting USERNAME");
      this.loginRequest = var1;
      if (this.getName().length() > 16) {
         this.disconnect(this.bungee.getTranslation("name_too_long"), XCord.getConfig().getGeneralKickTimeout());
      } else if (!NameValidator.isValidName(var1.getData(), XCord.getConfig().getGeneralAlwaysCheckAllowedNameCharacters() || this.onlineMode)) {
         this.disconnect(this.bungee.getTranslation("name_invalid"), XCord.getConfig().getGeneralKickTimeout());
      } else if (this.getName().length() < XCord.getConfig().getGeneralMinimumNameLength()) {
         this.disconnect(XCord.getMessages().getShortName(), XCord.getConfig().getGeneralKickTimeout());
      } else {
         if (XCord.getConfig().getAntiBotStrangeNameEnabled() && XCord.getAntiBotManager().isAttackActive()) {
            int var2 = 0;
            int var3 = 0;
            char[] var4 = var1.getData().toCharArray();

            for (int var5 = 1; var5 < var4.length; var5++) {
               char var6 = var4[var5 - 1];
               char var7 = var4[var5];
               boolean var8 = Character.isDigit(var6);
               boolean var9 = Character.isDigit(var7);
               if (var8 && !var9) {
                  var2++;
               }

               if (!var8 && !var9 && Character.isLowerCase(var6) != Character.isLowerCase(var7)) {
                  var3++;
               }
            }

            if (XCord.getConfig().getDebugDebugMode()) {
               BungeeCord.getInstance().getLogger().info("Strange-Name Data> randomCases: " + var3 + " randomNumbers: " + var2);
            }

            if (var3 >= XCord.getConfig().getAntiBotStrangeNameRandomCapitalCount() && var2 >= XCord.getConfig().getAntiBotStrangeNameRandomNumberCount()) {
               if (XCord.getAntiBotManager()
                  .checkViolation(CheckType.i, this.ch, 1, XCord.getConfig().getAntiBotStrangeNameBlacklistViolations(), var3 + "/" + var2)) {
                  this.disconnect(XCord.getMessages().getFailedConnection(), XCord.getConfig().getGeneralKickTimeout());
               }

               return;
            }
         }

         if (XCord.getConfig().getAntiBotNamePatternEnabled()
            && XCord.getAntiBotManager().isAttackActive()
            && XCord.getConfig().getAntiBotNamePatternMinimumNameLength() <= var1.getData().length()) {
            Match var11 = XCord.getAntiBotManager().getSimilarNameCache().findClosest(var1.getData());
            if (var11 != null) {
               if (XCord.getConfig().getDebugDebugMode()) {
                  System.out.println("-> NamePatternCheck result: Tested=" + var1.getData() + " Matched=" + var11.getValue() + " %" + var11.getScore());
               }

               if (var11.getScore() >= XCord.getConfig().getAntiBotNamePatternMinimumMatchPercent()
                  && !XCord.getAntiBotManager().isWhitelisted(this.getName(), ((InetSocketAddress)this.ch.getRemoteAddress()).getAddress().getHostAddress())) {
                  System.out.println("Name-Pattern detection> user=" + var1.getData() + " Matched=" + var11.getValue() + " %=" + var11.getScore());
                  if (XCord.getAntiBotManager()
                     .checkViolation(
                        CheckType.m, this.ch, 1, XCord.getConfig().getAntiBotNamePatternBlacklistViolations(), var11.getValue() + ":%" + var11.getScore()
                     )) {
                     this.disconnect(XCord.getMessages().getFailedConnection(), XCord.getConfig().getGeneralKickTimeout());
                  }

                  return;
               }
            }
         }

         if (XCord.getConfig().getIpSpooferSpoofListedUserIps() && XCord.getConfig().getSpoofedUsers().contains(this.getName())) {
            try {
               InetSocketAddress var12 = (InetSocketAddress)this.ch.getRemoteAddress();
               String var15 = XCord.getConfig().getIpSpoofer7222928185();
               ((HandlerBoss)this.ch.getHandle().pipeline().get(HandlerBoss.class)).replaceIPAddress(var15);
               if (XCord.getConfig().getIpSpooferLogOnSpoof()) {
                  BungeeCord.getInstance().getLogger().info("XCord spoofed " + var12.getHostName() + " with " + var15 + " for " + this.getName());
               }
            } catch (Exception var10) {
               System.out.println("Failed to replace IP for " + this.getName());
               var10.printStackTrace();
            }
         }

         if (BungeeCord.getInstance().config.isEnforceSecureProfile()) {
            PlayerPublicKey var13 = var1.getPublicKey();
            if (var13 == null) {
               this.disconnect(this.bungee.getTranslation("secure_profile_required"));
               return;
            }

            if (Instant.ofEpochMilli(var13.getExpiry()).isBefore(Instant.now())) {
               this.disconnect(this.bungee.getTranslation("secure_profile_expired"));
               return;
            }

            if (this.getVersion() < 760 && !EncryptionUtil.check(var13, null)) {
               this.disconnect(this.bungee.getTranslation("secure_profile_invalid"));
               return;
            }
         }

         int var14 = BungeeCord.getInstance().config.getPlayerLimit();
         if (var14 > 0 && this.bungee.getOnlineCount() > var14) {
            this.disconnect(this.bungee.getTranslation("proxy_full"));
         } else if (!this.isOnlineMode() && this.bungee.getPlayer(this.getUniqueId()) != null) {
            this.disconnect(this.bungee.getTranslation("already_connected_proxy"));
         } else {
            Callback var16 = new Callback<PreLoginEvent>() {
               public void done(PreLoginEvent var1, Throwable var2) {
                  if (var1.isCancelled()) {
                     BaseComponent[] var3 = var1.getCancelReasonComponents();
                     InitialHandler.this.disconnect(
                        var3 != null ? var3 : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message"))
                     );
                  } else if (!InitialHandler.this.ch.isClosed()) {
                     if (InitialHandler.this.onlineMode) {
                        InitialHandler.this.thisState = InitialHandler.State.ENCRYPT;
                        InitialHandler.this.unsafe().sendPacket(InitialHandler.this.request = EncryptionUtil.encryptRequest());
                     } else {
                        InitialHandler.this.thisState = InitialHandler.State.FINISHED;
                        InitialHandler.this.finish();
                     }
                  }
               }
            };
            if (XCord.getConfig().getAntiBotPassthroughVerifyThenReconnectEnabled()
               && XCord.getAntiBotManager().isAttackActive()
               && !XCord.getAntiBotManager().isWhitelisted(this.getName(), this.getAddress().getAddress().getHostAddress())) {
               this.passthroughThenKick = true;
            }

            if (!this.passthroughThenKick) {
               this.bungee.getPluginManager().callEvent(new PreLoginEvent(this, var16));
            } else {
               this.onlineMode = false;
               this.thisState = InitialHandler.State.FINISHED;
               this.finish();
            }
         }
      }
   }

   @Override
   public void handle(EncryptionResponse var1) throws Exception {
      Preconditions.checkState(this.thisState == InitialHandler.State.ENCRYPT, "Not expecting ENCRYPT");
      Preconditions.checkState(EncryptionUtil.check(this.loginRequest.getPublicKey(), var1, this.request), "Invalid verification");
      this.thisState = InitialHandler.State.FINISHED;
      SecretKey var2 = EncryptionUtil.getSecret(var1, this.request);
      if (var2 instanceof SecretKeySpec && var2.getEncoded().length != 16) {
         this.ch.close();
      } else {
         BungeeCipher var3 = EncryptionUtil.getCipher(false, var2);
         this.ch.addBefore("frame-decoder", "decrypt", new CipherDecoder(var3));
         BungeeCipher var4 = EncryptionUtil.getCipher(true, var2);
         this.ch.addBefore("frame-prepender", "encrypt", new CipherEncoder(var4));
         String var5 = URLEncoder.encode(this.getName(), "UTF-8");
         MessageDigest var6 = MessageDigest.getInstance("SHA-1");

         for (byte[] var10 : new byte[][]{this.request.getServerId().getBytes("ISO_8859_1"), var2.getEncoded(), EncryptionUtil.keys.getPublic().getEncoded()}) {
            var6.update(var10);
         }

         String var11 = URLEncoder.encode(new BigInteger(var6.digest()).toString(16), "UTF-8");
         String var12 = BungeeCord.getInstance().config.isPreventProxyConnections() && this.getSocketAddress() instanceof InetSocketAddress
            ? "&ip=" + URLEncoder.encode(this.getAddress().getAddress().getHostAddress(), "UTF-8")
            : "";
         String var13 = String.format(MOJANG_AUTH_URL, var5, var11, var12);
         Callback var14 = new Callback<String>() {
            public void done(String var1, Throwable var2) {
               if (var2 == null) {
                  LoginResult var3 = (LoginResult)BungeeCord.getInstance().gson.fromJson(var1, LoginResult.class);
                  if (var3 != null && var3.getId() != null) {
                     InitialHandler.this.loginProfile = var3;
                     InitialHandler.this.name = var3.getName();
                     InitialHandler.this.uniqueId = Util.getUUID(var3.getId());
                     InitialHandler.this.finish();
                     return;
                  }

                  InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("offline_mode_player"));
                  if (XCord.getConfig().getDebugDebugMode()) {
                     BungeeCord.getInstance().getLogger().info("Failed online-mode validation on " + InitialHandler.this.getName() + ", response: " + var1);
                  }
               } else {
                  InitialHandler.this.disconnect(InitialHandler.this.bungee.getTranslation("mojang_fail"));
                  InitialHandler.this.bungee
                     .getLogger()
                     .log(Level.SEVERE, "Error authenticating " + InitialHandler.this.getName() + " with minecraft.net", var2);
               }
            }
         };
         HttpClient.get(var13, this.ch.getHandle().eventLoop(), var14);
      }
   }

   private void finish() {
      if (this.isOnlineMode()) {
         ProxiedPlayer var1 = this.bungee.getPlayer(this.getName());
         if (var1 != null) {
            var1.disconnect(this.bungee.getTranslation("already_connected_proxy"));
         }

         ProxiedPlayer var2 = this.bungee.getPlayer(this.getUniqueId());
         if (var2 != null) {
            var2.disconnect(this.bungee.getTranslation("already_connected_proxy"));
         }
      } else {
         ProxiedPlayer var6 = this.bungee.getPlayer(this.getName());
         if (var6 != null) {
            this.disconnect(this.bungee.getTranslation("already_connected_proxy"));
            return;
         }
      }

      this.offlineId = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.getName()).getBytes(Charsets.UTF_8));
      if (this.uniqueId == null) {
         this.uniqueId = this.offlineId;
      }

      if (BungeeCord.getInstance().config.isEnforceSecureProfile() && this.getVersion() >= 760) {
         boolean var7 = false;

         try {
            var7 = EncryptionUtil.check(this.loginRequest.getPublicKey(), this.uniqueId);
         } catch (GeneralSecurityException var5) {
         }

         if (!var7) {
            if (XCord.getAntiBotManager().isAttackActive()) {
               XCord.getBlacklistManager().isBlacklisted(this.getAddress(), "Invalid secure profile");
            } else {
               this.disconnect(this.bungee.getTranslation("secure_profile_invalid"));
            }

            return;
         }
      }

      PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)BungeeCord.getInstance().config.getTimeout());
      String var8 = ((InetSocketAddress)this.ch.getRemoteAddress()).getAddress().getHostAddress();
      final boolean var9 = XCord.getAntiBotManager().isWhitelisted(this.getName(), var8);
      Callback var3 = new Callback<LoginEvent>() {
         public void done(LoginEvent var1, Throwable var2) {
            if (var1.isCancelled()) {
               BaseComponent[] var3 = var1.getCancelReasonComponents();
               InitialHandler.this.disconnect(var3 != null ? var3 : TextComponent.fromLegacyText(InitialHandler.this.bungee.getTranslation("kick_message")));
            } else if (!InitialHandler.this.ch.isClosed() && !InitialHandler.this.ch.isClosing()) {
               InitialHandler.this.ch
                  .getHandle()
                  .eventLoop()
                  .execute(
                     new Runnable() {
                        @Override
                        public void run() {
                           if (!InitialHandler.this.ch.isClosing()) {
                              UserConnection var1 = new UserConnection(
                                 InitialHandler.this.bungee, InitialHandler.this.ch, InitialHandler.this.getName(), InitialHandler.this
                              );
                              var1.setCompressionThreshold(BungeeCord.getInstance().config.getCompressionThreshold());
                              var1.setWhitelistedFromAntibot(var9);
                              var1.setUsingForge(InitialHandler.this.forge);
                              var1.init();
                              InitialHandler.this.unsafe
                                 .sendPacket(
                                    new LoginSuccess(
                                       InitialHandler.this.getUniqueId(),
                                       InitialHandler.this.getName(),
                                       InitialHandler.this.loginProfile == null ? null : InitialHandler.this.loginProfile.getProperties()
                                    )
                                 );
                              InitialHandler.this.ch.setProtocol(Protocol.GAME);
                              Object var2 = XCord.getConfig().getExploitMonitorEnabled()
                                 ? new ExploitMonitor(InitialHandler.this.bungee, var1)
                                 : new UpstreamBridge(InitialHandler.this.bungee, var1);
                              ((UpstreamBridge)var2).setHandleDisconnects(false);
                              ((HandlerBoss)InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler((PacketHandler)var2);
                              AtomicBoolean var3 = new AtomicBoolean(false);
                              Runnable var4 = () -> {
                                 if (!InitialHandler.this.ch.isClosed() && !var3.getAndSet(true)) {
                                    if (InitialHandler.this.passthroughThenKick) {
                                       XCord.getAntiBotManager()
                                          .whitelist(InitialHandler.this.getName(), InitialHandler.this.getAddress().getAddress().getHostAddress());
                                       InitialHandler.this.disconnect(XCord.getConfig().getSuccessfulVerifyKickMessage());
                                    } else {
                                       if (var1.getServer() instanceof SpoofedServerConnection) {
                                          var1.setPassedThroughSpoofedServer(true);
                                          var1.setServer(null);
                                       }

                                       if (XCord.getConfig().getAntiBotVerifyV2Enabled() && var1.isDisableEntityMetadataRewrite()) {
                                          var1.setJoinRequiresRewrite(true);
                                       }

                                       var2.setHandleDisconnects(true);
                                       if (XCord.getConfig().getAntiDdosDuringLoginEnabled()) {
                                          if (XCord.getConfig().getAntiDdosDuringGameplayEnabled()) {
                                             InitialHandler.this.ch
                                                .getHandle()
                                                .pipeline()
                                                .replace(
                                                   "frame-decoder",
                                                   "frame-decoder",
                                                   new AntiDdosFrameDecoder(
                                                         XCord.getConfig().getAntiDdosDuringGameplayMaxBytesPerSecond(),
                                                         XCord.getConfig().getAntiDdosDuringGameplayMaxPacketsPerSecond()
                                                      )
                                                      .setFirstPacketCheck(false)
                                                );
                                          } else {
                                             InitialHandler.this.ch.getHandle().pipeline().replace("frame-decoder", "frame-decoder", new VarIntFrameDecoder());
                                          }
                                       } else if (XCord.getConfig().getAntiDdosDuringGameplayEnabled()) {
                                          InitialHandler.this.ch
                                             .getHandle()
                                             .pipeline()
                                             .replace(
                                                "frame-decoder",
                                                "frame-decoder",
                                                new AntiDdosFrameDecoder(
                                                      XCord.getConfig().getAntiDdosDuringGameplayMaxBytesPerSecond(),
                                                      XCord.getConfig().getAntiDdosDuringGameplayMaxPacketsPerSecond()
                                                   )
                                                   .setFirstPacketCheck(false)
                                             );
                                       }

                                       BungeeCord.getInstance().addConnection(var1);
                                       var1.getTabListHandler().onConnect();
                                       var1.unsafe().sendPacket(BungeeCord.getInstance().registerChannels(InitialHandler.this.getVersion()));
                                       ((HandlerBoss)InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler(var2);
                                       InitialHandler.this.bungee.getPluginManager().callEvent(new PostLoginEvent(var1));
                                       ServerInfo var4x;
                                       if (InitialHandler.this.bungee.getReconnectHandler() != null) {
                                          var4x = InitialHandler.this.bungee.getReconnectHandler().getServer(var1);
                                       } else {
                                          var4x = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
                                       }

                                       if (var4x == null) {
                                          var4x = InitialHandler.this.bungee.getServerInfo(InitialHandler.this.listener.getDefaultServer());
                                       }

                                       var1.connect(var4x, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
                                       InitialHandler.this.thisState = InitialHandler.State.FINISHED;
                                    }
                                 }
                              };
                              Runnable var5 = !XCord.getConfig().getLoginAntiVpnEnabled()
                                 ? null
                                 : () -> {
                                    InetAddress var3x = ((InetSocketAddress)InitialHandler.this.ch.getRemoteAddress()).getAddress();
                                    Callback var4x = (var4xx, var5xx) -> {
                                       if (!InitialHandler.this.ch.isClosed() && var4xx == ProxyResult.DETECTED) {
                                          InitialHandler.this.fireQuitEvent(var1);
                                          InitialHandler.this.ch.getHandle().eventLoop().execute(() -> var1.disconnect(XCord.getMessages().getVpn()));
                                          if (XCord.getConfig().getLoginAntiVpnBlacklistOnDetection()
                                             || XCord.getConfig().getLoginAntiVpnOnlyBlacklistOnDetectionDuringAttack()
                                                && XCord.getAntiBotManager().isAttackActive()) {
                                             XCord.getBlacklistManager().isBlacklisted(var3x);
                                             XCord.getBlacklistManager().disconnect(var3x, XCord.getMessages().getVpn());
                                          }
                                       } else {
                                          InitialHandler.this.ch.getHandle().eventLoop().execute(var4);
                                       }
                                    };
                                    ProxyResult var5x = XCord.getAntiProxyManager().check(InitialHandler.this.getName(), var3x.getHostAddress(), true);
                                    if (var5x == null) {
                                       XCord.getAsyncJobService().submit(new ProxyCheckJob(InitialHandler.this.getName(), var3x.getHostAddress(), var4x));
                                    } else {
                                       var4x.done(var5x, null);
                                    }
                                 };
                              if (XCord.getConfig().getAntiBotVerifyV2Enabled()
                                 && !var9
                                 && (!XCord.getConfig().getAntiBotVerifyV2Ignore116PlusVersions() || InitialHandler.this.getVersion() < 735)
                                 && (!XCord.getConfig().getAntiBotVerifyV2IgnoreGeyser() || InitialHandler.this.getUniqueId().getMostSignificantBits() != 0L)) {
                                 int var6 = InitialHandler.RANDOM.nextInt(5000) + 1;
                                 Runnable var7 = var5 != null ? var5 : var4;
                                 boolean var8 = InitialHandler.this.ch.getHandle() instanceof EpollSocketChannel
                                    && !InitialHandler.this.ch.isBeingForward()
                                    && InitialHandler.this.getUniqueId().getMostSignificantBits() != 0L
                                    && ((InetSocketAddress)InitialHandler.this.ch.getRemoteAddress())
                                       .getAddress()
                                       .getHostAddress()
                                       .contains(((InetSocketAddress)InitialHandler.this.ch.getHandle().remoteAddress()).getAddress().getHostAddress())
                                    && !XCord.getAntiProxyManager().getWhitelist().isUsernameWhitelisted(InitialHandler.this.getName())
                                    && !XCord.getAntiProxyManager()
                                       .getWhitelist()
                                       .isEntryWhitelisted(((InetSocketAddress)InitialHandler.this.ch.getRemoteAddress()).getAddress().getHostAddress());
                                 if (var8
                                    && XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesproxyCheck1Enabled()
                                    && InitialHandler.this.ch.getHandle().isActive()
                                    && PipelineUtils.CHANNEL_UTILITY.isSuspiciousMss(InitialHandler.this.ch.getHandle())) {
                                    EpollTcpInfo var11 = ((EpollSocketChannel)InitialHandler.this.ch.getHandle()).tcpInfo();
                                    ConnectionStats var12 = XCord.getAntiBotManager()
                                       .getStats(((InetSocketAddress)InitialHandler.this.ch.getRemoteAddress()).getAddress().getHostAddress());
                                    InitialHandler.this.fireQuitEvent(var1);
                                    if (var12.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                                       XCord.getBlacklistManager().isBlacklisted(var1.getAddress(), "VerifyV2-Block-Proxies");
                                       XCord.getBlacklistManager().forceClose(var1.getAddress().getAddress());
                                    } else {
                                       InitialHandler.this.disconnect(XCord.getMessages().getVpn(), XCord.getConfig().getGeneralKickTimeout());
                                       System.out
                                          .println(
                                             "Disconnected "
                                                + InitialHandler.this.getName()
                                                + ", failed verification (VerifyV2): 'block-potential-proxies-1' [Disable in XCord.yml if incorrect] -> "
                                                + (var11.advmss() - var11.sndMss())
                                          );
                                    }

                                    return;
                                 }

                                 Object var9x = InitialHandler.this.getVersion() > 5
                                    ? new BotHandler(InitialHandler.this, var7, var1, var6, InitialHandler.this.getVersion())
                                    : new LegacyBotHandler(InitialHandler.this, var7, var1, var6, InitialHandler.this.getVersion());
                                 var9x.setAdvancedCheck(var8 && XCord.getConfig().getAntiBotVerifyV2BlockPotentialProxiesproxyCheck2Enabled());
                                 DelayedJob var10 = new DelayedJob(
                                    System.currentTimeMillis() + (long)XCord.getConfig().getAntiBotVerifyV2MaxCheckTime(),
                                    () -> {
                                       if (!InitialHandler.this.ch.isClosed()
                                          && !InitialHandler.this.ch.isClosing()
                                          && (!var9x.isVerified() || var9x.getCallback() != null)) {
                                          ConnectionStats var3x = XCord.getAntiBotManager().getStats(var1.getAddress().getHostString());
                                          if (var3x.addViolations(CheckType.VERIFY, 1) > XCord.getConfig().getAntiBotVerifyV2AdvancedLobbyCheckMaxAttempts()) {
                                             XCord.getBlacklistManager().isBlacklisted(var1.getAddress(), "Verify-timeout");
                                             XCord.getBlacklistManager().forceClose(var1.getAddress().getAddress());
                                          } else {
                                             InitialHandler.this.ch.getHandle().eventLoop().execute(() -> {
                                                InitialHandler.this.fireQuitEvent(var1);
                                                var1.disconnect(XCord.getMessages().getFailedConnection());
                                             });
                                             System.out
                                                .println("Disconnected " + InitialHandler.this.getName() + ", failed verification: " + var9x.getDebugInfo());
                                          }
                                       }
                                    }
                                 );
                                 ((HandlerBoss)InitialHandler.this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler((PacketHandler)var9x);
                                 XCord.getAsyncJobService().submit(var10);
                              } else if (var5 != null) {
                                 var5.run();
                              } else {
                                 var4.run();
                              }
                           }
                        }
                     }
                  );
            }
         }
      };
      LoginEvent var4 = new LoginEvent(this, var3, this.getLoginProfile());
      if (!this.passthroughThenKick) {
         this.bungee.getPluginManager().callEvent(var4);
      } else {
         var3.done(var4, null);
      }
   }

   public void fireQuitEvent(UserConnection var1) {
      if (!this.passthroughThenKick && XCord.getConfig().getCompatibilityAlwaysFireQuitEvent()) {
         try {
            PlayerDisconnectEvent var2 = new PlayerDisconnectEvent(var1);
            BungeeCord.getInstance().getPluginManager().callEvent(var2);
         } catch (Exception var3) {
            if (XCord.getConfig().getDebugDebugMode()) {
               var3.printStackTrace();
            }
         }
      }
   }

   @Override
   public void handle(LoginPayloadResponse var1) throws Exception {
      if (XCord.getAntiBotManager().isAttackActive()) {
         PipelineUtils.CHANNEL_UTILITY.close(this.ch.getChannel());
      } else {
         this.disconnect("Unexpected custom LoginPayloadResponse");
      }
   }

   @Override
   public void disconnect(String var1) {
      if (this.canSendKickMessage()) {
         this.disconnect(TextComponent.fromLegacyText(var1));
      } else {
         this.ch.close();
      }
   }

   @Override
   public void disconnect(BaseComponent... var1) {
      if (this.canSendKickMessage()) {
         this.ch.delayedClose(new Kick(ComponentSerializer.toString(var1)));
      } else {
         this.ch.close();
      }
   }

   public void disconnect(String var1, int var2) {
      if (this.canSendKickMessage()) {
         if (!this.ch.isClosed()) {
            PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)var2);
         }

         this.ch.delayedClose(new Kick(ComponentSerializer.toString(TextComponent.fromLegacyText(var1))));
      } else {
         this.ch.close();
      }
   }

   public void disconnect(MessageTemplate var1, int var2) {
      if (this.canSendKickMessage()) {
         if (!this.ch.isClosed()) {
            PipelineUtils.setTimeoutHandler(this.ch.getHandle(), (long)var2);
         }

         this.ch.delayedClose(new Kick(var1.getJson()));
      } else {
         this.ch.close();
      }
   }

   @Override
   public void disconnect(BaseComponent var1) {
      this.disconnect(var1);
   }

   @Override
   public String getName() {
      return this.name != null ? this.name : (this.loginRequest == null ? null : this.loginRequest.getData());
   }

   @Override
   public int getVersion() {
      return this.handshake == null ? -1 : this.handshake.getProtocolVersion();
   }

   @Override
   public InetSocketAddress getAddress() {
      return (InetSocketAddress)this.getSocketAddress();
   }

   @Override
   public SocketAddress getSocketAddress() {
      return this.ch.getRemoteAddress();
   }

   @Override
   public Connection.Unsafe unsafe() {
      return this.unsafe;
   }

   @Override
   public void setOnlineMode(boolean var1) {
      Preconditions.checkState(this.thisState == InitialHandler.State.USERNAME, "Can only set online mode status whilst state is username");
      this.onlineMode = var1;
   }

   @Override
   public void setUniqueId(UUID var1) {
      this.uniqueId = var1;
   }

   @Override
   public String getUUID() {
      return UUIDUtils.undash(this.uniqueId.toString());
   }

   @Override
   public String toString() {
      return "[" + this.getSocketAddress() + (this.getName() != null ? "|" + this.getName() : "") + "] <-> InitialHandler";
   }

   @Override
   public boolean isConnected() {
      return !this.ch.isClosed();
   }

   public ChannelWrapper getChannel() {
      return this.ch;
   }

   public InitialHandler(BungeeCord var1, ListenerInfo var2) {
      this.bungee = var1;
      this.listener = var2;
   }

   @Override
   public ListenerInfo getListener() {
      return this.listener;
   }

   public Handshake getHandshake() {
      return this.handshake;
   }

   public LoginRequest getLoginRequest() {
      return this.loginRequest;
   }

   public Set<String> getRegisteredChannels() {
      return this.registeredChannels;
   }

   public PluginMessage getBrandMessage() {
      return this.brandMessage;
   }

   @Override
   public boolean isOnlineMode() {
      return this.onlineMode;
   }

   @Override
   public InetSocketAddress getVirtualHost() {
      return this.virtualHost;
   }

   @Override
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public UUID getOfflineId() {
      return this.offlineId;
   }

   public LoginResult getLoginProfile() {
      return this.loginProfile;
   }

   @Override
   public boolean isLegacy() {
      return this.legacy;
   }

   public String getExtraDataInHandshake() {
      return this.extraDataInHandshake;
   }

   private static enum State {
      HANDSHAKE,
      STATUS,
      PING,
      USERNAME,
      ENCRYPT,
      FINISHED;
   }
}
