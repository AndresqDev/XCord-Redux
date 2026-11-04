package org.apache.logging.log4j.jul;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.status.StatusLogger;

public class Log4jBridgeHandler extends Handler implements PropertyChangeListener {
   private static final Logger SLOGGER = StatusLogger.getLogger();
   private static final String FQCN = java.util.logging.Logger.class.getName();
   private static final String UNKNOWN_LOGGER_NAME = "unknown.jul.logger";
   private static final Formatter julFormatter = new SimpleFormatter();
   private boolean doDebugOutput = false;
   private String julSuffixToAppend = null;
   private Set<java.util.logging.Logger> julLoggerRefs;

   public static void install(boolean var0, String var1, boolean var2) {
      java.util.logging.Logger var3 = getJulRootLogger();
      if (var0) {
         for (Handler var7 : var3.getHandlers()) {
            var3.removeHandler(var7);
         }
      }

      var3.addHandler(new Log4jBridgeHandler(false, var1, var2));
   }

   private static java.util.logging.Logger getJulRootLogger() {
      return java.util.logging.LogManager.getLogManager().getLogger("");
   }

   public Log4jBridgeHandler() {
      java.util.logging.LogManager var1 = java.util.logging.LogManager.getLogManager();
      String var2 = this.getClass().getName();
      this.init(
         Boolean.parseBoolean(var1.getProperty(var2 + ".sysoutDebug")),
         var1.getProperty(var2 + ".appendSuffix"),
         Boolean.parseBoolean(var1.getProperty(var2 + ".propagateLevels"))
      );
   }

   public Log4jBridgeHandler(boolean var1, String var2, boolean var3) {
      this.init(var1, var2, var3);
   }

   protected void init(boolean var1, String var2, boolean var3) {
      this.doDebugOutput = var1;
      if (var1) {
         new Exception("DIAGNOSTIC ONLY (sysout):  Log4jBridgeHandler instance created (" + this + ")").printStackTrace(System.out);
      }

      if (var2 != null) {
         var2 = var2.trim();
         if (var2.isEmpty()) {
            var2 = null;
         } else if (var2.charAt(0) != '.') {
            var2 = '.' + var2;
         }
      }

      this.julSuffixToAppend = var2;
      if (var3) {
         LoggerContext var4 = LoggerContext.getContext(false);
         var4.addPropertyChangeListener(this);
         this.propagateLogLevels(var4.getConfiguration());
      }

      SLOGGER.debug("Log4jBridgeHandler init. with: suffix='{}', lvlProp={}, instance={}", var2, var3, this);
   }

   @Override
   public void close() {
      this.julLoggerRefs = null;
      LoggerContext.getContext(false).removePropertyChangeListener(this);
      if (this.doDebugOutput) {
         System.out.println("sysout:  Log4jBridgeHandler close(): " + this);
      }
   }

   @Override
   public void publish(LogRecord var1) {
      if (var1 != null) {
         Logger var2 = this.getLog4jLogger(var1);
         String var3 = julFormatter.formatMessage(var1);
         Level var4 = LevelTranslator.toLevel(var1.getLevel());
         Throwable var5 = var1.getThrown();
         if (var2 instanceof ExtendedLogger) {
            try {
               ((ExtendedLogger)var2).logIfEnabled(FQCN, var4, null, var3, var5);
            } catch (NoClassDefFoundError var7) {
               var2.warn("Log4jBridgeHandler: ignored exception when calling 'ExtendedLogger': {}", var7.toString());
               var2.log(var4, var3, var5);
            }
         } else {
            var2.log(var4, var3, var5);
         }
      }
   }

   @Override
   public void flush() {
   }

   private Logger getLog4jLogger(LogRecord var1) {
      String var2 = var1.getLoggerName();
      if (var2 == null) {
         var2 = "unknown.jul.logger";
      } else if (this.julSuffixToAppend != null) {
         var2 = var2 + this.julSuffixToAppend;
      }

      return org.apache.logging.log4j.LogManager.getLogger(var2);
   }

   @Override
   public void propertyChange(PropertyChangeEvent var1) {
      SLOGGER.debug("Log4jBridgeHandler.propertyChange(): {}", var1);
      if ("config".equals(var1.getPropertyName()) && var1.getNewValue() instanceof Configuration) {
         this.propagateLogLevels((Configuration)var1.getNewValue());
      }
   }

   private void propagateLogLevels(Configuration var1) {
      SLOGGER.debug("Log4jBridgeHandler.propagateLogLevels(): {}", var1);
      if (this.julLoggerRefs == null) {
         this.julLoggerRefs = new HashSet<>();
      } else {
         this.julLoggerRefs.clear();
      }

      Map var2 = var1.getLoggers();

      for (LoggerConfig var4 : var2.values()) {
         java.util.logging.Logger var5 = java.util.logging.Logger.getLogger(var4.getName());
         java.util.logging.Level var6 = LevelTranslator.toJavaLevel(var4.getLevel());
         var5.setLevel(var6);
         this.julLoggerRefs.add(var5);
      }

      java.util.logging.LogManager var7 = java.util.logging.LogManager.getLogManager();
      Enumeration var8 = var7.getLoggerNames();

      while (var8.hasMoreElements()) {
         java.util.logging.Logger var9 = var7.getLogger((String)var8.nextElement());
         if (var9 != null && var9.getLevel() != null && !"".equals(var9.getName()) && !var2.containsKey(var9.getName())) {
            var9.setLevel(null);
         }
      }
   }
}
