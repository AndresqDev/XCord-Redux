package io.netty.handler.traffic;

import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalChannelTrafficCounter extends TrafficCounter {
   public GlobalChannelTrafficCounter(GlobalChannelTrafficShapingHandler var1, ScheduledExecutorService var2, String var3, long var4) {
      super(var1, var2, var3, var4);
      ObjectUtil.checkNotNullWithIAE(var2, "executor");
   }

   @Override
   public synchronized void start() {
      if (!this.monitorActive) {
         this.lastTime.set(milliSecondFromNano());
         long var1 = this.checkInterval.get();
         if (var1 > 0L) {
            this.monitorActive = true;
            this.monitor = new GlobalChannelTrafficCounter.MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler, this);
            this.scheduledFuture = this.executor.scheduleAtFixedRate(this.monitor, 0L, var1, TimeUnit.MILLISECONDS);
         }
      }
   }

   @Override
   public synchronized void stop() {
      if (this.monitorActive) {
         this.monitorActive = false;
         this.resetAccounting(milliSecondFromNano());
         this.trafficShapingHandler.doAccounting(this);
         if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
         }
      }
   }

   @Override
   public void resetCumulativeTime() {
      for (GlobalChannelTrafficShapingHandler.PerChannel var2 : ((GlobalChannelTrafficShapingHandler)this.trafficShapingHandler).channelQueues.values()) {
         var2.channelTrafficCounter.resetCumulativeTime();
      }

      super.resetCumulativeTime();
   }

   private static class MixedTrafficMonitoringTask implements Runnable {
      private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;
      private final TrafficCounter counter;

      MixedTrafficMonitoringTask(GlobalChannelTrafficShapingHandler var1, TrafficCounter var2) {
         this.trafficShapingHandler1 = var1;
         this.counter = var2;
      }

      @Override
      public void run() {
         if (this.counter.monitorActive) {
            long var1 = TrafficCounter.milliSecondFromNano();
            this.counter.resetAccounting(var1);

            for (GlobalChannelTrafficShapingHandler.PerChannel var4 : this.trafficShapingHandler1.channelQueues.values()) {
               var4.channelTrafficCounter.resetAccounting(var1);
            }

            this.trafficShapingHandler1.doAccounting(this.counter);
         }
      }
   }
}
