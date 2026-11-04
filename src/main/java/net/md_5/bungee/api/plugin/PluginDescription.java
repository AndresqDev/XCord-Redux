package net.md_5.bungee.api.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PluginDescription {
   private String name;
   private String main;
   private String version;
   private String author;
   private Set<String> depends = new HashSet<>();
   private Set<String> softDepends = new HashSet<>();
   private File file = null;
   private String description = null;
   private List<String> libraries = new LinkedList<>();

   public String getName() {
      return this.name;
   }

   public String getMain() {
      return this.main;
   }

   public String getVersion() {
      return this.version;
   }

   public String getAuthor() {
      return this.author;
   }

   public Set<String> getDepends() {
      return this.depends;
   }

   public Set<String> getSoftDepends() {
      return this.softDepends;
   }

   public File getFile() {
      return this.file;
   }

   public String getDescription() {
      return this.description;
   }

   public List<String> getLibraries() {
      return this.libraries;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setMain(String var1) {
      this.main = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public void setAuthor(String var1) {
      this.author = var1;
   }

   public void setDepends(Set<String> var1) {
      this.depends = var1;
   }

   public void setSoftDepends(Set<String> var1) {
      this.softDepends = var1;
   }

   public void setFile(File var1) {
      this.file = var1;
   }

   public void setDescription(String var1) {
      this.description = var1;
   }

   public void setLibraries(List<String> var1) {
      this.libraries = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PluginDescription)) {
         return false;
      } else {
         PluginDescription var2 = (PluginDescription)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getMain();
               String var6 = var2.getMain();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getVersion();
                  String var8 = var2.getVersion();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     String var9 = this.getAuthor();
                     String var10 = var2.getAuthor();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        Set var11 = this.getDepends();
                        Set var12 = var2.getDepends();
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           Set var13 = this.getSoftDepends();
                           Set var14 = var2.getSoftDepends();
                           if (var13 == null ? var14 == null : var13.equals(var14)) {
                              File var15 = this.getFile();
                              File var16 = var2.getFile();
                              if (var15 == null ? var16 == null : var15.equals(var16)) {
                                 String var17 = this.getDescription();
                                 String var18 = var2.getDescription();
                                 if (var17 == null ? var18 == null : var17.equals(var18)) {
                                    List var19 = this.getLibraries();
                                    List var20 = var2.getLibraries();
                                    return var19 == null ? var20 == null : var19.equals(var20);
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
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
      return var1 instanceof PluginDescription;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getMain();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getVersion();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getAuthor();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Set var7 = this.getDepends();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      Set var8 = this.getSoftDepends();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      File var9 = this.getFile();
      var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
      String var10 = this.getDescription();
      var2 = var2 * 59 + (var10 == null ? 43 : var10.hashCode());
      List var11 = this.getLibraries();
      return var2 * 59 + (var11 == null ? 43 : var11.hashCode());
   }

   @Override
   public String toString() {
      return "PluginDescription(name="
         + this.getName()
         + ", main="
         + this.getMain()
         + ", version="
         + this.getVersion()
         + ", author="
         + this.getAuthor()
         + ", depends="
         + this.getDepends()
         + ", softDepends="
         + this.getSoftDepends()
         + ", file="
         + this.getFile()
         + ", description="
         + this.getDescription()
         + ", libraries="
         + this.getLibraries()
         + ")";
   }

   public PluginDescription() {
   }

   public PluginDescription(String var1, String var2, String var3, String var4, Set<String> var5, Set<String> var6, File var7, String var8, List<String> var9) {
      this.name = var1;
      this.main = var2;
      this.version = var3;
      this.author = var4;
      this.depends = var5;
      this.softDepends = var6;
      this.file = var7;
      this.description = var8;
      this.libraries = var9;
   }
}
