package org.apache.maven.artifact.resolver;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public class ArtifactResolutionException extends AbstractArtifactResolutionException {
   public ArtifactResolutionException(
      String var1, String var2, String var3, String var4, String var5, String var6, List<ArtifactRepository> var7, List<String> var8, Throwable var9
   ) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public ArtifactResolutionException(String var1, String var2, String var3, String var4, String var5, String var6, Throwable var7) {
      super(var1, var2, var3, var4, var5, var6, null, null, var7);
   }

   public ArtifactResolutionException(String var1, Artifact var2) {
      super(var1, var2);
   }

   public ArtifactResolutionException(String var1, Artifact var2, List<ArtifactRepository> var3) {
      super(var1, var2, var3);
   }

   public ArtifactResolutionException(String var1, Artifact var2, Throwable var3) {
      super(var1, var2, null, var3);
   }

   public ArtifactResolutionException(String var1, Artifact var2, List<ArtifactRepository> var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
