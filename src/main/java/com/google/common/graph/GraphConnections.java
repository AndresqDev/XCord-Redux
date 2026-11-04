package com.google.common.graph;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.Set;

@ElementTypesAreNonnullByDefault
interface GraphConnections<N, V> {
   Set<N> adjacentNodes();

   Set<N> predecessors();

   Set<N> successors();

   Iterator<EndpointPair<N>> incidentEdgeIterator(N var1);

   V value(N var1);

   void removePredecessor(N var1);

   @CanIgnoreReturnValue
   V removeSuccessor(N var1);

   void addPredecessor(N var1, V var2);

   @CanIgnoreReturnValue
   V addSuccessor(N var1, V var2);
}
