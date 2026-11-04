package org.apache.logging.log4j.core.config;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.util.CronExpression;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.status.StatusLogger;

public class ConfigurationScheduler extends AbstractLifeCycle {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String SIMPLE_NAME = "Log4j2 " + ConfigurationScheduler.class.getSimpleName();
   private static final int MAX_SCHEDULED_ITEMS = 5;
   private volatile ScheduledExecutorService executorService;
   private int scheduledItems = 0;
   private final String name;

   public ConfigurationScheduler() {
      this(SIMPLE_NAME);
   }

   public ConfigurationScheduler(String var1) {
      this.name = var1;
   }

   @Override
   public void start() {
      super.start();
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      if (this.isExecutorServiceSet()) {
         LOGGER.debug("{} shutting down threads in {}", this.name, this.getExecutorService());
         this.executorService.shutdown();

         try {
            this.executorService.awaitTermination(var1, var3);
         } catch (InterruptedException var7) {
            this.executorService.shutdownNow();

            try {
               this.executorService.awaitTermination(var1, var3);
            } catch (InterruptedException var6) {
               LOGGER.warn("{} stopped but some scheduled services may not have completed.", this.name);
            }

            Thread.currentThread().interrupt();
         }
      }

      this.setStopped();
      return true;
   }

   public boolean isExecutorServiceSet() {
      return this.executorService != null;
   }

   public void incrementScheduledItems() {
      if (this.isExecutorServiceSet()) {
         LOGGER.error("{} attempted to increment scheduled items after start", this.name);
      } else {
         this.scheduledItems++;
      }
   }

   public void decrementScheduledItems() {
      if (!this.isStarted() && this.scheduledItems > 0) {
         this.scheduledItems--;
      }
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      return this.getExecutorService().schedule(var1, var2, var4);
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      return this.getExecutorService().schedule(var1, var2, var4);
   }

   public CronScheduledFuture<?> scheduleWithCron(CronExpression var1, Runnable var2) {
      return this.scheduleWithCron(var1, new Date(), var2);
   }

   public CronScheduledFuture<?> scheduleWithCron(CronExpression var1, Date var2, Runnable var3) {
      Date var4 = var1.getNextValidTimeAfter(var2 == null ? new Date() : var2);
      ConfigurationScheduler.CronRunnable var5 = new ConfigurationScheduler.CronRunnable(var3, var1);
      ScheduledFuture var6 = this.schedule(var5, this.nextFireInterval(var4), TimeUnit.MILLISECONDS);
      CronScheduledFuture var7 = new CronScheduledFuture(var6, var4);
      var5.setScheduledFuture(var7);
      LOGGER.debug("{} scheduled cron expression {} to fire at {}", this.name, var1.getCronExpression(), var4);
      return var7;
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.getExecutorService().scheduleAtFixedRate(var1, var2, var4, var6);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      return this.getExecutorService().scheduleWithFixedDelay(var1, var2, var4, var6);
   }

   public long nextFireInterval(Date var1) {
      return var1.getTime() - new Date().getTime();
   }

   private ScheduledExecutorService getExecutorService() {
      if (this.executorService == null) {
         synchronized (this) {
            if (this.executorService == null) {
               if (this.scheduledItems > 0) {
                  LOGGER.debug("{} starting {} threads", this.name, this.scheduledItems);
                  this.scheduledItems = Math.min(this.scheduledItems, 5);
                  ScheduledThreadPoolExecutor var2 = new ScheduledThreadPoolExecutor(
                     this.scheduledItems, Log4jThreadFactory.createDaemonThreadFactory("Scheduled")
                  );
                  var2.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
                  var2.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
                  this.executorService = var2;
               } else {
                  LOGGER.debug("{}: No scheduled items", this.name);
               }
            }
         }
      }

      return this.executorService;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("ConfigurationScheduler [name=");
      var1.append(this.name);
      var1.append(", [");
      if (this.executorService != null) {
         BlockingQueue var2 = ((ScheduledThreadPoolExecutor)this.executorService).getQueue();
         boolean var3 = true;

         for (Runnable var5 : var2) {
            if (!var3) {
               var1.append(", ");
            }

            var1.append(var5.toString());
            var3 = false;
         }
      }

      var1.append("]");
      return var1.toString();
   }

   public class CronRunnable implements Runnable {
      private final CronExpression cronExpression;
      private final Runnable runnable;
      private CronScheduledFuture<?> scheduledFuture;

      public CronRunnable(Runnable var2, CronExpression var3) {
         this.cronExpression = var3;
         this.runnable = var2;
      }

      public void setScheduledFuture(CronScheduledFuture<?> var1) {
         this.scheduledFuture = var1;
      }

      @Override
      public void run() {
         try {
            long var1 = this.scheduledFuture.getFireTime().getTime() - System.currentTimeMillis();
            if (var1 > 0L) {
               ConfigurationScheduler.LOGGER.debug("{} Cron thread woke up {} millis early. Sleeping", ConfigurationScheduler.this.name, var1);

               try {
                  Thread.sleep(var1);
               } catch (InterruptedException var10) {
               }
            }

            this.runnable.run();
         } catch (Throwable var11) {
            ConfigurationScheduler.LOGGER.error("{} caught error running command", ConfigurationScheduler.this.name, var11);
         } finally {
            Date var5 = this.cronExpression.getNextValidTimeAfter(new Date());
            ScheduledFuture var6 = ConfigurationScheduler.this.schedule(this, ConfigurationScheduler.this.nextFireInterval(var5), TimeUnit.MILLISECONDS);
            ConfigurationScheduler.LOGGER
               .debug("{} Cron expression {} scheduled to fire again at {}", ConfigurationScheduler.this.name, this.cronExpression.getCronExpression(), var5);
            this.scheduledFuture.reset(var6, var5);
         }
      }

      @Override
      public String toString() {
         return "CronRunnable{" + this.cronExpression.getCronExpression() + " - " + this.scheduledFuture.getFireTime();
      }
   }
}
