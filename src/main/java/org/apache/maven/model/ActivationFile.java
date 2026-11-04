package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationFile implements Serializable, Cloneable, InputLocationTracker {
   private String missing;
   private String exists;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation missingLocation;
   private InputLocation existsLocation;

   public ActivationFile clone() {
      try {
         ActivationFile var1 = (ActivationFile)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getExists() {
      return this.exists;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "missing":
               return this.missingLocation;
            case "exists":
               return this.existsLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getMissing() {
      return this.missing;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "missing":
               this.missingLocation = var2;
               return;
            case "exists":
               this.existsLocation = var2;
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

   public void setExists(String var1) {
      this.exists = var1;
   }

   public void setMissing(String var1) {
      this.missing = var1;
   }
}
