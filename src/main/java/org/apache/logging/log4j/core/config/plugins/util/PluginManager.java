package org.apache.logging.log4j.core.config.plugins.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class PluginManager {
   private static final CopyOnWriteArrayList<String> PACKAGES = new CopyOnWriteArrayList<>();
   private static final String LOG4J_PACKAGES = "org.apache.logging.log4j.core";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private Map<String, PluginType<?>> plugins = new HashMap<>();
   private final String category;

   public PluginManager(String var1) {
      this.category = var1;
   }

   @Deprecated
   public static void main(String[] var0) {
      System.err.println("ERROR: this tool is superseded by the annotation processor included in log4j-core.");
      System.err.println("If the annotation processor does not work for you, please see the manual page:");
      System.err.println("http://logging.apache.org/log4j/2.x/manual/configuration.html#ConfigurationSyntax");
      System.exit(-1);
   }

   public static void addPackage(String var0) {
      if (!Strings.isBlank(var0)) {
         PACKAGES.addIfAbsent(var0);
      }
   }

   public static void addPackages(Collection<String> var0) {
      for (String var2 : var0) {
         if (Strings.isNotBlank(var2)) {
            PACKAGES.addIfAbsent(var2);
         }
      }
   }

   public PluginType<?> getPluginType(String var1) {
      return this.plugins.get(var1.toLowerCase());
   }

   public Map<String, PluginType<?>> getPlugins() {
      return this.plugins;
   }

   public void collectPlugins() {
      this.collectPlugins(null);
   }

   public void collectPlugins(List<String> var1) {
      String var2 = this.category.toLowerCase();
      LinkedHashMap var3 = new LinkedHashMap();
      Map var4 = PluginRegistry.getInstance().loadFromMainClassLoader();
      if (var4.isEmpty()) {
         var4 = PluginRegistry.getInstance().loadFromPackage("org.apache.logging.log4j.core");
      }

      mergeByName(var3, (List<PluginType<?>>)var4.get(var2));

      for (Map var6 : PluginRegistry.getInstance().getPluginsByCategoryByBundleId().values()) {
         mergeByName(var3, (List<PluginType<?>>)var6.get(var2));
      }

      for (String var9 : PACKAGES) {
         mergeByName(var3, PluginRegistry.getInstance().loadFromPackage(var9).get(var2));
      }

      if (var1 != null) {
         for (String var10 : var1) {
            mergeByName(var3, PluginRegistry.getInstance().loadFromPackage(var10).get(var2));
         }
      }

      LOGGER.debug("PluginManager '{}' found {} plugins", this.category, var3.size());
      this.plugins = var3;
   }

   private static void mergeByName(Map<String, PluginType<?>> var0, List<PluginType<?>> var1) {
      if (var1 != null) {
         for (PluginType var3 : var1) {
            String var4 = var3.getKey();
            PluginType var5 = (PluginType)var0.get(var4);
            if (var5 == null) {
               var0.put(var4, var3);
            } else if (!var5.getPluginClass().equals(var3.getPluginClass())) {
               LOGGER.warn("Plugin [{}] is already mapped to {}, ignoring {}", var4, var5.getPluginClass(), var3.getPluginClass());
            }
         }
      }
   }
}
