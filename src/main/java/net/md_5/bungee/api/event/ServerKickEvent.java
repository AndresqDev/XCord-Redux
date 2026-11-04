package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ServerKickEvent extends Event implements Cancellable {
   private boolean cancelled;
   private final ProxiedPlayer player;
   private final ServerInfo kickedFrom;
   private BaseComponent[] kickReasonComponent;
   private ServerInfo cancelServer;
   private ServerKickEvent.State state;
   private ServerKickEvent.Cause cause;

   @Deprecated
   public ServerKickEvent(ProxiedPlayer var1, BaseComponent[] var2, ServerInfo var3) {
      this(var1, var2, var3, ServerKickEvent.State.UNKNOWN);
   }

   @Deprecated
   public ServerKickEvent(ProxiedPlayer var1, BaseComponent[] var2, ServerInfo var3, ServerKickEvent.State var4) {
      this(var1, var1.getServer().getInfo(), var2, var3, var4);
   }

   @Deprecated
   public ServerKickEvent(ProxiedPlayer var1, ServerInfo var2, BaseComponent[] var3, ServerInfo var4, ServerKickEvent.State var5) {
      this(var1, var2, var3, var4, var5, ServerKickEvent.Cause.UNKNOWN);
   }

   public ServerKickEvent(ProxiedPlayer var1, ServerInfo var2, BaseComponent[] var3, ServerInfo var4, ServerKickEvent.State var5, ServerKickEvent.Cause var6) {
      this.player = var1;
      this.kickedFrom = var2;
      this.kickReasonComponent = var3;
      this.cancelServer = var4;
      this.state = var5;
      this.cause = var6;
   }

   @Deprecated
   public String getKickReason() {
      return BaseComponent.toLegacyText(this.kickReasonComponent);
   }

   @Deprecated
   public void setKickReason(String var1) {
      this.kickReasonComponent = TextComponent.fromLegacyText(var1);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public ProxiedPlayer getPlayer() {
      return this.player;
   }

   public ServerInfo getKickedFrom() {
      return this.kickedFrom;
   }

   public BaseComponent[] getKickReasonComponent() {
      return this.kickReasonComponent;
   }

   public ServerInfo getCancelServer() {
      return this.cancelServer;
   }

   public ServerKickEvent.State getState() {
      return this.state;
   }

   public ServerKickEvent.Cause getCause() {
      return this.cause;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   public void setKickReasonComponent(BaseComponent[] var1) {
      this.kickReasonComponent = var1;
   }

   public void setCancelServer(ServerInfo var1) {
      this.cancelServer = var1;
   }

   public void setState(ServerKickEvent.State var1) {
      this.state = var1;
   }

   public void setCause(ServerKickEvent.Cause var1) {
      this.cause = var1;
   }

   @Override
   public String toString() {
      return "ServerKickEvent(cancelled="
         + this.isCancelled()
         + ", player="
         + this.getPlayer()
         + ", kickedFrom="
         + this.getKickedFrom()
         + ", kickReasonComponent="
         + Arrays.deepToString(this.getKickReasonComponent())
         + ", cancelServer="
         + this.getCancelServer()
         + ", state="
         + this.getState()
         + ", cause="
         + this.getCause()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerKickEvent)) {
         return false;
      } else {
         ServerKickEvent var2 = (ServerKickEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            ProxiedPlayer var3 = this.getPlayer();
            ProxiedPlayer var4 = var2.getPlayer();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerInfo var5 = this.getKickedFrom();
               ServerInfo var6 = var2.getKickedFrom();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  if (!Arrays.deepEquals(this.getKickReasonComponent(), var2.getKickReasonComponent())) {
                     return false;
                  } else {
                     ServerInfo var7 = this.getCancelServer();
                     ServerInfo var8 = var2.getCancelServer();
                     if (var7 == null ? var8 == null : var7.equals(var8)) {
                        ServerKickEvent.State var9 = this.getState();
                        ServerKickEvent.State var10 = var2.getState();
                        if (var9 == null ? var10 == null : var9.equals(var10)) {
                           ServerKickEvent.Cause var11 = this.getCause();
                           ServerKickEvent.Cause var12 = var2.getCause();
                           return var11 == null ? var12 == null : var11.equals(var12);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
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
      return var1 instanceof ServerKickEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      ProxiedPlayer var3 = this.getPlayer();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerInfo var4 = this.getKickedFrom();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      var2 = var2 * 59 + Arrays.deepHashCode(this.getKickReasonComponent());
      ServerInfo var5 = this.getCancelServer();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      ServerKickEvent.State var6 = this.getState();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      ServerKickEvent.Cause var7 = this.getCause();
      return var2 * 59 + (var7 == null ? 43 : var7.hashCode());
   }

   public static enum Cause {
      SERVER,
      LOST_CONNECTION,
      EXCEPTION,
      UNKNOWN;
   }

   public static enum State {
      CONNECTING,
      CONNECTED,
      UNKNOWN;
   }
}
