package org.apache.logging.log4j.core.config.properties;

import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterableComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggableComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptFileComponentBuilder;
import org.apache.logging.log4j.core.util.Builder;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class PropertiesConfigurationBuilder extends ConfigurationBuilderFactory implements Builder<PropertiesConfiguration> {
   private static final String ADVERTISER_KEY = "advertiser";
   private static final String STATUS_KEY = "status";
   private static final String SHUTDOWN_HOOK = "shutdownHook";
   private static final String SHUTDOWN_TIMEOUT = "shutdownTimeout";
   private static final String VERBOSE = "verbose";
   private static final String DEST = "dest";
   private static final String PACKAGES = "packages";
   private static final String CONFIG_NAME = "name";
   private static final String MONITOR_INTERVAL = "monitorInterval";
   private static final String CONFIG_TYPE = "type";
   private final ConfigurationBuilder<PropertiesConfiguration> builder = newConfigurationBuilder(PropertiesConfiguration.class);
   private LoggerContext loggerContext;
   private Properties rootProperties;

   public PropertiesConfigurationBuilder setRootProperties(Properties var1) {
      this.rootProperties = var1;
      return this;
   }

   public PropertiesConfigurationBuilder setConfigurationSource(ConfigurationSource var1) {
      this.builder.setConfigurationSource(var1);
      return this;
   }

   public PropertiesConfiguration build() {
      for (String var2 : this.rootProperties.stringPropertyNames()) {
         if (!var2.contains(".")) {
            this.builder.addRootProperty(var2, this.rootProperties.getProperty(var2));
         }
      }

      this.builder
         .setStatusLevel(Level.toLevel(this.rootProperties.getProperty("status"), Level.ERROR))
         .setShutdownHook(this.rootProperties.getProperty("shutdownHook"))
         .setShutdownTimeout(Long.parseLong(this.rootProperties.getProperty("shutdownTimeout", "0")), TimeUnit.MILLISECONDS)
         .setVerbosity(this.rootProperties.getProperty("verbose"))
         .setDestination(this.rootProperties.getProperty("dest"))
         .setPackages(this.rootProperties.getProperty("packages"))
         .setConfigurationName(this.rootProperties.getProperty("name"))
         .setMonitorInterval(this.rootProperties.getProperty("monitorInterval", "0"))
         .setAdvertiser(this.rootProperties.getProperty("advertiser"));
      Properties var13 = PropertiesUtil.extractSubset(this.rootProperties, "property");

      for (String var3 : var13.stringPropertyNames()) {
         this.builder.addProperty(var3, var13.getProperty(var3));
      }

      Map var15 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "script"));

      for (Entry var4 : var15.entrySet()) {
         Properties var5 = (Properties)var4.getValue();
         String var6 = (String)var5.remove("type");
         if (var6 == null) {
            throw new ConfigurationException("No type provided for script - must be Script or ScriptFile");
         }

         if (var6.equalsIgnoreCase("script")) {
            this.builder.add(this.createScript(var5));
         } else {
            this.builder.add(this.createScriptFile(var5));
         }
      }

      Properties var17 = PropertiesUtil.extractSubset(this.rootProperties, "customLevel");
      if (var17.size() > 0) {
         for (String var20 : var17.stringPropertyNames()) {
            this.builder.add(this.builder.newCustomLevel(var20, Integer.parseInt(var17.getProperty(var20))));
         }
      }

      String var19 = this.rootProperties.getProperty("filters");
      if (var19 != null) {
         String[] var21 = var19.split(",");

         for (String var9 : var21) {
            String var10 = var9.trim();
            this.builder.add(this.createFilter(var10, PropertiesUtil.extractSubset(this.rootProperties, "filter." + var10)));
         }
      } else {
         Map var22 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "filter"));

         for (Entry var29 : var22.entrySet()) {
            this.builder.add(this.createFilter(((String)var29.getKey()).trim(), (Properties)var29.getValue()));
         }
      }

      String var23 = this.rootProperties.getProperty("appenders");
      if (var23 != null) {
         String[] var26 = var23.split(",");

         for (String var42 : var26) {
            String var11 = var42.trim();
            this.builder.add(this.createAppender(var42.trim(), PropertiesUtil.extractSubset(this.rootProperties, "appender." + var11)));
         }
      } else {
         Map var27 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "appender"));

         for (Entry var36 : var27.entrySet()) {
            this.builder.add(this.createAppender(((String)var36.getKey()).trim(), (Properties)var36.getValue()));
         }
      }

      String var28 = this.rootProperties.getProperty("loggers");
      if (var28 != null) {
         String[] var32 = var28.split(",");

         for (String var45 : var32) {
            String var12 = var45.trim();
            if (!var12.equals("root")) {
               this.builder.add(this.createLogger(var12, PropertiesUtil.extractSubset(this.rootProperties, "logger." + var12)));
            }
         }
      } else {
         Map var33 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(this.rootProperties, "logger"));

         for (Entry var41 : var33.entrySet()) {
            String var44 = ((String)var41.getKey()).trim();
            if (!var44.equals("root")) {
               this.builder.add(this.createLogger(var44, (Properties)var41.getValue()));
            }
         }
      }

      Properties var34 = PropertiesUtil.extractSubset(this.rootProperties, "rootLogger");
      if (var34.size() > 0) {
         this.builder.add(this.createRootLogger(var34));
      }

      this.builder.setLoggerContext(this.loggerContext);
      return this.builder.build(false);
   }

   private ScriptComponentBuilder createScript(Properties var1) {
      String var2 = (String)var1.remove("name");
      String var3 = (String)var1.remove("language");
      String var4 = (String)var1.remove("text");
      ScriptComponentBuilder var5 = this.builder.newScript(var2, var3, var4);
      return processRemainingProperties(var5, var1);
   }

   private ScriptFileComponentBuilder createScriptFile(Properties var1) {
      String var2 = (String)var1.remove("name");
      String var3 = (String)var1.remove("path");
      ScriptFileComponentBuilder var4 = this.builder.newScriptFile(var2, var3);
      return processRemainingProperties(var4, var1);
   }

   private AppenderComponentBuilder createAppender(String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No name attribute provided for Appender " + var1);
      } else {
         String var4 = (String)var2.remove("type");
         if (Strings.isEmpty(var4)) {
            throw new ConfigurationException("No type attribute provided for Appender " + var1);
         } else {
            AppenderComponentBuilder var5 = this.builder.newAppender(var3, var4);
            this.addFiltersToComponent(var5, var2);
            Properties var6 = PropertiesUtil.extractSubset(var2, "layout");
            if (var6.size() > 0) {
               var5.add(this.createLayout(var3, var6));
            }

            return processRemainingProperties(var5, var2);
         }
      }
   }

   private FilterComponentBuilder createFilter(String var1, Properties var2) {
      String var3 = (String)var2.remove("type");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No type attribute provided for Filter " + var1);
      } else {
         String var4 = (String)var2.remove("onMatch");
         String var5 = (String)var2.remove("onMismatch");
         FilterComponentBuilder var6 = this.builder.newFilter(var3, var4, var5);
         return processRemainingProperties(var6, var2);
      }
   }

   private AppenderRefComponentBuilder createAppenderRef(String var1, Properties var2) {
      String var3 = (String)var2.remove("ref");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No ref attribute provided for AppenderRef " + var1);
      } else {
         AppenderRefComponentBuilder var4 = this.builder.newAppenderRef(var3);
         String var5 = Strings.trimToNull((String)var2.remove("level"));
         if (!Strings.isEmpty(var5)) {
            var4.addAttribute("level", var5);
         }

         return this.addFiltersToComponent(var4, var2);
      }
   }

   private LoggerComponentBuilder createLogger(String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      String var4 = (String)var2.remove("includeLocation");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No name attribute provided for Logger " + var1);
      } else {
         String var5 = Strings.trimToNull((String)var2.remove("level"));
         String var6 = (String)var2.remove("type");
         LoggerComponentBuilder var7;
         if (var6 != null) {
            if (!var6.equalsIgnoreCase("asyncLogger")) {
               throw new ConfigurationException("Unknown Logger type " + var6 + " for Logger " + var3);
            }

            if (var4 != null) {
               boolean var8 = Boolean.parseBoolean(var4);
               var7 = this.builder.newAsyncLogger(var3, var5, var8);
            } else {
               var7 = this.builder.newAsyncLogger(var3, var5);
            }
         } else if (var4 != null) {
            boolean var10 = Boolean.parseBoolean(var4);
            var7 = this.builder.newLogger(var3, var5, var10);
         } else {
            var7 = this.builder.newLogger(var3, var5);
         }

         this.addLoggersToComponent(var7, var2);
         this.addFiltersToComponent(var7, var2);
         String var9 = (String)var2.remove("additivity");
         if (!Strings.isEmpty(var9)) {
            var7.addAttribute("additivity", var9);
         }

         return var7;
      }
   }

   private RootLoggerComponentBuilder createRootLogger(Properties var1) {
      String var2 = Strings.trimToNull((String)var1.remove("level"));
      String var3 = (String)var1.remove("type");
      String var4 = (String)var1.remove("includeLocation");
      RootLoggerComponentBuilder var6;
      if (var3 != null) {
         if (!var3.equalsIgnoreCase("asyncRoot")) {
            throw new ConfigurationException("Unknown Logger type for root logger" + var3);
         }

         if (var4 != null) {
            boolean var5 = Boolean.parseBoolean(var4);
            var6 = this.builder.newAsyncRootLogger(var2, var5);
         } else {
            var6 = this.builder.newAsyncRootLogger(var2);
         }
      } else if (var4 != null) {
         boolean var7 = Boolean.parseBoolean(var4);
         var6 = this.builder.newRootLogger(var2, var7);
      } else {
         var6 = this.builder.newRootLogger(var2);
      }

      this.addLoggersToComponent(var6, var1);
      return this.addFiltersToComponent(var6, var1);
   }

   private LayoutComponentBuilder createLayout(String var1, Properties var2) {
      String var3 = (String)var2.remove("type");
      if (Strings.isEmpty(var3)) {
         throw new ConfigurationException("No type attribute provided for Layout on Appender " + var1);
      } else {
         LayoutComponentBuilder var4 = this.builder.newLayout(var3);
         return processRemainingProperties(var4, var2);
      }
   }

   private static <B extends ComponentBuilder<B>> ComponentBuilder<B> createComponent(ComponentBuilder<?> var0, String var1, Properties var2) {
      String var3 = (String)var2.remove("name");
      String var4 = (String)var2.remove("type");
      if (Strings.isEmpty(var4)) {
         throw new ConfigurationException("No type attribute provided for component " + var1);
      } else {
         ComponentBuilder var5 = var0.getBuilder().newComponent(var3, var4);
         return processRemainingProperties(var5, var2);
      }
   }

   private static <B extends ComponentBuilder<?>> B processRemainingProperties(B var0, Properties var1) {
      while (var1.size() > 0) {
         String var2 = var1.stringPropertyNames().iterator().next();
         int var3 = var2.indexOf(46);
         if (var3 > 0) {
            String var4 = var2.substring(0, var3);
            Properties var5 = PropertiesUtil.extractSubset(var1, var4);
            var0.addComponent(createComponent(var0, var4, var5));
         } else {
            var0.addAttribute(var2, var1.getProperty(var2));
            var1.remove(var2);
         }
      }

      return (B)var0;
   }

   private <B extends FilterableComponentBuilder<? extends ComponentBuilder<?>>> B addFiltersToComponent(B var1, Properties var2) {
      Map var3 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(var2, "filter"));

      for (Entry var5 : var3.entrySet()) {
         var1.add(this.createFilter(((String)var5.getKey()).trim(), (Properties)var5.getValue()));
      }

      return (B)var1;
   }

   private <B extends LoggableComponentBuilder<? extends ComponentBuilder<?>>> B addLoggersToComponent(B var1, Properties var2) {
      Map var3 = PropertiesUtil.partitionOnCommonPrefixes(PropertiesUtil.extractSubset(var2, "appenderRef"));

      for (Entry var5 : var3.entrySet()) {
         var1.add(this.createAppenderRef(((String)var5.getKey()).trim(), (Properties)var5.getValue()));
      }

      return (B)var1;
   }

   public PropertiesConfigurationBuilder setLoggerContext(LoggerContext var1) {
      this.loggerContext = var1;
      return this;
   }

   public LoggerContext getLoggerContext() {
      return this.loggerContext;
   }
}
