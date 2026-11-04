package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class IntCollections {
   private static final IntObjectMap<Object> EMPTY_MAP = new IntCollections.EmptyMap();

   private IntCollections() {
   }

   public static <V> IntObjectMap<V> emptyMap() {
      return (IntObjectMap<V>)EMPTY_MAP;
   }

   public static <V> IntObjectMap<V> unmodifiableMap(IntObjectMap<V> var0) {
      return new IntCollections.UnmodifiableMap<>(var0);
   }

   private static final class EmptyMap implements IntObjectMap<Object> {
      private EmptyMap() {
      }

      @Override
      public Object get(int var1) {
         return null;
      }

      @Override
      public Object put(int var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public Object remove(int var1) {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }

      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public boolean containsKey(Object var1) {
         return false;
      }

      @Override
      public void clear() {
      }

      @Override
      public Set<Integer> keySet() {
         return Collections.emptySet();
      }

      @Override
      public boolean containsKey(int var1) {
         return false;
      }

      @Override
      public boolean containsValue(Object var1) {
         return false;
      }

      @Override
      public Iterable<IntObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      @Override
      public Object get(Object var1) {
         return null;
      }

      public Object put(Integer var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object remove(Object var1) {
         return null;
      }

      @Override
      public void putAll(Map<? extends Integer, ?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<Object> values() {
         return Collections.emptyList();
      }

      @Override
      public Set<Entry<Integer, Object>> entrySet() {
         return Collections.emptySet();
      }
   }

   private static final class UnmodifiableMap<V> implements IntObjectMap<V> {
      private final IntObjectMap<V> map;
      private Set<Integer> keySet;
      private Set<Entry<Integer, V>> entrySet;
      private Collection<V> values;
      private Iterable<IntObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(IntObjectMap<V> var1) {
         this.map = var1;
      }

      @Override
      public V get(int var1) {
         return this.map.get(var1);
      }

      @Override
      public V put(int var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(int var1) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public int size() {
         return this.map.size();
      }

      @Override
      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      @Override
      public void clear() {
         throw new UnsupportedOperationException("clear");
      }

      @Override
      public boolean containsKey(int var1) {
         return this.map.containsKey(var1);
      }

      @Override
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.map.containsKey(var1);
      }

      @Override
      public V get(Object var1) {
         return this.map.get(var1);
      }

      public V put(Integer var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public void putAll(Map<? extends Integer, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      @Override
      public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>() {
               @Override
               public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      @Override
      public Set<Integer> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      @Override
      public Set<Entry<Integer, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = Collections.unmodifiableSet(this.map.entrySet());
         }

         return this.entrySet;
      }

      @Override
      public Collection<V> values() {
         if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.map.values());
         }

         return this.values;
      }

      private class EntryImpl implements IntObjectMap.PrimitiveEntry<V> {
         private final IntObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(IntObjectMap.PrimitiveEntry<V> var2) {
            this.entry = var2;
         }

         @Override
         public int key() {
            return this.entry.key();
         }

         @Override
         public V value() {
            return this.entry.value();
         }

         @Override
         public void setValue(V var1) {
            throw new UnsupportedOperationException("setValue");
         }
      }

      private class IteratorImpl implements Iterator<IntObjectMap.PrimitiveEntry<V>> {
         final Iterator<IntObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<IntObjectMap.PrimitiveEntry<V>> var2) {
            this.iter = var2;
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public IntObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return UnmodifiableMap.this.new EntryImpl(this.iter.next());
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      }
   }
}
