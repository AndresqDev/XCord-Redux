package com.google.gson.internal;

import java.lang.reflect.Type;

public final class Primitives {
   private Primitives() {
   }

   public static boolean isPrimitive(Type var0) {
      return var0 instanceof Class && ((Class)var0).isPrimitive();
   }

   public static boolean isWrapperType(Type var0) {
      return var0 == Integer.class
         || var0 == Float.class
         || var0 == Byte.class
         || var0 == Double.class
         || var0 == Long.class
         || var0 == Character.class
         || var0 == Boolean.class
         || var0 == Short.class
         || var0 == Void.class;
   }

   public static <T> Class<T> wrap(Class<T> var0) {
      if (var0 == int.class) {
         return (Class<T>)Integer.class;
      } else if (var0 == float.class) {
         return (Class<T>)Float.class;
      } else if (var0 == byte.class) {
         return (Class<T>)Byte.class;
      } else if (var0 == double.class) {
         return (Class<T>)Double.class;
      } else if (var0 == long.class) {
         return (Class<T>)Long.class;
      } else if (var0 == char.class) {
         return (Class<T>)Character.class;
      } else if (var0 == boolean.class) {
         return (Class<T>)Boolean.class;
      } else if (var0 == short.class) {
         return (Class<T>)Short.class;
      } else {
         return (Class<T>)(var0 == void.class ? Void.class : var0);
      }
   }

   public static <T> Class<T> unwrap(Class<T> var0) {
      if (var0 == Integer.class) {
         return (Class<T>)int.class;
      } else if (var0 == Float.class) {
         return (Class<T>)float.class;
      } else if (var0 == Byte.class) {
         return (Class<T>)byte.class;
      } else if (var0 == Double.class) {
         return (Class<T>)double.class;
      } else if (var0 == Long.class) {
         return (Class<T>)long.class;
      } else if (var0 == Character.class) {
         return (Class<T>)char.class;
      } else if (var0 == Boolean.class) {
         return (Class<T>)boolean.class;
      } else if (var0 == Short.class) {
         return (Class<T>)short.class;
      } else {
         return (Class<T>)(var0 == Void.class ? void.class : var0);
      }
   }
}
