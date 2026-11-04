package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.Map;

@ElementTypesAreNonnullByDefault
final class EdgesConnecting<E> extends AbstractSet<E> {
   private final Map<?, E> nodeToOutEdge;
   private final Object targetNode;

   EdgesConnecting(Map<?, E> var1, Object var2) {
      this.nodeToOutEdge = Preconditions.checkNotNull(var1);
      this.targetNode = Preconditions.checkNotNull(var2);
   }

   public UnmodifiableIterator<E> iterator() {
      Object var1 = this.getConnectingEdge();
      return var1 == null ? ImmutableSet.<E>of().iterator() : Iterators.singletonIterator((E)var1);
   }

   @Override
   public int size() {
      return this.getConnectingEdge() == null ? 0 : 1;
   }

   @Override
   public boolean contains(Object var1) {
      Object var2 = this.getConnectingEdge();
      return var2 != null && var2.equals(var1);
   }

   private E getConnectingEdge() {
      return this.nodeToOutEdge.get(this.targetNode);
   }
}
