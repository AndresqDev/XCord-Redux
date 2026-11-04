package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSet<E> extends ForwardingCollection<E> implements Set<E> {
   protected ForwardingSet() {
   }

   protected abstract Set<E> delegate();

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }

   @Override
   protected boolean standardRemoveAll(Collection<?> var1) {
      return Sets.removeAllImpl(this, Preconditions.checkNotNull(var1));
   }

   protected boolean standardEquals(Object var1) {
      return Sets.equalsImpl(this, var1);
   }

   protected int standardHashCode() {
      return Sets.hashCodeImpl(this);
   }
}
