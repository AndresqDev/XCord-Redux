package org.apache.logging.log4j.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.TriConsumer;

public class DefaultThreadContextMap implements ThreadContextMap, ReadOnlyStringMap {
   private static final long serialVersionUID = 8218007901108944053L;
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   private final boolean useMap;
   private final ThreadLocal<Map<String, String>> localMap;
   private static boolean inheritableMap;

   static ThreadLocal<Map<String, String>> createThreadLocalMap(final boolean var0) {
      return (ThreadLocal<Map<String, String>>)(inheritableMap ? new InheritableThreadLocal<Map<String, String>>() {
         protected Map<String, String> childValue(Map<String, String> var1) {
            return var1 != null && var0 ? Collections.unmodifiableMap(new HashMap<>(var1)) : null;
         }
      } : new ThreadLocal<>());
   }

   static void init() {
      inheritableMap = PropertiesUtil.getProperties().getBooleanProperty("isThreadContextMapInheritable");
   }

   public DefaultThreadContextMap() {
      this(true);
   }

   public DefaultThreadContextMap(boolean var1) {
      this.useMap = var1;
      this.localMap = createThreadLocalMap(var1);
   }

   @Override
   public void put(String var1, String var2) {
      if (this.useMap) {
         Map var3 = this.localMap.get();
         HashMap var4 = var3 == null ? new HashMap(1) : new HashMap(var3);
         var4.put(var1, var2);
         this.localMap.set(Collections.unmodifiableMap(var4));
      }
   }

   public void putAll(Map<String, String> var1) {
      if (this.useMap) {
         Map var2 = this.localMap.get();
         HashMap var5 = var2 == null ? new HashMap(var1.size()) : new HashMap(var2);

         for (Entry var4 : var1.entrySet()) {
            var5.put(var4.getKey(), var4.getValue());
         }

         this.localMap.set(Collections.unmodifiableMap(var5));
      }
   }

   @Override
   public String get(String var1) {
      Map var2 = this.localMap.get();
      return var2 == null ? null : (String)var2.get(var1);
   }

   @Override
   public void remove(String var1) {
      Map var2 = this.localMap.get();
      if (var2 != null) {
         HashMap var3 = new HashMap(var2);
         var3.remove(var1);
         this.localMap.set(Collections.unmodifiableMap(var3));
      }
   }

   public void removeAll(Iterable<String> var1) {
      Map var2 = this.localMap.get();
      if (var2 != null) {
         HashMap var3 = new HashMap(var2);

         for (String var5 : var1) {
            var3.remove(var5);
         }

         this.localMap.set(Collections.unmodifiableMap(var3));
      }
   }

   @Override
   public void clear() {
      this.localMap.remove();
   }

   @Override
   public Map<String, String> toMap() {
      return this.getCopy();
   }

   @Override
   public boolean containsKey(String var1) {
      Map var2 = this.localMap.get();
      return var2 != null && var2.containsKey(var1);
   }

   @Override
   public <V> void forEach(BiConsumer<String, ? super V> var1) {
      Map var2 = this.localMap.get();
      if (var2 != null) {
         for (Entry var4 : var2.entrySet()) {
            Object var5 = var4.getValue();
            var1.accept(var4.getKey(), var5);
         }
      }
   }

   @Override
   public <V, S> void forEach(TriConsumer<String, ? super V, S> var1, S var2) {
      Map var3 = this.localMap.get();
      if (var3 != null) {
         for (Entry var5 : var3.entrySet()) {
            Object var6 = var5.getValue();
            var1.accept(var5.getKey(), var6, var2);
         }
      }
   }

   @Override
   public <V> V getValue(String var1) {
      Map var2 = this.localMap.get();
      return (V)(var2 == null ? null : (String)var2.get(var1));
   }

   @Override
   public Map<String, String> getCopy() {
      Map var1 = this.localMap.get();
      return var1 == null ? new HashMap<>() : new HashMap<>(var1);
   }

   @Override
   public Map<String, String> getImmutableMapOrNull() {
      return this.localMap.get();
   }

   @Override
   public boolean isEmpty() {
      Map var1 = this.localMap.get();
      return var1 == null || var1.isEmpty();
   }

   @Override
   public int size() {
      Map var1 = this.localMap.get();
      return var1 == null ? 0 : var1.size();
   }

   @Override
   public String toString() {
      Map var1 = this.localMap.get();
      return var1 == null ? "{}" : var1.toString();
   }

   @Override
   public int hashCode() {
      byte var1 = 31;
      int var2 = 1;
      Map var3 = this.localMap.get();
      var2 = 31 * var2 + (var3 == null ? 0 : var3.hashCode());
      return 31 * var2 + Boolean.valueOf(this.useMap).hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         if (var1 instanceof DefaultThreadContextMap) {
            DefaultThreadContextMap var2 = (DefaultThreadContextMap)var1;
            if (this.useMap != var2.useMap) {
               return false;
            }
         }

         if (!(var1 instanceof ThreadContextMap)) {
            return false;
         } else {
            ThreadContextMap var5 = (ThreadContextMap)var1;
            Map var3 = this.localMap.get();
            Map var4 = var5.getImmutableMapOrNull();
            return Objects.equals(var3, var4);
         }
      }
   }

   static {
      init();
   }
}
