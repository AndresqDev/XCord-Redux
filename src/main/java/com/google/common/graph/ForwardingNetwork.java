package com.google.common.graph;

import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class ForwardingNetwork<N, E> extends AbstractNetwork<N, E> {
   abstract Network<N, E> delegate();

   @Override
   public Set<N> nodes() {
      return this.delegate().nodes();
   }

   @Override
   public Set<E> edges() {
      return this.delegate().edges();
   }

   @Override
   public boolean isDirected() {
      return this.delegate().isDirected();
   }

   @Override
   public boolean allowsParallelEdges() {
      return this.delegate().allowsParallelEdges();
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
   public ElementOrder<E> edgeOrder() {
      return this.delegate().edgeOrder();
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
   public Set<E> incidentEdges(N var1) {
      return this.delegate().incidentEdges((N)var1);
   }

   @Override
   public Set<E> inEdges(N var1) {
      return this.delegate().inEdges((N)var1);
   }

   @Override
   public Set<E> outEdges(N var1) {
      return this.delegate().outEdges((N)var1);
   }

   @Override
   public EndpointPair<N> incidentNodes(E var1) {
      return this.delegate().incidentNodes((E)var1);
   }

   @Override
   public Set<E> adjacentEdges(E var1) {
      return this.delegate().adjacentEdges((E)var1);
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
   public Set<E> edgesConnecting(N var1, N var2) {
      return this.delegate().edgesConnecting((N)var1, (N)var2);
   }

   @Override
   public Set<E> edgesConnecting(EndpointPair<N> var1) {
      return this.delegate().edgesConnecting(var1);
   }

   @Override
   public Optional<E> edgeConnecting(N var1, N var2) {
      return this.delegate().edgeConnecting((N)var1, (N)var2);
   }

   @Override
   public Optional<E> edgeConnecting(EndpointPair<N> var1) {
      return this.delegate().edgeConnecting(var1);
   }

   @Override
   public E edgeConnectingOrNull(N var1, N var2) {
      return this.delegate().edgeConnectingOrNull((N)var1, (N)var2);
   }

   @Override
   public E edgeConnectingOrNull(EndpointPair<N> var1) {
      return this.delegate().edgeConnectingOrNull(var1);
   }

   @Override
   public boolean hasEdgeConnecting(N var1, N var2) {
      return this.delegate().hasEdgeConnecting((N)var1, (N)var2);
   }

   @Override
   public boolean hasEdgeConnecting(EndpointPair<N> var1) {
      return this.delegate().hasEdgeConnecting(var1);
   }
}
