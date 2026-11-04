package net.md_5.bungee.connection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.unix.DomainSocketAddress;
import java.io.DataInput;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.score.Objective;
import net.md_5.bungee.api.score.Position;
import net.md_5.bungee.api.score.Score;
import net.md_5.bungee.api.score.Scoreboard;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.BossBar;
import net.md_5.bungee.protocol.packet.Commands;
import net.md_5.bungee.protocol.packet.EntityEffect;
import net.md_5.bungee.protocol.packet.EntityRemoveEffect;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.Respawn;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ServerData;
import net.md_5.bungee.protocol.packet.SetCompression;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import net.md_5.bungee.protocol.packet.Team;
import net.md_5.bungee.tab.TabList;
import xcord.XCord;

public class DownstreamBridge extends PacketHandler {
   private static final Command DUMMY_COMMAND = var0 -> 0;
   private final ProxyServer bungee;
   private final UserConnection con;
   private final ServerConnection server;

   @Override
   public void exception(Throwable var1) throws Exception {
      if (!this.server.isObsolete()) {
         if (XCord.getConfig().getDebugOutputDownstreamErrors()) {
            var1.printStackTrace();
         }

         ServerInfo var2 = this.con.updateAndGetNextServer(this.server.getInfo());
         ServerKickEvent var3 = this.bungee
            .getPluginManager()
            .callEvent(
               new ServerKickEvent(
                  this.con,
                  this.server.getInfo(),
                  TextComponent.fromLegacyText(this.bungee.getTranslation("server_went_down")),
                  var2,
                  ServerKickEvent.State.CONNECTED,
                  ServerKickEvent.Cause.EXCEPTION
               )
            );
         if (var3.isCancelled() && var3.getCancelServer() != null) {
            this.server.setObsolete(true);
            this.con.connectNow(var3.getCancelServer(), ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
         } else {
            this.con.disconnect0(var3.getKickReasonComponent());
         }
      }
   }

   @Override
   public void disconnected(ChannelWrapper var1) throws Exception {
      this.server.getInfo().removePlayer(this.con);
      if (this.bungee.getReconnectHandler() != null) {
         this.bungee.getReconnectHandler().setServer(this.con);
      }

      if (!this.server.isObsolete()) {
         ServerInfo var2 = this.con.updateAndGetNextServer(this.server.getInfo());
         ServerKickEvent var3 = this.bungee
            .getPluginManager()
            .callEvent(
               new ServerKickEvent(
                  this.con,
                  this.server.getInfo(),
                  TextComponent.fromLegacyText(this.bungee.getTranslation("lost_connection")),
                  var2,
                  ServerKickEvent.State.CONNECTED,
                  ServerKickEvent.Cause.LOST_CONNECTION
               )
            );
         if (var3.isCancelled() && var3.getCancelServer() != null) {
            this.server.setObsolete(true);
            this.con.connectNow(var3.getCancelServer());
         } else {
            this.con.disconnect0(var3.getKickReasonComponent());
         }
      }

      ServerDisconnectEvent var4 = new ServerDisconnectEvent(this.con, this.server.getInfo());
      this.bungee.getPluginManager().callEvent(var4);
   }

   @Override
   public boolean shouldHandle(PacketWrapper var1) throws Exception {
      return !this.server.isObsolete();
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      EntityMap var2 = this.con.getEntityRewrite();
      if (var2 != null) {
         var2.rewriteClientbound(var1.buf, this.con.getServerEntityId(), this.con.getClientEntityId(), this.con.getPendingConnection().getVersion());
      }

      this.con.appendBufferedPacket(var1);
   }

   @Override
   public void readCompleted() throws Exception {
      this.con.flush();
   }

   @Override
   public void handle(KeepAlive var1) throws Exception {
      int var2 = this.bungee.getConfig().getTimeout();
      if (var2 <= 0 || this.server.getKeepAlives().size() < var2 / 50) {
         this.server.getKeepAlives().add(new ServerConnection.KeepAliveData(var1.getRandomId(), System.currentTimeMillis()));
      }
   }

   @Override
   public void handle(PlayerListItem var1) throws Exception {
      boolean var2 = this.bungee.getConfig().isDisableTabListRewrite();
      this.con.getTabListHandler().onUpdate(var2 ? var1 : TabList.rewrite(var1));
      if (!var2) {
         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(ScoreboardObjective var1) throws Exception {
      if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
         Scoreboard var2 = this.con.getServerSentScoreboard();
         switch (var1.getAction()) {
            case 0:
               var2.addObjective(new Objective(var1.getName(), var1.getValue(), var1.getType() != null ? var1.getType().toString() : null));
               break;
            case 1:
               var2.removeObjective(var1.getName());
               break;
            case 2:
               Objective var3 = var2.getObjective(var1.getName());
               if (var3 != null) {
                  var3.setValue(var1.getValue());
                  var3.setType(var1.getType() != null ? var1.getType().toString() : null);
               }
               break;
            default:
               throw new IllegalArgumentException("Unknown objective action: " + var1.getAction());
         }
      }
   }

   @Override
   public void handle(ScoreboardScore var1) throws Exception {
      if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
         Scoreboard var2 = this.con.getServerSentScoreboard();
         switch (var1.getAction()) {
            case 0:
               Score var3 = new Score(var1.getItemName(), var1.getScoreName(), var1.getValue());
               var2.removeScore(var1.getItemName());
               var2.addScore(var3);
               break;
            case 1:
               var2.removeScore(var1.getItemName());
               break;
            default:
               throw new IllegalArgumentException("Unknown scoreboard action: " + var1.getAction());
         }
      }
   }

   @Override
   public void handle(ScoreboardDisplay var1) throws Exception {
      if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
         Scoreboard var2 = this.con.getServerSentScoreboard();
         var2.setName(var1.getName());
         var2.setPosition(Position.values()[var1.getPosition()]);
      }
   }

   @Override
   public void handle(Team var1) throws Exception {
      if (!XCord.getConfig().getPerformanceOptimizationsIgnoreScoreboardsAndDisableScoreboardApi()) {
         Scoreboard var2 = this.con.getServerSentScoreboard();
         if (var1.getMode() == 1) {
            var2.removeTeam(var1.getName());
         } else {
            net.md_5.bungee.api.score.Team var3;
            if (var1.getMode() == 0) {
               var3 = new net.md_5.bungee.api.score.Team(var1.getName());
               var2.addTeam(var3);
            } else {
               var3 = var2.getTeam(var1.getName());
            }

            if (var3 != null) {
               if (var1.getMode() == 0 || var1.getMode() == 2) {
                  var3.setDisplayName(var1.getDisplayName());
                  var3.setPrefix(var1.getPrefix());
                  var3.setSuffix(var1.getSuffix());
                  var3.setFriendlyFire(var1.getFriendlyFire());
                  var3.setNameTagVisibility(var1.getNameTagVisibility());
                  var3.setCollisionRule(var1.getCollisionRule());
                  var3.setColor(var1.getColor());
               }

               if (var1.getPlayers() != null) {
                  for (String var7 : var1.getPlayers()) {
                     if (var1.getMode() == 0 || var1.getMode() == 3) {
                        var3.addPlayer(var7);
                     } else if (var1.getMode() == 4) {
                        var3.removePlayer(var7);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void handle(PluginMessage var1) throws Exception {
      PluginMessageEvent var2 = new PluginMessageEvent(this.server, this.con, var1.getTag(), (byte[])var1.getData().clone());
      if (this.bungee.getPluginManager().callEvent(var2).isCancelled()) {
         throw CancelSendSignal.INSTANCE;
      } else if (var1.getTag().equals(this.con.getPendingConnection().getVersion() >= 393 ? "minecraft:brand" : "MC|Brand")) {
         if (ProtocolConstants.isAfterOrEq(this.con.getPendingConnection().getVersion(), 47)) {
            try {
               ByteBuf var14 = Unpooled.wrappedBuffer(var1.getData());
               String var16 = DefinedPacket.readString(var14);
               var14.release();
               var14 = ByteBufAllocator.DEFAULT.heapBuffer();
               DefinedPacket.writeString(XCord.getConfig().getGeneralXCord(), var14);
               var1.setData(var14);
               var14.release();
            } catch (Exception var13) {
               return;
            }
         } else {
            var1.setData(XCord.getConfig().getGeneralXCord().getBytes(StandardCharsets.UTF_8));
         }

         this.con.unsafe().sendPacket(var1);
         throw CancelSendSignal.INSTANCE;
      } else if (var1.getTag().equals("BungeeCord")) {
         DataInput var3 = var1.getStream();
         ByteArrayDataOutput var4 = ByteStreams.newDataOutput();
         String var5 = var3.readUTF();
         if (var5.equals("ForwardToPlayer")) {
            ProxiedPlayer var6 = this.bungee.getPlayer(var3.readUTF());
            if (var6 != null) {
               String var7 = var3.readUTF();
               short var8 = var3.readShort();
               byte[] var9 = new byte[var8];
               var3.readFully(var9);
               var4.writeUTF(var7);
               var4.writeShort(var9.length);
               var4.write(var9);
               byte[] var10 = var4.toByteArray();
               var6.getServer().sendData("BungeeCord", var10);
            }

            var4 = null;
         }

         if (var5.equals("Forward")) {
            String var17 = var3.readUTF();
            String var29 = var3.readUTF();
            short var37 = var3.readShort();
            byte[] var42 = new byte[var37];
            var3.readFully(var42);
            var4.writeUTF(var29);
            var4.writeShort(var42.length);
            var4.write(var42);
            byte[] var45 = var4.toByteArray();
            var4 = null;
            if (var17.equals("ALL")) {
               for (ServerInfo var12 : this.bungee.getServers().values()) {
                  if (var12 != this.server.getInfo()) {
                     var12.sendData("BungeeCord", var45);
                  }
               }
            } else if (var17.equals("ONLINE")) {
               for (ServerInfo var48 : this.bungee.getServers().values()) {
                  if (var48 != this.server.getInfo()) {
                     var48.sendData("BungeeCord", var45, false);
                  }
               }
            } else {
               ServerInfo var47 = this.bungee.getServerInfo(var17);
               if (var47 != null) {
                  var47.sendData("BungeeCord", var45);
               }
            }
         }

         if (var5.equals("Connect")) {
            ServerInfo var18 = this.bungee.getServerInfo(var3.readUTF());
            if (var18 != null) {
               this.con.connect(var18, ServerConnectEvent.Reason.PLUGIN_MESSAGE);
            }
         }

         if (var5.equals("ConnectOther")) {
            ProxiedPlayer var19 = this.bungee.getPlayer(var3.readUTF());
            if (var19 != null) {
               ServerInfo var30 = this.bungee.getServerInfo(var3.readUTF());
               if (var30 != null) {
                  var19.connect(var30);
               }
            }
         }

         if (var5.equals("IP")) {
            var4.writeUTF("IP");
            if (this.con.getSocketAddress() instanceof InetSocketAddress) {
               var4.writeUTF(this.con.getAddress().getHostString());
               var4.writeInt(this.con.getAddress().getPort());
            } else {
               var4.writeUTF("unix://" + ((DomainSocketAddress)this.con.getSocketAddress()).path());
               var4.writeInt(0);
            }
         }

         if (var5.equals("IPOther")) {
            ProxiedPlayer var20 = this.bungee.getPlayer(var3.readUTF());
            if (var20 != null) {
               var4.writeUTF("IPOther");
               var4.writeUTF(var20.getName());
               if (var20.getSocketAddress() instanceof InetSocketAddress) {
                  InetSocketAddress var31 = (InetSocketAddress)var20.getSocketAddress();
                  var4.writeUTF(var31.getHostString());
                  var4.writeInt(var31.getPort());
               } else {
                  var4.writeUTF("unix://" + ((DomainSocketAddress)var20.getSocketAddress()).path());
                  var4.writeInt(0);
               }
            }
         }

         if (var5.equals("PlayerCount")) {
            String var21 = var3.readUTF();
            var4.writeUTF("PlayerCount");
            if (var21.equals("ALL")) {
               var4.writeUTF("ALL");
               var4.writeInt(this.bungee.getOnlineCount());
            } else {
               ServerInfo var32 = this.bungee.getServerInfo(var21);
               if (var32 != null) {
                  var4.writeUTF(var32.getName());
                  var4.writeInt(var32.getPlayers().size());
               }
            }
         }

         if (var5.equals("PlayerList")) {
            String var22 = var3.readUTF();
            var4.writeUTF("PlayerList");
            if (var22.equals("ALL")) {
               var4.writeUTF("ALL");
               var4.writeUTF(Util.csv(this.bungee.getPlayers()));
            } else {
               ServerInfo var33 = this.bungee.getServerInfo(var22);
               if (var33 != null) {
                  var4.writeUTF(var33.getName());
                  var4.writeUTF(Util.csv(var33.getPlayers()));
               }
            }
         }

         if (var5.equals("GetServers")) {
            var4.writeUTF("GetServers");
            var4.writeUTF(Util.csv(this.bungee.getServers().keySet()));
         }

         if (var5.equals("Message")) {
            String var23 = var3.readUTF();
            String var34 = var3.readUTF();
            if (var23.equals("ALL")) {
               for (ProxiedPlayer var43 : this.bungee.getPlayers()) {
                  var43.sendMessage(var34);
               }
            } else {
               ProxiedPlayer var39 = this.bungee.getPlayer(var23);
               if (var39 != null) {
                  var39.sendMessage(var34);
               }
            }
         }

         if (var5.equals("MessageRaw")) {
            String var24 = var3.readUTF();
            BaseComponent[] var35 = ComponentSerializer.parse(var3.readUTF());
            if (var24.equals("ALL")) {
               for (ProxiedPlayer var44 : this.bungee.getPlayers()) {
                  var44.sendMessage(var35);
               }
            } else {
               ProxiedPlayer var41 = this.bungee.getPlayer(var24);
               if (var41 != null) {
                  var41.sendMessage(var35);
               }
            }
         }

         if (var5.equals("GetServer")) {
            var4.writeUTF("GetServer");
            var4.writeUTF(this.server.getInfo().getName());
         }

         if (var5.equals("UUID")) {
            var4.writeUTF("UUID");
            var4.writeUTF(this.con.getUUID());
         }

         if (var5.equals("UUIDOther")) {
            ProxiedPlayer var25 = this.bungee.getPlayer(var3.readUTF());
            if (var25 != null) {
               var4.writeUTF("UUIDOther");
               var4.writeUTF(var25.getName());
               var4.writeUTF(var25.getUUID());
            }
         }

         if (var5.equals("ServerIP")) {
            ServerInfo var26 = this.bungee.getServerInfo(var3.readUTF());
            if (var26 != null && !var26.getAddress().isUnresolved()) {
               var4.writeUTF("ServerIP");
               var4.writeUTF(var26.getName());
               var4.writeUTF(var26.getAddress().getAddress().getHostAddress());
               var4.writeShort(var26.getAddress().getPort());
            }
         }

         if (var5.equals("KickPlayer")) {
            ProxiedPlayer var27 = this.bungee.getPlayer(var3.readUTF());
            if (var27 != null) {
               String var36 = var3.readUTF();
               var27.disconnect(new TextComponent(var36));
            }
         }

         if (var4 != null) {
            byte[] var28 = var4.toByteArray();
            if (var28.length != 0) {
               this.server.sendData("BungeeCord", var28);
            }
         }

         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(Kick var1) throws Exception {
      ServerInfo var2 = this.con.updateAndGetNextServer(this.server.getInfo());
      if (Objects.equals(this.server.getInfo(), var2)) {
         var2 = null;
      }

      ServerKickEvent var3 = this.bungee
         .getPluginManager()
         .callEvent(
            new ServerKickEvent(
               this.con,
               this.server.getInfo(),
               ComponentSerializer.parse(var1.getMessage()),
               var2,
               ServerKickEvent.State.CONNECTED,
               ServerKickEvent.Cause.SERVER
            )
         );
      if (var3.isCancelled() && var3.getCancelServer() != null) {
         this.con.connectNow(var3.getCancelServer(), ServerConnectEvent.Reason.KICK_REDIRECT);
      } else {
         this.con.disconnect0(var3.getKickReasonComponent());
      }

      this.server.setObsolete(true);
      throw CancelSendSignal.INSTANCE;
   }

   @Override
   public void handle(SetCompression var1) throws Exception {
      this.server.getCh().setCompressionThreshold(var1.getThreshold());
   }

   @Override
   public void handle(TabCompleteResponse var1) throws Exception {
      List var2 = var1.getCommands();
      if (var2 == null) {
         var2 = Lists.transform(var1.getSuggestions().getList(), new Function<Suggestion, String>() {
            public String apply(Suggestion var1) {
               return var1.getText();
            }
         });
      }

      TabCompleteResponseEvent var3 = new TabCompleteResponseEvent(this.server, this.con, new ArrayList<>(var2));
      if (!this.bungee.getPluginManager().callEvent(var3).isCancelled()) {
         if (!var2.equals(var3.getSuggestions())) {
            if (var1.getCommands() != null) {
               var1.setCommands(var3.getSuggestions());
            } else {
               final StringRange var4 = var1.getSuggestions().getRange();
               var1.setSuggestions(new Suggestions(var4, Lists.transform(var3.getSuggestions(), new Function<String, Suggestion>() {
                  public Suggestion apply(String var1) {
                     return new Suggestion(var4, var1);
                  }
               })));
            }
         }

         this.con.unsafe().sendPacket(var1);
      }

      throw CancelSendSignal.INSTANCE;
   }

   @Override
   public void handle(BossBar var1) {
      switch (var1.getAction()) {
         case 0:
            this.con.getSentBossBars().add(var1.getUuid());
            break;
         case 1:
            this.con.getSentBossBars().remove(var1.getUuid());
      }
   }

   @Override
   public void handle(EntityEffect var1) throws Exception {
      if (!this.con.isDisableEntityMetadataRewrite()) {
         if (this.con.getForgeClientHandler().isForgeUser() && !this.con.getForgeClientHandler().isHandshakeComplete()) {
            throw CancelSendSignal.INSTANCE;
         } else {
            this.con.getPotions().put(this.rewriteEntityId(var1.getEntityId()), var1.getEffectId());
         }
      }
   }

   @Override
   public void handle(EntityRemoveEffect var1) throws Exception {
      if (!this.con.isDisableEntityMetadataRewrite()) {
         this.con.getPotions().remove(this.rewriteEntityId(var1.getEntityId()), var1.getEffectId());
      }
   }

   private int rewriteEntityId(int var1) {
      return var1 == this.con.getServerEntityId() ? this.con.getClientEntityId() : var1;
   }

   @Override
   public void handle(Respawn var1) {
      this.con.setDimension(var1.getDimension());
   }

   @Override
   public void handle(Commands var1) throws Exception {
      boolean var2 = false;
      HashMap var3 = new HashMap();

      for (Entry var5 : this.bungee.getPluginManager().getCommands()) {
         if (!this.bungee.getDisabledCommands().contains(var5.getKey())
            && var1.getRoot().getChild((String)var5.getKey()) == null
            && ((net.md_5.bungee.api.plugin.Command)var5.getValue()).hasPermission(this.con)) {
            var3.put((String)var5.getKey(), (net.md_5.bungee.api.plugin.Command)var5.getValue());
         }
      }

      ProxyDefineCommandsEvent var8 = new ProxyDefineCommandsEvent(this.server, this.con, var3);
      this.bungee.getPluginManager().callEvent(var8);

      for (Entry var6 : var8.getCommands().entrySet()) {
         CommandNode var7 = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)var6.getKey()).executes(DUMMY_COMMAND))
            .then(
               RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString())
                  .suggests(Commands.SuggestionRegistry.ASK_SERVER)
                  .executes(DUMMY_COMMAND)
            )
            .build();
         var1.getRoot().addChild(var7);
         var2 = true;
      }

      if (var2) {
         this.con.unsafe().sendPacket(var1);
         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(ServerData var1) throws Exception {
      var1.setMotd(null);
      var1.setIcon(null);
      this.con.unsafe().sendPacket(var1);
      throw CancelSendSignal.INSTANCE;
   }

   @Override
   public String toString() {
      return "[" + this.con.getAddress() + "|" + this.con.getName() + "] <-> DownstreamBridge <-> [" + this.server.getInfo().getName() + "]";
   }

   public DownstreamBridge(ProxyServer var1, UserConnection var2, ServerConnection var3) {
      this.bungee = var1;
      this.con = var2;
      this.server = var3;
   }
}
