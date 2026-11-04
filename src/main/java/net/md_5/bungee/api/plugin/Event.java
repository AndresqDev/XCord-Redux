package net.md_5.bungee.api.plugin;

import net.md_5.bungee.api.ProxyServer;

public abstract class Event {
   public void postCall() {
   }

   public final boolean callEvent() {
      ProxyServer.getInstance().getPluginManager().callEvent(this);
      return this instanceof Cancellable ? !((Cancellable)this).isCancelled() : true;
   }
}
