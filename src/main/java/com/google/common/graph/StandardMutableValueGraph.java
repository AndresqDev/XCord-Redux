package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Objects;

@ElementTypesAreNonnullByDefault
final class StandardMutableValueGraph<N, V> extends StandardValueGraph<N, V> implements MutableValueGraph<N, V> {
   private final ElementOrder<N> incidentEdgeOrder;

   StandardMutableValueGraph(AbstractGraphBuilder<? super N> var1) {
      super(var1);
      this.incidentEdgeOrder = var1.incidentEdgeOrder.cast();
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return this.incidentEdgeOrder;
   }

   @CanIgnoreReturnValue
   @Override
   public boolean addNode(N var1) {
      Preconditions.checkNotNull(var1, "node");
      if (this.containsNode((N)var1)) {
         return false;
      } else {
         this.addNodeInternal((N)var1);
         return true;
      }
   }

   @CanIgnoreReturnValue
   private GraphConnections<N, V> addNodeInternal(N var1) {
      GraphConnections var2 = this.newConnections();
      Preconditions.checkState(this.nodeConnections.put((N)var1, var2) == null);
      return var2;
   }

   @CanIgnoreReturnValue
   @Override
   public V putEdgeValue(N var1, N var2, V var3) {
      Preconditions.checkNotNull(var1, "nodeU");
      Preconditions.checkNotNull(var2, "nodeV");
      Preconditions.checkNotNull(var3, "value");
      if (!this.allowsSelfLoops()) {
         Preconditions.checkArgument(
            !var1.equals(var2),
            "Cannot add self-loop edge on node %s, as self-loops are not allowed. To construct a graph that allows self-loops, call allowsSelfLoops(true) on the Builder.",
            var1
         );
      }

      GraphConnections var4 = this.nodeConnections.get(var1);
      if (var4 == null) {
         var4 = this.addNodeInternal((N)var1);
      }

      Object var5 = var4.addSuccessor(var2, var3);
      GraphConnections var6 = this.nodeConnections.get(var2);
      if (var6 == null) {
         var6 = this.addNodeInternal((N)var2);
      }

      var6.addPredecessor(var1, var3);
      if (var5 == null) {
         Graphs.checkPositive(++this.edgeCount);
      }

      return (V)var5;
   }

   @CanIgnoreReturnValue
   @Override
   public V putEdgeValue(EndpointPair<N> var1, V var2) {
      this.validateEndpoints(var1);
      return this.putEdgeValue((N)var1.nodeU(), (N)var1.nodeV(), (V)var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean removeNode(N var1) {
      Preconditions.checkNotNull(var1, "node");
      GraphConnections var2 = this.nodeConnections.get(var1);
      if (var2 == null) {
         return false;
      } else {
         if (this.allowsSelfLoops() && var2.removeSuccessor(var1) != null) {
            var2.removePredecessor(var1);
            this.edgeCount--;
         }

         for (Object var4 : var2.successors()) {
            Objects.requireNonNull(this.nodeConnections.getWithoutCaching(var4)).removePredecessor((N)var1);
            this.edgeCount--;
         }

         if (this.isDirected()) {
            for (Object var6 : var2.predecessors()) {
               Preconditions.checkState(Objects.requireNonNull(this.nodeConnections.getWithoutCaching(var6)).removeSuccessor((N)var1) != null);
               this.edgeCount--;
            }
         }

         this.nodeConnections.remove(var1);
         Graphs.checkNonNegative(this.edgeCount);
         return true;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public V removeEdge(N var1, N var2) {
      Preconditions.checkNotNull(var1, "nodeU");
      Preconditions.checkNotNull(var2, "nodeV");
      GraphConnections var3 = this.nodeConnections.get(var1);
      GraphConnections var4 = this.nodeConnections.get(var2);
      if (var3 != null && var4 != null) {
         Object var5 = var3.removeSuccessor(var2);
         if (var5 != null) {
            var4.removePredecessor(var1);
            Graphs.checkNonNegative(--this.edgeCount);
         }

         return (V)var5;
      } else {
         return null;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public V removeEdge(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.removeEdge((N)var1.nodeU(), (N)var1.nodeV());
   }

   private GraphConnections<N, V> newConnections() {
      return (GraphConnections<N, V>)(this.isDirected()
         ? DirectedGraphConnections.of(this.incidentEdgeOrder)
         : UndirectedGraphConnections.of(this.incidentEdgeOrder));
   }
}
