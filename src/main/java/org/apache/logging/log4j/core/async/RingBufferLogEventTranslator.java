package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringMap;

public class RingBufferLogEventTranslator implements EventTranslator<RingBufferLogEvent> {
   private static final ContextDataInjector INJECTOR = ContextDataInjectorFactory.createInjector();
   private AsyncLogger asyncLogger;
   String loggerName;
   protected Marker marker;
   protected String fqcn;
   protected Level level;
   protected Message message;
   protected Throwable thrown;
   private ThreadContext.ContextStack contextStack;
   private long threadId = Thread.currentThread().getId();
   private String threadName = Thread.currentThread().getName();
   private int threadPriority = Thread.currentThread().getPriority();
   private StackTraceElement location;
   private Clock clock;
   private NanoClock nanoClock;

   public void translateTo(RingBufferLogEvent var1, long var2) {
      try {
         var1.setValues(
            this.asyncLogger,
            this.loggerName,
            this.marker,
            this.fqcn,
            this.level,
            this.message,
            this.thrown,
            INJECTOR.injectContextData(null, (StringMap)var1.getContextData()),
            this.contextStack,
            this.threadId,
            this.threadName,
            this.threadPriority,
            this.location,
            this.clock,
            this.nanoClock
         );
      } finally {
         this.clear();
      }
   }

   void clear() {
      this.setBasicValues(null, null, null, null, null, null, null, null, null, null, null);
   }

   public void setBasicValues(
      AsyncLogger var1,
      String var2,
      Marker var3,
      String var4,
      Level var5,
      Message var6,
      Throwable var7,
      ThreadContext.ContextStack var8,
      StackTraceElement var9,
      Clock var10,
      NanoClock var11
   ) {
      this.asyncLogger = var1;
      this.loggerName = var2;
      this.marker = var3;
      this.fqcn = var4;
      this.level = var5;
      this.message = var6;
      this.thrown = var7;
      this.contextStack = var8;
      this.location = var9;
      this.clock = var10;
      this.nanoClock = var11;
   }

   public void updateThreadValues() {
      Thread var1 = Thread.currentThread();
      this.threadId = var1.getId();
      this.threadName = var1.getName();
      this.threadPriority = var1.getPriority();
   }
}
