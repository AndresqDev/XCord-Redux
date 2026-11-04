package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.util.Map;

@Immutable(
   containerOf = {"N", "E"}
)
@ElementTypesAreNonnullByDefault
@Beta
public final class ImmutableNetwork<N, E> extends StandardNetwork<N, E> {
   private ImmutableNetwork(Network<N, E> var1) {
      super(NetworkBuilder.from(var1), getNodeConnections(var1), getEdgeToReferenceNode(var1));
   }

   public static <N, E> ImmutableNetwork<N, E> copyOf(Network<N, E> var0) {
      return var0 instanceof ImmutableNetwork ? (ImmutableNetwork)var0 : new ImmutableNetwork<>(var0);
   }

   @Deprecated
   public static <N, E> ImmutableNetwork<N, E> copyOf(ImmutableNetwork<N, E> var0) {
      return Preconditions.checkNotNull(var0);
   }

   public ImmutableGraph<N> asGraph() {
      return new ImmutableGraph<>(super.asGraph());
   }

   private static <N, E> Map<N, NetworkConnections<N, E>> getNodeConnections(Network<N, E> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();

      for (Object var3 : var0.nodes()) {
         var1.put(var3, connectionsOf(var0, var3));
      }

      return var1.build();
   }

   private static <N, E> Map<E, N> getEdgeToReferenceNode(Network<N, E> var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();

      for (Object var3 : var0.edges()) {
         var1.put(var3, var0.incidentNodes(var3).nodeU());
      }

      return var1.build();
   }

   private static <N, E> NetworkConnections<N, E> connectionsOf(Network<N, E> var0, N var1) {
      if (var0.isDirected()) {
         Map var5 = Maps.asMap(var0.inEdges(var1), sourceNodeFn(var0));
         Map var3 = Maps.asMap(var0.outEdges(var1), targetNodeFn(var0));
         int var4 = var0.edgesConnecting(var1, var1).size();
         return (NetworkConnections<N, E>)(var0.allowsParallelEdges()
            ? DirectedMultiNetworkConnections.ofImmutable(var5, var3, var4)
            : DirectedNetworkConnections.ofImmutable(var5, var3, var4));
      } else {
         Map var2 = Maps.asMap(var0.incidentEdges(var1), adjacentNodeFn(var0, var1));
         return (NetworkConnections<N, E>)(var0.allowsParallelEdges()
            ? UndirectedMultiNetworkConnections.ofImmutable(var2)
            : UndirectedNetworkConnections.ofImmutable(var2));
      }
   }

   private static <N, E> Function<E, N> sourceNodeFn(final Network<N, E> var0) {
      return new Function<E, N>() {
         @Override
         public N apply(E var1) {
            return (N)var0.incidentNodes(var1).source();
         }
      };
   }

   private static <N, E> Function<E, N> targetNodeFn(final Network<N, E> var0) {
      return new Function<E, N>() {
         @Override
         public N apply(E var1) {
            return (N)var0.incidentNodes(var1).target();
         }
      };
   }

   private static <N, E> Function<E, N> adjacentNodeFn(final Network<N, E> var0, final N var1) {
      return new Function<E, N>() {
         @Override
         public N apply(E var1x) {
            return (N)var0.incidentNodes(var1x).adjacentNode(var1);
         }
      };
   }

   public static class Builder<N, E> {
      private final MutableNetwork<N, E> mutableNetwork;

      Builder(NetworkBuilder<N, E> var1) {
         this.mutableNetwork = var1.build();
      }

      @CanIgnoreReturnValue
      public ImmutableNetwork.Builder<N, E> addNode(N var1) {
         this.mutableNetwork.addNode((N)var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableNetwork.Builder<N, E> addEdge(N var1, N var2, E var3) {
         this.mutableNetwork.addEdge((N)var1, (N)var2, (E)var3);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableNetwork.Builder<N, E> addEdge(EndpointPair<N> var1, E var2) {
         this.mutableNetwork.addEdge(var1, (E)var2);
         return this;
      }

      public ImmutableNetwork<N, E> build() {
         return ImmutableNetwork.copyOf(this.mutableNetwork);
      }
   }
}
