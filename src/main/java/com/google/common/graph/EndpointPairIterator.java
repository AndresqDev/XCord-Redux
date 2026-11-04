package com.google.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class EndpointPairIterator<N> extends AbstractIterator<EndpointPair<N>> {
   private final BaseGraph<N> graph;
   private final Iterator<N> nodeIterator;
   N node = (N)null;
   Iterator<N> successorIterator = ImmutableSet.<N>of().iterator();

   static <N> EndpointPairIterator<N> of(BaseGraph<N> var0) {
      return (EndpointPairIterator<N>)(var0.isDirected() ? new EndpointPairIterator.Directed<>(var0) : new EndpointPairIterator.Undirected<>(var0));
   }

   private EndpointPairIterator(BaseGraph<N> var1) {
      this.graph = var1;
      this.nodeIterator = var1.nodes().iterator();
   }

   final boolean advance() {
      Preconditions.checkState(!this.successorIterator.hasNext());
      if (!this.nodeIterator.hasNext()) {
         return false;
      } else {
         this.node = this.nodeIterator.next();
         this.successorIterator = this.graph.successors(this.node).iterator();
         return true;
      }
   }

   private static final class Directed<N> extends EndpointPairIterator<N> {
      private Directed(BaseGraph<N> var1) {
         super(var1);
      }

      protected EndpointPair<N> computeNext() {
         while (!this.successorIterator.hasNext()) {
            if (!this.advance()) {
               return this.endOfData();
            }
         }

         return EndpointPair.ordered(Objects.requireNonNull(this.node), this.successorIterator.next());
      }
   }

   private static final class Undirected<N> extends EndpointPairIterator<N> {
      private Set<N> visitedNodes;

      private Undirected(BaseGraph<N> var1) {
         super(var1);
         this.visitedNodes = Sets.newHashSetWithExpectedSize(var1.nodes().size() + 1);
      }

      protected EndpointPair<N> computeNext() {
         do {
            Objects.requireNonNull(this.visitedNodes);

            while (this.successorIterator.hasNext()) {
               Object var1 = this.successorIterator.next();
               if (!this.visitedNodes.contains(var1)) {
                  return EndpointPair.unordered(Objects.requireNonNull(this.node), (N)var1);
               }
            }

            this.visitedNodes.add(this.node);
         } while (this.advance());

         this.visitedNodes = null;
         return this.endOfData();
      }
   }
}
