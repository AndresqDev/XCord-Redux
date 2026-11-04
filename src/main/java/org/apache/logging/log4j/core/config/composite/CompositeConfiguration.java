package org.apache.logging.log4j.core.config.composite;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.core.util.Watcher;
import org.apache.logging.log4j.util.PropertiesUtil;

public class CompositeConfiguration extends AbstractConfiguration implements Reconfigurable {
   public static final String MERGE_STRATEGY_PROPERTY = "log4j.mergeStrategy";
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final List<? extends AbstractConfiguration> configurations;
   private MergeStrategy mergeStrategy;

   public CompositeConfiguration(List<? extends AbstractConfiguration> var1) {
      super(((AbstractConfiguration)var1.get(0)).getLoggerContext(), ConfigurationSource.COMPOSITE_SOURCE);
      this.rootNode = ((AbstractConfiguration)var1.get(0)).getRootNode();
      this.configurations = var1;
      String var2 = PropertiesUtil.getProperties().getStringProperty("log4j.mergeStrategy", DefaultMergeStrategy.class.getName());

      try {
         this.mergeStrategy = Loader.newInstanceOf(var2);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException | ClassNotFoundException var8) {
         this.mergeStrategy = new DefaultMergeStrategy();
      }

      for (AbstractConfiguration var4 : var1) {
         this.mergeStrategy.mergeRootProperties(this.rootNode, var4);
      }

      StatusConfiguration var9 = new StatusConfiguration().withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());

      for (Entry var5 : this.rootNode.getAttributes().entrySet()) {
         String var6 = (String)var5.getKey();
         String var7 = this.getConfigurationStrSubstitutor().replace((String)var5.getValue());
         if ("status".equalsIgnoreCase(var6)) {
            var9.withStatus(var7.toUpperCase());
         } else if ("dest".equalsIgnoreCase(var6)) {
            var9.withDestination(var7);
         } else if ("shutdownHook".equalsIgnoreCase(var6)) {
            this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var7);
         } else if ("shutdownTimeout".equalsIgnoreCase(var6)) {
            this.shutdownTimeoutMillis = Long.parseLong(var7);
         } else if ("verbose".equalsIgnoreCase(var6)) {
            var9.withVerbosity(var7);
         } else if ("packages".equalsIgnoreCase(var6)) {
            this.pluginPackages.addAll(Arrays.asList(var7.split(Patterns.COMMA_SEPARATOR)));
         } else if ("name".equalsIgnoreCase(var6)) {
            this.setName(var7);
         }
      }

      var9.initialize();
   }

   @Override
   public void setup() {
      AbstractConfiguration var1 = this.configurations.get(0);
      this.staffChildConfiguration(var1);
      WatchManager var2 = this.getWatchManager();
      WatchManager var3 = var1.getWatchManager();
      if (var3.getIntervalSeconds() > 0) {
         var2.setIntervalSeconds(var3.getIntervalSeconds());
         Map var4 = var3.getConfigurationWatchers();

         for (Entry var6 : var4.entrySet()) {
            var2.watch((Source)var6.getKey(), ((Watcher)var6.getValue()).newWatcher(this, this.listeners, ((Watcher)var6.getValue()).getLastModified()));
         }
      }

      for (AbstractConfiguration var14 : this.configurations.subList(1, this.configurations.size())) {
         this.staffChildConfiguration(var14);
         Node var15 = var14.getRootNode();
         this.mergeStrategy.mergConfigurations(this.rootNode, var15, this.getPluginManager());
         if (LOGGER.isEnabled(Level.ALL)) {
            StringBuilder var7 = new StringBuilder();
            this.printNodes("", this.rootNode, var7);
            System.out.println(var7.toString());
         }

         int var16 = var14.getWatchManager().getIntervalSeconds();
         if (var16 > 0) {
            int var8 = var2.getIntervalSeconds();
            if (var8 <= 0 || var16 < var8) {
               var2.setIntervalSeconds(var16);
            }

            WatchManager var9 = var14.getWatchManager();
            Map var10 = var9.getConfigurationWatchers();

            for (Entry var12 : var10.entrySet()) {
               var2.watch((Source)var12.getKey(), ((Watcher)var12.getValue()).newWatcher(this, this.listeners, ((Watcher)var12.getValue()).getLastModified()));
            }
         }
      }
   }

   @Override
   public Configuration reconfigure() {
      LOGGER.debug("Reconfiguring composite configuration");
      ArrayList var1 = new ArrayList();
      ConfigurationFactory var2 = ConfigurationFactory.getInstance();

      for (AbstractConfiguration var4 : this.configurations) {
         ConfigurationSource var5 = var4.getConfigurationSource();
         URI var6 = var5.getURI();
         Object var7 = var4;
         if (var6 == null) {
            LOGGER.warn("Unable to determine URI for configuration {}, changes to it will be ignored", var4.getName());
         } else {
            var7 = var2.getConfiguration(this.getLoggerContext(), var4.getName(), var6);
            if (var7 == null) {
               LOGGER.warn("Unable to reload configuration {}, changes to it will be ignored", var4.getName());
            }
         }

         var1.add((AbstractConfiguration)var7);
      }

      return new CompositeConfiguration(var1);
   }

   private void staffChildConfiguration(AbstractConfiguration var1) {
      var1.setPluginManager(this.pluginManager);
      var1.setScriptManager(this.scriptManager);
      var1.setup();
   }

   private void printNodes(String var1, Node var2, StringBuilder var3) {
      var3.append(var1).append(var2.getName()).append(" type: ").append(var2.getType()).append("\n");
      var3.append(var1).append(var2.getAttributes().toString()).append("\n");

      for (Node var5 : var2.getChildren()) {
         this.printNodes(var1 + "  ", var5, var3);
      }
   }

   @Override
   public String toString() {
      return this.getClass().getName()
         + "@"
         + Integer.toHexString(this.hashCode())
         + " [configurations="
         + this.configurations
         + ", mergeStrategy="
         + this.mergeStrategy
         + ", rootNode="
         + this.rootNode
         + ", listeners="
         + this.listeners
         + ", pluginPackages="
         + this.pluginPackages
         + ", pluginManager="
         + this.pluginManager
         + ", isShutdownHookEnabled="
         + this.isShutdownHookEnabled
         + ", shutdownTimeoutMillis="
         + this.shutdownTimeoutMillis
         + ", scriptManager="
         + this.scriptManager
         + "]";
   }
}
