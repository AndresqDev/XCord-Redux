package org.apache.maven.model.resolution;

public class UnresolvableModelException extends Exception {
   private final String groupId;
   private final String artifactId;
   private final String version;

   public UnresolvableModelException(String var1, String var2, String var3, String var4, Throwable var5) {
      super(var1, var5);
      this.groupId = var2 != null ? var2 : "";
      this.artifactId = var3 != null ? var3 : "";
      this.version = var4 != null ? var4 : "";
   }

   public UnresolvableModelException(String var1, String var2, String var3, String var4) {
      super(var1);
      this.groupId = var2 != null ? var2 : "";
      this.artifactId = var3 != null ? var3 : "";
      this.version = var4 != null ? var4 : "";
   }

   public UnresolvableModelException(Throwable var1, String var2, String var3, String var4) {
      super(var1);
      this.groupId = var2;
      this.artifactId = var3;
      this.version = var4;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getVersion() {
      return this.version;
   }
}
