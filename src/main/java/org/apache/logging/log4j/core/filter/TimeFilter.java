package org.apache.logging.log4j.core.filter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "TimeFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class TimeFilter extends AbstractFilter {
   private static final Clock CLOCK = ClockFactory.getClock();
   private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final long HOUR_MS = 3600000L;
   private static final long DAY_MS = 86400000L;
   private volatile long start;
   private final LocalTime startTime;
   private volatile long end;
   private final LocalTime endTime;
   private final long duration;
   private final ZoneId timeZone;

   TimeFilter(LocalTime var1, LocalTime var2, ZoneId var3, Filter.Result var4, Filter.Result var5, LocalDate var6) {
      super(var4, var5);
      this.startTime = var1;
      this.endTime = var2;
      this.timeZone = var3;
      this.start = ZonedDateTime.of(var6, this.startTime, var3).withEarlierOffsetAtOverlap().toInstant().toEpochMilli();
      long var7 = ZonedDateTime.of(var6, this.endTime, var3).withEarlierOffsetAtOverlap().toInstant().toEpochMilli();
      if (var2.isBefore(var1)) {
         var7 += 86400000L;
      }

      this.duration = this.startTime.isBefore(this.endTime)
         ? Duration.between(this.startTime, this.endTime).toMillis()
         : Duration.between(this.startTime, this.endTime).plusHours(24L).toMillis();
      long var9 = var7 - this.start - this.duration;
      if (var9 != 0L) {
         var7 -= var9;
      }

      this.end = var7;
   }

   private TimeFilter(LocalTime var1, LocalTime var2, ZoneId var3, Filter.Result var4, Filter.Result var5) {
      this(var1, var2, var3, var4, var5, LocalDate.now(var3));
   }

   private synchronized void adjustTimes(long var1) {
      if (var1 > this.end) {
         LocalDate var3 = Instant.ofEpochMilli(var1).atZone(this.timeZone).toLocalDate();
         this.start = ZonedDateTime.of(var3, this.startTime, this.timeZone).withEarlierOffsetAtOverlap().toInstant().toEpochMilli();
         long var4 = ZonedDateTime.of(var3, this.endTime, this.timeZone).withEarlierOffsetAtOverlap().toInstant().toEpochMilli();
         if (this.endTime.isBefore(this.startTime)) {
            var4 += 86400000L;
         }

         long var6 = var4 - this.start - this.duration;
         if (var6 != 0L) {
            var4 -= var6;
         }

         this.end = var4;
      }
   }

   Filter.Result filter(long var1) {
      if (var1 > this.end) {
         this.adjustTimes(var1);
      }

      return var1 >= this.start && var1 <= this.end ? this.onMatch : this.onMismatch;
   }

   @Override
   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getTimeMillis());
   }

   private Filter.Result filter() {
      return this.filter(CLOCK.currentTimeMillis());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(
      Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      return this.filter();
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13,
      Object var14
   ) {
      return this.filter();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("start=").append(this.start);
      var1.append(", end=").append(this.end);
      var1.append(", timezone=").append(this.timeZone.toString());
      return var1.toString();
   }

   @PluginFactory
   public static TimeFilter createFilter(
      @PluginAttribute("start") String var0,
      @PluginAttribute("end") String var1,
      @PluginAttribute("timezone") String var2,
      @PluginAttribute("onMatch") Filter.Result var3,
      @PluginAttribute("onMismatch") Filter.Result var4
   ) {
      LocalTime var5 = parseTimestamp(var0, LocalTime.MIN);
      LocalTime var6 = parseTimestamp(var1, LocalTime.MAX);
      ZoneId var7 = var2 == null ? ZoneId.systemDefault() : ZoneId.of(var2);
      Filter.Result var8 = var3 == null ? Filter.Result.NEUTRAL : var3;
      Filter.Result var9 = var4 == null ? Filter.Result.DENY : var4;
      return new TimeFilter(var5, var6, var7, var8, var9);
   }

   private static LocalTime parseTimestamp(String var0, LocalTime var1) {
      if (var0 == null) {
         return var1;
      } else {
         try {
            return LocalTime.parse(var0, FORMATTER);
         } catch (Exception var3) {
            LOGGER.warn("Error parsing TimeFilter timestamp value {}", var0, var3);
            return var1;
         }
      }
   }
}
