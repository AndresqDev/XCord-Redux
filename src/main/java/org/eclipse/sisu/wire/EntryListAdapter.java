package org.eclipse.sisu.wire;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public final class EntryListAdapter<V> extends AbstractSequentialList<V> {
   private final Iterable<? extends Entry<?, V>> iterable;

   public EntryListAdapter(Iterable<? extends Entry<?, V>> var1) {
      this.iterable = var1;
   }

   @Override
   public Iterator<V> iterator() {
      return new EntryListAdapter.ValueIterator<>(this.iterable);
   }

   @Override
   public ListIterator<V> listIterator(int var1) {
      return new EntryListAdapter.ValueListIterator<>(this.iterable, var1);
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

   private static final class ValueListIterator<V> implements ListIterator<V> {
      private final Iterator<? extends Entry<?, V>> iterator;
      private final List<Entry<?, V>> entryCache = new ArrayList<>();
      private int index;

      ValueListIterator(Iterable<? extends Entry<?, V>> var1, int var2) {
         if (var2 < 0) {
            throw new IndexOutOfBoundsException();
         } else {
            this.iterator = var1.iterator();

            try {
               while (this.index < var2) {
                  this.next();
               }
            } catch (NoSuchElementException var3) {
               throw new IndexOutOfBoundsException();
            }
         }
      }

      @Override
      public boolean hasNext() {
         return this.index < this.entryCache.size() || this.iterator.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.index > 0;
      }

      @Override
      public V next() {
         if (this.index >= this.entryCache.size()) {
            this.entryCache.add((Entry<?, V>)this.iterator.next());
         }

         return this.entryCache.get(this.index++).getValue();
      }

      @Override
      public V previous() {
         if (this.index <= 0) {
            throw new NoSuchElementException();
         } else {
            return this.entryCache.get(--this.index).getValue();
         }
      }

      @Override
      public int nextIndex() {
         return this.index;
      }

      @Override
      public int previousIndex() {
         return this.index - 1;
      }

      @Override
      public void add(V var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void set(V var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
