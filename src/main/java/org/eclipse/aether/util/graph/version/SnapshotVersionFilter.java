package org.eclipse.aether.util.graph.version;

import java.util.Iterator;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.version.Version;

public final class SnapshotVersionFilter implements VersionFilter {
   @Override
   public void filterVersions(VersionFilter.VersionFilterContext var1) {
      Iterator var2 = var1.iterator();

      while (var2.hasNext()) {
         String var3 = ((Version)var2.next()).toString();
         if (var3.endsWith("SNAPSHOT")) {
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
