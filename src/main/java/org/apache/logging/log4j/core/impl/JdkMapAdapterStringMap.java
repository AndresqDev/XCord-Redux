package org.apache.logging.log4j.core.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.TriConsumer;

public class JdkMapAdapterStringMap implements StringMap {
   private static final long serialVersionUID = -7348247784983193612L;
   private static final String FROZEN = "Frozen collection cannot be modified";
   private static final Comparator<? super String> NULL_FIRST_COMPARATOR = (var0, var1) -> {
      if (var0 == null) {
         return -1;
      } else {
         return var1 == null ? 1 : var0.compareTo(var1);
      }
   };
   private final Map<String, String> map;
   private boolean immutable = false;
   private transient String[] sortedKeys;
   private static TriConsumer<String, String, Map<String, String>> PUT_ALL = (var0, var1, var2) -> {
      String var10000 = var2.put(var0, var1);
   };

   public JdkMapAdapterStringMap() {
      this(new HashMap<>());
   }

   public JdkMapAdapterStringMap(Map<String, String> var1) {
      this.map = Objects.requireNonNull(var1, "map");
   }

   @Override
   public Map<String, String> toMap() {
      return this.map;
   }

   private void assertNotFrozen() {
      if (this.immutable) {
         throw new UnsupportedOperationException("Frozen collection cannot be modified");
      }
   }

   @Override
   public boolean containsKey(String var1) {
      return this.map.containsKey(var1);
   }

   @Override
   public <V> void forEach(BiConsumer<String, ? super V> var1) {
      String[] var2 = this.getSortedKeys();

      for (int var3 = 0; var3 < var2.length; var3++) {
         var1.accept(var2[var3], this.map.get(var2[var3]));
      }
   }

   @Override
   public <V, S> void forEach(TriConsumer<String, ? super V, S> var1, S var2) {
      String[] var3 = this.getSortedKeys();

      for (int var4 = 0; var4 < var3.length; var4++) {
         var1.accept(var3[var4], this.map.get(var3[var4]), var2);
      }
   }

   private String[] getSortedKeys() {
      if (this.sortedKeys == null) {
         this.sortedKeys = this.map.keySet().toArray(new String[this.map.size()]);
         Arrays.sort(this.sortedKeys, NULL_FIRST_COMPARATOR);
      }

      return this.sortedKeys;
   }

   @Override
   public <V> V getValue(String var1) {
      return (V)this.map.get(var1);
   }

   @Override
   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   @Override
   public int size() {
      return this.map.size();
   }

   @Override
   public void clear() {
      if (!this.map.isEmpty()) {
         this.assertNotFrozen();
         this.map.clear();
         this.sortedKeys = null;
      }
   }

   @Override
   public void freeze() {
      this.immutable = true;
   }

   @Override
   public boolean isFrozen() {
      return this.immutable;
   }

   @Override
   public void putAll(ReadOnlyStringMap var1) {
      this.assertNotFrozen();
      var1.forEach(PUT_ALL, this.map);
      this.sortedKeys = null;
   }

   @Override
   public void putValue(String var1, Object var2) {
      this.assertNotFrozen();
      this.map.put(var1, var2 == null ? null : String.valueOf(var2));
      this.sortedKeys = null;
   }

   @Override
   public void remove(String var1) {
      if (this.map.containsKey(var1)) {
         this.assertNotFrozen();
         this.map.remove(var1);
         this.sortedKeys = null;
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(this.map.size() * 13);
      var1.append('{');
      String[] var2 = this.getSortedKeys();

      for (int var3 = 0; var3 < var2.length; var3++) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(var2[var3]).append('=').append(this.map.get(var2[var3]));
      }

      var1.append('}');
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof JdkMapAdapterStringMap)) {
         return false;
      } else {
         JdkMapAdapterStringMap var2 = (JdkMapAdapterStringMap)var1;
         return this.map.equals(var2.map) && this.immutable == var2.immutable;
      }
   }

   @Override
   public int hashCode() {
      return this.map.hashCode() + (this.immutable ? 31 : 0);
   }
}
