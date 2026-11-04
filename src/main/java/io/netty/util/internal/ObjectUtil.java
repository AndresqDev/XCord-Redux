package io.netty.util.internal;

import java.util.Collection;
import java.util.Map;

public final class ObjectUtil {
   private static final float FLOAT_ZERO = 0.0F;
   private static final double DOUBLE_ZERO = 0.0;
   private static final long LONG_ZERO = 0L;
   private static final int INT_ZERO = 0;

   private ObjectUtil() {
   }

   public static <T> T checkNotNull(T var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1);
      } else {
         return (T)var0;
      }
   }

   public static <T> T[] deepCheckNotNull(String var0, T... var1) {
      if (var1 == null) {
         throw new NullPointerException(var0);
      } else {
         for (Object var5 : var1) {
            if (var5 == null) {
               throw new NullPointerException(var0);
            }
         }

         return (T[])var1;
      }
   }

   public static <T> T checkNotNullWithIAE(T var0, String var1) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be null");
      } else {
         return (T)var0;
      }
   }

   public static <T> T checkNotNullArrayParam(T var0, int var1, String var2) throws IllegalArgumentException {
      if (var0 == null) {
         throw new IllegalArgumentException("Array index " + var1 + " of parameter '" + var2 + "' must not be null");
      } else {
         return (T)var0;
      }
   }

   public static int checkPositive(int var0, String var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException(var1 + " : " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
   }

   public static long checkPositive(long var0, String var2) {
      if (var0 <= 0L) {
         throw new IllegalArgumentException(var2 + " : " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
   }

   public static double checkPositive(double var0, String var2) {
      if (var0 <= 0.0) {
         throw new IllegalArgumentException(var2 + " : " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
   }

   public static float checkPositive(float var0, String var1) {
      if (var0 <= 0.0F) {
         throw new IllegalArgumentException(var1 + " : " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
   }

   public static int checkPositiveOrZero(int var0, String var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException(var1 + " : " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static long checkPositiveOrZero(long var0, String var2) {
      if (var0 < 0L) {
         throw new IllegalArgumentException(var2 + " : " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static double checkPositiveOrZero(double var0, String var2) {
      if (var0 < 0.0) {
         throw new IllegalArgumentException(var2 + " : " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static float checkPositiveOrZero(float var0, String var1) {
      if (var0 < 0.0F) {
         throw new IllegalArgumentException(var1 + " : " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static int checkInRange(int var0, int var1, int var2, String var3) {
      if (var0 >= var1 && var0 <= var2) {
         return var0;
      } else {
         throw new IllegalArgumentException(var3 + ": " + var0 + " (expected: " + var1 + "-" + var2 + ")");
      }
   }

   public static long checkInRange(long var0, long var2, long var4, String var6) {
      if (var0 >= var2 && var0 <= var4) {
         return var0;
      } else {
         throw new IllegalArgumentException(var6 + ": " + var0 + " (expected: " + var2 + "-" + var4 + ")");
      }
   }

   public static <T> T[] checkNonEmpty(T[] var0, String var1) {
      if (checkNotNull(var0, var1).length == 0) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return (T[])var0;
      }
   }

   public static byte[] checkNonEmpty(byte[] var0, String var1) {
      if (checkNotNull(var0, var1).length == 0) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return var0;
      }
   }

   public static char[] checkNonEmpty(char[] var0, String var1) {
      if (checkNotNull(var0, var1).length == 0) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return var0;
      }
   }

   public static <T extends Collection<?>> T checkNonEmpty(T var0, String var1) {
      if (checkNotNull(var0, var1).isEmpty()) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return (T)var0;
      }
   }

   public static String checkNonEmpty(String var0, String var1) {
      if (checkNotNull(var0, var1).isEmpty()) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return var0;
      }
   }

   public static <K, V, T extends Map<K, V>> T checkNonEmpty(T var0, String var1) {
      if (checkNotNull(var0, var1).isEmpty()) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return (T)var0;
      }
   }

   public static CharSequence checkNonEmpty(CharSequence var0, String var1) {
      if (checkNotNull(var0, var1).length() == 0) {
         throw new IllegalArgumentException("Param '" + var1 + "' must not be empty");
      } else {
         return var0;
      }
   }

   public static String checkNonEmptyAfterTrim(String var0, String var1) {
      String var2 = checkNotNull(var0, var1).trim();
      return checkNonEmpty(var2, var1);
   }

   public static int intValue(Integer var0, int var1) {
      return var0 != null ? var0 : var1;
   }

   public static long longValue(Long var0, long var1) {
      return var0 != null ? var0 : var1;
   }
}
