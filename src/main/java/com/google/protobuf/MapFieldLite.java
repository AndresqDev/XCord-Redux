package com.google.protobuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class MapFieldLite<K, V> extends LinkedHashMap<K, V> {
   private boolean isMutable = true;
   private static final MapFieldLite EMPTY_MAP_FIELD = new MapFieldLite();

   private MapFieldLite() {
   }

   private MapFieldLite(Map<K, V> var1) {
      super(var1);
   }

   public static <K, V> MapFieldLite<K, V> emptyMapField() {
      return EMPTY_MAP_FIELD;
   }

   public void mergeFrom(MapFieldLite<K, V> var1) {
      this.ensureMutable();
      if (!var1.isEmpty()) {
         this.putAll(var1);
      }
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return this.isEmpty() ? Collections.emptySet() : super.entrySet();
   }

   @Override
   public void clear() {
      this.ensureMutable();
      super.clear();
   }

   @Override
   public V put(K var1, V var2) {
      this.ensureMutable();
      Internal.checkNotNull(var1);
      Internal.checkNotNull(var2);
      return super.put((K)var1, (V)var2);
   }

   public V put(Entry<K, V> var1) {
      return this.put((K)var1.getKey(), (V)var1.getValue());
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> var1) {
      this.ensureMutable();
      checkForNullKeysAndValues(var1);
      super.putAll(var1);
   }

   @Override
   public V remove(Object var1) {
      this.ensureMutable();
      return super.remove(var1);
   }

   private static void checkForNullKeysAndValues(Map<?, ?> var0) {
      for (Object var2 : var0.keySet()) {
         Internal.checkNotNull(var2);
         Internal.checkNotNull(var0.get(var2));
      }
   }

   private static boolean equals(Object var0, Object var1) {
      return var0 instanceof byte[] && var1 instanceof byte[] ? Arrays.equals((byte[])var0, (byte[])var1) : var0.equals(var1);
   }

   static <K, V> boolean equals(Map<K, V> var0, Map<K, V> var1) {
      if (var0 == var1) {
         return true;
      } else if (var0.size() != var1.size()) {
         return false;
      } else {
         for (Entry var3 : var0.entrySet()) {
            if (!var1.containsKey(var3.getKey())) {
               return false;
            }

            if (!equals(var3.getValue(), var1.get(var3.getKey()))) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Map && equals(this, (Map<K, V>)var1);
   }

   private static int calculateHashCodeForObject(Object var0) {
      if (var0 instanceof byte[]) {
         return Internal.hashCode((byte[])var0);
      } else if (var0 instanceof Internal.EnumLite) {
         throw new UnsupportedOperationException();
      } else {
         return var0.hashCode();
      }
   }

   static <K, V> int calculateHashCodeForMap(Map<K, V> var0) {
      int var1 = 0;

      for (Entry var3 : var0.entrySet()) {
         var1 += calculateHashCodeForObject(var3.getKey()) ^ calculateHashCodeForObject(var3.getValue());
      }

      return var1;
   }

   @Override
   public int hashCode() {
      return calculateHashCodeForMap(this);
   }

   private static Object copy(Object var0) {
      if (var0 instanceof byte[]) {
         byte[] var1 = (byte[])var0;
         return Arrays.copyOf(var1, var1.length);
      } else {
         return var0;
      }
   }

   static <K, V> Map<K, V> copy(Map<K, V> var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Entry var3 : var0.entrySet()) {
         var1.put(var3.getKey(), copy(var3.getValue()));
      }

      return var1;
   }

   public MapFieldLite<K, V> mutableCopy() {
      return this.isEmpty() ? new MapFieldLite<>() : new MapFieldLite<>(this);
   }

   public void makeImmutable() {
      this.isMutable = false;
   }

   public boolean isMutable() {
      return this.isMutable;
   }

   private void ensureMutable() {
      if (!this.isMutable()) {
         throw new UnsupportedOperationException();
      }
   }

   static {
      EMPTY_MAP_FIELD.makeImmutable();
   }
}
