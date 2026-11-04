package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PluginContainer implements Serializable, Cloneable, InputLocationTracker {
   private List<Plugin> plugins;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation pluginsLocation;
   Map<String, Plugin> pluginMap;

   public void addPlugin(Plugin var1) {
      this.getPlugins().add(var1);
   }

   public PluginContainer clone() {
      try {
         PluginContainer var1 = (PluginContainer)super.clone();
         if (this.plugins != null) {
            var1.plugins = new ArrayList<>();

            for (Plugin var3 : this.plugins) {
               var1.plugins.add(var3.clone());
            }
         }

         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var4) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var4);
      }
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "plugins":
               return this.pluginsLocation;
            default:
               return this.getOtherLocation(var1);
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         switch (var3) {
            case "":
               this.location = var2;
               return;
            case "plugins":
               this.pluginsLocation = var2;
               return;
            default:
               this.setOtherLocation(var1, var2);
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public List<Plugin> getPlugins() {
      if (this.plugins == null) {
         this.plugins = new ArrayList<>();
      }

      return this.plugins;
   }

   public void removePlugin(Plugin var1) {
      this.getPlugins().remove(var1);
   }

   public void setPlugins(List<Plugin> var1) {
      this.plugins = var1;
   }

   public synchronized void flushPluginMap() {
      this.pluginMap = null;
   }

   public synchronized Map<String, Plugin> getPluginsAsMap() {
      if (this.pluginMap == null) {
         this.pluginMap = new LinkedHashMap<>();
         if (this.plugins != null) {
            for (Plugin var2 : this.plugins) {
               this.pluginMap.put(var2.getKey(), var2);
            }
         }
      }

      return this.pluginMap;
   }
}
