package com.google.common.primitives;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Primitives {
   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
   private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

   private Primitives() {
   }

   private static void add(Map<Class<?>, Class<?>> var0, Map<Class<?>, Class<?>> var1, Class<?> var2, Class<?> var3) {
      var0.put(var2, var3);
      var1.put(var3, var2);
   }

   public static Set<Class<?>> allPrimitiveTypes() {
      return PRIMITIVE_TO_WRAPPER_TYPE.keySet();
   }

   public static Set<Class<?>> allWrapperTypes() {
      return WRAPPER_TO_PRIMITIVE_TYPE.keySet();
   }

   public static boolean isWrapperType(Class<?> var0) {
      return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Preconditions.checkNotNull(var0));
   }

   public static <T> Class<T> wrap(Class<T> var0) {
      Preconditions.checkNotNull(var0);
      Class var1 = PRIMITIVE_TO_WRAPPER_TYPE.get(var0);
      return var1 == null ? var0 : var1;
   }

   public static <T> Class<T> unwrap(Class<T> var0) {
      Preconditions.checkNotNull(var0);
      Class var1 = WRAPPER_TO_PRIMITIVE_TYPE.get(var0);
      return var1 == null ? var0 : var1;
   }

   static {
      LinkedHashMap var0 = new LinkedHashMap(16);
      LinkedHashMap var1 = new LinkedHashMap(16);
      add(var0, var1, boolean.class, Boolean.class);
      add(var0, var1, byte.class, Byte.class);
      add(var0, var1, char.class, Character.class);
      add(var0, var1, double.class, Double.class);
      add(var0, var1, float.class, Float.class);
      add(var0, var1, int.class, Integer.class);
      add(var0, var1, long.class, Long.class);
      add(var0, var1, short.class, Short.class);
      add(var0, var1, void.class, Void.class);
      PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(var0);
      WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(var1);
   }
}
