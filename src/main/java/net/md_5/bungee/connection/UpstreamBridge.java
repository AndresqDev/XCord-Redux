package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.entitymap.EntityMap;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.Chat;
import net.md_5.bungee.protocol.packet.ClientChat;
import net.md_5.bungee.protocol.packet.ClientCommand;
import net.md_5.bungee.protocol.packet.ClientSettings;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.KeepAlive;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.TabCompleteRequest;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import xcord.ConnectionStats;
import xcord.NameValidator;
import xcord.SpoofedServerConnection;
import xcord.XCord;

public class UpstreamBridge extends PacketHandler {
   private final ProxyServer bungee;
   private final UserConnection con;
   private long lastTabCompletion = -1L;
   private boolean handleDisconnects = true;
   protected boolean ignoreIncomming = false;

   public UpstreamBridge(ProxyServer var1, UserConnection var2) {
      this.bungee = var1;
      this.con = var2;
   }

   public boolean isHandleDisconnects() {
      return this.handleDisconnects;
   }

   public void setHandleDisconnects(boolean var1) {
      this.handleDisconnects = var1;
   }

   @Override
   public void exception(Throwable var1) throws Exception {
      this.con.disconnect(Util.exception(var1));
   }

   @Override
   public void disconnected(ChannelWrapper var1) throws Exception {
      PlayerDisconnectEvent var2 = new PlayerDisconnectEvent(this.con);
      this.bungee.getPluginManager().callEvent(var2);
      if (this.handleDisconnects) {
         this.con.getTabListHandler().onDisconnect();
         BungeeCord.getInstance().removeConnection(this.con);
         if (this.con.getServer() != null) {
            PlayerListItem var3 = new PlayerListItem();
            var3.setAction(PlayerListItem.Action.REMOVE_PLAYER);
            PlayerListItem.Item var4 = new PlayerListItem.Item();
            var4.setUuid(this.con.getUniqueId());
            var3.setItems(new PlayerListItem.Item[]{var4});

            for (ProxiedPlayer var6 : this.con.getServer().getInfo().getPlayers()) {
               if (ProtocolConstants.isAfterOrEq(var6.getPendingConnection().getVersion(), 47)) {
                  var6.unsafe().sendPacket(var3);
               }
            }

            this.con.getServer().disconnect("Quitting");
         }
      }
   }

   @Override
   public void writabilityChanged(ChannelWrapper var1) throws Exception {
      if (this.con.getServer() != null) {
         Channel var2 = this.con.getServer().getCh().getHandle();
         if (var1.getHandle().isWritable()) {
            var2.config().setAutoRead(true);
         } else {
            var2.config().setAutoRead(false);
         }
      }
   }

   public void killConnection() {
      this.ignoreIncomming = true;
      this.con.forceClose();
   }

   @Override
   public void handle(Handshake var1) throws Exception {
      throw CancelSendSignal.INSTANCE;
   }

   @Override
   public boolean shouldHandle(PacketWrapper var1) throws Exception {
      if (this.ignoreIncomming) {
         return false;
      } else {
         if (!this.con.isPassedBotChecks()) {
            this.con.incrementSentPackets();
         }

         return this.con.getServer() != null || var1.packet instanceof PluginMessage;
      }
   }

   @Override
   public void handle(PacketWrapper var1) throws Exception {
      if (this.con.getServer() != null) {
         EntityMap var2 = this.con.getEntityRewrite();
         if (var2 != null) {
            var2.rewriteServerbound(var1.buf, this.con.getClientEntityId(), this.con.getServerEntityId(), this.con.getPendingConnection().getVersion());
         }

         this.con.getServer().getCh().write(var1);
      }
   }

   @Override
   public void handle(KeepAlive var1) throws Exception {
      ServerConnection.KeepAliveData var2 = this.con.getServer().getKeepAlives().peek();
      if (var2 != null && var1.getRandomId() == var2.getId()) {
         Preconditions.checkState(var2 == this.con.getServer().getKeepAlives().poll(), "keepalive queue mismatch");
         int var3 = (int)(System.currentTimeMillis() - var2.getTime());
         this.con.getTabListHandler().onPingChange(var3);
         this.con.setPing(var3);
      } else {
         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(ClientChat var1) throws Exception {
      this.handleChat(var1.getMessage());
   }

   @Override
   public void handle(ClientCommand var1) throws Exception {
      this.handleChat("/" + var1.getCommand());
   }

   @Override
   public void handle(Chat var1) throws Exception {
      String var2 = this.handleChat(var1.getMessage());
      if (var2 != null) {
         var1.setMessage(var2);
         this.con.getServer().unsafe().sendPacket(var1);
      }

      throw CancelSendSignal.INSTANCE;
   }

   private String handleChat(String var1) {
      if (this.handleDisconnects && this.con.getServer() != null && !(this.con.getServer() instanceof SpoofedServerConnection)) {
         if ((System.currentTimeMillis() - this.con.getInitializeTime() < (long)XCord.getConfig().getAntiBotChatForceDelay() || this.con.getSettings() == null)
            && XCord.getConfig().getAntiBotChatCheck()) {
            if (!XCord.getConfig().getAntiBotDnsCheckIsStrict1() || var1.length() >= 100 || !var1.contains("PickaxeChat")) {
               throw CancelSendSignal.INSTANCE;
            }

            BungeeCord.getInstance().getLogger().info("PickaxeChat client joined from " + this.con.getName());
            this.con.setWhitelistedFromAntibot(true);
            this.con.setPassedBotChecks(true);
         }

         int var2 = 0;

         for (int var3 = var1.length(); var2 < var3; var2++) {
            char var4 = var1.charAt(var2);
            if (!NameValidator.isAllowedChar(var4)) {
               this.con.disconnect(this.bungee.getTranslation("illegal_chat_characters", Util.unicode(var4)));
               throw CancelSendSignal.INSTANCE;
            }
         }

         ChatEvent var6 = new ChatEvent(this.con, this.con.getServer(), var1);
         if (!this.bungee.getPluginManager().callEvent(var6).isCancelled()) {
            var1 = var6.getMessage();
            if (!var6.isCommand() || !this.bungee.getPluginManager().dispatchCommand(this.con, var1.substring(1))) {
               return var1;
            }
         }

         throw CancelSendSignal.INSTANCE;
      } else {
         throw CancelSendSignal.INSTANCE;
      }
   }

   @Override
   public void handle(TabCompleteRequest var1) throws Exception {
      if (this.bungee.getConfig().getTabThrottle() > 0
         && this.con.getPendingConnection().getVersion() >= 393
         && !this.bungee.getConfig().isDisableModernTabLimiter()) {
         long var2 = System.currentTimeMillis();
         if (this.lastTabCompletion > 0L && var2 - this.lastTabCompletion <= (long)this.bungee.getConfig().getTabThrottle()) {
            throw CancelSendSignal.INSTANCE;
         }

         this.lastTabCompletion = var2;
      }

      ArrayList var12 = new ArrayList();
      boolean var3 = false;
      if (var1.getCursor().startsWith("/")) {
         var3 = this.bungee.getPluginManager().dispatchCommand(this.con, var1.getCursor().substring(1), var12);
      }

      TabCompleteEvent var4 = new TabCompleteEvent(this.con, this.con.getServer(), var1.getCursor(), var12);
      this.bungee.getPluginManager().callEvent(var4);
      if (var4.isCancelled()) {
         throw CancelSendSignal.INSTANCE;
      } else {
         List var5 = var4.getSuggestions();
         if (var5.isEmpty()) {
            if (var3) {
               throw CancelSendSignal.INSTANCE;
            }
         } else {
            if (this.con.getPendingConnection().getVersion() < 393) {
               this.con.unsafe().sendPacket(new TabCompleteResponse(var5));
            } else {
               int var6 = var1.getCursor().lastIndexOf(32) + 1;
               int var7 = var1.getCursor().length();
               StringRange var8 = StringRange.between(var6, var7);
               LinkedList var9 = new LinkedList();

               for (String var11 : var5) {
                  var9.add(new Suggestion(var8, var11));
               }

               this.con.unsafe().sendPacket(new TabCompleteResponse(var1.getTransactionId(), new Suggestions(var8, var9)));
            }

            throw CancelSendSignal.INSTANCE;
         }
      }
   }

   @Override
   public void handle(ClientSettings var1) throws Exception {
      this.con.setSettings(var1);
      SettingsChangedEvent var2 = new SettingsChangedEvent(this.con);
      this.bungee.getPluginManager().callEvent(var2);
   }

   @Override
   public void handle(PluginMessage var1) throws Exception {
      if (var1.getTag().equals("BungeeCord")) {
         throw CancelSendSignal.INSTANCE;
      } else {
         if (var1.getTag().equals("FML|HS") || var1.getTag().equals("FML") || var1.getTag().equals("LMC")) {
            ConnectionStats var2 = XCord.getAntiBotManager().getStats(this.con.getAddress().getAddress().getHostAddress());
            var2.setWhitelisted(true);
         }

         if (BungeeCord.getInstance().config.isForgeSupport()) {
            if (var1.getTag().equals("FML") && var1.getStream().readUnsignedByte() == 1) {
               throw CancelSendSignal.INSTANCE;
            }

            if (var1.getTag().equals("FML|HS")) {
               this.con.getForgeClientHandler().handle(var1);
               throw CancelSendSignal.INSTANCE;
            }

            if (this.con.getServer() != null && !this.con.getServer().isForgeServer() && var1.getData().length > 32767) {
               throw CancelSendSignal.INSTANCE;
            }
         }

         PluginMessageEvent var3 = new PluginMessageEvent(this.con, this.con.getServer(), var1.getTag(), (byte[])var1.getData().clone());
         if (this.bungee.getPluginManager().callEvent(var3).isCancelled()) {
            throw CancelSendSignal.INSTANCE;
         } else {
            this.con.getPendingConnection().relayMessage(var1);
         }
      }
   }

   @Override
   public String toString() {
      return "[" + this.con.getAddress() + "|" + this.con.getName() + "] -> UpstreamBridge";
   }
}
