package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public class DefaultReliabilityStrategy implements LocationAwareReliabilityStrategy, ReliabilityStrategy {
   private final LoggerConfig loggerConfig;

   public DefaultReliabilityStrategy(LoggerConfig var1) {
      this.loggerConfig = Objects.requireNonNull(var1, "loggerConfig is null");
   }

   @Override
   public void log(Supplier<LoggerConfig> var1, String var2, String var3, Marker var4, Level var5, Message var6, Throwable var7) {
      this.loggerConfig.log(var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void log(Supplier<LoggerConfig> var1, String var2, String var3, StackTraceElement var4, Marker var5, Level var6, Message var7, Throwable var8) {
      this.loggerConfig.log(var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void log(Supplier<LoggerConfig> var1, LogEvent var2) {
      this.loggerConfig.log(var2);
   }

   @Override
   public LoggerConfig getActiveLoggerConfig(Supplier<LoggerConfig> var1) {
      return this.loggerConfig;
   }

   @Override
   public void afterLogEvent() {
   }

   @Override
   public void beforeStopAppenders() {
   }

   @Override
   public void beforeStopConfiguration(Configuration var1) {
   }
}
