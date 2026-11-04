package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableGraph<N> extends Graph<N> {
   @CanIgnoreReturnValue
   boolean addNode(N var1);

   @CanIgnoreReturnValue
   boolean putEdge(N var1, N var2);

   @CanIgnoreReturnValue
   boolean putEdge(EndpointPair<N> var1);

   @CanIgnoreReturnValue
   boolean removeNode(N var1);

   @CanIgnoreReturnValue
   boolean removeEdge(N var1, N var2);

   @CanIgnoreReturnValue
   boolean removeEdge(EndpointPair<N> var1);
}
