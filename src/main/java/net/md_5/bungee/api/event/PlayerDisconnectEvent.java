package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerDisconnectEvent extends Event {
   private final ProxiedPlayer player;

   public PlayerDisconnectEvent(ProxiedPlayer var1) {
      this.player = var1;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   @Override
   public String toString() {
      return "PlayerDisconnectEvent(player=" + this.getPlayer() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerDisconnectEvent)) {
         return false;
      } else {
         PlayerDisconnectEvent var2 = (PlayerDisconnectEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerDisconnectEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      ProxiedPlayer var3 = this.getPlayer();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
