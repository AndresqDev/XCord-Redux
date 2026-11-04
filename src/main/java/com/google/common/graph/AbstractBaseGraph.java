package com.google.common.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.util.AbstractSet;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class AbstractBaseGraph<N> implements BaseGraph<N> {
   protected long edgeCount() {
      long var1 = 0L;

      for (Object var4 : this.nodes()) {
         var1 += (long)this.degree((N)var4);
      }

      Preconditions.checkState((var1 & 1L) == 0L);
      return var1 >>> 1;
   }

   @Override
   public Set<EndpointPair<N>> edges() {
      return new AbstractSet<EndpointPair<N>>() {
         public UnmodifiableIterator<EndpointPair<N>> iterator() {
            return EndpointPairIterator.of(AbstractBaseGraph.this);
         }

         @Override
         public int size() {
            return Ints.saturatedCast(AbstractBaseGraph.this.edgeCount());
         }

         @Override
         public boolean remove(Object var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean contains(Object var1) {
            if (!(var1 instanceof EndpointPair)) {
               return false;
            } else {
               EndpointPair var2 = (EndpointPair)var1;
               return AbstractBaseGraph.this.isOrderingCompatible(var2)
                  && AbstractBaseGraph.this.nodes().contains(var2.nodeU())
                  && AbstractBaseGraph.this.successors(var2.nodeU()).contains(var2.nodeV());
            }
         }
      };
   }

   @Override
   public ElementOrder<N> incidentEdgeOrder() {
      return ElementOrder.unordered();
   }

   @Override
   public Set<EndpointPair<N>> incidentEdges(N var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this.nodes().contains(var1), "Node %s is not an element of this graph.", var1);
      return new IncidentEdgeSet<N>(this, this, var1) {
         public UnmodifiableIterator<EndpointPair<N>> iterator() {
            return this.graph.isDirected()
               ? Iterators.unmodifiableIterator(
                  Iterators.concat(
                     Iterators.transform(this.graph.predecessors(this.node).iterator(), new Function<N, EndpointPair<N>>() {
                        public EndpointPair<N> apply(N var1) {
                           return EndpointPair.ordered((N)var1, node);
                        }
                     }),
                     Iterators.transform(
                        Sets.difference(this.graph.successors(this.node), ImmutableSet.of(this.node)).iterator(), new Function<N, EndpointPair<N>>() {
                           public EndpointPair<N> apply(N var1) {
                              return EndpointPair.ordered(node, (N)var1);
                           }
                        }
                     )
                  )
               )
               : Iterators.unmodifiableIterator(Iterators.transform(this.graph.adjacentNodes(this.node).iterator(), new Function<N, EndpointPair<N>>() {
                  public EndpointPair<N> apply(N var1) {
                     return EndpointPair.unordered(node, (N)var1);
                  }
               }));
         }
      };
   }

   @Override
   public int degree(N var1) {
      if (this.isDirected()) {
         return IntMath.saturatedAdd(this.predecessors((N)var1).size(), this.successors((N)var1).size());
      } else {
         Set var2 = this.adjacentNodes((N)var1);
         int var3 = this.allowsSelfLoops() && var2.contains(var1) ? 1 : 0;
         return IntMath.saturatedAdd(var2.size(), var3);
      }
   }

   @Override
   public int inDegree(N var1) {
      return this.isDirected() ? this.predecessors((N)var1).size() : this.degree((N)var1);
   }

   @Override
   public int outDegree(N var1) {
      return this.isDirected() ? this.successors((N)var1).size() : this.degree((N)var1);
   }

   @Override
   public boolean hasEdgeConnecting(N var1, N var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return this.nodes().contains(var1) && this.successors((N)var1).contains(var2);
   }

   @Override
   public boolean hasEdgeConnecting(EndpointPair<N> var1) {
      Preconditions.checkNotNull(var1);
      if (!this.isOrderingCompatible(var1)) {
         return false;
      } else {
         Object var2 = var1.nodeU();
         Object var3 = var1.nodeV();
         return this.nodes().contains(var2) && this.successors((N)var2).contains(var3);
      }
   }

   protected final void validateEndpoints(EndpointPair<?> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this.isOrderingCompatible(var1), "Mismatch: unordered endpoints cannot be used with directed graphs");
   }

   protected final boolean isOrderingCompatible(EndpointPair<?> var1) {
      return var1.isOrdered() || !this.isDirected();
   }
}
