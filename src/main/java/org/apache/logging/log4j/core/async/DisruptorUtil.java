package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

final class DisruptorUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int RINGBUFFER_MIN_SIZE = 128;
   private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
   private static final int RINGBUFFER_NO_GC_DEFAULT_SIZE = 4096;
   static final boolean ASYNC_LOGGER_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
      .getBooleanProperty("AsyncLogger.SynchronizeEnqueueWhenQueueFull", true);
   static final boolean ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
      .getBooleanProperty("AsyncLoggerConfig.SynchronizeEnqueueWhenQueueFull", true);

   private DisruptorUtil() {
   }

   static WaitStrategy createWaitStrategy(String var0) {
      String var1 = PropertiesUtil.getProperties().getStringProperty(var0, "Timeout");
      LOGGER.trace("property {}={}", var0, var1);
      String var2 = Strings.toRootUpperCase(var1);
      long var3 = parseAdditionalLongProperty(var0, "Timeout", 10L);
      switch (var2) {
         case "SLEEP":
            long var7 = parseAdditionalLongProperty(var0, "SleepTimeNs", 100L);
            String var9 = getFullPropertyKey(var0, "Retries");
            int var10 = PropertiesUtil.getProperties().getIntegerProperty(var9, 200);
            return new SleepingWaitStrategy(var10, var7);
         case "YIELD":
            return new YieldingWaitStrategy();
         case "BLOCK":
            return new BlockingWaitStrategy();
         case "BUSYSPIN":
            return new BusySpinWaitStrategy();
         case "TIMEOUT":
            return new TimeoutBlockingWaitStrategy(var3, TimeUnit.MILLISECONDS);
         default:
            return new TimeoutBlockingWaitStrategy(var3, TimeUnit.MILLISECONDS);
      }
   }

   private static String getFullPropertyKey(String var0, String var1) {
      return var0.startsWith("AsyncLogger.") ? "AsyncLogger." + var1 : "AsyncLoggerConfig." + var1;
   }

   private static long parseAdditionalLongProperty(String var0, String var1, long var2) {
      String var4 = getFullPropertyKey(var0, var1);
      return PropertiesUtil.getProperties().getLongProperty(var4, var2);
   }

   static int calculateRingBufferSize(String var0) {
      int var1 = Constants.ENABLE_THREADLOCALS ? 4096 : 262144;
      String var2 = PropertiesUtil.getProperties().getStringProperty(var0, String.valueOf(var1));

      try {
         int var3 = Integer.parseInt(var2);
         if (var3 < 128) {
            var3 = 128;
            LOGGER.warn("Invalid RingBufferSize {}, using minimum size {}.", var2, 128);
         }

         var1 = var3;
      } catch (Exception var4) {
         LOGGER.warn("Invalid RingBufferSize {}, using default size {}.", var2, var1);
      }

      return Integers.ceilingNextPowerOfTwo(var1);
   }

   static ExceptionHandler<RingBufferLogEvent> getAsyncLoggerExceptionHandler() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLogger.ExceptionHandler");
      if (var0 == null) {
         return new AsyncLoggerDefaultExceptionHandler();
      } else {
         try {
            Class var1 = Loader.loadClass(var0);
            return (ExceptionHandler<RingBufferLogEvent>)var1.newInstance();
         } catch (Exception var2) {
            LOGGER.debug("Invalid AsyncLogger.ExceptionHandler value: error creating {}: ", var0, var2);
            return new AsyncLoggerDefaultExceptionHandler();
         }
      }
   }

   static ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> getAsyncLoggerConfigExceptionHandler() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLoggerConfig.ExceptionHandler");
      if (var0 == null) {
         return new AsyncLoggerConfigDefaultExceptionHandler();
      } else {
         try {
            Class var1 = Loader.loadClass(var0);
            return (ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper>)var1.newInstance();
         } catch (Exception var2) {
            LOGGER.debug("Invalid AsyncLoggerConfig.ExceptionHandler value: error creating {}: ", var0, var2);
            return new AsyncLoggerConfigDefaultExceptionHandler();
         }
      }
   }

   public static long getExecutorThreadId(ExecutorService var0) {
      Future var1 = var0.submit(() -> Thread.currentThread().getId());

      try {
         return (Long)var1.get();
      } catch (Exception var4) {
         String var3 = "Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.";
         throw new IllegalStateException("Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.", var4);
      }
   }
}
