package net.md_5.bungee.api.event;

import lombok.NonNull;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerConnectEvent extends Event implements Cancellable {
   private final ProxiedPlayer player;
   @NonNull
   private ServerInfo target;
   private final ServerConnectEvent.Reason reason;
   private final ServerConnectRequest request;
   private boolean cancelled;

   @Deprecated
   public ServerConnectEvent(ProxiedPlayer var1, ServerInfo var2) {
      this(var1, var2, ServerConnectEvent.Reason.UNKNOWN);
   }

   @Deprecated
   public ServerConnectEvent(ProxiedPlayer var1, ServerInfo var2, ServerConnectEvent.Reason var3) {
      this(var1, var2, var3, null);
   }

   public ServerConnectEvent(ProxiedPlayer var1, ServerInfo var2, ServerConnectEvent.Reason var3, ServerConnectRequest var4) {
      this.player = var1;
      this.target = var2;
      this.reason = var3;
      this.request = var4;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   @NonNull
   public ServerInfo getTarget() {
      return this.target;
   }

   public ServerConnectEvent.Reason getReason() {
      return this.reason;
   }

   public ServerConnectRequest getRequest() {
      return this.request;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setTarget(@NonNull ServerInfo var1) {
      if (var1 == null) {
         throw new NullPointerException("target is marked non-null but is null");
      } else {
         this.target = var1;
      }
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public String toString() {
      return "ServerConnectEvent(player="
         + this.getPlayer()
         + ", target="
         + this.getTarget()
         + ", reason="
         + this.getReason()
         + ", request="
         + this.getRequest()
         + ", cancelled="
         + this.isCancelled()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerConnectEvent)) {
         return false;
      } else {
         ServerConnectEvent var2 = (ServerConnectEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerInfo var5 = this.getTarget();
               ServerInfo var6 = var2.getTarget();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  ServerConnectEvent.Reason var7 = this.getReason();
                  ServerConnectEvent.Reason var8 = var2.getReason();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     ServerConnectRequest var9 = this.getRequest();
                     ServerConnectRequest var10 = var2.getRequest();
                     return var9 == null ? var10 == null : var9.equals(var10);
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
      return var1 instanceof ServerConnectEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      ProxiedPlayer var3 = this.getPlayer();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerInfo var4 = this.getTarget();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      ServerConnectEvent.Reason var5 = this.getReason();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      ServerConnectRequest var6 = this.getRequest();
      return var2 * 59 + (var6 == null ? 43 : var6.hashCode());
   }

   public static enum Reason {
      LOBBY_FALLBACK,
      COMMAND,
      SERVER_DOWN_REDIRECT,
      KICK_REDIRECT,
      PLUGIN_MESSAGE,
      JOIN_PROXY,
      PLUGIN,
      UNKNOWN;
   }
}
