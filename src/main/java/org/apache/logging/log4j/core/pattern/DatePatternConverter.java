package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.FixedDateFormat;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "DatePatternConverter",
   category = "Converter"
)
@ConverterKeys({"d", "date"})
@PerformanceSensitive({"allocation"})
public final class DatePatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private static final String UNIX_FORMAT = "UNIX";
   private static final String UNIX_MILLIS_FORMAT = "UNIX_MILLIS";
   private final String[] options;
   private final ThreadLocal<MutableInstant> threadLocalMutableInstant = new ThreadLocal<>();
   private final ThreadLocal<DatePatternConverter.Formatter> threadLocalFormatter = new ThreadLocal<>();
   private final AtomicReference<DatePatternConverter.CachedTime> cachedTime;
   private final DatePatternConverter.Formatter formatter;

   private DatePatternConverter(String[] var1) {
      super("Date", "date");
      this.options = var1 == null ? null : Arrays.copyOf(var1, var1.length);
      this.formatter = this.createFormatter(var1);
      this.cachedTime = new AtomicReference<>(this.fromEpochMillis(System.currentTimeMillis()));
   }

   private DatePatternConverter.CachedTime fromEpochMillis(long var1) {
      MutableInstant var3 = new MutableInstant();
      var3.initFromEpochMilli(var1, 0);
      return new DatePatternConverter.CachedTime(var3);
   }

   private DatePatternConverter.Formatter createFormatter(String[] var1) {
      FixedDateFormat var2 = FixedDateFormat.createIfSupported(var1);
      return var2 != null ? createFixedFormatter(var2) : createNonFixedFormatter(var1);
   }

   public static DatePatternConverter newInstance(String[] var0) {
      return new DatePatternConverter(var0);
   }

   private static DatePatternConverter.Formatter createFixedFormatter(FixedDateFormat var0) {
      return new DatePatternConverter.FixedFormatter(var0);
   }

   private static DatePatternConverter.Formatter createNonFixedFormatter(String[] var0) {
      Objects.requireNonNull(var0);
      if (var0.length == 0) {
         throw new IllegalArgumentException("Options array must have at least one element");
      } else {
         Objects.requireNonNull(var0[0]);
         String var1 = var0[0];
         if ("UNIX".equals(var1)) {
            return new DatePatternConverter.UnixFormatter();
         } else if ("UNIX_MILLIS".equals(var1)) {
            return new DatePatternConverter.UnixMillisFormatter();
         } else {
            FixedDateFormat.FixedFormat var2 = FixedDateFormat.FixedFormat.lookup(var1);
            String var3 = var2 == null ? var1 : var2.getPattern();
            TimeZone var4 = null;
            if (var0.length > 1 && var0[1] != null) {
               var4 = TimeZone.getTimeZone(var0[1]);
            }

            try {
               FastDateFormat var5 = FastDateFormat.getInstance(var3, var4);
               return new DatePatternConverter.PatternFormatter(var5);
            } catch (IllegalArgumentException var6) {
               LOGGER.warn("Could not instantiate FastDateFormat with pattern " + var3, (Throwable)var6);
               return createFixedFormatter(FixedDateFormat.create(FixedDateFormat.FixedFormat.DEFAULT, var4));
            }
         }
      }
   }

   public void format(Date var1, StringBuilder var2) {
      this.format(var1.getTime(), var2);
   }

   @Override
   public void format(LogEvent var1, StringBuilder var2) {
      this.format(var1.getInstant(), var2);
   }

   public void format(long var1, StringBuilder var3) {
      MutableInstant var4 = this.getMutableInstant();
      var4.initFromEpochMilli(var1, 0);
      this.format((Instant)var4, var3);
   }

   private MutableInstant getMutableInstant() {
      if (Constants.ENABLE_THREADLOCALS) {
         MutableInstant var1 = this.threadLocalMutableInstant.get();
         if (var1 == null) {
            var1 = new MutableInstant();
            this.threadLocalMutableInstant.set(var1);
         }

         return var1;
      } else {
         return new MutableInstant();
      }
   }

   public void format(Instant var1, StringBuilder var2) {
      if (Constants.ENABLE_THREADLOCALS) {
         this.formatWithoutAllocation(var1, var2);
      } else {
         this.formatWithoutThreadLocals(var1, var2);
      }
   }

   private void formatWithoutAllocation(Instant var1, StringBuilder var2) {
      this.getThreadLocalFormatter().formatToBuffer(var1, var2);
   }

   private DatePatternConverter.Formatter getThreadLocalFormatter() {
      DatePatternConverter.Formatter var1 = this.threadLocalFormatter.get();
      if (var1 == null) {
         var1 = this.createFormatter(this.options);
         this.threadLocalFormatter.set(var1);
      }

      return var1;
   }

   private void formatWithoutThreadLocals(Instant var1, StringBuilder var2) {
      DatePatternConverter.CachedTime var3 = this.cachedTime.get();
      if (var1.getEpochSecond() != var3.epochSecond || var1.getNanoOfSecond() != var3.nanoOfSecond) {
         DatePatternConverter.CachedTime var4 = new DatePatternConverter.CachedTime(var1);
         if (this.cachedTime.compareAndSet(var3, var4)) {
            var3 = var4;
         } else {
            var3 = this.cachedTime.get();
         }
      }

      var2.append(var3.formatted);
   }

   @Override
   public void format(Object var1, StringBuilder var2) {
      if (var1 instanceof Date) {
         this.format((Date)var1, var2);
      }

      super.format(var1, var2);
   }

   @Override
   public void format(StringBuilder var1, Object... var2) {
      for (Object var6 : var2) {
         if (var6 instanceof Date) {
            this.format(var6, var1);
            break;
         }
      }
   }

   public String getPattern() {
      return this.formatter.toPattern();
   }

   private final class CachedTime {
      public long epochSecond;
      public int nanoOfSecond;
      public String formatted;

      public CachedTime(Instant var2) {
         this.epochSecond = var2.getEpochSecond();
         this.nanoOfSecond = var2.getNanoOfSecond();
         this.formatted = DatePatternConverter.this.formatter.format(var2);
      }
   }

   private static final class FixedFormatter extends DatePatternConverter.Formatter {
      private final FixedDateFormat fixedDateFormat;
      private final char[] cachedBuffer = new char[70];
      private int length = 0;

      FixedFormatter(FixedDateFormat var1) {
         this.fixedDateFormat = var1;
      }

      @Override
      String format(Instant var1) {
         return this.fixedDateFormat.formatInstant(var1);
      }

      @Override
      void formatToBuffer(Instant var1, StringBuilder var2) {
         long var3 = var1.getEpochSecond();
         int var5 = var1.getNanoOfSecond();
         if (!this.fixedDateFormat.isEquivalent(this.previousTime, this.nanos, var3, var5)) {
            this.length = this.fixedDateFormat.formatInstant(var1, this.cachedBuffer, 0);
            this.previousTime = var3;
            this.nanos = var5;
         }

         var2.append(this.cachedBuffer, 0, this.length);
      }

      @Override
      public String toPattern() {
         return this.fixedDateFormat.getFormat();
      }
   }

   private abstract static class Formatter {
      long previousTime;
      int nanos;

      private Formatter() {
      }

      abstract String format(Instant var1);

      abstract void formatToBuffer(Instant var1, StringBuilder var2);

      public String toPattern() {
         return null;
      }
   }

   private static final class PatternFormatter extends DatePatternConverter.Formatter {
      private final FastDateFormat fastDateFormat;
      private final StringBuilder cachedBuffer = new StringBuilder(64);

      PatternFormatter(FastDateFormat var1) {
         this.fastDateFormat = var1;
      }

      @Override
      String format(Instant var1) {
         return this.fastDateFormat.format(var1.getEpochMillisecond());
      }

      @Override
      void formatToBuffer(Instant var1, StringBuilder var2) {
         long var3 = var1.getEpochMillisecond();
         if (this.previousTime != var3) {
            this.cachedBuffer.setLength(0);
            this.fastDateFormat.format(var3, this.cachedBuffer);
         }

         var2.append((CharSequence)this.cachedBuffer);
      }

      @Override
      public String toPattern() {
         return this.fastDateFormat.getPattern();
      }
   }

   private static final class UnixFormatter extends DatePatternConverter.Formatter {
      private UnixFormatter() {
      }

      @Override
      String format(Instant var1) {
         return Long.toString(var1.getEpochSecond());
      }

      @Override
      void formatToBuffer(Instant var1, StringBuilder var2) {
         var2.append(var1.getEpochSecond());
      }
   }

   private static final class UnixMillisFormatter extends DatePatternConverter.Formatter {
      private UnixMillisFormatter() {
      }

      @Override
      String format(Instant var1) {
         return Long.toString(var1.getEpochMillisecond());
      }

      @Override
      void formatToBuffer(Instant var1, StringBuilder var2) {
         var2.append(var1.getEpochMillisecond());
      }
   }
}
