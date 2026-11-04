package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

@ElementTypesAreNonnullByDefault
class StandardNetwork<N, E> extends AbstractNetwork<N, E> {
   private final boolean isDirected;
   private final boolean allowsParallelEdges;
   private final boolean allowsSelfLoops;
   private final ElementOrder<N> nodeOrder;
   private final ElementOrder<E> edgeOrder;
   final MapIteratorCache<N, NetworkConnections<N, E>> nodeConnections;
   final MapIteratorCache<E, N> edgeToReferenceNode;

   StandardNetwork(NetworkBuilder<? super N, ? super E> var1) {
      this(var1, var1.nodeOrder.createMap(var1.expectedNodeCount.or(10)), var1.edgeOrder.createMap(var1.expectedEdgeCount.or(20)));
   }

   StandardNetwork(NetworkBuilder<? super N, ? super E> var1, Map<N, NetworkConnections<N, E>> var2, Map<E, N> var3) {
      this.isDirected = var1.directed;
      this.allowsParallelEdges = var1.allowsParallelEdges;
      this.allowsSelfLoops = var1.allowsSelfLoops;
      this.nodeOrder = var1.nodeOrder.cast();
      this.edgeOrder = var1.edgeOrder.cast();
      this.nodeConnections = (MapIteratorCache<N, NetworkConnections<N, E>>)(var2 instanceof TreeMap
         ? new MapRetrievalCache<>(var2)
         : new MapIteratorCache<>(var2));
      this.edgeToReferenceNode = new MapIteratorCache<>(var3);
   }

   @Override
   public Set<N> nodes() {
      return this.nodeConnections.unmodifiableKeySet();
   }

   @Override
   public Set<E> edges() {
      return this.edgeToReferenceNode.unmodifiableKeySet();
   }

   @Override
   public boolean isDirected() {
      return this.isDirected;
   }

   @Override
   public boolean allowsParallelEdges() {
      return this.allowsParallelEdges;
   }

   @Override
   public boolean allowsSelfLoops() {
      return this.allowsSelfLoops;
   }

   @Override
   public ElementOrder<N> nodeOrder() {
      return this.nodeOrder;
   }

   @Override
   public ElementOrder<E> edgeOrder() {
      return this.edgeOrder;
   }

   @Override
   public Set<E> incidentEdges(N var1) {
      return this.checkedConnections((N)var1).incidentEdges();
   }

   @Override
   public EndpointPair<N> incidentNodes(E var1) {
      Object var2 = this.checkedReferenceNode((E)var1);
      Object var3 = Objects.requireNonNull(this.nodeConnections.get(var2)).adjacentNode((E)var1);
      return EndpointPair.of(this, (N)var2, (N)var3);
   }

   @Override
   public Set<N> adjacentNodes(N var1) {
      return this.checkedConnections((N)var1).adjacentNodes();
   }

   @Override
   public Set<E> edgesConnecting(N var1, N var2) {
      NetworkConnections var3 = this.checkedConnections((N)var1);
      if (!this.allowsSelfLoops && var1 == var2) {
         return ImmutableSet.of();
      } else {
         Preconditions.checkArgument(this.containsNode((N)var2), "Node %s is not an element of this graph.", var2);
         return var3.edgesConnecting(var2);
      }
   }

   @Override
   public Set<E> inEdges(N var1) {
      return this.checkedConnections((N)var1).inEdges();
   }

   @Override
   public Set<E> outEdges(N var1) {
      return this.checkedConnections((N)var1).outEdges();
   }

   @Override
   public Set<N> predecessors(N var1) {
      return this.checkedConnections((N)var1).predecessors();
   }

   @Override
   public Set<N> successors(N var1) {
      return this.checkedConnections((N)var1).successors();
   }

   final NetworkConnections<N, E> checkedConnections(N var1) {
      NetworkConnections var2 = this.nodeConnections.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException(String.format("Node %s is not an element of this graph.", var1));
      } else {
         return var2;
      }
   }

   final N checkedReferenceNode(E var1) {
      Object var2 = this.edgeToReferenceNode.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException(String.format("Edge %s is not an element of this graph.", var1));
      } else {
         return (N)var2;
      }
   }

   final boolean containsNode(N var1) {
      return this.nodeConnections.containsKey(var1);
   }

   final boolean containsEdge(E var1) {
      return this.edgeToReferenceNode.containsKey(var1);
   }
}
