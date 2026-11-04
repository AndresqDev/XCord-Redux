package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.math.LongMath;
import java.util.concurrent.TimeUnit;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
abstract class SmoothRateLimiter extends RateLimiter {
   double storedPermits;
   double maxPermits;
   double stableIntervalMicros;
   private long nextFreeTicketMicros = 0L;

   private SmoothRateLimiter(RateLimiter.SleepingStopwatch var1) {
      super(var1);
   }

   @Override
   final void doSetRate(double var1, long var3) {
      this.resync(var3);
      double var5 = (double)TimeUnit.SECONDS.toMicros(1L) / var1;
      this.stableIntervalMicros = var5;
      this.doSetRate(var1, var5);
   }

   abstract void doSetRate(double var1, double var3);

   @Override
   final double doGetRate() {
      return (double)TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
   }

   @Override
   final long queryEarliestAvailable(long var1) {
      return this.nextFreeTicketMicros;
   }

   @Override
   final long reserveEarliestAvailable(int var1, long var2) {
      this.resync(var2);
      long var4 = this.nextFreeTicketMicros;
      double var6 = Math.min((double)var1, this.storedPermits);
      double var8 = (double)var1 - var6;
      long var10 = this.storedPermitsToWaitTime(this.storedPermits, var6) + (long)(var8 * this.stableIntervalMicros);
      this.nextFreeTicketMicros = LongMath.saturatedAdd(this.nextFreeTicketMicros, var10);
      this.storedPermits -= var6;
      return var4;
   }

   abstract long storedPermitsToWaitTime(double var1, double var3);

   abstract double coolDownIntervalMicros();

   void resync(long var1) {
      if (var1 > this.nextFreeTicketMicros) {
         double var3 = (double)(var1 - this.nextFreeTicketMicros) / this.coolDownIntervalMicros();
         this.storedPermits = Math.min(this.maxPermits, this.storedPermits + var3);
         this.nextFreeTicketMicros = var1;
      }
   }

   static final class SmoothBursty extends SmoothRateLimiter {
      final double maxBurstSeconds;

      SmoothBursty(RateLimiter.SleepingStopwatch var1, double var2) {
         super(var1);
         this.maxBurstSeconds = var2;
      }

      @Override
      void doSetRate(double var1, double var3) {
         double var5 = this.maxPermits;
         this.maxPermits = this.maxBurstSeconds * var1;
         if (var5 == Double.POSITIVE_INFINITY) {
            this.storedPermits = this.maxPermits;
         } else {
            this.storedPermits = var5 == 0.0 ? 0.0 : this.storedPermits * this.maxPermits / var5;
         }
      }

      @Override
      long storedPermitsToWaitTime(double var1, double var3) {
         return 0L;
      }

      @Override
      double coolDownIntervalMicros() {
         return this.stableIntervalMicros;
      }
   }

   static final class SmoothWarmingUp extends SmoothRateLimiter {
      private final long warmupPeriodMicros;
      private double slope;
      private double thresholdPermits;
      private double coldFactor;

      SmoothWarmingUp(RateLimiter.SleepingStopwatch var1, long var2, TimeUnit var4, double var5) {
         super(var1);
         this.warmupPeriodMicros = var4.toMicros(var2);
         this.coldFactor = var5;
      }

      @Override
      void doSetRate(double var1, double var3) {
         double var5 = this.maxPermits;
         double var7 = var3 * this.coldFactor;
         this.thresholdPermits = 0.5 * (double)this.warmupPeriodMicros / var3;
         this.maxPermits = this.thresholdPermits + 2.0 * (double)this.warmupPeriodMicros / (var3 + var7);
         this.slope = (var7 - var3) / (this.maxPermits - this.thresholdPermits);
         if (var5 == Double.POSITIVE_INFINITY) {
            this.storedPermits = 0.0;
         } else {
            this.storedPermits = var5 == 0.0 ? this.maxPermits : this.storedPermits * this.maxPermits / var5;
         }
      }

      @Override
      long storedPermitsToWaitTime(double var1, double var3) {
         double var5 = var1 - this.thresholdPermits;
         long var7 = 0L;
         if (var5 > 0.0) {
            double var9 = Math.min(var5, var3);
            double var11 = this.permitsToTime(var5) + this.permitsToTime(var5 - var9);
            var7 = (long)(var9 * var11 / 2.0);
            var3 -= var9;
         }

         return var7 + (long)(this.stableIntervalMicros * var3);
      }

      private double permitsToTime(double var1) {
         return this.stableIntervalMicros + var1 * this.slope;
      }

      @Override
      double coolDownIntervalMicros() {
         return (double)this.warmupPeriodMicros / this.maxPermits;
      }
   }
}
