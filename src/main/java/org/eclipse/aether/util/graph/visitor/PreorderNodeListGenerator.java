package org.eclipse.aether.util.graph.visitor;

import org.eclipse.aether.graph.DependencyNode;

public final class PreorderNodeListGenerator extends AbstractDepthFirstNodeListGenerator {
   @Override
   public boolean visitEnter(DependencyNode var1) {
      if (!this.setVisited(var1)) {
         return false;
      } else {
         if (var1.getDependency() != null) {
            this.nodes.add(var1);
         }

         return true;
      }
   }

   @Override
   public boolean visitLeave(DependencyNode var1) {
      return true;
   }
}
