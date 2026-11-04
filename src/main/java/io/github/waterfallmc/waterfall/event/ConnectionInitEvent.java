package io.github.waterfallmc.waterfall.event;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.event.AsyncEvent;
import net.md_5.bungee.api.plugin.Cancellable;

public class ConnectionInitEvent extends AsyncEvent<ConnectionInitEvent> implements Cancellable {
   private final SocketAddress remoteAddress;
   private final ListenerInfo listener;
   private boolean isCancelled = false;

   public ConnectionInitEvent(SocketAddress var1, ListenerInfo var2, Callback<ConnectionInitEvent> var3) {
      super(var3);
      this.remoteAddress = var1;
      this.listener = var2;
   }

   public boolean isCancelled() {
      return this.isCancelled;
   }

   public void setCancelled(boolean var1) {
      this.isCancelled = var1;
   }

   @Deprecated
   public InetSocketAddress getRemoteAddress() {
      return (InetSocketAddress)this.remoteAddress;
   }

   public SocketAddress getRemoteSocketAddress() {
      return this.remoteAddress;
   }

   public String toString() {
      return "ConnectionInitEvent(remoteAddress=" + this.getRemoteAddress() + ", listener=" + this.listener + ", isCancelled=" + this.isCancelled() + ")";
   }
}
