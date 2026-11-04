package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

class MildValues<K, V> implements Map<K, V> {
   final ReferenceQueue<V> queue = new ReferenceQueue<>();
   final Map<K, Reference<V>> map;
   private final boolean soft;

   MildValues(Map<K, Reference<V>> var1, boolean var2) {
      this.map = var1;
      this.soft = var2;
   }

   @Override
   public final boolean containsKey(Object var1) {
      return this.map.containsKey(var1);
   }

   @Override
   public final boolean containsValue(Object var1) {
      return this.map.containsValue(tempValue(var1));
   }

   @Override
   public final V get(Object var1) {
      Reference var2 = this.map.get(var1);
      return (V)(var2 != null ? var2.get() : null);
   }

   @Override
   public final V put(K var1, V var2) {
      this.compact();
      Reference var3 = this.map.put((K)var1, this.mildValue((K)var1, (V)var2));
      return (V)(var3 != null ? var3.get() : null);
   }

   @Override
   public final void putAll(Map<? extends K, ? extends V> var1) {
      this.compact();

      for (Entry var2 : var1.entrySet()) {
         this.map.put((K)var2.getKey(), this.mildValue((K)var2.getKey(), (V)var2.getValue()));
      }
   }

   @Override
   public final V remove(Object var1) {
      this.compact();
      Reference var2 = this.map.remove(var1);
      return (V)(var2 != null ? var2.get() : null);
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
      return this.map.keySet();
   }

   @Override
   public final Collection<V> values() {
      this.compact();
      return new AbstractCollection<V>() {
         @Override
         public Iterator<V> iterator() {
            return MildValues.this.new ValueItr();
         }

         @Override
         public int size() {
            return MildValues.this.map.size();
         }
      };
   }

   @Override
   public final Set<Entry<K, V>> entrySet() {
      this.compact();
      return new AbstractSet<Entry<K, V>>() {
         @Override
         public Iterator<Entry<K, V>> iterator() {
            return MildValues.this.new EntryItr();
         }

         @Override
         public int size() {
            return MildValues.this.map.size();
         }
      };
   }

   final Reference<V> mildValue(K var1, V var2) {
      return (Reference<V>)(this.soft ? new MildValues.Soft<>(var1, (V)var2, this.queue) : new MildValues.Weak<>(var1, (V)var2, this.queue));
   }

   static final <V> Reference<V> tempValue(V var0) {
      return new MildValues.Weak<>(null, (V)var0, null);
   }

   void compact() {
      Reference var1;
      while ((var1 = this.queue.poll()) != null) {
         Object var2 = ((MildValues.InverseMapping)var1).key();
         if (this.map.get(var2) == var1) {
            this.map.remove(var2);
         }
      }
   }

   final class EntryItr implements Iterator<Entry<K, V>> {
      private final Iterator<Entry<K, Reference<V>>> itr = MildValues.this.map.entrySet().iterator();
      private Entry<K, Reference<V>> nextEntry;
      private V nextValue;

      @Override
      public boolean hasNext() {
         while (this.nextValue == null && this.itr.hasNext()) {
            this.nextEntry = this.itr.next();
            this.nextValue = this.nextEntry.getValue().get();
         }

         return this.nextValue != null;
      }

      public Entry<K, V> next() {
         if (this.hasNext()) {
            MildValues.StrongEntry var1 = MildValues.this.new StrongEntry(this.nextEntry, this.nextValue);
            this.nextEntry = null;
            this.nextValue = null;
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

   interface InverseMapping {
      Object key();
   }

   private static final class Soft<K, V> extends MildKeys.Soft<V> implements MildValues.InverseMapping {
      private final K key;

      Soft(K var1, V var2, ReferenceQueue<V> var3) {
         super((V)var2, var3);
         this.key = (K)var1;
      }

      @Override
      public Object key() {
         return this.key;
      }
   }

   final class StrongEntry implements Entry<K, V> {
      private final Entry<K, Reference<V>> entry;
      private V value;

      StrongEntry(Entry<K, Reference<V>> var2, V var3) {
         this.entry = var2;
         this.value = (V)var3;
      }

      @Override
      public K getKey() {
         return this.entry.getKey();
      }

      @Override
      public V getValue() {
         return this.value;
      }

      @Override
      public V setValue(V var1) {
         Object var2 = this.value;
         this.entry.setValue(MildValues.this.mildValue((K)this.getKey(), (V)var1));
         this.value = (V)var1;
         return (V)var2;
      }
   }

   final class ValueItr implements Iterator<V> {
      private final Iterator<Reference<V>> itr = MildValues.this.map.values().iterator();
      private V nextValue;

      @Override
      public boolean hasNext() {
         while (this.nextValue == null && this.itr.hasNext()) {
            this.nextValue = this.itr.next().get();
         }

         return this.nextValue != null;
      }

      @Override
      public V next() {
         if (this.hasNext()) {
            Object var1 = this.nextValue;
            this.nextValue = null;
            return (V)var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         this.itr.remove();
      }
   }

   private static final class Weak<K, V> extends MildKeys.Weak<V> implements MildValues.InverseMapping {
      private final K key;

      Weak(K var1, V var2, ReferenceQueue<V> var3) {
         super((V)var2, var3);
         this.key = (K)var1;
      }

      @Override
      public Object key() {
         return this.key;
      }
   }
}
