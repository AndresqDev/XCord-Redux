package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class Defaults {
   private static final Double DOUBLE_DEFAULT = 0.0;
   private static final Float FLOAT_DEFAULT = 0.0F;

   private Defaults() {
   }

   public static <T> T defaultValue(Class<T> var0) {
      Preconditions.checkNotNull(var0);
      if (var0.isPrimitive()) {
         if (var0 == boolean.class) {
            return (T)Boolean.FALSE;
         }

         if (var0 == char.class) {
            return (T)'\u0000';
         }

         if (var0 == byte.class) {
            return (T)(byte)0;
         }

         if (var0 == short.class) {
            return (T)(short)0;
         }

         if (var0 == int.class) {
            return (T)0;
         }

         if (var0 == long.class) {
            return (T)0L;
         }

         if (var0 == float.class) {
            return (T)FLOAT_DEFAULT;
         }

         if (var0 == double.class) {
            return (T)DOUBLE_DEFAULT;
         }
      }

      return null;
   }
}
