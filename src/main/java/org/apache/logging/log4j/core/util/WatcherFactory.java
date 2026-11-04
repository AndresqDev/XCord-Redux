package org.apache.logging.log4j.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.status.StatusLogger;

public class WatcherFactory {
   private static Logger LOGGER = StatusLogger.getLogger();
   private static PluginManager pluginManager = new PluginManager("Watcher");
   private static volatile WatcherFactory factory;
   private final Map<String, PluginType<?>> plugins;

   private WatcherFactory(List<String> var1) {
      pluginManager.collectPlugins(var1);
      this.plugins = pluginManager.getPlugins();
   }

   public static WatcherFactory getInstance(List<String> var0) {
      if (factory == null) {
         synchronized (pluginManager) {
            if (factory == null) {
               factory = new WatcherFactory(var0);
            }
         }
      }

      return factory;
   }

   public Watcher newWatcher(Source var1, Configuration var2, Reconfigurable var3, List<ConfigurationListener> var4, long var5) {
      if (var1.getFile() != null) {
         return new ConfigurationFileWatcher(var2, var3, var4, var5);
      } else {
         String var7 = var1.getURI().getScheme();
         PluginType var8 = this.plugins.get(var7);
         if (var8 != null) {
            return instantiate(var7, var8.getPluginClass(), var2, var3, var4, var5);
         } else {
            LOGGER.info("No Watcher plugin is available for protocol '{}'", var7);
            return null;
         }
      }
   }

   public static <T extends Watcher> T instantiate(
      String var0, Class<T> var1, Configuration var2, Reconfigurable var3, List<ConfigurationListener> var4, long var5
   ) {
      Objects.requireNonNull(var1, "No class provided");

      try {
         Constructor var7 = var1.getConstructor(Configuration.class, Reconfigurable.class, List.class, long.class);
         return (T)var7.newInstance(var2, var3, var4, var5);
      } catch (NoSuchMethodException var8) {
         throw new IllegalArgumentException("No valid constructor for Watcher plugin " + var0, var8);
      } catch (InstantiationException | LinkageError var9) {
         throw new IllegalArgumentException(var9);
      } catch (IllegalAccessException var10) {
         throw new IllegalStateException(var10);
      } catch (InvocationTargetException var11) {
         Throwables.rethrow(var11.getCause());
         throw new InternalError("Unreachable");
      }
   }
}
