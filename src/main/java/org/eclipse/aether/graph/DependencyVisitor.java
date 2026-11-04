package org.eclipse.aether.graph;

public interface DependencyVisitor {
   boolean visitEnter(DependencyNode var1);

   boolean visitLeave(DependencyNode var1);
}
