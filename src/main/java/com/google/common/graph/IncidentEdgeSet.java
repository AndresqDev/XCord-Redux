package com.google.common.graph;

import java.util.AbstractSet;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class IncidentEdgeSet<N> extends AbstractSet<EndpointPair<N>> {
   final N node;
   final BaseGraph<N> graph;

   IncidentEdgeSet(BaseGraph<N> var1, N var2) {
      this.graph = var1;
      this.node = (N)var2;
   }

   @Override
   public boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int size() {
      return this.graph.isDirected()
         ? this.graph.inDegree(this.node) + this.graph.outDegree(this.node) - (this.graph.successors(this.node).contains(this.node) ? 1 : 0)
         : this.graph.adjacentNodes(this.node).size();
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof EndpointPair)) {
         return false;
      } else {
         EndpointPair var2 = (EndpointPair)var1;
         if (this.graph.isDirected()) {
            if (!var2.isOrdered()) {
               return false;
            } else {
               Object var6 = var2.source();
               Object var7 = var2.target();
               return this.node.equals(var6) && this.graph.successors(this.node).contains(var7)
                  || this.node.equals(var7) && this.graph.predecessors(this.node).contains(var6);
            }
         } else if (var2.isOrdered()) {
            return false;
         } else {
            Set var3 = this.graph.adjacentNodes(this.node);
            Object var4 = var2.nodeU();
            Object var5 = var2.nodeV();
            return this.node.equals(var5) && var3.contains(var4) || this.node.equals(var4) && var3.contains(var5);
         }
      }
   }
}
