package org.apache.logging.log4j.core.appender;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.status.StatusLogger;

public class DefaultErrorHandler implements ErrorHandler {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_EXCEPTION_COUNT = 3;
   private static final long EXCEPTION_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(5L);
   private int exceptionCount = 0;
   private long lastExceptionInstantNanos = System.nanoTime() - EXCEPTION_INTERVAL_NANOS - 1L;
   private final Appender appender;

   public DefaultErrorHandler(Appender var1) {
      this.appender = Objects.requireNonNull(var1, "appender");
   }

   @Override
   public void error(String var1) {
      boolean var2 = this.acquirePermit();
      if (var2) {
         LOGGER.error(var1);
      }
   }

   @Override
   public void error(String var1, Throwable var2) {
      boolean var3 = this.acquirePermit();
      if (var3) {
         LOGGER.error(var1, var2);
      }

      if (!this.appender.ignoreExceptions() && var2 != null && !(var2 instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(var1, var2);
      }
   }

   @Override
   public void error(String var1, LogEvent var2, Throwable var3) {
      boolean var4 = this.acquirePermit();
      if (var4) {
         LOGGER.error(var1, var3);
      }

      if (!this.appender.ignoreExceptions() && var3 != null && !(var3 instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(var1, var3);
      }
   }

   private boolean acquirePermit() {
      long var1 = System.nanoTime();
      synchronized (this) {
         if (var1 - this.lastExceptionInstantNanos > EXCEPTION_INTERVAL_NANOS) {
            this.lastExceptionInstantNanos = var1;
            return true;
         } else if (this.exceptionCount < 3) {
            this.exceptionCount++;
            this.lastExceptionInstantNanos = var1;
            return true;
         } else {
            return false;
         }
      }
   }

   public Appender getAppender() {
      return this.appender;
   }
}
