package org.eclipse.aether.resolution;

import org.eclipse.aether.artifact.Artifact;

public final class ArtifactDescriptorPolicyRequest {
   private Artifact artifact;
   private String context = "";

   public ArtifactDescriptorPolicyRequest() {
   }

   public ArtifactDescriptorPolicyRequest(Artifact var1, String var2) {
      this.setArtifact(var1);
      this.setRequestContext(var2);
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public ArtifactDescriptorPolicyRequest setArtifact(Artifact var1) {
      this.artifact = var1;
      return this;
   }

   public String getRequestContext() {
      return this.context;
   }

   public ArtifactDescriptorPolicyRequest setRequestContext(String var1) {
      this.context = var1 != null ? var1 : "";
      return this;
   }

   @Override
   public String toString() {
      return String.valueOf(this.getArtifact());
   }
}
