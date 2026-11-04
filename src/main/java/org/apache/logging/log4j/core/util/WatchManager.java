package org.apache.logging.log4j.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.config.ConfigurationFileWatcher;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public class WatchManager extends AbstractLifeCycle {
   private static Logger logger = StatusLogger.getLogger();
   private final ConcurrentMap<Source, WatchManager.ConfigurationMonitor> watchers = new ConcurrentHashMap<>();
   private int intervalSeconds = 0;
   private ScheduledFuture<?> future;
   private final ConfigurationScheduler scheduler;
   private final List<WatchEventService> eventServiceList;
   private final UUID id = WatchManager.LocalUUID.get();

   public WatchManager(ConfigurationScheduler var1) {
      this.scheduler = Objects.requireNonNull(var1, "scheduler");
      this.eventServiceList = this.getEventServices();
   }

   public void checkFiles() {
      new WatchManager.WatchRunnable().run();
   }

   public Map<Source, Watcher> getConfigurationWatchers() {
      HashMap var1 = new HashMap(this.watchers.size());

      for (Entry var3 : this.watchers.entrySet()) {
         var1.put(var3.getKey(), ((WatchManager.ConfigurationMonitor)var3.getValue()).getWatcher());
      }

      return var1;
   }

   private List<WatchEventService> getEventServices() {
      ArrayList var1 = new ArrayList();

      for (ClassLoader var5 : LoaderUtil.getClassLoaders()) {
         try {
            for (WatchEventService var8 : ServiceLoader.load(WatchEventService.class, var5)) {
               var1.add(var8);
            }
         } catch (Throwable var9) {
            LOGGER.debug("Unable to retrieve WatchEventService from ClassLoader {}", var5, var9);
         }
      }

      return var1;
   }

   public UUID getId() {
      return this.id;
   }

   public int getIntervalSeconds() {
      return this.intervalSeconds;
   }

   @Deprecated
   public Map<File, FileWatcher> getWatchers() {
      HashMap var1 = new HashMap(this.watchers.size());

      for (Entry var3 : this.watchers.entrySet()) {
         if (((WatchManager.ConfigurationMonitor)var3.getValue()).getWatcher() instanceof ConfigurationFileWatcher) {
            var1.put(((Source)var3.getKey()).getFile(), (FileWatcher)((WatchManager.ConfigurationMonitor)var3.getValue()).getWatcher());
         } else {
            var1.put(((Source)var3.getKey()).getFile(), new WrappedFileWatcher((FileWatcher)((WatchManager.ConfigurationMonitor)var3.getValue()).getWatcher()));
         }
      }

      return var1;
   }

   public boolean hasEventListeners() {
      return this.eventServiceList.size() > 0;
   }

   private String millisToString(long var1) {
      return new Date(var1).toString();
   }

   public void reset() {
      logger.debug("Resetting {}", this);

      for (Source var2 : this.watchers.keySet()) {
         this.reset(var2);
      }
   }

   public void reset(File var1) {
      if (var1 != null) {
         Source var2 = new Source(var1);
         this.reset(var2);
      }
   }

   public void reset(Source var1) {
      if (var1 != null) {
         WatchManager.ConfigurationMonitor var2 = this.watchers.get(var1);
         if (var2 != null) {
            Watcher var3 = var2.getWatcher();
            if (var3.isModified()) {
               long var4 = var3.getLastModified();
               if (logger.isDebugEnabled()) {
                  logger.debug(
                     "Resetting file monitor for '{}' from {} ({}) to {} ({})",
                     var1.getLocation(),
                     this.millisToString(var2.lastModifiedMillis),
                     var2.lastModifiedMillis,
                     this.millisToString(var4),
                     var4
                  );
               }

               var2.setLastModifiedMillis(var4);
            }
         }
      }
   }

   public void setIntervalSeconds(int var1) {
      if (!this.isStarted()) {
         if (this.intervalSeconds > 0 && var1 == 0) {
            this.scheduler.decrementScheduledItems();
         } else if (this.intervalSeconds == 0 && var1 > 0) {
            this.scheduler.incrementScheduledItems();
         }

         this.intervalSeconds = var1;
      }
   }

   @Override
   public void start() {
      super.start();
      if (this.intervalSeconds > 0) {
         this.future = this.scheduler
            .scheduleWithFixedDelay(new WatchManager.WatchRunnable(), (long)this.intervalSeconds, (long)this.intervalSeconds, TimeUnit.SECONDS);
      }

      for (WatchEventService var2 : this.eventServiceList) {
         var2.subscribe(this);
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();

      for (WatchEventService var5 : this.eventServiceList) {
         var5.unsubscribe(this);
      }

      boolean var6 = this.stop(this.future);
      this.setStopped();
      return var6;
   }

   @Override
   public String toString() {
      return "WatchManager [intervalSeconds="
         + this.intervalSeconds
         + ", watchers="
         + this.watchers
         + ", scheduler="
         + this.scheduler
         + ", future="
         + this.future
         + "]";
   }

   public void unwatch(Source var1) {
      logger.debug("Unwatching configuration {}", var1);
      this.watchers.remove(var1);
   }

   public void unwatchFile(File var1) {
      Source var2 = new Source(var1);
      this.unwatch(var2);
   }

   public void watch(Source var1, Watcher var2) {
      var2.watching(var1);
      long var3 = var2.getLastModified();
      if (logger.isDebugEnabled()) {
         logger.debug("Watching configuration '{}' for lastModified {} ({})", var1, this.millisToString(var3), var3);
      }

      this.watchers.put(var1, new WatchManager.ConfigurationMonitor(var3, var2));
   }

   public void watchFile(File var1, FileWatcher var2) {
      Object var3;
      if (var2 instanceof Watcher) {
         var3 = (Watcher)var2;
      } else {
         var3 = new WrappedFileWatcher(var2);
      }

      Source var4 = new Source(var1);
      this.watch(var4, (Watcher)var3);
   }

   private final class ConfigurationMonitor {
      private final Watcher watcher;
      private volatile long lastModifiedMillis;

      public ConfigurationMonitor(long var2, Watcher var4) {
         this.watcher = var4;
         this.lastModifiedMillis = var2;
      }

      public Watcher getWatcher() {
         return this.watcher;
      }

      private void setLastModifiedMillis(long var1) {
         this.lastModifiedMillis = var1;
      }

      @Override
      public String toString() {
         return "ConfigurationMonitor [watcher=" + this.watcher + ", lastModifiedMillis=" + this.lastModifiedMillis + "]";
      }
   }

   private static class LocalUUID {
      private static final long LOW_MASK = 4294967295L;
      private static final long MID_MASK = 281470681743360L;
      private static final long HIGH_MASK = 1152640029630136320L;
      private static final int NODE_SIZE = 8;
      private static final int SHIFT_2 = 16;
      private static final int SHIFT_4 = 32;
      private static final int SHIFT_6 = 48;
      private static final int HUNDRED_NANOS_PER_MILLI = 10000;
      private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 122192928000000000L;
      private static final AtomicInteger COUNT = new AtomicInteger(0);
      private static final long TYPE1 = 4096L;
      private static final byte VARIANT = -128;
      private static final int SEQUENCE_MASK = 16383;

      public static UUID get() {
         long var0 = System.currentTimeMillis() * 10000L + 122192928000000000L + (long)(COUNT.incrementAndGet() % 10000);
         long var2 = (var0 & 4294967295L) << 32;
         long var4 = (var0 & 281470681743360L) >> 16;
         long var6 = (var0 & 1152640029630136320L) >> 48;
         long var8 = var2 | var4 | 4096L | var6;
         return new UUID(var8, (long)COUNT.incrementAndGet());
      }
   }

   private final class WatchRunnable implements Runnable {
      private final String SIMPLE_NAME = WatchManager.WatchRunnable.class.getSimpleName();

      private WatchRunnable() {
      }

      @Override
      public void run() {
         WatchManager.logger.trace("{} run triggered.", this.SIMPLE_NAME);

         for (Entry var2 : WatchManager.this.watchers.entrySet()) {
            Source var3 = (Source)var2.getKey();
            WatchManager.ConfigurationMonitor var4 = (WatchManager.ConfigurationMonitor)var2.getValue();
            if (var4.getWatcher().isModified()) {
               long var5 = var4.getWatcher().getLastModified();
               if (WatchManager.logger.isInfoEnabled()) {
                  WatchManager.logger
                     .info(
                        "Source '{}' was modified on {} ({}), previous modification was on {} ({})",
                        var3,
                        WatchManager.this.millisToString(var5),
                        var5,
                        WatchManager.this.millisToString(var4.lastModifiedMillis),
                        var4.lastModifiedMillis
                     );
               }

               var4.lastModifiedMillis = var5;
               var4.getWatcher().modified();
            }
         }

         WatchManager.logger.trace("{} run ended.", this.SIMPLE_NAME);
      }
   }
}
