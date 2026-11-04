package org.eclipse.aether.util.graph.visitor;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public final class TreeDependencyVisitor implements DependencyVisitor {
   private final Map<DependencyNode, Object> visitedNodes;
   private final DependencyVisitor visitor;
   private final Stack<Boolean> visits;

   public TreeDependencyVisitor(DependencyVisitor var1) {
      this.visitor = Objects.requireNonNull(var1, "dependency visitor delegate cannot be null");
      this.visitedNodes = new IdentityHashMap<>(512);
      this.visits = new Stack<>();
   }

   @Override
   public boolean visitEnter(DependencyNode var1) {
      boolean var2 = this.visitedNodes.put(var1, Boolean.TRUE) != null;
      this.visits.push(var2);
      return var2 ? false : this.visitor.visitEnter(var1);
   }

   @Override
   public boolean visitLeave(DependencyNode var1) {
      Boolean var2 = this.visits.pop();
      return var2 ? true : this.visitor.visitLeave(var1);
   }
}
