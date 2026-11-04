package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.Artifact;

public class CyclicDependencyException extends ArtifactResolutionException {
   private Artifact artifact;

   public CyclicDependencyException(String var1, Artifact var2) {
      super(var1, var2);
      this.artifact = var2;
   }

   @Override
   public Artifact getArtifact() {
      return this.artifact;
   }
}
