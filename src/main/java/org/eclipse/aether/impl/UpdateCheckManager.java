package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.MetadataTransferException;

public interface UpdateCheckManager {
   void checkArtifact(RepositorySystemSession var1, UpdateCheck<Artifact, ArtifactTransferException> var2);

   void touchArtifact(RepositorySystemSession var1, UpdateCheck<Artifact, ArtifactTransferException> var2);

   void checkMetadata(RepositorySystemSession var1, UpdateCheck<Metadata, MetadataTransferException> var2);

   void touchMetadata(RepositorySystemSession var1, UpdateCheck<Metadata, MetadataTransferException> var2);
}
