package org.eclipse.sisu.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Weak {
   private Weak() {
   }

   public static <T> Collection<T> elements() {
      return elements(10);
   }

   public static <T> Collection<T> elements(int var0) {
      return new MildElements<>(new ArrayList<>(var0), false);
   }

   public static <K, V> Map<K, V> keys() {
      return keys(16);
   }

   public static <K, V> Map<K, V> keys(int var0) {
      return new MildKeys<>(new HashMap<>(var0), false);
   }

   public static <K, V> ConcurrentMap<K, V> concurrentKeys() {
      return concurrentKeys(16, 4);
   }

   public static <K, V> ConcurrentMap<K, V> concurrentKeys(int var0, int var1) {
      return new MildConcurrentKeys<>(new ConcurrentHashMap<>(var0, 0.75F, var1), false);
   }

   public static <K, V> Map<K, V> values() {
      return values(16);
   }

   public static <K, V> Map<K, V> values(int var0) {
      return new MildValues<>(new HashMap<>(var0), false);
   }

   public static <K, V> ConcurrentMap<K, V> concurrentValues() {
      return concurrentValues(16, 4);
   }

   public static <K, V> ConcurrentMap<K, V> concurrentValues(int var0, int var1) {
      return new MildConcurrentValues<>(new ConcurrentHashMap<>(var0, 0.75F, var1), false);
   }
}
