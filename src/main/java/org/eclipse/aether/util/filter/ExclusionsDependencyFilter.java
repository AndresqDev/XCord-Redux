package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class ExclusionsDependencyFilter implements DependencyFilter {
   private final Set<String> excludes = new HashSet<>();

   public ExclusionsDependencyFilter(Collection<String> var1) {
      if (var1 != null) {
         this.excludes.addAll(var1);
      }
   }

   @Override
   public boolean accept(DependencyNode var1, List<DependencyNode> var2) {
      Dependency var3 = var1.getDependency();
      if (var3 == null) {
         return true;
      } else {
         String var4 = var3.getArtifact().getArtifactId();
         if (this.excludes.contains(var4)) {
            return false;
         } else {
            var4 = var3.getArtifact().getGroupId() + ':' + var4;
            return !this.excludes.contains(var4);
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         ExclusionsDependencyFilter var2 = (ExclusionsDependencyFilter)var1;
         return this.excludes.equals(var2.excludes);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 17;
      return var1 * 31 + this.excludes.hashCode();
   }
}
