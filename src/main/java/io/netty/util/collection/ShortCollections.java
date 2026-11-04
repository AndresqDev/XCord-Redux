package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ShortCollections {
   private static final ShortObjectMap<Object> EMPTY_MAP = new ShortCollections.EmptyMap();

   private ShortCollections() {
   }

   public static <V> ShortObjectMap<V> emptyMap() {
      return (ShortObjectMap<V>)EMPTY_MAP;
   }

   public static <V> ShortObjectMap<V> unmodifiableMap(ShortObjectMap<V> var0) {
      return new ShortCollections.UnmodifiableMap<>(var0);
   }

   private static final class EmptyMap implements ShortObjectMap<Object> {
      private EmptyMap() {
      }

      @Override
      public Object get(short var1) {
         return null;
      }

      @Override
      public Object put(short var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public Object remove(short var1) {
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
      public Set<Short> keySet() {
         return Collections.emptySet();
      }

      @Override
      public boolean containsKey(short var1) {
         return false;
      }

      @Override
      public boolean containsValue(Object var1) {
         return false;
      }

      @Override
      public Iterable<ShortObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      @Override
      public Object get(Object var1) {
         return null;
      }

      public Object put(Short var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object remove(Object var1) {
         return null;
      }

      @Override
      public void putAll(Map<? extends Short, ?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<Object> values() {
         return Collections.emptyList();
      }

      @Override
      public Set<Entry<Short, Object>> entrySet() {
         return Collections.emptySet();
      }
   }

   private static final class UnmodifiableMap<V> implements ShortObjectMap<V> {
      private final ShortObjectMap<V> map;
      private Set<Short> keySet;
      private Set<Entry<Short, V>> entrySet;
      private Collection<V> values;
      private Iterable<ShortObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(ShortObjectMap<V> var1) {
         this.map = var1;
      }

      @Override
      public V get(short var1) {
         return this.map.get(var1);
      }

      @Override
      public V put(short var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(short var1) {
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
      public boolean containsKey(short var1) {
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

      public V put(Short var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public void putAll(Map<? extends Short, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      @Override
      public Iterable<ShortObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<ShortObjectMap.PrimitiveEntry<V>>() {
               @Override
               public Iterator<ShortObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      @Override
      public Set<Short> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      @Override
      public Set<Entry<Short, V>> entrySet() {
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

      private class EntryImpl implements ShortObjectMap.PrimitiveEntry<V> {
         private final ShortObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(ShortObjectMap.PrimitiveEntry<V> var2) {
            this.entry = var2;
         }

         @Override
         public short key() {
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

      private class IteratorImpl implements Iterator<ShortObjectMap.PrimitiveEntry<V>> {
         final Iterator<ShortObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<ShortObjectMap.PrimitiveEntry<V>> var2) {
            this.iter = var2;
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public ShortObjectMap.PrimitiveEntry<V> next() {
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
