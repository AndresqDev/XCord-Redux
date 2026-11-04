package org.eclipse.aether.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

public interface ArtifactResolver {
   ArtifactResult resolveArtifact(RepositorySystemSession var1, ArtifactRequest var2) throws ArtifactResolutionException;

   List<ArtifactResult> resolveArtifacts(RepositorySystemSession var1, Collection<? extends ArtifactRequest> var2) throws ArtifactResolutionException;
}
