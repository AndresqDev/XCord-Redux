package com.google.common.graph;

import com.google.common.annotations.Beta;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class AbstractGraph<N> extends AbstractBaseGraph<N> implements Graph<N> {
   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Graph)) {
         return false;
      } else {
         Graph var2 = (Graph)var1;
         return this.isDirected() == var2.isDirected() && this.nodes().equals(var2.nodes()) && this.edges().equals(var2.edges());
      }
   }

   @Override
   public final int hashCode() {
      return this.edges().hashCode();
   }

   @Override
   public String toString() {
      boolean var1 = this.isDirected();
      boolean var2 = this.allowsSelfLoops();
      String var3 = String.valueOf(this.nodes());
      String var4 = String.valueOf(this.edges());
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
}
