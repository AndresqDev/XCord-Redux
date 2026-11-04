package org.eclipse.aether.util.graph.manager;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.Dependency;

public final class NoopDependencyManager implements DependencyManager {
   public static final DependencyManager INSTANCE = new NoopDependencyManager();

   @Override
   public DependencyManager deriveChildManager(DependencyCollectionContext var1) {
      return this;
   }

   @Override
   public DependencyManagement manageDependency(Dependency var1) {
      return null;
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
