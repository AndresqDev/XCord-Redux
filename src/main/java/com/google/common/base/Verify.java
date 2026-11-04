package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Verify {
   public static void verify(boolean var0) {
      if (!var0) {
         throw new VerifyException();
      }
   }

   public static void verify(boolean var0, String var1, Object... var2) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void verify(boolean var0, String var1, char var2) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void verify(boolean var0, String var1, int var2) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void verify(boolean var0, String var1, long var2) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void verify(boolean var0, String var1, Object var2) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      }
   }

   public static void verify(boolean var0, String var1, char var2, char var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, int var2, char var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, long var2, char var4) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, char var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, char var2, int var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, int var2, int var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, long var2, int var4) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, int var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, char var2, long var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, int var2, long var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, long var2, long var4) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, long var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, char var2, Object var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, int var2, Object var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, long var2, Object var4) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var4));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, Object var3) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, Object var3, Object var4) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3, var4));
      }
   }

   public static void verify(boolean var0, String var1, Object var2, Object var3, Object var4, Object var5) {
      if (!var0) {
         throw new VerifyException(Strings.lenientFormat(var1, var2, var3, var4, var5));
      }
   }

   @CanIgnoreReturnValue
   public static <T> T verifyNotNull(T var0) {
      return verifyNotNull((T)var0, "expected a non-null reference");
   }

   @CanIgnoreReturnValue
   public static <T> T verifyNotNull(T var0, String var1, Object... var2) {
      if (var0 == null) {
         throw new VerifyException(Strings.lenientFormat(var1, var2));
      } else {
         return (T)var0;
      }
   }

   private Verify() {
   }
}
