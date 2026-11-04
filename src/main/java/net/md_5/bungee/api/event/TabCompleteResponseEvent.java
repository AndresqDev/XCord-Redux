package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteResponseEvent extends TargetedEvent implements Cancellable {
   private boolean cancelled;
   private final List<String> suggestions;

   public TabCompleteResponseEvent(Connection var1, Connection var2, List<String> var3) {
      super(var1, var2);
      this.suggestions = var3;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public List<String> getSuggestions() {
      return this.suggestions;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public String toString() {
      return "TabCompleteResponseEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", suggestions=" + this.getSuggestions() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabCompleteResponseEvent)) {
         return false;
      } else {
         TabCompleteResponseEvent var2 = (TabCompleteResponseEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            List var3 = this.getSuggestions();
            List var4 = var2.getSuggestions();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof TabCompleteResponseEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      List var3 = this.getSuggestions();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
