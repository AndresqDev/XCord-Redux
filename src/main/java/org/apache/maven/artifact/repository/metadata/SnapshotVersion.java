package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class SnapshotVersion implements Serializable, Cloneable {
   private String classifier = "";
   private String extension;
   private String version;
   private String updated;

   public SnapshotVersion clone() {
      try {
         return (SnapshotVersion)super.clone();
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SnapshotVersion)) {
         return false;
      } else {
         SnapshotVersion var2 = (SnapshotVersion)var1;
         boolean var3 = true;
         var3 = var3 && (this.getClassifier() == null ? var2.getClassifier() == null : this.getClassifier().equals(var2.getClassifier()));
         var3 = var3 && (this.getExtension() == null ? var2.getExtension() == null : this.getExtension().equals(var2.getExtension()));
         var3 = var3 && (this.getVersion() == null ? var2.getVersion() == null : this.getVersion().equals(var2.getVersion()));
         return var3 && (this.getUpdated() == null ? var2.getUpdated() == null : this.getUpdated().equals(var2.getUpdated()));
      }
   }

   public String getClassifier() {
      return this.classifier;
   }

   public String getExtension() {
      return this.extension;
   }

   public String getUpdated() {
      return this.updated;
   }

   public String getVersion() {
      return this.version;
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = 37 * var1 + (this.classifier != null ? this.classifier.hashCode() : 0);
      var1 = 37 * var1 + (this.extension != null ? this.extension.hashCode() : 0);
      var1 = 37 * var1 + (this.version != null ? this.version.hashCode() : 0);
      return 37 * var1 + (this.updated != null ? this.updated.hashCode() : 0);
   }

   public void setClassifier(String var1) {
      this.classifier = var1;
   }

   public void setExtension(String var1) {
      this.extension = var1;
   }

   public void setUpdated(String var1) {
      this.updated = var1;
   }

   public void setVersion(String var1) {
      this.version = var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);
      var1.append("classifier = '");
      var1.append(this.getClassifier());
      var1.append("'");
      var1.append("\n");
      var1.append("extension = '");
      var1.append(this.getExtension());
      var1.append("'");
      var1.append("\n");
      var1.append("version = '");
      var1.append(this.getVersion());
      var1.append("'");
      var1.append("\n");
      var1.append("updated = '");
      var1.append(this.getUpdated());
      var1.append("'");
      return var1.toString();
   }
}
