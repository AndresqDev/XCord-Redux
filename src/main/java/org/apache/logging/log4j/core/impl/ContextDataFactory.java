package org.apache.logging.log4j.core.impl;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

public class ContextDataFactory {
   private static final String CLASS_NAME = PropertiesUtil.getProperties().getStringProperty("log4j2.ContextData");
   private static final Class<? extends StringMap> CACHED_CLASS = createCachedClass(CLASS_NAME);
   private static final Constructor<?> DEFAULT_CONSTRUCTOR = createDefaultConstructor(CACHED_CLASS);
   private static final Constructor<?> INITIAL_CAPACITY_CONSTRUCTOR = createInitialCapacityConstructor(CACHED_CLASS);
   private static final StringMap EMPTY_STRING_MAP = createContextData(0);

   private static Class<? extends StringMap> createCachedClass(String var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return Loader.loadClass(var0).asSubclass(IndexedStringMap.class);
         } catch (Exception var2) {
            return null;
         }
      }
   }

   private static Constructor<?> createDefaultConstructor(Class<? extends StringMap> var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return var0.getConstructor();
         } catch (IllegalAccessError | NoSuchMethodException var2) {
            return null;
         }
      }
   }

   private static Constructor<?> createInitialCapacityConstructor(Class<? extends StringMap> var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return var0.getConstructor(int.class);
         } catch (IllegalAccessError | NoSuchMethodException var2) {
            return null;
         }
      }
   }

   public static StringMap createContextData() {
      if (DEFAULT_CONSTRUCTOR == null) {
         return new SortedArrayStringMap();
      } else {
         try {
            return (IndexedStringMap)DEFAULT_CONSTRUCTOR.newInstance();
         } catch (Throwable var1) {
            return new SortedArrayStringMap();
         }
      }
   }

   public static StringMap createContextData(int var0) {
      if (INITIAL_CAPACITY_CONSTRUCTOR == null) {
         return new SortedArrayStringMap(var0);
      } else {
         try {
            return (IndexedStringMap)INITIAL_CAPACITY_CONSTRUCTOR.newInstance(var0);
         } catch (Throwable var2) {
            return new SortedArrayStringMap(var0);
         }
      }
   }

   public static StringMap createContextData(Map<String, String> var0) {
      StringMap var1 = createContextData(var0.size());

      for (Entry var3 : var0.entrySet()) {
         var1.putValue((String)var3.getKey(), var3.getValue());
      }

      return var1;
   }

   public static StringMap createContextData(ReadOnlyStringMap var0) {
      StringMap var1 = createContextData(var0.size());
      var1.putAll(var0);
      return var1;
   }

   public static StringMap emptyFrozenContextData() {
      return EMPTY_STRING_MAP;
   }

   static {
      EMPTY_STRING_MAP.freeze();
   }
}
