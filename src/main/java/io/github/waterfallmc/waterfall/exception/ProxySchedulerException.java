package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class ProxySchedulerException extends ProxyPluginException {
   private final ScheduledTask task;

   public ProxySchedulerException(String var1, Throwable var2, ScheduledTask var3) {
      super(var1, var2, var3.getOwner());
      this.task = (ScheduledTask)Preconditions.checkNotNull(var3, "task");
   }

   public ProxySchedulerException(Throwable var1, ScheduledTask var2) {
      super(var1, var2.getOwner());
      this.task = (ScheduledTask)Preconditions.checkNotNull(var2, "task");
   }

   protected ProxySchedulerException(String var1, Throwable var2, boolean var3, boolean var4, ScheduledTask var5) {
      super(var1, var2, var3, var4, var5.getOwner());
      this.task = (ScheduledTask)Preconditions.checkNotNull(var5, "task");
   }

   public ScheduledTask getTask() {
      return this.task;
   }
}
