package org.apache.maven.artifact.repository;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.Proxy;

public interface ArtifactRepository {
   String pathOf(Artifact var1);

   String pathOfRemoteRepositoryMetadata(ArtifactMetadata var1);

   String pathOfLocalRepositoryMetadata(ArtifactMetadata var1, ArtifactRepository var2);

   String getUrl();

   void setUrl(String var1);

   String getBasedir();

   String getProtocol();

   String getId();

   void setId(String var1);

   ArtifactRepositoryPolicy getSnapshots();

   void setSnapshotUpdatePolicy(ArtifactRepositoryPolicy var1);

   ArtifactRepositoryPolicy getReleases();

   void setReleaseUpdatePolicy(ArtifactRepositoryPolicy var1);

   ArtifactRepositoryLayout getLayout();

   void setLayout(ArtifactRepositoryLayout var1);

   String getKey();

   @Deprecated
   boolean isUniqueVersion();

   @Deprecated
   boolean isBlacklisted();

   @Deprecated
   void setBlacklisted(boolean var1);

   boolean isBlocked();

   void setBlocked(boolean var1);

   Artifact find(Artifact var1);

   List<String> findVersions(Artifact var1);

   boolean isProjectAware();

   void setAuthentication(Authentication var1);

   Authentication getAuthentication();

   void setProxy(Proxy var1);

   Proxy getProxy();

   List<ArtifactRepository> getMirroredRepositories();

   void setMirroredRepositories(List<ArtifactRepository> var1);
}
