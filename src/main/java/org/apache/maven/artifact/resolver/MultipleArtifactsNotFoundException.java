package org.apache.maven.artifact.resolver;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

public class MultipleArtifactsNotFoundException extends ArtifactResolutionException {
   private final List<Artifact> resolvedArtifacts;
   private final List<Artifact> missingArtifacts;

   @Deprecated
   public MultipleArtifactsNotFoundException(Artifact var1, List<Artifact> var2, List<ArtifactRepository> var3) {
      this(var1, new ArrayList<>(), var2, var3);
   }

   public MultipleArtifactsNotFoundException(Artifact var1, List<Artifact> var2, List<Artifact> var3, List<ArtifactRepository> var4) {
      super(constructMessage(var3), var1, var4);
      this.resolvedArtifacts = var2;
      this.missingArtifacts = var3;
   }

   public List<Artifact> getResolvedArtifacts() {
      return this.resolvedArtifacts;
   }

   public List<Artifact> getMissingArtifacts() {
      return this.missingArtifacts;
   }

   private static String constructMessage(List<Artifact> var0) {
      StringBuilder var1 = new StringBuilder(256);
      var1.append("Missing:\n");
      var1.append("----------\n");
      int var2 = 0;

      for (Artifact var4 : var0) {
         String var5 = ++var2 + ") " + var4.getId();
         var1.append(
            constructMissingArtifactMessage(
               var5,
               "  ",
               var4.getGroupId(),
               var4.getArtifactId(),
               var4.getVersion(),
               var4.getType(),
               var4.getClassifier(),
               var4.getDownloadUrl(),
               var4.getDependencyTrail()
            )
         );
      }

      var1.append("----------\n");
      int var6 = var0.size();
      var1.append(var6).append(" required artifact");
      if (var6 > 1) {
         var1.append("s are");
      } else {
         var1.append(" is");
      }

      var1.append(" missing.\n\nfor artifact: ");
      return var1.toString();
   }
}
