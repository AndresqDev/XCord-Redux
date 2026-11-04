package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class DoubleMath {
   private static final double MIN_INT_AS_DOUBLE = -2.1474836E9F;
   private static final double MAX_INT_AS_DOUBLE = 2.147483647E9;
   private static final double MIN_LONG_AS_DOUBLE = -9.223372E18F;
   private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372E18F;
   private static final double LN_2 = Math.log(2.0);
   @VisibleForTesting
   static final int MAX_FACTORIAL = 170;
   @VisibleForTesting
   static final double[] everySixteenthFactorial = new double[]{
      1.0,
      2.0922789888E13,
      2.631308369336935E35,
      1.2413915592536073E61,
      1.2688693218588417E89,
      7.156945704626381E118,
      9.916779348709496E149,
      1.974506857221074E182,
      3.856204823625804E215,
      5.5502938327393044E249,
      4.7147236359920616E284
   };

   @GwtIncompatible
   static double roundIntermediate(double var0, RoundingMode var2) {
      if (!DoubleUtils.isFinite(var0)) {
         throw new ArithmeticException("input is infinite or NaN");
      } else {
         switch (var2) {
            case UNNECESSARY:
               MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(var0));
               return var0;
            case FLOOR:
               if (!(var0 >= 0.0) && !isMathematicalInteger(var0)) {
                  return (double)((long)var0 - 1L);
               }

               return var0;
            case CEILING:
               if (!(var0 <= 0.0) && !isMathematicalInteger(var0)) {
                  return (double)((long)var0 + 1L);
               }

               return var0;
            case DOWN:
               return var0;
            case UP:
               if (isMathematicalInteger(var0)) {
                  return var0;
               }

               return (double)((long)var0 + (long)(var0 > 0.0 ? 1 : -1));
            case HALF_EVEN:
               return Math.rint(var0);
            case HALF_UP:
               double var5 = Math.rint(var0);
               if (Math.abs(var0 - var5) == 0.5) {
                  return var0 + Math.copySign(0.5, var0);
               }

               return var5;
            case HALF_DOWN:
               double var3 = Math.rint(var0);
               if (Math.abs(var0 - var3) == 0.5) {
                  return var0;
               }

               return var3;
            default:
               throw new AssertionError();
         }
      }
   }

   @GwtIncompatible
   public static int roundToInt(double var0, RoundingMode var2) {
      double var3 = roundIntermediate(var0, var2);
      MathPreconditions.checkInRangeForRoundingInputs(var3 > -2.147483649E9 & var3 < 2.1474836E9F, var0, var2);
      return (int)var3;
   }

   @GwtIncompatible
   public static long roundToLong(double var0, RoundingMode var2) {
      double var3 = roundIntermediate(var0, var2);
      MathPreconditions.checkInRangeForRoundingInputs(-9.223372E18F - var3 < 1.0 & var3 < 9.223372E18F, var0, var2);
      return (long)var3;
   }

   @GwtIncompatible
   public static BigInteger roundToBigInteger(double var0, RoundingMode var2) {
      var0 = roundIntermediate(var0, var2);
      if (-9.223372E18F - var0 < 1.0 & var0 < 9.223372E18F) {
         return BigInteger.valueOf((long)var0);
      } else {
         int var3 = Math.getExponent(var0);
         long var4 = DoubleUtils.getSignificand(var0);
         BigInteger var6 = BigInteger.valueOf(var4).shiftLeft(var3 - 52);
         return var0 < 0.0 ? var6.negate() : var6;
      }
   }

   @GwtIncompatible
   public static boolean isPowerOfTwo(double var0) {
      if (var0 > 0.0 && DoubleUtils.isFinite(var0)) {
         long var2 = DoubleUtils.getSignificand(var0);
         return (var2 & var2 - 1L) == 0L;
      } else {
         return false;
      }
   }

   public static double log2(double var0) {
      return Math.log(var0) / LN_2;
   }

   @GwtIncompatible
   public static int log2(double var0, RoundingMode var2) {
      Preconditions.checkArgument(var0 > 0.0 && DoubleUtils.isFinite(var0), "x must be positive and finite");
      int var3 = Math.getExponent(var0);
      if (!DoubleUtils.isNormal(var0)) {
         return log2(var0 * 4.5035996E15F, var2) - 52;
      } else {
         boolean var4;
         switch (var2) {
            case UNNECESSARY:
               MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(var0));
            case FLOOR:
               var4 = false;
               break;
            case CEILING:
               var4 = !isPowerOfTwo(var0);
               break;
            case DOWN:
               var4 = var3 < 0 & !isPowerOfTwo(var0);
               break;
            case UP:
               var4 = var3 >= 0 & !isPowerOfTwo(var0);
               break;
            case HALF_EVEN:
            case HALF_UP:
            case HALF_DOWN:
               double var5 = DoubleUtils.scaleNormalize(var0);
               var4 = var5 * var5 > 2.0;
               break;
            default:
               throw new AssertionError();
         }

         return var4 ? var3 + 1 : var3;
      }
   }

   @GwtIncompatible
   public static boolean isMathematicalInteger(double var0) {
      return DoubleUtils.isFinite(var0) && (var0 == 0.0 || 52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(var0)) <= Math.getExponent(var0));
   }

   public static double factorial(int var0) {
      MathPreconditions.checkNonNegative("n", var0);
      if (var0 > 170) {
         return Double.POSITIVE_INFINITY;
      } else {
         double var1 = 1.0;

         for (int var3 = 1 + (var0 & -16); var3 <= var0; var3++) {
            var1 *= (double)var3;
         }

         return var1 * everySixteenthFactorial[var0 >> 4];
      }
   }

   public static boolean fuzzyEquals(double var0, double var2, double var4) {
      MathPreconditions.checkNonNegative("tolerance", var4);
      return Math.copySign(var0 - var2, 1.0) <= var4 || var0 == var2 || Double.isNaN(var0) && Double.isNaN(var2);
   }

   public static int fuzzyCompare(double var0, double var2, double var4) {
      if (fuzzyEquals(var0, var2, var4)) {
         return 0;
      } else if (var0 < var2) {
         return -1;
      } else {
         return var0 > var2 ? 1 : Booleans.compare(Double.isNaN(var0), Double.isNaN(var2));
      }
   }

   @Deprecated
   @GwtIncompatible
   public static double mean(double... var0) {
      Preconditions.checkArgument(var0.length > 0, "Cannot take mean of 0 values");
      long var1 = 1L;
      double var3 = checkFinite(var0[0]);

      for (int var5 = 1; var5 < var0.length; var5++) {
         checkFinite(var0[var5]);
         var1++;
         var3 += (var0[var5] - var3) / (double)var1;
      }

      return var3;
   }

   @Deprecated
   public static double mean(int... var0) {
      Preconditions.checkArgument(var0.length > 0, "Cannot take mean of 0 values");
      long var1 = 0L;

      for (int var3 = 0; var3 < var0.length; var3++) {
         var1 += (long)var0[var3];
      }

      return (double)var1 / (double)var0.length;
   }

   @Deprecated
   public static double mean(long... var0) {
      Preconditions.checkArgument(var0.length > 0, "Cannot take mean of 0 values");
      long var1 = 1L;
      double var3 = (double)var0[0];

      for (int var5 = 1; var5 < var0.length; var5++) {
         var1++;
         var3 += ((double)var0[var5] - var3) / (double)var1;
      }

      return var3;
   }

   @Deprecated
   @GwtIncompatible
   public static double mean(Iterable<? extends Number> var0) {
      return mean(var0.iterator());
   }

   @Deprecated
   @GwtIncompatible
   public static double mean(Iterator<? extends Number> var0) {
      Preconditions.checkArgument(var0.hasNext(), "Cannot take mean of 0 values");
      long var1 = 1L;
      double var3 = checkFinite(((Number)var0.next()).doubleValue());

      while (var0.hasNext()) {
         double var5 = checkFinite(((Number)var0.next()).doubleValue());
         var1++;
         var3 += (var5 - var3) / (double)var1;
      }

      return var3;
   }

   @GwtIncompatible
   @CanIgnoreReturnValue
   private static double checkFinite(double var0) {
      Preconditions.checkArgument(DoubleUtils.isFinite(var0));
      return var0;
   }

   private DoubleMath() {
   }
}
