package net.md_5.bungee.api;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class AbstractReconnectHandler implements ReconnectHandler {
   @Override
   public ServerInfo getServer(ProxiedPlayer var1) {
      ServerInfo var2 = getForcedHost(var1.getPendingConnection());
      if (var2 == null) {
         var2 = this.getStoredServer(var1);
         if (var2 == null) {
            var2 = ProxyServer.getInstance().getServerInfo(var1.getPendingConnection().getListener().getDefaultServer());
         }

         Preconditions.checkState(var2 != null, "Default server not defined");
      }

      return var2;
   }

   public static ServerInfo getForcedHost(PendingConnection var0) {
      if (var0.getVirtualHost() == null) {
         return null;
      } else {
         String var1 = var0.getListener().getForcedHosts().get(var0.getVirtualHost().getHostString());
         if (var1 == null && var0.getListener().isForceDefault()) {
            var1 = var0.getListener().getDefaultServer();
         }

         return ProxyServer.getInstance().getServerInfo(var1);
      }
   }

   protected abstract ServerInfo getStoredServer(ProxiedPlayer var1);
}
