package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;
import java.util.Set;

@Immutable(
   containerOf = {"N", "V"}
)
@ElementTypesAreNonnullByDefault
@Beta
public final class ImmutableValueGraph<N, V> extends StandardValueGraph<N, V> {
   private ImmutableValueGraph(ValueGraph<N, V> var1) {
      super(ValueGraphBuilder.from(var1), getNodeConnections(var1), (long)var1.edges().size());
   }

   public static <N, V> ImmutableValueGraph<N, V> copyOf(ValueGraph<N, V> var0) {
      return var0 instanceof ImmutableValueGraph ? (ImmutableValueGraph)var0 : new ImmutableValueGraph<>(var0);
   }

   @Deprecated
   public static <N, V> ImmutableValueGraph<N, V> copyOf(ImmutableValueGraph<N, V> var0) {
      return Preconditions.checkNotNull(var0);
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return ElementOrder.stable();
   }

   public ImmutableGraph<N> asGraph() {
      return new ImmutableGraph<>(this);
   }

   private static <N, V> ImmutableMap<N, GraphConnections<N, V>> getNodeConnections(ValueGraph<N, V> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();

      for (Object var3 : var0.nodes()) {
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N, V> GraphConnections<N, V> connectionsOf(final ValueGraph<N, V> var0, final N var1) {
      Function var2 = new Function<N, V>() {
         @Override
         public V apply(N var1x) {
            return Objects.requireNonNull((V)var0.edgeValueOrDefault(var1, var1x, null));
         }
      };
      return (GraphConnections<N, V>)(var0.isDirected()
         ? DirectedGraphConnections.ofImmutable((N)var1, (Iterable<EndpointPair<N>>)var0.incidentEdges(var1), var2)
         : UndirectedGraphConnections.ofImmutable(Maps.asMap((Set<N>)var0.adjacentNodes(var1), var2)));
   }

   public static class Builder<N, V> {
      private final MutableValueGraph<N, V> mutableValueGraph;

      Builder(ValueGraphBuilder<N, V> var1) {
         this.mutableValueGraph = var1.copy().incidentEdgeOrder(ElementOrder.stable()).build();
      }

      @CanIgnoreReturnValue
      public ImmutableValueGraph.Builder<N, V> addNode(N var1) {
         this.mutableValueGraph.addNode((N)var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableValueGraph.Builder<N, V> putEdgeValue(N var1, N var2, V var3) {
         this.mutableValueGraph.putEdgeValue((N)var1, (N)var2, (V)var3);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableValueGraph.Builder<N, V> putEdgeValue(EndpointPair<N> var1, V var2) {
         this.mutableValueGraph.putEdgeValue(var1, (V)var2);
         return this;
      }

      public ImmutableValueGraph<N, V> build() {
         return ImmutableValueGraph.copyOf(this.mutableValueGraph);
      }
   }
}
