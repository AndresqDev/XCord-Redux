package org.apache.maven.artifact.versioning;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;

public class OverConstrainedVersionException extends ArtifactResolutionException {
   public OverConstrainedVersionException(String var1, Artifact var2) {
      super(var1, var2);
   }

   public OverConstrainedVersionException(String var1, Artifact var2, List<ArtifactRepository> var3) {
      super(var1, var2, var3);
   }
}
