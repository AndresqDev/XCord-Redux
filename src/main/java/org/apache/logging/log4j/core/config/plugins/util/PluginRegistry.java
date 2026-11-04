package org.apache.logging.log4j.core.config.plugins.util;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.processor.PluginCache;
import org.apache.logging.log4j.core.config.plugins.processor.PluginEntry;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class PluginRegistry {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile PluginRegistry INSTANCE;
   private static final Object INSTANCE_LOCK = new Object();
   private final AtomicReference<Map<String, List<PluginType<?>>>> pluginsByCategoryRef = new AtomicReference<>();
   private final ConcurrentMap<Long, Map<String, List<PluginType<?>>>> pluginsByCategoryByBundleId = new ConcurrentHashMap<>();
   private final ConcurrentMap<String, Map<String, List<PluginType<?>>>> pluginsByCategoryByPackage = new ConcurrentHashMap<>();

   private PluginRegistry() {
   }

   public static PluginRegistry getInstance() {
      PluginRegistry var0 = INSTANCE;
      if (var0 == null) {
         synchronized (INSTANCE_LOCK) {
            var0 = INSTANCE;
            if (var0 == null) {
               INSTANCE = var0 = new PluginRegistry();
            }
         }
      }

      return var0;
   }

   public void clear() {
      this.pluginsByCategoryRef.set(null);
      this.pluginsByCategoryByPackage.clear();
      this.pluginsByCategoryByBundleId.clear();
   }

   public Map<Long, Map<String, List<PluginType<?>>>> getPluginsByCategoryByBundleId() {
      return this.pluginsByCategoryByBundleId;
   }

   public Map<String, List<PluginType<?>>> loadFromMainClassLoader() {
      Map var1 = this.pluginsByCategoryRef.get();
      if (var1 != null) {
         return var1;
      } else {
         Map var2 = this.decodeCacheFiles(Loader.getClassLoader());
         return this.pluginsByCategoryRef.compareAndSet(null, var2) ? var2 : this.pluginsByCategoryRef.get();
      }
   }

   public void clearBundlePlugins(long var1) {
      this.pluginsByCategoryByBundleId.remove(Long.valueOf(var1));
   }

   public Map<String, List<PluginType<?>>> loadFromBundle(long var1, ClassLoader var3) {
      Map var4 = this.pluginsByCategoryByBundleId.get(Long.valueOf(var1));
      if (var4 != null) {
         return var4;
      } else {
         Map var5 = this.decodeCacheFiles(var3);
         var4 = this.pluginsByCategoryByBundleId.putIfAbsent(var1, var5);
         return var4 != null ? var4 : var5;
      }
   }

   private Map<String, List<PluginType<?>>> decodeCacheFiles(ClassLoader var1) {
      long var2 = System.nanoTime();
      PluginCache var4 = new PluginCache();

      try {
         Enumeration var5 = var1.getResources("META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat");
         if (var5 == null) {
            LOGGER.info("Plugin preloads not available from class loader {}", var1);
         } else {
            var4.loadCacheFiles(var5);
         }
      } catch (IOException var19) {
         LOGGER.warn("Unable to preload plugins", (Throwable)var19);
      }

      HashMap var20 = new HashMap();
      int var6 = 0;

      for (Entry var8 : var4.getAllCategories().entrySet()) {
         String var9 = (String)var8.getKey();
         ArrayList var10 = new ArrayList(((Map)var8.getValue()).size());
         var20.put(var9, var10);

         for (Entry var12 : ((Map)var8.getValue()).entrySet()) {
            PluginEntry var13 = (PluginEntry)var12.getValue();
            String var14 = var13.getClassName();

            try {
               Class var15 = var1.loadClass(var14);
               PluginType var16 = new PluginType(var13, var15, var13.getName());
               var10.add(var16);
               var6++;
            } catch (ClassNotFoundException var17) {
               LOGGER.info("Plugin [{}] could not be loaded due to missing classes.", var14, var17);
            } catch (LinkageError var18) {
               LOGGER.info("Plugin [{}] could not be loaded due to linkage error.", var14, var18);
            }
         }
      }

      int var21 = var6;
      LOGGER.debug(() -> {
         long var4x = System.nanoTime();
         StringBuilder var6x = new StringBuilder("Took ");
         DecimalFormat var7 = new DecimalFormat("#0.000000");
         var6x.append(var7.format((double)(var4x - var2) * 1.0E-9));
         var6x.append(" seconds to load ").append(var21);
         var6x.append(" plugins from ").append(var1);
         return var6x.toString();
      });
      return var20;
   }

   public Map<String, List<PluginType<?>>> loadFromPackage(String var1) {
      if (Strings.isBlank(var1)) {
         return Collections.emptyMap();
      } else {
         Map var2 = this.pluginsByCategoryByPackage.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            long var3 = System.nanoTime();
            ResolverUtil var5 = new ResolverUtil();
            ClassLoader var6 = Loader.getClassLoader();
            if (var6 != null) {
               var5.setClassLoader(var6);
            }

            var5.findInPackage(new PluginRegistry.PluginTest(), var1);
            HashMap var7 = new HashMap();

            for (Class var9 : var5.getClasses()) {
               Plugin var10 = var9.getAnnotation(Plugin.class);
               String var11 = var10.category().toLowerCase();
               Object var12 = (List)var7.get(var11);
               if (var12 == null) {
                  var7.put(var11, var12 = new ArrayList());
               }

               PluginEntry var13 = new PluginEntry();
               String var14 = var10.elementType().equals("") ? var10.name() : var10.elementType();
               var13.setKey(var10.name().toLowerCase());
               var13.setName(var10.name());
               var13.setCategory(var10.category());
               var13.setClassName(var9.getName());
               var13.setPrintable(var10.printObject());
               var13.setDefer(var10.deferChildren());
               PluginType var15 = new PluginType(var13, var9, var14);
               var12.add(var15);
               PluginAliases var16 = var9.getAnnotation(PluginAliases.class);
               if (var16 != null) {
                  for (String var20 : var16.value()) {
                     PluginEntry var21 = new PluginEntry();
                     String var22 = var10.elementType().equals("") ? var20.trim() : var10.elementType();
                     var21.setKey(var20.trim().toLowerCase());
                     var21.setName(var10.name());
                     var21.setCategory(var10.category());
                     var21.setClassName(var9.getName());
                     var21.setPrintable(var10.printObject());
                     var21.setDefer(var10.deferChildren());
                     PluginType var23 = new PluginType(var21, var9, var22);
                     var12.add(var23);
                  }
               }
            }

            LOGGER.debug(() -> {
               long var4 = System.nanoTime();
               StringBuilder var6x = new StringBuilder("Took ");
               DecimalFormat var7x = new DecimalFormat("#0.000000");
               var6x.append(var7x.format((double)(var4 - var3) * 1.0E-9));
               var6x.append(" seconds to load ").append(var5.getClasses().size());
               var6x.append(" plugins from package ").append(var1);
               return var6x.toString();
            });
            var2 = this.pluginsByCategoryByPackage.putIfAbsent(var1, var7);
            return (Map<String, List<PluginType<?>>>)(var2 != null ? var2 : var7);
         }
      }
   }

   public static class PluginTest implements ResolverUtil.Test {
      @Override
      public boolean matches(Class<?> var1) {
         return var1 != null && var1.isAnnotationPresent(Plugin.class);
      }

      @Override
      public String toString() {
         return "annotated with @" + Plugin.class.getSimpleName();
      }

      @Override
      public boolean matches(URI var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean doesMatchClass() {
         return true;
      }

      @Override
      public boolean doesMatchResource() {
         return false;
      }
   }
}
