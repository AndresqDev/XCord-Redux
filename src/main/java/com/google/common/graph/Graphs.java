package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@Beta
public final class Graphs {
   private Graphs() {
   }

   public static <N> boolean hasCycle(Graph<N> var0) {
      int var1 = var0.edges().size();
      if (var1 == 0) {
         return false;
      } else if (!var0.isDirected() && var1 >= var0.nodes().size()) {
         return true;
      } else {
         HashMap var2 = Maps.newHashMapWithExpectedSize(var0.nodes().size());

         for (Object var4 : var0.nodes()) {
            if (subgraphHasCycle(var0, var2, var4, null)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean hasCycle(Network<?, ?> var0) {
      return !var0.isDirected() && var0.allowsParallelEdges() && var0.edges().size() > var0.asGraph().edges().size() ? true : hasCycle(var0.asGraph());
   }

   private static <N> boolean subgraphHasCycle(Graph<N> var0, Map<Object, Graphs.NodeVisitState> var1, N var2, N var3) {
      Graphs.NodeVisitState var4 = (Graphs.NodeVisitState)var1.get(var2);
      if (var4 == Graphs.NodeVisitState.COMPLETE) {
         return false;
      } else if (var4 == Graphs.NodeVisitState.PENDING) {
         return true;
      } else {
         var1.put(var2, Graphs.NodeVisitState.PENDING);

         for (Object var6 : var0.successors(var2)) {
            if (canTraverseWithoutReusingEdge(var0, var6, var3) && subgraphHasCycle(var0, var1, var6, var2)) {
               return true;
            }
         }

         var1.put(var2, Graphs.NodeVisitState.COMPLETE);
         return false;
      }
   }

   private static boolean canTraverseWithoutReusingEdge(Graph<?> var0, Object var1, Object var2) {
      return var0.isDirected() || !Objects.equal(var2, var1);
   }

   public static <N> Graph<N> transitiveClosure(Graph<N> var0) {
      MutableGraph var1 = GraphBuilder.from(var0).allowsSelfLoops(true).build();
      if (var0.isDirected()) {
         for (Object var3 : var0.nodes()) {
            for (Object var5 : reachableNodes(var0, var3)) {
               var1.putEdge(var3, var5);
            }
         }
      } else {
         HashSet var11 = new HashSet();

         for (Object var13 : var0.nodes()) {
            if (!var11.contains(var13)) {
               Set var14 = reachableNodes(var0, var13);
               var11.addAll(var14);
               int var6 = 1;

               for (Object var8 : var14) {
                  for (Object var10 : Iterables.limit(var14, var6++)) {
                     var1.putEdge(var8, var10);
                  }
               }
            }
         }
      }

      return var1;
   }

   public static <N> Set<N> reachableNodes(Graph<N> var0, N var1) {
      Preconditions.checkArgument(var0.nodes().contains(var1), "Node %s is not an element of this graph.", var1);
      return ImmutableSet.copyOf(Traverser.forGraph(var0).breadthFirst((N)var1));
   }

   public static <N> Graph<N> transpose(Graph<N> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (Graph<N>)(var0 instanceof Graphs.TransposedGraph ? ((Graphs.TransposedGraph)var0).graph : new Graphs.TransposedGraph<>(var0));
      }
   }

   public static <N, V> ValueGraph<N, V> transpose(ValueGraph<N, V> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (ValueGraph<N, V>)(var0 instanceof Graphs.TransposedValueGraph
            ? ((Graphs.TransposedValueGraph)var0).graph
            : new Graphs.TransposedValueGraph<>(var0));
      }
   }

   public static <N, E> Network<N, E> transpose(Network<N, E> var0) {
      if (!var0.isDirected()) {
         return var0;
      } else {
         return (Network<N, E>)(var0 instanceof Graphs.TransposedNetwork ? ((Graphs.TransposedNetwork)var0).network : new Graphs.TransposedNetwork<>(var0));
      }
   }

   static <N> EndpointPair<N> transpose(EndpointPair<N> var0) {
      return var0.isOrdered() ? EndpointPair.ordered((N)var0.target(), (N)var0.source()) : var0;
   }

   public static <N> MutableGraph<N> inducedSubgraph(Graph<N> var0, Iterable<? extends N> var1) {
      MutableGraph var2 = var1 instanceof Collection
         ? GraphBuilder.from(var0).expectedNodeCount(((Collection)var1).size()).build()
         : GraphBuilder.from(var0).build();

      for (Object var4 : var1) {
         var2.addNode(var4);
      }

      for (Object var8 : var2.nodes()) {
         for (Object var6 : var0.successors(var8)) {
            if (var2.nodes().contains(var6)) {
               var2.putEdge(var8, var6);
            }
         }
      }

      return var2;
   }

   public static <N, V> MutableValueGraph<N, V> inducedSubgraph(ValueGraph<N, V> var0, Iterable<? extends N> var1) {
      MutableValueGraph var2 = var1 instanceof Collection
         ? ValueGraphBuilder.from(var0).expectedNodeCount(((Collection)var1).size()).build()
         : ValueGraphBuilder.from(var0).build();

      for (Object var4 : var1) {
         var2.addNode(var4);
      }

      for (Object var8 : var2.nodes()) {
         for (Object var6 : var0.successors(var8)) {
            if (var2.nodes().contains(var6)) {
               var2.putEdgeValue(var8, var6, java.util.Objects.requireNonNull(var0.edgeValueOrDefault(var8, var6, null)));
            }
         }
      }

      return var2;
   }

   public static <N, E> MutableNetwork<N, E> inducedSubgraph(Network<N, E> var0, Iterable<? extends N> var1) {
      MutableNetwork var2 = var1 instanceof Collection
         ? NetworkBuilder.from(var0).expectedNodeCount(((Collection)var1).size()).build()
         : NetworkBuilder.from(var0).build();

      for (Object var4 : var1) {
         var2.addNode(var4);
      }

      for (Object var9 : var2.nodes()) {
         for (Object var6 : var0.outEdges(var9)) {
            Object var7 = var0.incidentNodes(var6).adjacentNode(var9);
            if (var2.nodes().contains(var7)) {
               var2.addEdge(var9, var7, var6);
            }
         }
      }

      return var2;
   }

   public static <N> MutableGraph<N> copyOf(Graph<N> var0) {
      MutableGraph var1 = GraphBuilder.from(var0).expectedNodeCount(var0.nodes().size()).build();

      for (Object var3 : var0.nodes()) {
         var1.addNode(var3);
      }

      for (EndpointPair var5 : var0.edges()) {
         var1.putEdge(var5.nodeU(), var5.nodeV());
      }

      return var1;
   }

   public static <N, V> MutableValueGraph<N, V> copyOf(ValueGraph<N, V> var0) {
      MutableValueGraph var1 = ValueGraphBuilder.from(var0).expectedNodeCount(var0.nodes().size()).build();

      for (Object var3 : var0.nodes()) {
         var1.addNode(var3);
      }

      for (EndpointPair var5 : var0.edges()) {
         var1.putEdgeValue(var5.nodeU(), var5.nodeV(), java.util.Objects.requireNonNull(var0.edgeValueOrDefault(var5.nodeU(), var5.nodeV(), null)));
      }

      return var1;
   }

   public static <N, E> MutableNetwork<N, E> copyOf(Network<N, E> var0) {
      MutableNetwork var1 = NetworkBuilder.from(var0).expectedNodeCount(var0.nodes().size()).expectedEdgeCount(var0.edges().size()).build();

      for (Object var3 : var0.nodes()) {
         var1.addNode(var3);
      }

      for (Object var6 : var0.edges()) {
         EndpointPair var4 = var0.incidentNodes(var6);
         var1.addEdge(var4.nodeU(), var4.nodeV(), var6);
      }

      return var1;
   }

   @CanIgnoreReturnValue
   static int checkNonNegative(int var0) {
      Preconditions.checkArgument(var0 >= 0, "Not true that %s is non-negative.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static long checkNonNegative(long var0) {
      Preconditions.checkArgument(var0 >= 0L, "Not true that %s is non-negative.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static int checkPositive(int var0) {
      Preconditions.checkArgument(var0 > 0, "Not true that %s is positive.", var0);
      return var0;
   }

   @CanIgnoreReturnValue
   static long checkPositive(long var0) {
      Preconditions.checkArgument(var0 > 0L, "Not true that %s is positive.", var0);
      return var0;
   }

   private static enum NodeVisitState {
      PENDING,
      COMPLETE;
   }

   private static class TransposedGraph<N> extends ForwardingGraph<N> {
      private final Graph<N> graph;

      TransposedGraph(Graph<N> var1) {
         this.graph = var1;
      }

      Graph<N> delegate() {
         return this.graph;
      }

      @Override
      public Set<N> predecessors(N var1) {
         return this.delegate().successors((N)var1);
      }

      @Override
      public Set<N> successors(N var1) {
         return this.delegate().predecessors((N)var1);
      }

      @Override
      public Set<EndpointPair<N>> incidentEdges(N var1) {
         return new IncidentEdgeSet<N>(this, var1) {
            @Override
            public Iterator<EndpointPair<N>> iterator() {
               return Iterators.transform(
                  TransposedGraph.this.delegate().incidentEdges(this.node).iterator(), new Function<EndpointPair<N>, EndpointPair<N>>() {
                     public EndpointPair<N> apply(EndpointPair<N> var1) {
                        return EndpointPair.of(TransposedGraph.this.delegate(), (N)var1.nodeV(), (N)var1.nodeU());
                     }
                  }
               );
            }
         };
      }

      @Override
      public int inDegree(N var1) {
         return this.delegate().outDegree((N)var1);
      }

      @Override
      public int outDegree(N var1) {
         return this.delegate().inDegree((N)var1);
      }

      @Override
      public boolean hasEdgeConnecting(N var1, N var2) {
         return this.delegate().hasEdgeConnecting((N)var2, (N)var1);
      }

      @Override
      public boolean hasEdgeConnecting(EndpointPair<N> var1) {
         return this.delegate().hasEdgeConnecting(Graphs.transpose(var1));
      }
   }

   private static class TransposedNetwork<N, E> extends ForwardingNetwork<N, E> {
      private final Network<N, E> network;

      TransposedNetwork(Network<N, E> var1) {
         this.network = var1;
      }

      @Override
      Network<N, E> delegate() {
         return this.network;
      }

      @Override
      public Set<N> predecessors(N var1) {
         return this.delegate().successors((N)var1);
      }

      @Override
      public Set<N> successors(N var1) {
         return this.delegate().predecessors((N)var1);
      }

      @Override
      public int inDegree(N var1) {
         return this.delegate().outDegree((N)var1);
      }

      @Override
      public int outDegree(N var1) {
         return this.delegate().inDegree((N)var1);
      }

      @Override
      public Set<E> inEdges(N var1) {
         return this.delegate().outEdges((N)var1);
      }

      @Override
      public Set<E> outEdges(N var1) {
         return this.delegate().inEdges((N)var1);
      }

      @Override
      public EndpointPair<N> incidentNodes(E var1) {
         EndpointPair var2 = this.delegate().incidentNodes((E)var1);
         return EndpointPair.of(this.network, (N)var2.nodeV(), (N)var2.nodeU());
      }

      @Override
      public Set<E> edgesConnecting(N var1, N var2) {
         return this.delegate().edgesConnecting((N)var2, (N)var1);
      }

      @Override
      public Set<E> edgesConnecting(EndpointPair<N> var1) {
         return this.delegate().edgesConnecting(Graphs.transpose(var1));
      }

      @Override
      public Optional<E> edgeConnecting(N var1, N var2) {
         return this.delegate().edgeConnecting((N)var2, (N)var1);
      }

      @Override
      public Optional<E> edgeConnecting(EndpointPair<N> var1) {
         return this.delegate().edgeConnecting(Graphs.transpose(var1));
      }

      @Override
      public E edgeConnectingOrNull(N var1, N var2) {
         return this.delegate().edgeConnectingOrNull((N)var2, (N)var1);
      }

      @Override
      public E edgeConnectingOrNull(EndpointPair<N> var1) {
         return this.delegate().edgeConnectingOrNull(Graphs.transpose(var1));
      }

      @Override
      public boolean hasEdgeConnecting(N var1, N var2) {
         return this.delegate().hasEdgeConnecting((N)var2, (N)var1);
      }

      @Override
      public boolean hasEdgeConnecting(EndpointPair<N> var1) {
         return this.delegate().hasEdgeConnecting(Graphs.transpose(var1));
      }
   }

   private static class TransposedValueGraph<N, V> extends ForwardingValueGraph<N, V> {
      private final ValueGraph<N, V> graph;

      TransposedValueGraph(ValueGraph<N, V> var1) {
         this.graph = var1;
      }

      @Override
      ValueGraph<N, V> delegate() {
         return this.graph;
      }

      @Override
      public Set<N> predecessors(N var1) {
         return this.delegate().successors((N)var1);
      }

      @Override
      public Set<N> successors(N var1) {
         return this.delegate().predecessors((N)var1);
      }

      @Override
      public int inDegree(N var1) {
         return this.delegate().outDegree((N)var1);
      }

      @Override
      public int outDegree(N var1) {
         return this.delegate().inDegree((N)var1);
      }

      @Override
      public boolean hasEdgeConnecting(N var1, N var2) {
         return this.delegate().hasEdgeConnecting((N)var2, (N)var1);
      }

      @Override
      public boolean hasEdgeConnecting(EndpointPair<N> var1) {
         return this.delegate().hasEdgeConnecting(Graphs.transpose(var1));
      }

      @Override
      public Optional<V> edgeValue(N var1, N var2) {
         return this.delegate().edgeValue((N)var2, (N)var1);
      }

      @Override
      public Optional<V> edgeValue(EndpointPair<N> var1) {
         return this.delegate().edgeValue(Graphs.transpose(var1));
      }

      @Override
      public V edgeValueOrDefault(N var1, N var2, V var3) {
         return this.delegate().edgeValueOrDefault((N)var2, (N)var1, (V)var3);
      }

      @Override
      public V edgeValueOrDefault(EndpointPair<N> var1, V var2) {
         return this.delegate().edgeValueOrDefault(Graphs.transpose(var1), (V)var2);
      }
   }
}
