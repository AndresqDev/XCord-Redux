package org.eclipse.aether.util.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;

public final class OrDependencyFilter implements DependencyFilter {
   private final Set<DependencyFilter> filters = new LinkedHashSet<>();

   public OrDependencyFilter(DependencyFilter... var1) {
      if (var1 != null) {
         Collections.addAll(this.filters, var1);
      }
   }

   public OrDependencyFilter(Collection<DependencyFilter> var1) {
      if (var1 != null) {
         this.filters.addAll(var1);
      }
   }

   public static DependencyFilter newInstance(DependencyFilter var0, DependencyFilter var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (DependencyFilter)(var1 == null ? var0 : new OrDependencyFilter(var0, var1));
      }
   }

   @Override
   public boolean accept(DependencyNode var1, List<DependencyNode> var2) {
      for (DependencyFilter var4 : this.filters) {
         if (var4.accept(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         OrDependencyFilter var2 = (OrDependencyFilter)var1;
         return this.filters.equals(var2.filters);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.getClass().hashCode();
      return var1 * 31 + this.filters.hashCode();
   }
}
