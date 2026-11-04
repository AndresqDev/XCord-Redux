package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Set;

@ElementTypesAreNonnullByDefault
interface NetworkConnections<N, E> {
   Set<N> adjacentNodes();

   Set<N> predecessors();

   Set<N> successors();

   Set<E> incidentEdges();

   Set<E> inEdges();

   Set<E> outEdges();

   Set<E> edgesConnecting(N var1);

   N adjacentNode(E var1);

   @CanIgnoreReturnValue
   N removeInEdge(E var1, boolean var2);

   @CanIgnoreReturnValue
   N removeOutEdge(E var1);

   void addInEdge(E var1, N var2, boolean var3);

   void addOutEdge(E var1, N var2);
}
