package org.eclipse.aether.util.repository;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicyRequest;

public final class SimpleResolutionErrorPolicy implements ResolutionErrorPolicy {
   private final int artifactPolicy;
   private final int metadataPolicy;

   public SimpleResolutionErrorPolicy(boolean var1, boolean var2) {
      this((var1 ? 1 : 0) | (var2 ? 2 : 0));
   }

   public SimpleResolutionErrorPolicy(int var1) {
      this(var1, var1);
   }

   public SimpleResolutionErrorPolicy(int var1, int var2) {
      this.artifactPolicy = var1;
      this.metadataPolicy = var2;
   }

   @Override
   public int getArtifactPolicy(RepositorySystemSession var1, ResolutionErrorPolicyRequest<Artifact> var2) {
      return this.artifactPolicy;
   }

   @Override
   public int getMetadataPolicy(RepositorySystemSession var1, ResolutionErrorPolicyRequest<Metadata> var2) {
      return this.metadataPolicy;
   }
}
