package io.netty.util.internal;

import java.util.Iterator;

public final class ReadOnlyIterator<T> implements Iterator<T> {
   private final Iterator<? extends T> iterator;

   public ReadOnlyIterator(Iterator<? extends T> var1) {
      this.iterator = ObjectUtil.checkNotNull(var1, "iterator");
   }

   @Override
   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   @Override
   public T next() {
      return (T)this.iterator.next();
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("read-only");
   }
}
