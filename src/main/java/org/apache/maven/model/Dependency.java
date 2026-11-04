package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dependency implements Serializable, Cloneable, InputLocationTracker {
   private String groupId;
   private String artifactId;
   private String version;
   private String type = "jar";
   private String classifier;
   private String scope;
   private String systemPath;
   private List<Exclusion> exclusions;
   private String optional;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation groupIdLocation;
   private InputLocation artifactIdLocation;
   private InputLocation versionLocation;
   private InputLocation typeLocation;
   private InputLocation classifierLocation;
   private InputLocation scopeLocation;
   private InputLocation systemPathLocation;
   private InputLocation exclusionsLocation;
   private InputLocation optionalLocation;
   private String managementKey;

   public void addExclusion(Exclusion var1) {
      this.getExclusions().add(var1);
   }

   public Dependency clone() {
      try {
         Dependency var1 = (Dependency)super.clone();
         if (this.exclusions != null) {
            var1.exclusions = new ArrayList<>();

            for (Exclusion var3 : this.exclusions) {
               var1.exclusions.add(var3.clone());
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

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getClassifier() {
      return this.classifier;
   }

   public List<Exclusion> getExclusions() {
      if (this.exclusions == null) {
         this.exclusions = new ArrayList<>();
      }

      return this.exclusions;
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
            case "type":
               return this.typeLocation;
            case "classifier":
               return this.classifierLocation;
            case "scope":
               return this.scopeLocation;
            case "systemPath":
               return this.systemPathLocation;
            case "exclusions":
               return this.exclusionsLocation;
            case "optional":
               return this.optionalLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public String getOptional() {
      return this.optional;
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
            case "type":
               this.typeLocation = var2;
               return;
            case "classifier":
               this.classifierLocation = var2;
               return;
            case "scope":
               this.scopeLocation = var2;
               return;
            case "systemPath":
               this.systemPathLocation = var2;
               return;
            case "exclusions":
               this.exclusionsLocation = var2;
               return;
            case "optional":
               this.optionalLocation = var2;
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

   public String getScope() {
      return this.scope;
   }

   public String getSystemPath() {
      return this.systemPath;
   }

   public String getType() {
      return this.type;
   }

   public String getVersion() {
      return this.version;
   }

   public void removeExclusion(Exclusion var1) {
      this.getExclusions().remove(var1);
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setClassifier(String var1) {
      this.classifier = var1;
   }

   public void setExclusions(List<Exclusion> var1) {
      this.exclusions = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setOptional(String var1) {
      this.optional = var1;
   }

   public void setScope(String var1) {
      this.scope = var1;
   }

   public void setSystemPath(String var1) {
      this.systemPath = var1;
   }

   public void setType(String var1) {
      this.type = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public boolean isOptional() {
      return this.optional != null ? Boolean.parseBoolean(this.optional) : false;
   }

   public void setOptional(boolean var1) {
      this.optional = String.valueOf(var1);
   }

   @Override
   public String toString() {
      return "Dependency {groupId=" + this.groupId + ", artifactId=" + this.artifactId + ", version=" + this.version + ", type=" + this.type + "}";
   }

   public String getManagementKey() {
      if (this.managementKey == null) {
         this.managementKey = this.groupId + ":" + this.artifactId + ":" + this.type + (this.classifier != null ? ":" + this.classifier : "");
      }

      return this.managementKey;
   }

   public void clearManagementKey() {
      this.managementKey = null;
   }
}
