package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface ResolutionErrorPolicy {
   int CACHE_DISABLED = 0;
   int CACHE_NOT_FOUND = 1;
   int CACHE_TRANSFER_ERROR = 2;
   int CACHE_ALL = 3;

   int getArtifactPolicy(RepositorySystemSession var1, ResolutionErrorPolicyRequest<Artifact> var2);

   int getMetadataPolicy(RepositorySystemSession var1, ResolutionErrorPolicyRequest<Metadata> var2);
}
