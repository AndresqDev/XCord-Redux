package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

class CopyOnWriteSortedArrayThreadContextMap implements CopyOnWrite, ObjectThreadContextMap, ReadOnlyThreadContextMap {
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
   protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";
   private static final StringMap EMPTY_CONTEXT_DATA = new SortedArrayStringMap(1);
   private static volatile int initialCapacity;
   private static volatile boolean inheritableMap;
   private final ThreadLocal<StringMap> localMap = this.createThreadLocalMap();

   static void init() {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      initialCapacity = var0.getIntegerProperty("log4j2.ThreadContext.initial.capacity", 16);
      inheritableMap = var0.getBooleanProperty("isThreadContextMapInheritable");
   }

   public CopyOnWriteSortedArrayThreadContextMap() {
   }

   private ThreadLocal<StringMap> createThreadLocalMap() {
      return (ThreadLocal<StringMap>)(inheritableMap ? new InheritableThreadLocal<StringMap>() {
         protected StringMap childValue(StringMap var1) {
            if (var1 == null) {
               return null;
            } else {
               StringMap var2 = CopyOnWriteSortedArrayThreadContextMap.this.createStringMap(var1);
               var2.freeze();
               return var2;
            }
         }
      } : new ThreadLocal<>());
   }

   protected StringMap createStringMap() {
      return new SortedArrayStringMap(initialCapacity);
   }

   protected StringMap createStringMap(ReadOnlyStringMap var1) {
      return new SortedArrayStringMap(var1);
   }

   @Override
   public void put(String var1, String var2) {
      this.putValue(var1, var2);
   }

   @Override
   public void putValue(String var1, Object var2) {
      StringMap var3 = this.localMap.get();
      var3 = var3 == null ? this.createStringMap() : this.createStringMap(var3);
      var3.putValue(var1, var2);
      var3.freeze();
      this.localMap.set(var3);
   }

   @Override
   public void putAll(Map<String, String> var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringMap var2 = this.localMap.get();
         var2 = var2 == null ? this.createStringMap() : this.createStringMap(var2);

         for (Entry var4 : var1.entrySet()) {
            var2.putValue((String)var4.getKey(), var4.getValue());
         }

         var2.freeze();
         this.localMap.set(var2);
      }
   }

   @Override
   public <V> void putAllValues(Map<String, V> var1) {
      if (var1 != null && !var1.isEmpty()) {
         StringMap var2 = this.localMap.get();
         var2 = var2 == null ? this.createStringMap() : this.createStringMap(var2);

         for (Entry var4 : var1.entrySet()) {
            var2.putValue((String)var4.getKey(), var4.getValue());
         }

         var2.freeze();
         this.localMap.set(var2);
      }
   }

   @Override
   public String get(String var1) {
      return this.getValue(var1);
   }

   @Override
   public <V> V getValue(String var1) {
      StringMap var2 = this.localMap.get();
      return var2 == null ? null : var2.getValue(var1);
   }

   @Override
   public void remove(String var1) {
      StringMap var2 = this.localMap.get();
      if (var2 != null) {
         StringMap var3 = this.createStringMap(var2);
         var3.remove(var1);
         var3.freeze();
         this.localMap.set(var3);
      }
   }

   @Override
   public void removeAll(Iterable<String> var1) {
      StringMap var2 = this.localMap.get();
      if (var2 != null) {
         StringMap var3 = this.createStringMap(var2);

         for (String var5 : var1) {
            var3.remove(var5);
         }

         var3.freeze();
         this.localMap.set(var3);
      }
   }

   @Override
   public void clear() {
      this.localMap.remove();
   }

   @Override
   public boolean containsKey(String var1) {
      StringMap var2 = this.localMap.get();
      return var2 != null && var2.containsKey(var1);
   }

   @Override
   public Map<String, String> getCopy() {
      StringMap var1 = this.localMap.get();
      return (Map<String, String>)(var1 == null ? new HashMap<>() : var1.toMap());
   }

   @Override
   public StringMap getReadOnlyContextData() {
      StringMap var1 = this.localMap.get();
      return var1 == null ? EMPTY_CONTEXT_DATA : var1;
   }

   @Override
   public Map<String, String> getImmutableMapOrNull() {
      StringMap var1 = this.localMap.get();
      return var1 == null ? null : Collections.unmodifiableMap(var1.toMap());
   }

   @Override
   public boolean isEmpty() {
      StringMap var1 = this.localMap.get();
      return var1 == null || var1.isEmpty();
   }

   @Override
   public String toString() {
      StringMap var1 = this.localMap.get();
      return var1 == null ? "{}" : var1.toString();
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      byte var2 = 1;
      StringMap var3 = this.localMap.get();
      return 31 * var2 + (var3 == null ? 0 : var3.hashCode());
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof ThreadContextMap)) {
         return false;
      } else {
         ThreadContextMap var2 = (ThreadContextMap)var1;
         Map var3 = this.getImmutableMapOrNull();
         Map var4 = var2.getImmutableMapOrNull();
         return Objects.equals(var3, var4);
      }
   }

   static {
      EMPTY_CONTEXT_DATA.freeze();
      init();
   }
}
