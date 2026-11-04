package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectedEvent extends Event {
   private final ProxiedPlayer player;
   private final Server server;

   public ServerConnectedEvent(ProxiedPlayer var1, Server var2) {
      this.player = var1;
      this.server = var2;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   public Server getServer() {
      return this.server;
   }

   @Override
   public String toString() {
      return "ServerConnectedEvent(player=" + this.getPlayer() + ", server=" + this.getServer() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerConnectedEvent)) {
         return false;
      } else {
         ServerConnectedEvent var2 = (ServerConnectedEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Server var5 = this.getServer();
               Server var6 = var2.getServer();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ServerConnectedEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ProxiedPlayer var3 = this.getPlayer();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Server var4 = this.getServer();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
