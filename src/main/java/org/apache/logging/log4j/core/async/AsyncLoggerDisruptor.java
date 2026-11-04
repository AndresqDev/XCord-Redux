package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.message.Message;

class AsyncLoggerDisruptor extends AbstractLifeCycle {
   private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
   private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
   private final Object queueFullEnqueueLock = new Object();
   private volatile Disruptor<RingBufferLogEvent> disruptor;
   private String contextName;
   private boolean useThreadLocalTranslator = true;
   private long backgroundThreadId;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;
   private int ringBufferSize;

   AsyncLoggerDisruptor(String var1) {
      this.contextName = var1;
   }

   public String getContextName() {
      return this.contextName;
   }

   public void setContextName(String var1) {
      this.contextName = var1;
   }

   Disruptor<RingBufferLogEvent> getDisruptor() {
      return this.disruptor;
   }

   @Override
   public synchronized void start() {
      if (this.disruptor != null) {
         LOGGER.trace("[{}] AsyncLoggerDisruptor not starting new disruptor for this context, using existing object.", this.contextName);
      } else if (this.isStarting()) {
         LOGGER.trace("[{}] AsyncLoggerDisruptor is already starting.", this.contextName);
      } else {
         this.setStarting();
         LOGGER.trace("[{}] AsyncLoggerDisruptor creating new disruptor for this context.", this.contextName);
         this.ringBufferSize = DisruptorUtil.calculateRingBufferSize("AsyncLogger.RingBufferSize");
         WaitStrategy var1 = DisruptorUtil.createWaitStrategy("AsyncLogger.WaitStrategy");
         Log4jThreadFactory var2 = new Log4jThreadFactory("AsyncLogger[" + this.contextName + "]", true, 5) {
            @Override
            public Thread newThread(Runnable var1) {
               Thread var2 = super.newThread(var1);
               AsyncLoggerDisruptor.this.backgroundThreadId = var2.getId();
               return var2;
            }
         };
         this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
         this.disruptor = new Disruptor(RingBufferLogEvent.FACTORY, this.ringBufferSize, var2, ProducerType.MULTI, var1);
         ExceptionHandler var3 = DisruptorUtil.getAsyncLoggerExceptionHandler();
         this.disruptor.setDefaultExceptionHandler(var3);
         RingBufferLogEventHandler[] var4 = new RingBufferLogEventHandler[]{new RingBufferLogEventHandler()};
         this.disruptor.handleEventsWith(var4);
         LOGGER.debug(
            "[{}] Starting AsyncLogger disruptor for this context with ringbufferSize={}, waitStrategy={}, exceptionHandler={}...",
            this.contextName,
            this.disruptor.getRingBuffer().getBufferSize(),
            var1.getClass().getSimpleName(),
            var3
         );
         this.disruptor.start();
         LOGGER.trace("[{}] AsyncLoggers use a {} translator", this.contextName, this.useThreadLocalTranslator ? "threadlocal" : "vararg");
         super.start();
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      Disruptor var4 = this.getDisruptor();
      if (var4 == null) {
         LOGGER.trace("[{}] AsyncLoggerDisruptor: disruptor for this context already shut down.", this.contextName);
         return true;
      } else {
         this.setStopping();
         LOGGER.debug("[{}] AsyncLoggerDisruptor: shutting down disruptor for this context.", this.contextName);
         this.disruptor = null;

         for (int var5 = 0; hasBacklog(var4) && var5 < 200; var5++) {
            try {
               Thread.sleep(50L);
            } catch (InterruptedException var8) {
            }
         }

         try {
            var4.shutdown(var1, var3);
         } catch (TimeoutException var7) {
            LOGGER.warn("[{}] AsyncLoggerDisruptor: shutdown timed out after {} {}", this.contextName, var1, var3);
            var4.halt();
         }

         LOGGER.trace("[{}] AsyncLoggerDisruptor: disruptor has been shut down.", this.contextName);
         if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
            LOGGER.trace(
               "AsyncLoggerDisruptor: {} discarded {} events.",
               this.asyncQueueFullPolicy,
               DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy)
            );
         }

         this.setStopped();
         return true;
      }
   }

   private static boolean hasBacklog(Disruptor<?> var0) {
      RingBuffer var1 = var0.getRingBuffer();
      return !var1.hasAvailableCapacity(var1.getBufferSize());
   }

   public RingBufferAdmin createRingBufferAdmin(String var1) {
      RingBuffer var2 = this.disruptor == null ? null : this.disruptor.getRingBuffer();
      return RingBufferAdmin.forAsyncLogger(var2, var1);
   }

   EventRoute getEventRoute(Level var1) {
      int var2 = this.remainingDisruptorCapacity();
      return var2 < 0 ? EventRoute.DISCARD : this.asyncQueueFullPolicy.getRoute(this.backgroundThreadId, var1);
   }

   private int remainingDisruptorCapacity() {
      Disruptor var1 = this.disruptor;
      return this.hasLog4jBeenShutDown(var1) ? -1 : (int)var1.getRingBuffer().remainingCapacity();
   }

   private boolean hasLog4jBeenShutDown(Disruptor<RingBufferLogEvent> var1) {
      if (var1 == null) {
         LOGGER.warn("Ignoring log event after log4j was shut down");
         return true;
      } else {
         return false;
      }
   }

   boolean tryPublish(RingBufferLogEventTranslator var1) {
      try {
         return this.disruptor.getRingBuffer().tryPublishEvent(var1);
      } catch (NullPointerException var3) {
         this.logWarningOnNpeFromDisruptorPublish(var1);
         return false;
      }
   }

   void enqueueLogMessageWhenQueueFull(RingBufferLogEventTranslator var1) {
      try {
         if (this.synchronizeEnqueueWhenQueueFull()) {
            synchronized (this.queueFullEnqueueLock) {
               this.disruptor.publishEvent(var1);
            }
         } else {
            this.disruptor.publishEvent(var1);
         }
      } catch (NullPointerException var5) {
         this.logWarningOnNpeFromDisruptorPublish(var1);
      }
   }

   void enqueueLogMessageWhenQueueFull(
      EventTranslatorVararg<RingBufferLogEvent> var1,
      AsyncLogger var2,
      StackTraceElement var3,
      String var4,
      Level var5,
      Marker var6,
      Message var7,
      Throwable var8
   ) {
      try {
         if (this.synchronizeEnqueueWhenQueueFull()) {
            synchronized (this.queueFullEnqueueLock) {
               this.disruptor.getRingBuffer().publishEvent(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8});
            }
         } else {
            this.disruptor.getRingBuffer().publishEvent(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8});
         }
      } catch (NullPointerException var12) {
         this.logWarningOnNpeFromDisruptorPublish(var5, var4, var7, var8);
      }
   }

   private boolean synchronizeEnqueueWhenQueueFull() {
      return DisruptorUtil.ASYNC_LOGGER_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL
         && this.backgroundThreadId != Thread.currentThread().getId()
         && !(Thread.currentThread() instanceof Log4jThread);
   }

   private void logWarningOnNpeFromDisruptorPublish(RingBufferLogEventTranslator var1) {
      this.logWarningOnNpeFromDisruptorPublish(var1.level, var1.loggerName, var1.message, var1.thrown);
   }

   private void logWarningOnNpeFromDisruptorPublish(Level var1, String var2, Message var3, Throwable var4) {
      LOGGER.warn(
         "[{}] Ignoring log event after log4j was shut down: {} [{}] {}{}",
         this.contextName,
         var1,
         var2,
         var3.getFormattedMessage(),
         var4 == null ? "" : Throwables.toStringList(var4)
      );
   }

   public boolean isUseThreadLocals() {
      return this.useThreadLocalTranslator;
   }

   public void setUseThreadLocals(boolean var1) {
      this.useThreadLocalTranslator = var1;
      LOGGER.trace("[{}] AsyncLoggers have been modified to use a {} translator", this.contextName, this.useThreadLocalTranslator ? "threadlocal" : "vararg");
   }
}
