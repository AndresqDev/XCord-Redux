package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;

public final class ThreadContextMapFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String THREAD_CONTEXT_KEY = "log4j2.threadContextMap";
   private static final String GC_FREE_THREAD_CONTEXT_KEY = "log4j2.garbagefree.threadContextMap";
   private static boolean GcFreeThreadContextKey;
   private static String ThreadContextMapName;

   public static void init() {
      CopyOnWriteSortedArrayThreadContextMap.init();
      GarbageFreeSortedArrayThreadContextMap.init();
      DefaultThreadContextMap.init();
      initPrivate();
   }

   private static void initPrivate() {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      ThreadContextMapName = var0.getStringProperty("log4j2.threadContextMap");
      GcFreeThreadContextKey = var0.getBooleanProperty("log4j2.garbagefree.threadContextMap");
   }

   private ThreadContextMapFactory() {
   }

   public static ThreadContextMap createThreadContextMap() {
      ClassLoader var0 = ProviderUtil.findClassLoader();
      ThreadContextMap var1 = null;
      if (ThreadContextMapName != null) {
         try {
            Class var2 = var0.loadClass(ThreadContextMapName);
            if (ThreadContextMap.class.isAssignableFrom(var2)) {
               var1 = (ThreadContextMap)var2.newInstance();
            }
         } catch (ClassNotFoundException var7) {
            LOGGER.error("Unable to locate configured ThreadContextMap {}", ThreadContextMapName);
         } catch (Exception var8) {
            LOGGER.error("Unable to create configured ThreadContextMap {}", ThreadContextMapName, var8);
         }
      }

      if (var1 == null && ProviderUtil.hasProviders() && LogManager.getFactory() != null) {
         String var10 = LogManager.getFactory().getClass().getName();

         for (Provider var4 : ProviderUtil.getProviders()) {
            if (var10.equals(var4.getClassName())) {
               Class var5 = var4.loadThreadContextMap();
               if (var5 != null) {
                  try {
                     var1 = (ThreadContextMap)var5.newInstance();
                     break;
                  } catch (Exception var9) {
                     LOGGER.error("Unable to locate or load configured ThreadContextMap {}", var4.getThreadContextMap(), var9);
                     var1 = createDefaultThreadContextMap();
                  }
               }
            }
         }
      }

      if (var1 == null) {
         var1 = createDefaultThreadContextMap();
      }

      return var1;
   }

   private static ThreadContextMap createDefaultThreadContextMap() {
      if (Constants.ENABLE_THREADLOCALS) {
         return (ThreadContextMap)(GcFreeThreadContextKey ? new GarbageFreeSortedArrayThreadContextMap() : new CopyOnWriteSortedArrayThreadContextMap());
      } else {
         return new DefaultThreadContextMap(true);
      }
   }

   static {
      initPrivate();
   }
}
