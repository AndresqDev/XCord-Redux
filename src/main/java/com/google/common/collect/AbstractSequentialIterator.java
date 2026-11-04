package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T> {
   private T nextOrNull;

   protected AbstractSequentialIterator(T var1) {
      this.nextOrNull = (T)var1;
   }

   protected abstract T computeNext(T var1);

   @Override
   public final boolean hasNext() {
      return this.nextOrNull != null;
   }

   @Override
   public final T next() {
      if (this.nextOrNull == null) {
         throw new NoSuchElementException();
      } else {
         Object var1 = this.nextOrNull;
         this.nextOrNull = this.computeNext((T)var1);
         return (T)var1;
      }
   }
}
