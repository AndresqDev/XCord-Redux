package net.md_5.bungee;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.entitymap.EntityMap_Dummy;
import net.md_5.bungee.forge.ForgeServerHandler;
import net.md_5.bungee.forge.ForgeUtils;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EntityStatus;
import net.md_5.bungee.protocol.packet.GameState;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.Login;
import net.md_5.bungee.protocol.packet.LoginPayloadRequest;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.ViewDistance;
import net.md_5.bungee.util.AddressUtil;
import net.md_5.bungee.util.QuietException;
import xcord.Log4jFilter;
import xcord.SpoofedServerConnection;
import xcord.XCord;

public class ServerConnector extends PacketHandler {
   private final ProxyServer bungee;
   private ChannelWrapper ch;
   private final UserConnection user;
   private final BungeeServerInfo target;
   private ServerConnector.State thisState = ServerConnector.State.LOGIN_SUCCESS;
   private ForgeServerHandler handshakeHandler;
   private boolean obsolete;

   @Override
   public void exception(Throwable var1) throws Exception {
      if (!this.obsolete) {
         Object var2 = "Exception Connecting:" + Util.exception(var1);
         if (this.user.getServer() == null) {
            this.user.disconnect((String)var2);
         } else {
            this.user.sendMessage(ChatColor.RED + var2);
         }
      }
   }

   @Override
   public void connected(ChannelWrapper var1) throws Exception {
      this.ch = var1;
      this.handshakeHandler = new ForgeServerHandler(this.user, this.ch, this.target);
      Handshake var2 = this.user.getPendingConnection().getHandshake();
      Handshake var3 = new Handshake(var2.getProtocolVersion(), var2.getHost(), var2.getPort(), 2);
      if (BungeeCord.getInstance().config.isIpForward() && this.user.getSocketAddress() instanceof InetSocketAddress) {
         String var4 = var3.getHost() + "\u0000" + AddressUtil.sanitizeAddress(this.user.getAddress()) + "\u0000" + this.user.getUUID();
         LoginResult var5 = this.user.getPendingConnection().getLoginProfile();
         Property[] var6 = new Property[0];
         if (var5 != null && var5.getProperties() != null && var5.getProperties().length > 0) {
            var6 = var5.getProperties();
         }

         if ((!XCord.getConfig().getFixesFix17KickWhen18Joins() || this.user.getPendingConnection().getVersion() > 47)
            && this.user.getForgeClientHandler().isFmlTokenInHandshake()) {
            Property[] var7 = Arrays.copyOf(var6, var6.length + 2);
            var7[var7.length - 2] = new Property("forgeClient", "true", null);
            var7[var7.length - 1] = new Property("extraData", this.user.getExtraDataInHandshake().replaceAll("\u0000", "\u0001"), "");
            var6 = var7;
         }

         if (var6.length > 0) {
            var4 = var4 + "\u0000" + BungeeCord.getInstance().gson.toJson(var6);
         }

         Log4jFilter.a(var4);
         var3.setHost(var4);
      } else if (!this.user.getExtraDataInHandshake().isEmpty()) {
         var3.setHost(var3.getHost() + this.user.getExtraDataInHandshake());
      }

      var1.write(var3);
      var1.setProtocol(Protocol.LOGIN);
      var1.write(new LoginRequest(this.user.getName(), null, this.user.getUniqueId()));
   }

   @Override
   public void disconnected(ChannelWrapper var1) throws Exception {
      this.user.getPendingConnects().remove(this.target);
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      if (var1.packet == null) {
      }
   }

   @Override
   public void handle(LoginSuccess var1) throws Exception {
      Preconditions.checkState(this.thisState == ServerConnector.State.LOGIN_SUCCESS, "Not expecting LOGIN_SUCCESS");
      this.ch.setProtocol(Protocol.GAME);
      this.thisState = ServerConnector.State.LOGIN;
      if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer())
         && this.user.getForgeClientHandler().isHandshakeComplete()
         && this.user.getServer().isForgeServer()) {
         this.user.getForgeClientHandler().resetHandshake();
      }

      throw CancelSendSignal.INSTANCE;
   }

   @Override
   public void handle(SetCompression var1) throws Exception {
      this.ch.setCompressionThreshold(var1.getThreshold());
   }

   @Override
   public void handle(Login var1) throws Exception {
      Preconditions.checkState(this.thisState == ServerConnector.State.LOGIN, "Not expecting LOGIN");
      if (this.user.isJoinRequiresRewrite()) {
         this.user.setEntityRewrite(EntityMap.getEntityMap(this.user.getPendingConnection().getVersion(), true));
      }

      ServerConnection var2 = new ServerConnection(this.ch, this.target);
      ServerConnectedEvent var3 = new ServerConnectedEvent(this.user, var2);
      if (var2.isForgeServer() && this.user.isForgeUser()) {
         ((MinecraftDecoder)var2.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
         ((MinecraftDecoder)this.user.getCh().getHandle().pipeline().get("packet-decoder")).setSupportsForge(true);
      }

      this.bungee.getPluginManager().callEvent(var3);
      this.ch.write(BungeeCord.getInstance().registerChannels(this.user.getPendingConnection().getVersion()));
      Queue var4 = this.target.getPacketQueue();
      synchronized (var4) {
         while (!var4.isEmpty()) {
            this.ch.write(var4.poll());
         }
      }

      PluginMessage var5 = this.user.getPendingConnection().getBrandMessage();
      if (var5 != null) {
         this.ch.write(var5);
      }

      Set var6 = this.user.getPendingConnection().getRegisteredChannels();
      if (!var6.isEmpty()) {
         this.ch
            .write(
               new PluginMessage(
                  this.user.getPendingConnection().getVersion() >= 393 ? "minecraft:register" : "REGISTER",
                  Joiner.on("\u0000").join(var6).getBytes(StandardCharsets.UTF_8),
                  false
               )
            );
      }

      if (!this.user.isDisableEntityMetadataRewrite() && this.user.getSettings() != null) {
         this.ch.write(this.user.getSettings());
      }

      if (this.user.getForgeClientHandler().getClientModList() == null && !this.user.getForgeClientHandler().isHandshakeComplete()) {
         this.user.getForgeClientHandler().setHandshakeComplete();
      }

      if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer()) && var1.getDimension() instanceof Integer) {
         if (this.user.getServer() != null) {
            this.user.getServer().setObsolete(true);
         }

         this.user.getTabListHandler().onServerChange();
         Scoreboard var11 = this.user.getServerSentScoreboard();
         if (!this.user.isDisableEntityMetadataRewrite()) {
            for (Objective var20 : var11.getObjectives()) {
               this.user
                  .unsafe()
                  .sendPacket(
                     new ScoreboardObjective(
                        var20.getName(),
                        var20.getValue(),
                        var20.getType() == null ? null : ScoreboardObjective.HealthDisplay.fromString(var20.getType()),
                        (byte)1
                     )
                  );
            }

            for (Score var21 : var11.getScores()) {
               this.user.unsafe().sendPacket(new ScoreboardScore(var21.getItemName(), (byte)1, var21.getScoreName(), var21.getValue()));
            }

            for (Team var22 : var11.getTeams()) {
               this.user.unsafe().sendPacket(new net.md_5.bungee.protocol.packet.Team(var22.getName()));
            }
         }

         var11.clear();

         for (UUID var23 : this.user.getSentBossBars()) {
            this.user.unsafe().sendPacket(new BossBar(var23, 1));
         }

         this.user.getSentBossBars().clear();
         this.user.unsafe().sendPacket(new EntityStatus(this.user.getClientEntityId(), (byte)(var1.isReducedDebugInfo() ? 22 : 23)));
         if (this.user.getPendingConnection().getVersion() >= 573) {
            this.user.unsafe().sendPacket(new GameState((short)11, var1.isNormalRespawn() ? 0.0F : 1.0F));
         }

         this.user.setDimensionChange(true);
         if (!this.user.isDisableEntityMetadataRewrite() && var1.getDimension() == this.user.getDimension()) {
            this.user
               .unsafe()
               .sendPacket(
                  new Respawn(
                     var1.getDimension() >= 0 ? -1 : 0,
                     var1.getWorldName(),
                     var1.getSeed(),
                     var1.getDifficulty(),
                     var1.getGameMode(),
                     var1.getPreviousGameMode(),
                     var1.getLevelType(),
                     var1.isDebug(),
                     var1.isFlat(),
                     false,
                     var1.getDeathLocation()
                  )
               );
         }

         this.user.setServerEntityId(var1.getEntityId());
         if (this.user.isDisableEntityMetadataRewrite() || this.user.isPassedThroughSpoofedServer()) {
            this.user.setClientEntityId(var1.getEntityId());
            if (var1.getDimension() != this.user.getDimension()) {
               this.user
                  .unsafe()
                  .sendPacket(
                     new Respawn(
                        var1.getDimension() >= 0 ? -1 : 0,
                        var1.getWorldName(),
                        var1.getSeed(),
                        var1.getDifficulty(),
                        var1.getGameMode(),
                        var1.getPreviousGameMode(),
                        var1.getLevelType(),
                        var1.isDebug(),
                        var1.isFlat(),
                        false,
                        var1.getDeathLocation()
                     )
                  );
            }

            Login var18 = new Login(
               var1.getEntityId(),
               var1.isHardcore(),
               var1.getGameMode(),
               var1.getPreviousGameMode(),
               var1.getWorldNames(),
               var1.getDimensions(),
               var1.getDimension(),
               var1.getWorldName(),
               var1.getSeed(),
               var1.getDifficulty(),
               (byte)this.user.getPendingConnection().getListener().getTabListSize(),
               var1.getLevelType(),
               var1.getViewDistance(),
               var1.getSimulationDistance(),
               var1.isReducedDebugInfo(),
               var1.isNormalRespawn(),
               var1.isDebug(),
               var1.isFlat(),
               var1.getDeathLocation()
            );
            this.user.unsafe().sendPacket(var18);
            if (var1.getDimension() == this.user.getDimension()) {
               this.user
                  .unsafe()
                  .sendPacket(
                     new Respawn(
                        var1.getDimension() >= 0 ? -1 : 0,
                        var1.getWorldName(),
                        var1.getSeed(),
                        var1.getDifficulty(),
                        var1.getGameMode(),
                        var1.getPreviousGameMode(),
                        var1.getLevelType(),
                        var1.isDebug(),
                        var1.isFlat(),
                        false,
                        var1.getDeathLocation()
                     )
                  );
            }
         }

         this.user
            .unsafe()
            .sendPacket(
               new Respawn(
                  var1.getDimension(),
                  var1.getWorldName(),
                  var1.getSeed(),
                  var1.getDifficulty(),
                  var1.getGameMode(),
                  var1.getPreviousGameMode(),
                  var1.getLevelType(),
                  var1.isDebug(),
                  var1.isFlat(),
                  false,
                  var1.getDeathLocation()
               )
            );
         if (this.user.getPendingConnection().getVersion() >= 477) {
            this.user.unsafe().sendPacket(new ViewDistance(var1.getViewDistance()));
         }

         this.user.setDimension(var1.getDimension());
         if (this.user.getServer() != null) {
            this.user.getServer().disconnect("Quitting");
         }
      } else {
         this.user.setClientEntityId(var1.getEntityId());
         this.user.setServerEntityId(var1.getEntityId());
         Login var7 = new Login(
            var1.getEntityId(),
            var1.isHardcore(),
            var1.getGameMode(),
            var1.getPreviousGameMode(),
            var1.getWorldNames(),
            var1.getDimensions(),
            var1.getDimension(),
            var1.getWorldName(),
            var1.getSeed(),
            var1.getDifficulty(),
            (byte)this.user.getPendingConnection().getListener().getTabListSize(),
            var1.getLevelType(),
            var1.getViewDistance(),
            var1.getSimulationDistance(),
            var1.isReducedDebugInfo(),
            var1.isNormalRespawn(),
            var1.isDebug(),
            var1.isFlat(),
            var1.getDeathLocation()
         );
         this.user.unsafe().sendPacket(var7);
         if (this.user.getServer() == null && !this.user.isPassedThroughSpoofedServer()) {
            String var13 = this.bungee.getName() + " (" + this.bungee.getVersion() + ")";
            if (ProtocolConstants.isBeforeOrEq(this.user.getPendingConnection().getVersion(), 5)) {
               this.user.unsafe().sendPacket(new PluginMessage("MC|Brand", var13.getBytes(StandardCharsets.UTF_8), this.handshakeHandler.isServerForge()));
            } else {
               ByteBuf var19 = ByteBufAllocator.DEFAULT.heapBuffer();
               DefinedPacket.writeString(var13, var19);
               this.user
                  .unsafe()
                  .sendPacket(
                     new PluginMessage(
                        this.user.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand", var19, this.handshakeHandler.isServerForge()
                     )
                  );
               var19.release();
            }
         } else {
            if (this.user.getServer() != null) {
               this.user.getServer().setObsolete(true);
               this.user.getTabListHandler().onServerChange();
               this.user.getServerSentScoreboard().clear();

               for (UUID var9 : this.user.getSentBossBars()) {
                  this.user.unsafe().sendPacket(new BossBar(var9, 1));
               }

               this.user.getSentBossBars().clear();
            }

            this.user
               .unsafe()
               .sendPacket(
                  new Respawn(
                     var1.getDimension(),
                     var1.getWorldName(),
                     var1.getSeed(),
                     var1.getDifficulty(),
                     var1.getGameMode(),
                     var1.getPreviousGameMode(),
                     var1.getLevelType(),
                     var1.isDebug(),
                     var1.isFlat(),
                     false,
                     var1.getDeathLocation()
                  )
               );
            if (this.user.getServer() != null) {
               this.user.getServer().disconnect("Quitting");
            }
         }

         this.user.setDimension(var1.getDimension());
      }

      if (this.user.isJoinRequiresRewrite()) {
         this.user.setJoinRequiresRewrite(false);
         this.user.setEntityRewrite(EntityMap_Dummy.INSTANCE);
      }

      if (!this.user.isActive()) {
         var2.disconnect("Quitting");
         if (!XCord.isLogFilterEnabled()) {
            this.bungee.getLogger().warning("No client connected for pending server!");
         }
      } else {
         this.target.addPlayer(this.user);
         this.user.getPendingConnects().remove(this.target);
         this.user.setServerJoinQueue(null);
         this.user.setDimensionChange(false);
         BungeeServerInfo var12 = this.user.getServer() == null ? null : this.user.getServer().getInfo();
         this.user.setServer(var2);
         ((HandlerBoss)this.ch.getHandle().pipeline().get(HandlerBoss.class)).setHandler(new DownstreamBridge(this.bungee, this.user, var2));
         this.bungee.getPluginManager().callEvent(new ServerSwitchEvent(this.user, var12));
         this.thisState = ServerConnector.State.FINISHED;
         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(EncryptionRequest var1) throws Exception {
      throw new QuietException("Server is online mode!");
   }

   @Override
   public void handle(Kick var1) throws Exception {
      ServerInfo var2 = this.user.updateAndGetNextServer(this.target);
      ServerKickEvent var3 = new ServerKickEvent(
         this.user, this.target, ComponentSerializer.parse(var1.getMessage()), var2, ServerKickEvent.State.CONNECTING, ServerKickEvent.Cause.SERVER
      );
      if (var3.getKickReason().toLowerCase(Locale.ROOT).contains("outdated") && var2 != null) {
         var3.setCancelled(true);
      }

      if (!this.user.isPassedThroughSpoofedServer()) {
         this.bungee.getPluginManager().callEvent(var3);
      }

      if (var3.isCancelled() && var3.getCancelServer() != null) {
         this.obsolete = true;
         this.user.connect(var3.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
         throw CancelSendSignal.INSTANCE;
      } else {
         String var4 = this.bungee.getTranslation("connect_kick", this.target.getName(), var3.getKickReason());
         if (this.user.isDimensionChange()) {
            this.user.disconnect(var4);
         } else if (!this.user.isPassedThroughSpoofedServer() && this.user.getServer() != null && !(this.user.getServer() instanceof SpoofedServerConnection)) {
            this.user.sendMessage(var4);
         } else {
            this.user.disconnect(var4);
         }

         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(PluginMessage var1) throws Exception {
      if (BungeeCord.getInstance().config.isForgeSupport()) {
         if (var1.getTag().equals("REGISTER")) {
            Set var2 = ForgeUtils.readRegisteredChannels(var1);
            boolean var3 = false;
            Iterator var4 = var2.iterator();

            while (true) {
               if (var4.hasNext()) {
                  String var5 = (String)var4.next();
                  if (!var5.equals("FML|HS")) {
                     continue;
                  }

                  if ((this.user.getServer() != null || this.user.isPassedThroughSpoofedServer()) && this.user.getForgeClientHandler().isHandshakeComplete()) {
                     this.user.getForgeClientHandler().resetHandshake();
                  }

                  var3 = true;
               }

               if (var3 && !this.handshakeHandler.isServerForge()) {
                  this.handshakeHandler.setServerAsForgeServer();
                  this.user.setForgeServerHandler(this.handshakeHandler);
               }
               break;
            }
         }

         if (var1.getTag().equals("FML|HS") || var1.getTag().equals("FORGE")) {
            this.handshakeHandler.handle(var1);
            if (this.user.getForgeClientHandler().checkUserOutdated()) {
               this.ch.close();
               this.user.getPendingConnects().remove(this.target);
            }

            throw CancelSendSignal.INSTANCE;
         }
      }

      this.user.unsafe().sendPacket(var1);
   }

   @Override
   public String toString() {
      return "[" + this.user.getName() + "|" + this.user.getAddress() + "] <-> ServerConnector [" + this.target.getName() + "]";
   }

   @Override
   public void handle(LoginPayloadRequest var1) {
      this.ch.write(new LoginPayloadResponse(var1.getId(), null));
   }

   public ServerConnector(ProxyServer var1, UserConnection var2, BungeeServerInfo var3) {
      this.bungee = var1;
      this.user = var2;
      this.target = var3;
   }

   public ForgeServerHandler getHandshakeHandler() {
      return this.handshakeHandler;
   }

   private static enum State {
      LOGIN_SUCCESS,
      ENCRYPT_RESPONSE,
      LOGIN,
      FINISHED;
   }
}
