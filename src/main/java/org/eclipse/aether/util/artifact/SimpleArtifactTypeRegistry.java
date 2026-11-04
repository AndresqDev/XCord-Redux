package org.eclipse.aether.util.artifact;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.aether.artifact.ArtifactType;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;

class SimpleArtifactTypeRegistry implements ArtifactTypeRegistry {
   private final Map<String, ArtifactType> types = new HashMap<>();

   public SimpleArtifactTypeRegistry add(ArtifactType var1) {
      this.types.put(var1.getId(), var1);
      return this;
   }

   @Override
   public ArtifactType get(String var1) {
      return this.types.get(var1);
   }

   @Override
   public String toString() {
      return this.types.toString();
   }
}
