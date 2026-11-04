package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatternSet implements Serializable, Cloneable, InputLocationTracker {
   private List<String> includes;
   private List<String> excludes;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation includesLocation;
   private InputLocation excludesLocation;

   public void addExclude(String var1) {
      this.getExcludes().add(var1);
   }

   public void addInclude(String var1) {
      this.getIncludes().add(var1);
   }

   public PatternSet clone() {
      try {
         PatternSet var1 = (PatternSet)super.clone();
         if (this.includes != null) {
            var1.includes = new ArrayList<>();
            var1.includes.addAll(this.includes);
         }

         if (this.excludes != null) {
            var1.excludes = new ArrayList<>();
            var1.excludes.addAll(this.excludes);
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public List<String> getExcludes() {
      if (this.excludes == null) {
         this.excludes = new ArrayList<>();
      }

      return this.excludes;
   }

   public List<String> getIncludes() {
      if (this.includes == null) {
         this.includes = new ArrayList<>();
      }

      return this.includes;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "includes":
               return this.includesLocation;
            case "excludes":
               return this.excludesLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "includes":
               this.includesLocation = var2;
               return;
            case "excludes":
               this.excludesLocation = var2;
               return;
            default:
               this.setOtherLocation(var1, var2);
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public void removeExclude(String var1) {
      this.getExcludes().remove(var1);
   }

   public void removeInclude(String var1) {
      this.getIncludes().remove(var1);
   }

   public void setExcludes(List<String> var1) {
      this.excludes = var1;
   }

   public void setIncludes(List<String> var1) {
      this.includes = var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append("PatternSet [includes: {");

      for (String var3 : this.getIncludes()) {
         var1.append(var3).append(", ");
      }

      if (var1.substring(var1.length() - 2).equals(", ")) {
         var1.delete(var1.length() - 2, var1.length());
      }

      var1.append("}, excludes: {");

      for (String var5 : this.getExcludes()) {
         var1.append(var5).append(", ");
      }

      if (var1.substring(var1.length() - 2).equals(", ")) {
         var1.delete(var1.length() - 2, var1.length());
      }

      var1.append("}]");
      return var1.toString();
   }
}
