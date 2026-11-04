package org.eclipse.aether.util.repository;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicyRequest;

public final class SimpleArtifactDescriptorPolicy implements ArtifactDescriptorPolicy {
   private final int policy;

   public SimpleArtifactDescriptorPolicy(boolean var1, boolean var2) {
      this((var1 ? 1 : 0) | (var2 ? 2 : 0));
   }

   public SimpleArtifactDescriptorPolicy(int var1) {
      this.policy = var1;
   }

   @Override
   public int getPolicy(RepositorySystemSession var1, ArtifactDescriptorPolicyRequest var2) {
      return this.policy;
   }
}
