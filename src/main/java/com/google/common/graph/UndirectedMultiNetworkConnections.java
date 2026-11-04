package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
final class UndirectedMultiNetworkConnections<N, E> extends AbstractUndirectedNetworkConnections<N, E> {
   @LazyInit
   private transient Reference<Multiset<N>> adjacentNodesReference;

   private UndirectedMultiNetworkConnections(Map<E, N> var1) {
      super(var1);
   }

   static <N, E> UndirectedMultiNetworkConnections<N, E> of() {
      return new UndirectedMultiNetworkConnections<>(new HashMap<>(2, 1.0F));
   }

   static <N, E> UndirectedMultiNetworkConnections<N, E> ofImmutable(Map<E, N> var0) {
      return new UndirectedMultiNetworkConnections<>(ImmutableMap.copyOf(var0));
   }

   @Override
   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(this.adjacentNodesMultiset().elementSet());
   }

   private Multiset<N> adjacentNodesMultiset() {
      Object var1 = getReference(this.adjacentNodesReference);
      if (var1 == null) {
         var1 = HashMultiset.create(this.incidentEdgeMap.values());
         this.adjacentNodesReference = new SoftReference<>((Multiset<N>)var1);
      }

      return (Multiset<N>)var1;
   }

   @Override
   public Set<E> edgesConnecting(final N var1) {
      return new MultiEdgesConnecting<E>(this.incidentEdgeMap, var1) {
         @Override
         public int size() {
            return UndirectedMultiNetworkConnections.this.adjacentNodesMultiset().count(var1);
         }
      };
   }

   @Override
   public N removeInEdge(E var1, boolean var2) {
      return !var2 ? this.removeOutEdge((E)var1) : null;
   }

   @Override
   public N removeOutEdge(E var1) {
      Object var2 = super.removeOutEdge((E)var1);
      Multiset var3 = getReference(this.adjacentNodesReference);
      if (var3 != null) {
         Preconditions.checkState(var3.remove(var2));
      }

      return (N)var2;
   }

   @Override
   public void addInEdge(E var1, N var2, boolean var3) {
      if (!var3) {
         this.addOutEdge((E)var1, (N)var2);
      }
   }

   @Override
   public void addOutEdge(E var1, N var2) {
      super.addOutEdge((E)var1, (N)var2);
      Multiset var3 = getReference(this.adjacentNodesReference);
      if (var3 != null) {
         Preconditions.checkState(var3.add(var2));
      }
   }

   private static <T> T getReference(Reference<T> var0) {
      return (T)(var0 == null ? null : var0.get());
   }
}
