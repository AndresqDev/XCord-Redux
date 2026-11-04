package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@Beta
public interface MutableValueGraph<N, V> extends ValueGraph<N, V> {
   @CanIgnoreReturnValue
   boolean addNode(N var1);

   @CanIgnoreReturnValue
   V putEdgeValue(N var1, N var2, V var3);

   @CanIgnoreReturnValue
   V putEdgeValue(EndpointPair<N> var1, V var2);

   @CanIgnoreReturnValue
   boolean removeNode(N var1);

   @CanIgnoreReturnValue
   V removeEdge(N var1, N var2);

   @CanIgnoreReturnValue
   V removeEdge(EndpointPair<N> var1);
}
