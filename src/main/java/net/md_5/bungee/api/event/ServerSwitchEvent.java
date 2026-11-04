package net.md_5.bungee.api.event;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerSwitchEvent extends Event {
   private final ProxiedPlayer player;
   private final ServerInfo from;

   public ServerSwitchEvent(ProxiedPlayer var1, ServerInfo var2) {
      this.player = var1;
      this.from = var2;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   public ServerInfo getFrom() {
      return this.from;
   }

   @Override
   public String toString() {
      return "ServerSwitchEvent(player=" + this.getPlayer() + ", from=" + this.getFrom() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerSwitchEvent)) {
         return false;
      } else {
         ServerSwitchEvent var2 = (ServerSwitchEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerInfo var5 = this.getFrom();
               ServerInfo var6 = var2.getFrom();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ServerSwitchEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ProxiedPlayer var3 = this.getPlayer();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerInfo var4 = this.getFrom();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
