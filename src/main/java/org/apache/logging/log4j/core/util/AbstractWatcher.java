package org.apache.logging.log4j.core.util;

import java.util.List;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.Reconfigurable;

public abstract class AbstractWatcher implements Watcher {
   private final Reconfigurable reconfigurable;
   private final List<ConfigurationListener> configurationListeners;
   private final Log4jThreadFactory threadFactory;
   private final Configuration configuration;
   private Source source;

   public AbstractWatcher(Configuration var1, Reconfigurable var2, List<ConfigurationListener> var3) {
      this.configuration = var1;
      this.reconfigurable = var2;
      this.configurationListeners = var3;
      this.threadFactory = var3 != null ? Log4jThreadFactory.createDaemonThreadFactory("ConfiguratonFileWatcher") : null;
   }

   @Override
   public List<ConfigurationListener> getListeners() {
      return this.configurationListeners;
   }

   @Override
   public void modified() {
      for (ConfigurationListener var2 : this.configurationListeners) {
         Thread var3 = this.threadFactory.newThread(new AbstractWatcher.ReconfigurationRunnable(var2, this.reconfigurable));
         var3.start();
      }
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   @Override
   public abstract long getLastModified();

   @Override
   public abstract boolean isModified();

   @Override
   public void watching(Source var1) {
      this.source = var1;
   }

   @Override
   public Source getSource() {
      return this.source;
   }

   public static class ReconfigurationRunnable implements Runnable {
      private final ConfigurationListener configurationListener;
      private final Reconfigurable reconfigurable;

      public ReconfigurationRunnable(ConfigurationListener var1, Reconfigurable var2) {
         this.configurationListener = var1;
         this.reconfigurable = var2;
      }

      @Override
      public void run() {
         this.configurationListener.onChange(this.reconfigurable);
      }
   }
}
