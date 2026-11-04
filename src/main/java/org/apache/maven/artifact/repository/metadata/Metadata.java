package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Metadata implements Serializable, Cloneable {
   private String modelVersion;
   private String groupId;
   private String artifactId;
   private Versioning versioning;
   private String version;
   @Deprecated
   private List<Plugin> plugins;
   private String modelEncoding = "UTF-8";

   public void addPlugin(Plugin var1) {
      this.getPlugins().add(var1);
   }

   public Metadata clone() {
      try {
         Metadata var1 = (Metadata)super.clone();
         if (this.versioning != null) {
            var1.versioning = this.versioning.clone();
         }

         if (this.plugins != null) {
            var1.plugins = new ArrayList<>();

            for (Plugin var3 : this.plugins) {
               var1.plugins.add(var3.clone());
            }
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getModelEncoding() {
      return this.modelEncoding;
   }

   public String getModelVersion() {
      return this.modelVersion;
   }

   public List<Plugin> getPlugins() {
      if (this.plugins == null) {
         this.plugins = new ArrayList<>();
      }

      return this.plugins;
   }

   public String getVersion() {
      return this.version;
   }

   public Versioning getVersioning() {
      return this.versioning;
   }

   public void removePlugin(Plugin var1) {
      this.getPlugins().remove(var1);
   }

   public void setArtifactId(String var1) {
      this.artifactId = var1;
   }

   public void setGroupId(String var1) {
      this.groupId = var1;
   }

   public void setModelEncoding(String var1) {
      this.modelEncoding = var1;
   }

   public void setModelVersion(String var1) {
      this.modelVersion = var1;
   }

   public void setPlugins(List<Plugin> var1) {
      this.plugins = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   public void setVersioning(Versioning var1) {
      this.versioning = var1;
   }

   private String getSnapshotVersionKey(SnapshotVersion var1) {
      return var1.getClassifier() + ":" + var1.getExtension();
   }

   public boolean merge(Metadata var1) {
      boolean var2 = false;

      for (Plugin var4 : var1.getPlugins()) {
         boolean var5 = false;

         for (Plugin var7 : this.getPlugins()) {
            if (var7.getPrefix().equals(var4.getPrefix())) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            Plugin var16 = new Plugin();
            var16.setArtifactId(var4.getArtifactId());
            var16.setPrefix(var4.getPrefix());
            var16.setName(var4.getName());
            this.addPlugin(var16);
            var2 = true;
         }
      }

      Versioning var12 = var1.getVersioning();
      if (var12 != null) {
         Versioning var13 = this.getVersioning();
         if (var13 == null) {
            var13 = new Versioning();
            this.setVersioning(var13);
            var2 = true;
         }

         for (String var17 : var12.getVersions()) {
            if (!var13.getVersions().contains(var17)) {
               var2 = true;
               var13.getVersions().add(var17);
            }
         }

         if ("null".equals(var12.getLastUpdated())) {
            var12.setLastUpdated(null);
         }

         if ("null".equals(var13.getLastUpdated())) {
            var13.setLastUpdated(null);
         }

         if (var12.getLastUpdated() == null || var12.getLastUpdated().length() == 0) {
            var12.setLastUpdated(var13.getLastUpdated());
         }

         if (var13.getLastUpdated() == null || var13.getLastUpdated().length() == 0 || var12.getLastUpdated().compareTo(var13.getLastUpdated()) >= 0) {
            var2 = true;
            var13.setLastUpdated(var12.getLastUpdated());
            if (var12.getRelease() != null) {
               var2 = true;
               var13.setRelease(var12.getRelease());
            }

            if (var12.getLatest() != null) {
               var2 = true;
               var13.setLatest(var12.getLatest());
            }

            Snapshot var15 = var13.getSnapshot();
            Snapshot var18 = var12.getSnapshot();
            if (var18 != null) {
               boolean var19 = false;
               if (var15 == null) {
                  var15 = new Snapshot();
                  var13.setSnapshot(var15);
                  var2 = true;
                  var19 = true;
               }

               if (var15.getTimestamp() == null ? var18.getTimestamp() != null : !var15.getTimestamp().equals(var18.getTimestamp())) {
                  var15.setTimestamp(var18.getTimestamp());
                  var2 = true;
                  var19 = true;
               }

               if (var15.getBuildNumber() != var18.getBuildNumber()) {
                  var15.setBuildNumber(var18.getBuildNumber());
                  var2 = true;
               }

               if (var15.isLocalCopy() != var18.isLocalCopy()) {
                  var15.setLocalCopy(var18.isLocalCopy());
                  var2 = true;
               }

               if (var19) {
                  LinkedHashMap var8 = new LinkedHashMap();
                  if (!var13.getSnapshotVersions().isEmpty()) {
                     for (SnapshotVersion var10 : var12.getSnapshotVersions()) {
                        String var11 = this.getSnapshotVersionKey(var10);
                        var8.put(var11, var10);
                     }

                     if (!var8.isEmpty()) {
                        for (SnapshotVersion var21 : var13.getSnapshotVersions()) {
                           String var22 = this.getSnapshotVersionKey(var21);
                           if (!var8.containsKey(var22)) {
                              var8.put(var22, var21);
                           }
                        }
                     }

                     var13.setSnapshotVersions(new ArrayList<>(var8.values()));
                  }

                  var2 = true;
               }
            }
         }
      }

      return var2;
   }
}
