package org.apache.maven.repository.internal;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

public final class RelocatedArtifact extends AbstractArtifact {
   private final Artifact artifact;
   private final String groupId;
   private final String artifactId;
   private final String version;
   private final String message;

   RelocatedArtifact(Artifact var1, String var2, String var3, String var4, String var5) {
      this.artifact = Objects.requireNonNull(var1, "artifact cannot be null");
      this.groupId = var2 != null && var2.length() > 0 ? var2 : null;
      this.artifactId = var3 != null && var3.length() > 0 ? var3 : null;
      this.version = var4 != null && var4.length() > 0 ? var4 : null;
      this.message = var5 != null && var5.length() > 0 ? var5 : null;
   }

   @Override
   public String getGroupId() {
      return this.groupId != null ? this.groupId : this.artifact.getGroupId();
   }

   @Override
   public String getArtifactId() {
      return this.artifactId != null ? this.artifactId : this.artifact.getArtifactId();
   }

   @Override
   public String getVersion() {
      return this.version != null ? this.version : this.artifact.getVersion();
   }

   @Override
   public Artifact setVersion(String var1) {
      String var2 = this.getVersion();
      return !var2.equals(var1) && (var1 != null || var2.length() > 0)
         ? new RelocatedArtifact(this.artifact, this.groupId, this.artifactId, var1, this.message)
         : this;
   }

   @Override
   public Artifact setFile(File var1) {
      File var2 = this.getFile();
      return Objects.equals(var2, var1) ? this : new RelocatedArtifact(this.artifact.setFile(var1), this.groupId, this.artifactId, this.version, this.message);
   }

   @Override
   public Artifact setProperties(Map<String, String> var1) {
      Map var2 = this.getProperties();
      return !var2.equals(var1) && (var1 != null || !var2.isEmpty())
         ? new RelocatedArtifact(this.artifact.setProperties(var1), this.groupId, this.artifactId, this.version, this.message)
         : this;
   }

   @Override
   public String getClassifier() {
      return this.artifact.getClassifier();
   }

   @Override
   public String getExtension() {
      return this.artifact.getExtension();
   }

   @Override
   public File getFile() {
      return this.artifact.getFile();
   }

   @Override
   public String getProperty(String var1, String var2) {
      return this.artifact.getProperty(var1, var2);
   }

   @Override
   public Map<String, String> getProperties() {
      return this.artifact.getProperties();
   }

   public String getMessage() {
      return this.message;
   }
}
