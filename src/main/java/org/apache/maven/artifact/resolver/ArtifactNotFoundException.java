package org.apache.maven.artifact.resolver;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public class ArtifactNotFoundException extends AbstractArtifactResolutionException {
   private String downloadUrl;

   protected ArtifactNotFoundException(String var1, Artifact var2, List<ArtifactRepository> var3) {
      super(var1, var2, var3);
   }

   public ArtifactNotFoundException(String var1, Artifact var2) {
      this(
         var1,
         var2.getGroupId(),
         var2.getArtifactId(),
         var2.getVersion(),
         var2.getType(),
         var2.getClassifier(),
         null,
         var2.getDownloadUrl(),
         var2.getDependencyTrail()
      );
   }

   protected ArtifactNotFoundException(String var1, Artifact var2, List<ArtifactRepository> var3, Throwable var4) {
      this(
         var1,
         var2.getGroupId(),
         var2.getArtifactId(),
         var2.getVersion(),
         var2.getType(),
         var2.getClassifier(),
         var3,
         var2.getDownloadUrl(),
         var2.getDependencyTrail(),
         var4
      );
   }

   public ArtifactNotFoundException(
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      List<ArtifactRepository> var7,
      String var8,
      List<String> var9,
      Throwable var10
   ) {
      super(constructMissingArtifactMessage(var1, "", var2, var3, var4, var5, var6, var8, var9), var2, var3, var4, var5, var6, var7, null, var10);
      this.downloadUrl = var8;
   }

   private ArtifactNotFoundException(
      String var1, String var2, String var3, String var4, String var5, String var6, List<ArtifactRepository> var7, String var8, List<String> var9
   ) {
      super(constructMissingArtifactMessage(var1, "", var2, var3, var4, var5, var6, var8, var9), var2, var3, var4, var5, var6, var7, null);
      this.downloadUrl = var8;
   }

   public String getDownloadUrl() {
      return this.downloadUrl;
   }
}
