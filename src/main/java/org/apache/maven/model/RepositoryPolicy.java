package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepositoryPolicy implements Serializable, Cloneable, InputLocationTracker {
   private String enabled;
   private String updatePolicy;
   private String checksumPolicy;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation enabledLocation;
   private InputLocation updatePolicyLocation;
   private InputLocation checksumPolicyLocation;

   public RepositoryPolicy clone() {
      try {
         RepositoryPolicy var1 = (RepositoryPolicy)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getChecksumPolicy() {
      return this.checksumPolicy;
   }

   public String getEnabled() {
      return this.enabled;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "enabled":
               return this.enabledLocation;
            case "updatePolicy":
               return this.updatePolicyLocation;
            case "checksumPolicy":
               return this.checksumPolicyLocation;
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
            case "enabled":
               this.enabledLocation = var2;
               return;
            case "updatePolicy":
               this.updatePolicyLocation = var2;
               return;
            case "checksumPolicy":
               this.checksumPolicyLocation = var2;
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

   public String getUpdatePolicy() {
      return this.updatePolicy;
   }

   public void setChecksumPolicy(String var1) {
      this.checksumPolicy = var1;
   }

   public void setEnabled(String var1) {
      this.enabled = var1;
   }

   public void setUpdatePolicy(String var1) {
      this.updatePolicy = var1;
   }

   public boolean isEnabled() {
      return this.enabled != null ? Boolean.parseBoolean(this.enabled) : true;
   }

   public void setEnabled(boolean var1) {
      this.enabled = String.valueOf(var1);
   }
}
