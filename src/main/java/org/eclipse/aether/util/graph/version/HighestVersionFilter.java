package org.eclipse.aether.util.graph.version;

import java.util.Iterator;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;

public final class HighestVersionFilter implements VersionFilter {
   @Override
   public void filterVersions(VersionFilter.VersionFilterContext var1) {
      Iterator var2 = var1.iterator();
      boolean var3 = var2.hasNext();

      while (var3) {
         var2.next();
         var3 = var2.hasNext();
         if (var3) {
            var2.remove();
         }
      }
   }

   @Override
   public VersionFilter deriveChildFilter(DependencyCollectionContext var1) {
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
