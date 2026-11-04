package net.md_5.bungee.api.event;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;

public class ProxyPingEvent extends AsyncEvent<ProxyPingEvent> {
   private final PendingConnection connection;
   private ServerPing response;

   public ProxyPingEvent(PendingConnection var1, ServerPing var2, Callback<ProxyPingEvent> var3) {
      super(var3);
      this.connection = var1;
      this.response = var2;
   }

   public PendingConnection getConnection() {
      return this.connection;
   }

   public ServerPing getResponse() {
      return this.response;
   }

   public void setResponse(ServerPing var1) {
      this.response = var1;
   }

   @Override
   public String toString() {
      return "ProxyPingEvent(connection=" + this.getConnection() + ", response=" + this.getResponse() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ProxyPingEvent)) {
         return false;
      } else {
         ProxyPingEvent var2 = (ProxyPingEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            PendingConnection var3 = this.getConnection();
            PendingConnection var4 = var2.getConnection();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerPing var5 = this.getResponse();
               ServerPing var6 = var2.getResponse();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof ProxyPingEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      PendingConnection var3 = this.getConnection();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerPing var4 = this.getResponse();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
