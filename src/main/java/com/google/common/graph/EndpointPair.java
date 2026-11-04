package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.Immutable;

@Immutable(
   containerOf = {"N"}
)
@ElementTypesAreNonnullByDefault
@Beta
public abstract class EndpointPair<N> implements Iterable<N> {
   private final N nodeU;
   private final N nodeV;

   private EndpointPair(N var1, N var2) {
      this.nodeU = Preconditions.checkNotNull((N)var1);
      this.nodeV = Preconditions.checkNotNull((N)var2);
   }

   public static <N> EndpointPair<N> ordered(N var0, N var1) {
      return new EndpointPair.Ordered<>(var0, var1);
   }

   public static <N> EndpointPair<N> unordered(N var0, N var1) {
      return new EndpointPair.Unordered<>(var1, var0);
   }

   static <N> EndpointPair<N> of(Graph<?> var0, N var1, N var2) {
      return var0.isDirected() ? ordered((N)var1, (N)var2) : unordered((N)var1, (N)var2);
   }

   static <N> EndpointPair<N> of(Network<?, ?> var0, N var1, N var2) {
      return var0.isDirected() ? ordered((N)var1, (N)var2) : unordered((N)var1, (N)var2);
   }

   public abstract N source();

   public abstract N target();

   public final N nodeU() {
      return this.nodeU;
   }

   public final N nodeV() {
      return this.nodeV;
   }

   public final N adjacentNode(N var1) {
      if (var1.equals(this.nodeU)) {
         return this.nodeV;
      } else if (var1.equals(this.nodeV)) {
         return this.nodeU;
      } else {
         String var2 = String.valueOf(this);
         String var3 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(36 + String.valueOf(var2).length() + String.valueOf(var3).length())
               .append("EndpointPair ")
               .append(var2)
               .append(" does not contain node ")
               .append(var3)
               .toString()
         );
      }
   }

   public abstract boolean isOrdered();

   public final UnmodifiableIterator<N> iterator() {
      return Iterators.forArray(this.nodeU, this.nodeV);
   }

   @Override
   public abstract boolean equals(Object var1);

   @Override
   public abstract int hashCode();

   private static final class Ordered<N> extends EndpointPair<N> {
      private Ordered(N var1, N var2) {
         super(var1, var2);
      }

      @Override
      public N source() {
         return this.nodeU();
      }

      @Override
      public N target() {
         return this.nodeV();
      }

      @Override
      public boolean isOrdered() {
         return true;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof EndpointPair)) {
            return false;
         } else {
            EndpointPair var2 = (EndpointPair)var1;
            return this.isOrdered() != var2.isOrdered() ? false : this.source().equals(var2.source()) && this.target().equals(var2.target());
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.source(), this.target());
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.source());
         String var2 = String.valueOf(this.target());
         return new StringBuilder(6 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("<")
            .append(var1)
            .append(" -> ")
            .append(var2)
            .append(">")
            .toString();
      }
   }

   private static final class Unordered<N> extends EndpointPair<N> {
      private Unordered(N var1, N var2) {
         super(var1, var2);
      }

      @Override
      public N source() {
         throw new UnsupportedOperationException(
            "Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't."
         );
      }

      @Override
      public N target() {
         throw new UnsupportedOperationException(
            "Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't."
         );
      }

      @Override
      public boolean isOrdered() {
         return false;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof EndpointPair)) {
            return false;
         } else {
            EndpointPair var2 = (EndpointPair)var1;
            if (this.isOrdered() != var2.isOrdered()) {
               return false;
            } else {
               return this.nodeU().equals(var2.nodeU())
                  ? this.nodeV().equals(var2.nodeV())
                  : this.nodeU().equals(var2.nodeV()) && this.nodeV().equals(var2.nodeU());
            }
         }
      }

      @Override
      public int hashCode() {
         return this.nodeU().hashCode() + this.nodeV().hashCode();
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.nodeU());
         String var2 = String.valueOf(this.nodeV());
         return new StringBuilder(4 + String.valueOf(var1).length() + String.valueOf(var2).length())
            .append("[")
            .append(var1)
            .append(", ")
            .append(var2)
            .append("]")
            .toString();
      }
   }
}
