package org.slf4j.impl;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public final class JDK14LoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger {
   private static final long serialVersionUID = -8053026990503422791L;
   final transient Logger logger;
   static String SELF = JDK14LoggerAdapter.class.getName();
   static String SUPER = MarkerIgnoringBase.class.getName();

   JDK14LoggerAdapter(Logger var1) {
      this.logger = var1;
      this.name = var1.getName();
   }

   @Override
   public boolean isTraceEnabled() {
      return this.logger.isLoggable(Level.FINEST);
   }

   @Override
   public void trace(String var1) {
      if (this.logger.isLoggable(Level.FINEST)) {
         this.log(SELF, Level.FINEST, var1, null);
      }
   }

   @Override
   public void trace(String var1, Object var2) {
      if (this.logger.isLoggable(Level.FINEST)) {
         FormattingTuple var3 = MessageFormatter.format(var1, var2);
         this.log(SELF, Level.FINEST, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void trace(String var1, Object var2, Object var3) {
      if (this.logger.isLoggable(Level.FINEST)) {
         FormattingTuple var4 = MessageFormatter.format(var1, var2, var3);
         this.log(SELF, Level.FINEST, var4.getMessage(), var4.getThrowable());
      }
   }

   @Override
   public void trace(String var1, Object... var2) {
      if (this.logger.isLoggable(Level.FINEST)) {
         FormattingTuple var3 = MessageFormatter.arrayFormat(var1, var2);
         this.log(SELF, Level.FINEST, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void trace(String var1, Throwable var2) {
      if (this.logger.isLoggable(Level.FINEST)) {
         this.log(SELF, Level.FINEST, var1, var2);
      }
   }

   @Override
   public boolean isDebugEnabled() {
      return this.logger.isLoggable(Level.FINE);
   }

   @Override
   public void debug(String var1) {
      if (this.logger.isLoggable(Level.FINE)) {
         this.log(SELF, Level.FINE, var1, null);
      }
   }

   @Override
   public void debug(String var1, Object var2) {
      if (this.logger.isLoggable(Level.FINE)) {
         FormattingTuple var3 = MessageFormatter.format(var1, var2);
         this.log(SELF, Level.FINE, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void debug(String var1, Object var2, Object var3) {
      if (this.logger.isLoggable(Level.FINE)) {
         FormattingTuple var4 = MessageFormatter.format(var1, var2, var3);
         this.log(SELF, Level.FINE, var4.getMessage(), var4.getThrowable());
      }
   }

   @Override
   public void debug(String var1, Object... var2) {
      if (this.logger.isLoggable(Level.FINE)) {
         FormattingTuple var3 = MessageFormatter.arrayFormat(var1, var2);
         this.log(SELF, Level.FINE, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void debug(String var1, Throwable var2) {
      if (this.logger.isLoggable(Level.FINE)) {
         this.log(SELF, Level.FINE, var1, var2);
      }
   }

   @Override
   public boolean isInfoEnabled() {
      return this.logger.isLoggable(Level.INFO);
   }

   @Override
   public void info(String var1) {
      if (this.logger.isLoggable(Level.INFO)) {
         this.log(SELF, Level.INFO, var1, null);
      }
   }

   @Override
   public void info(String var1, Object var2) {
      if (this.logger.isLoggable(Level.INFO)) {
         FormattingTuple var3 = MessageFormatter.format(var1, var2);
         this.log(SELF, Level.INFO, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void info(String var1, Object var2, Object var3) {
      if (this.logger.isLoggable(Level.INFO)) {
         FormattingTuple var4 = MessageFormatter.format(var1, var2, var3);
         this.log(SELF, Level.INFO, var4.getMessage(), var4.getThrowable());
      }
   }

   @Override
   public void info(String var1, Object... var2) {
      if (this.logger.isLoggable(Level.INFO)) {
         FormattingTuple var3 = MessageFormatter.arrayFormat(var1, var2);
         this.log(SELF, Level.INFO, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void info(String var1, Throwable var2) {
      if (this.logger.isLoggable(Level.INFO)) {
         this.log(SELF, Level.INFO, var1, var2);
      }
   }

   @Override
   public boolean isWarnEnabled() {
      return this.logger.isLoggable(Level.WARNING);
   }

   @Override
   public void warn(String var1) {
      if (this.logger.isLoggable(Level.WARNING)) {
         this.log(SELF, Level.WARNING, var1, null);
      }
   }

   @Override
   public void warn(String var1, Object var2) {
      if (this.logger.isLoggable(Level.WARNING)) {
         FormattingTuple var3 = MessageFormatter.format(var1, var2);
         this.log(SELF, Level.WARNING, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void warn(String var1, Object var2, Object var3) {
      if (this.logger.isLoggable(Level.WARNING)) {
         FormattingTuple var4 = MessageFormatter.format(var1, var2, var3);
         this.log(SELF, Level.WARNING, var4.getMessage(), var4.getThrowable());
      }
   }

   @Override
   public void warn(String var1, Object... var2) {
      if (this.logger.isLoggable(Level.WARNING)) {
         FormattingTuple var3 = MessageFormatter.arrayFormat(var1, var2);
         this.log(SELF, Level.WARNING, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void warn(String var1, Throwable var2) {
      if (this.logger.isLoggable(Level.WARNING)) {
         this.log(SELF, Level.WARNING, var1, var2);
      }
   }

   @Override
   public boolean isErrorEnabled() {
      return this.logger.isLoggable(Level.SEVERE);
   }

   @Override
   public void error(String var1) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         this.log(SELF, Level.SEVERE, var1, null);
      }
   }

   @Override
   public void error(String var1, Object var2) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         FormattingTuple var3 = MessageFormatter.format(var1, var2);
         this.log(SELF, Level.SEVERE, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void error(String var1, Object var2, Object var3) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         FormattingTuple var4 = MessageFormatter.format(var1, var2, var3);
         this.log(SELF, Level.SEVERE, var4.getMessage(), var4.getThrowable());
      }
   }

   @Override
   public void error(String var1, Object... var2) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         FormattingTuple var3 = MessageFormatter.arrayFormat(var1, var2);
         this.log(SELF, Level.SEVERE, var3.getMessage(), var3.getThrowable());
      }
   }

   @Override
   public void error(String var1, Throwable var2) {
      if (this.logger.isLoggable(Level.SEVERE)) {
         this.log(SELF, Level.SEVERE, var1, var2);
      }
   }

   private void log(String var1, Level var2, String var3, Throwable var4) {
      LogRecord var5 = new LogRecord(var2, var3);
      var5.setLoggerName(this.getName());
      var5.setThrown(var4);
      this.fillCallerData(var1, var5);
      this.logger.log(var5);
   }

   private final void fillCallerData(String var1, LogRecord var2) {
      StackTraceElement[] var3 = new Throwable().getStackTrace();
      int var4 = -1;

      for (int var5 = 0; var5 < var3.length; var5++) {
         String var6 = var3[var5].getClassName();
         if (var6.equals(var1) || var6.equals(SUPER)) {
            var4 = var5;
            break;
         }
      }

      int var8 = -1;

      for (int var9 = var4 + 1; var9 < var3.length; var9++) {
         String var7 = var3[var9].getClassName();
         if (!var7.equals(var1) && !var7.equals(SUPER)) {
            var8 = var9;
            break;
         }
      }

      if (var8 != -1) {
         StackTraceElement var10 = var3[var8];
         var2.setSourceClassName(var10.getClassName());
         var2.setSourceMethodName(var10.getMethodName());
      }
   }

   @Override
   public void log(Marker var1, String var2, int var3, String var4, Object[] var5, Throwable var6) {
      Level var7 = this.slf4jLevelIntToJULLevel(var3);
      if (this.logger.isLoggable(var7)) {
         this.log(var2, var7, var4, var6);
      }
   }

   private Level slf4jLevelIntToJULLevel(int var1) {
      Level var2;
      switch (var1) {
         case 0:
            var2 = Level.FINEST;
            break;
         case 10:
            var2 = Level.FINE;
            break;
         case 20:
            var2 = Level.INFO;
            break;
         case 30:
            var2 = Level.WARNING;
            break;
         case 40:
            var2 = Level.SEVERE;
            break;
         default:
            throw new IllegalStateException("Level number " + var1 + " is not recognized.");
      }

      return var2;
   }

   public void log(LoggingEvent var1) {
      Level var2 = this.slf4jLevelIntToJULLevel(var1.getLevel().toInt());
      if (this.logger.isLoggable(var2)) {
         LogRecord var3 = this.eventToRecord(var1, var2);
         this.logger.log(var3);
      }
   }

   private LogRecord eventToRecord(LoggingEvent var1, Level var2) {
      String var3 = var1.getMessage();
      Object[] var4 = var1.getArgumentArray();
      FormattingTuple var5 = MessageFormatter.arrayFormat(var3, var4);
      if (var5.getThrowable() != null && var1.getThrowable() != null) {
         throw new IllegalArgumentException("both last element in argument array and last argument are of type Throwable");
      } else {
         Throwable var6 = var1.getThrowable();
         if (var5.getThrowable() != null) {
            var6 = var5.getThrowable();
            throw new IllegalStateException("fix above code");
         } else {
            LogRecord var7 = new LogRecord(var2, var5.getMessage());
            var7.setLoggerName(var1.getLoggerName());
            var7.setMillis(var1.getTimeStamp());
            var7.setSourceClassName("NA/SubstituteLogger");
            var7.setSourceMethodName("NA/SubstituteLogger");
            var7.setThrown(var6);
            return var7;
         }
      }
   }
}
