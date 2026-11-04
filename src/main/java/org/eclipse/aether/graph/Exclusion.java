package org.eclipse.aether.graph;

public final class Exclusion {
   private final String groupId;
   private final String artifactId;
   private final String classifier;
   private final String extension;

   public Exclusion(String var1, String var2, String var3, String var4) {
      this.groupId = var1 != null ? var1 : "";
      this.artifactId = var2 != null ? var2 : "";
      this.classifier = var3 != null ? var3 : "";
      this.extension = var4 != null ? var4 : "";
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getClassifier() {
      return this.classifier;
   }

   public String getExtension() {
      return this.extension;
   }

   @Override
   public String toString() {
      return this.getGroupId() + ':' + this.getArtifactId() + ':' + this.getExtension() + (this.getClassifier().length() > 0 ? ':' + this.getClassifier() : "");
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         Exclusion var2 = (Exclusion)var1;
         return this.artifactId.equals(var2.artifactId)
            && this.groupId.equals(var2.groupId)
            && this.extension.equals(var2.extension)
            && this.classifier.equals(var2.classifier);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.artifactId.hashCode();
      var1 = var1 * 31 + this.groupId.hashCode();
      var1 = var1 * 31 + this.classifier.hashCode();
      return var1 * 31 + this.extension.hashCode();
   }
}
