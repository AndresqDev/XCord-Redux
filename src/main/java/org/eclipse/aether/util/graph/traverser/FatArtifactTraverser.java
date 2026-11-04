package org.eclipse.aether.util.graph.traverser;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;

public final class FatArtifactTraverser implements DependencyTraverser {
   @Override
   public boolean traverseDependency(Dependency var1) {
      String var2 = var1.getArtifact().getProperty("includesDependencies", "");
      return !Boolean.parseBoolean(var2);
   }

   @Override
   public DependencyTraverser deriveChildTraverser(DependencyCollectionContext var1) {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 ? true : null != var1 && this.getClass().equals(var1.getClass());
   }

   @Override
   public int hashCode() {
      return this.getClass().hashCode();
   }
}
