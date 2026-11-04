package org.eclipse.sisu.wire;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

public final class EntrySetAdapter<V> extends AbstractSet<V> {
   private final Iterable<? extends Entry<?, V>> iterable;

   public EntrySetAdapter(Iterable<? extends Entry<?, V>> var1) {
      this.iterable = var1;
   }

   @Override
   public Iterator<V> iterator() {
      return new EntrySetAdapter.ValueIterator<>(this.iterable);
   }

   @Override
   public boolean isEmpty() {
      return !this.iterator().hasNext();
   }

   @Override
   public int size() {
      int var1 = 0;
      Iterator var2 = this.iterable.iterator();

      while (var2.hasNext()) {
         var1++;
         var2.next();
      }

      return var1;
   }

   private static final class ValueIterator<V> implements Iterator<V> {
      private final Iterator<? extends Entry<?, V>> iterator;

      ValueIterator(Iterable<? extends Entry<?, V>> var1) {
         this.iterator = var1.iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Override
      public V next() {
         return this.iterator.next().getValue();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
