package org.apache.maven.repository.internal;

import org.apache.maven.model.Repository;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

public class ArtifactDescriptorUtils {
   public static Artifact toPomArtifact(Artifact var0) {
      Object var1 = var0;
      if (var0.getClassifier().length() > 0 || !"pom".equals(var0.getExtension())) {
         var1 = new DefaultArtifact(var0.getGroupId(), var0.getArtifactId(), "pom", var0.getVersion());
      }

      return (Artifact)var1;
   }

   public static RemoteRepository toRemoteRepository(Repository var0) {
      RemoteRepository.Builder var1 = new RemoteRepository.Builder(var0.getId(), var0.getLayout(), var0.getUrl());
      var1.setSnapshotPolicy(toRepositoryPolicy(var0.getSnapshots()));
      var1.setReleasePolicy(toRepositoryPolicy(var0.getReleases()));
      return var1.build();
   }

   public static RepositoryPolicy toRepositoryPolicy(org.apache.maven.model.RepositoryPolicy var0) {
      boolean var1 = true;
      String var2 = "warn";
      String var3 = "daily";
      if (var0 != null) {
         var1 = var0.isEnabled();
         if (var0.getUpdatePolicy() != null) {
            var3 = var0.getUpdatePolicy();
         }

         if (var0.getChecksumPolicy() != null) {
            var2 = var0.getChecksumPolicy();
         }
      }

      return new RepositoryPolicy(var1, var3, var2);
   }
}
