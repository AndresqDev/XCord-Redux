package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

public final class Classes {
   private static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<>(13);

   private Classes() {
      throw new UnsupportedOperationException();
   }

   public static String shortNameOf(String var0) {
      return var0.substring(var0.lastIndexOf(46) + 1);
   }

   public static <T> Class<T> wrapperOf(Class<T> var0) {
      return var0.isPrimitive() ? (Class)WRAPPERS.get(var0) : var0;
   }

   static {
      WRAPPERS.put(boolean.class, Boolean.class);
      WRAPPERS.put(byte.class, Byte.class);
      WRAPPERS.put(char.class, Character.class);
      WRAPPERS.put(double.class, Double.class);
      WRAPPERS.put(float.class, Float.class);
      WRAPPERS.put(int.class, Integer.class);
      WRAPPERS.put(long.class, Long.class);
      WRAPPERS.put(short.class, Short.class);
      WRAPPERS.put(void.class, Void.class);
   }
}
