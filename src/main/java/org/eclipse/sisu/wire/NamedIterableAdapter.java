package org.eclipse.sisu.wire;

import com.google.inject.name.Named;
import java.util.Iterator;
import java.util.Map.Entry;

final class NamedIterableAdapter<V> implements Iterable<Entry<String, V>> {
   private final Iterable<Entry<Named, V>> delegate;

   NamedIterableAdapter(Iterable<Entry<Named, V>> var1) {
      this.delegate = var1;
   }

   @Override
   public Iterator<Entry<String, V>> iterator() {
      return new NamedIterableAdapter.NamedIterator<>(this.delegate);
   }

   private static final class NamedEntry<V> implements Entry<String, V> {
      private final Entry<Named, V> entry;

      NamedEntry(Entry<Named, V> var1) {
         this.entry = var1;
      }

      public String getKey() {
         return this.entry.getKey().value();
      }

      @Override
      public V getValue() {
         return this.entry.getValue();
      }

      @Override
      public V setValue(V var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }

   private static final class NamedIterator<V> implements Iterator<Entry<String, V>> {
      private final Iterator<Entry<Named, V>> iterator;

      NamedIterator(Iterable<Entry<Named, V>> var1) {
         this.iterator = var1.iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Entry<String, V> next() {
         return new NamedIterableAdapter.NamedEntry<>(this.iterator.next());
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
