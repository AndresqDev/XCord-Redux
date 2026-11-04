package org.apache.logging.log4j.core.config.builder.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.CustomLevelComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.KeyValuePairComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.PropertyComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ScriptFileComponentBuilder;
import org.apache.logging.log4j.core.util.Throwables;

public class DefaultConfigurationBuilder<T extends BuiltConfiguration> implements ConfigurationBuilder<T> {
   private static final String INDENT = "  ";
   private final Component root = new Component();
   private Component loggers;
   private Component appenders;
   private Component filters;
   private Component properties;
   private Component customLevels;
   private Component scripts;
   private final Class<T> clazz;
   private ConfigurationSource source;
   private int monitorInterval;
   private Level level;
   private String verbosity;
   private String destination;
   private String packages;
   private String shutdownFlag;
   private long shutdownTimeoutMillis;
   private String advertiser;
   private LoggerContext loggerContext;
   private String name;

   public static void formatXml(Source var0, Result var1) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
      Transformer var2 = TransformerFactory.newInstance().newTransformer();
      var2.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString("  ".length()));
      var2.setOutputProperty("indent", "yes");
      var2.transform(var0, var1);
   }

   public DefaultConfigurationBuilder() {
      this((Class<T>)BuiltConfiguration.class);
      this.root.addAttribute("name", "Built");
   }

   public DefaultConfigurationBuilder(Class<T> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("A Configuration class must be provided");
      } else {
         this.clazz = var1;
         List var2 = this.root.getComponents();
         this.properties = new Component("Properties");
         var2.add(this.properties);
         this.scripts = new Component("Scripts");
         var2.add(this.scripts);
         this.customLevels = new Component("CustomLevels");
         var2.add(this.customLevels);
         this.filters = new Component("Filters");
         var2.add(this.filters);
         this.appenders = new Component("Appenders");
         var2.add(this.appenders);
         this.loggers = new Component("Loggers");
         var2.add(this.loggers);
      }
   }

   protected ConfigurationBuilder<T> add(Component var1, ComponentBuilder<?> var2) {
      var1.getComponents().add((Component)var2.build());
      return this;
   }

   @Override
   public ConfigurationBuilder<T> add(AppenderComponentBuilder var1) {
      return this.add(this.appenders, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(CustomLevelComponentBuilder var1) {
      return this.add(this.customLevels, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(FilterComponentBuilder var1) {
      return this.add(this.filters, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(ScriptComponentBuilder var1) {
      return this.add(this.scripts, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(ScriptFileComponentBuilder var1) {
      return this.add(this.scripts, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(LoggerComponentBuilder var1) {
      return this.add(this.loggers, var1);
   }

   @Override
   public ConfigurationBuilder<T> add(RootLoggerComponentBuilder var1) {
      for (Component var3 : this.loggers.getComponents()) {
         if (var3.getPluginType().equals("root")) {
            throw new ConfigurationException("Root Logger was previously defined");
         }
      }

      return this.add(this.loggers, var1);
   }

   @Override
   public ConfigurationBuilder<T> addProperty(String var1, String var2) {
      this.properties.addComponent((Component)this.newComponent(var1, "Property", var2).build());
      return this;
   }

   public T build() {
      return this.build(true);
   }

   public T build(boolean var1) {
      BuiltConfiguration var2;
      try {
         if (this.source == null) {
            this.source = ConfigurationSource.NULL_SOURCE;
         }

         Constructor var3 = this.clazz.getConstructor(LoggerContext.class, ConfigurationSource.class, Component.class);
         var2 = (BuiltConfiguration)var3.newInstance(this.loggerContext, this.source, this.root);
         var2.getRootNode().getAttributes().putAll(this.root.getAttributes());
         if (this.name != null) {
            var2.setName(this.name);
         }

         if (this.level != null) {
            var2.getStatusConfiguration().withStatus(this.level);
         }

         if (this.verbosity != null) {
            var2.getStatusConfiguration().withVerbosity(this.verbosity);
         }

         if (this.destination != null) {
            var2.getStatusConfiguration().withDestination(this.destination);
         }

         if (this.packages != null) {
            var2.setPluginPackages(this.packages);
         }

         if (this.shutdownFlag != null) {
            var2.setShutdownHook(this.shutdownFlag);
         }

         if (this.shutdownTimeoutMillis > 0L) {
            var2.setShutdownTimeoutMillis(this.shutdownTimeoutMillis);
         }

         if (this.advertiser != null) {
            var2.createAdvertiser(this.advertiser, this.source);
         }

         var2.setMonitorInterval(this.monitorInterval);
      } catch (Exception var4) {
         throw new IllegalArgumentException("Invalid Configuration class specified", var4);
      }

      var2.getStatusConfiguration().initialize();
      if (var1) {
         var2.initialize();
      }

      return (T)var2;
   }

   private String formatXml(String var1) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
      StringWriter var2 = new StringWriter();
      formatXml(new StreamSource(new StringReader(var1)), new StreamResult(var2));
      return var2.toString();
   }

   @Override
   public void writeXmlConfiguration(OutputStream var1) throws IOException {
      try {
         XMLStreamWriter var2 = XMLOutputFactory.newInstance().createXMLStreamWriter(var1);
         this.writeXmlConfiguration(var2);
         var2.close();
      } catch (XMLStreamException var3) {
         if (var3.getNestedException() instanceof IOException) {
            throw (IOException)var3.getNestedException();
         }

         Throwables.rethrow(var3);
      }
   }

   @Override
   public String toXmlConfiguration() {
      StringWriter var1 = new StringWriter();

      try {
         XMLStreamWriter var2 = XMLOutputFactory.newInstance().createXMLStreamWriter(var1);
         this.writeXmlConfiguration(var2);
         var2.close();
         return this.formatXml(var1.toString());
      } catch (TransformerException | XMLStreamException var3) {
         Throwables.rethrow(var3);
         return var1.toString();
      }
   }

   private void writeXmlConfiguration(XMLStreamWriter var1) throws XMLStreamException {
      var1.writeStartDocument();
      var1.writeStartElement("Configuration");
      if (this.name != null) {
         var1.writeAttribute("name", this.name);
      }

      if (this.level != null) {
         var1.writeAttribute("status", this.level.name());
      }

      if (this.verbosity != null) {
         var1.writeAttribute("verbose", this.verbosity);
      }

      if (this.destination != null) {
         var1.writeAttribute("dest", this.destination);
      }

      if (this.packages != null) {
         var1.writeAttribute("packages", this.packages);
      }

      if (this.shutdownFlag != null) {
         var1.writeAttribute("shutdownHook", this.shutdownFlag);
      }

      if (this.shutdownTimeoutMillis > 0L) {
         var1.writeAttribute("shutdownTimeout", String.valueOf(this.shutdownTimeoutMillis));
      }

      if (this.advertiser != null) {
         var1.writeAttribute("advertiser", this.advertiser);
      }

      if (this.monitorInterval > 0) {
         var1.writeAttribute("monitorInterval", String.valueOf(this.monitorInterval));
      }

      this.writeXmlSection(var1, this.properties);
      this.writeXmlSection(var1, this.scripts);
      this.writeXmlSection(var1, this.customLevels);
      if (this.filters.getComponents().size() == 1) {
         this.writeXmlComponent(var1, this.filters.getComponents().get(0));
      } else if (this.filters.getComponents().size() > 1) {
         this.writeXmlSection(var1, this.filters);
      }

      this.writeXmlSection(var1, this.appenders);
      this.writeXmlSection(var1, this.loggers);
      var1.writeEndElement();
      var1.writeEndDocument();
   }

   private void writeXmlSection(XMLStreamWriter var1, Component var2) throws XMLStreamException {
      if (!var2.getAttributes().isEmpty() || !var2.getComponents().isEmpty() || var2.getValue() != null) {
         this.writeXmlComponent(var1, var2);
      }
   }

   private void writeXmlComponent(XMLStreamWriter var1, Component var2) throws XMLStreamException {
      if (var2.getComponents().isEmpty() && var2.getValue() == null) {
         var1.writeEmptyElement(var2.getPluginType());
         this.writeXmlAttributes(var1, var2);
      } else {
         var1.writeStartElement(var2.getPluginType());
         this.writeXmlAttributes(var1, var2);

         for (Component var4 : var2.getComponents()) {
            this.writeXmlComponent(var1, var4);
         }

         if (var2.getValue() != null) {
            var1.writeCharacters(var2.getValue());
         }

         var1.writeEndElement();
      }
   }

   private void writeXmlAttributes(XMLStreamWriter var1, Component var2) throws XMLStreamException {
      for (Entry var4 : var2.getAttributes().entrySet()) {
         var1.writeAttribute((String)var4.getKey(), (String)var4.getValue());
      }
   }

   @Override
   public ScriptComponentBuilder newScript(String var1, String var2, String var3) {
      return new DefaultScriptComponentBuilder(this, var1, var2, var3);
   }

   @Override
   public ScriptFileComponentBuilder newScriptFile(String var1) {
      return new DefaultScriptFileComponentBuilder(this, var1, var1);
   }

   @Override
   public ScriptFileComponentBuilder newScriptFile(String var1, String var2) {
      return new DefaultScriptFileComponentBuilder(this, var1, var2);
   }

   @Override
   public AppenderComponentBuilder newAppender(String var1, String var2) {
      return new DefaultAppenderComponentBuilder(this, var1, var2);
   }

   @Override
   public AppenderRefComponentBuilder newAppenderRef(String var1) {
      return new DefaultAppenderRefComponentBuilder(this, var1);
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1) {
      return new DefaultLoggerComponentBuilder(this, var1, null, "AsyncLogger");
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1, boolean var2) {
      return new DefaultLoggerComponentBuilder(this, var1, null, "AsyncLogger", var2);
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1, Level var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), "AsyncLogger");
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1, Level var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), "AsyncLogger", var3);
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1, String var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, "AsyncLogger");
   }

   @Override
   public LoggerComponentBuilder newAsyncLogger(String var1, String var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, "AsyncLogger", var3);
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger() {
      return new DefaultRootLoggerComponentBuilder(this, "AsyncRoot");
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger(boolean var1) {
      return new DefaultRootLoggerComponentBuilder(this, null, "AsyncRoot", var1);
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger(Level var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), "AsyncRoot");
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger(Level var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), "AsyncRoot", var2);
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger(String var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1, "AsyncRoot");
   }

   @Override
   public RootLoggerComponentBuilder newAsyncRootLogger(String var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1, "AsyncRoot", var2);
   }

   @Override
   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1) {
      return new DefaultComponentBuilder<>(this, var1);
   }

   @Override
   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2) {
      return new DefaultComponentBuilder<>(this, var1, var2);
   }

   @Override
   public <B extends ComponentBuilder<B>> ComponentBuilder<B> newComponent(String var1, String var2, String var3) {
      return new DefaultComponentBuilder<>(this, var1, var2, var3);
   }

   @Override
   public PropertyComponentBuilder newProperty(String var1, String var2) {
      return new DefaultPropertyComponentBuilder(this, var1, var2);
   }

   @Override
   public KeyValuePairComponentBuilder newKeyValuePair(String var1, String var2) {
      return new DefaultKeyValuePairComponentBuilder(this, var1, var2);
   }

   @Override
   public CustomLevelComponentBuilder newCustomLevel(String var1, int var2) {
      return new DefaultCustomLevelComponentBuilder(this, var1, var2);
   }

   @Override
   public FilterComponentBuilder newFilter(String var1, Filter.Result var2, Filter.Result var3) {
      return new DefaultFilterComponentBuilder(this, var1, var2.name(), var3.name());
   }

   @Override
   public FilterComponentBuilder newFilter(String var1, String var2, String var3) {
      return new DefaultFilterComponentBuilder(this, var1, var2, var3);
   }

   @Override
   public LayoutComponentBuilder newLayout(String var1) {
      return new DefaultLayoutComponentBuilder(this, var1);
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1) {
      return new DefaultLoggerComponentBuilder(this, var1, null);
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1, boolean var2) {
      return new DefaultLoggerComponentBuilder(this, var1, null, var2);
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1, Level var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString());
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1, Level var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2.toString(), var3);
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1, String var2) {
      return new DefaultLoggerComponentBuilder(this, var1, var2);
   }

   @Override
   public LoggerComponentBuilder newLogger(String var1, String var2, boolean var3) {
      return new DefaultLoggerComponentBuilder(this, var1, var2, var3);
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger() {
      return new DefaultRootLoggerComponentBuilder(this, null);
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger(boolean var1) {
      return new DefaultRootLoggerComponentBuilder(this, null, var1);
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger(Level var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString());
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger(Level var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1.toString(), var2);
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger(String var1) {
      return new DefaultRootLoggerComponentBuilder(this, var1);
   }

   @Override
   public RootLoggerComponentBuilder newRootLogger(String var1, boolean var2) {
      return new DefaultRootLoggerComponentBuilder(this, var1, var2);
   }

   @Override
   public ConfigurationBuilder<T> setAdvertiser(String var1) {
      this.advertiser = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setConfigurationName(String var1) {
      this.name = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setConfigurationSource(ConfigurationSource var1) {
      this.source = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setMonitorInterval(String var1) {
      this.monitorInterval = Integer.parseInt(var1);
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setPackages(String var1) {
      this.packages = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setShutdownHook(String var1) {
      this.shutdownFlag = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setShutdownTimeout(long var1, TimeUnit var3) {
      this.shutdownTimeoutMillis = var3.toMillis(var1);
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setStatusLevel(Level var1) {
      this.level = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setVerbosity(String var1) {
      this.verbosity = var1;
      return this;
   }

   @Override
   public ConfigurationBuilder<T> setDestination(String var1) {
      this.destination = var1;
      return this;
   }

   @Override
   public void setLoggerContext(LoggerContext var1) {
      this.loggerContext = var1;
   }

   @Override
   public ConfigurationBuilder<T> addRootProperty(String var1, String var2) {
      this.root.getAttributes().put(var1, var2);
      return this;
   }
}
