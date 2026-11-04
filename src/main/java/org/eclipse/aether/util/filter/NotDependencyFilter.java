package org.eclipse.aether.util.filter;

import java.util.List;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class NotDependencyFilter implements DependencyFilter {
   private final DependencyFilter filter;

   public NotDependencyFilter(DependencyFilter var1) {
      this.filter = Objects.requireNonNull(var1, "dependency filter cannot be null");
   }

   @Override
   public boolean accept(DependencyNode var1, List<DependencyNode> var2) {
      return !this.filter.accept(var1, var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         NotDependencyFilter var2 = (NotDependencyFilter)var1;
         return this.filter.equals(var2.filter);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.getClass().hashCode();
      return var1 * 31 + this.filter.hashCode();
   }
}
