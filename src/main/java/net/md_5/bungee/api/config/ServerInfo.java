package net.md_5.bungee.api.config;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ServerInfo {
   String getName();

   @Deprecated
   InetSocketAddress getAddress();

   SocketAddress getSocketAddress();

   Collection<ProxiedPlayer> getPlayers();

   String getMotd();

   boolean isRestricted();

   String getPermission();

   boolean canAccess(CommandSender var1);

   void sendData(String var1, byte[] var2);

   boolean sendData(String var1, byte[] var2, boolean var3);

   void ping(Callback<ServerPing> var1);
}
