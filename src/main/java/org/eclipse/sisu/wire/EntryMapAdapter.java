package org.eclipse.sisu.wire;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public final class EntryMapAdapter<K, V> extends AbstractMap<K, V> {
   private final Set<Entry<K, V>> entrySet;

   public EntryMapAdapter(Iterable<? extends Entry<K, V>> var1) {
      this.entrySet = new EntryMapAdapter.EntrySet<>(var1);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.entrySet;
   }

   @Override
   public boolean isEmpty() {
      return this.entrySet.isEmpty();
   }

   private static final class EntrySet<K, V> extends AbstractSet<Entry<K, V>> {
      private final Iterable<Entry<K, V>> iterable;

      EntrySet(Iterable<? extends Entry<K, V>> var1) {
         this.iterable = var1;
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         return this.iterable.iterator();
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
   }
}
