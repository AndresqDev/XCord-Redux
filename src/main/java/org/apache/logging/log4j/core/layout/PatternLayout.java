package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "PatternLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class PatternLayout extends AbstractStringLayout {
   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %notEmpty{%x }- %m%n";
   public static final String SIMPLE_CONVERSION_PATTERN = "%d [%t] %p %c - %m%n";
   public static final String KEY = "Converter";
   private final String conversionPattern;
   private final PatternSelector patternSelector;
   private final AbstractStringLayout.Serializer eventSerializer;

   private PatternLayout(
      Configuration var1,
      RegexReplacement var2,
      String var3,
      PatternSelector var4,
      Charset var5,
      boolean var6,
      boolean var7,
      boolean var8,
      String var9,
      String var10
   ) {
      super(
         var1,
         var5,
         newSerializerBuilder()
            .setConfiguration(var1)
            .setReplace(var2)
            .setPatternSelector(var4)
            .setAlwaysWriteExceptions(var6)
            .setDisableAnsi(var7)
            .setNoConsoleNoAnsi(var8)
            .setPattern(var9)
            .build(),
         newSerializerBuilder()
            .setConfiguration(var1)
            .setReplace(var2)
            .setPatternSelector(var4)
            .setAlwaysWriteExceptions(var6)
            .setDisableAnsi(var7)
            .setNoConsoleNoAnsi(var8)
            .setPattern(var10)
            .build()
      );
      this.conversionPattern = var3;
      this.patternSelector = var4;
      this.eventSerializer = newSerializerBuilder()
         .setConfiguration(var1)
         .setReplace(var2)
         .setPatternSelector(var4)
         .setAlwaysWriteExceptions(var6)
         .setDisableAnsi(var7)
         .setNoConsoleNoAnsi(var8)
         .setPattern(var3)
         .setDefaultPattern("%m%n")
         .build();
   }

   public static PatternLayout.SerializerBuilder newSerializerBuilder() {
      return new PatternLayout.SerializerBuilder();
   }

   @Override
   public boolean requiresLocation() {
      return this.eventSerializer instanceof LocationAware && ((LocationAware)this.eventSerializer).requiresLocation();
   }

   @Deprecated
   public static AbstractStringLayout.Serializer createSerializer(
      Configuration var0, RegexReplacement var1, String var2, String var3, PatternSelector var4, boolean var5, boolean var6
   ) {
      PatternLayout.SerializerBuilder var7 = newSerializerBuilder();
      var7.setAlwaysWriteExceptions(var5);
      var7.setConfiguration(var0);
      var7.setDefaultPattern(var3);
      var7.setNoConsoleNoAnsi(var6);
      var7.setPattern(var2);
      var7.setPatternSelector(var4);
      var7.setReplace(var1);
      return var7.build();
   }

   public String getConversionPattern() {
      return this.conversionPattern;
   }

   @Override
   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("structured", "false");
      var1.put("formatType", "conversion");
      var1.put("format", this.conversionPattern);
      return var1;
   }

   public String toSerializable(LogEvent var1) {
      return this.eventSerializer.toSerializable(var1);
   }

   public void serialize(LogEvent var1, StringBuilder var2) {
      this.eventSerializer.toSerializable(var1, var2);
   }

   @Override
   public void encode(LogEvent var1, ByteBufferDestination var2) {
      StringBuilder var3 = this.toText(this.eventSerializer, var1, getStringBuilder());
      Encoder var4 = this.getStringBuilderEncoder();
      var4.encode(var3, var2);
      trimToMaxSize(var3);
   }

   private StringBuilder toText(AbstractStringLayout.Serializer2 var1, LogEvent var2, StringBuilder var3) {
      return var1.toSerializable(var2, var3);
   }

   public static PatternParser createPatternParser(Configuration var0) {
      if (var0 == null) {
         return new PatternParser(var0, "Converter", LogEventPatternConverter.class);
      } else {
         PatternParser var1 = var0.getComponent("Converter");
         if (var1 == null) {
            var1 = new PatternParser(var0, "Converter", LogEventPatternConverter.class);
            var0.addComponent("Converter", var1);
            var1 = var0.getComponent("Converter");
         }

         return var1;
      }
   }

   @Override
   public String toString() {
      return this.patternSelector == null ? this.conversionPattern : this.patternSelector.toString();
   }

   @PluginFactory
   @Deprecated
   public static PatternLayout createLayout(
      @PluginAttribute(value = "pattern",defaultString = "%m%n") String var0,
      @PluginElement("PatternSelector") PatternSelector var1,
      @PluginConfiguration Configuration var2,
      @PluginElement("Replace") RegexReplacement var3,
      @PluginAttribute("charset") Charset var4,
      @PluginAttribute(value = "alwaysWriteExceptions",defaultBoolean = true) boolean var5,
      @PluginAttribute("noConsoleNoAnsi") boolean var6,
      @PluginAttribute("header") String var7,
      @PluginAttribute("footer") String var8
   ) {
      return newBuilder()
         .withPattern(var0)
         .withPatternSelector(var1)
         .withConfiguration(var2)
         .withRegexReplacement(var3)
         .withCharset(var4)
         .withAlwaysWriteExceptions(var5)
         .withNoConsoleNoAnsi(var6)
         .withHeader(var7)
         .withFooter(var8)
         .build();
   }

   public static PatternLayout createDefaultLayout() {
      return newBuilder().build();
   }

   public static PatternLayout createDefaultLayout(Configuration var0) {
      return newBuilder().withConfiguration(var0).build();
   }

   @PluginBuilderFactory
   public static PatternLayout.Builder newBuilder() {
      return new PatternLayout.Builder();
   }

   public AbstractStringLayout.Serializer getEventSerializer() {
      return this.eventSerializer;
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<PatternLayout> {
      @PluginBuilderAttribute
      private String pattern = "%m%n";
      @PluginElement("PatternSelector")
      private PatternSelector patternSelector;
      @PluginConfiguration
      private Configuration configuration;
      @PluginElement("Replace")
      private RegexReplacement regexReplacement;
      @PluginBuilderAttribute
      private Charset charset = Charset.defaultCharset();
      @PluginBuilderAttribute
      private boolean alwaysWriteExceptions = true;
      @PluginBuilderAttribute
      private boolean disableAnsi = !this.useAnsiEscapeCodes();
      @PluginBuilderAttribute
      private boolean noConsoleNoAnsi;
      @PluginBuilderAttribute
      private String header;
      @PluginBuilderAttribute
      private String footer;

      private Builder() {
      }

      private boolean useAnsiEscapeCodes() {
         PropertiesUtil var1 = PropertiesUtil.getProperties();
         boolean var2 = !var1.isOsWindows();
         boolean var3 = !var1.getBooleanProperty("log4j.skipJansi", true);
         return var2 || var3;
      }

      public PatternLayout.Builder withPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternLayout.Builder withPatternSelector(PatternSelector var1) {
         this.patternSelector = var1;
         return this;
      }

      public PatternLayout.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public PatternLayout.Builder withRegexReplacement(RegexReplacement var1) {
         this.regexReplacement = var1;
         return this;
      }

      public PatternLayout.Builder withCharset(Charset var1) {
         if (var1 != null) {
            this.charset = var1;
         }

         return this;
      }

      public PatternLayout.Builder withAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public PatternLayout.Builder withDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public PatternLayout.Builder withNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public PatternLayout.Builder withHeader(String var1) {
         this.header = var1;
         return this;
      }

      public PatternLayout.Builder withFooter(String var1) {
         this.footer = var1;
         return this;
      }

      public PatternLayout build() {
         if (this.configuration == null) {
            this.configuration = new DefaultConfiguration();
         }

         return new PatternLayout(
            this.configuration,
            this.regexReplacement,
            this.pattern,
            this.patternSelector,
            this.charset,
            this.alwaysWriteExceptions,
            this.disableAnsi,
            this.noConsoleNoAnsi,
            this.header,
            this.footer
         );
      }
   }

   private static final class NoFormatPatternSerializer implements PatternLayout.PatternSerializer {
      private final LogEventPatternConverter[] converters;

      private NoFormatPatternSerializer(PatternFormatter[] var1) {
         this.converters = new LogEventPatternConverter[var1.length];

         for (int var2 = 0; var2 < var1.length; var2++) {
            this.converters[var2] = var1[var2].getConverter();
         }
      }

      @Override
      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      @Override
      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         for (LogEventPatternConverter var6 : this.converters) {
            var6.format(var1, var2);
         }

         return var2;
      }

      @Override
      public boolean requiresLocation() {
         for (LogEventPatternConverter var4 : this.converters) {
            if (var4 instanceof LocationAware && ((LocationAware)var4).requiresLocation()) {
               return true;
            }
         }

         return false;
      }

      @Override
      public String toString() {
         return super.toString() + "[converters=" + Arrays.toString((Object[])this.converters) + "]";
      }
   }

   private static final class PatternFormatterPatternSerializer implements PatternLayout.PatternSerializer {
      private final PatternFormatter[] formatters;

      private PatternFormatterPatternSerializer(PatternFormatter[] var1) {
         this.formatters = var1;
      }

      @Override
      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      @Override
      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         for (PatternFormatter var6 : this.formatters) {
            var6.format(var1, var2);
         }

         return var2;
      }

      @Override
      public boolean requiresLocation() {
         for (PatternFormatter var4 : this.formatters) {
            if (var4.requiresLocation()) {
               return true;
            }
         }

         return false;
      }

      @Override
      public String toString() {
         return super.toString() + "[formatters=" + Arrays.toString((Object[])this.formatters) + "]";
      }
   }

   private static final class PatternSelectorSerializer implements LocationAware, AbstractStringLayout.Serializer, AbstractStringLayout.Serializer2 {
      private final PatternSelector patternSelector;
      private final RegexReplacement replace;

      private PatternSelectorSerializer(PatternSelector var1, RegexReplacement var2) {
         this.patternSelector = var1;
         this.replace = var2;
      }

      @Override
      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      @Override
      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         for (PatternFormatter var6 : this.patternSelector.getFormatters(var1)) {
            var6.format(var1, var2);
         }

         if (this.replace != null) {
            String var7 = var2.toString();
            var7 = this.replace.format(var7);
            var2.setLength(0);
            var2.append(var7);
         }

         return var2;
      }

      @Override
      public boolean requiresLocation() {
         return this.patternSelector instanceof LocationAware && ((LocationAware)this.patternSelector).requiresLocation();
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.toString());
         var1.append("[patternSelector=");
         var1.append(this.patternSelector);
         var1.append(", replace=");
         var1.append(this.replace);
         var1.append("]");
         return var1.toString();
      }
   }

   private interface PatternSerializer extends LocationAware, AbstractStringLayout.Serializer, AbstractStringLayout.Serializer2 {
   }

   private static final class PatternSerializerWithReplacement implements LocationAware, AbstractStringLayout.Serializer, AbstractStringLayout.Serializer2 {
      private final PatternLayout.PatternSerializer delegate;
      private final RegexReplacement replace;

      private PatternSerializerWithReplacement(PatternLayout.PatternSerializer var1, RegexReplacement var2) {
         this.delegate = var1;
         this.replace = var2;
      }

      @Override
      public String toSerializable(LogEvent var1) {
         StringBuilder var2 = AbstractStringLayout.getStringBuilder();

         String var3;
         try {
            var3 = this.toSerializable(var1, var2).toString();
         } finally {
            AbstractStringLayout.trimToMaxSize(var2);
         }

         return var3;
      }

      @Override
      public StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         StringBuilder var3 = this.delegate.toSerializable(var1, var2);
         String var4 = var3.toString();
         var4 = this.replace.format(var4);
         var3.setLength(0);
         var3.append(var4);
         return var3;
      }

      @Override
      public boolean requiresLocation() {
         return this.delegate.requiresLocation();
      }

      @Override
      public String toString() {
         return super.toString() + "[delegate=" + this.delegate + ", replace=" + this.replace + "]";
      }
   }

   public static class SerializerBuilder implements org.apache.logging.log4j.core.util.Builder<AbstractStringLayout.Serializer> {
      private Configuration configuration;
      private RegexReplacement replace;
      private String pattern;
      private String defaultPattern;
      private PatternSelector patternSelector;
      private boolean alwaysWriteExceptions;
      private boolean disableAnsi;
      private boolean noConsoleNoAnsi;

      public AbstractStringLayout.Serializer build() {
         if (Strings.isEmpty(this.pattern) && Strings.isEmpty(this.defaultPattern)) {
            return null;
         } else if (this.patternSelector == null) {
            try {
               PatternParser var1 = PatternLayout.createPatternParser(this.configuration);
               List var2 = var1.parse(
                  this.pattern == null ? this.defaultPattern : this.pattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi
               );
               PatternFormatter[] var3 = var2.toArray(PatternFormatter.EMPTY_ARRAY);
               boolean var4 = false;

               for (PatternFormatter var8 : var3) {
                  FormattingInfo var9 = var8.getFormattingInfo();
                  if (var9 != null && var9 != FormattingInfo.getDefault()) {
                     var4 = true;
                     break;
                  }
               }

               Object var11 = var4 ? new PatternLayout.PatternFormatterPatternSerializer(var3) : new PatternLayout.NoFormatPatternSerializer(var3);
               return (AbstractStringLayout.Serializer)(this.replace == null
                  ? var11
                  : new PatternLayout.PatternSerializerWithReplacement((PatternLayout.PatternSerializer)var11, this.replace));
            } catch (RuntimeException var10) {
               throw new IllegalArgumentException("Cannot parse pattern '" + this.pattern + "'", var10);
            }
         } else {
            return new PatternLayout.PatternSelectorSerializer(this.patternSelector, this.replace);
         }
      }

      public PatternLayout.SerializerBuilder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setReplace(RegexReplacement var1) {
         this.replace = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setPatternSelector(PatternSelector var1) {
         this.patternSelector = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public PatternLayout.SerializerBuilder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }
   }
}
