package net.md_5.bungee.api.event;

import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class ServerDisconnectEvent extends Event {
   @NonNull
   private final ProxiedPlayer player;
   @NonNull
   private final ServerInfo target;

   @NonNull
   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   @NonNull
   public ServerInfo getTarget() {
      return this.target;
   }

   public ServerDisconnectEvent(@NonNull ProxiedPlayer var1, @NonNull ServerInfo var2) {
      if (var1 == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else if (var2 == null) {
         throw new NullPointerException("target is marked non-null but is null");
      } else {
         this.player = var1;
         this.target = var2;
      }
   }

   @Override
   public String toString() {
      return "ServerDisconnectEvent(player=" + this.getPlayer() + ", target=" + this.getTarget() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerDisconnectEvent)) {
         return false;
      } else {
         ServerDisconnectEvent var2 = (ServerDisconnectEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerInfo var5 = this.getTarget();
               ServerInfo var6 = var2.getTarget();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ServerDisconnectEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ProxiedPlayer var3 = this.getPlayer();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerInfo var4 = this.getTarget();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
