package org.apache.logging.log4j.core.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public final class Configurator {
   private static final String FQCN = Configurator.class.getName();
   private static final Logger LOGGER = StatusLogger.getLogger();

   private static Log4jContextFactory getFactory() {
      LoggerContextFactory var0 = LogManager.getFactory();
      if (var0 instanceof Log4jContextFactory) {
         return (Log4jContextFactory)var0;
      } else {
         if (var0 != null) {
            LOGGER.error(
               "LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j.",
               var0.getClass().getName(),
               Log4jContextFactory.class.getName()
            );
         } else {
            LOGGER.fatal("LogManager did not return a LoggerContextFactory. This indicates something has gone terribly wrong!");
         }

         return null;
      }
   }

   public static LoggerContext initialize(ClassLoader var0, ConfigurationSource var1) {
      return initialize(var0, var1, null);
   }

   public static LoggerContext initialize(ClassLoader var0, ConfigurationSource var1, Object var2) {
      try {
         Log4jContextFactory var3 = getFactory();
         return var3 == null ? null : var3.getContext(FQCN, var0, var2, false, var1);
      } catch (Exception var4) {
         LOGGER.error("There was a problem obtaining a LoggerContext using the configuration source [{}]", var1, var4);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, String var2) {
      return initialize(var0, var1, var2, null);
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, String var2, Object var3) {
      if (Strings.isBlank(var2)) {
         return initialize(var0, var1, (URI)null, var3);
      } else if (var2.contains(",")) {
         String[] var4 = var2.split(",");
         String var5 = null;
         ArrayList var6 = new ArrayList(var4.length);

         for (String var10 : var4) {
            URI var11 = NetUtils.toURI(var5 != null ? var5 + ":" + var10.trim() : var10.trim());
            if (var5 == null && var11.getScheme() != null) {
               var5 = var11.getScheme();
            }

            var6.add(var11);
         }

         return initialize(var0, var1, var6, var3);
      } else {
         return initialize(var0, var1, NetUtils.toURI(var2), var3);
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, URI var2) {
      return initialize(var0, var1, var2, null);
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, URI var2, Object var3) {
      try {
         Log4jContextFactory var4 = getFactory();
         return var4 == null ? null : var4.getContext(FQCN, var1, var3, false, var2, var0);
      } catch (Exception var5) {
         LOGGER.error("There was a problem initializing the LoggerContext [{}] using configuration at [{}].", var0, var2, var5);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, URI var2, Entry<String, Object> var3) {
      try {
         Log4jContextFactory var4 = getFactory();
         return var4 == null ? null : var4.getContext(FQCN, var1, var3, false, var2, var0);
      } catch (Exception var5) {
         LOGGER.error("There was a problem initializing the LoggerContext [{}] using configuration at [{}].", var0, var2, var5);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, ClassLoader var1, List<URI> var2, Object var3) {
      try {
         Log4jContextFactory var4 = getFactory();
         return var4 == null ? null : var4.getContext(FQCN, var1, var3, false, var2, var0);
      } catch (Exception var5) {
         LOGGER.error("There was a problem initializing the LoggerContext [{}] using configurations at [{}].", var0, var2, var5);
         return null;
      }
   }

   public static LoggerContext initialize(String var0, String var1) {
      return initialize(var0, null, var1);
   }

   public static LoggerContext initialize(Configuration var0) {
      return initialize(null, var0, null);
   }

   public static LoggerContext initialize(ClassLoader var0, Configuration var1) {
      return initialize(var0, var1, null);
   }

   public static LoggerContext initialize(ClassLoader var0, Configuration var1, Object var2) {
      try {
         Log4jContextFactory var3 = getFactory();
         return var3 == null ? null : var3.getContext(FQCN, var0, var2, false, var1);
      } catch (Exception var4) {
         LOGGER.error("There was a problem initializing the LoggerContext using configuration {}", var1.getName(), var4);
         return null;
      }
   }

   public static void reconfigure(Configuration var0) {
      try {
         Log4jContextFactory var1 = getFactory();
         if (var1 != null) {
            var1.getContext(FQCN, null, null, false).reconfigure(var0);
         }
      } catch (Exception var2) {
         LOGGER.error("There was a problem initializing the LoggerContext using configuration {}", var0.getName(), var2);
      }
   }

   public static void reconfigure() {
      try {
         Log4jContextFactory var0 = getFactory();
         if (var0 != null) {
            var0.getSelector().getContext(FQCN, null, false).reconfigure();
         } else {
            LOGGER.warn("Unable to reconfigure - Log4j has not been initialized.");
         }
      } catch (Exception var1) {
         LOGGER.error("Error encountered trying to reconfigure logging", (Throwable)var1);
      }
   }

   public static void reconfigure(URI var0) {
      try {
         Log4jContextFactory var1 = getFactory();
         if (var1 != null) {
            var1.getSelector().getContext(FQCN, null, false).setConfigLocation(var0);
         } else {
            LOGGER.warn("Unable to reconfigure - Log4j has not been initialized.");
         }
      } catch (Exception var2) {
         LOGGER.error("Error encountered trying to reconfigure logging", (Throwable)var2);
      }
   }

   public static void setAllLevels(String var0, Level var1) {
      LoggerContext var2 = LoggerContext.getContext(false);
      Configuration var3 = var2.getConfiguration();
      boolean var4 = setLevel(var0, var1, var3);

      for (Entry var6 : var3.getLoggers().entrySet()) {
         if (((String)var6.getKey()).startsWith(var0)) {
            var4 |= setLevel((LoggerConfig)var6.getValue(), var1);
         }
      }

      if (var4) {
         var2.updateLoggers();
      }
   }

   private static boolean setLevel(LoggerConfig var0, Level var1) {
      boolean var2 = !var0.getLevel().equals(var1);
      if (var2) {
         var0.setLevel(var1);
      }

      return var2;
   }

   public static void setLevel(Map<String, Level> var0) {
      LoggerContext var1 = LoggerContext.getContext(false);
      Configuration var2 = var1.getConfiguration();
      boolean var3 = false;

      for (Entry var5 : var0.entrySet()) {
         String var6 = (String)var5.getKey();
         Level var7 = (Level)var5.getValue();
         var3 |= setLevel(var6, var7, var2);
      }

      if (var3) {
         var1.updateLoggers();
      }
   }

   public static void setLevel(String var0, Level var1) {
      LoggerContext var2 = LoggerContext.getContext(false);
      if (Strings.isEmpty(var0)) {
         setRootLevel(var1);
      } else if (setLevel(var0, var1, var2.getConfiguration())) {
         var2.updateLoggers();
      }
   }

   private static boolean setLevel(String var0, Level var1, Configuration var2) {
      LoggerConfig var4 = var2.getLoggerConfig(var0);
      boolean var3;
      if (!var0.equals(var4.getName())) {
         var4 = new LoggerConfig(var0, var1, true);
         var2.addLogger(var0, var4);
         var4.setLevel(var1);
         var3 = true;
      } else {
         var3 = setLevel(var4, var1);
      }

      return var3;
   }

   public static void setRootLevel(Level var0) {
      LoggerContext var1 = LoggerContext.getContext(false);
      LoggerConfig var2 = var1.getConfiguration().getRootLogger();
      if (!var2.getLevel().equals(var0)) {
         var2.setLevel(var0);
         var1.updateLoggers();
      }
   }

   public static void shutdown(LoggerContext var0) {
      if (var0 != null) {
         var0.stop();
      }
   }

   public static boolean shutdown(LoggerContext var0, long var1, TimeUnit var3) {
      return var0 != null ? var0.stop(var1, var3) : true;
   }

   private Configurator() {
   }
}
