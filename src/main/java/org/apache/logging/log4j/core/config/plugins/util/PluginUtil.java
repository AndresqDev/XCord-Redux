package org.apache.logging.log4j.core.config.plugins.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

public final class PluginUtil {
   private PluginUtil() {
   }

   public static Map<String, PluginType<?>> collectPluginsByCategory(String var0) {
      Objects.requireNonNull(var0, "category");
      return collectPluginsByCategoryAndPackage(var0, Collections.emptyList());
   }

   public static Map<String, PluginType<?>> collectPluginsByCategoryAndPackage(String var0, List<String> var1) {
      Objects.requireNonNull(var0, "category");
      Objects.requireNonNull(var1, "packages");
      PluginManager var2 = new PluginManager(var0);
      var2.collectPlugins(var1);
      return var2.getPlugins();
   }

   public static <V> V instantiatePlugin(Class<V> var0) {
      Objects.requireNonNull(var0, "pluginClass");
      Method var1 = findPluginFactoryMethod(var0);

      try {
         return (V)var1.invoke(null);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         String var3 = String.format("failed to instantiate plugin of type %s using the factory method %s", var0, var1);
         throw new IllegalStateException(var3, var4);
      }
   }

   public static Method findPluginFactoryMethod(Class<?> var0) {
      Objects.requireNonNull(var0, "pluginClass");

      for (Method var4 : var0.getDeclaredMethods()) {
         boolean var5 = var4.isAnnotationPresent(PluginFactory.class);
         if (var5) {
            boolean var6 = Modifier.isStatic(var4.getModifiers());
            if (var6) {
               return var4;
            }
         }
      }

      throw new IllegalStateException("no factory method found for class " + var0);
   }
}
