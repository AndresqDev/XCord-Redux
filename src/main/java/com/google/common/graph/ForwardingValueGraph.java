package com.google.common.graph;

import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class ForwardingValueGraph<N, V> extends AbstractValueGraph<N, V> {
   abstract ValueGraph<N, V> delegate();

   @Override
   public Set<N> nodes() {
      return this.delegate().nodes();
   }

   @Override
   protected long edgeCount() {
      return (long)this.delegate().edges().size();
   }

   @Override
   public boolean isDirected() {
      return this.delegate().isDirected();
   }

   @Override
   public boolean allowsSelfLoops() {
      return this.delegate().allowsSelfLoops();
   }

   @Override
   public ElementOrder<N> nodeOrder() {
      return this.delegate().nodeOrder();
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return this.delegate().incidentEdgeOrder();
   }

   @Override
   public Set<N> adjacentNodes(N var1) {
      return this.delegate().adjacentNodes((N)var1);
   }

   @Override
   public Set<N> predecessors(N var1) {
      return this.delegate().predecessors((N)var1);
   }

   @Override
   public Set<N> successors(N var1) {
      return this.delegate().successors((N)var1);
   }

   @Override
   public int degree(N var1) {
      return this.delegate().degree((N)var1);
   }

   @Override
   public int inDegree(N var1) {
      return this.delegate().inDegree((N)var1);
   }

   @Override
   public int outDegree(N var1) {
      return this.delegate().outDegree((N)var1);
   }

   @Override
   public boolean hasEdgeConnecting(N var1, N var2) {
      return this.delegate().hasEdgeConnecting((N)var1, (N)var2);
   }

   @Override
   public boolean hasEdgeConnecting(EndpointPair<N> var1) {
      return this.delegate().hasEdgeConnecting(var1);
   }

   @Override
   public Optional<V> edgeValue(N var1, N var2) {
      return this.delegate().edgeValue((N)var1, (N)var2);
   }

   @Override
   public Optional<V> edgeValue(EndpointPair<N> var1) {
      return this.delegate().edgeValue(var1);
   }

   @Override
   public V edgeValueOrDefault(N var1, N var2, V var3) {
      return this.delegate().edgeValueOrDefault((N)var1, (N)var2, (V)var3);
   }

   @Override
   public V edgeValueOrDefault(EndpointPair<N> var1, V var2) {
      return this.delegate().edgeValueOrDefault(var1, (V)var2);
   }
}
