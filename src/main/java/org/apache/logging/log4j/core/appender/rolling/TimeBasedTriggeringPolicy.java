package org.apache.logging.log4j.core.appender.rolling;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(
   name = "TimeBasedTriggeringPolicy",
   category = "Core",
   printObject = true
)
public final class TimeBasedTriggeringPolicy extends AbstractTriggeringPolicy {
   private long nextRolloverMillis;
   private final int interval;
   private final boolean modulate;
   private final long maxRandomDelayMillis;
   private RollingFileManager manager;

   private TimeBasedTriggeringPolicy(int var1, boolean var2, long var3) {
      this.interval = var1;
      this.modulate = var2;
      this.maxRandomDelayMillis = var3;
   }

   public int getInterval() {
      return this.interval;
   }

   public long getNextRolloverMillis() {
      return this.nextRolloverMillis;
   }

   @Override
   public void initialize(RollingFileManager var1) {
      this.manager = var1;
      long var2 = var1.getFileTime();
      if (var2 == 0L) {
         var2 = System.currentTimeMillis();
      }

      var1.getPatternProcessor().getNextTime(var2, this.interval, this.modulate);
      var1.getPatternProcessor().setTimeBased(true);
      this.nextRolloverMillis = ThreadLocalRandom.current().nextLong(0L, 1L + this.maxRandomDelayMillis)
         + var1.getPatternProcessor().getNextTime(var2, this.interval, this.modulate);
   }

   @Override
   public boolean isTriggeringEvent(LogEvent var1) {
      long var2 = var1.getTimeMillis();
      if (var2 >= this.nextRolloverMillis) {
         this.nextRolloverMillis = ThreadLocalRandom.current().nextLong(0L, 1L + this.maxRandomDelayMillis)
            + this.manager.getPatternProcessor().getNextTime(var2, this.interval, this.modulate);
         this.manager.getPatternProcessor().setCurrentFileTime(System.currentTimeMillis());
         return true;
      } else {
         return false;
      }
   }

   @Deprecated
   public static TimeBasedTriggeringPolicy createPolicy(@PluginAttribute("interval") String var0, @PluginAttribute("modulate") String var1) {
      return newBuilder().withInterval(Integers.parseInt(var0, 1)).withModulate(Boolean.parseBoolean(var1)).build();
   }

   @PluginBuilderFactory
   public static TimeBasedTriggeringPolicy.Builder newBuilder() {
      return new TimeBasedTriggeringPolicy.Builder();
   }

   @Override
   public String toString() {
      return "TimeBasedTriggeringPolicy(nextRolloverMillis=" + this.nextRolloverMillis + ", interval=" + this.interval + ", modulate=" + this.modulate + ")";
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<TimeBasedTriggeringPolicy> {
      @PluginBuilderAttribute
      private int interval = 1;
      @PluginBuilderAttribute
      private boolean modulate = false;
      @PluginBuilderAttribute
      private int maxRandomDelay = 0;

      public TimeBasedTriggeringPolicy build() {
         long var1 = TimeUnit.SECONDS.toMillis((long)this.maxRandomDelay);
         return new TimeBasedTriggeringPolicy(this.interval, this.modulate, var1);
      }

      public int getInterval() {
         return this.interval;
      }

      public boolean isModulate() {
         return this.modulate;
      }

      public int getMaxRandomDelay() {
         return this.maxRandomDelay;
      }

      public TimeBasedTriggeringPolicy.Builder withInterval(int var1) {
         this.interval = var1;
         return this;
      }

      public TimeBasedTriggeringPolicy.Builder withModulate(boolean var1) {
         this.modulate = var1;
         return this;
      }

      public TimeBasedTriggeringPolicy.Builder withMaxRandomDelay(int var1) {
         this.maxRandomDelay = var1;
         return this;
      }
   }
}
