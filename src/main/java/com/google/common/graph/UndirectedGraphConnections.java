package com.google.common.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
final class UndirectedGraphConnections<N, V> implements GraphConnections<N, V> {
   private final Map<N, V> adjacentNodeValues;

   private UndirectedGraphConnections(Map<N, V> var1) {
      this.adjacentNodeValues = Preconditions.checkNotNull(var1);
   }

   static <N, V> UndirectedGraphConnections<N, V> of(ElementOrder<N> var0) {
      switch (var0.type()) {
         case UNORDERED:
            return new UndirectedGraphConnections<>(new HashMap<>(2, 1.0F));
         case STABLE:
            return new UndirectedGraphConnections<>(new LinkedHashMap<>(2, 1.0F));
         default:
            throw new AssertionError(var0.type());
      }
   }

   static <N, V> UndirectedGraphConnections<N, V> ofImmutable(Map<N, V> var0) {
      return new UndirectedGraphConnections<>(ImmutableMap.copyOf(var0));
   }

   @Override
   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(this.adjacentNodeValues.keySet());
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
   public Iterator<EndpointPair<N>> incidentEdgeIterator(final N var1) {
      return Iterators.transform(this.adjacentNodeValues.keySet().iterator(), new Function<N, EndpointPair<N>>(this) {
         public EndpointPair<N> apply(N var1x) {
            return EndpointPair.unordered((N)var1, (N)var1x);
         }
      });
   }

   @Override
   public V value(N var1) {
      return this.adjacentNodeValues.get(var1);
   }

   @Override
   public void removePredecessor(N var1) {
      Object var2 = this.removeSuccessor((N)var1);
   }

   @Override
   public V removeSuccessor(N var1) {
      return this.adjacentNodeValues.remove(var1);
   }

   @Override
   public void addPredecessor(N var1, V var2) {
      Object var3 = this.addSuccessor((N)var1, (V)var2);
   }

   @Override
   public V addSuccessor(N var1, V var2) {
      return this.adjacentNodeValues.put((N)var1, (V)var2);
   }
}
