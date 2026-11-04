package org.apache.logging.log4j.core.impl;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.InternalAsyncUtil;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterConsumer;
import org.apache.logging.log4j.message.ParameterVisitable;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.TimestampMessage;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.StringMap;

public class MutableLogEvent implements LogEvent, ParameterVisitable, ReusableMessage {
   private static final Message EMPTY = new SimpleMessage("");
   private int threadPriority;
   private long threadId;
   private final MutableInstant instant = new MutableInstant();
   private long nanoTime;
   private short parameterCount;
   private boolean includeLocation;
   private boolean endOfBatch = false;
   private Level level;
   private String threadName;
   private String loggerName;
   private Message message;
   private String messageFormat;
   private StringBuilder messageText;
   private Object[] parameters;
   private Throwable thrown;
   private ThrowableProxy thrownProxy;
   private StringMap contextData = ContextDataFactory.createContextData();
   private Marker marker;
   private String loggerFqcn;
   private StackTraceElement source;
   private ThreadContext.ContextStack contextStack;
   transient boolean reserved = false;

   public MutableLogEvent() {
      this(null, null);
   }

   public MutableLogEvent(StringBuilder var1, Object[] var2) {
      this.messageText = var1;
      this.parameters = var2;
   }

   public Log4jLogEvent toImmutable() {
      return this.createMemento();
   }

   public void initFrom(LogEvent var1) {
      this.loggerFqcn = var1.getLoggerFqcn();
      this.marker = var1.getMarker();
      this.level = var1.getLevel();
      this.loggerName = var1.getLoggerName();
      this.thrown = var1.getThrown();
      this.thrownProxy = var1.getThrownProxy();
      this.instant.initFrom(var1.getInstant());
      this.contextData.putAll(var1.getContextData());
      this.contextStack = var1.getContextStack();
      this.source = var1.isIncludeLocation() ? var1.getSource() : null;
      this.threadId = var1.getThreadId();
      this.threadName = var1.getThreadName();
      this.threadPriority = var1.getThreadPriority();
      this.endOfBatch = var1.isEndOfBatch();
      this.includeLocation = var1.isIncludeLocation();
      this.nanoTime = var1.getNanoTime();
      this.setMessage(var1.getMessage());
   }

   public void clear() {
      this.loggerFqcn = null;
      this.marker = null;
      this.level = null;
      this.loggerName = null;
      this.message = null;
      this.messageFormat = null;
      this.thrown = null;
      this.thrownProxy = null;
      this.source = null;
      if (this.contextData != null) {
         if (this.contextData.isFrozen()) {
            this.contextData = null;
         } else {
            this.contextData.clear();
         }
      }

      this.contextStack = null;
      StringBuilders.trimToMaxSize(this.messageText, Constants.MAX_REUSABLE_MESSAGE_SIZE);
      if (this.parameters != null) {
         Arrays.fill(this.parameters, null);
      }
   }

   @Override
   public String getLoggerFqcn() {
      return this.loggerFqcn;
   }

   public void setLoggerFqcn(String var1) {
      this.loggerFqcn = var1;
   }

   @Override
   public Marker getMarker() {
      return this.marker;
   }

   public void setMarker(Marker var1) {
      this.marker = var1;
   }

   @Override
   public Level getLevel() {
      if (this.level == null) {
         this.level = Level.OFF;
      }

      return this.level;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   @Override
   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   @Override
   public Message getMessage() {
      if (this.message == null) {
         return (Message)(this.messageText == null ? EMPTY : this);
      } else {
         return this.message;
      }
   }

   public void setMessage(Message var1) {
      if (var1 instanceof ReusableMessage) {
         ReusableMessage var2 = (ReusableMessage)var1;
         var2.formatTo(this.getMessageTextForWriting());
         this.messageFormat = var1.getFormat();
         this.parameters = var2.swapParameters(this.parameters == null ? new Object[10] : this.parameters);
         this.parameterCount = var2.getParameterCount();
      } else {
         this.message = InternalAsyncUtil.makeMessageImmutable(var1);
      }
   }

   private StringBuilder getMessageTextForWriting() {
      if (this.messageText == null) {
         this.messageText = new StringBuilder(Constants.INITIAL_REUSABLE_MESSAGE_SIZE);
      }

      this.messageText.setLength(0);
      return this.messageText;
   }

   @Override
   public String getFormattedMessage() {
      return this.messageText.toString();
   }

   @Override
   public String getFormat() {
      return this.messageFormat;
   }

   @Override
   public Object[] getParameters() {
      return this.parameters == null ? null : Arrays.copyOf(this.parameters, this.parameterCount);
   }

   @Override
   public <S> void forEachParameter(ParameterConsumer<S> var1, S var2) {
      if (this.parameters != null) {
         for (short var3 = 0; var3 < this.parameterCount; var3++) {
            var1.accept(this.parameters[var3], var3, var2);
         }
      }
   }

   @Override
   public Throwable getThrowable() {
      return this.getThrown();
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append((CharSequence)this.messageText);
   }

   @Override
   public Object[] swapParameters(Object[] var1) {
      Object[] var2 = this.parameters;
      this.parameters = var1;
      return var2;
   }

   @Override
   public short getParameterCount() {
      return this.parameterCount;
   }

   @Override
   public Message memento() {
      if (this.message == null) {
         this.message = new MementoMessage(String.valueOf(this.messageText), this.messageFormat, this.getParameters());
      }

      return this.message;
   }

   @Override
   public Throwable getThrown() {
      return this.thrown;
   }

   public void setThrown(Throwable var1) {
      this.thrown = var1;
   }

   void initTime(Clock var1, NanoClock var2) {
      if (this.message instanceof TimestampMessage) {
         this.instant.initFromEpochMilli(((TimestampMessage)this.message).getTimestamp(), 0);
      } else {
         this.instant.initFrom(var1);
      }

      this.nanoTime = var2.nanoTime();
   }

   @Override
   public long getTimeMillis() {
      return this.instant.getEpochMillisecond();
   }

   public void setTimeMillis(long var1) {
      this.instant.initFromEpochMilli(var1, 0);
   }

   @Override
   public Instant getInstant() {
      return this.instant;
   }

   @Override
   public ThrowableProxy getThrownProxy() {
      if (this.thrownProxy == null && this.thrown != null) {
         this.thrownProxy = new ThrowableProxy(this.thrown);
      }

      return this.thrownProxy;
   }

   public void setSource(StackTraceElement var1) {
      this.source = var1;
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
   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   @Override
   public Map<String, String> getContextMap() {
      return this.contextData.toMap();
   }

   public void setContextData(StringMap var1) {
      this.contextData = var1;
   }

   @Override
   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   public void setContextStack(ThreadContext.ContextStack var1) {
      this.contextStack = var1;
   }

   @Override
   public long getThreadId() {
      return this.threadId;
   }

   public void setThreadId(long var1) {
      this.threadId = var1;
   }

   @Override
   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String var1) {
      this.threadName = var1;
   }

   @Override
   public int getThreadPriority() {
      return this.threadPriority;
   }

   public void setThreadPriority(int var1) {
      this.threadPriority = var1;
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

   public void setNanoTime(long var1) {
      this.nanoTime = var1;
   }

   protected Object writeReplace() {
      return new Log4jLogEvent.LogEventProxy(this, this.includeLocation);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   public Log4jLogEvent createMemento() {
      return Log4jLogEvent.deserialize(Log4jLogEvent.serialize(this, this.includeLocation));
   }

   public void initializeBuilder(Log4jLogEvent.Builder var1) {
      var1.setContextData(this.contextData)
         .setContextStack(this.contextStack)
         .setEndOfBatch(this.endOfBatch)
         .setIncludeLocation(this.includeLocation)
         .setLevel(this.getLevel())
         .setLoggerFqcn(this.loggerFqcn)
         .setLoggerName(this.loggerName)
         .setMarker(this.marker)
         .setMessage(this.memento())
         .setNanoTime(this.nanoTime)
         .setSource(this.source)
         .setThreadId(this.threadId)
         .setThreadName(this.threadName)
         .setThreadPriority(this.threadPriority)
         .setThrown(this.getThrown())
         .setThrownProxy(this.thrownProxy)
         .setInstant(this.instant);
   }
}
