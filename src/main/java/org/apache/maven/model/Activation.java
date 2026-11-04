package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Activation implements Serializable, Cloneable, InputLocationTracker {
   private boolean activeByDefault = false;
   private String jdk;
   private ActivationOS os;
   private ActivationProperty property;
   private ActivationFile file;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation activeByDefaultLocation;
   private InputLocation jdkLocation;
   private InputLocation osLocation;
   private InputLocation propertyLocation;
   private InputLocation fileLocation;

   public Activation clone() {
      try {
         Activation var1 = (Activation)super.clone();
         if (this.os != null) {
            var1.os = this.os.clone();
         }

         if (this.property != null) {
            var1.property = this.property.clone();
         }

         if (this.file != null) {
            var1.file = this.file.clone();
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public ActivationFile getFile() {
      return this.file;
   }

   public String getJdk() {
      return this.jdk;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "activeByDefault":
               return this.activeByDefaultLocation;
            case "jdk":
               return this.jdkLocation;
            case "os":
               return this.osLocation;
            case "property":
               return this.propertyLocation;
            case "file":
               return this.fileLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public ActivationOS getOs() {
      return this.os;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "activeByDefault":
               this.activeByDefaultLocation = var2;
               return;
            case "jdk":
               this.jdkLocation = var2;
               return;
            case "os":
               this.osLocation = var2;
               return;
            case "property":
               this.propertyLocation = var2;
               return;
            case "file":
               this.fileLocation = var2;
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

   public ActivationProperty getProperty() {
      return this.property;
   }

   public boolean isActiveByDefault() {
      return this.activeByDefault;
   }

   public void setActiveByDefault(boolean var1) {
      this.activeByDefault = var1;
   }

   public void setFile(ActivationFile var1) {
      this.file = var1;
   }

   public void setJdk(String var1) {
      this.jdk = var1;
   }

   public void setOs(ActivationOS var1) {
      this.os = var1;
   }

   public void setProperty(ActivationProperty var1) {
      this.property = var1;
   }
}
