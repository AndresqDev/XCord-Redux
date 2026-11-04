package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Preconditions {
   private Preconditions() {
   }

   public static void checkArgument(boolean var0) {
      if (!var0) {
         throw new IllegalArgumentException();
      }
   }

   public static void checkArgument(boolean var0, Object var1) {
      if (!var0) {
         throw new IllegalArgumentException(String.valueOf(var1));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object... var2) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, String var1, char var2) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, String var1, int var2) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, String var1, long var2) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, String var1, char var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, char var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, char var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, char var2, Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, int var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, int var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, int var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, int var2, Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, long var2, char var4) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, String var1, long var2, int var4) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, String var1, long var2, long var4) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, String var1, long var2, Object var4) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, Object var3, Object var4) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3, var4));
      }
   }

   public static void checkArgument(boolean var0, String var1, Object var2, Object var3, Object var4, Object var5) {
      if (!var0) {
         throw new IllegalArgumentException(Strings.lenientFormat(var1, var2, var3, var4, var5));
      }
   }

   public static void checkState(boolean var0) {
      if (!var0) {
         throw new IllegalStateException();
      }
   }

   public static void checkState(boolean var0, Object var1) {
      if (!var0) {
         throw new IllegalStateException(String.valueOf(var1));
      }
   }

   public static void checkState(boolean var0, String var1, Object... var2) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkState(boolean var0, String var1, char var2) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkState(boolean var0, String var1, int var2) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkState(boolean var0, String var1, long var2) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void checkState(boolean var0, String var1, char var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, char var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, char var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, char var2, Object var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, int var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, int var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, int var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, int var2, Object var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, long var2, char var4) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, String var1, long var2, int var4) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, String var1, long var2, long var4) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, String var1, long var2, Object var4) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, Object var3) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, Object var3, Object var4) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3, var4));
      }
   }

   public static void checkState(boolean var0, String var1, Object var2, Object var3, Object var4, Object var5) {
      if (!var0) {
         throw new IllegalStateException(Strings.lenientFormat(var1, var2, var3, var4, var5));
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, Object var1) {
      if (var0 == null) {
         throw new NullPointerException(String.valueOf(var1));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object... var2) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, char var2) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, long var2) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, char var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, char var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, char var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, char var2, Object var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, int var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, int var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, int var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, int var2, Object var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, long var2, char var4) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var4));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, long var2, int var4) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var4));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, long var2, long var4) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var4));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, long var2, Object var4) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var4));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, Object var3) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, Object var3, Object var4) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3, var4));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, String var1, Object var2, Object var3, Object var4, Object var5) {
      if (var0 == null) {
         throw new NullPointerException(Strings.lenientFormat(var1, var2, var3, var4, var5));
      } else {
         return (T)var0;
      }
   }

   @CanIgnoreReturnValue
   public static int checkElementIndex(int var0, int var1) {
      return checkElementIndex(var0, var1, "index");
   }

   @CanIgnoreReturnValue
   public static int checkElementIndex(int var0, int var1, String var2) {
      if (var0 >= 0 && var0 < var1) {
         return var0;
      } else {
         throw new IndexOutOfBoundsException(badElementIndex(var0, var1, var2));
      }
   }

   private static String badElementIndex(int var0, int var1, String var2) {
      if (var0 < 0) {
         return Strings.lenientFormat("%s (%s) must not be negative", var2, var0);
      } else if (var1 < 0) {
         throw new IllegalArgumentException(new StringBuilder(26).append("negative size: ").append(var1).toString());
      } else {
         return Strings.lenientFormat("%s (%s) must be less than size (%s)", var2, var0, var1);
      }
   }

   @CanIgnoreReturnValue
   public static int checkPositionIndex(int var0, int var1) {
      return checkPositionIndex(var0, var1, "index");
   }

   @CanIgnoreReturnValue
   public static int checkPositionIndex(int var0, int var1, String var2) {
      if (var0 >= 0 && var0 <= var1) {
         return var0;
      } else {
         throw new IndexOutOfBoundsException(badPositionIndex(var0, var1, var2));
      }
   }

   private static String badPositionIndex(int var0, int var1, String var2) {
      if (var0 < 0) {
         return Strings.lenientFormat("%s (%s) must not be negative", var2, var0);
      } else if (var1 < 0) {
         throw new IllegalArgumentException(new StringBuilder(26).append("negative size: ").append(var1).toString());
      } else {
         return Strings.lenientFormat("%s (%s) must not be greater than size (%s)", var2, var0, var1);
      }
   }

   public static void checkPositionIndexes(int var0, int var1, int var2) {
      if (var0 < 0 || var1 < var0 || var1 > var2) {
         throw new IndexOutOfBoundsException(badPositionIndexes(var0, var1, var2));
      }
   }

   private static String badPositionIndexes(int var0, int var1, int var2) {
      if (var0 < 0 || var0 > var2) {
         return badPositionIndex(var0, var2, "start index");
      } else {
         return var1 >= 0 && var1 <= var2
            ? Strings.lenientFormat("end index (%s) must not be less than start index (%s)", var1, var0)
            : badPositionIndex(var1, var2, "end index");
      }
   }
}
