package net.md_5.bungee.api.plugin;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import net.md_5.bungee.api.CommandSender;

public abstract class Command {
   private final String name;
   private final String permission;
   private final String[] aliases;
   private String permissionMessage;

   public Command(String var1) {
      this(var1, null);
   }

   public Command(String var1, String var2, String... var3) {
      Preconditions.checkArgument(var1 != null, "name");
      this.name = var1;
      this.permission = var2;
      this.aliases = var3;
      this.permissionMessage = null;
   }

   public abstract void execute(CommandSender var1, String[] var2);

   public boolean hasPermission(CommandSender var1) {
      return this.permission == null || this.permission.isEmpty() || var1.hasPermission(this.permission);
   }

   public String getName() {
      return this.name;
   }

   public String getPermission() {
      return this.permission;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public String getPermissionMessage() {
      return this.permissionMessage;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Command)) {
         return false;
      } else {
         Command var2 = (Command)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getPermission();
               String var6 = var2.getPermission();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  if (!Arrays.deepEquals(this.getAliases(), var2.getAliases())) {
                     return false;
                  } else {
                     String var7 = this.getPermissionMessage();
                     String var8 = var2.getPermissionMessage();
                     return var7 == null ? var8 == null : var7.equals(var8);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Command;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getPermission();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      var2 = var2 * 59 + Arrays.deepHashCode(this.getAliases());
      String var5 = this.getPermissionMessage();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   @Override
   public String toString() {
      return "Command(name="
         + this.getName()
         + ", permission="
         + this.getPermission()
         + ", aliases="
         + Arrays.deepToString(this.getAliases())
         + ", permissionMessage="
         + this.getPermissionMessage()
         + ")";
   }

   protected void setPermissionMessage(String var1) {
      this.permissionMessage = var1;
   }
}
