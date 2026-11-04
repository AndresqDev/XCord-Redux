package org.apache.maven.artifact.repository.layout;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface ArtifactRepositoryLayout {
   String ROLE = ArtifactRepositoryLayout.class.getName();

   String getId();

   String pathOf(Artifact var1);

   String pathOfLocalRepositoryMetadata(ArtifactMetadata var1, ArtifactRepository var2);

   String pathOfRemoteRepositoryMetadata(ArtifactMetadata var1);
}
