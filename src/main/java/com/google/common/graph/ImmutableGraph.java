package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.util.Set;

@Immutable(
   containerOf = {"N"}
)
@ElementTypesAreNonnullByDefault
@Beta
public class ImmutableGraph<N> extends ForwardingGraph<N> {
   private final BaseGraph<N> backingGraph;

   ImmutableGraph(BaseGraph<N> var1) {
      this.backingGraph = var1;
   }

   public static <N> ImmutableGraph<N> copyOf(Graph<N> var0) {
      return var0 instanceof ImmutableGraph
         ? (ImmutableGraph)var0
         : new ImmutableGraph<>(new StandardValueGraph<>(GraphBuilder.from(var0), getNodeConnections(var0), (long)var0.edges().size()));
   }

   @Deprecated
   public static <N> ImmutableGraph<N> copyOf(ImmutableGraph<N> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return ElementOrder.stable();
   }

   private static <N> ImmutableMap<N, GraphConnections<N, GraphConstants.Presence>> getNodeConnections(Graph<N> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();

      for (Object var3 : var0.nodes()) {
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N> GraphConnections<N, GraphConstants.Presence> connectionsOf(Graph<N> var0, N var1) {
      Function var2 = Functions.constant(GraphConstants.Presence.EDGE_EXISTS);
      return (GraphConnections<N, GraphConstants.Presence>)(var0.isDirected()
         ? DirectedGraphConnections.ofImmutable((N)var1, (Iterable<EndpointPair<N>>)var0.incidentEdges(var1), var2)
         : UndirectedGraphConnections.ofImmutable(Maps.asMap((Set<N>)var0.adjacentNodes(var1), var2)));
   }

   @Override
   BaseGraph<N> delegate() {
      return this.backingGraph;
   }

   public static class Builder<N> {
      private final MutableGraph<N> mutableGraph;

      Builder(GraphBuilder<N> var1) {
         this.mutableGraph = var1.copy().incidentEdgeOrder(ElementOrder.stable()).build();
      }

      @CanIgnoreReturnValue
      public ImmutableGraph.Builder<N> addNode(N var1) {
         this.mutableGraph.addNode((N)var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableGraph.Builder<N> putEdge(N var1, N var2) {
         this.mutableGraph.putEdge((N)var1, (N)var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableGraph.Builder<N> putEdge(EndpointPair<N> var1) {
         this.mutableGraph.putEdge(var1);
         return this;
      }

      public ImmutableGraph<N> build() {
         return ImmutableGraph.copyOf(this.mutableGraph);
      }
   }
}
