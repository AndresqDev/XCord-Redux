package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.math.IntMath;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractNetwork<N, E> implements Network<N, E> {
   @Override
   public Graph<N> asGraph() {
      return new AbstractGraph<N>() {
         @Override
         public Set<N> nodes() {
            return AbstractNetwork.this.nodes();
         }

         @Override
         public Set<EndpointPair<N>> edges() {
            return (Set<EndpointPair<N>>)(AbstractNetwork.this.allowsParallelEdges() ? super.edges() : new AbstractSet<EndpointPair<N>>() {
               @Override
               public Iterator<EndpointPair<N>> iterator() {
                  return Iterators.transform(AbstractNetwork.this.edges().iterator(), new Function<E, EndpointPair<N>>() {
                     public EndpointPair<N> apply(E var1) {
                        return AbstractNetwork.this.incidentNodes(var1);
                     }
                  });
               }

               @Override
               public int size() {
                  return AbstractNetwork.this.edges().size();
               }

               @Override
               public boolean contains(Object var1) {
                  if (!(var1 instanceof EndpointPair)) {
                     return false;
                  } else {
                     EndpointPair var2 = (EndpointPair)var1;
                     return isOrderingCompatible(var2) && nodes().contains(var2.nodeU()) && successors((N)var2.nodeU()).contains(var2.nodeV());
                  }
               }
            });
         }

         @Override
         public ElementOrder<N> nodeOrder() {
            return AbstractNetwork.this.nodeOrder();
         }

         @Override
         public ElementOrder<N> incidentEdgeOrder() {
            return ElementOrder.unordered();
         }

         @Override
         public boolean isDirected() {
            return AbstractNetwork.this.isDirected();
         }

         @Override
         public boolean allowsSelfLoops() {
            return AbstractNetwork.this.allowsSelfLoops();
         }

         @Override
         public Set<N> adjacentNodes(N var1) {
            return (Set<N>)AbstractNetwork.this.adjacentNodes(var1);
         }

         @Override
         public Set<N> predecessors(N var1) {
            return (Set<N>)AbstractNetwork.this.predecessors(var1);
         }

         @Override
         public Set<N> successors(N var1) {
            return (Set<N>)AbstractNetwork.this.successors(var1);
         }
      };
   }

   @Override
   public int degree(N var1) {
      return this.isDirected()
         ? IntMath.saturatedAdd(this.inEdges((N)var1).size(), this.outEdges((N)var1).size())
         : IntMath.saturatedAdd(this.incidentEdges((N)var1).size(), this.edgesConnecting((N)var1, (N)var1).size());
   }

   @Override
   public int inDegree(N var1) {
      return this.isDirected() ? this.inEdges((N)var1).size() : this.degree((N)var1);
   }

   @Override
   public int outDegree(N var1) {
      return this.isDirected() ? this.outEdges((N)var1).size() : this.degree((N)var1);
   }

   @Override
   public Set<E> adjacentEdges(E var1) {
      EndpointPair var2 = this.incidentNodes((E)var1);
      Sets.SetView var3 = Sets.union(this.incidentEdges((N)var2.nodeU()), this.incidentEdges((N)var2.nodeV()));
      return Sets.difference(var3, ImmutableSet.of(var1));
   }

   @Override
   public Set<E> edgesConnecting(N var1, N var2) {
      Set var3 = this.outEdges((N)var1);
      Set var4 = this.inEdges((N)var2);
      return var3.size() <= var4.size()
         ? Collections.unmodifiableSet(Sets.filter(var3, this.connectedPredicate((N)var1, (N)var2)))
         : Collections.unmodifiableSet(Sets.filter(var4, this.connectedPredicate((N)var2, (N)var1)));
   }

   @Override
   public Set<E> edgesConnecting(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.edgesConnecting((N)var1.nodeU(), (N)var1.nodeV());
   }

   private Predicate<E> connectedPredicate(final N var1, final N var2) {
      return new Predicate<E>() {
         @Override
         public boolean apply(E var1x) {
            return AbstractNetwork.this.incidentNodes(var1x).adjacentNode((N)var1).equals(var2);
         }
      };
   }

   @Override
   public Optional<E> edgeConnecting(N var1, N var2) {
      return Optional.ofNullable(this.edgeConnectingOrNull((N)var1, (N)var2));
   }

   @Override
   public Optional<E> edgeConnecting(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.edgeConnecting((N)var1.nodeU(), (N)var1.nodeV());
   }

   @Override
   public E edgeConnectingOrNull(N var1, N var2) {
      Set var3 = this.edgesConnecting((N)var1, (N)var2);
      switch (var3.size()) {
         case 0:
            return null;
         case 1:
            return (E)var3.iterator().next();
         default:
            throw new IllegalArgumentException(
               String.format(
                  "Cannot call edgeConnecting() when parallel edges exist between %s and %s. Consider calling edgesConnecting() instead.", var1, var2
               )
            );
      }
   }

   @Override
   public E edgeConnectingOrNull(EndpointPair<N> var1) {
      this.validateEndpoints(var1);
      return this.edgeConnectingOrNull((N)var1.nodeU(), (N)var1.nodeV());
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
      return !this.isOrderingCompatible(var1) ? false : this.hasEdgeConnecting((N)var1.nodeU(), (N)var1.nodeV());
   }

   protected final void validateEndpoints(EndpointPair<?> var1) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkArgument(this.isOrderingCompatible(var1), "Mismatch: unordered endpoints cannot be used with directed graphs");
   }

   protected final boolean isOrderingCompatible(EndpointPair<?> var1) {
      return var1.isOrdered() || !this.isDirected();
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Network)) {
         return false;
      } else {
         Network var2 = (Network)var1;
         return this.isDirected() == var2.isDirected() && this.nodes().equals(var2.nodes()) && edgeIncidentNodesMap(this).equals(edgeIncidentNodesMap(var2));
      }
   }

   @Override
   public final int hashCode() {
      return edgeIncidentNodesMap(this).hashCode();
   }

   @Override
   public String toString() {
      boolean var1 = this.isDirected();
      boolean var2 = this.allowsParallelEdges();
      boolean var3 = this.allowsSelfLoops();
      String var4 = String.valueOf(this.nodes());
      String var5 = String.valueOf(edgeIncidentNodesMap(this));
      return new StringBuilder(87 + String.valueOf(var4).length() + String.valueOf(var5).length())
         .append("isDirected: ")
         .append(var1)
         .append(", allowsParallelEdges: ")
         .append(var2)
         .append(", allowsSelfLoops: ")
         .append(var3)
         .append(", nodes: ")
         .append(var4)
         .append(", edges: ")
         .append(var5)
         .toString();
   }

   private static <N, E> Map<E, EndpointPair<N>> edgeIncidentNodesMap(final Network<N, E> var0) {
      Function var1 = new Function<E, EndpointPair<N>>() {
         public EndpointPair<N> apply(E var1) {
            return var0.incidentNodes(var1);
         }
      };
      return Maps.asMap(var0.edges(), var1);
   }
}
