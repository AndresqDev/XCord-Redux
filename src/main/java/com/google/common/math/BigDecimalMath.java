package com.google.common.math;

import com.google.common.annotations.GwtIncompatible;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public class BigDecimalMath {
   private BigDecimalMath() {
   }

   public static double roundToDouble(BigDecimal var0, RoundingMode var1) {
      return BigDecimalMath.BigDecimalToDoubleRounder.INSTANCE.roundToDouble(var0, var1);
   }

   private static class BigDecimalToDoubleRounder extends ToDoubleRounder<BigDecimal> {
      static final BigDecimalMath.BigDecimalToDoubleRounder INSTANCE = new BigDecimalMath.BigDecimalToDoubleRounder();

      double roundToDoubleArbitrarily(BigDecimal var1) {
         return var1.doubleValue();
      }

      int sign(BigDecimal var1) {
         return var1.signum();
      }

      BigDecimal toX(double var1, RoundingMode var3) {
         return new BigDecimal(var1);
      }

      BigDecimal minus(BigDecimal var1, BigDecimal var2) {
         return var1.subtract(var2);
      }
   }
}
