package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtCompatible
@CanIgnoreReturnValue
final class MathPreconditions {
   static int checkPositive(String var0, int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException(
            new StringBuilder(26 + String.valueOf(var0).length()).append(var0).append(" (").append(var1).append(") must be > 0").toString()
         );
      } else {
         return var1;
      }
   }

   static long checkPositive(String var0, long var1) {
      if (var1 <= 0L) {
         throw new IllegalArgumentException(
            new StringBuilder(35 + String.valueOf(var0).length()).append(var0).append(" (").append(var1).append(") must be > 0").toString()
         );
      } else {
         return var1;
      }
   }

   static BigInteger checkPositive(String var0, BigInteger var1) {
      if (var1.signum() <= 0) {
         String var2 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(15 + String.valueOf(var0).length() + String.valueOf(var2).length())
               .append(var0)
               .append(" (")
               .append(var2)
               .append(") must be > 0")
               .toString()
         );
      } else {
         return var1;
      }
   }

   static int checkNonNegative(String var0, int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException(
            new StringBuilder(27 + String.valueOf(var0).length()).append(var0).append(" (").append(var1).append(") must be >= 0").toString()
         );
      } else {
         return var1;
      }
   }

   static long checkNonNegative(String var0, long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException(
            new StringBuilder(36 + String.valueOf(var0).length()).append(var0).append(" (").append(var1).append(") must be >= 0").toString()
         );
      } else {
         return var1;
      }
   }

   static BigInteger checkNonNegative(String var0, BigInteger var1) {
      if (var1.signum() < 0) {
         String var2 = String.valueOf(var1);
         throw new IllegalArgumentException(
            new StringBuilder(16 + String.valueOf(var0).length() + String.valueOf(var2).length())
               .append(var0)
               .append(" (")
               .append(var2)
               .append(") must be >= 0")
               .toString()
         );
      } else {
         return var1;
      }
   }

   static double checkNonNegative(String var0, double var1) {
      if (!(var1 >= 0.0)) {
         throw new IllegalArgumentException(
            new StringBuilder(40 + String.valueOf(var0).length()).append(var0).append(" (").append(var1).append(") must be >= 0").toString()
         );
      } else {
         return var1;
      }
   }

   static void checkRoundingUnnecessary(boolean var0) {
      if (!var0) {
         throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
      }
   }

   static void checkInRangeForRoundingInputs(boolean var0, double var1, RoundingMode var3) {
      if (!var0) {
         String var4 = String.valueOf(var3);
         throw new ArithmeticException(
            new StringBuilder(83 + String.valueOf(var4).length())
               .append("rounded value is out of range for input ")
               .append(var1)
               .append(" and rounding mode ")
               .append(var4)
               .toString()
         );
      }
   }

   static void checkNoOverflow(boolean var0, String var1, int var2, int var3) {
      if (!var0) {
         throw new ArithmeticException(
            new StringBuilder(36 + String.valueOf(var1).length())
               .append("overflow: ")
               .append(var1)
               .append("(")
               .append(var2)
               .append(", ")
               .append(var3)
               .append(")")
               .toString()
         );
      }
   }

   static void checkNoOverflow(boolean var0, String var1, long var2, long var4) {
      if (!var0) {
         throw new ArithmeticException(
            new StringBuilder(54 + String.valueOf(var1).length())
               .append("overflow: ")
               .append(var1)
               .append("(")
               .append(var2)
               .append(", ")
               .append(var4)
               .append(")")
               .toString()
         );
      }
   }

   private MathPreconditions() {
   }
}
