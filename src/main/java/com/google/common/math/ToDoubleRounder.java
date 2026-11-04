package com.google.common.math;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class ToDoubleRounder<X extends Number & Comparable<X>> {
   abstract double roundToDoubleArbitrarily(X var1);

   abstract int sign(X var1);

   abstract X toX(double var1, RoundingMode var3);

   abstract X minus(X var1, X var2);

   final double roundToDouble(X var1, RoundingMode var2) {
      Preconditions.checkNotNull(var1, "x");
      Preconditions.checkNotNull(var2, "mode");
      double var3 = this.roundToDoubleArbitrarily((X)var1);
      if (Double.isInfinite(var3)) {
         switch (var2) {
            case DOWN:
            case HALF_EVEN:
            case HALF_DOWN:
            case HALF_UP:
               return (Double.MAX_VALUE) * (double)this.sign((X)var1);
            case FLOOR:
               return var3 == Double.POSITIVE_INFINITY ? Double.MAX_VALUE : Double.NEGATIVE_INFINITY;
            case CEILING:
               return var3 == Double.POSITIVE_INFINITY ? Double.POSITIVE_INFINITY : -Double.MAX_VALUE;
            case UP:
               return var3;
            case UNNECESSARY:
               String var16 = String.valueOf(var1);
               throw new ArithmeticException(
                  new StringBuilder(44 + String.valueOf(var16).length()).append(var16).append(" cannot be represented precisely as a double").toString()
               );
         }
      }

      Number var5 = this.toX(var3, RoundingMode.UNNECESSARY);
      int var6 = ((Comparable)var1).compareTo(var5);
      switch (var2) {
         case DOWN:
            if (this.sign((X)var1) >= 0) {
               return var6 >= 0 ? var3 : DoubleUtils.nextDown(var3);
            }

            return var6 <= 0 ? var3 : Math.nextUp(var3);
         case HALF_EVEN:
         case HALF_DOWN:
         case HALF_UP:
            Number var7;
            double var8;
            Number var10;
            double var11;
            if (var6 >= 0) {
               var8 = var3;
               var7 = var5;
               var11 = Math.nextUp(var3);
               if (var11 == Double.POSITIVE_INFINITY) {
                  return var3;
               }

               var10 = this.toX(var11, RoundingMode.CEILING);
            } else {
               var11 = var3;
               var10 = var5;
               var8 = DoubleUtils.nextDown(var3);
               if (var8 == Double.NEGATIVE_INFINITY) {
                  return var3;
               }

               var7 = this.toX(var8, RoundingMode.FLOOR);
            }

            Number var13 = this.minus((X)var1, (X)var7);
            Number var14 = this.minus((X)var10, (X)var1);
            int var15 = ((Comparable)var13).compareTo(var14);
            if (var15 < 0) {
               return var8;
            } else if (var15 > 0) {
               return var11;
            } else {
               switch (var2) {
                  case HALF_EVEN:
                     return (Double.doubleToRawLongBits(var8) & 1L) == 0L ? var8 : var11;
                  case HALF_DOWN:
                     return this.sign((X)var1) >= 0 ? var8 : var11;
                  case HALF_UP:
                     return this.sign((X)var1) >= 0 ? var11 : var8;
                  default:
                     throw new AssertionError("impossible");
               }
            }
         case FLOOR:
            return var6 >= 0 ? var3 : DoubleUtils.nextDown(var3);
         case CEILING:
            return var6 <= 0 ? var3 : Math.nextUp(var3);
         case UP:
            if (this.sign((X)var1) >= 0) {
               return var6 <= 0 ? var3 : Math.nextUp(var3);
            }

            return var6 >= 0 ? var3 : DoubleUtils.nextDown(var3);
         case UNNECESSARY:
            MathPreconditions.checkRoundingUnnecessary(var6 == 0);
            return var3;
         default:
            throw new AssertionError("impossible");
      }
   }
}
