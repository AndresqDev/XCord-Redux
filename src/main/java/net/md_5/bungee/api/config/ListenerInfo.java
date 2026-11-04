package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class ListenerInfo {
   private final SocketAddress socketAddress;
   private final String motd;
   private final int maxPlayers;
   private final int tabListSize;
   private final List<String> serverPriority;
   private final boolean forceDefault;
   private final Map<String, String> forcedHosts;
   private final String tabListType;
   private final boolean setLocalAddress;
   private final boolean pingPassthrough;
   private final int queryPort;
   private final boolean queryEnabled;
   private final boolean proxyProtocol;

   @Deprecated
   public ListenerInfo(
      InetSocketAddress var1,
      String var2,
      int var3,
      int var4,
      List<String> var5,
      boolean var6,
      Map<String, String> var7,
      String var8,
      boolean var9,
      boolean var10,
      int var11,
      boolean var12
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, false);
   }

   @Deprecated
   public String getDefaultServer() {
      return this.serverPriority.get(0);
   }

   @Deprecated
   public String getFallbackServer() {
      return this.serverPriority.size() > 1 ? this.serverPriority.get(1) : this.getDefaultServer();
   }

   @Deprecated
   public InetSocketAddress getHost() {
      return (InetSocketAddress)this.socketAddress;
   }

   public SocketAddress getSocketAddress() {
      return this.socketAddress;
   }

   public String getMotd() {
      return this.motd;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public int getTabListSize() {
      return this.tabListSize;
   }

   public List<String> getServerPriority() {
      return this.serverPriority;
   }

   public boolean isForceDefault() {
      return this.forceDefault;
   }

   public Map<String, String> getForcedHosts() {
      return this.forcedHosts;
   }

   public String getTabListType() {
      return this.tabListType;
   }

   public boolean isSetLocalAddress() {
      return this.setLocalAddress;
   }

   public boolean isPingPassthrough() {
      return this.pingPassthrough;
   }

   public int getQueryPort() {
      return this.queryPort;
   }

   public boolean isQueryEnabled() {
      return this.queryEnabled;
   }

   public boolean isProxyProtocol() {
      return this.proxyProtocol;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ListenerInfo)) {
         return false;
      } else {
         ListenerInfo var2 = (ListenerInfo)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getMaxPlayers() != var2.getMaxPlayers()) {
            return false;
         } else if (this.getTabListSize() != var2.getTabListSize()) {
            return false;
         } else if (this.isForceDefault() != var2.isForceDefault()) {
            return false;
         } else if (this.isSetLocalAddress() != var2.isSetLocalAddress()) {
            return false;
         } else if (this.isPingPassthrough() != var2.isPingPassthrough()) {
            return false;
         } else if (this.getQueryPort() != var2.getQueryPort()) {
            return false;
         } else if (this.isQueryEnabled() != var2.isQueryEnabled()) {
            return false;
         } else if (this.isProxyProtocol() != var2.isProxyProtocol()) {
            return false;
         } else {
            SocketAddress var3 = this.getSocketAddress();
            SocketAddress var4 = var2.getSocketAddress();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getMotd();
               String var6 = var2.getMotd();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  List var7 = this.getServerPriority();
                  List var8 = var2.getServerPriority();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     Map var9 = this.getForcedHosts();
                     Map var10 = var2.getForcedHosts();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        String var11 = this.getTabListType();
                        String var12 = var2.getTabListType();
                        return var11 == null ? var12 == null : var11.equals(var12);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ListenerInfo;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getMaxPlayers();
      var2 = var2 * 59 + this.getTabListSize();
      var2 = var2 * 59 + (this.isForceDefault() ? 79 : 97);
      var2 = var2 * 59 + (this.isSetLocalAddress() ? 79 : 97);
      var2 = var2 * 59 + (this.isPingPassthrough() ? 79 : 97);
      var2 = var2 * 59 + this.getQueryPort();
      var2 = var2 * 59 + (this.isQueryEnabled() ? 79 : 97);
      var2 = var2 * 59 + (this.isProxyProtocol() ? 79 : 97);
      SocketAddress var3 = this.getSocketAddress();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getMotd();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      List var5 = this.getServerPriority();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Map var6 = this.getForcedHosts();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      String var7 = this.getTabListType();
      return var2 * 59 + (var7 == null ? 43 : var7.hashCode());
   }

   @Override
   public String toString() {
      return "ListenerInfo(socketAddress="
         + this.getSocketAddress()
         + ", motd="
         + this.getMotd()
         + ", maxPlayers="
         + this.getMaxPlayers()
         + ", tabListSize="
         + this.getTabListSize()
         + ", serverPriority="
         + this.getServerPriority()
         + ", forceDefault="
         + this.isForceDefault()
         + ", forcedHosts="
         + this.getForcedHosts()
         + ", tabListType="
         + this.getTabListType()
         + ", setLocalAddress="
         + this.isSetLocalAddress()
         + ", pingPassthrough="
         + this.isPingPassthrough()
         + ", queryPort="
         + this.getQueryPort()
         + ", queryEnabled="
         + this.isQueryEnabled()
         + ", proxyProtocol="
         + this.isProxyProtocol()
         + ")";
   }

   public ListenerInfo(
      SocketAddress var1,
      String var2,
      int var3,
      int var4,
      List<String> var5,
      boolean var6,
      Map<String, String> var7,
      String var8,
      boolean var9,
      boolean var10,
      int var11,
      boolean var12,
      boolean var13
   ) {
      this.socketAddress = var1;
      this.motd = var2;
      this.maxPlayers = var3;
      this.tabListSize = var4;
      this.serverPriority = var5;
      this.forceDefault = var6;
      this.forcedHosts = var7;
      this.tabListType = var8;
      this.setLocalAddress = var9;
      this.pingPassthrough = var10;
      this.queryPort = var11;
      this.queryEnabled = var12;
      this.proxyProtocol = var13;
   }
}
