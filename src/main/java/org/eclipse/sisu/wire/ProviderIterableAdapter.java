package org.eclipse.sisu.wire;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map.Entry;
import org.eclipse.sisu.BeanEntry;
import xcord.Provider;

final class ProviderIterableAdapter<K extends Annotation, V> implements Iterable<Entry<K, Provider<V>>> {
   private final Iterable<BeanEntry<K, V>> delegate;

   ProviderIterableAdapter(Iterable<BeanEntry<K, V>> var1) {
      this.delegate = var1;
   }

   @Override
   public Iterator<Entry<K, Provider<V>>> iterator() {
      return new ProviderIterableAdapter.ProviderIterator<>(this.delegate);
   }

   private static final class ProviderEntry<K extends Annotation, V> implements Entry<K, Provider<V>> {
      private final BeanEntry<K, V> entry;

      ProviderEntry(BeanEntry<K, V> var1) {
         this.entry = var1;
      }

      public K getKey() {
         return this.entry.getKey();
      }

      public Provider<V> getValue() {
         return this.entry.getProvider();
      }

      public Provider<V> setValue(Provider<V> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }

   private static final class ProviderIterator<K extends Annotation, V> implements Iterator<Entry<K, Provider<V>>> {
      private final Iterator<BeanEntry<K, V>> iterator;

      ProviderIterator(Iterable<BeanEntry<K, V>> var1) {
         this.iterator = var1.iterator();
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Entry<K, Provider<V>> next() {
         return new ProviderIterableAdapter.ProviderEntry<>(this.iterator.next());
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
