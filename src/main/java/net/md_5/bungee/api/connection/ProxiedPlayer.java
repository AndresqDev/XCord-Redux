package net.md_5.bungee.api.connection;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.SkinConfiguration;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;

public interface ProxiedPlayer extends CommandSender, Connection {
   String getDisplayName();

   void setDisplayName(String var1);

   void sendMessage(ChatMessageType var1, BaseComponent... var2);

   void sendMessage(ChatMessageType var1, BaseComponent var2);

   void sendMessage(UUID var1, BaseComponent... var2);

   void sendMessage(UUID var1, BaseComponent var2);

   void connect(ServerInfo var1);

   void connect(ServerInfo var1, ServerConnectEvent.Reason var2);

   void connect(ServerInfo var1, Callback<Boolean> var2);

   void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3);

   void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3, int var4);

   void connect(ServerInfo var1, Callback<Boolean> var2, ServerConnectEvent.Reason var3);

   void connect(ServerInfo var1, Callback<Boolean> var2, boolean var3, ServerConnectEvent.Reason var4, int var5);

   void connect(ServerConnectRequest var1);

   Server getServer();

   int getPing();

   void sendData(String var1, byte[] var2);

   PendingConnection getPendingConnection();

   void chat(String var1);

   ServerInfo getReconnectServer();

   void setReconnectServer(ServerInfo var1);

   @Deprecated
   String getUUID();

   UUID getUniqueId();

   Locale getLocale();

   byte getViewDistance();

   ProxiedPlayer.ChatMode getChatMode();

   boolean hasChatColors();

   SkinConfiguration getSkinParts();

   ProxiedPlayer.MainHand getMainHand();

   void setTabHeader(BaseComponent var1, BaseComponent var2);

   void setTabHeader(BaseComponent[] var1, BaseComponent[] var2);

   void resetTabHeader();

   void sendTitle(Title var1);

   boolean isForgeUser();

   Map<String, String> getModList();

   Scoreboard getScoreboard();

   public static enum ChatMode {
      SHOWN,
      COMMANDS_ONLY,
      HIDDEN;
   }

   public static enum MainHand {
      LEFT,
      RIGHT;
   }
}
