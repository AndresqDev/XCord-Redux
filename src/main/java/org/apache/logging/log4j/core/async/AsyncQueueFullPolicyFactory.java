package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class AsyncQueueFullPolicyFactory {
   static final String PROPERTY_NAME_ASYNC_EVENT_ROUTER = "log4j2.AsyncQueueFullPolicy";
   static final String PROPERTY_VALUE_DEFAULT_ASYNC_EVENT_ROUTER = "Default";
   static final String PROPERTY_VALUE_DISCARDING_ASYNC_EVENT_ROUTER = "Discard";
   static final String PROPERTY_NAME_DISCARDING_THRESHOLD_LEVEL = "log4j2.DiscardThreshold";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public static AsyncQueueFullPolicy create() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j2.AsyncQueueFullPolicy");
      if (var0 == null || isRouterSelected(var0, DefaultAsyncQueueFullPolicy.class, "Default")) {
         return new DefaultAsyncQueueFullPolicy();
      } else {
         return isRouterSelected(var0, DiscardingAsyncQueueFullPolicy.class, "Discard") ? createDiscardingAsyncQueueFullPolicy() : createCustomRouter(var0);
      }
   }

   private static boolean isRouterSelected(String var0, Class<? extends AsyncQueueFullPolicy> var1, String var2) {
      return var0 != null && (var2.equalsIgnoreCase(var0) || var1.getName().equals(var0) || var1.getSimpleName().equals(var0));
   }

   private static AsyncQueueFullPolicy createCustomRouter(String var0) {
      try {
         Class var1 = Loader.loadClass(var0).asSubclass(AsyncQueueFullPolicy.class);
         LOGGER.debug("Creating custom AsyncQueueFullPolicy '{}'", var0);
         return (AsyncQueueFullPolicy)var1.newInstance();
      } catch (Exception var2) {
         LOGGER.debug("Using DefaultAsyncQueueFullPolicy. Could not create custom AsyncQueueFullPolicy '{}': {}", var0, var2.toString());
         return new DefaultAsyncQueueFullPolicy();
      }
   }

   private static AsyncQueueFullPolicy createDiscardingAsyncQueueFullPolicy() {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      String var1 = var0.getStringProperty("log4j2.DiscardThreshold", Level.INFO.name());
      Level var2 = Level.toLevel(var1, Level.INFO);
      LOGGER.debug("Creating custom DiscardingAsyncQueueFullPolicy(discardThreshold:{})", var2);
      return new DiscardingAsyncQueueFullPolicy(var2);
   }
}
