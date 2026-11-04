package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;

@Plugin(
   name = "YamlLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class YamlLayout extends AbstractJacksonLayout {
   private static final String DEFAULT_FOOTER = "";
   private static final String DEFAULT_HEADER = "";
   static final String CONTENT_TYPE = "application/yaml";

   @Deprecated
   protected YamlLayout(
      Configuration var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, String var7, String var8, Charset var9, boolean var10
   ) {
      super(
         var1,
         new JacksonFactory.YAML(var10, false).newWriter(var2, var3, var5),
         var9,
         var5,
         var4,
         var6,
         null,
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var7).setDefaultPattern("").build(),
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var8).setDefaultPattern("").build(),
         false,
         null
      );
   }

   private YamlLayout(
      Configuration var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      String var7,
      String var8,
      String var9,
      Charset var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      KeyValuePair[] var15
   ) {
      super(
         var1,
         new JacksonFactory.YAML(var11, var12).newWriter(var2, var3, var5, var14),
         var10,
         var5,
         var4,
         var6,
         var7,
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var8).setDefaultPattern("").build(),
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var9).setDefaultPattern("").build(),
         var13,
         var15
      );
   }

   @Override
   public byte[] getHeader() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         String var2 = this.serializeToString(this.getHeaderSerializer());
         if (var2 != null) {
            var1.append(var2);
         }

         var1.append(this.eol);
         return this.getBytes(var1.toString());
      }
   }

   @Override
   public byte[] getFooter() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.eol);
         String var2 = this.serializeToString(this.getFooterSerializer());
         if (var2 != null) {
            var1.append(var2);
         }

         var1.append(this.eol);
         return this.getBytes(var1.toString());
      }
   }

   @Override
   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("version", "2.0");
      return var1;
   }

   @Override
   public String getContentType() {
      return "application/yaml; charset=" + this.getCharset();
   }

   @Deprecated
   public static AbstractJacksonLayout createLayout(Configuration var0, boolean var1, boolean var2, String var3, String var4, Charset var5, boolean var6) {
      return new YamlLayout(var0, var1, var2, false, false, true, null, var3, var4, var5, var6, false, false, false, null);
   }

   @PluginBuilderFactory
   public static <B extends YamlLayout.Builder<B>> B newBuilder() {
      return new YamlLayout.Builder<B>().asBuilder();
   }

   public static AbstractJacksonLayout createDefaultLayout() {
      return new YamlLayout(
         new DefaultConfiguration(), false, false, false, false, false, null, "", "", StandardCharsets.UTF_8, true, false, false, false, null
      );
   }

   public static class Builder<B extends YamlLayout.Builder<B>>
      extends AbstractJacksonLayout.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<YamlLayout> {
      public Builder() {
         this.setCharset(StandardCharsets.UTF_8);
      }

      public YamlLayout build() {
         String var1 = this.toStringOrNull(this.getHeader());
         String var2 = this.toStringOrNull(this.getFooter());
         return new YamlLayout(
            this.getConfiguration(),
            this.isLocationInfo(),
            this.isProperties(),
            this.isComplete(),
            this.isCompact(),
            this.getEventEol(),
            this.getEndOfLine(),
            var1,
            var2,
            this.getCharset(),
            this.isIncludeStacktrace(),
            this.isStacktraceAsString(),
            this.isIncludeNullDelimiter(),
            this.isIncludeTimeMillis(),
            this.getAdditionalFields()
         );
      }
   }
}
