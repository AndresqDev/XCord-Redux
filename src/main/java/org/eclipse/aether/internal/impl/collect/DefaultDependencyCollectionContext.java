package org.eclipse.aether.internal.impl.collect;

import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.graph.Dependency;

final class DefaultDependencyCollectionContext implements DependencyCollectionContext {
   private final RepositorySystemSession session;
   private Artifact artifact;
   private Dependency dependency;
   private List<Dependency> managedDependencies;

   DefaultDependencyCollectionContext(RepositorySystemSession var1, Artifact var2, Dependency var3, List<Dependency> var4) {
      this.session = var1;
      this.artifact = var3 != null ? var3.getArtifact() : var2;
      this.dependency = var3;
      this.managedDependencies = var4;
   }

   @Override
   public RepositorySystemSession getSession() {
      return this.session;
   }

   @Override
   public Artifact getArtifact() {
      return this.artifact;
   }

   @Override
   public Dependency getDependency() {
      return this.dependency;
   }

   @Override
   public List<Dependency> getManagedDependencies() {
      return this.managedDependencies;
   }

   public void set(Dependency var1, List<Dependency> var2) {
      this.artifact = var1.getArtifact();
      this.dependency = var1;
      this.managedDependencies = var2;
   }

   @Override
   public String toString() {
      return String.valueOf(this.getDependency());
   }
}
