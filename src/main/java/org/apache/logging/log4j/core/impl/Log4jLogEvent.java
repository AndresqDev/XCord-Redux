package org.apache.logging.log4j.core.impl;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.core.util.DummyNanoClock;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.TimestampMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.StringMap;

public class Log4jLogEvent implements LogEvent {
   private static final long serialVersionUID = -8393305700508709443L;
   private static final Clock CLOCK = ClockFactory.getClock();
   private static volatile NanoClock nanoClock = new DummyNanoClock();
   private static final ContextDataInjector CONTEXT_DATA_INJECTOR = ContextDataInjectorFactory.createInjector();
   private final String loggerFqcn;
   private final Marker marker;
   private final Level level;
   private final String loggerName;
   private Message message;
   private final MutableInstant instant = new MutableInstant();
   private final transient Throwable thrown;
   private ThrowableProxy thrownProxy;
   private final StringMap contextData;
   private final ThreadContext.ContextStack contextStack;
   private long threadId;
   private String threadName;
   private int threadPriority;
   private StackTraceElement source;
   private boolean includeLocation;
   private boolean endOfBatch = false;
   private final transient long nanoTime;

   public static Log4jLogEvent.Builder newBuilder() {
      return new Log4jLogEvent.Builder();
   }

   public Log4jLogEvent() {
      this("", null, "", null, null, (Throwable)null, null, null, null, 0L, null, 0, null, CLOCK, nanoClock.nanoTime());
   }

   @Deprecated
   public Log4jLogEvent(long var1) {
      this("", null, "", null, null, (Throwable)null, null, null, null, 0L, null, 0, null, var1, 0, nanoClock.nanoTime());
   }

   @Deprecated
   public Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, Throwable var6) {
      this(var1, var2, var3, var4, var5, null, var6);
   }

   public Log4jLogEvent(String var1, Marker var2, String var3, Level var4, Message var5, List<Property> var6, Throwable var7) {
      this(
         var1,
         var2,
         var3,
         var4,
         var5,
         var7,
         null,
         createContextData(var6),
         ThreadContext.getDepth() == 0 ? null : ThreadContext.cloneStack(),
         0L,
         null,
         0,
         null,
         CLOCK,
         nanoClock.nanoTime()
      );
   }

   public Log4jLogEvent(String var1, Marker var2, String var3, StackTraceElement var4, Level var5, Message var6, List<Property> var7, Throwable var8) {
      this(
         var1,
         var2,
         var3,
         var5,
         var6,
         var8,
         null,
         createContextData(var7),
         ThreadContext.getDepth() == 0 ? null : ThreadContext.cloneStack(),
         0L,
         null,
         0,
         var4,
         CLOCK,
         nanoClock.nanoTime()
      );
   }

   @Deprecated
   public Log4jLogEvent(
      String var1,
      Marker var2,
      String var3,
      Level var4,
      Message var5,
      Throwable var6,
      Map<String, String> var7,
      ThreadContext.ContextStack var8,
      String var9,
      StackTraceElement var10,
      long var11
   ) {
      this(var1, var2, var3, var4, var5, var6, null, createContextData(var7), var8, 0L, var9, 0, var10, var11, 0, nanoClock.nanoTime());
   }

   @Deprecated
   public static Log4jLogEvent createEvent(
      String var0,
      Marker var1,
      String var2,
      Level var3,
      Message var4,
      Throwable var5,
      ThrowableProxy var6,
      Map<String, String> var7,
      ThreadContext.ContextStack var8,
      String var9,
      StackTraceElement var10,
      long var11
   ) {
      return new Log4jLogEvent(var0, var1, var2, var3, var4, var5, var6, createContextData(var7), var8, 0L, var9, 0, var10, var11, 0, nanoClock.nanoTime());
   }

   private Log4jLogEvent(
      String var1,
      Marker var2,
      String var3,
      Level var4,
      Message var5,
      Throwable var6,
      ThrowableProxy var7,
      StringMap var8,
      ThreadContext.ContextStack var9,
      long var10,
      String var12,
      int var13,
      StackTraceElement var14,
      long var15,
      int var17,
      long var18
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, var13, var14, var18);
      long var20 = var5 instanceof TimestampMessage ? ((TimestampMessage)var5).getTimestamp() : var15;
      this.instant.initFromEpochMilli(var20, var17);
   }

   private Log4jLogEvent(
      String var1,
      Marker var2,
      String var3,
      Level var4,
      Message var5,
      Throwable var6,
      ThrowableProxy var7,
      StringMap var8,
      ThreadContext.ContextStack var9,
      long var10,
      String var12,
      int var13,
      StackTraceElement var14,
      Clock var15,
      long var16
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, var13, var14, var16);
      if (var5 instanceof TimestampMessage) {
         this.instant.initFromEpochMilli(((TimestampMessage)var5).getTimestamp(), 0);
      } else {
         this.instant.initFrom(var15);
      }
   }

   private Log4jLogEvent(
      String var1,
      Marker var2,
      String var3,
      Level var4,
      Message var5,
      Throwable var6,
      ThrowableProxy var7,
      StringMap var8,
      ThreadContext.ContextStack var9,
      long var10,
      String var12,
      int var13,
      StackTraceElement var14,
      long var15
   ) {
      this.loggerName = var1;
      this.marker = var2;
      this.loggerFqcn = var3;
      this.level = var4 == null ? Level.OFF : var4;
      this.message = var5;
      this.thrown = var6;
      this.thrownProxy = var7;
      this.contextData = var8 == null ? ContextDataFactory.createContextData() : var8;
      this.contextStack = (ThreadContext.ContextStack)(var9 == null ? ThreadContext.EMPTY_STACK : var9);
      this.threadId = var10;
      this.threadName = var12;
      this.threadPriority = var13;
      this.source = var14;
      if (var5 instanceof LoggerNameAwareMessage) {
         ((LoggerNameAwareMessage)var5).setLoggerName(var1);
      }

      this.nanoTime = var15;
   }

   private static StringMap createContextData(Map<String, String> var0) {
      StringMap var1 = ContextDataFactory.createContextData();
      if (var0 != null) {
         for (Entry var3 : var0.entrySet()) {
            var1.putValue((String)var3.getKey(), var3.getValue());
         }
      }

      return var1;
   }

   private static StringMap createContextData(List<Property> var0) {
      StringMap var1 = ContextDataFactory.createContextData();
      return CONTEXT_DATA_INJECTOR.injectContextData(var0, var1);
   }

   public static NanoClock getNanoClock() {
      return nanoClock;
   }

   public static void setNanoClock(NanoClock var0) {
      nanoClock = Objects.requireNonNull(var0, "NanoClock must be non-null");
      StatusLogger.getLogger().trace("Using {} for nanosecond timestamps.", var0.getClass().getSimpleName());
   }

   public Log4jLogEvent.Builder asBuilder() {
      return new Log4jLogEvent.Builder(this);
   }

   public Log4jLogEvent toImmutable() {
      if (this.getMessage() instanceof ReusableMessage) {
         this.makeMessageImmutable();
      }

      return this;
   }

   @Override
   public Level getLevel() {
      return this.level;
   }

   @Override
   public String getLoggerName() {
      return this.loggerName;
   }

   @Override
   public Message getMessage() {
      return this.message;
   }

   public void makeMessageImmutable() {
      this.message = new MementoMessage(this.message.getFormattedMessage(), this.message.getFormat(), this.message.getParameters());
   }

   @Override
   public long getThreadId() {
      if (this.threadId == 0L) {
         this.threadId = Thread.currentThread().getId();
      }

      return this.threadId;
   }

   @Override
   public String getThreadName() {
      if (this.threadName == null) {
         this.threadName = Thread.currentThread().getName();
      }

      return this.threadName;
   }

   @Override
   public int getThreadPriority() {
      if (this.threadPriority == 0) {
         this.threadPriority = Thread.currentThread().getPriority();
      }

      return this.threadPriority;
   }

   @Override
   public long getTimeMillis() {
      return this.instant.getEpochMillisecond();
   }

   @Override
   public Instant getInstant() {
      return this.instant;
   }

   @Override
   public Throwable getThrown() {
      return this.thrown;
   }

   @Override
   public ThrowableProxy getThrownProxy() {
      if (this.thrownProxy == null && this.thrown != null) {
         this.thrownProxy = new ThrowableProxy(this.thrown);
      }

      return this.thrownProxy;
   }

   @Override
   public Marker getMarker() {
      return this.marker;
   }

   @Override
   public String getLoggerFqcn() {
      return this.loggerFqcn;
   }

   @Override
   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   @Override
   public Map<String, String> getContextMap() {
      return this.contextData.toMap();
   }

   @Override
   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   @Override
   public StackTraceElement getSource() {
      if (this.source != null) {
         return this.source;
      } else if (this.loggerFqcn != null && this.includeLocation) {
         this.source = StackLocatorUtil.calcLocation(this.loggerFqcn);
         return this.source;
      } else {
         return null;
      }
   }

   @Override
   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   @Override
   public void setIncludeLocation(boolean var1) {
      this.includeLocation = var1;
   }

   @Override
   public boolean isEndOfBatch() {
      return this.endOfBatch;
   }

   @Override
   public void setEndOfBatch(boolean var1) {
      this.endOfBatch = var1;
   }

   @Override
   public long getNanoTime() {
      return this.nanoTime;
   }

   protected Object writeReplace() {
      this.getThrownProxy();
      return new Log4jLogEvent.LogEventProxy(this, this.includeLocation);
   }

   public static Serializable serialize(LogEvent var0, boolean var1) {
      if (var0 instanceof Log4jLogEvent) {
         var0.getThrownProxy();
         return new Log4jLogEvent.LogEventProxy((Log4jLogEvent)var0, var1);
      } else {
         return new Log4jLogEvent.LogEventProxy(var0, var1);
      }
   }

   public static Serializable serialize(Log4jLogEvent var0, boolean var1) {
      var0.getThrownProxy();
      return new Log4jLogEvent.LogEventProxy(var0, var1);
   }

   public static boolean canDeserialize(Serializable var0) {
      return var0 instanceof Log4jLogEvent.LogEventProxy;
   }

   public static Log4jLogEvent deserialize(Serializable var0) {
      Objects.requireNonNull(var0, "Event cannot be null");
      if (var0 instanceof Log4jLogEvent.LogEventProxy) {
         Log4jLogEvent.LogEventProxy var1 = (Log4jLogEvent.LogEventProxy)var0;
         Log4jLogEvent var2 = new Log4jLogEvent(
            var1.loggerName,
            var1.marker,
            var1.loggerFQCN,
            var1.level,
            var1.message,
            var1.thrown,
            var1.thrownProxy,
            var1.contextData,
            var1.contextStack,
            var1.threadId,
            var1.threadName,
            var1.threadPriority,
            var1.source,
            var1.timeMillis,
            var1.nanoOfMillisecond,
            var1.nanoTime
         );
         var2.setEndOfBatch(var1.isEndOfBatch);
         var2.setIncludeLocation(var1.isLocationRequired);
         return var2;
      } else {
         throw new IllegalArgumentException("Event is not a serialized LogEvent: " + var0.toString());
      }
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   public static LogEvent createMemento(LogEvent var0) {
      return new Log4jLogEvent.Builder(var0).build();
   }

   public static Log4jLogEvent createMemento(LogEvent var0, boolean var1) {
      return deserialize(serialize(var0, var1));
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = this.loggerName.isEmpty() ? "root" : this.loggerName;
      var1.append("Logger=").append(var2);
      var1.append(" Level=").append(this.level.name());
      var1.append(" Message=").append(this.message == null ? null : this.message.getFormattedMessage());
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Log4jLogEvent var2 = (Log4jLogEvent)var1;
         if (this.endOfBatch != var2.endOfBatch) {
            return false;
         } else if (this.includeLocation != var2.includeLocation) {
            return false;
         } else if (!this.instant.equals(var2.instant)) {
            return false;
         } else if (this.nanoTime != var2.nanoTime) {
            return false;
         } else if (this.loggerFqcn != null ? this.loggerFqcn.equals(var2.loggerFqcn) : var2.loggerFqcn == null) {
            if (this.level != null ? this.level.equals(var2.level) : var2.level == null) {
               if (this.source != null ? this.source.equals(var2.source) : var2.source == null) {
                  if (this.marker != null ? this.marker.equals(var2.marker) : var2.marker == null) {
                     if (this.contextData != null ? this.contextData.equals(var2.contextData) : var2.contextData == null) {
                        if (!this.message.equals(var2.message)) {
                           return false;
                        } else if (!this.loggerName.equals(var2.loggerName)) {
                           return false;
                        } else if (this.contextStack != null ? this.contextStack.equals(var2.contextStack) : var2.contextStack == null) {
                           if (this.threadId != var2.threadId) {
                              return false;
                           } else if (this.threadName != null ? this.threadName.equals(var2.threadName) : var2.threadName == null) {
                              if (this.threadPriority != var2.threadPriority) {
                                 return false;
                              } else if (this.thrown != null ? this.thrown.equals(var2.thrown) : var2.thrown == null) {
                                 return this.thrownProxy != null ? this.thrownProxy.equals(var2.thrownProxy) : var2.thrownProxy == null;
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.loggerFqcn != null ? this.loggerFqcn.hashCode() : 0;
      var1 = 31 * var1 + (this.marker != null ? this.marker.hashCode() : 0);
      var1 = 31 * var1 + (this.level != null ? this.level.hashCode() : 0);
      var1 = 31 * var1 + this.loggerName.hashCode();
      var1 = 31 * var1 + this.message.hashCode();
      var1 = 31 * var1 + this.instant.hashCode();
      var1 = 31 * var1 + (int)(this.nanoTime ^ this.nanoTime >>> 32);
      var1 = 31 * var1 + (this.thrown != null ? this.thrown.hashCode() : 0);
      var1 = 31 * var1 + (this.thrownProxy != null ? this.thrownProxy.hashCode() : 0);
      var1 = 31 * var1 + (this.contextData != null ? this.contextData.hashCode() : 0);
      var1 = 31 * var1 + (this.contextStack != null ? this.contextStack.hashCode() : 0);
      var1 = 31 * var1 + (int)(this.threadId ^ this.threadId >>> 32);
      var1 = 31 * var1 + (this.threadName != null ? this.threadName.hashCode() : 0);
      var1 = 31 * var1 + (this.threadPriority ^ this.threadPriority >>> 32);
      var1 = 31 * var1 + (this.source != null ? this.source.hashCode() : 0);
      var1 = 31 * var1 + (this.includeLocation ? 1 : 0);
      return 31 * var1 + (this.endOfBatch ? 1 : 0);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<LogEvent> {
      private String loggerFqcn;
      private Marker marker;
      private Level level;
      private String loggerName;
      private Message message;
      private Throwable thrown;
      private final MutableInstant instant = new MutableInstant();
      private ThrowableProxy thrownProxy;
      private StringMap contextData = Log4jLogEvent.createContextData((List<Property>)null);
      private ThreadContext.ContextStack contextStack = ThreadContext.getImmutableStack();
      private long threadId;
      private String threadName;
      private int threadPriority;
      private StackTraceElement source;
      private boolean includeLocation;
      private boolean endOfBatch = false;
      private long nanoTime;

      public Builder() {
      }

      public Builder(LogEvent var1) {
         Objects.requireNonNull(var1);
         if (var1 instanceof RingBufferLogEvent) {
            ((RingBufferLogEvent)var1).initializeBuilder(this);
         } else if (var1 instanceof MutableLogEvent) {
            ((MutableLogEvent)var1).initializeBuilder(this);
         } else {
            this.loggerFqcn = var1.getLoggerFqcn();
            this.marker = var1.getMarker();
            this.level = var1.getLevel();
            this.loggerName = var1.getLoggerName();
            this.message = var1.getMessage();
            this.instant.initFrom(var1.getInstant());
            this.thrown = var1.getThrown();
            this.contextStack = var1.getContextStack();
            this.includeLocation = var1.isIncludeLocation();
            this.endOfBatch = var1.isEndOfBatch();
            this.nanoTime = var1.getNanoTime();
            if (var1 instanceof Log4jLogEvent) {
               Log4jLogEvent var2 = (Log4jLogEvent)var1;
               this.contextData = var2.contextData;
               this.thrownProxy = var2.thrownProxy;
               this.source = var2.source;
               this.threadId = var2.threadId;
               this.threadName = var2.threadName;
               this.threadPriority = var2.threadPriority;
            } else {
               if (var1.getContextData() instanceof StringMap) {
                  this.contextData = (StringMap)var1.getContextData();
               } else {
                  if (this.contextData.isFrozen()) {
                     this.contextData = ContextDataFactory.createContextData();
                  } else {
                     this.contextData.clear();
                  }

                  this.contextData.putAll(var1.getContextData());
               }

               this.thrownProxy = var1.getThrownProxy();
               this.source = var1.getSource();
               this.threadId = var1.getThreadId();
               this.threadName = var1.getThreadName();
               this.threadPriority = var1.getThreadPriority();
            }
         }
      }

      public Log4jLogEvent.Builder setLevel(Level var1) {
         this.level = var1;
         return this;
      }

      public Log4jLogEvent.Builder setLoggerFqcn(String var1) {
         this.loggerFqcn = var1;
         return this;
      }

      public Log4jLogEvent.Builder setLoggerName(String var1) {
         this.loggerName = var1;
         return this;
      }

      public Log4jLogEvent.Builder setMarker(Marker var1) {
         this.marker = var1;
         return this;
      }

      public Log4jLogEvent.Builder setMessage(Message var1) {
         this.message = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThrown(Throwable var1) {
         this.thrown = var1;
         return this;
      }

      public Log4jLogEvent.Builder setTimeMillis(long var1) {
         this.instant.initFromEpochMilli(var1, 0);
         return this;
      }

      public Log4jLogEvent.Builder setInstant(Instant var1) {
         this.instant.initFrom(var1);
         return this;
      }

      public Log4jLogEvent.Builder setThrownProxy(ThrowableProxy var1) {
         this.thrownProxy = var1;
         return this;
      }

      @Deprecated
      public Log4jLogEvent.Builder setContextMap(Map<String, String> var1) {
         this.contextData = ContextDataFactory.createContextData();
         if (var1 != null) {
            for (Entry var3 : var1.entrySet()) {
               this.contextData.putValue((String)var3.getKey(), var3.getValue());
            }
         }

         return this;
      }

      public Log4jLogEvent.Builder setContextData(StringMap var1) {
         this.contextData = var1;
         return this;
      }

      public Log4jLogEvent.Builder setContextStack(ThreadContext.ContextStack var1) {
         this.contextStack = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadId(long var1) {
         this.threadId = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadName(String var1) {
         this.threadName = var1;
         return this;
      }

      public Log4jLogEvent.Builder setThreadPriority(int var1) {
         this.threadPriority = var1;
         return this;
      }

      public Log4jLogEvent.Builder setSource(StackTraceElement var1) {
         this.source = var1;
         return this;
      }

      public Log4jLogEvent.Builder setIncludeLocation(boolean var1) {
         this.includeLocation = var1;
         return this;
      }

      public Log4jLogEvent.Builder setEndOfBatch(boolean var1) {
         this.endOfBatch = var1;
         return this;
      }

      public Log4jLogEvent.Builder setNanoTime(long var1) {
         this.nanoTime = var1;
         return this;
      }

      public Log4jLogEvent build() {
         this.initTimeFields();
         Log4jLogEvent var1 = new Log4jLogEvent(
            this.loggerName,
            this.marker,
            this.loggerFqcn,
            this.level,
            this.message,
            this.thrown,
            this.thrownProxy,
            this.contextData,
            this.contextStack,
            this.threadId,
            this.threadName,
            this.threadPriority,
            this.source,
            this.instant.getEpochMillisecond(),
            this.instant.getNanoOfMillisecond(),
            this.nanoTime
         );
         var1.setIncludeLocation(this.includeLocation);
         var1.setEndOfBatch(this.endOfBatch);
         return var1;
      }

      private void initTimeFields() {
         if (this.instant.getEpochMillisecond() == 0L) {
            this.instant.initFrom(Log4jLogEvent.CLOCK);
         }
      }
   }

   static class LogEventProxy implements Serializable {
      private static final long serialVersionUID = -8634075037355293699L;
      private final String loggerFQCN;
      private final Marker marker;
      private final Level level;
      private final String loggerName;
      private final transient Message message;
      private MarshalledObject<Message> marshalledMessage;
      private String messageString;
      private final long timeMillis;
      private final int nanoOfMillisecond;
      private final transient Throwable thrown;
      private final ThrowableProxy thrownProxy;
      private final StringMap contextData;
      private final ThreadContext.ContextStack contextStack;
      private final long threadId;
      private final String threadName;
      private final int threadPriority;
      private final StackTraceElement source;
      private final boolean isLocationRequired;
      private final boolean isEndOfBatch;
      private final transient long nanoTime;

      public LogEventProxy(Log4jLogEvent var1, boolean var2) {
         this.loggerFQCN = var1.loggerFqcn;
         this.marker = var1.marker;
         this.level = var1.level;
         this.loggerName = var1.loggerName;
         this.message = var1.message instanceof ReusableMessage ? memento((ReusableMessage)var1.message) : var1.message;
         this.timeMillis = var1.instant.getEpochMillisecond();
         this.nanoOfMillisecond = var1.instant.getNanoOfMillisecond();
         this.thrown = var1.thrown;
         this.thrownProxy = var1.thrownProxy;
         this.contextData = var1.contextData;
         this.contextStack = var1.contextStack;
         this.source = var2 ? var1.getSource() : null;
         this.threadId = var1.getThreadId();
         this.threadName = var1.getThreadName();
         this.threadPriority = var1.getThreadPriority();
         this.isLocationRequired = var2;
         this.isEndOfBatch = var1.endOfBatch;
         this.nanoTime = var1.nanoTime;
      }

      public LogEventProxy(LogEvent var1, boolean var2) {
         this.loggerFQCN = var1.getLoggerFqcn();
         this.marker = var1.getMarker();
         this.level = var1.getLevel();
         this.loggerName = var1.getLoggerName();
         Message var3 = var1.getMessage();
         this.message = var3 instanceof ReusableMessage ? memento((ReusableMessage)var3) : var3;
         this.timeMillis = var1.getInstant().getEpochMillisecond();
         this.nanoOfMillisecond = var1.getInstant().getNanoOfMillisecond();
         this.thrown = var1.getThrown();
         this.thrownProxy = var1.getThrownProxy();
         this.contextData = memento(var1.getContextData());
         this.contextStack = var1.getContextStack();
         this.source = var2 ? var1.getSource() : null;
         this.threadId = var1.getThreadId();
         this.threadName = var1.getThreadName();
         this.threadPriority = var1.getThreadPriority();
         this.isLocationRequired = var2;
         this.isEndOfBatch = var1.isEndOfBatch();
         this.nanoTime = var1.getNanoTime();
      }

      private static Message memento(ReusableMessage var0) {
         return var0.memento();
      }

      private static StringMap memento(ReadOnlyStringMap var0) {
         StringMap var1 = ContextDataFactory.createContextData();
         var1.putAll(var0);
         return var1;
      }

      private static MarshalledObject<Message> marshall(Message var0) {
         try {
            return new MarshalledObject<>(var0);
         } catch (Exception var2) {
            return null;
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         this.messageString = this.message.getFormattedMessage();
         this.marshalledMessage = marshall(this.message);
         var1.defaultWriteObject();
      }

      protected Object readResolve() {
         Log4jLogEvent var1 = new Log4jLogEvent(
            this.loggerName,
            this.marker,
            this.loggerFQCN,
            this.level,
            this.message(),
            this.thrown,
            this.thrownProxy,
            this.contextData,
            this.contextStack,
            this.threadId,
            this.threadName,
            this.threadPriority,
            this.source,
            this.timeMillis,
            this.nanoOfMillisecond,
            this.nanoTime
         );
         var1.setEndOfBatch(this.isEndOfBatch);
         var1.setIncludeLocation(this.isLocationRequired);
         return var1;
      }

      private Message message() {
         if (this.marshalledMessage != null) {
            try {
               return this.marshalledMessage.get();
            } catch (Exception var2) {
            }
         }

         return new SimpleMessage(this.messageString);
      }
   }
}
