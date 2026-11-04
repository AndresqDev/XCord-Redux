package org.apache.logging.log4j.util;

import java.net.URL;
import java.security.Permission;
import java.util.Collection;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

public class Activator implements BundleActivator, SynchronousBundleListener {
   private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
   private static final Logger LOGGER = StatusLogger.getLogger();
   private boolean lockingProviderUtil;

   private static void checkPermission(Permission var0) {
      if (SECURITY_MANAGER != null) {
         SECURITY_MANAGER.checkPermission(var0);
      }
   }

   private void loadProvider(Bundle var1) {
      if (var1.getState() != 1) {
         try {
            checkPermission(new AdminPermission(var1, "resource"));
            checkPermission(new AdaptPermission(BundleWiring.class.getName(), var1, "adapt"));
            BundleContext var2 = var1.getBundleContext();
            if (var2 == null) {
               LOGGER.debug("Bundle {} has no context (state={}), skipping loading provider", var1.getSymbolicName(), this.toStateString(var1.getState()));
            } else {
               this.loadProvider(var2, (BundleWiring)var1.adapt(BundleWiring.class));
            }
         } catch (SecurityException var3) {
            LOGGER.debug("Cannot access bundle [{}] contents. Ignoring.", var1.getSymbolicName(), var3);
         } catch (Exception var4) {
            LOGGER.warn("Problem checking bundle {} for Log4j 2 provider.", var1.getSymbolicName(), var4);
         }
      }
   }

   private String toStateString(int var1) {
      switch (var1) {
         case 1:
            return "UNINSTALLED";
         case 2:
            return "INSTALLED";
         case 4:
            return "RESOLVED";
         case 8:
            return "STARTING";
         case 16:
            return "STOPPING";
         case 32:
            return "ACTIVE";
         default:
            return Integer.toString(var1);
      }
   }

   private void loadProvider(BundleContext var1, BundleWiring var2) {
      String var3 = "(APIVersion>=2.6.0)";

      try {
         Collection var4 = var1.getServiceReferences(Provider.class, "(APIVersion>=2.6.0)");
         Provider var5 = null;

         for (ServiceReference var7 : var4) {
            Provider var8 = (Provider)var1.getService(var7);
            if (var5 == null || var8.getPriority() > var5.getPriority()) {
               var5 = var8;
            }
         }

         if (var5 != null) {
            ProviderUtil.addProvider(var5);
         }
      } catch (InvalidSyntaxException var9) {
         LOGGER.error("Invalid service filter: (APIVersion>=2.6.0)", (Throwable)var9);
      }

      for (URL var12 : var2.findEntries("META-INF", "log4j-provider.properties", 0)) {
         ProviderUtil.loadProvider(var12, var2.getClassLoader());
      }
   }

   public void start(BundleContext var1) throws Exception {
      ProviderUtil.STARTUP_LOCK.lock();
      this.lockingProviderUtil = true;
      BundleWiring var2 = (BundleWiring)var1.getBundle().adapt(BundleWiring.class);

      for (BundleWire var5 : var2.getRequiredWires(LoggerContextFactory.class.getName())) {
         this.loadProvider(var1, var5.getProviderWiring());
      }

      var1.addBundleListener(this);
      Bundle[] var9 = var1.getBundles();

      for (Bundle var8 : var9) {
         this.loadProvider(var8);
      }

      this.unlockIfReady();
   }

   private void unlockIfReady() {
      if (this.lockingProviderUtil && !ProviderUtil.PROVIDERS.isEmpty()) {
         ProviderUtil.STARTUP_LOCK.unlock();
         this.lockingProviderUtil = false;
      }
   }

   public void stop(BundleContext var1) throws Exception {
      var1.removeBundleListener(this);
      this.unlockIfReady();
   }

   public void bundleChanged(BundleEvent var1) {
      switch (var1.getType()) {
         case 2:
            this.loadProvider(var1.getBundle());
            this.unlockIfReady();
      }
   }
}
