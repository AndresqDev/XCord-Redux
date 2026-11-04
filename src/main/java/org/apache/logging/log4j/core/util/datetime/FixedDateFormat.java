package org.apache.logging.log4j.core.util.datetime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.time.Instant;

public class FixedDateFormat {
   private static final char NONE = '\u0000';
   private final FixedDateFormat.FixedFormat fixedFormat;
   private final TimeZone timeZone;
   private final int length;
   private final int secondFractionDigits;
   private final FastDateFormat fastDateFormat;
   private final char timeSeparatorChar;
   private final char millisSeparatorChar;
   private final int timeSeparatorLength;
   private final int millisSeparatorLength;
   private final FixedDateFormat.FixedTimeZoneFormat fixedTimeZoneFormat;
   private volatile long midnightToday;
   private volatile long midnightTomorrow;
   private final int[] dstOffsets = new int[25];
   private char[] cachedDate;
   private int dateLength;
   static int[] TABLE = new int[]{100000, 10000, 1000, 100, 10, 1};

   FixedDateFormat(FixedDateFormat.FixedFormat var1, TimeZone var2) {
      this(var1, var2, var1.getSecondFractionDigits());
   }

   FixedDateFormat(FixedDateFormat.FixedFormat var1, TimeZone var2, int var3) {
      this.fixedFormat = Objects.requireNonNull(var1);
      this.timeZone = Objects.requireNonNull(var2);
      this.timeSeparatorChar = var1.timeSeparatorChar;
      this.timeSeparatorLength = var1.timeSeparatorLength;
      this.millisSeparatorChar = var1.millisSeparatorChar;
      this.millisSeparatorLength = var1.millisSeparatorLength;
      this.fixedTimeZoneFormat = var1.fixedTimeZoneFormat;
      this.length = var1.getLength();
      this.secondFractionDigits = Math.max(1, Math.min(9, var3));
      this.fastDateFormat = var1.getFastDateFormat(var2);
   }

   public static FixedDateFormat createIfSupported(String... var0) {
      if (var0 != null && var0.length != 0 && var0[0] != null) {
         TimeZone var1;
         if (var0.length > 1) {
            if (var0[1] != null) {
               var1 = TimeZone.getTimeZone(var0[1]);
            } else {
               var1 = TimeZone.getDefault();
            }
         } else {
            var1 = TimeZone.getDefault();
         }

         String var2 = var0[0];
         FixedDateFormat.FixedFormat var3 = FixedDateFormat.FixedFormat.lookupIgnoringNanos(var2);
         if (var3 != null) {
            int[] var8 = FixedDateFormat.FixedFormat.nanoRange(var2);
            int var5 = var8[0];
            int var6 = var8[1];
            int var7 = var6 - var5;
            return new FixedDateFormat(var3, var1, var7);
         } else {
            FixedDateFormat.FixedFormat var4 = FixedDateFormat.FixedFormat.lookup(var2);
            return var4 == null ? null : new FixedDateFormat(var4, var1);
         }
      } else {
         return new FixedDateFormat(FixedDateFormat.FixedFormat.DEFAULT, TimeZone.getDefault());
      }
   }

   public static FixedDateFormat create(FixedDateFormat.FixedFormat var0) {
      return new FixedDateFormat(var0, TimeZone.getDefault());
   }

   public static FixedDateFormat create(FixedDateFormat.FixedFormat var0, TimeZone var1) {
      return new FixedDateFormat(var0, var1 != null ? var1 : TimeZone.getDefault());
   }

   public String getFormat() {
      return this.fixedFormat.getPattern();
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public long millisSinceMidnight(long var1) {
      if (var1 >= this.midnightTomorrow || var1 < this.midnightToday) {
         this.updateMidnightMillis(var1);
      }

      return var1 - this.midnightToday;
   }

   private void updateMidnightMillis(long var1) {
      if (var1 >= this.midnightTomorrow || var1 < this.midnightToday) {
         synchronized (this) {
            this.updateCachedDate(var1);
            this.midnightToday = this.calcMidnightMillis(var1, 0);
            this.midnightTomorrow = this.calcMidnightMillis(var1, 1);
            this.updateDaylightSavingTime();
         }
      }
   }

   private long calcMidnightMillis(long var1, int var3) {
      Calendar var4 = Calendar.getInstance(this.timeZone);
      var4.setTimeInMillis(var1);
      var4.set(11, 0);
      var4.set(12, 0);
      var4.set(13, 0);
      var4.set(14, 0);
      var4.add(5, var3);
      return var4.getTimeInMillis();
   }

   private void updateDaylightSavingTime() {
      Arrays.fill(this.dstOffsets, 0);
      int var1 = (int)TimeUnit.HOURS.toMillis(1L);
      if (this.timeZone.getOffset(this.midnightToday) != this.timeZone.getOffset(this.midnightToday + (long)(23 * var1))) {
         for (int var2 = 0; var2 < this.dstOffsets.length; var2++) {
            long var3 = this.midnightToday + (long)(var2 * var1);
            this.dstOffsets[var2] = this.timeZone.getOffset(var3) - this.timeZone.getRawOffset();
         }

         if (this.dstOffsets[0] > this.dstOffsets[23]) {
            for (int var5 = this.dstOffsets.length - 1; var5 >= 0; var5--) {
               this.dstOffsets[var5] = this.dstOffsets[var5] - this.dstOffsets[0];
            }
         }
      }
   }

   private void updateCachedDate(long var1) {
      if (this.fastDateFormat != null) {
         StringBuilder var3 = this.fastDateFormat.format(var1, new StringBuilder());
         this.cachedDate = var3.toString().toCharArray();
         this.dateLength = var3.length();
      }
   }

   public String formatInstant(Instant var1) {
      char[] var2 = new char[this.length << 1];
      int var3 = this.formatInstant(var1, var2, 0);
      return new String(var2, 0, var3);
   }

   public int formatInstant(Instant var1, char[] var2, int var3) {
      long var4 = var1.getEpochMillisecond();
      int var6 = this.format(var4, var2, var3);
      var6 -= this.digitsLessThanThree();
      int var7 = this.formatNanoOfMillisecond(var1.getNanoOfMillisecond(), var2, var3 + var6);
      return this.writeTimeZone(var4, var2, var7);
   }

   private int digitsLessThanThree() {
      return Math.max(0, FixedDateFormat.FixedFormat.MILLI_FRACTION_DIGITS - this.secondFractionDigits);
   }

   public String format(long var1) {
      char[] var3 = new char[this.length << 1];
      int var4 = this.format(var1, var3, 0);
      return new String(var3, 0, var4);
   }

   public int format(long var1, char[] var3, int var4) {
      int var5 = (int)this.millisSinceMidnight(var1);
      this.writeDate(var3, var4);
      int var6 = this.writeTime(var5, var3, var4 + this.dateLength);
      return var6 - var4;
   }

   private void writeDate(char[] var1, int var2) {
      if (this.cachedDate != null) {
         System.arraycopy(this.cachedDate, 0, var1, var2, this.dateLength);
      }
   }

   private int writeTime(int var1, char[] var2, int var3) {
      int var4 = var1 / 3600000;
      int var5 = var4 + this.daylightSavingTime(var4) / 3600000;
      var1 -= 3600000 * var4;
      int var6 = var1 / 60000;
      var1 -= 60000 * var6;
      int var7 = var1 / 1000;
      var1 -= 1000 * var7;
      int var8 = var5 / 10;
      var2[var3++] = (char)(var8 + 48);
      var2[var3++] = (char)(var5 - 10 * var8 + 48);
      var2[var3] = this.timeSeparatorChar;
      var3 += this.timeSeparatorLength;
      var8 = var6 / 10;
      var2[var3++] = (char)(var8 + 48);
      var2[var3++] = (char)(var6 - 10 * var8 + 48);
      var2[var3] = this.timeSeparatorChar;
      var3 += this.timeSeparatorLength;
      var8 = var7 / 10;
      var2[var3++] = (char)(var8 + 48);
      var2[var3++] = (char)(var7 - 10 * var8 + 48);
      var2[var3] = this.millisSeparatorChar;
      var3 += this.millisSeparatorLength;
      var8 = var1 / 100;
      var2[var3++] = (char)(var8 + 48);
      var1 -= 100 * var8;
      var8 = var1 / 10;
      var2[var3++] = (char)(var8 + 48);
      var1 -= 10 * var8;
      var2[var3++] = (char)(var1 + 48);
      return var3;
   }

   private int writeTimeZone(long var1, char[] var3, int var4) {
      if (this.fixedTimeZoneFormat != null) {
         var4 = this.fixedTimeZoneFormat.write(this.timeZone.getOffset(var1), var3, var4);
      }

      return var4;
   }

   private int formatNanoOfMillisecond(int var1, char[] var2, int var3) {
      int var5 = var1;

      for (int var6 = 0; var6 < this.secondFractionDigits - FixedDateFormat.FixedFormat.MILLI_FRACTION_DIGITS; var6++) {
         int var7 = TABLE[var6];
         int var4 = var5 / var7;
         var2[var3++] = (char)(var4 + 48);
         var5 -= var7 * var4;
      }

      return var3;
   }

   private int daylightSavingTime(int var1) {
      return var1 > 23 ? this.dstOffsets[23] : this.dstOffsets[var1];
   }

   public boolean isEquivalent(long var1, int var3, long var4, int var6) {
      if (var1 == var4) {
         return this.secondFractionDigits <= 3 ? (long)var3 / 1000000L == (long)var6 / 1000000L : var3 == var6;
      } else {
         return false;
      }
   }

   public static enum FixedFormat {
      ABSOLUTE("HH:mm:ss,SSS", null, 0, ':', 1, ',', 1, 3, null),
      ABSOLUTE_MICROS("HH:mm:ss,nnnnnn", null, 0, ':', 1, ',', 1, 6, null),
      ABSOLUTE_NANOS("HH:mm:ss,nnnnnnnnn", null, 0, ':', 1, ',', 1, 9, null),
      ABSOLUTE_PERIOD("HH:mm:ss.SSS", null, 0, ':', 1, '.', 1, 3, null),
      COMPACT("yyyyMMddHHmmssSSS", "yyyyMMdd", 0, ' ', 0, ' ', 0, 3, null),
      DATE("dd MMM yyyy HH:mm:ss,SSS", "dd MMM yyyy ", 0, ':', 1, ',', 1, 3, null),
      DATE_PERIOD("dd MMM yyyy HH:mm:ss.SSS", "dd MMM yyyy ", 0, ':', 1, '.', 1, 3, null),
      DEFAULT("yyyy-MM-dd HH:mm:ss,SSS", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 3, null),
      DEFAULT_MICROS("yyyy-MM-dd HH:mm:ss,nnnnnn", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 6, null),
      DEFAULT_NANOS("yyyy-MM-dd HH:mm:ss,nnnnnnnnn", "yyyy-MM-dd ", 0, ':', 1, ',', 1, 9, null),
      DEFAULT_PERIOD("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd ", 0, ':', 1, '.', 1, 3, null),
      ISO8601_BASIC("yyyyMMdd'T'HHmmss,SSS", "yyyyMMdd'T'", 2, ' ', 0, ',', 1, 3, null),
      ISO8601_BASIC_PERIOD("yyyyMMdd'T'HHmmss.SSS", "yyyyMMdd'T'", 2, ' ', 0, '.', 1, 3, null),
      ISO8601("yyyy-MM-dd'T'HH:mm:ss,SSS", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, null),
      ISO8601_OFFSET_DATE_TIME_HH("yyyy-MM-dd'T'HH:mm:ss,SSSX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedDateFormat.FixedTimeZoneFormat.HH),
      ISO8601_OFFSET_DATE_TIME_HHMM("yyyy-MM-dd'T'HH:mm:ss,SSSXX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedDateFormat.FixedTimeZoneFormat.HHMM),
      ISO8601_OFFSET_DATE_TIME_HHCMM("yyyy-MM-dd'T'HH:mm:ss,SSSXXX", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1, 3, FixedDateFormat.FixedTimeZoneFormat.HHCMM),
      ISO8601_PERIOD("yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'", 2, ':', 1, '.', 1, 3, null),
      ISO8601_PERIOD_MICROS("yyyy-MM-dd'T'HH:mm:ss.nnnnnn", "yyyy-MM-dd'T'", 2, ':', 1, '.', 1, 6, null),
      US_MONTH_DAY_YEAR2_TIME("dd/MM/yy HH:mm:ss.SSS", "dd/MM/yy ", 0, ':', 1, '.', 1, 3, null),
      US_MONTH_DAY_YEAR4_TIME("dd/MM/yyyy HH:mm:ss.SSS", "dd/MM/yyyy ", 0, ':', 1, '.', 1, 3, null);

      private static final String DEFAULT_SECOND_FRACTION_PATTERN = "SSS";
      private static final int MILLI_FRACTION_DIGITS = "SSS".length();
      private static final char SECOND_FRACTION_PATTERN = 'n';
      private final String pattern;
      private final String datePattern;
      private final int escapeCount;
      private final char timeSeparatorChar;
      private final int timeSeparatorLength;
      private final char millisSeparatorChar;
      private final int millisSeparatorLength;
      private final int secondFractionDigits;
      private final FixedDateFormat.FixedTimeZoneFormat fixedTimeZoneFormat;
      private static final int[] EMPTY_RANGE = new int[]{-1, -1};

      private FixedFormat(String var3, String var4, int var5, char var6, int var7, char var8, int var9, int var10, FixedDateFormat.FixedTimeZoneFormat var11) {
         this.timeSeparatorChar = var6;
         this.timeSeparatorLength = var7;
         this.millisSeparatorChar = var8;
         this.millisSeparatorLength = var9;
         this.pattern = Objects.requireNonNull(var3);
         this.datePattern = var4;
         this.escapeCount = var5;
         this.secondFractionDigits = var10;
         this.fixedTimeZoneFormat = var11;
      }

      public String getPattern() {
         return this.pattern;
      }

      public String getDatePattern() {
         return this.datePattern;
      }

      public static FixedDateFormat.FixedFormat lookup(String var0) {
         for (FixedDateFormat.FixedFormat var4 : values()) {
            if (var4.name().equals(var0) || var4.getPattern().equals(var0)) {
               return var4;
            }
         }

         return null;
      }

      static FixedDateFormat.FixedFormat lookupIgnoringNanos(String var0) {
         int[] var1 = nanoRange(var0);
         int var2 = var1[0];
         int var3 = var1[1];
         if (var2 > 0) {
            String var4 = var0.substring(0, var2) + "SSS" + var0.substring(var3, var0.length());

            for (FixedDateFormat.FixedFormat var8 : values()) {
               if (var8.getPattern().equals(var4)) {
                  return var8;
               }
            }
         }

         return null;
      }

      private static int[] nanoRange(String var0) {
         int var1 = var0.indexOf(110);
         int var2 = -1;
         if (var1 >= 0) {
            var2 = var0.indexOf(90, var1);
            var2 = var2 < 0 ? var0.indexOf(88, var1) : var2;
            var2 = var2 < 0 ? var0.length() : var2;

            for (int var3 = var1 + 1; var3 < var2; var3++) {
               if (var0.charAt(var3) != 'n') {
                  return EMPTY_RANGE;
               }
            }
         }

         return new int[]{var1, var2};
      }

      public int getLength() {
         return this.pattern.length() - this.escapeCount;
      }

      public int getDatePatternLength() {
         return this.getDatePattern() == null ? 0 : this.getDatePattern().length() - this.escapeCount;
      }

      public FastDateFormat getFastDateFormat() {
         return this.getFastDateFormat(null);
      }

      public FastDateFormat getFastDateFormat(TimeZone var1) {
         return this.getDatePattern() == null ? null : FastDateFormat.getInstance(this.getDatePattern(), var1);
      }

      public int getSecondFractionDigits() {
         return this.secondFractionDigits;
      }

      public FixedDateFormat.FixedTimeZoneFormat getFixedTimeZoneFormat() {
         return this.fixedTimeZoneFormat;
      }
   }

   public static enum FixedTimeZoneFormat {
      HH('\u0000', false, 3),
      HHMM('\u0000', true, 5),
      HHCMM(':', true, 6);

      private final char timeSeparatorChar;
      private final int timeSeparatorCharLen;
      private final boolean useMinutes;
      private final int length;

      private FixedTimeZoneFormat() {
         this('\u0000', true, 4);
      }

      private FixedTimeZoneFormat(char var3, boolean var4, int var5) {
         this.timeSeparatorChar = var3;
         this.timeSeparatorCharLen = var3 != 0 ? 1 : 0;
         this.useMinutes = var4;
         this.length = var5;
      }

      public int getLength() {
         return this.length;
      }

      private int write(int var1, char[] var2, int var3) {
         var2[var3++] = (char)(var1 < 0 ? 45 : 43);
         int var4 = Math.abs(var1);
         int var5 = var4 / 3600000;
         int var6 = var4 - 3600000 * var5;
         int var7 = var5 / 10;
         var2[var3++] = (char)(var7 + 48);
         var2[var3++] = (char)(var5 - 10 * var7 + 48);
         if (this.useMinutes) {
            var2[var3] = this.timeSeparatorChar;
            var3 += this.timeSeparatorCharLen;
            int var8 = var6 / 60000;
            var6 -= 60000 * var8;
            var7 = var8 / 10;
            var2[var3++] = (char)(var7 + 48);
            var2[var3++] = (char)(var8 - 10 * var7 + 48);
         }

         return var3;
      }
   }
}
