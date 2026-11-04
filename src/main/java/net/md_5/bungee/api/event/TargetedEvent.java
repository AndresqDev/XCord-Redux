package net.md_5.bungee.api.event;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Event;

public abstract class TargetedEvent extends Event {
   private final Connection sender;
   private final Connection receiver;

   public Connection getSender() {
      return this.sender;
   }

   public Connection getReceiver() {
      return this.receiver;
   }

   @Override
   public String toString() {
      return "TargetedEvent(sender=" + this.getSender() + ", receiver=" + this.getReceiver() + ")";
   }

   public TargetedEvent(Connection var1, Connection var2) {
      this.sender = var1;
      this.receiver = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TargetedEvent)) {
         return false;
      } else {
         TargetedEvent var2 = (TargetedEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            Connection var3 = this.getSender();
            Connection var4 = var2.getSender();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Connection var5 = this.getReceiver();
               Connection var6 = var2.getReceiver();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof TargetedEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      Connection var3 = this.getSender();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Connection var4 = this.getReceiver();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
