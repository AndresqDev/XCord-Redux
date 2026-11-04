package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@ElementTypesAreNonnullByDefault
@Beta
public final class ValueGraphBuilder<N, V> extends AbstractGraphBuilder<N> {
   private ValueGraphBuilder(boolean var1) {
      super(var1);
   }

   public static ValueGraphBuilder<Object, Object> directed() {
      return new ValueGraphBuilder<>(true);
   }

   public static ValueGraphBuilder<Object, Object> undirected() {
      return new ValueGraphBuilder<>(false);
   }

   public static <N, V> ValueGraphBuilder<N, V> from(ValueGraph<N, V> var0) {
      return new ValueGraphBuilder(var0.isDirected())
         .allowsSelfLoops(var0.allowsSelfLoops())
         .nodeOrder(var0.nodeOrder())
         .incidentEdgeOrder(var0.incidentEdgeOrder());
   }

   public <N1 extends N, V1 extends V> ImmutableValueGraph.Builder<N1, V1> immutable() {
      ValueGraphBuilder var1 = this.cast();
      return new ImmutableValueGraph.Builder<>(var1);
   }

   public ValueGraphBuilder<N, V> allowsSelfLoops(boolean var1) {
      this.allowsSelfLoops = var1;
      return this;
   }

   public ValueGraphBuilder<N, V> expectedNodeCount(int var1) {
      this.expectedNodeCount = Optional.of(Graphs.checkNonNegative(var1));
      return this;
   }

   public <N1 extends N> ValueGraphBuilder<N1, V> nodeOrder(ElementOrder<N1> var1) {
      ValueGraphBuilder var2 = this.cast();
      var2.nodeOrder = Preconditions.checkNotNull(var1);
      return var2;
   }

   public <N1 extends N> ValueGraphBuilder<N1, V> incidentEdgeOrder(ElementOrder<N1> var1) {
      Preconditions.checkArgument(
         var1.type() == ElementOrder.Type.UNORDERED || var1.type() == ElementOrder.Type.STABLE,
         "The given elementOrder (%s) is unsupported. incidentEdgeOrder() only supports ElementOrder.unordered() and ElementOrder.stable().",
         var1
      );
      ValueGraphBuilder var2 = this.cast();
      var2.incidentEdgeOrder = Preconditions.checkNotNull(var1);
      return var2;
   }

   public <N1 extends N, V1 extends V> MutableValueGraph<N1, V1> build() {
      return new StandardMutableValueGraph<>(this);
   }

   ValueGraphBuilder<N, V> copy() {
      ValueGraphBuilder var1 = new ValueGraphBuilder(this.directed);
      var1.allowsSelfLoops = this.allowsSelfLoops;
      var1.nodeOrder = this.nodeOrder;
      var1.expectedNodeCount = this.expectedNodeCount;
      var1.incidentEdgeOrder = this.incidentEdgeOrder;
      return var1;
   }

   private <N1 extends N, V1 extends V> ValueGraphBuilder<N1, V1> cast() {
      return this;
   }
}
