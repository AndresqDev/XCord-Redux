package org.apache.logging.log4j.core.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.time.PreciseClock;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Supplier;

public final class ClockFactory {
   public static final String PROPERTY_NAME = "log4j.Clock";
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   private ClockFactory() {
   }

   public static Clock getClock() {
      return createClock();
   }

   private static Map<String, Supplier<Clock>> aliases() {
      HashMap var0 = new HashMap();
      var0.put("SystemClock", SystemClock::new);
      var0.put("SystemMillisClock", SystemMillisClock::new);
      var0.put("CachedClock", CachedClock::instance);
      var0.put("CoarseCachedClock", CoarseCachedClock::instance);
      var0.put("org.apache.logging.log4j.core.util.CachedClock", CachedClock::instance);
      var0.put("org.apache.logging.log4j.core.util.CoarseCachedClock", CoarseCachedClock::instance);
      return var0;
   }

   private static Clock createClock() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j.Clock");
      if (var0 == null) {
         LOGGER.trace("Using default SystemClock for timestamps.");
         return logSupportedPrecision(new SystemClock());
      } else {
         Supplier var1 = aliases().get(var0);
         if (var1 != null) {
            LOGGER.trace("Using specified {} for timestamps.", var0);
            return logSupportedPrecision((Clock)var1.get());
         } else {
            try {
               Clock var2 = Loader.newCheckedInstanceOf(var0, Clock.class);
               LOGGER.trace("Using {} for timestamps.", var2.getClass().getName());
               return logSupportedPrecision(var2);
            } catch (Exception var4) {
               String var3 = "Could not create {}: {}, using default SystemClock for timestamps.";
               LOGGER.error("Could not create {}: {}, using default SystemClock for timestamps.", var0, var4);
               return logSupportedPrecision(new SystemClock());
            }
         }
      }
   }

   private static Clock logSupportedPrecision(Clock var0) {
      String var1 = var0 instanceof PreciseClock ? "supports" : "does not support";
      LOGGER.debug("{} {} precise timestamps.", var0.getClass().getName(), var1);
      return var0;
   }
}
