package org.apache.logging.log4j.core.time;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public class MutableInstant implements Serializable, TemporalAccessor, Instant {
   private static final int MILLIS_PER_SECOND = 1000;
   private static final int NANOS_PER_MILLI = 1000000;
   private static final int NANOS_PER_SECOND = 1000000000;
   private long epochSecond;
   private int nanoOfSecond;

   @Override
   public long getEpochSecond() {
      return this.epochSecond;
   }

   @Override
   public int getNanoOfSecond() {
      return this.nanoOfSecond;
   }

   @Override
   public long getEpochMillisecond() {
      int var1 = this.nanoOfSecond / 1000000;
      return this.epochSecond * 1000L + (long)var1;
   }

   @Override
   public int getNanoOfMillisecond() {
      int var1 = this.nanoOfSecond / 1000000;
      return this.nanoOfSecond - var1 * 1000000;
   }

   public void initFrom(Instant var1) {
      this.epochSecond = var1.getEpochSecond();
      this.nanoOfSecond = var1.getNanoOfSecond();
   }

   public void initFromEpochMilli(long var1, int var3) {
      this.validateNanoOfMillisecond(var3);
      this.epochSecond = var1 / 1000L;
      this.nanoOfSecond = (int)(var1 - this.epochSecond * 1000L) * 1000000 + var3;
   }

   private void validateNanoOfMillisecond(int var1) {
      if (var1 < 0 || var1 >= 1000000) {
         throw new IllegalArgumentException("Invalid nanoOfMillisecond " + var1);
      }
   }

   public void initFrom(Clock var1) {
      if (var1 instanceof PreciseClock) {
         ((PreciseClock)var1).init(this);
      } else {
         this.initFromEpochMilli(var1.currentTimeMillis(), 0);
      }
   }

   public void initFromEpochSecond(long var1, int var3) {
      this.validateNanoOfSecond(var3);
      this.epochSecond = var1;
      this.nanoOfSecond = var3;
   }

   private void validateNanoOfSecond(int var1) {
      if (var1 < 0 || var1 >= 1000000000) {
         throw new IllegalArgumentException("Invalid nanoOfSecond " + var1);
      }
   }

   public static void instantToMillisAndNanos(long var0, int var2, long[] var3) {
      int var4 = var2 / 1000000;
      var3[0] = var0 * 1000L + (long)var4;
      var3[1] = (long)(var2 - var4 * 1000000);
   }

   @Override
   public boolean isSupported(TemporalField var1) {
      return var1 instanceof ChronoField
         ? var1 == ChronoField.INSTANT_SECONDS
            || var1 == ChronoField.NANO_OF_SECOND
            || var1 == ChronoField.MICRO_OF_SECOND
            || var1 == ChronoField.MILLI_OF_SECOND
         : var1 != null && var1.isSupportedBy(this);
   }

   @Override
   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch ((ChronoField)var1) {
            case NANO_OF_SECOND:
               return (long)this.nanoOfSecond;
            case MICRO_OF_SECOND:
               return (long)(this.nanoOfSecond / 1000);
            case MILLI_OF_SECOND:
               return (long)(this.nanoOfSecond / 1000000);
            case INSTANT_SECONDS:
               return this.epochSecond;
            default:
               throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   @Override
   public ValueRange range(TemporalField var1) {
      return TemporalAccessor.super.range(var1);
   }

   @Override
   public int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch ((ChronoField)var1) {
            case NANO_OF_SECOND:
               return this.nanoOfSecond;
            case MICRO_OF_SECOND:
               return this.nanoOfSecond / 1000;
            case MILLI_OF_SECOND:
               return this.nanoOfSecond / 1000000;
            case INSTANT_SECONDS:
               ChronoField.INSTANT_SECONDS.checkValidIntValue(this.epochSecond);
            default:
               throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return this.range(var1).checkValidIntValue(var1.getFrom(this), var1);
      }
   }

   @Override
   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.precision()) {
         return (R)ChronoUnit.NANOS;
      } else {
         return (R)(var1 != TemporalQueries.chronology()
               && var1 != TemporalQueries.zoneId()
               && var1 != TemporalQueries.zone()
               && var1 != TemporalQueries.offset()
               && var1 != TemporalQueries.localDate()
               && var1 != TemporalQueries.localTime()
            ? var1.queryFrom(this)
            : null);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MutableInstant)) {
         return false;
      } else {
         MutableInstant var2 = (MutableInstant)var1;
         return this.epochSecond == var2.epochSecond && this.nanoOfSecond == var2.nanoOfSecond;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = 31 * var1 + (int)(this.epochSecond ^ this.epochSecond >>> 32);
      return 31 * var1 + this.nanoOfSecond;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      this.formatTo(var1);
      return var1.toString();
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append("MutableInstant[epochSecond=").append(this.epochSecond).append(", nano=").append(this.nanoOfSecond).append("]");
   }
}
