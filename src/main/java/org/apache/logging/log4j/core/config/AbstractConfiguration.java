package org.apache.logging.log4j.core.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Version;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDelegate;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDisruptor;
import org.apache.logging.log4j.core.config.arbiters.Arbiter;
import org.apache.logging.log4j.core.config.arbiters.SelectArbiter;
import org.apache.logging.log4j.core.config.plugins.util.PluginBuilder;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.lookup.ConfigurationStrSubstitutor;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.PropertiesLookup;
import org.apache.logging.log4j.core.lookup.RuntimeStrSubstitutor;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.core.util.DummyNanoClock;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.core.util.Watcher;
import org.apache.logging.log4j.core.util.WatcherFactory;
import org.apache.logging.log4j.util.PropertiesUtil;

public abstract class AbstractConfiguration extends AbstractFilterable implements Configuration {
   private static final int BUF_SIZE = 16384;
   protected Node rootNode;
   protected final List<ConfigurationListener> listeners = new CopyOnWriteArrayList<>();
   protected final List<String> pluginPackages = new ArrayList<>();
   protected PluginManager pluginManager;
   protected boolean isShutdownHookEnabled = true;
   protected long shutdownTimeoutMillis = 0L;
   protected ScriptManager scriptManager;
   private Advertiser advertiser = new DefaultAdvertiser();
   private Node advertiserNode = null;
   private Object advertisement;
   private String name;
   private ConcurrentMap<String, Appender> appenders = new ConcurrentHashMap<>();
   private ConcurrentMap<String, LoggerConfig> loggerConfigs = new ConcurrentHashMap<>();
   private List<CustomLevelConfig> customLevels = Collections.emptyList();
   private final ConcurrentMap<String, String> propertyMap = new ConcurrentHashMap<>();
   private final StrLookup tempLookup = new Interpolator(this.propertyMap);
   private final StrSubstitutor subst = new RuntimeStrSubstitutor(this.tempLookup);
   private final StrSubstitutor configurationStrSubstitutor = new ConfigurationStrSubstitutor(this.subst);
   private LoggerConfig root = new LoggerConfig();
   private final ConcurrentMap<String, Object> componentMap = new ConcurrentHashMap<>();
   private final ConfigurationSource configurationSource;
   private final ConfigurationScheduler configurationScheduler = new ConfigurationScheduler();
   private final WatchManager watchManager = new WatchManager(this.configurationScheduler);
   private AsyncLoggerConfigDisruptor asyncLoggerConfigDisruptor;
   private NanoClock nanoClock = new DummyNanoClock();
   private final WeakReference<LoggerContext> loggerContext;

   protected AbstractConfiguration(LoggerContext var1, ConfigurationSource var2) {
      this.loggerContext = new WeakReference<>(var1);
      this.configurationSource = Objects.requireNonNull(var2, "configurationSource is null");
      this.componentMap.put("ContextProperties", this.propertyMap);
      this.pluginManager = new PluginManager("Core");
      this.rootNode = new Node();
      this.setState(LifeCycle.State.INITIALIZING);
   }

   @Override
   public ConfigurationSource getConfigurationSource() {
      return this.configurationSource;
   }

   @Override
   public List<String> getPluginPackages() {
      return this.pluginPackages;
   }

   @Override
   public Map<String, String> getProperties() {
      return this.propertyMap;
   }

   @Override
   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public void setScriptManager(ScriptManager var1) {
      this.scriptManager = var1;
   }

   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   public void setPluginManager(PluginManager var1) {
      this.pluginManager = var1;
   }

   @Override
   public WatchManager getWatchManager() {
      return this.watchManager;
   }

   @Override
   public ConfigurationScheduler getScheduler() {
      return this.configurationScheduler;
   }

   public Node getRootNode() {
      return this.rootNode;
   }

   @Override
   public AsyncLoggerConfigDelegate getAsyncLoggerConfigDelegate() {
      if (this.asyncLoggerConfigDisruptor == null) {
         this.asyncLoggerConfigDisruptor = new AsyncLoggerConfigDisruptor();
      }

      return this.asyncLoggerConfigDisruptor;
   }

   @Override
   public void initialize() {
      LOGGER.debug(Version.getProductString() + " initializing configuration {}", this);
      this.subst.setConfiguration(this);
      this.configurationStrSubstitutor.setConfiguration(this);

      try {
         this.scriptManager = new ScriptManager(this, this.watchManager);
      } catch (Exception | LinkageError var7) {
         LOGGER.info("Cannot initialize scripting support because this JRE does not support it.", (Throwable)var7);
      }

      this.pluginManager.collectPlugins(this.pluginPackages);
      PluginManager var1 = new PluginManager("Level");
      var1.collectPlugins(this.pluginPackages);
      Map var2 = var1.getPlugins();
      if (var2 != null) {
         for (PluginType var4 : var2.values()) {
            try {
               Loader.initializeClass(var4.getPluginClass().getName(), var4.getPluginClass().getClassLoader());
            } catch (Exception var6) {
               LOGGER.error("Unable to initialize {} due to {}", var4.getPluginClass().getName(), var6.getClass().getSimpleName(), var6);
            }
         }
      }

      this.setup();
      this.setupAdvertisement();
      this.doConfigure();
      this.setState(LifeCycle.State.INITIALIZED);
      LOGGER.debug("Configuration {} initialized", this);
   }

   protected void initializeWatchers(Reconfigurable var1, ConfigurationSource var2, int var3) {
      if (var2.getFile() != null || var2.getURL() != null) {
         if (var3 > 0) {
            this.watchManager.setIntervalSeconds(var3);
            if (var2.getFile() != null) {
               Source var4 = new Source(var2);
               long var5 = var2.getFile().lastModified();
               ConfigurationFileWatcher var7 = new ConfigurationFileWatcher(this, var1, this.listeners, var5);
               this.watchManager.watch(var4, var7);
            } else if (var2.getURL() != null) {
               this.monitorSource(var1, var2);
            }
         } else if (this.watchManager.hasEventListeners() && var2.getURL() != null && var3 >= 0) {
            this.monitorSource(var1, var2);
         }
      }
   }

   private void monitorSource(Reconfigurable var1, ConfigurationSource var2) {
      if (var2.getLastModified() > 0L) {
         Source var3 = new Source(var2);
         Watcher var4 = WatcherFactory.getInstance(this.pluginPackages).newWatcher(var3, this, var1, this.listeners, var2.getLastModified());
         if (var4 != null) {
            this.watchManager.watch(var3, var4);
         }
      } else {
         LOGGER.info("{} does not support dynamic reconfiguration", var2.getURI());
      }
   }

   @Override
   public void start() {
      if (this.getState().equals(LifeCycle.State.INITIALIZING)) {
         this.initialize();
      }

      LOGGER.debug("Starting configuration {}", this);
      this.setStarting();
      if (this.watchManager.getIntervalSeconds() >= 0) {
         this.watchManager.start();
      }

      if (this.hasAsyncLoggers()) {
         this.asyncLoggerConfigDisruptor.start();
      }

      HashSet var1 = new HashSet();

      for (LoggerConfig var3 : this.loggerConfigs.values()) {
         var3.start();
         var1.add(var3);
      }

      for (Appender var5 : this.appenders.values()) {
         var5.start();
      }

      if (!var1.contains(this.root)) {
         this.root.start();
      }

      super.start();
      LOGGER.debug("Started configuration {} OK.", this);
   }

   private boolean hasAsyncLoggers() {
      if (this.root instanceof AsyncLoggerConfig) {
         return true;
      } else {
         for (LoggerConfig var2 : this.loggerConfigs.values()) {
            if (var2 instanceof AsyncLoggerConfig) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      LOGGER.trace("Stopping {}...", this);

      for (LoggerConfig var5 : this.loggerConfigs.values()) {
         var5.getReliabilityStrategy().beforeStopConfiguration(this);
      }

      this.root.getReliabilityStrategy().beforeStopConfiguration(this);
      String var10 = this.getClass().getSimpleName();
      LOGGER.trace("{} notified {} ReliabilityStrategies that config will be stopped.", var10, this.loggerConfigs.size() + 1);
      if (!this.loggerConfigs.isEmpty()) {
         LOGGER.trace("{} stopping {} LoggerConfigs.", var10, this.loggerConfigs.size());

         for (LoggerConfig var6 : this.loggerConfigs.values()) {
            var6.stop(var1, var3);
         }
      }

      LOGGER.trace("{} stopping root LoggerConfig.", var10);
      if (!this.root.isStopped()) {
         this.root.stop(var1, var3);
      }

      if (this.hasAsyncLoggers()) {
         LOGGER.trace("{} stopping AsyncLoggerConfigDisruptor.", var10);
         this.asyncLoggerConfigDisruptor.stop(var1, var3);
      }

      LOGGER.trace("{} notifying ReliabilityStrategies that appenders will be stopped.", var10);

      for (LoggerConfig var14 : this.loggerConfigs.values()) {
         var14.getReliabilityStrategy().beforeStopAppenders();
      }

      this.root.getReliabilityStrategy().beforeStopAppenders();
      Appender[] var13 = this.appenders.values().toArray(new Appender[this.appenders.size()]);
      List var15 = this.getAsyncAppenders(var13);
      if (!var15.isEmpty()) {
         LOGGER.trace("{} stopping {} AsyncAppenders.", var10, var15.size());

         for (Appender var8 : var15) {
            if (var8 instanceof LifeCycle2) {
               ((LifeCycle2)var8).stop(var1, var3);
            } else {
               var8.stop();
            }
         }
      }

      LOGGER.trace("{} stopping remaining Appenders.", var10);
      int var16 = 0;

      for (int var17 = var13.length - 1; var17 >= 0; var17--) {
         if (var13[var17].isStarted()) {
            if (var13[var17] instanceof LifeCycle2) {
               ((LifeCycle2)var13[var17]).stop(var1, var3);
            } else {
               var13[var17].stop();
            }

            var16++;
         }
      }

      LOGGER.trace("{} stopped {} remaining Appenders.", var10, var16);
      LOGGER.trace("{} cleaning Appenders from {} LoggerConfigs.", var10, this.loggerConfigs.size() + 1);

      for (LoggerConfig var9 : this.loggerConfigs.values()) {
         var9.clearAppenders();
      }

      this.root.clearAppenders();
      if (this.watchManager.isStarted()) {
         this.watchManager.stop(var1, var3);
      }

      this.configurationScheduler.stop(var1, var3);
      if (this.advertiser != null && this.advertisement != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      LOGGER.debug("Stopped {} OK", this);
      return true;
   }

   private List<Appender> getAsyncAppenders(Appender[] var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = var1.length - 1; var3 >= 0; var3--) {
         if (var1[var3] instanceof AsyncAppender) {
            var2.add(var1[var3]);
         }
      }

      return var2;
   }

   @Override
   public boolean isShutdownHookEnabled() {
      return this.isShutdownHookEnabled;
   }

   @Override
   public long getShutdownTimeoutMillis() {
      return this.shutdownTimeoutMillis;
   }

   public void setup() {
   }

   protected Level getDefaultStatus() {
      String var1 = PropertiesUtil.getProperties().getStringProperty("Log4jDefaultStatusLevel", Level.ERROR.name());

      try {
         return Level.toLevel(var1);
      } catch (Exception var3) {
         return Level.ERROR;
      }
   }

   protected void createAdvertiser(String var1, ConfigurationSource var2, byte[] var3, String var4) {
      if (var1 != null) {
         Node var5 = new Node(null, var1, null);
         Map var6 = var5.getAttributes();
         var6.put("content", new String(var3));
         var6.put("contentType", var4);
         var6.put("name", "configuration");
         if (var2.getLocation() != null) {
            var6.put("location", var2.getLocation());
         }

         this.advertiserNode = var5;
      }
   }

   private void setupAdvertisement() {
      if (this.advertiserNode != null) {
         String var1 = this.advertiserNode.getName();
         PluginType var2 = this.pluginManager.getPluginType(var1);
         if (var2 != null) {
            Class var3 = var2.getPluginClass().asSubclass(Advertiser.class);

            try {
               this.advertiser = (Advertiser)var3.newInstance();
               this.advertisement = this.advertiser.advertise(this.advertiserNode.getAttributes());
            } catch (InstantiationException var5) {
               LOGGER.error("InstantiationException attempting to instantiate advertiser: {}", var1, var5);
            } catch (IllegalAccessException var6) {
               LOGGER.error("IllegalAccessException attempting to instantiate advertiser: {}", var1, var6);
            }
         }
      }
   }

   @Override
   public <T> T getComponent(String var1) {
      return (T)this.componentMap.get(var1);
   }

   @Override
   public void addComponent(String var1, Object var2) {
      this.componentMap.putIfAbsent(var1, var2);
   }

   protected void preConfigure(Node var1) {
      try {
         for (Node var3 : var1.getChildren()) {
            if (var3.getType() == null) {
               LOGGER.error("Unable to locate plugin type for " + var3.getName());
            } else {
               Class var4 = var3.getType().getPluginClass();
               if (var4.isAnnotationPresent(Scheduled.class)) {
                  this.configurationScheduler.incrementScheduledItems();
               }

               this.preConfigure(var3);
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Error capturing node data for node " + var1.getName(), (Throwable)var5);
      }
   }

   protected void processConditionals(Node var1) {
      try {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();

         for (Node var5 : var1.getChildren()) {
            PluginType var6 = var5.getType();
            if (var6 != null && "Arbiter".equals(var6.getElementName())) {
               Class var7 = var6.getPluginClass();
               if (SelectArbiter.class.isAssignableFrom(var7)) {
                  var3.add(var5);
                  var2.addAll(this.processSelect(var5, var6));
               } else if (Arbiter.class.isAssignableFrom(var7)) {
                  var3.add(var5);

                  try {
                     Arbiter var8 = (Arbiter)this.createPluginObject(var6, var5, null);
                     if (var8.isCondition()) {
                        var2.addAll(var5.getChildren());
                        this.processConditionals(var5);
                     }
                  } catch (Exception var9) {
                     LOGGER.error("Exception processing {}: Ignoring and including children", var6.getPluginClass());
                     this.processConditionals(var5);
                  }
               } else {
                  LOGGER.error("Encountered Condition Plugin that does not implement Condition: {}", var5.getName());
                  this.processConditionals(var5);
               }
            } else {
               this.processConditionals(var5);
            }
         }

         if (!var3.isEmpty()) {
            List var11 = var1.getChildren();
            var11.removeAll(var3);
            var11.addAll(var2);

            for (Node var13 : var2) {
               var13.setParent(var1);
            }
         }
      } catch (Exception var10) {
         LOGGER.error("Error capturing node data for node " + var1.getName(), (Throwable)var10);
      }
   }

   protected List<Node> processSelect(Node var1, PluginType<?> var2) {
      ArrayList var3 = new ArrayList();
      SelectArbiter var4 = (SelectArbiter)this.createPluginObject(var2, var1, null);
      ArrayList var5 = new ArrayList();

      for (Node var7 : var1.getChildren()) {
         PluginType var8 = var7.getType();
         if (var8 != null) {
            if (Arbiter.class.isAssignableFrom(var8.getPluginClass())) {
               Arbiter var9 = (Arbiter)this.createPluginObject(var8, var7, null);
               var5.add(var9);
               var7.setObject(var9);
            } else {
               LOGGER.error("Invalid Node {} for Select. Must be a Condition", var7.getName());
            }
         } else {
            LOGGER.error("No PluginType for node {}", var7.getName());
         }
      }

      Arbiter var10 = var4.evaluateConditions(var5);
      if (var10 != null) {
         for (Node var12 : var1.getChildren()) {
            if (var10 == var12.getObject()) {
               var3.addAll(var12.getChildren());
               this.processConditionals(var12);
            }
         }
      }

      return var3;
   }

   protected void doConfigure() {
      this.processConditionals(this.rootNode);
      this.preConfigure(this.rootNode);
      this.configurationScheduler.start();
      if (this.rootNode.hasChildren() && this.rootNode.getChildren().get(0).getName().equalsIgnoreCase("Properties")) {
         Node var9 = this.rootNode.getChildren().get(0);
         this.createConfiguration(var9, null);
         if (var9.getObject() != null) {
            StrLookup var11 = var9.getObject();
            this.subst.setVariableResolver(var11);
            this.configurationStrSubstitutor.setVariableResolver(var11);
         }
      } else {
         Map var1 = this.getComponent("ContextProperties");
         PropertiesLookup var2 = var1 == null ? null : new PropertiesLookup(var1);
         Interpolator var3 = new Interpolator(var2, this.pluginPackages);
         this.subst.setVariableResolver(var3);
         this.configurationStrSubstitutor.setVariableResolver(var3);
      }

      boolean var10 = false;
      boolean var12 = false;

      for (Node var4 : this.rootNode.getChildren()) {
         if (!var4.getName().equalsIgnoreCase("Properties")) {
            this.createConfiguration(var4, null);
            if (var4.getObject() != null) {
               if (var4.getName().equalsIgnoreCase("Scripts")) {
                  for (AbstractScript var8 : var4.getObject(AbstractScript[].class)) {
                     if (var8 instanceof ScriptRef) {
                        LOGGER.error("Script reference to {} not added. Scripts definition cannot contain script references", var8.getName());
                     } else if (this.scriptManager != null) {
                        this.scriptManager.addScript(var8);
                     }
                  }
               } else if (var4.getName().equalsIgnoreCase("Appenders")) {
                  this.appenders = var4.getObject();
               } else if (var4.isInstanceOf(Filter.class)) {
                  this.addFilter(var4.getObject(Filter.class));
               } else if (var4.getName().equalsIgnoreCase("Loggers")) {
                  Loggers var5 = var4.getObject();
                  this.loggerConfigs = var5.getMap();
                  var10 = true;
                  if (var5.getRoot() != null) {
                     this.root = var5.getRoot();
                     var12 = true;
                  }
               } else if (var4.getName().equalsIgnoreCase("CustomLevels")) {
                  this.customLevels = var4.getObject(CustomLevels.class).getCustomLevels();
               } else if (var4.isInstanceOf(CustomLevelConfig.class)) {
                  ArrayList var16 = new ArrayList<>(this.customLevels);
                  var16.add(var4.getObject(CustomLevelConfig.class));
                  this.customLevels = var16;
               } else {
                  List var17 = Arrays.asList("\"Appenders\"", "\"Loggers\"", "\"Properties\"", "\"Scripts\"", "\"CustomLevels\"");
                  LOGGER.error(
                     "Unknown object \"{}\" of type {} is ignored: try nesting it inside one of: {}.",
                     var4.getName(),
                     var4.getObject().getClass().getName(),
                     var17
                  );
               }
            }
         } else if (this.tempLookup == this.subst.getVariableResolver()) {
            LOGGER.error("Properties declaration must be the first element in the configuration");
         }
      }

      if (!var10) {
         LOGGER.warn("No Loggers were configured, using default. Is the Loggers element missing?");
         this.setToDefault();
      } else {
         if (!var12) {
            LOGGER.warn("No Root logger was configured, creating default ERROR-level Root logger with Console appender");
            this.setToDefault();
         }

         for (Entry var15 : this.loggerConfigs.entrySet()) {
            LoggerConfig var19 = (LoggerConfig)var15.getValue();

            for (AppenderRef var21 : var19.getAppenderRefs()) {
               Appender var22 = this.appenders.get(var21.getRef());
               if (var22 != null) {
                  var19.addAppender(var22, var21.getLevel(), var21.getFilter());
               } else {
                  LOGGER.error("Unable to locate appender \"{}\" for logger config \"{}\"", var21.getRef(), var19);
               }
            }
         }

         this.setParents();
      }
   }

   protected void setToDefault() {
      this.setName("Default@" + Integer.toHexString(this.hashCode()));
      PatternLayout var1 = PatternLayout.newBuilder().withPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n").withConfiguration(this).build();
      ConsoleAppender var2 = ConsoleAppender.createDefaultAppenderForLayout(var1);
      var2.start();
      this.addAppender(var2);
      LoggerConfig var3 = this.getRootLogger();
      var3.addAppender(var2, null, null);
      Level var4 = Level.ERROR;
      String var5 = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.level", var4.name());
      Level var6 = Level.valueOf(var5);
      var3.setLevel(var6 != null ? var6 : var4);
   }

   public void setName(String var1) {
      this.name = var1;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public void addListener(ConfigurationListener var1) {
      this.listeners.add(var1);
   }

   @Override
   public void removeListener(ConfigurationListener var1) {
      this.listeners.remove(var1);
   }

   @Override
   public <T extends Appender> T getAppender(String var1) {
      return (T)(var1 != null ? this.appenders.get(var1) : null);
   }

   @Override
   public Map<String, Appender> getAppenders() {
      return this.appenders;
   }

   @Override
   public void addAppender(Appender var1) {
      if (var1 != null) {
         this.appenders.putIfAbsent(var1.getName(), var1);
      }
   }

   @Override
   public StrSubstitutor getStrSubstitutor() {
      return this.subst;
   }

   @Override
   public StrSubstitutor getConfigurationStrSubstitutor() {
      return this.configurationStrSubstitutor;
   }

   @Override
   public void setAdvertiser(Advertiser var1) {
      this.advertiser = var1;
   }

   @Override
   public Advertiser getAdvertiser() {
      return this.advertiser;
   }

   @Override
   public ReliabilityStrategy getReliabilityStrategy(LoggerConfig var1) {
      return ReliabilityStrategyFactory.getReliabilityStrategy(var1);
   }

   @Override
   public synchronized void addLoggerAppender(Logger var1, Appender var2) {
      if (var2 != null && var1 != null) {
         String var3 = var1.getName();
         this.appenders.putIfAbsent(var2.getName(), var2);
         LoggerConfig var4 = this.getLoggerConfig(var3);
         if (var4.getName().equals(var3)) {
            var4.addAppender(var2, null, null);
         } else {
            LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var4.isAdditive());
            var5.addAppender(var2, null, null);
            var5.setParent(var4);
            this.loggerConfigs.putIfAbsent(var3, var5);
            this.setParents();
            var1.getContext().updateLoggers();
         }
      }
   }

   @Override
   public synchronized void addLoggerFilter(Logger var1, Filter var2) {
      String var3 = var1.getName();
      LoggerConfig var4 = this.getLoggerConfig(var3);
      if (var4.getName().equals(var3)) {
         var4.addFilter(var2);
      } else {
         LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var4.isAdditive());
         var5.addFilter(var2);
         var5.setParent(var4);
         this.loggerConfigs.putIfAbsent(var3, var5);
         this.setParents();
         var1.getContext().updateLoggers();
      }
   }

   @Override
   public synchronized void setLoggerAdditive(Logger var1, boolean var2) {
      String var3 = var1.getName();
      LoggerConfig var4 = this.getLoggerConfig(var3);
      if (var4.getName().equals(var3)) {
         var4.setAdditive(var2);
      } else {
         LoggerConfig var5 = new LoggerConfig(var3, var4.getLevel(), var2);
         var5.setParent(var4);
         this.loggerConfigs.putIfAbsent(var3, var5);
         this.setParents();
         var1.getContext().updateLoggers();
      }
   }

   public synchronized void removeAppender(String var1) {
      for (LoggerConfig var3 : this.loggerConfigs.values()) {
         var3.removeAppender(var1);
      }

      Appender var4 = var1 != null ? this.appenders.remove(var1) : null;
      if (var4 != null) {
         var4.stop();
      }
   }

   @Override
   public List<CustomLevelConfig> getCustomLevels() {
      return Collections.unmodifiableList(this.customLevels);
   }

   @Override
   public LoggerConfig getLoggerConfig(String var1) {
      LoggerConfig var2 = this.loggerConfigs.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1;

         while ((var3 = NameUtil.getSubName(var3)) != null) {
            var2 = this.loggerConfigs.get(var3);
            if (var2 != null) {
               return var2;
            }
         }

         return this.root;
      }
   }

   @Override
   public LoggerContext getLoggerContext() {
      return this.loggerContext.get();
   }

   @Override
   public LoggerConfig getRootLogger() {
      return this.root;
   }

   @Override
   public Map<String, LoggerConfig> getLoggers() {
      return Collections.unmodifiableMap(this.loggerConfigs);
   }

   public LoggerConfig getLogger(String var1) {
      return this.loggerConfigs.get(var1);
   }

   @Override
   public synchronized void addLogger(String var1, LoggerConfig var2) {
      this.loggerConfigs.putIfAbsent(var1, var2);
      this.setParents();
   }

   @Override
   public synchronized void removeLogger(String var1) {
      this.loggerConfigs.remove(var1);
      this.setParents();
   }

   @Override
   public void createConfiguration(Node var1, LogEvent var2) {
      PluginType var3 = var1.getType();
      if (var3 != null && var3.isDeferChildren()) {
         var1.setObject(this.createPluginObject(var3, var1, var2));
      } else {
         for (Node var5 : var1.getChildren()) {
            this.createConfiguration(var5, var2);
         }

         if (var3 == null) {
            if (var1.getParent() != null) {
               LOGGER.error("Unable to locate plugin for {}", var1.getName());
            }
         } else {
            var1.setObject(this.createPluginObject(var3, var1, var2));
         }
      }
   }

   public Object createPluginObject(PluginType<?> var1, Node var2) {
      if (this.getState().equals(LifeCycle.State.INITIALIZING)) {
         return this.createPluginObject(var1, var2, null);
      } else {
         LOGGER.warn("Plugin Object creation is not allowed after initialization");
         return null;
      }
   }

   private Object createPluginObject(PluginType<?> var1, Node var2, LogEvent var3) {
      Class var4 = var1.getPluginClass();
      if (Map.class.isAssignableFrom(var4)) {
         try {
            return createPluginMap(var2);
         } catch (Exception var7) {
            LOGGER.warn("Unable to create Map for {} of class {}", var1.getElementName(), var4, var7);
         }
      }

      if (Collection.class.isAssignableFrom(var4)) {
         try {
            return createPluginCollection(var2);
         } catch (Exception var6) {
            LOGGER.warn("Unable to create List for {} of class {}", var1.getElementName(), var4, var6);
         }
      }

      return new PluginBuilder(var1).withConfiguration(this).withConfigurationNode(var2).forLogEvent(var3).build();
   }

   private static Map<String, ?> createPluginMap(Node var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Node var3 : var0.getChildren()) {
         Object var4 = var3.getObject();
         var1.put(var3.getName(), var4);
      }

      return var1;
   }

   private static Collection<?> createPluginCollection(Node var0) {
      List var1 = var0.getChildren();
      ArrayList var2 = new ArrayList(var1.size());

      for (Node var4 : var1) {
         Object var5 = var4.getObject();
         var2.add(var5);
      }

      return var2;
   }

   private void setParents() {
      for (Entry var2 : this.loggerConfigs.entrySet()) {
         LoggerConfig var3 = (LoggerConfig)var2.getValue();
         String var4 = (String)var2.getKey();
         if (!var4.isEmpty()) {
            int var5 = var4.lastIndexOf(46);
            if (var5 > 0) {
               var4 = var4.substring(0, var5);
               LoggerConfig var6 = this.getLoggerConfig(var4);
               if (var6 == null) {
                  var6 = this.root;
               }

               var3.setParent(var6);
            } else {
               var3.setParent(this.root);
            }
         }
      }
   }

   protected static byte[] toByteArray(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      byte[] var3 = new byte[16384];

      int var2;
      while ((var2 = var0.read(var3, 0, var3.length)) != -1) {
         var1.write(var3, 0, var2);
      }

      return var1.toByteArray();
   }

   @Override
   public NanoClock getNanoClock() {
      return this.nanoClock;
   }

   @Override
   public void setNanoClock(NanoClock var1) {
      this.nanoClock = Objects.requireNonNull(var1, "nanoClock");
   }
}
