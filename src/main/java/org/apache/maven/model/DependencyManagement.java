package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DependencyManagement implements Serializable, Cloneable, InputLocationTracker {
   private List<Dependency> dependencies;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation dependenciesLocation;

   public void addDependency(Dependency var1) {
      this.getDependencies().add(var1);
   }

   public DependencyManagement clone() {
      try {
         DependencyManagement var1 = (DependencyManagement)super.clone();
         if (this.dependencies != null) {
            var1.dependencies = new ArrayList<>();

            for (Dependency var3 : this.dependencies) {
               var1.dependencies.add(var3.clone());
            }
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public List<Dependency> getDependencies() {
      if (this.dependencies == null) {
         this.dependencies = new ArrayList<>();
      }

      return this.dependencies;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "dependencies":
               return this.dependenciesLocation;
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
            case "dependencies":
               this.dependenciesLocation = var2;
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

   public void removeDependency(Dependency var1) {
      this.getDependencies().remove(var1);
   }

   public void setDependencies(List<Dependency> var1) {
      this.dependencies = var1;
   }
}
