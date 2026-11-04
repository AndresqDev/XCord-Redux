package net.md_5.bungee.api.event;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Event;

public class PermissionCheckEvent extends Event {
   private final CommandSender sender;
   private final String permission;
   private boolean hasPermission;

   public boolean hasPermission() {
      return this.hasPermission;
   }

   public CommandSender getSender() {
      return this.sender;
   }

   public String getPermission() {
      return this.permission;
   }

   public void setHasPermission(boolean var1) {
      this.hasPermission = var1;
   }

   public PermissionCheckEvent(CommandSender var1, String var2, boolean var3) {
      this.sender = var1;
      this.permission = var2;
      this.hasPermission = var3;
   }

   @Override
   public String toString() {
      return "PermissionCheckEvent(sender=" + this.getSender() + ", permission=" + this.getPermission() + ", hasPermission=" + this.hasPermission + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PermissionCheckEvent)) {
         return false;
      } else {
         PermissionCheckEvent var2 = (PermissionCheckEvent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.hasPermission != var2.hasPermission) {
            return false;
         } else {
            CommandSender var3 = this.getSender();
            CommandSender var4 = var2.getSender();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getPermission();
               String var6 = var2.getPermission();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PermissionCheckEvent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.hasPermission ? 79 : 97);
      CommandSender var3 = this.getSender();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getPermission();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
