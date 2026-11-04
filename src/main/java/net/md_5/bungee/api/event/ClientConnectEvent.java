package net.md_5.bungee.api.event;

import java.net.SocketAddress;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class ClientConnectEvent extends Event implements Cancellable {
   private boolean cancelled;
   private final SocketAddress socketAddress;
   private final ListenerInfo listener;

   public ClientConnectEvent(SocketAddress var1, ListenerInfo var2) {
      this.socketAddress = var1;
      this.listener = var2;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public SocketAddress getSocketAddress() {
      return this.socketAddress;
   }

   public ListenerInfo getListener() {
      return this.listener;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public String toString() {
      return "ClientConnectEvent(cancelled=" + this.isCancelled() + ", socketAddress=" + this.getSocketAddress() + ", listener=" + this.getListener() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClientConnectEvent)) {
         return false;
      } else {
         ClientConnectEvent var2 = (ClientConnectEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            SocketAddress var3 = this.getSocketAddress();
            SocketAddress var4 = var2.getSocketAddress();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ListenerInfo var5 = this.getListener();
               ListenerInfo var6 = var2.getListener();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ClientConnectEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      SocketAddress var3 = this.getSocketAddress();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ListenerInfo var4 = this.getListener();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
