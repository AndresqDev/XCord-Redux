package org.apache.logging.log4j.core.util;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class Loader {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";

   private Loader() {
   }

   public static ClassLoader getClassLoader() {
      return getClassLoader(Loader.class, null);
   }

   public static ClassLoader getThreadContextClassLoader() {
      return LoaderUtil.getThreadContextClassLoader();
   }

   public static ClassLoader getClassLoader(Class<?> var0, Class<?> var1) {
      ClassLoader var2 = getThreadContextClassLoader();
      ClassLoader var3 = var0 == null ? null : var0.getClassLoader();
      ClassLoader var4 = var1 == null ? null : var1.getClassLoader();
      if (isChild(var2, var3)) {
         return isChild(var2, var4) ? var2 : var4;
      } else {
         return isChild(var3, var4) ? var3 : var4;
      }
   }

   public static URL getResource(String var0, ClassLoader var1) {
      try {
         ClassLoader var2 = getThreadContextClassLoader();
         if (var2 != null) {
            LOGGER.trace("Trying to find [{}] using context class loader {}.", var0, var2);
            URL var3 = var2.getResource(var0);
            if (var3 != null) {
               return var3;
            }
         }

         var2 = Loader.class.getClassLoader();
         if (var2 != null) {
            LOGGER.trace("Trying to find [{}] using {} class loader.", var0, var2);
            URL var6 = var2.getResource(var0);
            if (var6 != null) {
               return var6;
            }
         }

         if (var1 != null) {
            LOGGER.trace("Trying to find [{}] using {} class loader.", var0, var1);
            URL var7 = var1.getResource(var0);
            if (var7 != null) {
               return var7;
            }
         }
      } catch (Throwable var4) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var4);
      }

      LOGGER.trace("Trying to find [{}] using ClassLoader.getSystemResource().", var0);
      return ClassLoader.getSystemResource(var0);
   }

   public static InputStream getResourceAsStream(String var0, ClassLoader var1) {
      try {
         ClassLoader var2 = getThreadContextClassLoader();
         if (var2 != null) {
            LOGGER.trace("Trying to find [{}] using context class loader {}.", var0, var2);
            InputStream var3 = var2.getResourceAsStream(var0);
            if (var3 != null) {
               return var3;
            }
         }

         var2 = Loader.class.getClassLoader();
         if (var2 != null) {
            LOGGER.trace("Trying to find [{}] using {} class loader.", var0, var2);
            InputStream var6 = var2.getResourceAsStream(var0);
            if (var6 != null) {
               return var6;
            }
         }

         if (var1 != null) {
            LOGGER.trace("Trying to find [{}] using {} class loader.", var0, var1);
            InputStream var7 = var1.getResourceAsStream(var0);
            if (var7 != null) {
               return var7;
            }
         }
      } catch (Throwable var4) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var4);
      }

      LOGGER.trace("Trying to find [{}] using ClassLoader.getSystemResource().", var0);
      return ClassLoader.getSystemResourceAsStream(var0);
   }

   private static boolean isChild(ClassLoader var0, ClassLoader var1) {
      if (var0 != null && var1 != null) {
         ClassLoader var2 = var0.getParent();

         while (var2 != null && var2 != var1) {
            var2 = var2.getParent();
         }

         return var2 != null;
      } else {
         return var0 != null;
      }
   }

   public static Class<?> initializeClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      return Class.forName(var0, true, var1);
   }

   public static Class<?> loadClass(String var0, ClassLoader var1) throws ClassNotFoundException {
      return var1 != null ? var1.loadClass(var0) : null;
   }

   public static Class<?> loadSystemClass(String var0) throws ClassNotFoundException {
      try {
         return Class.forName(var0, true, ClassLoader.getSystemClassLoader());
      } catch (Throwable var2) {
         LOGGER.trace("Couldn't use SystemClassLoader. Trying Class.forName({}).", var0, var2);
         return Class.forName(var0);
      }
   }

   public static <T> T newInstanceOf(String var0) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();

      Object var2;
      try {
         Thread.currentThread().setContextClassLoader(getClassLoader());
         var2 = LoaderUtil.newInstanceOf(var0);
      } finally {
         Thread.currentThread().setContextClassLoader(var1);
      }

      return (T)var2;
   }

   public static <T> T newCheckedInstanceOf(String var0, Class<T> var1) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      ClassLoader var2 = Thread.currentThread().getContextClassLoader();

      Object var3;
      try {
         Thread.currentThread().setContextClassLoader(getClassLoader());
         var3 = LoaderUtil.newCheckedInstanceOf(var0, var1);
      } finally {
         Thread.currentThread().setContextClassLoader(var2);
      }

      return (T)var3;
   }

   public static <T> T newCheckedInstanceOfProperty(String var0, Class<T> var1) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      String var2 = PropertiesUtil.getProperties().getStringProperty(var0);
      ClassLoader var3 = Thread.currentThread().getContextClassLoader();

      Object var4;
      try {
         Thread.currentThread().setContextClassLoader(getClassLoader());
         var4 = LoaderUtil.newCheckedInstanceOfProperty(var0, var1);
      } finally {
         Thread.currentThread().setContextClassLoader(var3);
      }

      return (T)var4;
   }

   public static boolean isClassAvailable(String var0) {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();

      boolean var2;
      try {
         Thread.currentThread().setContextClassLoader(getClassLoader());
         var2 = LoaderUtil.isClassAvailable(var0);
      } finally {
         Thread.currentThread().setContextClassLoader(var1);
      }

      return var2;
   }

   public static boolean isJansiAvailable() {
      return isClassAvailable("org.fusesource.jansi.AnsiRenderer");
   }

   public static Class<?> loadClass(String var0) throws ClassNotFoundException {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();

      Class var2;
      try {
         Thread.currentThread().setContextClassLoader(getClassLoader());
         var2 = LoaderUtil.loadClass(var0);
      } finally {
         Thread.currentThread().setContextClassLoader(var1);
      }

      return var2;
   }
}
