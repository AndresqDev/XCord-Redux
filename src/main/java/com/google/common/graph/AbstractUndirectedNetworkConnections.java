package com.google.common.graph;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class AbstractUndirectedNetworkConnections<N, E> implements NetworkConnections<N, E> {
   final Map<E, N> incidentEdgeMap;

   AbstractUndirectedNetworkConnections(Map<E, N> var1) {
      this.incidentEdgeMap = Preconditions.checkNotNull(var1);
   }

   @Override
   public Set<N> predecessors() {
      return this.adjacentNodes();
   }

   @Override
   public Set<N> successors() {
      return this.adjacentNodes();
   }

   @Override
   public Set<E> incidentEdges() {
      return Collections.unmodifiableSet(this.incidentEdgeMap.keySet());
   }

   @Override
   public Set<E> inEdges() {
      return this.incidentEdges();
   }

   @Override
   public Set<E> outEdges() {
      return this.incidentEdges();
   }

   @Override
   public N adjacentNode(E var1) {
      return Objects.requireNonNull(this.incidentEdgeMap.get(var1));
   }

   @Override
   public N removeInEdge(E var1, boolean var2) {
      return !var2 ? this.removeOutEdge((E)var1) : null;
   }

   @Override
   public N removeOutEdge(E var1) {
      Object var2 = this.incidentEdgeMap.remove(var1);
      return Objects.requireNonNull((N)var2);
   }

   @Override
   public void addInEdge(E var1, N var2, boolean var3) {
      if (!var3) {
         this.addOutEdge((E)var1, (N)var2);
      }
   }

   @Override
   public void addOutEdge(E var1, N var2) {
      Object var3 = this.incidentEdgeMap.put((E)var1, (N)var2);
      Preconditions.checkState(var3 == null);
   }
}
