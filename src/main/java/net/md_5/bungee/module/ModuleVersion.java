package net.md_5.bungee.module;

public class ModuleVersion {
   private final String build;
   private final String git;

   public static ModuleVersion parse(String var0) {
      int var1 = var0.lastIndexOf(58);
      int var2 = var0.lastIndexOf(58, var1 - 1);
      if (var1 != -1 && var2 != -1) {
         String var3 = var0.substring(var1 + 1, var0.length());
         String var4 = var0.substring(var2 + 1, var1).replaceAll("\"", "");
         return !"unknown".equals(var3) && !"unknown".equals(var4) ? new ModuleVersion(var3, var4) : null;
      } else {
         return null;
      }
   }

   public String getBuild() {
      return this.build;
   }

   public String getGit() {
      return this.git;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ModuleVersion)) {
         return false;
      } else {
         ModuleVersion var2 = (ModuleVersion)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getBuild();
            String var4 = var2.getBuild();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getGit();
               String var6 = var2.getGit();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ModuleVersion;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getBuild();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getGit();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   @Override
   public String toString() {
      return "ModuleVersion(build=" + this.getBuild() + ", git=" + this.getGit() + ")";
   }

   private ModuleVersion(String var1, String var2) {
      this.build = var1;
      this.git = var2;
   }
}
