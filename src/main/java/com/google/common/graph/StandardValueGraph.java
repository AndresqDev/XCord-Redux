package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@ElementTypesAreNonnullByDefault
class StandardValueGraph<N, V> extends AbstractValueGraph<N, V> {
   private final boolean isDirected;
   private final boolean allowsSelfLoops;
   private final ElementOrder<N> nodeOrder;
   final MapIteratorCache<N, GraphConnections<N, V>> nodeConnections;
   long edgeCount;

   StandardValueGraph(AbstractGraphBuilder<? super N> var1) {
      this(var1, var1.nodeOrder.createMap(var1.expectedNodeCount.or(10)), 0L);
   }

   StandardValueGraph(AbstractGraphBuilder<? super N> var1, Map<N, GraphConnections<N, V>> var2, long var3) {
      this.isDirected = var1.directed;
      this.allowsSelfLoops = var1.allowsSelfLoops;
      this.nodeOrder = var1.nodeOrder.cast();
      this.nodeConnections = (MapIteratorCache<N, GraphConnections<N, V>>)(var2 instanceof TreeMap
         ? new MapRetrievalCache<>(var2)
         : new MapIteratorCache<>(var2));
      this.edgeCount = Graphs.checkNonNegative(var3);
   }

   @Override
   public Set<N> nodes() {
      return this.nodeConnections.unmodifiableKeySet();
   }

   @Override
   public boolean isDirected() {
      return this.isDirected;
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
   public Set<N> adjacentNodes(N var1) {
      return this.checkedConnections((N)var1).adjacentNodes();
   }

   @Override
   public Set<N> predecessors(N var1) {
      return this.checkedConnections((N)var1).predecessors();
   }

   @Override
   public Set<N> successors(N var1) {
      return this.checkedConnections((N)var1).successors();
   }

   @Override
   public Set<EndpointPair<N>> incidentEdges(N var1) {
      final GraphConnections var2 = this.checkedConnections((N)var1);
      return new IncidentEdgeSet<N>(this, this, var1) {
         @Override
         public Iterator<EndpointPair<N>> iterator() {
            return var2.incidentEdgeIterator(this.node);
         }
      };
   }

   @Override
   public boolean hasEdgeConnecting(N var1, N var2) {
      return this.hasEdgeConnectingInternal(Preconditions.checkNotNull((N)var1), Preconditions.checkNotNull((N)var2));
   }

   @Override
   public boolean hasEdgeConnecting(EndpointPair<N> var1) {
      Preconditions.checkNotNull(var1);
      return this.isOrderingCompatible(var1) && this.hasEdgeConnectingInternal((N)var1.nodeU(), (N)var1.nodeV());
   }

   @Override
   public V edgeValueOrDefault(N var1, N var2, V var3) {
      return this.edgeValueOrDefaultInternal(Preconditions.checkNotNull((N)var1), Preconditions.checkNotNull((N)var2), (V)var3);
   }

   @Override
   public V edgeValueOrDefault(EndpointPair<N> var1, V var2) {
      this.validateEndpoints(var1);
      return this.edgeValueOrDefaultInternal((N)var1.nodeU(), (N)var1.nodeV(), (V)var2);
   }

   @Override
   protected long edgeCount() {
      return this.edgeCount;
   }

   private final GraphConnections<N, V> checkedConnections(N var1) {
      GraphConnections var2 = this.nodeConnections.get(var1);
      if (var2 == null) {
         Preconditions.checkNotNull(var1);
         String var3 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(38 + String.valueOf(var3).length()).append("Node ").append(var3).append(" is not an element of this graph.").toString()
         );
      } else {
         return var2;
      }
   }

   final boolean containsNode(N var1) {
      return this.nodeConnections.containsKey(var1);
   }

   private final boolean hasEdgeConnectingInternal(N var1, N var2) {
      GraphConnections var3 = this.nodeConnections.get(var1);
      return var3 != null && var3.successors().contains(var2);
   }

   private final V edgeValueOrDefaultInternal(N var1, N var2, V var3) {
      GraphConnections var4 = this.nodeConnections.get(var1);
      Object var5 = var4 == null ? null : var4.value(var2);
      return (V)(var5 == null ? var3 : var5);
   }
}
