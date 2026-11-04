package net.md_5.bungee.api.event;

import java.util.Arrays;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.plugin.Cancellable;

public class PluginMessageEvent extends TargetedEvent implements Cancellable {
   private boolean cancelled;
   private final String tag;
   private final byte[] data;

   public PluginMessageEvent(Connection var1, Connection var2, String var3, byte[] var4) {
      super(var1, var2);
      this.tag = var3;
      this.data = var4;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public String getTag() {
      return this.tag;
   }

   public byte[] getData() {
      return this.data;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public String toString() {
      return "PluginMessageEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", tag=" + this.getTag() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PluginMessageEvent)) {
         return false;
      } else {
         PluginMessageEvent var2 = (PluginMessageEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else if (this.isCancelled() != var2.isCancelled()) {
            return false;
         } else {
            String var3 = this.getTag();
            String var4 = var2.getTag();
            return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.equals(this.getData(), var2.getData()) : false;
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof PluginMessageEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      var2 = var2 * 59 + (this.isCancelled() ? 79 : 97);
      String var3 = this.getTag();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      return var2 * 59 + Arrays.hashCode(this.getData());
   }
}
