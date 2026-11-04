package com.google.common.graph;

import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class ForwardingGraph<N> extends AbstractGraph<N> {
   abstract BaseGraph<N> delegate();

   @Override
   public Set<N> nodes() {
      return this.delegate().nodes();
   }

   @Override
   protected long edgeCount() {
      return (long)this.delegate().edges().size();
   }

   @Override
   public boolean isDirected() {
      return this.delegate().isDirected();
   }

   @Override
   public boolean allowsSelfLoops() {
      return this.delegate().allowsSelfLoops();
   }

   @Override
   public ElementOrder<N> nodeOrder() {
      return this.delegate().nodeOrder();
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return this.delegate().incidentEdgeOrder();
   }

   @Override
   public Set<N> adjacentNodes(N var1) {
      return this.delegate().adjacentNodes((N)var1);
   }

   @Override
   public Set<N> predecessors(N var1) {
      return this.delegate().predecessors((N)var1);
   }

   @Override
   public Set<N> successors(N var1) {
      return this.delegate().successors((N)var1);
   }

   @Override
   public Set<EndpointPair<N>> incidentEdges(N var1) {
      return this.delegate().incidentEdges((N)var1);
   }

   @Override
   public int degree(N var1) {
      return this.delegate().degree((N)var1);
   }

   @Override
   public int inDegree(N var1) {
      return this.delegate().inDegree((N)var1);
   }

   @Override
   public int outDegree(N var1) {
      return this.delegate().outDegree((N)var1);
   }

   @Override
   public boolean hasEdgeConnecting(N var1, N var2) {
      return this.delegate().hasEdgeConnecting((N)var1, (N)var2);
   }

   @Override
   public boolean hasEdgeConnecting(EndpointPair<N> var1) {
      return this.delegate().hasEdgeConnecting(var1);
   }
}
