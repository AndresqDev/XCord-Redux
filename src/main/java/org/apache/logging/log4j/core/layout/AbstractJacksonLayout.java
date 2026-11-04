package org.apache.logging.log4j.core.layout;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.core.util.StringBuilderWriter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

abstract class AbstractJacksonLayout extends AbstractStringLayout {
   protected static final String DEFAULT_EOL = "\r\n";
   protected static final String COMPACT_EOL = "";
   protected final String eol;
   protected final ObjectWriter objectWriter;
   protected final boolean compact;
   protected final boolean complete;
   protected final boolean includeNullDelimiter;
   protected final AbstractJacksonLayout.ResolvableKeyValuePair[] additionalFields;

   @Deprecated
   protected AbstractJacksonLayout(
      Configuration var1,
      ObjectWriter var2,
      Charset var3,
      boolean var4,
      boolean var5,
      boolean var6,
      AbstractStringLayout.Serializer var7,
      AbstractStringLayout.Serializer var8
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, false);
   }

   @Deprecated
   protected AbstractJacksonLayout(
      Configuration var1,
      ObjectWriter var2,
      Charset var3,
      boolean var4,
      boolean var5,
      boolean var6,
      AbstractStringLayout.Serializer var7,
      AbstractStringLayout.Serializer var8,
      boolean var9
   ) {
      this(var1, var2, var3, var4, var5, var6, null, var7, var8, var9, null);
   }

   protected AbstractJacksonLayout(
      Configuration var1,
      ObjectWriter var2,
      Charset var3,
      boolean var4,
      boolean var5,
      boolean var6,
      String var7,
      AbstractStringLayout.Serializer var8,
      AbstractStringLayout.Serializer var9,
      boolean var10,
      KeyValuePair[] var11
   ) {
      super(var1, var3, var8, var9);
      this.objectWriter = var2;
      this.compact = var4;
      this.complete = var5;
      this.eol = var7 != null ? var7 : (var4 && !var6 ? "" : "\r\n");
      this.includeNullDelimiter = var10;
      this.additionalFields = prepareAdditionalFields(var1, var11);
   }

   protected static boolean valueNeedsLookup(String var0) {
      return var0 != null && var0.contains("${");
   }

   private static AbstractJacksonLayout.ResolvableKeyValuePair[] prepareAdditionalFields(Configuration var0, KeyValuePair[] var1) {
      if (var1 != null && var1.length != 0) {
         AbstractJacksonLayout.ResolvableKeyValuePair[] var2 = new AbstractJacksonLayout.ResolvableKeyValuePair[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            AbstractJacksonLayout.ResolvableKeyValuePair var4 = var2[var3] = new AbstractJacksonLayout.ResolvableKeyValuePair(var1[var3]);
            if (var0 == null && var4.valueNeedsLookup) {
               throw new IllegalArgumentException("configuration needs to be set when there are additional fields with variables");
            }
         }

         return var2;
      } else {
         return AbstractJacksonLayout.ResolvableKeyValuePair.EMPTY_ARRAY;
      }
   }

   public String toSerializable(LogEvent var1) {
      StringBuilderWriter var2 = new StringBuilderWriter();

      try {
         this.toSerializable(var1, var2);
         return var2.toString();
      } catch (IOException var4) {
         LOGGER.error(var4);
         return "";
      }
   }

   private static LogEvent convertMutableToLog4jEvent(LogEvent var0) {
      return var0 instanceof Log4jLogEvent ? var0 : Log4jLogEvent.createMemento(var0);
   }

   protected Object wrapLogEvent(LogEvent var1) {
      if (this.additionalFields.length > 0) {
         Map var2 = this.resolveAdditionalFields(var1);
         return new AbstractJacksonLayout.LogEventWithAdditionalFields(var1, var2);
      } else {
         return var1 instanceof Message ? new AbstractJacksonLayout.ReadOnlyLogEventWrapper(var1) : var1;
      }
   }

   private Map<String, String> resolveAdditionalFields(LogEvent var1) {
      LinkedHashMap var2 = new LinkedHashMap(this.additionalFields.length);
      StrSubstitutor var3 = this.configuration.getStrSubstitutor();

      for (AbstractJacksonLayout.ResolvableKeyValuePair var7 : this.additionalFields) {
         if (var7.valueNeedsLookup) {
            var2.put(var7.key, var3.replace(var1, var7.value));
         } else {
            var2.put(var7.key, var7.value);
         }
      }

      return var2;
   }

   public void toSerializable(LogEvent var1, Writer var2) throws JsonGenerationException, JsonMappingException, IOException {
      this.objectWriter.writeValue(var2, this.wrapLogEvent(convertMutableToLog4jEvent(var1)));
      var2.write(this.eol);
      if (this.includeNullDelimiter) {
         var2.write(0);
      }

      this.markEvent();
   }

   public abstract static class Builder<B extends AbstractJacksonLayout.Builder<B>> extends AbstractStringLayout.Builder<B> {
      @PluginBuilderAttribute
      private boolean eventEol;
      @PluginBuilderAttribute
      private String endOfLine;
      @PluginBuilderAttribute
      private boolean compact;
      @PluginBuilderAttribute
      private boolean complete;
      @PluginBuilderAttribute
      private boolean locationInfo;
      @PluginBuilderAttribute
      private boolean properties;
      @PluginBuilderAttribute
      private boolean includeStacktrace = true;
      @PluginBuilderAttribute
      private boolean stacktraceAsString = false;
      @PluginBuilderAttribute
      private boolean includeNullDelimiter = false;
      @PluginBuilderAttribute
      private boolean includeTimeMillis = false;
      @PluginElement("AdditionalField")
      private KeyValuePair[] additionalFields;

      protected String toStringOrNull(byte[] var1) {
         return var1 == null ? null : new String(var1, Charset.defaultCharset());
      }

      public boolean getEventEol() {
         return this.eventEol;
      }

      public String getEndOfLine() {
         return this.endOfLine;
      }

      public boolean isCompact() {
         return this.compact;
      }

      public boolean isComplete() {
         return this.complete;
      }

      public boolean isLocationInfo() {
         return this.locationInfo;
      }

      public boolean isProperties() {
         return this.properties;
      }

      public boolean isIncludeStacktrace() {
         return this.includeStacktrace;
      }

      public boolean isStacktraceAsString() {
         return this.stacktraceAsString;
      }

      public boolean isIncludeNullDelimiter() {
         return this.includeNullDelimiter;
      }

      public boolean isIncludeTimeMillis() {
         return this.includeTimeMillis;
      }

      public KeyValuePair[] getAdditionalFields() {
         return this.additionalFields;
      }

      public B setEventEol(boolean var1) {
         this.eventEol = var1;
         return this.asBuilder();
      }

      public B setEndOfLine(String var1) {
         this.endOfLine = var1;
         return this.asBuilder();
      }

      public B setCompact(boolean var1) {
         this.compact = var1;
         return this.asBuilder();
      }

      public B setComplete(boolean var1) {
         this.complete = var1;
         return this.asBuilder();
      }

      public B setLocationInfo(boolean var1) {
         this.locationInfo = var1;
         return this.asBuilder();
      }

      public B setProperties(boolean var1) {
         this.properties = var1;
         return this.asBuilder();
      }

      public B setIncludeStacktrace(boolean var1) {
         this.includeStacktrace = var1;
         return this.asBuilder();
      }

      public B setStacktraceAsString(boolean var1) {
         this.stacktraceAsString = var1;
         return this.asBuilder();
      }

      public B setIncludeNullDelimiter(boolean var1) {
         this.includeNullDelimiter = var1;
         return this.asBuilder();
      }

      public B setIncludeTimeMillis(boolean var1) {
         this.includeTimeMillis = var1;
         return this.asBuilder();
      }

      public B setAdditionalFields(KeyValuePair[] var1) {
         this.additionalFields = var1;
         return this.asBuilder();
      }
   }

   @JsonRootName("Event")
   @JacksonXmlRootElement(
      namespace = "http://logging.apache.org/log4j/2.0/events",
      localName = "Event"
   )
   public static class LogEventWithAdditionalFields {
      private final Object logEvent;
      private final Map<String, String> additionalFields;

      public LogEventWithAdditionalFields(Object var1, Map<String, String> var2) {
         this.logEvent = var1;
         this.additionalFields = var2;
      }

      @JsonUnwrapped
      public Object getLogEvent() {
         return this.logEvent;
      }

      @JsonAnyGetter
      public Map<String, String> getAdditionalFields() {
         return this.additionalFields;
      }
   }

   private static class ReadOnlyLogEventWrapper implements LogEvent {
      @JsonIgnore
      private final LogEvent event;

      public ReadOnlyLogEventWrapper(LogEvent var1) {
         this.event = var1;
      }

      @Override
      public LogEvent toImmutable() {
         return this.event.toImmutable();
      }

      @Override
      public Map<String, String> getContextMap() {
         return this.event.getContextMap();
      }

      @Override
      public ReadOnlyStringMap getContextData() {
         return this.event.getContextData();
      }

      @Override
      public ThreadContext.ContextStack getContextStack() {
         return this.event.getContextStack();
      }

      @Override
      public String getLoggerFqcn() {
         return this.event.getLoggerFqcn();
      }

      @Override
      public Level getLevel() {
         return this.event.getLevel();
      }

      @Override
      public String getLoggerName() {
         return this.event.getLoggerName();
      }

      @Override
      public Marker getMarker() {
         return this.event.getMarker();
      }

      @Override
      public Message getMessage() {
         return this.event.getMessage();
      }

      @Override
      public long getTimeMillis() {
         return this.event.getTimeMillis();
      }

      @Override
      public Instant getInstant() {
         return this.event.getInstant();
      }

      @Override
      public StackTraceElement getSource() {
         return this.event.getSource();
      }

      @Override
      public String getThreadName() {
         return this.event.getThreadName();
      }

      @Override
      public long getThreadId() {
         return this.event.getThreadId();
      }

      @Override
      public int getThreadPriority() {
         return this.event.getThreadPriority();
      }

      @Override
      public Throwable getThrown() {
         return this.event.getThrown();
      }

      @Override
      public ThrowableProxy getThrownProxy() {
         return this.event.getThrownProxy();
      }

      @Override
      public boolean isEndOfBatch() {
         return this.event.isEndOfBatch();
      }

      @Override
      public boolean isIncludeLocation() {
         return this.event.isIncludeLocation();
      }

      @Override
      public void setEndOfBatch(boolean var1) {
      }

      @Override
      public void setIncludeLocation(boolean var1) {
      }

      @Override
      public long getNanoTime() {
         return this.event.getNanoTime();
      }
   }

   protected static class ResolvableKeyValuePair {
      static final AbstractJacksonLayout.ResolvableKeyValuePair[] EMPTY_ARRAY = new AbstractJacksonLayout.ResolvableKeyValuePair[0];
      final String key;
      final String value;
      final boolean valueNeedsLookup;

      ResolvableKeyValuePair(KeyValuePair var1) {
         this.key = var1.getKey();
         this.value = var1.getValue();
         this.valueNeedsLookup = AbstractJacksonLayout.valueNeedsLookup(this.value);
      }
   }
}
