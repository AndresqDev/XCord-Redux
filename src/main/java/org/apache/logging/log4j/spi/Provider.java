package org.apache.logging.log4j.spi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class Provider {
   public static final String FACTORY_PRIORITY = "FactoryPriority";
   public static final String THREAD_CONTEXT_MAP = "ThreadContextMap";
   public static final String LOGGER_CONTEXT_FACTORY = "LoggerContextFactory";
   private static final Integer DEFAULT_PRIORITY = -1;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Integer priority;
   private final String className;
   private final Class<? extends LoggerContextFactory> loggerContextFactoryClass;
   private final String threadContextMap;
   private final Class<? extends ThreadContextMap> threadContextMapClass;
   private final String versions;
   private final URL url;
   private final WeakReference<ClassLoader> classLoader;

   public Provider(Properties var1, URL var2, ClassLoader var3) {
      this.url = var2;
      this.classLoader = new WeakReference<>(var3);
      String var4 = var1.getProperty("FactoryPriority");
      this.priority = var4 == null ? DEFAULT_PRIORITY : Integer.valueOf(var4);
      this.className = var1.getProperty("LoggerContextFactory");
      this.threadContextMap = var1.getProperty("ThreadContextMap");
      this.loggerContextFactoryClass = null;
      this.threadContextMapClass = null;
      this.versions = null;
   }

   public Provider(Integer var1, String var2, Class<? extends LoggerContextFactory> var3) {
      this(var1, var2, var3, null);
   }

   public Provider(Integer var1, String var2, Class<? extends LoggerContextFactory> var3, Class<? extends ThreadContextMap> var4) {
      this.url = null;
      this.classLoader = null;
      this.priority = var1;
      this.loggerContextFactoryClass = var3;
      this.threadContextMapClass = var4;
      this.className = null;
      this.threadContextMap = null;
      this.versions = var2;
   }

   public String getVersions() {
      return this.versions;
   }

   public Integer getPriority() {
      return this.priority;
   }

   public String getClassName() {
      return this.loggerContextFactoryClass != null ? this.loggerContextFactoryClass.getName() : this.className;
   }

   public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
      if (this.loggerContextFactoryClass != null) {
         return this.loggerContextFactoryClass;
      } else if (this.className == null) {
         return null;
      } else {
         ClassLoader var1 = this.classLoader.get();
         if (var1 == null) {
            return null;
         } else {
            try {
               Class var2 = var1.loadClass(this.className);
               if (LoggerContextFactory.class.isAssignableFrom(var2)) {
                  return var2.asSubclass(LoggerContextFactory.class);
               }
            } catch (Exception var3) {
               LOGGER.error("Unable to create class {} specified in {}", this.className, this.url.toString(), var3);
            }

            return null;
         }
      }
   }

   public String getThreadContextMap() {
      return this.threadContextMapClass != null ? this.threadContextMapClass.getName() : this.threadContextMap;
   }

   public Class<? extends ThreadContextMap> loadThreadContextMap() {
      if (this.threadContextMapClass != null) {
         return this.threadContextMapClass;
      } else if (this.threadContextMap == null) {
         return null;
      } else {
         ClassLoader var1 = this.classLoader.get();
         if (var1 == null) {
            return null;
         } else {
            try {
               Class var2 = var1.loadClass(this.threadContextMap);
               if (ThreadContextMap.class.isAssignableFrom(var2)) {
                  return var2.asSubclass(ThreadContextMap.class);
               }
            } catch (Exception var3) {
               LOGGER.error("Unable to create class {} specified in {}", this.threadContextMap, this.url.toString(), var3);
            }

            return null;
         }
      }
   }

   public URL getUrl() {
      return this.url;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Provider[");
      if (!DEFAULT_PRIORITY.equals(this.priority)) {
         var1.append("priority=").append(this.priority).append(", ");
      }

      if (this.threadContextMap != null) {
         var1.append("threadContextMap=").append(this.threadContextMap).append(", ");
      } else if (this.threadContextMapClass != null) {
         var1.append("threadContextMapClass=").append(this.threadContextMapClass.getName());
      }

      if (this.className != null) {
         var1.append("className=").append(this.className).append(", ");
      } else if (this.loggerContextFactoryClass != null) {
         var1.append("class=").append(this.loggerContextFactoryClass.getName());
      }

      if (this.url != null) {
         var1.append("url=").append(this.url);
      }

      ClassLoader var2;
      if (this.classLoader != null && (var2 = this.classLoader.get()) != null) {
         var1.append(", classLoader=").append(var2);
      } else {
         var1.append(", classLoader=null(not reachable)");
      }

      var1.append("]");
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Provider var2 = (Provider)var1;
         if (this.priority != null ? this.priority.equals(var2.priority) : var2.priority == null) {
            if (this.className != null ? this.className.equals(var2.className) : var2.className == null) {
               if (this.loggerContextFactoryClass != null
                  ? this.loggerContextFactoryClass.equals(var2.loggerContextFactoryClass)
                  : var2.loggerContextFactoryClass == null) {
                  return this.versions != null ? this.versions.equals(var2.versions) : var2.versions == null;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.priority != null ? this.priority.hashCode() : 0;
      var1 = 31 * var1 + (this.className != null ? this.className.hashCode() : 0);
      var1 = 31 * var1 + (this.loggerContextFactoryClass != null ? this.loggerContextFactoryClass.hashCode() : 0);
      return 31 * var1 + (this.versions != null ? this.versions.hashCode() : 0);
   }
}
