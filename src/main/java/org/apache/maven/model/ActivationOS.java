package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationOS implements Serializable, Cloneable, InputLocationTracker {
   private String name;
   private String family;
   private String arch;
   private String version;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation nameLocation;
   private InputLocation familyLocation;
   private InputLocation archLocation;
   private InputLocation versionLocation;

   public ActivationOS clone() {
      try {
         ActivationOS var1 = (ActivationOS)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getArch() {
      return this.arch;
   }

   public String getFamily() {
      return this.family;
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
            case "family":
               return this.familyLocation;
            case "arch":
               return this.archLocation;
            case "version":
               return this.versionLocation;
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
            case "family":
               this.familyLocation = var2;
               return;
            case "arch":
               this.archLocation = var2;
               return;
            case "version":
               this.versionLocation = var2;
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

   public String getVersion() {
      return this.version;
   }

   public void setArch(String var1) {
      this.arch = var1;
   }

   public void setFamily(String var1) {
      this.family = var1;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }
}
