package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractValueGraph<N, V> extends AbstractBaseGraph<N> implements ValueGraph<N, V> {
   @Override
   public Graph<N> asGraph() {
      return new AbstractGraph<N>() {
         @Override
         public Set<N> nodes() {
            return AbstractValueGraph.this.nodes();
         }

         @Override
         public Set<EndpointPair<N>> edges() {
            return AbstractValueGraph.this.edges();
         }

         @Override
         public boolean isDirected() {
            return AbstractValueGraph.this.isDirected();
         }

         @Override
         public boolean allowsSelfLoops() {
            return AbstractValueGraph.this.allowsSelfLoops();
         }

         @Override
         public ElementOrder<N> nodeOrder() {
            return AbstractValueGraph.this.nodeOrder();
         }

         @Override
         public ElementOrder<N> incidentEdgeOrder() {
            return AbstractValueGraph.this.incidentEdgeOrder();
         }

         @Override
         public Set<N> adjacentNodes(N var1) {
            return (Set<N>)AbstractValueGraph.this.adjacentNodes(var1);
         }

         @Override
         public Set<N> predecessors(N var1) {
            return (Set<N>)AbstractValueGraph.this.predecessors(var1);
         }

         @Override
         public Set<N> successors(N var1) {
            return (Set<N>)AbstractValueGraph.this.successors(var1);
         }

         @Override
         public int degree(N var1) {
            return AbstractValueGraph.this.degree(var1);
         }

         @Override
         public int inDegree(N var1) {
            return AbstractValueGraph.this.inDegree(var1);
         }

         @Override
         public int outDegree(N var1) {
            return AbstractValueGraph.this.outDegree(var1);
         }
      };
   }

   @Override
   public Optional<V> edgeValue(N var1, N var2) {
      return Optional.ofNullable(this.edgeValueOrDefault((N)var1, (N)var2, null));
   }

   @Override
   public Optional<V> edgeValue(EndpointPair<N> var1) {
      return Optional.ofNullable(this.edgeValueOrDefault(var1, null));
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ValueGraph)) {
         return false;
      } else {
         ValueGraph var2 = (ValueGraph)var1;
         return this.isDirected() == var2.isDirected() && this.nodes().equals(var2.nodes()) && edgeValueMap(this).equals(edgeValueMap(var2));
      }
   }

   @Override
   public final int hashCode() {
      return edgeValueMap(this).hashCode();
   }

   @Override
   public String toString() {
      boolean var1 = this.isDirected();
      boolean var2 = this.allowsSelfLoops();
      String var3 = String.valueOf(this.nodes());
      String var4 = String.valueOf(edgeValueMap(this));
      return new StringBuilder(59 + String.valueOf(var3).length() + String.valueOf(var4).length())
         .append("isDirected: ")
         .append(var1)
         .append(", allowsSelfLoops: ")
         .append(var2)
         .append(", nodes: ")
         .append(var3)
         .append(", edges: ")
         .append(var4)
         .toString();
   }

   private static <N, V> Map<EndpointPair<N>, V> edgeValueMap(final ValueGraph<N, V> var0) {
      Function var1 = new Function<EndpointPair<N>, V>() {
         public V apply(EndpointPair<N> var1) {
            return Objects.requireNonNull((V)var0.edgeValueOrDefault(var1.nodeU(), var1.nodeV(), null));
         }
      };
      return Maps.asMap(var0.edges(), var1);
   }
}
