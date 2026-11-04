package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DistributionManagement implements Serializable, Cloneable, InputLocationTracker {
   private DeploymentRepository repository;
   private DeploymentRepository snapshotRepository;
   private Site site;
   private String downloadUrl;
   private Relocation relocation;
   private String status;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation repositoryLocation;
   private InputLocation snapshotRepositoryLocation;
   private InputLocation siteLocation;
   private InputLocation downloadUrlLocation;
   private InputLocation relocationLocation;
   private InputLocation statusLocation;

   public DistributionManagement clone() {
      try {
         DistributionManagement var1 = (DistributionManagement)super.clone();
         if (this.repository != null) {
            var1.repository = this.repository.clone();
         }

         if (this.snapshotRepository != null) {
            var1.snapshotRepository = this.snapshotRepository.clone();
         }

         if (this.site != null) {
            var1.site = this.site.clone();
         }

         if (this.relocation != null) {
            var1.relocation = this.relocation.clone();
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public String getDownloadUrl() {
      return this.downloadUrl;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "repository":
               return this.repositoryLocation;
            case "snapshotRepository":
               return this.snapshotRepositoryLocation;
            case "site":
               return this.siteLocation;
            case "downloadUrl":
               return this.downloadUrlLocation;
            case "relocation":
               return this.relocationLocation;
            case "status":
               return this.statusLocation;
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
            case "repository":
               this.repositoryLocation = var2;
               return;
            case "snapshotRepository":
               this.snapshotRepositoryLocation = var2;
               return;
            case "site":
               this.siteLocation = var2;
               return;
            case "downloadUrl":
               this.downloadUrlLocation = var2;
               return;
            case "relocation":
               this.relocationLocation = var2;
               return;
            case "status":
               this.statusLocation = var2;
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

   public Relocation getRelocation() {
      return this.relocation;
   }

   public DeploymentRepository getRepository() {
      return this.repository;
   }

   public Site getSite() {
      return this.site;
   }

   public DeploymentRepository getSnapshotRepository() {
      return this.snapshotRepository;
   }

   public String getStatus() {
      return this.status;
   }

   public void setDownloadUrl(String var1) {
      this.downloadUrl = var1;
   }

   public void setRelocation(Relocation var1) {
      this.relocation = var1;
   }

   public void setRepository(DeploymentRepository var1) {
      this.repository = var1;
   }

   public void setSite(Site var1) {
      this.site = var1;
   }

   public void setSnapshotRepository(DeploymentRepository var1) {
      this.snapshotRepository = var1;
   }

   public void setStatus(String var1) {
      this.status = var1;
   }
}
