package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Reporting implements Serializable, Cloneable, InputLocationTracker {
   private String excludeDefaults;
   private String outputDirectory;
   private List<ReportPlugin> plugins;
   private Map<Object, InputLocation> locations;
   private InputLocation location;
   private InputLocation excludeDefaultsLocation;
   private InputLocation outputDirectoryLocation;
   private InputLocation pluginsLocation;
   Map<String, ReportPlugin> reportPluginMap;

   public void addPlugin(ReportPlugin var1) {
      this.getPlugins().add(var1);
   }

   public Reporting clone() {
      try {
         Reporting var1 = (Reporting)super.clone();
         if (this.plugins != null) {
            var1.plugins = new ArrayList<>();

            for (ReportPlugin var3 : this.plugins) {
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

   public String getExcludeDefaults() {
      return this.excludeDefaults;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         switch (var2) {
            case "":
               return this.location;
            case "excludeDefaults":
               return this.excludeDefaultsLocation;
            case "outputDirectory":
               return this.outputDirectoryLocation;
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
            case "excludeDefaults":
               this.excludeDefaultsLocation = var2;
               return;
            case "outputDirectory":
               this.outputDirectoryLocation = var2;
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

   public String getOutputDirectory() {
      return this.outputDirectory;
   }

   public List<ReportPlugin> getPlugins() {
      if (this.plugins == null) {
         this.plugins = new ArrayList<>();
      }

      return this.plugins;
   }

   public void removePlugin(ReportPlugin var1) {
      this.getPlugins().remove(var1);
   }

   public void setExcludeDefaults(String var1) {
      this.excludeDefaults = var1;
   }

   public void setOutputDirectory(String var1) {
      this.outputDirectory = var1;
   }

   public void setPlugins(List<ReportPlugin> var1) {
      this.plugins = var1;
   }

   public boolean isExcludeDefaults() {
      return this.excludeDefaults != null ? Boolean.parseBoolean(this.excludeDefaults) : false;
   }

   public void setExcludeDefaults(boolean var1) {
      this.excludeDefaults = String.valueOf(var1);
   }

   public synchronized void flushReportPluginMap() {
      this.reportPluginMap = null;
   }

   public synchronized Map<String, ReportPlugin> getReportPluginsAsMap() {
      if (this.reportPluginMap == null) {
         this.reportPluginMap = new LinkedHashMap<>();
         if (this.getPlugins() != null) {
            for (ReportPlugin var2 : this.getPlugins()) {
               this.reportPluginMap.put(var2.getKey(), var2);
            }
         }
      }

      return this.reportPluginMap;
   }
}
