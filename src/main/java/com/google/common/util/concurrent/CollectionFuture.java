package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class CollectionFuture<V, C> extends AggregateFuture<V, C> {
   private List<CollectionFuture.Present<V>> values;

   CollectionFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> var1, boolean var2) {
      super(var1, var2, true);
      Object var3 = var1.isEmpty() ? Collections.emptyList() : Lists.newArrayListWithCapacity(var1.size());

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3.add(null);
      }

      this.values = (List<CollectionFuture.Present<V>>)var3;
   }

   @Override
   final void collectOneValue(int var1, @ParametricNullness V var2) {
      List var3 = this.values;
      if (var3 != null) {
         var3.set(var1, new CollectionFuture.Present<>(var2));
      }
   }

   @Override
   final void handleAllCompleted() {
      List var1 = this.values;
      if (var1 != null) {
         this.set(this.combine(var1));
      }
   }

   @Override
   void releaseResources(AggregateFuture.ReleaseResourcesReason var1) {
      super.releaseResources(var1);
      this.values = null;
   }

   abstract C combine(List<CollectionFuture.Present<V>> var1);

   static final class ListFuture<V> extends CollectionFuture<V, List<V>> {
      ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> var1, boolean var2) {
         super(var1, var2);
         this.init();
      }

      public List<V> combine(List<CollectionFuture.Present<V>> var1) {
         ArrayList var2 = Lists.newArrayListWithCapacity(var1.size());

         for (CollectionFuture.Present var4 : var1) {
            var2.add(var4 != null ? var4.value : null);
         }

         return Collections.unmodifiableList(var2);
      }
   }

   private static final class Present<V> {
      V value;

      Present(V var1) {
         this.value = (V)var1;
      }
   }
}
