package org.apache.logging.log4j.core.async;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;

public enum ThreadNameCachingStrategy {
   CACHED {
      @Override
      public String getThreadName() {
         String var1 = ThreadNameCachingStrategy.THREADLOCAL_NAME.get();
         if (var1 == null) {
            var1 = Thread.currentThread().getName();
            ThreadNameCachingStrategy.THREADLOCAL_NAME.set(var1);
         }

         return var1;
      }
   },
   UNCACHED {
      @Override
      public String getThreadName() {
         return Thread.currentThread().getName();
      }
   };

   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final ThreadLocal<String> THREADLOCAL_NAME = new ThreadLocal<>();
   static final ThreadNameCachingStrategy DEFAULT_STRATEGY = isAllocatingThreadGetName() ? CACHED : UNCACHED;

   private ThreadNameCachingStrategy() {
   }

   abstract String getThreadName();

   public static ThreadNameCachingStrategy create() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLogger.ThreadNameStrategy");

      try {
         ThreadNameCachingStrategy var1 = var0 != null ? valueOf(var0) : DEFAULT_STRATEGY;
         LOGGER.debug("AsyncLogger.ThreadNameStrategy={} (user specified {}, default is {})", var1.name(), var0, DEFAULT_STRATEGY.name());
         return var1;
      } catch (Exception var2) {
         LOGGER.debug("Using AsyncLogger.ThreadNameStrategy.{}: '{}' not valid: {}", DEFAULT_STRATEGY.name(), var0, var2.toString());
         return DEFAULT_STRATEGY;
      }
   }

   static boolean isAllocatingThreadGetName() {
      if (Constants.JAVA_MAJOR_VERSION == 8) {
         try {
            Pattern var0 = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+)");
            Matcher var1 = var0.matcher(System.getProperty("java.version"));
            return !var1.matches() ? true : Integer.parseInt(var1.group(3)) == 0 && Integer.parseInt(var1.group(4)) < 102;
         } catch (Exception var2) {
            return true;
         }
      } else {
         return Constants.JAVA_MAJOR_VERSION < 8;
      }
   }
}
