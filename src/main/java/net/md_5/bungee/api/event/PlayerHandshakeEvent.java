package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.protocol.packet.Handshake;

public class PlayerHandshakeEvent extends Event {
   private final PendingConnection connection;
   private final Handshake handshake;

   public PlayerHandshakeEvent(PendingConnection var1, Handshake var2) {
      this.connection = var1;
      this.handshake = var2;
   }

   public PendingConnection getConnection() {
      return this.connection;
   }

   public Handshake getHandshake() {
      return this.handshake;
   }

   @Override
   public String toString() {
      return "PlayerHandshakeEvent(connection=" + this.getConnection() + ", handshake=" + this.getHandshake() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerHandshakeEvent)) {
         return false;
      } else {
         PlayerHandshakeEvent var2 = (PlayerHandshakeEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            PendingConnection var3 = this.getConnection();
            PendingConnection var4 = var2.getConnection();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Handshake var5 = this.getHandshake();
               Handshake var6 = var2.getHandshake();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerHandshakeEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      PendingConnection var3 = this.getConnection();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Handshake var4 = this.getHandshake();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
