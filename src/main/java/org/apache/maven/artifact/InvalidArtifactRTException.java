package org.apache.maven.artifact;

public class InvalidArtifactRTException extends RuntimeException {
   private final String groupId;
   private final String artifactId;
   private final String version;
   private final String type;
   private final String baseMessage;

   public InvalidArtifactRTException(String var1, String var2, String var3, String var4, String var5) {
      this.groupId = var1;
      this.artifactId = var2;
      this.version = var3;
      this.type = var4;
      this.baseMessage = var5;
   }

   public InvalidArtifactRTException(String var1, String var2, String var3, String var4, String var5, Throwable var6) {
      super(var6);
      this.groupId = var1;
      this.artifactId = var2;
      this.version = var3;
      this.type = var4;
      this.baseMessage = var5;
   }

   @Override
   public String getMessage() {
      return "For artifact {" + this.getArtifactKey() + "}: " + this.getBaseMessage();
   }

   public String getBaseMessage() {
      return this.baseMessage;
   }

   public String getArtifactId() {
      return this.artifactId;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public String getType() {
      return this.type;
   }

   public String getVersion() {
      return this.version;
   }

   public String getArtifactKey() {
      return this.groupId + ":" + this.artifactId + ":" + this.version + ":" + this.type;
   }
}
