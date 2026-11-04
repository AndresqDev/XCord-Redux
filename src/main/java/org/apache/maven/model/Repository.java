package org.apache.maven.model;

import java.io.Serializable;

public class Repository extends RepositoryBase implements Serializable, Cloneable {
   private RepositoryPolicy releases;
   private RepositoryPolicy snapshots;

   public Repository clone() {
      try {
         Repository var1 = (Repository)super.clone();
         if (this.releases != null) {
            var1.releases = this.releases.clone();
         }

         if (this.snapshots != null) {
            var1.snapshots = this.snapshots.clone();
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public RepositoryPolicy getReleases() {
      return this.releases;
   }

   public RepositoryPolicy getSnapshots() {
      return this.snapshots;
   }

   public void setReleases(RepositoryPolicy var1) {
      this.releases = var1;
   }

   public void setSnapshots(RepositoryPolicy var1) {
      this.snapshots = var1;
   }
}
