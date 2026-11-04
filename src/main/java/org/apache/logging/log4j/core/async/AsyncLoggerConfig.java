package org.apache.logging.log4j.core.async;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.spi.AbstractLogger;

@Plugin(
   name = "asyncLogger",
   category = "Core",
   printObject = true
)
public class AsyncLoggerConfig extends LoggerConfig {
   private static final ThreadLocal<Boolean> ASYNC_LOGGER_ENTERED = new ThreadLocal<Boolean>() {
      protected Boolean initialValue() {
         return Boolean.FALSE;
      }
   };
   private final AsyncLoggerConfigDelegate delegate;

   protected AsyncLoggerConfig(String var1, List<AppenderRef> var2, Filter var3, Level var4, boolean var5, Property[] var6, Configuration var7, boolean var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.delegate = var7.getAsyncLoggerConfigDelegate();
      this.delegate.setLogEventFactory(this.getLogEventFactory());
   }

   @Override
   protected void log(LogEvent var1, LoggerConfig.LoggerConfigPredicate var2) {
      if (var2 == LoggerConfig.LoggerConfigPredicate.ALL && ASYNC_LOGGER_ENTERED.get() == Boolean.FALSE && this.hasAppenders()) {
         ASYNC_LOGGER_ENTERED.set(Boolean.TRUE);

         try {
            super.log(var1, LoggerConfig.LoggerConfigPredicate.SYNCHRONOUS_ONLY);
            this.logToAsyncDelegate(var1);
         } finally {
            ASYNC_LOGGER_ENTERED.set(Boolean.FALSE);
         }
      } else {
         super.log(var1, var2);
      }
   }

   @Override
   protected void callAppenders(LogEvent var1) {
      super.callAppenders(var1);
   }

   private void logToAsyncDelegate(LogEvent var1) {
      if (!this.isFiltered(var1)) {
         this.populateLazilyInitializedFields(var1);
         if (!this.delegate.tryEnqueue(var1, this)) {
            this.handleQueueFull(var1);
         }
      }
   }

   private void handleQueueFull(LogEvent var1) {
      if (AbstractLogger.getRecursionDepth() > 1) {
         AsyncQueueFullMessageUtil.logWarningToStatusLogger();
         this.logToAsyncLoggerConfigsOnCurrentThread(var1);
      } else {
         EventRoute var2 = this.delegate.getEventRoute(var1.getLevel());
         var2.logMessage(this, var1);
      }
   }

   private void populateLazilyInitializedFields(LogEvent var1) {
      var1.getSource();
      var1.getThreadName();
   }

   void logInBackgroundThread(LogEvent var1) {
      this.delegate.enqueueEvent(var1, this);
   }

   void logToAsyncLoggerConfigsOnCurrentThread(LogEvent var1) {
      this.log(var1, LoggerConfig.LoggerConfigPredicate.ASYNCHRONOUS_ONLY);
   }

   private String displayName() {
      return "".equals(this.getName()) ? "root" : this.getName();
   }

   @Override
   public void start() {
      LOGGER.trace("AsyncLoggerConfig[{}] starting...", this.displayName());
      super.start();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      LOGGER.trace("AsyncLoggerConfig[{}] stopping...", this.displayName());
      this.setStopped();
      return true;
   }

   public RingBufferAdmin createRingBufferAdmin(String var1) {
      return this.delegate.createRingBufferAdmin(var1, this.getName());
   }

   @Deprecated
   public static LoggerConfig createLogger(
      String var0, String var1, String var2, String var3, AppenderRef[] var4, Property[] var5, Configuration var6, Filter var7
   ) {
      if (var2 == null) {
         LOGGER.error("Loggers cannot be configured without a name");
         return null;
      } else {
         List var8 = Arrays.asList(var4);

         Level var9;
         try {
            var9 = Level.toLevel(var1, Level.ERROR);
         } catch (Exception var12) {
            LOGGER.error("Invalid Log level specified: {}. Defaulting to Error", var1);
            var9 = Level.ERROR;
         }

         String var10 = var2.equals("root") ? "" : var2;
         boolean var11 = Booleans.parseBoolean(var0, true);
         return new AsyncLoggerConfig(var10, var8, var7, var9, var11, var5, var6, includeLocation(var3));
      }
   }

   @PluginFactory
   public static LoggerConfig createLogger(
      @PluginAttribute(value = "additivity",defaultBoolean = true) boolean var0,
      @PluginAttribute("level") Level var1,
      @Required(message = "Loggers cannot be configured without a name") @PluginAttribute("name") String var2,
      @PluginAttribute("includeLocation") String var3,
      @PluginElement("AppenderRef") AppenderRef[] var4,
      @PluginElement("Properties") Property[] var5,
      @PluginConfiguration Configuration var6,
      @PluginElement("Filter") Filter var7
   ) {
      String var8 = var2.equals("root") ? "" : var2;
      return new AsyncLoggerConfig(var8, Arrays.asList(var4), var7, var1, var0, var5, var6, includeLocation(var3));
   }

   protected static boolean includeLocation(String var0) {
      return Boolean.parseBoolean(var0);
   }

   @Plugin(
      name = "asyncRoot",
      category = "Core",
      printObject = true
   )
   public static class RootLogger extends LoggerConfig {
      @Deprecated
      public static LoggerConfig createLogger(String var0, String var1, String var2, AppenderRef[] var3, Property[] var4, Configuration var5, Filter var6) {
         List var7 = Arrays.asList(var3);
         Object var8 = null;

         try {
            var8 = Level.toLevel(var1, Level.ERROR);
         } catch (Exception var10) {
            LOGGER.error("Invalid Log level specified: {}. Defaulting to Error", var1);
            var8 = Level.ERROR;
         }

         boolean var9 = Booleans.parseBoolean(var0, true);
         return new AsyncLoggerConfig("", var7, var6, (Level)var8, var9, var4, var5, AsyncLoggerConfig.includeLocation(var2));
      }

      @PluginFactory
      public static LoggerConfig createLogger(
         @PluginAttribute("additivity") String var0,
         @PluginAttribute("level") Level var1,
         @PluginAttribute("includeLocation") String var2,
         @PluginElement("AppenderRef") AppenderRef[] var3,
         @PluginElement("Properties") Property[] var4,
         @PluginConfiguration Configuration var5,
         @PluginElement("Filter") Filter var6
      ) {
         List var7 = Arrays.asList(var3);
         Level var8 = var1 == null ? Level.ERROR : var1;
         boolean var9 = Booleans.parseBoolean(var0, true);
         return new AsyncLoggerConfig("", var7, var6, var8, var9, var4, var5, AsyncLoggerConfig.includeLocation(var2));
      }
   }
}
