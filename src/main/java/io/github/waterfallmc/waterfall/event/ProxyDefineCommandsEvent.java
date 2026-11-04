package io.github.waterfallmc.waterfall.event;

import java.util.Map;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Command;

public class ProxyDefineCommandsEvent extends TargetedEvent {
   private final Map<String, Command> commands;

   public ProxyDefineCommandsEvent(Connection var1, Connection var2, Map<String, Command> var3) {
      super(var1, var2);
      this.commands = var3;
   }

   public Map<String, Command> getCommands() {
      return this.commands;
   }

   public String toString() {
      return "ProxyDefineCommandsEvent(super=" + super.toString() + ", commands=" + this.getCommands() + ")";
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ProxyDefineCommandsEvent)) {
         return false;
      } else {
         ProxyDefineCommandsEvent var2 = (ProxyDefineCommandsEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            Map var3 = this.getCommands();
            Map var4 = var2.getCommands();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ProxyDefineCommandsEvent;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      Map var3 = this.getCommands();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
