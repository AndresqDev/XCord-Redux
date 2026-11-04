package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.dsl.Disruptor;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.ReliabilityStrategy;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.StringMap;

public class AsyncLogger extends Logger implements EventTranslatorVararg<RingBufferLogEvent> {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final Clock CLOCK = ClockFactory.getClock();
   private static final ContextDataInjector CONTEXT_DATA_INJECTOR = ContextDataInjectorFactory.createInjector();
   private static final ThreadNameCachingStrategy THREAD_NAME_CACHING_STRATEGY = ThreadNameCachingStrategy.create();
   private final ThreadLocal<RingBufferLogEventTranslator> threadLocalTranslator = new ThreadLocal<>();
   private final AsyncLoggerDisruptor loggerDisruptor;
   private volatile boolean includeLocation;
   private volatile NanoClock nanoClock;
   private final AsyncLogger.TranslatorType threadLocalTranslatorType = new AsyncLogger.TranslatorType() {
      @Override
      void log(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6) {
         AsyncLogger.this.logWithThreadLocalTranslator(var1, var2, var3, var4, var5, var6);
      }

      @Override
      void log(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
         AsyncLogger.this.logWithThreadLocalTranslator(var1, var2, var3, var4, var5);
      }
   };
   private final AsyncLogger.TranslatorType varargTranslatorType = new AsyncLogger.TranslatorType() {
      @Override
      void log(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6) {
         AsyncLogger.this.logWithVarargTranslator(var1, var2, var3, var4, var5, var6);
      }

      @Override
      void log(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
         AsyncLogger.this.logWithVarargTranslator(var1, var2, var3, var4, var5);
      }
   };

   public AsyncLogger(LoggerContext var1, String var2, MessageFactory var3, AsyncLoggerDisruptor var4) {
      super(var1, var2, var3);
      this.loggerDisruptor = var4;
      this.includeLocation = this.privateConfig.loggerConfig.isIncludeLocation();
      this.nanoClock = var1.getConfiguration().getNanoClock();
   }

   @Override
   protected void updateConfiguration(Configuration var1) {
      this.nanoClock = var1.getNanoClock();
      this.includeLocation = var1.getLoggerConfig(this.name).isIncludeLocation();
      super.updateConfiguration(var1);
   }

   NanoClock getNanoClock() {
      return this.nanoClock;
   }

   private RingBufferLogEventTranslator getCachedTranslator() {
      RingBufferLogEventTranslator var1 = this.threadLocalTranslator.get();
      if (var1 == null) {
         var1 = new RingBufferLogEventTranslator();
         this.threadLocalTranslator.set(var1);
      }

      return var1;
   }

   @Override
   public void logMessage(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      this.getTranslatorType().log(var1, var2, var3, var4, var5);
   }

   @Override
   public void log(Level var1, Marker var2, String var3, StackTraceElement var4, Message var5, Throwable var6) {
      this.getTranslatorType().log(var3, var4, var1, var2, var5, var6);
   }

   private AsyncLogger.TranslatorType getTranslatorType() {
      return this.loggerDisruptor.isUseThreadLocals() ? this.threadLocalTranslatorType : this.varargTranslatorType;
   }

   private boolean isReused(Message var1) {
      return var1 instanceof ReusableMessage;
   }

   private void logWithThreadLocalTranslator(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      RingBufferLogEventTranslator var6 = this.getCachedTranslator();
      this.initTranslator(var6, var1, var2, var3, var4, var5);
      this.initTranslatorThreadValues(var6);
      this.publish(var6);
   }

   private void logWithThreadLocalTranslator(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6) {
      RingBufferLogEventTranslator var7 = this.getCachedTranslator();
      this.initTranslator(var7, var1, var2, var3, var4, var5, var6);
      this.initTranslatorThreadValues(var7);
      this.publish(var7);
   }

   private void publish(RingBufferLogEventTranslator var1) {
      if (!this.loggerDisruptor.tryPublish(var1)) {
         this.handleRingBufferFull(var1);
      }
   }

   private void handleRingBufferFull(RingBufferLogEventTranslator var1) {
      if (AbstractLogger.getRecursionDepth() > 1) {
         AsyncQueueFullMessageUtil.logWarningToStatusLogger();
         this.logMessageInCurrentThread(var1.fqcn, var1.level, var1.marker, var1.message, var1.thrown);
         var1.clear();
      } else {
         EventRoute var2 = this.loggerDisruptor.getEventRoute(var1.level);
         switch (var2) {
            case ENQUEUE:
               this.loggerDisruptor.enqueueLogMessageWhenQueueFull(var1);
               break;
            case SYNCHRONOUS:
               this.logMessageInCurrentThread(var1.fqcn, var1.level, var1.marker, var1.message, var1.thrown);
               var1.clear();
               break;
            case DISCARD:
               var1.clear();
               break;
            default:
               throw new IllegalStateException("Unknown EventRoute " + var2);
         }
      }
   }

   private void initTranslator(RingBufferLogEventTranslator var1, String var2, StackTraceElement var3, Level var4, Marker var5, Message var6, Throwable var7) {
      var1.setBasicValues(this, this.name, var5, var2, var4, var6, var7, ThreadContext.getImmutableStack(), var3, CLOCK, this.nanoClock);
   }

   private void initTranslator(RingBufferLogEventTranslator var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      var1.setBasicValues(
         this, this.name, var4, var2, var3, var5, var6, ThreadContext.getImmutableStack(), this.calcLocationIfRequested(var2), CLOCK, this.nanoClock
      );
   }

   private void initTranslatorThreadValues(RingBufferLogEventTranslator var1) {
      if (THREAD_NAME_CACHING_STRATEGY == ThreadNameCachingStrategy.UNCACHED) {
         var1.updateThreadValues();
      }
   }

   private StackTraceElement calcLocationIfRequested(String var1) {
      return this.includeLocation ? StackLocatorUtil.calcLocation(var1) : null;
   }

   private void logWithVarargTranslator(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      Disruptor var6 = this.loggerDisruptor.getDisruptor();
      if (var6 == null) {
         LOGGER.error("Ignoring log event after Log4j has been shut down.");
      } else {
         if (!this.isReused(var4)) {
            InternalAsyncUtil.makeMessageImmutable(var4);
         }

         Object var7 = null;
         if (!var6.getRingBuffer().tryPublishEvent(this, new Object[]{this, var7 = this.calcLocationIfRequested(var1), var1, var2, var3, var4, var5})) {
            this.handleRingBufferFull((StackTraceElement)var7, var1, var2, var3, var4, var5);
         }
      }
   }

   private void logWithVarargTranslator(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6) {
      Disruptor var7 = this.loggerDisruptor.getDisruptor();
      if (var7 == null) {
         LOGGER.error("Ignoring log event after Log4j has been shut down.");
      } else {
         if (!this.isReused(var5)) {
            InternalAsyncUtil.makeMessageImmutable(var5);
         }

         if (!var7.getRingBuffer().tryPublishEvent(this, new Object[]{this, var2, var1, var3, var4, var5, var6})) {
            this.handleRingBufferFull(var2, var1, var3, var4, var5, var6);
         }
      }
   }

   public void translateTo(RingBufferLogEvent var1, long var2, Object... var4) {
      AsyncLogger var5 = (AsyncLogger)var4[0];
      StackTraceElement var6 = (StackTraceElement)var4[1];
      String var7 = (String)var4[2];
      Level var8 = (Level)var4[3];
      Marker var9 = (Marker)var4[4];
      Message var10 = (Message)var4[5];
      Throwable var11 = (Throwable)var4[6];
      ThreadContext.ContextStack var12 = ThreadContext.getImmutableStack();
      Thread var13 = Thread.currentThread();
      String var14 = THREAD_NAME_CACHING_STRATEGY.getThreadName();
      var1.setValues(
         var5,
         var5.getName(),
         var9,
         var7,
         var8,
         var10,
         var11,
         CONTEXT_DATA_INJECTOR.injectContextData(null, (StringMap)var1.getContextData()),
         var12,
         var13.getId(),
         var14,
         var13.getPriority(),
         var6,
         CLOCK,
         this.nanoClock
      );
   }

   void logMessageInCurrentThread(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      ReliabilityStrategy var6 = this.privateConfig.loggerConfig.getReliabilityStrategy();
      var6.log(this, this.getName(), var1, var3, var2, var4, var5);
   }

   private void handleRingBufferFull(StackTraceElement var1, String var2, Level var3, Marker var4, Message var5, Throwable var6) {
      if (AbstractLogger.getRecursionDepth() > 1) {
         AsyncQueueFullMessageUtil.logWarningToStatusLogger();
         this.logMessageInCurrentThread(var2, var3, var4, var5, var6);
      } else {
         EventRoute var7 = this.loggerDisruptor.getEventRoute(var3);
         switch (var7) {
            case ENQUEUE:
               this.loggerDisruptor.enqueueLogMessageWhenQueueFull(this, this, var1, var2, var3, var4, var5, var6);
               break;
            case SYNCHRONOUS:
               this.logMessageInCurrentThread(var2, var3, var4, var5, var6);
            case DISCARD:
               break;
            default:
               throw new IllegalStateException("Unknown EventRoute " + var7);
         }
      }
   }

   public void actualAsyncLog(RingBufferLogEvent var1) {
      LoggerConfig var2 = this.privateConfig.loggerConfig;
      List var3 = var2.getPropertyList();
      if (var3 != null) {
         this.onPropertiesPresent(var1, var3);
      }

      var2.getReliabilityStrategy().log(this, var1);
   }

   private void onPropertiesPresent(RingBufferLogEvent var1, List<Property> var2) {
      StringMap var3 = getContextData(var1);
      int var4 = 0;

      for (int var5 = var2.size(); var4 < var5; var4++) {
         Property var6 = (Property)var2.get(var4);
         if (var3.getValue(var6.getName()) == null) {
            String var7 = var6.isValueNeedsLookup() ? this.privateConfig.config.getStrSubstitutor().replace(var1, var6.getValue()) : var6.getValue();
            var3.putValue(var6.getName(), var7);
         }
      }

      var1.setContextData(var3);
   }

   private static StringMap getContextData(RingBufferLogEvent var0) {
      StringMap var1 = (StringMap)var0.getContextData();
      if (var1.isFrozen()) {
         StringMap var2 = ContextDataFactory.createContextData();
         var2.putAll(var1);
         return var2;
      } else {
         return var1;
      }
   }

   abstract class TranslatorType {
      abstract void log(String var1, StackTraceElement var2, Level var3, Marker var4, Message var5, Throwable var6);

      abstract void log(String var1, Level var2, Marker var3, Message var4, Throwable var5);
   }
}
