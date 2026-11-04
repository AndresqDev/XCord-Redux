package org.apache.logging.log4j.core.impl;

import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.ThreadNameCachingStrategy;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringMap;

public class ReusableLogEventFactory implements LocationAwareLogEventFactory, LogEventFactory {
   private static final ThreadNameCachingStrategy THREAD_NAME_CACHING_STRATEGY = ThreadNameCachingStrategy.create();
   private static final Clock CLOCK = ClockFactory.getClock();
   private static final ThreadLocal<MutableLogEvent> mutableLogEventThreadLocal = new ThreadLocal<>();
   private final ContextDataInjector injector = ContextDataInjectorFactory.createInjector();

   @Override
   public LogEvent createEvent(String var1, Marker var2, String var3, Level var4, Message var5, List<Property> var6, Throwable var7) {
      return this.createEvent(var1, var2, var3, null, var4, var5, var6, var7);
   }

   @Override
   public LogEvent createEvent(String var1, Marker var2, String var3, StackTraceElement var4, Level var5, Message var6, List<Property> var7, Throwable var8) {
      MutableLogEvent var9 = getOrCreateMutableLogEvent();
      var9.reserved = true;
      var9.setLoggerName(var1);
      var9.setMarker(var2);
      var9.setLoggerFqcn(var3);
      var9.setLevel(var5 == null ? Level.OFF : var5);
      var9.setMessage(var6);
      var9.initTime(CLOCK, Log4jLogEvent.getNanoClock());
      var9.setThrown(var8);
      var9.setSource(var4);
      var9.setContextData(this.injector.injectContextData(var7, (StringMap)var9.getContextData()));
      var9.setContextStack((ThreadContext.ContextStack)(ThreadContext.getDepth() == 0 ? ThreadContext.EMPTY_STACK : ThreadContext.cloneStack()));
      if (THREAD_NAME_CACHING_STRATEGY == ThreadNameCachingStrategy.UNCACHED) {
         var9.setThreadName(Thread.currentThread().getName());
         var9.setThreadPriority(Thread.currentThread().getPriority());
      }

      return var9;
   }

   private static MutableLogEvent getOrCreateMutableLogEvent() {
      MutableLogEvent var0 = mutableLogEventThreadLocal.get();
      return var0 != null && !var0.reserved ? var0 : createInstance(var0);
   }

   private static MutableLogEvent createInstance(MutableLogEvent var0) {
      MutableLogEvent var1 = new MutableLogEvent();
      var1.setThreadId(Thread.currentThread().getId());
      var1.setThreadName(Thread.currentThread().getName());
      var1.setThreadPriority(Thread.currentThread().getPriority());
      if (var0 == null) {
         mutableLogEventThreadLocal.set(var1);
      }

      return var1;
   }

   public static void release(LogEvent var0) {
      if (var0 instanceof MutableLogEvent) {
         MutableLogEvent var1 = (MutableLogEvent)var0;
         var1.clear();
         var1.reserved = false;
      }
   }
}
