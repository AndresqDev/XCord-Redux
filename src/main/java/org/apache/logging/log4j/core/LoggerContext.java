package org.apache.logging.log4j.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.ThreadContextDataInjector;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.spi.LoggerContextShutdownEnabled;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;
import org.apache.logging.log4j.util.PropertiesUtil;

public class LoggerContext
   extends AbstractLifeCycle
   implements AutoCloseable,
   ConfigurationListener,
   org.apache.logging.log4j.spi.LoggerContext,
   LoggerContextShutdownEnabled,
   Terminable {
   public static final String PROPERTY_CONFIG = "config";
   private static final Configuration NULL_CONFIGURATION;
   private final LoggerRegistry<Logger> loggerRegistry = new LoggerRegistry<>();
   private final CopyOnWriteArrayList<PropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<>();
   private volatile List<LoggerContextShutdownAware> listeners;
   private volatile Configuration configuration = new DefaultConfiguration();
   private static final String EXTERNAL_CONTEXT_KEY = "__EXTERNAL_CONTEXT_KEY__";
   private ConcurrentMap<String, Object> externalMap = new ConcurrentHashMap<>();
   private String contextName;
   private volatile URI configLocation;
   private Cancellable shutdownCallback;
   private final Lock configLock = new ReentrantLock();

   public LoggerContext(String var1) {
      this(var1, null, (URI)null);
   }

   public LoggerContext(String var1, Object var2) {
      this(var1, var2, (URI)null);
   }

   public LoggerContext(String var1, Object var2, URI var3) {
      this.contextName = var1;
      if (var2 == null) {
         this.externalMap.remove("__EXTERNAL_CONTEXT_KEY__");
      } else {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", var2);
      }

      this.configLocation = var3;
      Thread var4 = new Thread(new LoggerContext.ThreadContextDataTask(), "Thread Context Data Task");
      var4.setDaemon(true);
      var4.start();
   }

   public LoggerContext(String var1, Object var2, String var3) {
      this.contextName = var1;
      if (var2 == null) {
         this.externalMap.remove("__EXTERNAL_CONTEXT_KEY__");
      } else {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", var2);
      }

      if (var3 != null) {
         URI var4;
         try {
            var4 = new File(var3).toURI();
         } catch (Exception var6) {
            var4 = null;
         }

         this.configLocation = var4;
      } else {
         this.configLocation = null;
      }

      Thread var7 = new Thread(new LoggerContext.ThreadContextDataTask(), "Thread Context Data Task");
      var7.setDaemon(true);
      var7.start();
   }

   @Override
   public void addShutdownListener(LoggerContextShutdownAware var1) {
      if (this.listeners == null) {
         synchronized (this) {
            if (this.listeners == null) {
               this.listeners = new CopyOnWriteArrayList<>();
            }
         }
      }

      this.listeners.add(var1);
   }

   @Override
   public List<LoggerContextShutdownAware> getListeners() {
      return this.listeners;
   }

   public static LoggerContext getContext() {
      return (LoggerContext)LogManager.getContext();
   }

   public static LoggerContext getContext(boolean var0) {
      return (LoggerContext)LogManager.getContext(var0);
   }

   public static LoggerContext getContext(ClassLoader var0, boolean var1, URI var2) {
      return (LoggerContext)LogManager.getContext(var0, var1, var2);
   }

   @Override
   public void start() {
      LOGGER.debug("Starting LoggerContext[name={}, {}]...", this.getName(), this);
      if (PropertiesUtil.getProperties().getBooleanProperty("log4j.LoggerContext.stacktrace.on.start", false)) {
         LOGGER.debug("Stack trace to locate invoker", (Throwable)(new Exception("Not a real error, showing stack trace to locate invoker")));
      }

      if (this.configLock.tryLock()) {
         try {
            if (this.isInitialized() || this.isStopped()) {
               this.setStarting();
               this.reconfigure();
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      LOGGER.debug("LoggerContext[name={}, {}] started OK.", this.getName(), this);
   }

   public void start(Configuration var1) {
      LOGGER.debug("Starting LoggerContext[name={}, {}] with configuration {}...", this.getName(), this, var1);
      if (this.configLock.tryLock()) {
         try {
            if (this.isInitialized() || this.isStopped()) {
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      this.setConfiguration(var1);
      LOGGER.debug("LoggerContext[name={}, {}] started OK with configuration {}.", this.getName(), this, var1);
   }

   private void setUpShutdownHook() {
      if (this.shutdownCallback == null) {
         LoggerContextFactory var1 = LogManager.getFactory();
         if (var1 instanceof ShutdownCallbackRegistry) {
            LOGGER.debug(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Shutdown hook enabled. Registering a new one.");

            try {
               final long var2 = this.configuration.getShutdownTimeoutMillis();
               this.shutdownCallback = ((ShutdownCallbackRegistry)var1).addShutdownCallback(new Runnable() {
                  @Override
                  public void run() {
                     LoggerContext var1 = LoggerContext.this;
                     AbstractLifeCycle.LOGGER.debug(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Stopping LoggerContext[name={}, {}]", var1.getName(), var1);
                     var1.stop(var2, TimeUnit.MILLISECONDS);
                  }

                  @Override
                  public String toString() {
                     return "Shutdown callback for LoggerContext[name=" + LoggerContext.this.getName() + ']';
                  }
               });
            } catch (IllegalStateException var4) {
               throw new IllegalStateException("Unable to register Log4j shutdown hook because JVM is shutting down.", var4);
            } catch (SecurityException var5) {
               LOGGER.error(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Unable to register shutdown hook due to security restrictions", (Throwable)var5);
            }
         }
      }
   }

   @Override
   public void close() {
      this.stop();
   }

   @Override
   public void terminate() {
      this.stop();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      LOGGER.debug("Stopping LoggerContext[name={}, {}]...", this.getName(), this);
      this.configLock.lock();

      try {
         if (this.isStopped()) {
            return true;
         }

         this.setStopping();

         try {
            Server.unregisterLoggerContext(this.getName());
         } catch (Exception | LinkageError var11) {
            LOGGER.error("Unable to unregister MBeans", (Throwable)var11);
         }

         if (this.shutdownCallback != null) {
            this.shutdownCallback.cancel();
            this.shutdownCallback = null;
         }

         Configuration var4 = this.configuration;
         this.configuration = NULL_CONFIGURATION;
         this.updateLoggers();
         if (var4 instanceof LifeCycle2) {
            ((LifeCycle2)var4).stop(var1, var3);
         } else {
            var4.stop();
         }

         this.externalMap.clear();
         LogManager.getFactory().removeContext(this);
      } finally {
         this.configLock.unlock();
         this.setStopped();
      }

      if (this.listeners != null) {
         for (LoggerContextShutdownAware var5 : this.listeners) {
            try {
               var5.contextShutdown(this);
            } catch (Exception var10) {
            }
         }
      }

      LOGGER.debug("Stopped LoggerContext[name={}, {}] with status {}", this.getName(), this, true);
      return true;
   }

   public String getName() {
      return this.contextName;
   }

   public Logger getRootLogger() {
      return this.getLogger("");
   }

   public void setName(String var1) {
      this.contextName = Objects.requireNonNull(var1);
   }

   @Override
   public Object getObject(String var1) {
      return this.externalMap.get(var1);
   }

   @Override
   public Object putObject(String var1, Object var2) {
      return this.externalMap.put(var1, var2);
   }

   @Override
   public Object putObjectIfAbsent(String var1, Object var2) {
      return this.externalMap.putIfAbsent(var1, var2);
   }

   @Override
   public Object removeObject(String var1) {
      return this.externalMap.remove(var1);
   }

   @Override
   public boolean removeObject(String var1, Object var2) {
      return this.externalMap.remove(var1, var2);
   }

   public void setExternalContext(Object var1) {
      if (var1 != null) {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", var1);
      } else {
         this.externalMap.remove("__EXTERNAL_CONTEXT_KEY__");
      }
   }

   @Override
   public Object getExternalContext() {
      return this.externalMap.get("__EXTERNAL_CONTEXT_KEY__");
   }

   public Logger getLogger(String var1) {
      return this.getLogger(var1, null);
   }

   public Collection<Logger> getLoggers() {
      return this.loggerRegistry.getLoggers();
   }

   public Logger getLogger(String var1, MessageFactory var2) {
      Logger var3 = this.loggerRegistry.getLogger(var1, var2);
      if (var3 != null) {
         AbstractLogger.checkMessageFactory(var3, var2);
         return var3;
      } else {
         var3 = this.newInstance(this, var1, var2);
         this.loggerRegistry.putIfAbsent(var1, var2, var3);
         return this.loggerRegistry.getLogger(var1, var2);
      }
   }

   @Override
   public boolean hasLogger(String var1) {
      return this.loggerRegistry.hasLogger(var1);
   }

   @Override
   public boolean hasLogger(String var1, MessageFactory var2) {
      return this.loggerRegistry.hasLogger(var1, var2);
   }

   @Override
   public boolean hasLogger(String var1, Class<? extends MessageFactory> var2) {
      return this.loggerRegistry.hasLogger(var1, var2);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public void addFilter(Filter var1) {
      this.configuration.addFilter(var1);
   }

   public void removeFilter(Filter var1) {
      this.configuration.removeFilter(var1);
   }

   public Configuration setConfiguration(Configuration var1) {
      if (var1 == null) {
         LOGGER.error("No configuration found for context '{}'.", this.contextName);
         return this.configuration;
      } else {
         this.configLock.lock();

         Configuration var4;
         try {
            Configuration var2 = this.configuration;
            var1.addListener(this);
            ConcurrentMap var3 = var1.getComponent("ContextProperties");

            try {
               var3.computeIfAbsent("hostName", var0 -> NetUtils.getLocalHostname());
            } catch (Exception var10) {
               LOGGER.debug("Ignoring {}, setting hostName to 'unknown'", var10.toString());
               var3.putIfAbsent("hostName", "unknown");
            }

            var3.putIfAbsent("contextName", this.contextName);
            var1.start();
            this.configuration = var1;
            this.updateLoggers();
            if (var2 != null) {
               var2.removeListener(this);
               var2.stop();
            }

            this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", var2, var1));

            try {
               Server.reregisterMBeansAfterReconfigure();
            } catch (Exception | LinkageError var9) {
               LOGGER.error("Could not reconfigure JMX", (Throwable)var9);
            }

            Log4jLogEvent.setNanoClock(this.configuration.getNanoClock());
            var4 = var2;
         } finally {
            this.configLock.unlock();
         }

         return var4;
      }
   }

   private void firePropertyChangeEvent(PropertyChangeEvent var1) {
      for (PropertyChangeListener var3 : this.propertyChangeListeners) {
         var3.propertyChange(var1);
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeListeners.add(Objects.requireNonNull(var1, "listener"));
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.propertyChangeListeners.remove(var1);
   }

   public URI getConfigLocation() {
      return this.configLocation;
   }

   public void setConfigLocation(URI var1) {
      this.configLocation = var1;
      this.reconfigure(var1);
   }

   private void reconfigure(URI var1) {
      Object var2 = this.externalMap.get("__EXTERNAL_CONTEXT_KEY__");
      ClassLoader var3 = ClassLoader.class.isInstance(var2) ? (ClassLoader)var2 : null;
      LOGGER.debug("Reconfiguration started for context[name={}] at URI {} ({}) with optional ClassLoader: {}", this.contextName, var1, this, var3);
      Configuration var4 = ConfigurationFactory.getInstance().getConfiguration(this, this.contextName, var1, var3);
      if (var4 == null) {
         LOGGER.error("Reconfiguration failed: No configuration found for '{}' at '{}' in '{}'", this.contextName, var1, var3);
      } else {
         this.setConfiguration(var4);
         String var5 = this.configuration == null ? "?" : String.valueOf(this.configuration.getConfigurationSource());
         LOGGER.debug("Reconfiguration complete for context[name={}] at URI {} ({}) with optional ClassLoader: {}", this.contextName, var5, this, var3);
      }
   }

   public void reconfigure() {
      this.reconfigure(this.configLocation);
   }

   public void reconfigure(Configuration var1) {
      this.setConfiguration(var1);
      ConfigurationSource var2 = var1.getConfigurationSource();
      if (var2 != null) {
         URI var3 = var2.getURI();
         if (var3 != null) {
            this.configLocation = var3;
         }
      }
   }

   public void updateLoggers() {
      this.updateLoggers(this.configuration);
   }

   public void updateLoggers(Configuration var1) {
      Configuration var2 = this.configuration;

      for (Logger var4 : this.loggerRegistry.getLoggers()) {
         var4.updateConfiguration(var1);
      }

      this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", var2, var1));
   }

   @Override
   public synchronized void onChange(Reconfigurable var1) {
      long var2 = System.currentTimeMillis();
      LOGGER.debug("Reconfiguration started for context {} ({})", this.contextName, this);
      this.initApiModule();
      Configuration var4 = var1.reconfigure();
      if (var4 != null) {
         this.setConfiguration(var4);
         LOGGER.debug("Reconfiguration completed for {} ({}) in {} milliseconds.", this.contextName, this, System.currentTimeMillis() - var2);
      } else {
         LOGGER.debug("Reconfiguration failed for {} ({}) in {} milliseconds.", this.contextName, this, System.currentTimeMillis() - var2);
      }
   }

   private void initApiModule() {
      ThreadContextMapFactory.init();
   }

   protected Logger newInstance(LoggerContext var1, String var2, MessageFactory var3) {
      return new Logger(var1, var2, var3);
   }

   static {
      try {
         Loader.loadClass(ExecutorServices.class.getName());
      } catch (Exception var1) {
         LOGGER.error("Failed to preload ExecutorServices class.", (Throwable)var1);
      }

      NULL_CONFIGURATION = new NullConfiguration();
   }

   private class ThreadContextDataTask implements Runnable {
      private ThreadContextDataTask() {
      }

      @Override
      public void run() {
         AbstractLifeCycle.LOGGER.debug("Initializing Thread Context Data Service Providers");
         ThreadContextDataInjector.initServiceProviders();
         AbstractLifeCycle.LOGGER.debug("Thread Context Data Service Provider initialization complete");
      }
   }
}
