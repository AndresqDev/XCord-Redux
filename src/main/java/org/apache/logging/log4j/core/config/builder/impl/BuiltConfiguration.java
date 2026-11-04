package org.apache.logging.log4j.core.config.builder.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Patterns;

public class BuiltConfiguration extends AbstractConfiguration {
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final StatusConfiguration statusConfig;
   protected Component rootComponent;
   private Component loggersComponent;
   private Component appendersComponent;
   private Component filtersComponent;
   private Component propertiesComponent;
   private Component customLevelsComponent;
   private Component scriptsComponent;
   private String contentType = "text";

   public BuiltConfiguration(LoggerContext var1, ConfigurationSource var2, Component var3) {
      super(var1, var2);
      this.statusConfig = new StatusConfiguration().withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());

      for (Component var5 : var3.getComponents()) {
         String var6 = var5.getPluginType();
         switch (var6) {
            case "Scripts":
               this.scriptsComponent = var5;
               break;
            case "Loggers":
               this.loggersComponent = var5;
               break;
            case "Appenders":
               this.appendersComponent = var5;
               break;
            case "Filters":
               this.filtersComponent = var5;
               break;
            case "Properties":
               this.propertiesComponent = var5;
               break;
            case "CustomLevels":
               this.customLevelsComponent = var5;
         }
      }

      this.rootComponent = var3;
   }

   @Override
   public void setup() {
      List var1 = this.rootNode.getChildren();
      if (this.propertiesComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.propertiesComponent));
      }

      if (this.scriptsComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.scriptsComponent));
      }

      if (this.customLevelsComponent.getComponents().size() > 0) {
         var1.add(this.convertToNode(this.rootNode, this.customLevelsComponent));
      }

      var1.add(this.convertToNode(this.rootNode, this.loggersComponent));
      var1.add(this.convertToNode(this.rootNode, this.appendersComponent));
      if (this.filtersComponent.getComponents().size() > 0) {
         if (this.filtersComponent.getComponents().size() == 1) {
            var1.add(this.convertToNode(this.rootNode, this.filtersComponent.getComponents().get(0)));
         } else {
            var1.add(this.convertToNode(this.rootNode, this.filtersComponent));
         }
      }

      this.rootComponent = null;
   }

   public String getContentType() {
      return this.contentType;
   }

   public void setContentType(String var1) {
      this.contentType = var1;
   }

   public void createAdvertiser(String var1, ConfigurationSource var2) {
      byte[] var3 = null;

      try {
         if (var2 != null) {
            InputStream var4 = var2.getInputStream();
            if (var4 != null) {
               var3 = toByteArray(var4);
            }
         }
      } catch (IOException var5) {
         LOGGER.warn("Unable to read configuration source " + var2.toString());
      }

      super.createAdvertiser(var1, var2, var3, this.contentType);
   }

   public StatusConfiguration getStatusConfiguration() {
      return this.statusConfig;
   }

   public void setPluginPackages(String var1) {
      this.pluginPackages.addAll(Arrays.asList(var1.split(Patterns.COMMA_SEPARATOR)));
   }

   public void setShutdownHook(String var1) {
      this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var1);
   }

   public void setShutdownTimeoutMillis(long var1) {
      this.shutdownTimeoutMillis = var1;
   }

   public void setMonitorInterval(int var1) {
      if (this instanceof Reconfigurable && var1 > 0) {
         this.initializeWatchers((Reconfigurable)this, this.getConfigurationSource(), var1);
      }
   }

   @Override
   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   protected Node convertToNode(Node var1, Component var2) {
      String var3 = var2.getPluginType();
      PluginType var4 = this.pluginManager.getPluginType(var3);
      Node var5 = new Node(var1, var3, var4);
      var5.getAttributes().putAll(var2.getAttributes());
      var5.setValue(var2.getValue());
      List var6 = var5.getChildren();

      for (Component var8 : var2.getComponents()) {
         var6.add(this.convertToNode(var5, var8));
      }

      return var5;
   }
}
