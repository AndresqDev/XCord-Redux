package org.eclipse.aether.util.graph.visitor;

import org.eclipse.aether.graph.DependencyNode;

public final class PostorderNodeListGenerator extends AbstractDepthFirstNodeListGenerator {
   private final Stack<Boolean> visits = new Stack<>();

   @Override
   public boolean visitEnter(DependencyNode var1) {
      boolean var2 = !this.setVisited(var1);
      this.visits.push(var2);
      return !var2;
   }

   @Override
   public boolean visitLeave(DependencyNode var1) {
      Boolean var2 = this.visits.pop();
      if (var2) {
         return true;
      } else {
         if (var1.getDependency() != null) {
            this.nodes.add(var1);
         }

         return true;
      }
   }
}
