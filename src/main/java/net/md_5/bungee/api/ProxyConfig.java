package net.md_5.bungee.api;

import java.util.Collection;
import java.util.Map;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;

@Deprecated
public interface ProxyConfig {
   int getTimeout();

   String getUuid();

   Collection<ListenerInfo> getListeners();

   @Deprecated
   Map<String, ServerInfo> getServers();

   Map<String, ServerInfo> getServersCopy();

   ServerInfo getServerInfo(String var1);

   ServerInfo addServer(ServerInfo var1);

   boolean addServers(Collection<ServerInfo> var1);

   ServerInfo removeServerNamed(String var1);

   ServerInfo removeServer(ServerInfo var1);

   boolean removeServersNamed(Collection<String> var1);

   boolean removeServers(Collection<ServerInfo> var1);

   boolean isOnlineMode();

   boolean isLogCommands();

   int getRemotePingCache();

   int getPlayerLimit();

   Collection<String> getDisabledCommands();

   int getServerConnectTimeout();

   int getRemotePingTimeout();

   @Deprecated
   int getThrottle();

   @Deprecated
   boolean isIpForward();

   @Deprecated
   String getFavicon();

   Favicon getFaviconObject();

   boolean isLogInitialHandlerConnections();

   String getGameVersion();

   boolean isUseNettyDnsResolver();

   int getTabThrottle();

   boolean isDisableModernTabLimiter();

   boolean isDisableEntityMetadataRewrite();

   boolean isDisableTabListRewrite();
}
