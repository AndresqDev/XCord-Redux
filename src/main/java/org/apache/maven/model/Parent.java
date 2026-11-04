package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parent implements Serializable, Cloneable, InputLocationTracker {
   private String groupId;
   private String artifactId;
   private String version;
   private String relativePath = "../pom.xml";
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation groupIdLocation;
   private InputLocation artifactIdLocation;
   private InputLocation versionLocation;
   private InputLocation relativePathLocation;

   public Parent clone() {
      try {
         Parent var1 = (Parent)super.clone();
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
            case "relativePath":
               return this.relativePathLocation;
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
            case "groupId":
               this.groupIdLocation = var2;
               return;
            case "artifactId":
               this.artifactIdLocation = var2;
               return;
            case "version":
               this.versionLocation = var2;
               return;
            case "relativePath":
               this.relativePathLocation = var2;
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

   public String getRelativePath() {
      return this.relativePath;
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

   public void setRelativePath(String var1) {
      this.relativePath = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public String getId() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append(this.getGroupId());
      var1.append(":");
      var1.append(this.getArtifactId());
      var1.append(":");
      var1.append("pom");
      var1.append(":");
      var1.append(this.getVersion());
      return var1.toString();
   }

   @Override
   public String toString() {
      return this.getId();
   }
}
