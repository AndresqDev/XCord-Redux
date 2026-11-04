package org.eclipse.aether.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.version.VersionConstraint;

public class UnsolvableVersionConflictException extends RepositoryException {
   private final transient Collection<String> versions;
   private final transient Collection<? extends List<? extends DependencyNode>> paths;

   public UnsolvableVersionConflictException(Collection<? extends List<? extends DependencyNode>> var1) {
      super("Could not resolve version conflict among " + toPaths(var1));
      if (var1 == null) {
         this.paths = Collections.emptyList();
         this.versions = Collections.emptyList();
      } else {
         this.paths = var1;
         this.versions = new LinkedHashSet<>();

         for (List var3 : var1) {
            VersionConstraint var4 = ((DependencyNode)var3.get(var3.size() - 1)).getVersionConstraint();
            if (var4 != null && var4.getRange() != null) {
               this.versions.add(var4.toString());
            }
         }
      }
   }

   private static String toPaths(Collection<? extends List<? extends DependencyNode>> var0) {
      String var1 = "";
      if (var0 != null) {
         LinkedHashSet var2 = new LinkedHashSet();

         for (List var4 : var0) {
            var2.add(toPath(var4));
         }

         var1 = var2.toString();
      }

      return var1;
   }

   private static String toPath(List<? extends DependencyNode> var0) {
      StringBuilder var1 = new StringBuilder(256);
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         DependencyNode var3 = (DependencyNode)var2.next();
         if (var3.getDependency() != null) {
            Artifact var4 = var3.getDependency().getArtifact();
            var1.append(var4.getGroupId());
            var1.append(':').append(var4.getArtifactId());
            var1.append(':').append(var4.getExtension());
            if (var4.getClassifier().length() > 0) {
               var1.append(':').append(var4.getClassifier());
            }

            var1.append(':').append(var3.getVersionConstraint());
            if (var2.hasNext()) {
               var1.append(" -> ");
            }
         }
      }

      return var1.toString();
   }

   public Collection<? extends List<? extends DependencyNode>> getPaths() {
      return this.paths;
   }

   public Collection<String> getVersions() {
      return this.versions;
   }
}
