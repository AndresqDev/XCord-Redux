package org.apache.logging.log4j.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;

public final class ProviderUtil {
   protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
   protected static final Collection<Provider> PROVIDERS = new HashSet<>();
   protected static final Lock STARTUP_LOCK = new ReentrantLock();
   private static final String API_VERSION = "Log4jAPIVersion";
   private static final String[] COMPATIBLE_API_VERSIONS = new String[]{"2.6.0"};
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile ProviderUtil instance;

   private ProviderUtil() {
      for (ClassLoader var4 : LoaderUtil.getClassLoaders()) {
         try {
            loadProviders(var4);
         } catch (Throwable var6) {
            LOGGER.debug("Unable to retrieve provider from ClassLoader {}", var4, var6);
         }
      }

      for (LoaderUtil.UrlResource var8 : LoaderUtil.findUrlResources("META-INF/log4j-provider.properties")) {
         loadProvider(var8.getUrl(), var8.getClassLoader());
      }
   }

   protected static void addProvider(Provider var0) {
      PROVIDERS.add(var0);
      LOGGER.debug("Loaded Provider {}", var0);
   }

   protected static void loadProvider(URL var0, ClassLoader var1) {
      try {
         Properties var2 = PropertiesUtil.loadClose(var0.openStream(), var0);
         if (validVersion(var2.getProperty("Log4jAPIVersion"))) {
            Provider var3 = new Provider(var2, var0, var1);
            PROVIDERS.add(var3);
            LOGGER.debug("Loaded Provider {}", var3);
         }
      } catch (IOException var4) {
         LOGGER.error("Unable to open {}", var0, var4);
      }
   }

   protected static void loadProviders(ClassLoader var0) {
      for (Provider var3 : ServiceLoader.load(Provider.class, var0)) {
         if (validVersion(var3.getVersions()) && !PROVIDERS.contains(var3)) {
            PROVIDERS.add(var3);
         }
      }
   }

   @Deprecated
   protected static void loadProviders(Enumeration<URL> var0, ClassLoader var1) {
      if (var0 != null) {
         while (var0.hasMoreElements()) {
            loadProvider((URL)var0.nextElement(), var1);
         }
      }
   }

   public static Iterable<Provider> getProviders() {
      lazyInit();
      return PROVIDERS;
   }

   public static boolean hasProviders() {
      lazyInit();
      return !PROVIDERS.isEmpty();
   }

   protected static void lazyInit() {
      if (instance == null) {
         try {
            STARTUP_LOCK.lockInterruptibly();

            try {
               if (instance == null) {
                  instance = new ProviderUtil();
               }
            } finally {
               STARTUP_LOCK.unlock();
            }
         } catch (InterruptedException var4) {
            LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", (Throwable)var4);
            Thread.currentThread().interrupt();
         }
      }
   }

   public static ClassLoader findClassLoader() {
      return LoaderUtil.getThreadContextClassLoader();
   }

   private static boolean validVersion(String var0) {
      for (String var4 : COMPATIBLE_API_VERSIONS) {
         if (var0.startsWith(var4)) {
            return true;
         }
      }

      return false;
   }
}
