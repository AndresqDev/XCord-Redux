package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MapField<K, V> implements MutabilityOracle {
   private volatile boolean isMutable;
   private volatile MapField.StorageMode mode;
   private MapField.MutatabilityAwareMap<K, V> mapData;
   private List<Message> listData;
   private final MapField.Converter<K, V> converter;

   private MapField(MapField.Converter<K, V> var1, MapField.StorageMode var2, Map<K, V> var3) {
      this.converter = var1;
      this.isMutable = true;
      this.mode = var2;
      this.mapData = new MapField.MutatabilityAwareMap<>(this, var3);
      this.listData = null;
   }

   private MapField(MapEntry<K, V> var1, MapField.StorageMode var2, Map<K, V> var3) {
      this(new MapField.ImmutableMessageConverter<>(var1), var2, var3);
   }

   public static <K, V> MapField<K, V> emptyMapField(MapEntry<K, V> var0) {
      return new MapField<>(var0, MapField.StorageMode.MAP, Collections.emptyMap());
   }

   public static <K, V> MapField<K, V> newMapField(MapEntry<K, V> var0) {
      return new MapField<>(var0, MapField.StorageMode.MAP, new LinkedHashMap<>());
   }

   private Message convertKeyAndValueToMessage(K var1, V var2) {
      return this.converter.convertKeyAndValueToMessage((K)var1, (V)var2);
   }

   private void convertMessageToKeyAndValue(Message var1, Map<K, V> var2) {
      this.converter.convertMessageToKeyAndValue(var1, var2);
   }

   private List<Message> convertMapToList(MapField.MutatabilityAwareMap<K, V> var1) {
      ArrayList var2 = new ArrayList();

      for (Entry var4 : var1.entrySet()) {
         var2.add(this.convertKeyAndValueToMessage((K)var4.getKey(), (V)var4.getValue()));
      }

      return var2;
   }

   private MapField.MutatabilityAwareMap<K, V> convertListToMap(List<Message> var1) {
      LinkedHashMap var2 = new LinkedHashMap();

      for (Message var4 : var1) {
         this.convertMessageToKeyAndValue(var4, var2);
      }

      return new MapField.MutatabilityAwareMap<>(this, var2);
   }

   public Map<K, V> getMap() {
      if (this.mode == MapField.StorageMode.LIST) {
         synchronized (this) {
            if (this.mode == MapField.StorageMode.LIST) {
               this.mapData = this.convertListToMap(this.listData);
               this.mode = MapField.StorageMode.BOTH;
            }
         }
      }

      return Collections.unmodifiableMap(this.mapData);
   }

   public Map<K, V> getMutableMap() {
      if (this.mode != MapField.StorageMode.MAP) {
         if (this.mode == MapField.StorageMode.LIST) {
            this.mapData = this.convertListToMap(this.listData);
         }

         this.listData = null;
         this.mode = MapField.StorageMode.MAP;
      }

      return this.mapData;
   }

   public void mergeFrom(MapField<K, V> var1) {
      this.getMutableMap().putAll(MapFieldLite.copy(var1.getMap()));
   }

   public void clear() {
      this.mapData = new MapField.MutatabilityAwareMap<>(this, new LinkedHashMap<>());
      this.mode = MapField.StorageMode.MAP;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof MapField)) {
         return false;
      } else {
         MapField var2 = (MapField)var1;
         return MapFieldLite.equals(this.getMap(), var2.getMap());
      }
   }

   @Override
   public int hashCode() {
      return MapFieldLite.calculateHashCodeForMap(this.getMap());
   }

   public MapField<K, V> copy() {
      return new MapField<>(this.converter, MapField.StorageMode.MAP, MapFieldLite.copy(this.getMap()));
   }

   List<Message> getList() {
      if (this.mode == MapField.StorageMode.MAP) {
         synchronized (this) {
            if (this.mode == MapField.StorageMode.MAP) {
               this.listData = this.convertMapToList(this.mapData);
               this.mode = MapField.StorageMode.BOTH;
            }
         }
      }

      return Collections.unmodifiableList(this.listData);
   }

   List<Message> getMutableList() {
      if (this.mode != MapField.StorageMode.LIST) {
         if (this.mode == MapField.StorageMode.MAP) {
            this.listData = this.convertMapToList(this.mapData);
         }

         this.mapData = null;
         this.mode = MapField.StorageMode.LIST;
      }

      return this.listData;
   }

   Message getMapEntryMessageDefaultInstance() {
      return this.converter.getMessageDefaultInstance();
   }

   public void makeImmutable() {
      this.isMutable = false;
   }

   public boolean isMutable() {
      return this.isMutable;
   }

   @Override
   public void ensureMutable() {
      if (!this.isMutable()) {
         throw new UnsupportedOperationException();
      }
   }

   private interface Converter<K, V> {
      Message convertKeyAndValueToMessage(K var1, V var2);

      void convertMessageToKeyAndValue(Message var1, Map<K, V> var2);

      Message getMessageDefaultInstance();
   }

   private static class ImmutableMessageConverter<K, V> implements MapField.Converter<K, V> {
      private final MapEntry<K, V> defaultEntry;

      public ImmutableMessageConverter(MapEntry<K, V> var1) {
         this.defaultEntry = var1;
      }

      @Override
      public Message convertKeyAndValueToMessage(K var1, V var2) {
         return this.defaultEntry.newBuilderForType().setKey((K)var1).setValue((V)var2).buildPartial();
      }

      @Override
      public void convertMessageToKeyAndValue(Message var1, Map<K, V> var2) {
         MapEntry var3 = (MapEntry)var1;
         var2.put(var3.getKey(), var3.getValue());
      }

      @Override
      public Message getMessageDefaultInstance() {
         return this.defaultEntry;
      }
   }

   private static class MutatabilityAwareMap<K, V> implements Map<K, V> {
      private final MutabilityOracle mutabilityOracle;
      private final Map<K, V> delegate;

      MutatabilityAwareMap(MutabilityOracle var1, Map<K, V> var2) {
         this.mutabilityOracle = var1;
         this.delegate = var2;
      }

      @Override
      public int size() {
         return this.delegate.size();
      }

      @Override
      public boolean isEmpty() {
         return this.delegate.isEmpty();
      }

      @Override
      public boolean containsKey(Object var1) {
         return this.delegate.containsKey(var1);
      }

      @Override
      public boolean containsValue(Object var1) {
         return this.delegate.containsValue(var1);
      }

      @Override
      public V get(Object var1) {
         return this.delegate.get(var1);
      }

      @Override
      public V put(K var1, V var2) {
         this.mutabilityOracle.ensureMutable();
         Internal.checkNotNull(var1);
         Internal.checkNotNull(var2);
         return this.delegate.put((K)var1, (V)var2);
      }

      @Override
      public V remove(Object var1) {
         this.mutabilityOracle.ensureMutable();
         return this.delegate.remove(var1);
      }

      @Override
      public void putAll(Map<? extends K, ? extends V> var1) {
         this.mutabilityOracle.ensureMutable();

         for (Object var3 : var1.keySet()) {
            Internal.checkNotNull(var3);
            Internal.checkNotNull(var1.get(var3));
         }

         this.delegate.putAll(var1);
      }

      @Override
      public void clear() {
         this.mutabilityOracle.ensureMutable();
         this.delegate.clear();
      }

      @Override
      public Set<K> keySet() {
         return new MapField.MutatabilityAwareMap.MutatabilityAwareSet<>(this.mutabilityOracle, this.delegate.keySet());
      }

      @Override
      public Collection<V> values() {
         return new MapField.MutatabilityAwareMap.MutatabilityAwareCollection<>(this.mutabilityOracle, this.delegate.values());
      }

      @Override
      public Set<Entry<K, V>> entrySet() {
         return new MapField.MutatabilityAwareMap.MutatabilityAwareSet<>(this.mutabilityOracle, this.delegate.entrySet());
      }

      @Override
      public boolean equals(Object var1) {
         return this.delegate.equals(var1);
      }

      @Override
      public int hashCode() {
         return this.delegate.hashCode();
      }

      @Override
      public String toString() {
         return this.delegate.toString();
      }

      private static class MutatabilityAwareCollection<E> implements Collection<E> {
         private final MutabilityOracle mutabilityOracle;
         private final Collection<E> delegate;

         MutatabilityAwareCollection(MutabilityOracle var1, Collection<E> var2) {
            this.mutabilityOracle = var1;
            this.delegate = var2;
         }

         @Override
         public int size() {
            return this.delegate.size();
         }

         @Override
         public boolean isEmpty() {
            return this.delegate.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            return this.delegate.contains(var1);
         }

         @Override
         public Iterator<E> iterator() {
            return new MapField.MutatabilityAwareMap.MutatabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
         }

         @Override
         public Object[] toArray() {
            return this.delegate.toArray();
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])this.delegate.toArray(var1);
         }

         @Override
         public boolean add(E var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean remove(Object var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(var1);
         }

         @Override
         public boolean containsAll(Collection<?> var1) {
            return this.delegate.containsAll(var1);
         }

         @Override
         public boolean addAll(Collection<? extends E> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.removeAll(var1);
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.retainAll(var1);
         }

         @Override
         public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
         }

         @Override
         public boolean equals(Object var1) {
            return this.delegate.equals(var1);
         }

         @Override
         public int hashCode() {
            return this.delegate.hashCode();
         }

         @Override
         public String toString() {
            return this.delegate.toString();
         }
      }

      private static class MutatabilityAwareIterator<E> implements Iterator<E> {
         private final MutabilityOracle mutabilityOracle;
         private final Iterator<E> delegate;

         MutatabilityAwareIterator(MutabilityOracle var1, Iterator<E> var2) {
            this.mutabilityOracle = var1;
            this.delegate = var2;
         }

         @Override
         public boolean hasNext() {
            return this.delegate.hasNext();
         }

         @Override
         public E next() {
            return this.delegate.next();
         }

         @Override
         public void remove() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.remove();
         }

         @Override
         public boolean equals(Object var1) {
            return this.delegate.equals(var1);
         }

         @Override
         public int hashCode() {
            return this.delegate.hashCode();
         }

         @Override
         public String toString() {
            return this.delegate.toString();
         }
      }

      private static class MutatabilityAwareSet<E> implements Set<E> {
         private final MutabilityOracle mutabilityOracle;
         private final Set<E> delegate;

         MutatabilityAwareSet(MutabilityOracle var1, Set<E> var2) {
            this.mutabilityOracle = var1;
            this.delegate = var2;
         }

         @Override
         public int size() {
            return this.delegate.size();
         }

         @Override
         public boolean isEmpty() {
            return this.delegate.isEmpty();
         }

         @Override
         public boolean contains(Object var1) {
            return this.delegate.contains(var1);
         }

         @Override
         public Iterator<E> iterator() {
            return new MapField.MutatabilityAwareMap.MutatabilityAwareIterator<>(this.mutabilityOracle, this.delegate.iterator());
         }

         @Override
         public Object[] toArray() {
            return this.delegate.toArray();
         }

         @Override
         public <T> T[] toArray(T[] var1) {
            return (T[])this.delegate.toArray(var1);
         }

         @Override
         public boolean add(E var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.add((E)var1);
         }

         @Override
         public boolean remove(Object var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.remove(var1);
         }

         @Override
         public boolean containsAll(Collection<?> var1) {
            return this.delegate.containsAll(var1);
         }

         @Override
         public boolean addAll(Collection<? extends E> var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.addAll(var1);
         }

         @Override
         public boolean retainAll(Collection<?> var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.retainAll(var1);
         }

         @Override
         public boolean removeAll(Collection<?> var1) {
            this.mutabilityOracle.ensureMutable();
            return this.delegate.removeAll(var1);
         }

         @Override
         public void clear() {
            this.mutabilityOracle.ensureMutable();
            this.delegate.clear();
         }

         @Override
         public boolean equals(Object var1) {
            return this.delegate.equals(var1);
         }

         @Override
         public int hashCode() {
            return this.delegate.hashCode();
         }

         @Override
         public String toString() {
            return this.delegate.toString();
         }
      }
   }

   private static enum StorageMode {
      MAP,
      LIST,
      BOTH;
   }
}
