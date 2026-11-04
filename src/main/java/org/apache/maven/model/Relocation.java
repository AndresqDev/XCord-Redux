package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Relocation implements Serializable, Cloneable, InputLocationTracker {
   private String groupId;
   private String artifactId;
   private String version;
   private String message;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation groupIdLocation;
   private InputLocation artifactIdLocation;
   private InputLocation versionLocation;
   private InputLocation messageLocation;

   public Relocation clone() {
      try {
         Relocation var1 = (Relocation)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "groupId":
               return this.groupIdLocation;
            case "artifactId":
               return this.artifactIdLocation;
            case "version":
               return this.versionLocation;
            case "message":
               return this.messageLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getMessage() {
      return this.message;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "groupId":
               this.groupIdLocation = var2;
               return;
            case "artifactId":
               this.artifactIdLocation = var2;
               return;
            case "version":
               this.versionLocation = var2;
               return;
            case "message":
               this.messageLocation = var2;
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

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }
}
