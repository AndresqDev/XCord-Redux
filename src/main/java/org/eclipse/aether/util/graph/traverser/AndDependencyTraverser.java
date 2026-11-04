package org.eclipse.aether.util.graph.traverser;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;

public final class AndDependencyTraverser implements DependencyTraverser {
   private final Set<? extends DependencyTraverser> traversers;
   private int hashCode;

   public AndDependencyTraverser(DependencyTraverser... var1) {
      if (var1 != null && var1.length > 0) {
         this.traversers = new LinkedHashSet<>(Arrays.asList(var1));
      } else {
         this.traversers = Collections.emptySet();
      }
   }

   public AndDependencyTraverser(Collection<? extends DependencyTraverser> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.traversers = new LinkedHashSet<>(var1);
      } else {
         this.traversers = Collections.emptySet();
      }
   }

   private AndDependencyTraverser(Set<DependencyTraverser> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.traversers = var1;
      } else {
         this.traversers = Collections.emptySet();
      }
   }

   public static DependencyTraverser newInstance(DependencyTraverser var0, DependencyTraverser var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (DependencyTraverser)(var1 != null && !var1.equals(var0) ? new AndDependencyTraverser(var0, var1) : var0);
      }
   }

   @Override
   public boolean traverseDependency(Dependency var1) {
      for (DependencyTraverser var3 : this.traversers) {
         if (!var3.traverseDependency(var1)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public DependencyTraverser deriveChildTraverser(DependencyCollectionContext var1) {
      int var2 = 0;
      LinkedHashSet var3 = null;

      for (DependencyTraverser var5 : this.traversers) {
         DependencyTraverser var6 = var5.deriveChildTraverser(var1);
         if (var3 != null) {
            if (var6 != null) {
               var3.add(var6);
            }
         } else if (var5 == var6) {
            var2++;
         } else {
            var3 = new LinkedHashSet();
            if (var2 > 0) {
               for (DependencyTraverser var8 : this.traversers) {
                  if (var3.size() >= var2) {
                     break;
                  }

                  var3.add(var8);
               }
            }

            if (var6 != null) {
               var3.add(var6);
            }
         }
      }

      if (var3 == null) {
         return this;
      } else if (var3.size() > 1) {
         return new AndDependencyTraverser(var3);
      } else {
         return var3.isEmpty() ? null : (DependencyTraverser)var3.iterator().next();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (null != var1 && this.getClass().equals(var1.getClass())) {
         AndDependencyTraverser var2 = (AndDependencyTraverser)var1;
         return this.traversers.equals(var2.traversers);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         int var1 = 17;
         var1 = var1 * 31 + this.traversers.hashCode();
         this.hashCode = var1;
      }

      return this.hashCode;
   }
}
