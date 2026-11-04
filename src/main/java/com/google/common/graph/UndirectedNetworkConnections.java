package com.google.common.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
final class UndirectedNetworkConnections<N, E> extends AbstractUndirectedNetworkConnections<N, E> {
   UndirectedNetworkConnections(Map<E, N> var1) {
      super(var1);
   }

   static <N, E> UndirectedNetworkConnections<N, E> of() {
      return new UndirectedNetworkConnections<>(HashBiMap.create(2));
   }

   static <N, E> UndirectedNetworkConnections<N, E> ofImmutable(Map<E, N> var0) {
      return new UndirectedNetworkConnections<>(ImmutableBiMap.copyOf(var0));
   }

   @Override
   public Set<N> adjacentNodes() {
      return Collections.unmodifiableSet(((BiMap)this.incidentEdgeMap).values());
   }

   @Override
   public Set<E> edgesConnecting(N var1) {
      return new EdgesConnecting<>(((BiMap)this.incidentEdgeMap).inverse(), var1);
   }
}
