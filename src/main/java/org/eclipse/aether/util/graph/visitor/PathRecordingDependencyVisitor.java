package org.eclipse.aether.util.graph.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

public final class PathRecordingDependencyVisitor implements DependencyVisitor {
   private final DependencyFilter filter;
   private final List<List<DependencyNode>> paths;
   private final Stack<DependencyNode> parents;
   private final boolean excludeChildrenOfMatches;

   public PathRecordingDependencyVisitor(DependencyFilter var1) {
      this(var1, true);
   }

   public PathRecordingDependencyVisitor(DependencyFilter var1, boolean var2) {
      this.filter = var1;
      this.excludeChildrenOfMatches = var2;
      this.paths = new ArrayList<>();
      this.parents = new Stack<>();
   }

   public DependencyFilter getFilter() {
      return this.filter;
   }

   public List<List<DependencyNode>> getPaths() {
      return this.paths;
   }

   @Override
   public boolean visitEnter(DependencyNode var1) {
      boolean var2 = this.filter == null || this.filter.accept(var1, this.parents);
      boolean var3 = this.parents.contains(var1);
      this.parents.push(var1);
      if (var2) {
         DependencyNode[] var4 = new DependencyNode[this.parents.size()];
         int var5 = 0;

         for (int var6 = this.parents.size(); var5 < var6; var5++) {
            var4[var6 - var5 - 1] = this.parents.get(var5);
         }

         this.paths.add(Arrays.asList(var4));
         if (this.excludeChildrenOfMatches) {
            return false;
         }
      }

      return !var3;
   }

   @Override
   public boolean visitLeave(DependencyNode var1) {
      this.parents.pop();
      return true;
   }
}
