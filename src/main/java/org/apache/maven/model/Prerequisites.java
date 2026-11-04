package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Prerequisites implements Serializable, Cloneable, InputLocationTracker {
   private String maven = "2.0";
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation mavenLocation;

   public Prerequisites clone() {
      try {
         Prerequisites var1 = (Prerequisites)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "maven":
               return this.mavenLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getMaven() {
      return this.maven;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "maven":
               this.mavenLocation = var2;
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

   public void setMaven(String var1) {
      this.maven = var1;
   }
}
