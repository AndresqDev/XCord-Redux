package org.eclipse.aether.util.graph.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;

public final class AndDependencySelector implements DependencySelector {
   private final Set<? extends DependencySelector> selectors;
   private int hashCode;

   public AndDependencySelector(DependencySelector... var1) {
      if (var1 != null && var1.length > 0) {
         this.selectors = new LinkedHashSet<>(Arrays.asList(var1));
      } else {
         this.selectors = Collections.emptySet();
      }
   }

   public AndDependencySelector(Collection<? extends DependencySelector> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.selectors = new LinkedHashSet<>(var1);
      } else {
         this.selectors = Collections.emptySet();
      }
   }

   private AndDependencySelector(Set<DependencySelector> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.selectors = var1;
      } else {
         this.selectors = Collections.emptySet();
      }
   }

   public static DependencySelector newInstance(DependencySelector var0, DependencySelector var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (DependencySelector)(var1 != null && !var1.equals(var0) ? new AndDependencySelector(var0, var1) : var0);
      }
   }

   @Override
   public boolean selectDependency(Dependency var1) {
      for (DependencySelector var3 : this.selectors) {
         if (!var3.selectDependency(var1)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public DependencySelector deriveChildSelector(DependencyCollectionContext var1) {
      int var2 = 0;
      LinkedHashSet var3 = null;

      for (DependencySelector var5 : this.selectors) {
         DependencySelector var6 = var5.deriveChildSelector(var1);
         if (var3 != null) {
            if (var6 != null) {
               var3.add(var6);
            }
         } else if (var5 == var6) {
            var2++;
         } else {
            var3 = new LinkedHashSet();
            if (var2 > 0) {
               for (DependencySelector var8 : this.selectors) {
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
         return new AndDependencySelector(var3);
      } else {
         return var3.isEmpty() ? null : (DependencySelector)var3.iterator().next();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (null != var1 && this.getClass().equals(var1.getClass())) {
         AndDependencySelector var2 = (AndDependencySelector)var1;
         return this.selectors.equals(var2.selectors);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         int var1 = 17;
         var1 = var1 * 31 + this.selectors.hashCode();
         this.hashCode = var1;
      }

      return this.hashCode;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder().append(this.getClass().getSimpleName()).append('(');
      Iterator var2 = this.selectors.iterator();

      while (var2.hasNext()) {
         DependencySelector var3 = (DependencySelector)var2.next();
         var1.append(var3.toString());
         if (var2.hasNext()) {
            var1.append(" && ");
         }
      }

      return var1.append(')').toString();
   }
}
