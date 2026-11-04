package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class ScopeDependencyFilter implements DependencyFilter {
   private final Set<String> included = new HashSet<>();
   private final Set<String> excluded = new HashSet<>();

   public ScopeDependencyFilter(Collection<String> var1, Collection<String> var2) {
      if (var1 != null) {
         this.included.addAll(var1);
      }

      if (var2 != null) {
         this.excluded.addAll(var2);
      }
   }

   public ScopeDependencyFilter(String... var1) {
      if (var1 != null) {
         this.excluded.addAll(Arrays.asList(var1));
      }
   }

   @Override
   public boolean accept(DependencyNode var1, List<DependencyNode> var2) {
      Dependency var3 = var1.getDependency();
      if (var3 == null) {
         return true;
      } else {
         String var4 = var1.getDependency().getScope();
         return (this.included.isEmpty() || this.included.contains(var4)) && (this.excluded.isEmpty() || !this.excluded.contains(var4));
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         ScopeDependencyFilter var2 = (ScopeDependencyFilter)var1;
         return this.included.equals(var2.included) && this.excluded.equals(var2.excluded);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.included.hashCode();
      return var1 * 31 + this.excluded.hashCode();
   }
}
