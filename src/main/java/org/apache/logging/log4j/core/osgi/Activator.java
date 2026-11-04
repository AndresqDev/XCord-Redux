package org.apache.logging.log4j.core.osgi;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;
import org.apache.logging.log4j.core.impl.Log4jProvider;
import org.apache.logging.log4j.core.impl.ThreadContextDataInjector;
import org.apache.logging.log4j.core.impl.ThreadContextDataProvider;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWiring;

public final class Activator implements BundleActivator, SynchronousBundleListener {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final AtomicReference<BundleContext> contextRef = new AtomicReference<>();
   ServiceRegistration provideRegistration = null;
   ServiceRegistration contextDataRegistration = null;

   public void start(BundleContext var1) throws Exception {
      Log4jProvider var2 = new Log4jProvider();
      Hashtable var3 = new Hashtable();
      var3.put("APIVersion", "2.60");
      ThreadContextDataProvider var4 = new ThreadContextDataProvider();
      this.provideRegistration = var1.registerService(Provider.class.getName(), var2, var3);
      this.contextDataRegistration = var1.registerService(ContextDataProvider.class.getName(), var4, null);
      loadContextProviders(var1);
      if (PropertiesUtil.getProperties().getStringProperty("Log4jContextSelector") == null) {
         System.setProperty("Log4jContextSelector", BundleContextSelector.class.getName());
      }

      if (this.contextRef.compareAndSet(null, var1)) {
         var1.addBundleListener(this);
         scanInstalledBundlesForPlugins(var1);
      }
   }

   private static void scanInstalledBundlesForPlugins(BundleContext var0) {
      Bundle[] var1 = var0.getBundles();

      for (Bundle var5 : var1) {
         scanBundleForPlugins(var5);
      }
   }

   private static void scanBundleForPlugins(Bundle var0) {
      long var1 = var0.getBundleId();
      if (var0.getState() == 32 && var1 != 0L) {
         LOGGER.trace("Scanning bundle [{}, id=%d] for plugins.", var0.getSymbolicName(), var1);
         PluginRegistry.getInstance().loadFromBundle(var1, ((BundleWiring)var0.adapt(BundleWiring.class)).getClassLoader());
      }
   }

   private static void loadContextProviders(BundleContext var0) {
      try {
         for (ServiceReference var3 : var0.getServiceReferences(ContextDataProvider.class, null)) {
            ContextDataProvider var4 = (ContextDataProvider)var0.getService(var3);
            ThreadContextDataInjector.contextDataProviders.add(var4);
         }
      } catch (InvalidSyntaxException var5) {
         LOGGER.error("Error accessing context data provider", (Throwable)var5);
      }
   }

   private static void stopBundlePlugins(Bundle var0) {
      LOGGER.trace("Stopping bundle [{}] plugins.", var0.getSymbolicName());
      PluginRegistry.getInstance().clearBundlePlugins(var0.getBundleId());
   }

   public void stop(BundleContext var1) throws Exception {
      this.provideRegistration.unregister();
      this.contextDataRegistration.unregister();
      this.contextRef.compareAndSet(var1, null);
      LogManager.shutdown();
   }

   public void bundleChanged(BundleEvent var1) {
      switch (var1.getType()) {
         case 2:
            scanBundleForPlugins(var1.getBundle());
            break;
         case 256:
            stopBundlePlugins(var1.getBundle());
      }
   }
}
