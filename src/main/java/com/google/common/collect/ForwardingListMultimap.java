package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V> {
   protected ForwardingListMultimap() {
   }

   protected abstract ListMultimap<K, V> delegate();

   @Override
   public List<V> get(@ParametricNullness K var1) {
      return this.delegate().get((K)var1);
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> removeAll(Object var1) {
      return this.delegate().removeAll(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2) {
      return this.delegate().replaceValues((K)var1, var2);
   }
}
