package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.InlineMeValidationDisabled;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Strings {
   private Strings() {
   }

   public static String nullToEmpty(String var0) {
      return Platform.nullToEmpty(var0);
   }

   public static String emptyToNull(String var0) {
      return Platform.emptyToNull(var0);
   }

   public static boolean isNullOrEmpty(String var0) {
      return Platform.stringIsNullOrEmpty(var0);
   }

   public static String padStart(String var0, int var1, char var2) {
      Preconditions.checkNotNull(var0);
      if (var0.length() >= var1) {
         return var0;
      } else {
         StringBuilder var3 = new StringBuilder(var1);

         for (int var4 = var0.length(); var4 < var1; var4++) {
            var3.append(var2);
         }

         var3.append(var0);
         return var3.toString();
      }
   }

   public static String padEnd(String var0, int var1, char var2) {
      Preconditions.checkNotNull(var0);
      if (var0.length() >= var1) {
         return var0;
      } else {
         StringBuilder var3 = new StringBuilder(var1);
         var3.append(var0);

         for (int var4 = var0.length(); var4 < var1; var4++) {
            var3.append(var2);
         }

         return var3.toString();
      }
   }

   @InlineMe(
      replacement = "string.repeat(count)"
   )
   @InlineMeValidationDisabled("Java 11+ API only")
   public static String repeat(String var0, int var1) {
      Preconditions.checkNotNull(var0);
      if (var1 <= 1) {
         Preconditions.checkArgument(var1 >= 0, "invalid count: %s", var1);
         return var1 == 0 ? "" : var0;
      } else {
         int var2 = var0.length();
         long var3 = (long)var2 * (long)var1;
         int var5 = (int)var3;
         if ((long)var5 != var3) {
            throw new ArrayIndexOutOfBoundsException(new StringBuilder(51).append("Required array size too large: ").append(var3).toString());
         } else {
            char[] var6 = new char[var5];
            var0.getChars(0, var2, var6, 0);

            int var7;
            for (var7 = var2; var7 < var5 - var7; var7 <<= 1) {
               System.arraycopy(var6, 0, var6, var7, var7);
            }

            System.arraycopy(var6, 0, var6, var7, var5 - var7);
            return new String(var6);
         }
      }
   }

   public static String commonPrefix(CharSequence var0, CharSequence var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      int var2 = Math.min(var0.length(), var1.length());
      int var3 = 0;

      while (var3 < var2 && var0.charAt(var3) == var1.charAt(var3)) {
         var3++;
      }

      if (validSurrogatePairAt(var0, var3 - 1) || validSurrogatePairAt(var1, var3 - 1)) {
         var3--;
      }

      return var0.subSequence(0, var3).toString();
   }

   public static String commonSuffix(CharSequence var0, CharSequence var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      int var2 = Math.min(var0.length(), var1.length());
      int var3 = 0;

      while (var3 < var2 && var0.charAt(var0.length() - var3 - 1) == var1.charAt(var1.length() - var3 - 1)) {
         var3++;
      }

      if (validSurrogatePairAt(var0, var0.length() - var3 - 1) || validSurrogatePairAt(var1, var1.length() - var3 - 1)) {
         var3--;
      }

      return var0.subSequence(var0.length() - var3, var0.length()).toString();
   }

   @VisibleForTesting
   static boolean validSurrogatePairAt(CharSequence var0, int var1) {
      return var1 >= 0 && var1 <= var0.length() - 2 && Character.isHighSurrogate(var0.charAt(var1)) && Character.isLowSurrogate(var0.charAt(var1 + 1));
   }

   public static String lenientFormat(String var0, Object... var1) {
      var0 = String.valueOf(var0);
      if (var1 == null) {
         var1 = new Object[]{"(Object[])null"};
      } else {
         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = lenientToString(var1[var2]);
         }
      }

      StringBuilder var7 = new StringBuilder(var0.length() + 16 * var1.length);
      int var3 = 0;
      int var4 = 0;

      while (var4 < var1.length) {
         int var5 = var0.indexOf("%s", var3);
         if (var5 == -1) {
            break;
         }

         var7.append(var0, var3, var5);
         var7.append(var1[var4++]);
         var3 = var5 + 2;
      }

      var7.append(var0, var3, var0.length());
      if (var4 < var1.length) {
         var7.append(" [");
         var7.append(var1[var4++]);

         while (var4 < var1.length) {
            var7.append(", ");
            var7.append(var1[var4++]);
         }

         var7.append(']');
      }

      return var7.toString();
   }

   private static String lenientToString(Object var0) {
      if (var0 == null) {
         return "null";
      } else {
         try {
            return var0.toString();
         } catch (Exception var5) {
            String var3 = var0.getClass().getName();
            String var4 = Integer.toHexString(System.identityHashCode(var0));
            String var2 = new StringBuilder(1 + String.valueOf(var3).length() + String.valueOf(var4).length()).append(var3).append('@').append(var4).toString();
            Logger var10000 = Logger.getLogger("com.google.common.base.Strings");
            Level var10001 = Level.WARNING;
            String var10003 = String.valueOf(var2);
            var10000.log(
               var10001,
               var10003.length() != 0 ? "Exception during lenientFormat for ".concat(var10003) : new String("Exception during lenientFormat for "),
               (Throwable)var5
            );
            var3 = var5.getClass().getName();
            return new StringBuilder(9 + String.valueOf(var2).length() + String.valueOf(var3).length())
               .append("<")
               .append(var2)
               .append(" threw ")
               .append(var3)
               .append(">")
               .toString();
         }
      }
   }
}
