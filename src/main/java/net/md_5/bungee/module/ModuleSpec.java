package net.md_5.bungee.module;

import java.io.File;

public class ModuleSpec {
   private final String name;
   private final File file;
   private final ModuleSource provider;

   public ModuleSpec(String var1, File var2, ModuleSource var3) {
      this.name = var1;
      this.file = var2;
      this.provider = var3;
   }

   public String getName() {
      return this.name;
   }

   public File getFile() {
      return this.file;
   }

   public ModuleSource getProvider() {
      return this.provider;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ModuleSpec)) {
         return false;
      } else {
         ModuleSpec var2 = (ModuleSpec)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               File var5 = this.getFile();
               File var6 = var2.getFile();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  ModuleSource var7 = this.getProvider();
                  ModuleSource var8 = var2.getProvider();
                  return var7 == null ? var8 == null : var7.equals(var8);
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
      return var1 instanceof ModuleSpec;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      File var4 = this.getFile();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      ModuleSource var5 = this.getProvider();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   @Override
   public String toString() {
      return "ModuleSpec(name=" + this.getName() + ", file=" + this.getFile() + ", provider=" + this.getProvider() + ")";
   }
}
