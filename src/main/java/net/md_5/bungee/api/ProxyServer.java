package net.md_5.bungee.api;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public abstract class ProxyServer {
   private static ProxyServer instance;

   public static void setInstance(ProxyServer var0) {
      instance = var0;
   }

   public abstract String getName();

   public abstract String getVersion();

   public abstract String getTranslation(String var1, Object... var2);

   public abstract Logger getLogger();

   public abstract Collection<ProxiedPlayer> getPlayers();

   public abstract ProxiedPlayer getPlayer(String var1);

   public abstract ProxiedPlayer getPlayer(UUID var1);

   @Deprecated
   public abstract Map<String, ServerInfo> getServers();

   public abstract Map<String, ServerInfo> getServersCopy();

   public abstract ServerInfo getServerInfo(String var1);

   public abstract PluginManager getPluginManager();

   public abstract ConfigurationAdapter getConfigurationAdapter();

   public abstract void setConfigurationAdapter(ConfigurationAdapter var1);

   public abstract ReconnectHandler getReconnectHandler();

   public abstract void setReconnectHandler(ReconnectHandler var1);

   public abstract void stop();

   public abstract void stop(String var1);

   public abstract void registerChannel(String var1);

   public abstract void unregisterChannel(String var1);

   public abstract Collection<String> getChannels();

   @Deprecated
   public abstract String getGameVersion();

   @Deprecated
   public abstract int getProtocolVersion();

   public abstract ServerInfo constructServerInfo(String var1, InetSocketAddress var2, String var3, boolean var4);

   public abstract ServerInfo constructServerInfo(String var1, SocketAddress var2, String var3, boolean var4);

   public abstract CommandSender getConsole();

   public abstract File getPluginsFolder();

   public abstract TaskScheduler getScheduler();

   public abstract int getOnlineCount();

   @Deprecated
   public abstract void broadcast(String var1);

   public abstract void broadcast(BaseComponent... var1);

   public abstract void broadcast(BaseComponent var1);

   public abstract Collection<String> getDisabledCommands();

   public abstract ProxyConfig getConfig();

   public abstract Collection<ProxiedPlayer> matchPlayer(String var1);

   public abstract Title createTitle();

   public static ProxyServer getInstance() {
      return instance;
   }
}
