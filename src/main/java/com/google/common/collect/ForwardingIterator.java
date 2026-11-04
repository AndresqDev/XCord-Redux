package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingIterator<T> extends ForwardingObject implements Iterator<T> {
   protected ForwardingIterator() {
   }

   protected abstract Iterator<T> delegate();

   @Override
   public boolean hasNext() {
      return this.delegate().hasNext();
   }

   @ParametricNullness
   @CanIgnoreReturnValue
   @Override
   public T next() {
      return this.delegate().next();
   }

   @Override
   public void remove() {
      this.delegate().remove();
   }
}
