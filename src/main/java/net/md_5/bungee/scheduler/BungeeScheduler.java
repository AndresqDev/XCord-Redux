package net.md_5.bungee.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class BungeeScheduler implements TaskScheduler {
   private final Object lock = new Object();
   private final AtomicInteger taskCounter = new AtomicInteger();
   private final TIntObjectMap<BungeeTask> tasks = TCollections.synchronizedMap(new TIntObjectHashMap());
   private final Multimap<Plugin, BungeeTask> tasksByPlugin = Multimaps.synchronizedMultimap(HashMultimap.create());
   private final TaskScheduler.Unsafe unsafe = new TaskScheduler.Unsafe() {
      @Override
      public ExecutorService getExecutorService(Plugin var1) {
         return var1.getExecutorService();
      }
   };

   @Override
   public void cancel(int var1) {
      BungeeTask var2 = (BungeeTask)this.tasks.get(var1);
      Preconditions.checkArgument(var2 != null, "No task with id %s", var1);
      var2.cancel();
   }

   void cancel0(BungeeTask var1) {
      synchronized (this.lock) {
         this.tasks.remove(var1.getId());
         this.tasksByPlugin.values().remove(var1);
      }
   }

   @Override
   public void cancel(ScheduledTask var1) {
      var1.cancel();
   }

   @Override
   public int cancel(Plugin var1) {
      HashSet var2 = new HashSet();
      synchronized (this.lock) {
         for (ScheduledTask var5 : this.tasksByPlugin.get(var1)) {
            var2.add(var5);
         }
      }

      for (ScheduledTask var9 : var2) {
         this.cancel(var9);
      }

      return var2.size();
   }

   @Override
   public ScheduledTask runAsync(Plugin var1, Runnable var2) {
      return this.schedule(var1, var2, 0L, TimeUnit.MILLISECONDS);
   }

   @Override
   public ScheduledTask schedule(Plugin var1, Runnable var2, long var3, TimeUnit var5) {
      return this.schedule(var1, var2, var3, 0L, var5);
   }

   @Override
   public ScheduledTask schedule(Plugin var1, Runnable var2, long var3, long var5, TimeUnit var7) {
      Preconditions.checkNotNull(var1, "owner");
      Preconditions.checkNotNull(var2, "task");
      BungeeTask var8 = new BungeeTask(this, this.taskCounter.getAndIncrement(), var1, var2, var3, var5, var7);
      synchronized (this.lock) {
         this.tasks.put(var8.getId(), var8);
         this.tasksByPlugin.put(var1, var8);
      }

      var1.getExecutorService().execute(var8);
      return var8;
   }

   @Override
   public TaskScheduler.Unsafe unsafe() {
      return this.unsafe;
   }
}
