package org.apache.maven.repository.legacy.metadata;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException;

public interface ArtifactMetadata {
   boolean storedInArtifactVersionDirectory();

   boolean storedInGroupDirectory();

   String getGroupId();

   String getArtifactId();

   String getBaseVersion();

   Object getKey();

   String getLocalFilename(ArtifactRepository var1);

   String getRemoteFilename();

   void merge(ArtifactMetadata var1);

   void storeInLocalRepository(ArtifactRepository var1, ArtifactRepository var2) throws RepositoryMetadataStoreException;

   String extendedToString();
}
