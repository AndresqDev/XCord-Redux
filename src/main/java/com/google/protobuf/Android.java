package com.google.protobuf;

final class Android {
   private static final Class<?> MEMORY_CLASS = getClassForName("libcore.io.Memory");
   private static final boolean IS_ROBOLECTRIC = getClassForName("org.robolectric.Robolectric") != null;

   static boolean isOnAndroidDevice() {
      return MEMORY_CLASS != null && !IS_ROBOLECTRIC;
   }

   static Class<?> getMemoryClass() {
      return MEMORY_CLASS;
   }

   private static <T> Class<T> getClassForName(String var0) {
      try {
         return (Class<T>)Class.forName(var0);
      } catch (Throwable var2) {
         return null;
      }
   }
}
