package org.apache.http.util;

import java.util.Collection;

public class Args {
   public static void check(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalArgumentException(var1);
      }
   }

   public static void check(boolean var0, String var1, Object... var2) {
      if (!var0) {
         throw new IllegalArgumentException(String.format(var1, var2));
      }
   }

   public static void check(boolean var0, String var1, Object var2) {
      if (!var0) {
         throw new IllegalArgumentException(String.format(var1, var2));
      }
   }

   public static <T> T notNull(T var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1 + " may not be null");
      } else {
         return (T)var0;
      }
   }

   public static <T extends CharSequence> T notEmpty(T var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1 + " may not be null");
      } else if (TextUtils.isEmpty(var0)) {
         throw new IllegalArgumentException(var1 + " may not be empty");
      } else {
         return (T)var0;
      }
   }

   public static <T extends CharSequence> T notBlank(T var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1 + " may not be null");
      } else if (TextUtils.isBlank(var0)) {
         throw new IllegalArgumentException(var1 + " may not be blank");
      } else {
         return (T)var0;
      }
   }

   public static <T extends CharSequence> T containsNoBlanks(T var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1 + " may not be null");
      } else if (var0.length() == 0) {
         throw new IllegalArgumentException(var1 + " may not be empty");
      } else if (TextUtils.containsBlanks(var0)) {
         throw new IllegalArgumentException(var1 + " may not contain blanks");
      } else {
         return (T)var0;
      }
   }

   public static <E, T extends Collection<E>> T notEmpty(T var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1 + " may not be null");
      } else if (var0.isEmpty()) {
         throw new IllegalArgumentException(var1 + " may not be empty");
      } else {
         return (T)var0;
      }
   }

   public static int positive(int var0, String var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException(var1 + " may not be negative or zero");
      } else {
         return var0;
      }
   }

   public static long positive(long var0, String var2) {
      if (var0 <= 0L) {
         throw new IllegalArgumentException(var2 + " may not be negative or zero");
      } else {
         return var0;
      }
   }

   public static int notNegative(int var0, String var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException(var1 + " may not be negative");
      } else {
         return var0;
      }
   }

   public static long notNegative(long var0, String var2) {
      if (var0 < 0L) {
         throw new IllegalArgumentException(var2 + " may not be negative");
      } else {
         return var0;
      }
   }
}
