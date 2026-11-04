package org.eclipse.aether.util.graph.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

abstract class AbstractDepthFirstNodeListGenerator implements DependencyVisitor {
   private final Map<DependencyNode, Object> visitedNodes;
   protected final List<DependencyNode> nodes = new ArrayList<>(128);

   AbstractDepthFirstNodeListGenerator() {
      this.visitedNodes = new IdentityHashMap<>(512);
   }

   public List<DependencyNode> getNodes() {
      return this.nodes;
   }

   public List<Dependency> getDependencies(boolean var1) {
      ArrayList var2 = new ArrayList(this.getNodes().size());

      for (DependencyNode var4 : this.getNodes()) {
         Dependency var5 = var4.getDependency();
         if (var5 != null && (var1 || var5.getArtifact().getFile() != null)) {
            var2.add(var5);
         }
      }

      return var2;
   }

   public List<Artifact> getArtifacts(boolean var1) {
      ArrayList var2 = new ArrayList(this.getNodes().size());

      for (DependencyNode var4 : this.getNodes()) {
         if (var4.getDependency() != null) {
            Artifact var5 = var4.getDependency().getArtifact();
            if (var1 || var5.getFile() != null) {
               var2.add(var5);
            }
         }
      }

      return var2;
   }

   public List<File> getFiles() {
      ArrayList var1 = new ArrayList(this.getNodes().size());

      for (DependencyNode var3 : this.getNodes()) {
         if (var3.getDependency() != null) {
            File var4 = var3.getDependency().getArtifact().getFile();
            if (var4 != null) {
               var1.add(var4);
            }
         }
      }

      return var1;
   }

   public String getClassPath() {
      StringBuilder var1 = new StringBuilder(1024);
      Iterator var2 = this.getNodes().iterator();

      while (var2.hasNext()) {
         DependencyNode var3 = (DependencyNode)var2.next();
         if (var3.getDependency() != null) {
            Artifact var4 = var3.getDependency().getArtifact();
            if (var4.getFile() != null) {
               var1.append(var4.getFile().getAbsolutePath());
               if (var2.hasNext()) {
                  var1.append(File.pathSeparatorChar);
               }
            }
         }
      }

      return var1.toString();
   }

   protected boolean setVisited(DependencyNode var1) {
      return this.visitedNodes.put(var1, Boolean.TRUE) == null;
   }

   @Override
   public abstract boolean visitEnter(DependencyNode var1);

   @Override
   public abstract boolean visitLeave(DependencyNode var1);
}
