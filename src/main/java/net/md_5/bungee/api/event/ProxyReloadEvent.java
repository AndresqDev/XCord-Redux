package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class ProxyReloadEvent extends Event {
   private final CommandSender sender;

   public CommandSender getSender() {
      return this.sender;
   }

   public ProxyReloadEvent(CommandSender var1) {
      this.sender = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ProxyReloadEvent)) {
         return false;
      } else {
         ProxyReloadEvent var2 = (ProxyReloadEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            CommandSender var3 = this.getSender();
            CommandSender var4 = var2.getSender();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ProxyReloadEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      CommandSender var3 = this.getSender();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
