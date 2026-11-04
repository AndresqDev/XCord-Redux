package org.eclipse.aether.util.graph.selector;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class StaticDependencySelector implements DependencySelector {
   private final boolean select;

   public StaticDependencySelector(boolean var1) {
      this.select = var1;
   }

   @Override
   public boolean selectDependency(Dependency var1) {
      return this.select;
   }

   @Override
   public DependencySelector deriveChildSelector(DependencyCollectionContext var1) {
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (null != var1 && this.getClass().equals(var1.getClass())) {
         StaticDependencySelector var2 = (StaticDependencySelector)var1;
         return this.select == var2.select;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.getClass().hashCode();
      return var1 * 31 + (this.select ? 1 : 0);
   }

   @Override
   public String toString() {
      return String.format("%s(%s)", this.getClass().getSimpleName(), this.select ? "Select all" : "Exclude all");
   }
}
