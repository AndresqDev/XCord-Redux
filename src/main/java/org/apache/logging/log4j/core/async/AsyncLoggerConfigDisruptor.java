package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.util.Log4jThreadFactory;
import org.apache.logging.log4j.core.util.Throwables;
import org.apache.logging.log4j.message.ReusableMessage;

public class AsyncLoggerConfigDisruptor extends AbstractLifeCycle implements AsyncLoggerConfigDelegate {
   private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;
   private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
   private static final EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> FACTORY = AsyncLoggerConfigDisruptor.Log4jEventWrapper::new;
   private static final EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> MUTABLE_FACTORY = () -> new AsyncLoggerConfigDisruptor.Log4jEventWrapper(
         new MutableLogEvent()
      );
   private static final EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> TRANSLATOR = (var0, var1, var3, var4) -> {
      var0.event = var3;
      var0.loggerConfig = var4;
   };
   private static final EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> MUTABLE_TRANSLATOR = (var0, var1, var3, var4) -> {
      ((MutableLogEvent)var0.event).initFrom(var3);
      var0.loggerConfig = var4;
   };
   private int ringBufferSize;
   private AsyncQueueFullPolicy asyncQueueFullPolicy;
   private Boolean mutable = Boolean.FALSE;
   private volatile Disruptor<AsyncLoggerConfigDisruptor.Log4jEventWrapper> disruptor;
   private long backgroundThreadId;
   private EventFactory<AsyncLoggerConfigDisruptor.Log4jEventWrapper> factory;
   private EventTranslatorTwoArg<AsyncLoggerConfigDisruptor.Log4jEventWrapper, LogEvent, AsyncLoggerConfig> translator;
   private volatile boolean alreadyLoggedWarning;
   private final Object queueFullEnqueueLock = new Object();

   @Override
   public void setLogEventFactory(LogEventFactory var1) {
      this.mutable = this.mutable || var1 instanceof ReusableLogEventFactory;
   }

   @Override
   public synchronized void start() {
      if (this.disruptor != null) {
         LOGGER.trace("AsyncLoggerConfigDisruptor not starting new disruptor for this configuration, using existing object.");
      } else {
         LOGGER.trace("AsyncLoggerConfigDisruptor creating new disruptor for this configuration.");
         this.ringBufferSize = DisruptorUtil.calculateRingBufferSize("AsyncLoggerConfig.RingBufferSize");
         WaitStrategy var1 = DisruptorUtil.createWaitStrategy("AsyncLoggerConfig.WaitStrategy");
         Log4jThreadFactory var2 = new Log4jThreadFactory("AsyncLoggerConfig", true, 5) {
            @Override
            public Thread newThread(Runnable var1) {
               Thread var2 = super.newThread(var1);
               AsyncLoggerConfigDisruptor.this.backgroundThreadId = var2.getId();
               return var2;
            }
         };
         this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
         this.translator = this.mutable ? MUTABLE_TRANSLATOR : TRANSLATOR;
         this.factory = this.mutable ? MUTABLE_FACTORY : FACTORY;
         this.disruptor = new Disruptor(this.factory, this.ringBufferSize, var2, ProducerType.MULTI, var1);
         ExceptionHandler var3 = DisruptorUtil.getAsyncLoggerConfigExceptionHandler();
         this.disruptor.setDefaultExceptionHandler(var3);
         AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler[] var4 = new AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler[]{
            new AsyncLoggerConfigDisruptor.Log4jEventWrapperHandler()
         };
         this.disruptor.handleEventsWith(var4);
         LOGGER.debug(
            "Starting AsyncLoggerConfig disruptor for this configuration with ringbufferSize={}, waitStrategy={}, exceptionHandler={}...",
            this.disruptor.getRingBuffer().getBufferSize(),
            var1.getClass().getSimpleName(),
            var3
         );
         this.disruptor.start();
         super.start();
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      Disruptor var4 = this.disruptor;
      if (var4 == null) {
         LOGGER.trace("AsyncLoggerConfigDisruptor: disruptor for this configuration already shut down.");
         return true;
      } else {
         this.setStopping();
         LOGGER.trace("AsyncLoggerConfigDisruptor: shutting down disruptor for this configuration.");
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
            LOGGER.warn("AsyncLoggerConfigDisruptor: shutdown timed out after {} {}", var1, var3);
            var4.halt();
         }

         LOGGER.trace("AsyncLoggerConfigDisruptor: disruptor has been shut down.");
         if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
            LOGGER.trace(
               "AsyncLoggerConfigDisruptor: {} discarded {} events.",
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

   @Override
   public EventRoute getEventRoute(Level var1) {
      int var2 = this.remainingDisruptorCapacity();
      return var2 < 0 ? EventRoute.DISCARD : this.asyncQueueFullPolicy.getRoute(this.backgroundThreadId, var1);
   }

   private int remainingDisruptorCapacity() {
      Disruptor var1 = this.disruptor;
      return this.hasLog4jBeenShutDown(var1) ? -1 : (int)var1.getRingBuffer().remainingCapacity();
   }

   private boolean hasLog4jBeenShutDown(Disruptor<AsyncLoggerConfigDisruptor.Log4jEventWrapper> var1) {
      if (var1 == null) {
         LOGGER.warn("Ignoring log event after log4j was shut down");
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void enqueueEvent(LogEvent var1, AsyncLoggerConfig var2) {
      try {
         LogEvent var3 = this.prepareEvent(var1);
         this.enqueue(var3, var2);
      } catch (NullPointerException var4) {
         LOGGER.warn(
            "Ignoring log event after log4j was shut down: {} [{}] {}",
            var1.getLevel(),
            var1.getLoggerName(),
            var1.getMessage().getFormattedMessage() + (var1.getThrown() == null ? "" : Throwables.toStringList(var1.getThrown()))
         );
      }
   }

   private LogEvent prepareEvent(LogEvent var1) {
      Object var2 = this.ensureImmutable(var1);
      if (((LogEvent)var2).getMessage() instanceof ReusableMessage) {
         if (var2 instanceof Log4jLogEvent) {
            ((Log4jLogEvent)var2).makeMessageImmutable();
         } else if (var2 instanceof MutableLogEvent) {
            if (this.translator != MUTABLE_TRANSLATOR) {
               var2 = ((MutableLogEvent)var2).createMemento();
            }
         } else {
            this.showWarningAboutCustomLogEventWithReusableMessage((LogEvent)var2);
         }
      } else {
         InternalAsyncUtil.makeMessageImmutable(((LogEvent)var2).getMessage());
      }

      return (LogEvent)var2;
   }

   private void showWarningAboutCustomLogEventWithReusableMessage(LogEvent var1) {
      if (!this.alreadyLoggedWarning) {
         LOGGER.warn(
            "Custom log event of type {} contains a mutable message of type {}. AsyncLoggerConfig does not know how to make an immutable copy of this message. This may result in ConcurrentModificationExceptions or incorrect log messages if the application modifies objects in the message while the background thread is writing it to the appenders.",
            var1.getClass().getName(),
            var1.getMessage().getClass().getName()
         );
         this.alreadyLoggedWarning = true;
      }
   }

   private void enqueue(LogEvent var1, AsyncLoggerConfig var2) {
      if (this.synchronizeEnqueueWhenQueueFull()) {
         synchronized (this.queueFullEnqueueLock) {
            this.disruptor.getRingBuffer().publishEvent(this.translator, var1, var2);
         }
      } else {
         this.disruptor.getRingBuffer().publishEvent(this.translator, var1, var2);
      }
   }

   private boolean synchronizeEnqueueWhenQueueFull() {
      return DisruptorUtil.ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL
         && this.backgroundThreadId != Thread.currentThread().getId()
         && !(Thread.currentThread() instanceof Log4jThread);
   }

   @Override
   public boolean tryEnqueue(LogEvent var1, AsyncLoggerConfig var2) {
      LogEvent var3 = this.prepareEvent(var1);
      return this.disruptor.getRingBuffer().tryPublishEvent(this.translator, var3, var2);
   }

   private LogEvent ensureImmutable(LogEvent var1) {
      LogEvent var2 = var1;
      if (var1 instanceof RingBufferLogEvent) {
         var2 = ((RingBufferLogEvent)var1).createMemento();
      }

      return var2;
   }

   @Override
   public RingBufferAdmin createRingBufferAdmin(String var1, String var2) {
      return RingBufferAdmin.forAsyncLoggerConfig(this.disruptor.getRingBuffer(), var1, var2);
   }

   public static class Log4jEventWrapper {
      private AsyncLoggerConfig loggerConfig;
      private LogEvent event;

      public Log4jEventWrapper() {
      }

      public Log4jEventWrapper(MutableLogEvent var1) {
         this.event = var1;
      }

      public void clear() {
         this.loggerConfig = null;
         if (this.event instanceof MutableLogEvent) {
            ((MutableLogEvent)this.event).clear();
         } else {
            this.event = null;
         }
      }

      @Override
      public String toString() {
         return String.valueOf(this.event);
      }
   }

   private static class Log4jEventWrapperHandler implements SequenceReportingEventHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> {
      private static final int NOTIFY_PROGRESS_THRESHOLD = 50;
      private Sequence sequenceCallback;
      private int counter;

      private Log4jEventWrapperHandler() {
      }

      public void setSequenceCallback(Sequence var1) {
         this.sequenceCallback = var1;
      }

      public void onEvent(AsyncLoggerConfigDisruptor.Log4jEventWrapper var1, long var2, boolean var4) throws Exception {
         var1.event.setEndOfBatch(var4);
         var1.loggerConfig.logToAsyncLoggerConfigsOnCurrentThread(var1.event);
         var1.clear();
         this.notifyIntermediateProgress(var2);
      }

      private void notifyIntermediateProgress(long var1) {
         if (++this.counter > 50) {
            this.sequenceCallback.set(var1);
            this.counter = 0;
         }
      }
   }
}
