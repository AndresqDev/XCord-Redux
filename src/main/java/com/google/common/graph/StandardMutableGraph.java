package com.google.common.graph;

@ElementTypesAreNonnullByDefault
final class StandardMutableGraph<N> extends ForwardingGraph<N> implements MutableGraph<N> {
   private final MutableValueGraph<N, GraphConstants.Presence> backingValueGraph;

   StandardMutableGraph(AbstractGraphBuilder<? super N> var1) {
      this.backingValueGraph = new StandardMutableValueGraph<>(var1);
   }

   @Override
   BaseGraph<N> delegate() {
      return this.backingValueGraph;
   }

   @Override
   public boolean addNode(N var1) {
      return this.backingValueGraph.addNode((N)var1);
   }

   @Override
   public boolean putEdge(N var1, N var2) {
      return this.backingValueGraph.putEdgeValue((N)var1, (N)var2, GraphConstants.Presence.EDGE_EXISTS) == null;
   }

   @Override
   public boolean putEdge(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.putEdge((N)var1.nodeU(), (N)var1.nodeV());
   }

   @Override
   public boolean removeNode(N var1) {
      return this.backingValueGraph.removeNode((N)var1);
   }

   @Override
   public boolean removeEdge(N var1, N var2) {
      return this.backingValueGraph.removeEdge((N)var1, (N)var2) != null;
   }

   @Override
   public boolean removeEdge(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.removeEdge((N)var1.nodeU(), (N)var1.nodeV());
   }
}
