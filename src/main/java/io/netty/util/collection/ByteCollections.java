package io.netty.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public final class ByteCollections {
   private static final ByteObjectMap<Object> EMPTY_MAP = new ByteCollections.EmptyMap();

   private ByteCollections() {
   }

   public static <V> ByteObjectMap<V> emptyMap() {
      return (ByteObjectMap<V>)EMPTY_MAP;
   }

   public static <V> ByteObjectMap<V> unmodifiableMap(ByteObjectMap<V> var0) {
      return new ByteCollections.UnmodifiableMap<>(var0);
   }

   private static final class EmptyMap implements ByteObjectMap<Object> {
      private EmptyMap() {
      }

      @Override
      public Object get(byte var1) {
         return null;
      }

      @Override
      public Object put(byte var1, Object var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public Object remove(byte var1) {
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
      public Set<Byte> keySet() {
         return Collections.emptySet();
      }

      @Override
      public boolean containsKey(byte var1) {
         return false;
      }

      @Override
      public boolean containsValue(Object var1) {
         return false;
      }

      @Override
      public Iterable<ByteObjectMap.PrimitiveEntry<Object>> entries() {
         return Collections.emptySet();
      }

      @Override
      public Object get(Object var1) {
         return null;
      }

      public Object put(Byte var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object remove(Object var1) {
         return null;
      }

      @Override
      public void putAll(Map<? extends Byte, ?> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Collection<Object> values() {
         return Collections.emptyList();
      }

      @Override
      public Set<Entry<Byte, Object>> entrySet() {
         return Collections.emptySet();
      }
   }

   private static final class UnmodifiableMap<V> implements ByteObjectMap<V> {
      private final ByteObjectMap<V> map;
      private Set<Byte> keySet;
      private Set<Entry<Byte, V>> entrySet;
      private Collection<V> values;
      private Iterable<ByteObjectMap.PrimitiveEntry<V>> entries;

      UnmodifiableMap(ByteObjectMap<V> var1) {
         this.map = var1;
      }

      @Override
      public V get(byte var1) {
         return this.map.get(var1);
      }

      @Override
      public V put(byte var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(byte var1) {
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
      public boolean containsKey(byte var1) {
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

      public V put(Byte var1, V var2) {
         throw new UnsupportedOperationException("put");
      }

      @Override
      public V remove(Object var1) {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public void putAll(Map<? extends Byte, ? extends V> var1) {
         throw new UnsupportedOperationException("putAll");
      }

      @Override
      public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
         if (this.entries == null) {
            this.entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>() {
               @Override
               public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
                  return UnmodifiableMap.this.new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
               }
            };
         }

         return this.entries;
      }

      @Override
      public Set<Byte> keySet() {
         if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.map.keySet());
         }

         return this.keySet;
      }

      @Override
      public Set<Entry<Byte, V>> entrySet() {
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

      private class EntryImpl implements ByteObjectMap.PrimitiveEntry<V> {
         private final ByteObjectMap.PrimitiveEntry<V> entry;

         EntryImpl(ByteObjectMap.PrimitiveEntry<V> var2) {
            this.entry = var2;
         }

         @Override
         public byte key() {
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

      private class IteratorImpl implements Iterator<ByteObjectMap.PrimitiveEntry<V>> {
         final Iterator<ByteObjectMap.PrimitiveEntry<V>> iter;

         IteratorImpl(Iterator<ByteObjectMap.PrimitiveEntry<V>> var2) {
            this.iter = var2;
         }

         @Override
         public boolean hasNext() {
            return this.iter.hasNext();
         }

         public ByteObjectMap.PrimitiveEntry<V> next() {
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
