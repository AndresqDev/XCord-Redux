package net.md_5.bungee.api.event;

import java.util.List;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class TabCompleteEvent extends TargetedEvent implements Cancellable {
   private boolean cancelled;
   private final String cursor;
   private final List<String> suggestions;

   public TabCompleteEvent(Connection var1, Connection var2, String var3, List<String> var4) {
      super(var1, var2);
      this.cursor = var3;
      this.suggestions = var4;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public String getCursor() {
      return this.cursor;
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
      return "TabCompleteEvent(super="
         + super.toString()
         + ", cancelled="
         + this.isCancelled()
         + ", cursor="
         + this.getCursor()
         + ", suggestions="
         + this.getSuggestions()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabCompleteEvent)) {
         return false;
      } else {
         TabCompleteEvent var2 = (TabCompleteEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            String var3 = this.getCursor();
            String var4 = var2.getCursor();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               List var5 = this.getSuggestions();
               List var6 = var2.getSuggestions();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof TabCompleteEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      String var3 = this.getCursor();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      List var4 = this.getSuggestions();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
