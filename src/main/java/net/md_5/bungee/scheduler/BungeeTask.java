package net.md_5.bungee.scheduler;

import io.github.waterfallmc.waterfall.event.ProxyExceptionEvent;
import io.github.waterfallmc.waterfall.exception.ProxySchedulerException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeTask implements Runnable, ScheduledTask {
   private final BungeeScheduler sched;
   private final int id;
   private final Plugin owner;
   private final Runnable task;
   private final long delay;
   private final long period;
   private final AtomicBoolean running = new AtomicBoolean(true);

   public BungeeTask(BungeeScheduler var1, int var2, Plugin var3, Runnable var4, long var5, long var7, TimeUnit var9) {
      this.sched = var1;
      this.id = var2;
      this.owner = var3;
      this.task = var4;
      this.delay = var9.toMillis(var5);
      this.period = var9.toMillis(var7);
   }

   @Override
   public void cancel() {
      boolean var1 = this.running.getAndSet(false);
      if (var1) {
         this.sched.cancel0(this);
      }
   }

   @Override
   public void run() {
      if (this.delay > 0L) {
         try {
            Thread.sleep(this.delay);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
         }
      }

      while (this.running.get()) {
         try {
            this.task.run();
         } catch (Throwable var4) {
            String var2 = "Task " + this + " encountered an exception";
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, var2, var4);
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyExceptionEvent(new ProxySchedulerException(var2, var4, this)));
         }

         if (this.period <= 0L) {
            break;
         }

         try {
            Thread.sleep(this.period);
         } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
         }
      }

      this.cancel();
   }

   public BungeeScheduler getSched() {
      return this.sched;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public Plugin getOwner() {
      return this.owner;
   }

   @Override
   public Runnable getTask() {
      return this.task;
   }

   public long getDelay() {
      return this.delay;
   }

   public long getPeriod() {
      return this.period;
   }

   public AtomicBoolean getRunning() {
      return this.running;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BungeeTask)) {
         return false;
      } else {
         BungeeTask var2 = (BungeeTask)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getId() != var2.getId()) {
            return false;
         } else if (this.getDelay() != var2.getDelay()) {
            return false;
         } else if (this.getPeriod() != var2.getPeriod()) {
            return false;
         } else {
            BungeeScheduler var3 = this.getSched();
            BungeeScheduler var4 = var2.getSched();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Plugin var5 = this.getOwner();
               Plugin var6 = var2.getOwner();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  Runnable var7 = this.getTask();
                  Runnable var8 = var2.getTask();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     AtomicBoolean var9 = this.getRunning();
                     AtomicBoolean var10 = var2.getRunning();
                     return var9 == null ? var10 == null : var9.equals(var10);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof BungeeTask;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getId();
      long var3 = this.getDelay();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      long var5 = this.getPeriod();
      var2 = var2 * 59 + (int)(var5 >>> 32 ^ var5);
      BungeeScheduler var7 = this.getSched();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      Plugin var8 = this.getOwner();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      Runnable var9 = this.getTask();
      var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
      AtomicBoolean var10 = this.getRunning();
      return var2 * 59 + (var10 == null ? 43 : var10.hashCode());
   }

   @Override
   public String toString() {
      return "BungeeTask(sched="
         + this.getSched()
         + ", id="
         + this.getId()
         + ", owner="
         + this.getOwner()
         + ", task="
         + this.getTask()
         + ", delay="
         + this.getDelay()
         + ", period="
         + this.getPeriod()
         + ", running="
         + this.getRunning()
         + ")";
   }
}
