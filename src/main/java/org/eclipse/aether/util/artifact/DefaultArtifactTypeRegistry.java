package org.eclipse.aether.util.artifact;

import org.eclipse.aether.artifact.ArtifactType;

public final class DefaultArtifactTypeRegistry extends SimpleArtifactTypeRegistry {
   public DefaultArtifactTypeRegistry add(ArtifactType var1) {
      super.add(var1);
      return this;
   }
}
