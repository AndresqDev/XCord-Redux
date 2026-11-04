package io.netty.util.internal.logging;

import org.slf4j.spi.LocationAwareLogger;

final class LocationAwareSlf4JLogger extends AbstractInternalLogger {
   static final String FQCN = LocationAwareSlf4JLogger.class.getName();
   private static final long serialVersionUID = -8292030083201538180L;
   private final transient LocationAwareLogger logger;

   LocationAwareSlf4JLogger(LocationAwareLogger var1) {
      super(var1.getName());
      this.logger = var1;
   }

   private void log(int var1, String var2) {
      this.logger.log(null, FQCN, var1, var2, null, null);
   }

   private void log(int var1, String var2, Throwable var3) {
      this.logger.log(null, FQCN, var1, var2, null, var3);
   }

   private void log(int var1, org.slf4j.helpers.FormattingTuple var2) {
      this.logger.log(null, FQCN, var1, var2.getMessage(), var2.getArgArray(), var2.getThrowable());
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   @Override
   public void trace(String var1) {
      if (this.isTraceEnabled()) {
         this.log(0, var1);
      }
   }

   @Override
   public void trace(String var1, Object var2) {
      if (this.isTraceEnabled()) {
         this.log(0, org.slf4j.helpers.MessageFormatter.format(var1, var2));
      }
   }

   @Override
   public void trace(String var1, Object var2, Object var3) {
      if (this.isTraceEnabled()) {
         this.log(0, org.slf4j.helpers.MessageFormatter.format(var1, var2, var3));
      }
   }

   @Override
   public void trace(String var1, Object... var2) {
      if (this.isTraceEnabled()) {
         this.log(0, org.slf4j.helpers.MessageFormatter.arrayFormat(var1, var2));
      }
   }

   @Override
   public void trace(String var1, Throwable var2) {
      if (this.isTraceEnabled()) {
         this.log(0, var1, var2);
      }
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   @Override
   public void debug(String var1) {
      if (this.isDebugEnabled()) {
         this.log(10, var1);
      }
   }

   @Override
   public void debug(String var1, Object var2) {
      if (this.isDebugEnabled()) {
         this.log(10, org.slf4j.helpers.MessageFormatter.format(var1, var2));
      }
   }

   @Override
   public void debug(String var1, Object var2, Object var3) {
      if (this.isDebugEnabled()) {
         this.log(10, org.slf4j.helpers.MessageFormatter.format(var1, var2, var3));
      }
   }

   @Override
   public void debug(String var1, Object... var2) {
      if (this.isDebugEnabled()) {
         this.log(10, org.slf4j.helpers.MessageFormatter.arrayFormat(var1, var2));
      }
   }

   @Override
   public void debug(String var1, Throwable var2) {
      if (this.isDebugEnabled()) {
         this.log(10, var1, var2);
      }
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   @Override
   public void info(String var1) {
      if (this.isInfoEnabled()) {
         this.log(20, var1);
      }
   }

   @Override
   public void info(String var1, Object var2) {
      if (this.isInfoEnabled()) {
         this.log(20, org.slf4j.helpers.MessageFormatter.format(var1, var2));
      }
   }

   @Override
   public void info(String var1, Object var2, Object var3) {
      if (this.isInfoEnabled()) {
         this.log(20, org.slf4j.helpers.MessageFormatter.format(var1, var2, var3));
      }
   }

   @Override
   public void info(String var1, Object... var2) {
      if (this.isInfoEnabled()) {
         this.log(20, org.slf4j.helpers.MessageFormatter.arrayFormat(var1, var2));
      }
   }

   @Override
   public void info(String var1, Throwable var2) {
      if (this.isInfoEnabled()) {
         this.log(20, var1, var2);
      }
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
   }

   @Override
   public void warn(String var1) {
      if (this.isWarnEnabled()) {
         this.log(30, var1);
      }
   }

   @Override
   public void warn(String var1, Object var2) {
      if (this.isWarnEnabled()) {
         this.log(30, org.slf4j.helpers.MessageFormatter.format(var1, var2));
      }
   }

   @Override
   public void warn(String var1, Object... var2) {
      if (this.isWarnEnabled()) {
         this.log(30, org.slf4j.helpers.MessageFormatter.arrayFormat(var1, var2));
      }
   }

   @Override
   public void warn(String var1, Object var2, Object var3) {
      if (this.isWarnEnabled()) {
         this.log(30, org.slf4j.helpers.MessageFormatter.format(var1, var2, var3));
      }
   }

   @Override
   public void warn(String var1, Throwable var2) {
      if (this.isWarnEnabled()) {
         this.log(30, var1, var2);
      }
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   @Override
   public void error(String var1) {
      if (this.isErrorEnabled()) {
         this.log(40, var1);
      }
   }

   @Override
   public void error(String var1, Object var2) {
      if (this.isErrorEnabled()) {
         this.log(40, org.slf4j.helpers.MessageFormatter.format(var1, var2));
      }
   }

   @Override
   public void error(String var1, Object var2, Object var3) {
      if (this.isErrorEnabled()) {
         this.log(40, org.slf4j.helpers.MessageFormatter.format(var1, var2, var3));
      }
   }

   @Override
   public void error(String var1, Object... var2) {
      if (this.isErrorEnabled()) {
         this.log(40, org.slf4j.helpers.MessageFormatter.arrayFormat(var1, var2));
      }
   }

   @Override
   public void error(String var1, Throwable var2) {
      if (this.isErrorEnabled()) {
         this.log(40, var1, var2);
      }
   }
}
