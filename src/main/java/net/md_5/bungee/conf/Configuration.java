package net.md_5.bungee.conf;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import gnu.trove.map.TMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.util.CaseInsensitiveMap;
import net.md_5.bungee.util.CaseInsensitiveSet;

public abstract class Configuration implements ProxyConfig {
   private int timeout = 30000;
   private String uuid = UUID.randomUUID().toString();
   private Collection<ListenerInfo> listeners;
   private final Object serversLock = new Object();
   private TMap<String, ServerInfo> servers;
   private boolean onlineMode = true;
   private boolean enforceSecureProfile;
   private boolean logCommands;
   private boolean logPings = true;
   private int remotePingCache = -1;
   private int playerLimit = -1;
   private Collection<String> disabledCommands;
   private int serverConnectTimeout = 5000;
   private int remotePingTimeout = 5000;
   private int throttle = 4000;
   private int throttleLimit = 3;
   private boolean ipForward;
   private Favicon favicon;
   private int compressionThreshold = 256;
   private boolean preventProxyConnections;
   private boolean forgeSupport = true;

   public void load() {
      synchronized (this.serversLock) {
         ConfigurationAdapter var2 = ProxyServer.getInstance().getConfigurationAdapter();
         var2.load();
         File var3 = new File("server-icon.png");
         if (var3.exists()) {
            try {
               this.favicon = Favicon.create(ImageIO.read(var3));
            } catch (IllegalArgumentException | IOException var15) {
               ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load server icon", (Throwable)var15);
            }
         }

         this.listeners = var2.getListeners();
         this.timeout = var2.getInt("timeout", this.timeout);
         this.uuid = var2.getString("stats", this.uuid);
         this.onlineMode = var2.getBoolean("online_mode", this.onlineMode);
         this.enforceSecureProfile = var2.getBoolean("enforce_secure_profile", this.enforceSecureProfile);
         this.logCommands = var2.getBoolean("log_commands", this.logCommands);
         this.logPings = var2.getBoolean("log_pings", this.logPings);
         this.remotePingCache = var2.getInt("remote_ping_cache", this.remotePingCache);
         this.playerLimit = var2.getInt("player_limit", this.playerLimit);
         this.serverConnectTimeout = var2.getInt("server_connect_timeout", this.serverConnectTimeout);
         this.remotePingTimeout = var2.getInt("remote_ping_timeout", this.remotePingTimeout);
         this.throttle = var2.getInt("connection_throttle", this.throttle);
         this.throttleLimit = var2.getInt("connection_throttle_limit", this.throttleLimit);
         this.ipForward = var2.getBoolean("ip_forward", this.ipForward);
         this.compressionThreshold = var2.getInt("network_compression_threshold", this.compressionThreshold);
         this.preventProxyConnections = var2.getBoolean("prevent_proxy_connections", this.preventProxyConnections);
         this.forgeSupport = var2.getBoolean("forge_support", this.forgeSupport);
         this.disabledCommands = new CaseInsensitiveSet((Collection<? extends String>)var2.getList("disabled_commands", Arrays.asList("disabledcommandhere")));
         Preconditions.checkArgument(this.listeners != null && !this.listeners.isEmpty(), "No listeners defined.");
         Map var4 = var2.getServers();
         Preconditions.checkArgument(var4 != null && !var4.isEmpty(), "No servers defined");
         if (this.servers == null) {
            this.servers = new CaseInsensitiveMap(var4);
         } else {
            Map var5 = this.getServersCopy();

            for (ServerInfo var7 : var5.values()) {
               ServerInfo var8 = (ServerInfo)var4.get(var7.getName());
               if ((var8 == null || !var7.getAddress().equals(var8.getAddress())) && !var7.getPlayers().isEmpty()) {
                  BungeeCord.getInstance().getLogger().info("Moving players off of server: " + var7.getName());

                  for (ProxiedPlayer var10 : var7.getPlayers()) {
                     ListenerInfo var11 = var10.getPendingConnection().getListener();
                     String var12 = var4.get(var11.getDefaultServer()) == null ? var11.getDefaultServer() : var11.getFallbackServer();
                     ServerInfo var13 = (ServerInfo)var4.get(var12);
                     if (var13 == null) {
                        BungeeCord.getInstance()
                           .getLogger()
                           .severe(
                              "Couldn't find server "
                                 + var11.getDefaultServer()
                                 + " or "
                                 + var11.getFallbackServer()
                                 + " to put player "
                                 + var10.getName()
                                 + " on"
                           );
                        var10.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick", "Not found on reload"));
                     } else {
                        var10.connect(var13, (var2x, var3x) -> {
                           if (!var2x) {
                              BungeeCord.getInstance().getLogger().log(Level.WARNING, "Failed to connect " + var10.getName() + " to " + var13.getName(), var3x);
                              var10.disconnect(BungeeCord.getInstance().getTranslation("fallback_kick", var3x.getCause().getClass().getName()));
                           }
                        });
                     }
                  }
               } else {
                  var4.put(var7.getName(), var7);
               }
            }

            this.servers = new CaseInsensitiveMap(var4);
         }

         for (ListenerInfo var18 : this.listeners) {
            for (int var19 = 0; var19 < var18.getServerPriority().size(); var19++) {
               String var21 = var18.getServerPriority().get(var19);
               Preconditions.checkArgument(this.servers.containsKey(var21), "Server %s (priority %s) is not defined", var21, var19);
            }

            for (String var22 : var18.getForcedHosts().values()) {
               if (!this.servers.containsKey(var22)) {
                  ProxyServer.getInstance().getLogger().log(Level.WARNING, "Forced host server {0} is not defined", var22);
               }
            }
         }
      }
   }

   @Deprecated
   @Override
   public String getFavicon() {
      return this.getFaviconObject().getEncoded();
   }

   @Override
   public Favicon getFaviconObject() {
      return this.favicon;
   }

   @Override
   public Map<String, ServerInfo> getServersCopy() {
      synchronized (this.serversLock) {
         return ImmutableMap.copyOf(this.servers);
      }
   }

   @Override
   public ServerInfo getServerInfo(String var1) {
      synchronized (this.serversLock) {
         return (ServerInfo)this.servers.get(var1);
      }
   }

   @Override
   public ServerInfo addServer(ServerInfo var1) {
      synchronized (this.serversLock) {
         return (ServerInfo)this.servers.put(var1.getName(), var1);
      }
   }

   @Override
   public boolean addServers(Collection<ServerInfo> var1) {
      synchronized (this.serversLock) {
         boolean var3 = false;

         for (ServerInfo var5 : var1) {
            if (var5 != this.servers.put(var5.getName(), var5)) {
               var3 = true;
            }
         }

         return var3;
      }
   }

   @Override
   public ServerInfo removeServerNamed(String var1) {
      synchronized (this.serversLock) {
         return (ServerInfo)this.servers.remove(var1);
      }
   }

   @Override
   public ServerInfo removeServer(ServerInfo var1) {
      synchronized (this.serversLock) {
         return (ServerInfo)this.servers.remove(var1.getName());
      }
   }

   @Override
   public boolean removeServersNamed(Collection<String> var1) {
      synchronized (this.serversLock) {
         return this.servers.keySet().removeAll(var1);
      }
   }

   @Override
   public boolean removeServers(Collection<ServerInfo> var1) {
      synchronized (this.serversLock) {
         boolean var3 = false;

         for (ServerInfo var5 : var1) {
            if (null != this.servers.remove(var5.getName())) {
               var3 = true;
            }
         }

         return var3;
      }
   }

   @Override
   public int getTimeout() {
      return this.timeout;
   }

   @Override
   public String getUuid() {
      return this.uuid;
   }

   @Override
   public Collection<ListenerInfo> getListeners() {
      return this.listeners;
   }

   public Object getServersLock() {
      return this.serversLock;
   }

   public TMap<String, ServerInfo> getServers() {
      return this.servers;
   }

   @Override
   public boolean isOnlineMode() {
      return this.onlineMode;
   }

   public boolean isEnforceSecureProfile() {
      return this.enforceSecureProfile;
   }

   @Override
   public boolean isLogCommands() {
      return this.logCommands;
   }

   public boolean isLogPings() {
      return this.logPings;
   }

   @Override
   public int getRemotePingCache() {
      return this.remotePingCache;
   }

   @Override
   public int getPlayerLimit() {
      return this.playerLimit;
   }

   @Override
   public Collection<String> getDisabledCommands() {
      return this.disabledCommands;
   }

   @Override
   public int getServerConnectTimeout() {
      return this.serverConnectTimeout;
   }

   @Override
   public int getRemotePingTimeout() {
      return this.remotePingTimeout;
   }

   @Override
   public int getThrottle() {
      return this.throttle;
   }

   public int getThrottleLimit() {
      return this.throttleLimit;
   }

   @Override
   public boolean isIpForward() {
      return this.ipForward;
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }

   public boolean isPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public boolean isForgeSupport() {
      return this.forgeSupport;
   }
}
