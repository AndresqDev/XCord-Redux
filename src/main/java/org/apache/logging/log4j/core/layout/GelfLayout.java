package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.layout.internal.ExcludeChecker;
import org.apache.logging.log4j.core.layout.internal.IncludeChecker;
import org.apache.logging.log4j.core.layout.internal.ListChecker;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Severity;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.TriConsumer;

@Plugin(
   name = "GelfLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class GelfLayout extends AbstractStringLayout {
   private static final char C = ',';
   private static final int COMPRESSION_THRESHOLD = 1024;
   private static final char Q = '"';
   private static final String QC = "\",";
   private static final String QU = "\"_";
   private final KeyValuePair[] additionalFields;
   private final int compressionThreshold;
   private final GelfLayout.CompressionType compressionType;
   private final String host;
   private final boolean includeStacktrace;
   private final boolean includeThreadContext;
   private final boolean includeMapMessage;
   private final boolean includeNullDelimiter;
   private final boolean includeNewLineDelimiter;
   private final boolean omitEmptyFields;
   private final PatternLayout layout;
   private final GelfLayout.FieldWriter mdcWriter;
   private final GelfLayout.FieldWriter mapWriter;
   private static final ThreadLocal<StringBuilder> messageStringBuilder = new ThreadLocal<>();
   private static final ThreadLocal<StringBuilder> timestampStringBuilder = new ThreadLocal<>();

   @Deprecated
   public GelfLayout(String var1, KeyValuePair[] var2, GelfLayout.CompressionType var3, int var4, boolean var5) {
      this(null, var1, var2, var3, var4, var5, true, true, false, false, false, null, null, null, "", "");
   }

   private GelfLayout(
      Configuration var1,
      String var2,
      KeyValuePair[] var3,
      GelfLayout.CompressionType var4,
      int var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      ListChecker var12,
      ListChecker var13,
      PatternLayout var14,
      String var15,
      String var16
   ) {
      super(var1, StandardCharsets.UTF_8, null, null);
      this.host = var2 != null ? var2 : NetUtils.getLocalHostname();
      this.additionalFields = var3 != null ? var3 : KeyValuePair.EMPTY_ARRAY;
      if (var1 == null) {
         for (KeyValuePair var20 : this.additionalFields) {
            if (valueNeedsLookup(var20.getValue())) {
               throw new IllegalArgumentException("configuration needs to be set when there are additional fields with variables");
            }
         }
      }

      this.compressionType = var4;
      this.compressionThreshold = var5;
      this.includeStacktrace = var6;
      this.includeThreadContext = var7;
      this.includeMapMessage = var8;
      this.includeNullDelimiter = var9;
      this.includeNewLineDelimiter = var10;
      this.omitEmptyFields = var11;
      if (var9 && var4 != GelfLayout.CompressionType.OFF) {
         throw new IllegalArgumentException("null delimiter cannot be used with compression");
      } else {
         this.mdcWriter = new GelfLayout.FieldWriter(var12, var15);
         this.mapWriter = new GelfLayout.FieldWriter(var13, var16);
         this.layout = var14;
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("host=").append(this.host);
      var1.append(", compressionType=").append(this.compressionType.toString());
      var1.append(", compressionThreshold=").append(this.compressionThreshold);
      var1.append(", includeStackTrace=").append(this.includeStacktrace);
      var1.append(", includeThreadContext=").append(this.includeThreadContext);
      var1.append(", includeNullDelimiter=").append(this.includeNullDelimiter);
      var1.append(", includeNewLineDelimiter=").append(this.includeNewLineDelimiter);
      String var2 = this.mdcWriter.getChecker().toString();
      if (var2.length() > 0) {
         var1.append(", ").append(var2);
      }

      String var3 = this.mapWriter.getChecker().toString();
      if (var3.length() > 0) {
         var1.append(", ").append(var3);
      }

      if (this.layout != null) {
         var1.append(", PatternLayout{").append(this.layout.toString()).append("}");
      }

      return var1.toString();
   }

   @Deprecated
   public static GelfLayout createLayout(
      @PluginAttribute("host") String var0,
      @PluginElement("AdditionalField") KeyValuePair[] var1,
      @PluginAttribute(value = "compressionType",defaultString = "GZIP") GelfLayout.CompressionType var2,
      @PluginAttribute(value = "compressionThreshold",defaultInt = 1024) int var3,
      @PluginAttribute(value = "includeStacktrace",defaultBoolean = true) boolean var4
   ) {
      return new GelfLayout(null, var0, var1, var2, var3, var4, true, true, false, false, false, null, null, null, "", "");
   }

   @PluginBuilderFactory
   public static <B extends GelfLayout.Builder<B>> B newBuilder() {
      return new GelfLayout.Builder<B>().asBuilder();
   }

   @Override
   public Map<String, String> getContentFormat() {
      return Collections.emptyMap();
   }

   @Override
   public String getContentType() {
      return "application/json; charset=" + this.getCharset();
   }

   @Override
   public byte[] toByteArray(LogEvent var1) {
      StringBuilder var2 = this.toText(var1, getStringBuilder(), false);
      byte[] var3 = this.getBytes(var2.toString());
      return this.compressionType != GelfLayout.CompressionType.OFF && var3.length > this.compressionThreshold ? this.compress(var3) : var3;
   }

   @Override
   public void encode(LogEvent var1, ByteBufferDestination var2) {
      if (this.compressionType != GelfLayout.CompressionType.OFF) {
         super.encode(var1, var2);
      } else {
         StringBuilder var3 = this.toText(var1, getStringBuilder(), true);
         Encoder var4 = this.getStringBuilderEncoder();
         var4.encode(var3, var2);
      }
   }

   @Override
   public boolean requiresLocation() {
      return Objects.nonNull(this.layout) && this.layout.requiresLocation();
   }

   private byte[] compress(byte[] var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream(this.compressionThreshold / 8);

         byte[] var5;
         try (DeflaterOutputStream var3 = this.compressionType.createDeflaterOutputStream(var2)) {
            if (var3 != null) {
               var3.write(var1);
               var3.finish();
               return var2.toByteArray();
            }

            var5 = var1;
         }

         return var5;
      } catch (IOException var18) {
         StatusLogger.getLogger().error(var18);
         return var1;
      }
   }

   public String toSerializable(LogEvent var1) {
      StringBuilder var2 = this.toText(var1, getStringBuilder(), false);
      return var2.toString();
   }

   private StringBuilder toText(LogEvent var1, StringBuilder var2, boolean var3) {
      var2.append('{');
      var2.append("\"version\":\"1.1\",");
      var2.append("\"host\":\"");
      JsonUtils.quoteAsString(toNullSafeString(this.host), var2);
      var2.append("\",");
      var2.append("\"timestamp\":").append(formatTimestamp(var1.getTimeMillis())).append(',');
      var2.append("\"level\":").append(this.formatLevel(var1.getLevel())).append(',');
      if (var1.getThreadName() != null) {
         var2.append("\"_thread\":\"");
         JsonUtils.quoteAsString(var1.getThreadName(), var2);
         var2.append("\",");
      }

      if (var1.getLoggerName() != null) {
         var2.append("\"_logger\":\"");
         JsonUtils.quoteAsString(var1.getLoggerName(), var2);
         var2.append("\",");
      }

      if (this.additionalFields.length > 0) {
         StrSubstitutor var4 = this.getConfiguration().getStrSubstitutor();

         for (KeyValuePair var8 : this.additionalFields) {
            String var9 = valueNeedsLookup(var8.getValue()) ? var4.replace(var1, var8.getValue()) : var8.getValue();
            if (Strings.isNotEmpty(var9) || !this.omitEmptyFields) {
               var2.append("\"_");
               JsonUtils.quoteAsString(var8.getKey(), var2);
               var2.append("\":\"");
               JsonUtils.quoteAsString(toNullSafeString(var9), var2);
               var2.append("\",");
            }
         }
      }

      if (this.includeThreadContext) {
         var1.getContextData().forEach(this.mdcWriter, var2);
      }

      if (this.includeMapMessage && var1.getMessage() instanceof MapMessage) {
         ((MapMessage)var1.getMessage()).forEach((var2x, var3x) -> this.mapWriter.accept(var2x, var3x, var2));
      }

      if (var1.getThrown() != null || this.layout != null) {
         var2.append("\"full_message\":\"");
         if (this.layout != null) {
            StringBuilder var13 = getMessageStringBuilder();
            this.layout.serialize(var1, var13);
            JsonUtils.quoteAsString(var13, var2);
         } else if (this.includeStacktrace) {
            JsonUtils.quoteAsString(formatThrowable(var1.getThrown()), var2);
         } else {
            JsonUtils.quoteAsString(var1.getThrown().toString(), var2);
         }

         var2.append("\",");
      }

      var2.append("\"short_message\":\"");
      Message var14 = var1.getMessage();
      if (var14 instanceof CharSequence) {
         JsonUtils.quoteAsString((CharSequence)var14, var2);
      } else if (var3 && var14 instanceof StringBuilderFormattable) {
         StringBuilder var15 = getMessageStringBuilder();

         try {
            ((StringBuilderFormattable)var14).formatTo(var15);
            JsonUtils.quoteAsString(var15, var2);
         } finally {
            trimToMaxSize(var15);
         }
      } else {
         JsonUtils.quoteAsString(toNullSafeString(var14.getFormattedMessage()), var2);
      }

      var2.append('"');
      var2.append('}');
      if (this.includeNullDelimiter) {
         var2.append('\u0000');
      }

      if (this.includeNewLineDelimiter) {
         var2.append('\n');
      }

      return var2;
   }

   private static boolean valueNeedsLookup(String var0) {
      return var0 != null && var0.contains("${");
   }

   private static StringBuilder getMessageStringBuilder() {
      StringBuilder var0 = messageStringBuilder.get();
      if (var0 == null) {
         var0 = new StringBuilder(1024);
         messageStringBuilder.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   private static CharSequence toNullSafeString(CharSequence var0) {
      return (CharSequence)(var0 == null ? "" : var0);
   }

   static CharSequence formatTimestamp(long var0) {
      if (var0 < 1000L) {
         return "0";
      } else {
         StringBuilder var2 = getTimestampStringBuilder();
         var2.append(var0);
         var2.insert(var2.length() - 3, '.');
         return var2;
      }
   }

   private static StringBuilder getTimestampStringBuilder() {
      StringBuilder var0 = timestampStringBuilder.get();
      if (var0 == null) {
         var0 = new StringBuilder(20);
         timestampStringBuilder.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   private int formatLevel(Level var1) {
      return Severity.getSeverity(var1).getCode();
   }

   static CharSequence formatThrowable(Throwable var0) {
      StringWriter var1 = new StringWriter(2048);
      PrintWriter var2 = new PrintWriter(var1);
      var0.printStackTrace(var2);
      var2.flush();
      return var1.getBuffer();
   }

   public static class Builder<B extends GelfLayout.Builder<B>>
      extends AbstractStringLayout.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<GelfLayout> {
      @PluginBuilderAttribute
      private String host;
      @PluginElement("AdditionalField")
      private KeyValuePair[] additionalFields;
      @PluginBuilderAttribute
      private GelfLayout.CompressionType compressionType = GelfLayout.CompressionType.GZIP;
      @PluginBuilderAttribute
      private int compressionThreshold = 1024;
      @PluginBuilderAttribute
      private boolean includeStacktrace = true;
      @PluginBuilderAttribute
      private boolean includeThreadContext = true;
      @PluginBuilderAttribute
      private boolean includeNullDelimiter = false;
      @PluginBuilderAttribute
      private boolean includeNewLineDelimiter = false;
      @PluginBuilderAttribute
      private String threadContextIncludes = null;
      @PluginBuilderAttribute
      private String threadContextExcludes = null;
      @PluginBuilderAttribute
      private String mapMessageIncludes = null;
      @PluginBuilderAttribute
      private String mapMessageExcludes = null;
      @PluginBuilderAttribute
      private boolean includeMapMessage = true;
      @PluginBuilderAttribute
      private boolean omitEmptyFields = false;
      @PluginBuilderAttribute
      private String messagePattern = null;
      @PluginBuilderAttribute
      private String threadContextPrefix = "";
      @PluginBuilderAttribute
      private String mapPrefix = "";
      @PluginElement("PatternSelector")
      private PatternSelector patternSelector = null;

      public Builder() {
         this.setCharset(StandardCharsets.UTF_8);
      }

      public GelfLayout build() {
         ListChecker var1 = this.createChecker(this.threadContextExcludes, this.threadContextIncludes);
         ListChecker var2 = this.createChecker(this.mapMessageExcludes, this.mapMessageIncludes);
         PatternLayout var3 = null;
         if (this.messagePattern != null && this.patternSelector != null) {
            AbstractLayout.LOGGER.error("A message pattern and PatternSelector cannot both be specified on GelfLayout, ignoring message pattern");
            this.messagePattern = null;
         }

         if (this.messagePattern != null) {
            var3 = PatternLayout.newBuilder()
               .withPattern(this.messagePattern)
               .withAlwaysWriteExceptions(this.includeStacktrace)
               .withConfiguration(this.getConfiguration())
               .build();
         }

         if (this.patternSelector != null) {
            var3 = PatternLayout.newBuilder()
               .withPatternSelector(this.patternSelector)
               .withAlwaysWriteExceptions(this.includeStacktrace)
               .withConfiguration(this.getConfiguration())
               .build();
         }

         return new GelfLayout(
            this.getConfiguration(),
            this.host,
            this.additionalFields,
            this.compressionType,
            this.compressionThreshold,
            this.includeStacktrace,
            this.includeThreadContext,
            this.includeMapMessage,
            this.includeNullDelimiter,
            this.includeNewLineDelimiter,
            this.omitEmptyFields,
            var1,
            var2,
            var3,
            this.threadContextPrefix,
            this.mapPrefix
         );
      }

      private ListChecker createChecker(String var1, String var2) {
         Object var3 = null;
         if (var1 != null) {
            String[] var4 = var1.split(Patterns.COMMA_SEPARATOR);
            if (var4.length > 0) {
               ArrayList var5 = new ArrayList(var4.length);

               for (String var9 : var4) {
                  var5.add(var9.trim());
               }

               var3 = new ExcludeChecker(var5);
            }
         }

         if (var2 != null) {
            String[] var10 = var2.split(Patterns.COMMA_SEPARATOR);
            if (var10.length > 0) {
               ArrayList var11 = new ArrayList(var10.length);

               for (String var15 : var10) {
                  var11.add(var15.trim());
               }

               var3 = new IncludeChecker(var11);
            }
         }

         if (var3 == null) {
            var3 = ListChecker.NOOP_CHECKER;
         }

         return (ListChecker)var3;
      }

      public String getHost() {
         return this.host;
      }

      public GelfLayout.CompressionType getCompressionType() {
         return this.compressionType;
      }

      public int getCompressionThreshold() {
         return this.compressionThreshold;
      }

      public boolean isIncludeStacktrace() {
         return this.includeStacktrace;
      }

      public boolean isIncludeThreadContext() {
         return this.includeThreadContext;
      }

      public boolean isIncludeNullDelimiter() {
         return this.includeNullDelimiter;
      }

      public boolean isIncludeNewLineDelimiter() {
         return this.includeNewLineDelimiter;
      }

      public KeyValuePair[] getAdditionalFields() {
         return this.additionalFields;
      }

      public B setHost(String var1) {
         this.host = var1;
         return this.asBuilder();
      }

      public B setCompressionType(GelfLayout.CompressionType var1) {
         this.compressionType = var1;
         return this.asBuilder();
      }

      public B setCompressionThreshold(int var1) {
         this.compressionThreshold = var1;
         return this.asBuilder();
      }

      public B setIncludeStacktrace(boolean var1) {
         this.includeStacktrace = var1;
         return this.asBuilder();
      }

      public B setIncludeThreadContext(boolean var1) {
         this.includeThreadContext = var1;
         return this.asBuilder();
      }

      public B setIncludeNullDelimiter(boolean var1) {
         this.includeNullDelimiter = var1;
         return this.asBuilder();
      }

      public B setIncludeNewLineDelimiter(boolean var1) {
         this.includeNewLineDelimiter = var1;
         return this.asBuilder();
      }

      public B setAdditionalFields(KeyValuePair[] var1) {
         this.additionalFields = var1;
         return this.asBuilder();
      }

      public B setMessagePattern(String var1) {
         this.messagePattern = var1;
         return this.asBuilder();
      }

      public B setPatternSelector(PatternSelector var1) {
         this.patternSelector = var1;
         return this.asBuilder();
      }

      public B setMdcIncludes(String var1) {
         this.threadContextIncludes = var1;
         return this.asBuilder();
      }

      public B setMdcExcludes(String var1) {
         this.threadContextExcludes = var1;
         return this.asBuilder();
      }

      public B setIncludeMapMessage(boolean var1) {
         this.includeMapMessage = var1;
         return this.asBuilder();
      }

      public B setMapMessageIncludes(String var1) {
         this.mapMessageIncludes = var1;
         return this.asBuilder();
      }

      public B setMapMessageExcludes(String var1) {
         this.mapMessageExcludes = var1;
         return this.asBuilder();
      }

      public B setThreadContextPrefix(String var1) {
         if (var1 != null) {
            this.threadContextPrefix = var1;
         }

         return this.asBuilder();
      }

      public B setMapPrefix(String var1) {
         if (var1 != null) {
            this.mapPrefix = var1;
         }

         return this.asBuilder();
      }
   }

   public static enum CompressionType {
      GZIP {
         @Override
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return new GZIPOutputStream(var1);
         }
      },
      ZLIB {
         @Override
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return new DeflaterOutputStream(var1);
         }
      },
      OFF {
         @Override
         public DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException {
            return null;
         }
      };

      private CompressionType() {
      }

      public abstract DeflaterOutputStream createDeflaterOutputStream(OutputStream var1) throws IOException;
   }

   private class FieldWriter implements TriConsumer<String, Object, StringBuilder> {
      private final ListChecker checker;
      private final String prefix;

      FieldWriter(ListChecker var2, String var3) {
         this.checker = var2;
         this.prefix = var3;
      }

      public void accept(String var1, Object var2, StringBuilder var3) {
         String var4 = String.valueOf(var2);
         if (this.checker.check(var1) && (Strings.isNotEmpty(var4) || !GelfLayout.this.omitEmptyFields)) {
            var3.append("\"_");
            JsonUtils.quoteAsString(Strings.concat(this.prefix, var1), var3);
            var3.append("\":\"");
            JsonUtils.quoteAsString(GelfLayout.toNullSafeString(var4), var3);
            var3.append("\",");
         }
      }

      public ListChecker getChecker() {
         return this.checker;
      }
   }
}
