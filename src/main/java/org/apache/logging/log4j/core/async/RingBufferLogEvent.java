package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.MementoMessage;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
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
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.StringMap;

public class RingBufferLogEvent implements CharSequence, LogEvent, ParameterVisitable, ReusableMessage {
   public static final RingBufferLogEvent.Factory FACTORY = new RingBufferLogEvent.Factory();
   private static final long serialVersionUID = 8462119088943934758L;
   private static final Message EMPTY = new SimpleMessage("");
   private boolean populated;
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
   private transient Throwable thrown;
   private ThrowableProxy thrownProxy;
   private StringMap contextData = ContextDataFactory.createContextData();
   private Marker marker;
   private String fqcn;
   private StackTraceElement location;
   private ThreadContext.ContextStack contextStack;
   private transient AsyncLogger asyncLogger;

   public void setValues(
      AsyncLogger var1,
      String var2,
      Marker var3,
      String var4,
      Level var5,
      Message var6,
      Throwable var7,
      StringMap var8,
      ThreadContext.ContextStack var9,
      long var10,
      String var12,
      int var13,
      StackTraceElement var14,
      Clock var15,
      NanoClock var16
   ) {
      this.threadPriority = var13;
      this.threadId = var10;
      this.level = var5;
      this.threadName = var12;
      this.loggerName = var2;
      this.setMessage(var6);
      this.initTime(var15);
      this.nanoTime = var16.nanoTime();
      this.thrown = var7;
      this.thrownProxy = null;
      this.marker = var3;
      this.fqcn = var4;
      this.location = var14;
      this.contextData = var8;
      this.contextStack = var9;
      this.asyncLogger = var1;
      this.populated = true;
   }

   private void initTime(Clock var1) {
      if (this.message instanceof TimestampMessage) {
         this.instant.initFromEpochMilli(((TimestampMessage)this.message).getTimestamp(), 0);
      } else {
         this.instant.initFrom(var1);
      }
   }

   @Override
   public LogEvent toImmutable() {
      return this.createMemento();
   }

   private void setMessage(Message var1) {
      if (var1 instanceof ReusableMessage) {
         ReusableMessage var2 = (ReusableMessage)var1;
         var2.formatTo(this.getMessageTextForWriting());
         this.messageFormat = var2.getFormat();
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

   public void execute(boolean var1) {
      this.endOfBatch = var1;
      this.asyncLogger.actualAsyncLog(this);
   }

   public boolean isPopulated() {
      return this.populated;
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
   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   @Override
   public void setIncludeLocation(boolean var1) {
      this.includeLocation = var1;
   }

   @Override
   public String getLoggerName() {
      return this.loggerName;
   }

   @Override
   public Marker getMarker() {
      return this.marker;
   }

   @Override
   public String getLoggerFqcn() {
      return this.fqcn;
   }

   @Override
   public Level getLevel() {
      if (this.level == null) {
         this.level = Level.OFF;
      }

      return this.level;
   }

   @Override
   public Message getMessage() {
      if (this.message == null) {
         return (Message)(this.messageText == null ? EMPTY : this);
      } else {
         return this.message;
      }
   }

   @Override
   public String getFormattedMessage() {
      return this.messageText != null ? this.messageText.toString() : (this.message == null ? null : this.message.getFormattedMessage());
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
   public <S> void forEachParameter(ParameterConsumer<S> var1, S var2) {
      if (this.parameters != null) {
         for (short var3 = 0; var3 < this.parameterCount; var3++) {
            var1.accept(this.parameters[var3], var3, var2);
         }
      }
   }

   @Override
   public Message memento() {
      if (this.message == null) {
         this.message = new MementoMessage(String.valueOf(this.messageText), this.messageFormat, this.getParameters());
      }

      return this.message;
   }

   @Override
   public int length() {
      return this.messageText.length();
   }

   @Override
   public char charAt(int var1) {
      return this.messageText.charAt(var1);
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return this.messageText.subSequence(var1, var2);
   }

   @Override
   public Throwable getThrown() {
      if (this.thrown == null && this.thrownProxy != null) {
         this.thrown = this.thrownProxy.getThrowable();
      }

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
   public ReadOnlyStringMap getContextData() {
      return this.contextData;
   }

   void setContextData(StringMap var1) {
      this.contextData = var1;
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
   public long getThreadId() {
      return this.threadId;
   }

   @Override
   public String getThreadName() {
      return this.threadName;
   }

   @Override
   public int getThreadPriority() {
      return this.threadPriority;
   }

   @Override
   public StackTraceElement getSource() {
      return this.location;
   }

   @Override
   public long getTimeMillis() {
      return this.message instanceof TimestampMessage ? ((TimestampMessage)this.message).getTimestamp() : this.instant.getEpochMillisecond();
   }

   @Override
   public Instant getInstant() {
      return this.instant;
   }

   @Override
   public long getNanoTime() {
      return this.nanoTime;
   }

   public void clear() {
      this.populated = false;
      this.asyncLogger = null;
      this.loggerName = null;
      this.marker = null;
      this.fqcn = null;
      this.level = null;
      this.message = null;
      this.messageFormat = null;
      this.thrown = null;
      this.thrownProxy = null;
      this.contextStack = null;
      this.location = null;
      if (this.contextData != null) {
         if (this.contextData.isFrozen()) {
            this.contextData = null;
         } else {
            this.contextData.clear();
         }
      }

      if (Constants.ENABLE_THREADLOCALS) {
         StringBuilders.trimToMaxSize(this.messageText, Constants.MAX_REUSABLE_MESSAGE_SIZE);
         if (this.parameters != null) {
            Arrays.fill(this.parameters, null);
         }
      } else {
         this.messageText = null;
         this.parameters = null;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.getThrownProxy();
      var1.defaultWriteObject();
   }

   public LogEvent createMemento() {
      return new Log4jLogEvent.Builder(this).build();
   }

   public void initializeBuilder(Log4jLogEvent.Builder var1) {
      var1.setContextData(this.contextData)
         .setContextStack(this.contextStack)
         .setEndOfBatch(this.endOfBatch)
         .setIncludeLocation(this.includeLocation)
         .setLevel(this.getLevel())
         .setLoggerFqcn(this.fqcn)
         .setLoggerName(this.loggerName)
         .setMarker(this.marker)
         .setMessage(this.memento())
         .setNanoTime(this.nanoTime)
         .setSource(this.location)
         .setThreadId(this.threadId)
         .setThreadName(this.threadName)
         .setThreadPriority(this.threadPriority)
         .setThrown(this.getThrown())
         .setThrownProxy(this.thrownProxy)
         .setInstant(this.instant);
   }

   private static class Factory implements EventFactory<RingBufferLogEvent> {
      private Factory() {
      }

      public RingBufferLogEvent newInstance() {
         return new RingBufferLogEvent();
      }
   }
}
