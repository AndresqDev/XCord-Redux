package net.md_5.bungee.api.scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.plugin.Plugin;

public interface TaskScheduler {
   void cancel(int var1);

   void cancel(ScheduledTask var1);

   int cancel(Plugin var1);

   ScheduledTask runAsync(Plugin var1, Runnable var2);

   ScheduledTask schedule(Plugin var1, Runnable var2, long var3, TimeUnit var5);

   ScheduledTask schedule(Plugin var1, Runnable var2, long var3, long var5, TimeUnit var7);

   TaskScheduler.Unsafe unsafe();

   public interface Unsafe {
      ExecutorService getExecutorService(Plugin var1);
   }
}
