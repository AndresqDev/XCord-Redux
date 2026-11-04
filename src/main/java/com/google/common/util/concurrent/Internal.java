package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import java.time.Duration;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class Internal {
   static long toNanosSaturated(Duration var0) {
      try {
         return var0.toNanos();
      } catch (ArithmeticException var2) {
         return var0.isNegative() ? Long.MIN_VALUE : Long.MAX_VALUE;
      }
   }

   private Internal() {
   }
}
