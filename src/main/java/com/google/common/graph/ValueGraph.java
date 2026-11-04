package com.google.common.graph;

import com.google.common.annotations.Beta;
import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@Beta
public interface ValueGraph<N, V> extends BaseGraph<N> {
   @Override
   Set<N> nodes();

   @Override
   Set<EndpointPair<N>> edges();

   Graph<N> asGraph();

   @Override
   boolean isDirected();

   @Override
   boolean allowsSelfLoops();

   @Override
   ElementOrder<N> nodeOrder();

   @Override
   ElementOrder<N> incidentEdgeOrder();

   @Override
   Set<N> adjacentNodes(N var1);

   @Override
   Set<N> predecessors(N var1);

   @Override
   Set<N> successors(N var1);

   @Override
   Set<EndpointPair<N>> incidentEdges(N var1);

   @Override
   int degree(N var1);

   @Override
   int inDegree(N var1);

   @Override
   int outDegree(N var1);

   @Override
   boolean hasEdgeConnecting(N var1, N var2);

   @Override
   boolean hasEdgeConnecting(EndpointPair<N> var1);

   Optional<V> edgeValue(N var1, N var2);

   Optional<V> edgeValue(EndpointPair<N> var1);

   V edgeValueOrDefault(N var1, N var2, V var3);

   V edgeValueOrDefault(EndpointPair<N> var1, V var2);

   @Override
   boolean equals(Object var1);

   @Override
   int hashCode();
}
