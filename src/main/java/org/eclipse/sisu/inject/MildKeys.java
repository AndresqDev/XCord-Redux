package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

class MildKeys<K, V> implements Map<K, V> {
   final ReferenceQueue<K> queue = new ReferenceQueue<>();
   final Map<Reference<K>, V> map;
   private final boolean soft;

   MildKeys(Map<Reference<K>, V> var1, boolean var2) {
      this.map = var1;
      this.soft = var2;
   }

   @Override
   public final boolean containsKey(Object var1) {
      return this.map.containsKey(tempKey(var1));
   }

   @Override
   public final boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   @Override
   public final V get(Object var1) {
      return this.map.get(tempKey(var1));
   }

   @Override
   public final V put(K var1, V var2) {
      this.compact();
      return this.map.put(this.mildKey((K)var1), (V)var2);
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> var1) {
      this.compact();

      for (Entry var2 : var1.entrySet()) {
         this.map.put(this.mildKey((K)var2.getKey()), (V)var2.getValue());
      }
   }

   @Override
   public final V remove(Object var1) {
      this.compact();
      return this.map.remove(tempKey(var1));
   }

   @Override
   public final void clear() {
      this.map.clear();
      this.compact();
   }

   @Override
   public final boolean isEmpty() {
      this.compact();
      return this.map.isEmpty();
   }

   @Override
   public final int size() {
      this.compact();
      return this.map.size();
   }

   @Override
   public final Set<K> keySet() {
      this.compact();
      return new AbstractSet<K>() {
         @Override
         public Iterator<K> iterator() {
            return MildKeys.this.new KeyItr();
         }

         @Override
         public int size() {
            return MildKeys.this.map.size();
         }
      };
   }

   @Override
   public final Collection<V> values() {
      this.compact();
      return this.map.values();
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      this.compact();
      return new AbstractSet<Entry<K, V>>() {
         @Override
         public Iterator<Entry<K, V>> iterator() {
            return MildKeys.this.new EntryItr();
         }

         @Override
         public int size() {
            return MildKeys.this.map.size();
         }
      };
   }

   final Reference<K> mildKey(K var1) {
      return (Reference<K>)(this.soft ? new MildKeys.Soft<>((K)var1, this.queue) : new MildKeys.Weak<>((K)var1, this.queue));
   }

   static final <K> Reference<K> tempKey(K var0) {
      return new MildKeys.Weak<>((K)var0, null);
   }

   final void compact() {
      Reference var1;
      while ((var1 = this.queue.poll()) != null) {
         this.map.remove(var1);
      }
   }

   final class EntryItr implements Iterator<Entry<K, V>> {
      private final Iterator<Entry<Reference<K>, V>> itr = MildKeys.this.map.entrySet().iterator();
      private Entry<Reference<K>, V> nextEntry;
      private K nextKey;

      @Override
      public boolean hasNext() {
         while (this.nextKey == null && this.itr.hasNext()) {
            this.nextEntry = this.itr.next();
            this.nextKey = this.nextEntry.getKey().get();
         }

         return this.nextKey != null;
      }

      public Entry<K, V> next() {
         if (this.hasNext()) {
            MildKeys.StrongEntry var1 = MildKeys.this.new StrongEntry(this.nextEntry, this.nextKey);
            this.nextEntry = null;
            this.nextKey = null;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         this.itr.remove();
      }
   }

   final class KeyItr implements Iterator<K> {
      private final Iterator<Reference<K>> itr = MildKeys.this.map.keySet().iterator();
      private K nextKey;

      @Override
      public boolean hasNext() {
         while (this.nextKey == null && this.itr.hasNext()) {
            this.nextKey = this.itr.next().get();
         }

         return this.nextKey != null;
      }

      @Override
      public K next() {
         if (this.hasNext()) {
            Object var1 = this.nextKey;
            this.nextKey = null;
            return (K)var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         this.itr.remove();
      }
   }

   static class Soft<T> extends SoftReference<T> {
      private final int hash;

      Soft(T var1, ReferenceQueue<T> var2) {
         super((T)var1, var2);
         this.hash = var1.hashCode();
      }

      @Override
      public final int hashCode() {
         return this.hash;
      }

      @Override
      public final boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Object var2 = this.get();
            return var2 == null || !(var1 instanceof Reference) ? false : var2 == ((Reference)var1).get();
         }
      }
   }

   final class StrongEntry implements Entry<K, V> {
      private final Entry<Reference<K>, V> entry;
      private final K key;

      StrongEntry(Entry<Reference<K>, V> var2, K var3) {
         this.entry = var2;
         this.key = (K)var3;
      }

      @Override
      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.entry.getValue();
      }

      @Override
      public V setValue(V var1) {
         return this.entry.setValue((V)var1);
      }
   }

   static class Weak<T> extends WeakReference<T> {
      private final int hash;

      Weak(T var1, ReferenceQueue<T> var2) {
         super((T)var1, var2);
         this.hash = var1.hashCode();
      }

      @Override
      public final int hashCode() {
         return this.hash;
      }

      @Override
      public final boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            Object var2 = this.get();
            return var2 == null || !(var1 instanceof Reference) ? false : var2 == ((Reference)var1).get();
         }
      }
   }
}
