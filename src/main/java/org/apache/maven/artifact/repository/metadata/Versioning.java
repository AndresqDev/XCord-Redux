package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Versioning implements Serializable, Cloneable {
   private String latest;
   private String release;
   private List<String> versions;
   private String lastUpdated;
   private Snapshot snapshot;
   private List<SnapshotVersion> snapshotVersions;

   public void addSnapshotVersion(SnapshotVersion var1) {
      this.getSnapshotVersions().add(var1);
   }

   public void addVersion(String var1) {
      this.getVersions().add(var1);
   }

   public Versioning clone() {
      try {
         Versioning var1 = (Versioning)super.clone();
         if (this.versions != null) {
            var1.versions = new ArrayList<>();
            var1.versions.addAll(this.versions);
         }

         if (this.snapshot != null) {
            var1.snapshot = this.snapshot.clone();
         }

         if (this.snapshotVersions != null) {
            var1.snapshotVersions = new ArrayList<>();

            for (SnapshotVersion var3 : this.snapshotVersions) {
               var1.snapshotVersions.add(var3.clone());
            }
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   public String getLastUpdated() {
      return this.lastUpdated;
   }

   public String getLatest() {
      return this.latest;
   }

   public String getRelease() {
      return this.release;
   }

   public Snapshot getSnapshot() {
      return this.snapshot;
   }

   public List<SnapshotVersion> getSnapshotVersions() {
      if (this.snapshotVersions == null) {
         this.snapshotVersions = new ArrayList<>();
      }

      return this.snapshotVersions;
   }

   public List<String> getVersions() {
      if (this.versions == null) {
         this.versions = new ArrayList<>();
      }

      return this.versions;
   }

   public void removeSnapshotVersion(SnapshotVersion var1) {
      this.getSnapshotVersions().remove(var1);
   }

   public void removeVersion(String var1) {
      this.getVersions().remove(var1);
   }

   public void setLastUpdated(String var1) {
      this.lastUpdated = var1;
   }

   public void setLatest(String var1) {
      this.latest = var1;
   }

   public void setRelease(String var1) {
      this.release = var1;
   }

   public void setSnapshot(Snapshot var1) {
      this.snapshot = var1;
   }

   public void setSnapshotVersions(List<SnapshotVersion> var1) {
      this.snapshotVersions = var1;
   }

   public void setVersions(List<String> var1) {
      this.versions = var1;
   }

   public void updateTimestamp() {
      this.setLastUpdatedTimestamp(new Date());
   }

   public void setLastUpdatedTimestamp(Date var1) {
      TimeZone var2 = TimeZone.getTimeZone("UTC");
      SimpleDateFormat var3 = new SimpleDateFormat("yyyyMMddHHmmss");
      var3.setTimeZone(var2);
      this.setLastUpdated(var3.format(var1));
   }
}
