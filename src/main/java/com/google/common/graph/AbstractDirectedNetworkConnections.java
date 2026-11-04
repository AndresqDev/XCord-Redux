package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class AbstractDirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
   final Map<E, N> inEdgeMap;
   final Map<E, N> outEdgeMap;
   private int selfLoopCount;

   AbstractDirectedNetworkConnections(Map<E, N> var1, Map<E, N> var2, int var3) {
      this.inEdgeMap = Preconditions.checkNotNull(var1);
      this.outEdgeMap = Preconditions.checkNotNull(var2);
      this.selfLoopCount = Graphs.checkNonNegative(var3);
      Preconditions.checkState(var3 <= var1.size() && var3 <= var2.size());
   }

   @Override
   public Set<N> adjacentNodes() {
      return Sets.union(this.predecessors(), this.successors());
   }

   @Override
   public Set<E> incidentEdges() {
      return new AbstractSet<E>() {
         public UnmodifiableIterator<E> iterator() {
            Object var1 = AbstractDirectedNetworkConnections.this.selfLoopCount == 0
               ? Iterables.concat(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet())
               : Sets.union(AbstractDirectedNetworkConnections.this.inEdgeMap.keySet(), AbstractDirectedNetworkConnections.this.outEdgeMap.keySet());
            return Iterators.unmodifiableIterator(var1.iterator());
         }

         @Override
         public int size() {
            return IntMath.saturatedAdd(
               AbstractDirectedNetworkConnections.this.inEdgeMap.size(),
               AbstractDirectedNetworkConnections.this.outEdgeMap.size() - AbstractDirectedNetworkConnections.this.selfLoopCount
            );
         }

         @Override
         public boolean contains(Object var1) {
            return AbstractDirectedNetworkConnections.this.inEdgeMap.containsKey(var1) || AbstractDirectedNetworkConnections.this.outEdgeMap.containsKey(var1);
         }
      };
   }

   @Override
   public Set<E> inEdges() {
      return Collections.unmodifiableSet(this.inEdgeMap.keySet());
   }

   @Override
   public Set<E> outEdges() {
      return Collections.unmodifiableSet(this.outEdgeMap.keySet());
   }

   @Override
   public N adjacentNode(E var1) {
      return Objects.requireNonNull(this.outEdgeMap.get(var1));
   }

   @Override
   public N removeInEdge(E var1, boolean var2) {
      if (var2) {
         Graphs.checkNonNegative(--this.selfLoopCount);
      }

      Object var3 = this.inEdgeMap.remove(var1);
      return Objects.requireNonNull((N)var3);
   }

   @Override
   public N removeOutEdge(E var1) {
      Object var2 = this.outEdgeMap.remove(var1);
      return Objects.requireNonNull((N)var2);
   }

   @Override
   public void addInEdge(E var1, N var2, boolean var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      if (var3) {
         Graphs.checkPositive(++this.selfLoopCount);
      }

      Object var4 = this.inEdgeMap.put((E)var1, (N)var2);
      Preconditions.checkState(var4 == null);
   }

   @Override
   public void addOutEdge(E var1, N var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Object var3 = this.outEdgeMap.put((E)var1, (N)var2);
      Preconditions.checkState(var3 == null);
   }
}
