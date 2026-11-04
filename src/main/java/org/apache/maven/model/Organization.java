package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Organization implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String url;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation nameLocation;
   private InputLocation urlLocation;

   public Organization clone() {
      try {
         Organization var1 = (Organization)super.clone();
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
            case "name":
               return this.nameLocation;
            case "url":
               return this.urlLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getName() {
      return this.name;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "name":
               this.nameLocation = var2;
               return;
            case "url":
               this.urlLocation = var2;
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

   public String getUrl() {
      return this.url;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setUrl(String var1) {
      this.url = var1;
   }
}
