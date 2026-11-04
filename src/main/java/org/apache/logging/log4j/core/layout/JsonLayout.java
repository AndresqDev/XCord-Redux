package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.util.KeyValuePair;

@Plugin(
   name = "JsonLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class JsonLayout extends AbstractJacksonLayout {
   private static final String DEFAULT_FOOTER = "]";
   private static final String DEFAULT_HEADER = "[";
   static final String CONTENT_TYPE = "application/json";

   @Deprecated
   protected JsonLayout(
      Configuration var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      String var8,
      String var9,
      String var10,
      Charset var11,
      boolean var12
   ) {
      super(
         var1,
         new JacksonFactory.JSON(var4, var12, false, false).newWriter(var2, var3, var6),
         var11,
         var6,
         var5,
         var7,
         var8,
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var9).setDefaultPattern("[").build(),
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var10).setDefaultPattern("]").build(),
         false,
         null
      );
   }

   private JsonLayout(
      Configuration var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      String var8,
      String var9,
      String var10,
      Charset var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15,
      KeyValuePair[] var16,
      boolean var17
   ) {
      super(
         var1,
         new JacksonFactory.JSON(var4, var12, var13, var17).newWriter(var2, var3, var6, var15),
         var11,
         var6,
         var5,
         var7,
         var8,
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var9).setDefaultPattern("[").build(),
         PatternLayout.newSerializerBuilder().setConfiguration(var1).setPattern(var10).setDefaultPattern("]").build(),
         var14,
         var16
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
      return "application/json; charset=" + this.getCharset();
   }

   @Deprecated
   public static JsonLayout createLayout(
      Configuration var0,
      boolean var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      String var7,
      String var8,
      Charset var9,
      boolean var10
   ) {
      boolean var11 = var2 && var3;
      return new JsonLayout(var0, var1, var2, var11, var4, var5, var6, null, var7, var8, var9, var10, false, false, false, null, false);
   }

   @PluginBuilderFactory
   public static <B extends JsonLayout.Builder<B>> B newBuilder() {
      return new JsonLayout.Builder<B>().asBuilder();
   }

   public static JsonLayout createDefaultLayout() {
      return new JsonLayout(
         new DefaultConfiguration(), false, false, false, false, false, false, null, "[", "]", StandardCharsets.UTF_8, true, false, false, false, null, false
      );
   }

   @Override
   public void toSerializable(LogEvent var1, Writer var2) throws IOException {
      if (this.complete && this.eventCount > 0L) {
         var2.append(", ");
      }

      super.toSerializable(var1, var2);
   }

   public static class Builder<B extends JsonLayout.Builder<B>>
      extends AbstractJacksonLayout.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<JsonLayout> {
      @PluginBuilderAttribute
      private boolean propertiesAsList;
      @PluginBuilderAttribute
      private boolean objectMessageAsJsonObject;
      @PluginElement("AdditionalField")
      private KeyValuePair[] additionalFields;

      public Builder() {
         this.setCharset(StandardCharsets.UTF_8);
      }

      public JsonLayout build() {
         boolean var1 = this.isProperties() && this.propertiesAsList;
         String var2 = this.toStringOrNull(this.getHeader());
         String var3 = this.toStringOrNull(this.getFooter());
         return new JsonLayout(
            this.getConfiguration(),
            this.isLocationInfo(),
            this.isProperties(),
            var1,
            this.isComplete(),
            this.isCompact(),
            this.getEventEol(),
            this.getEndOfLine(),
            var2,
            var3,
            this.getCharset(),
            this.isIncludeStacktrace(),
            this.isStacktraceAsString(),
            this.isIncludeNullDelimiter(),
            this.isIncludeTimeMillis(),
            this.getAdditionalFields(),
            this.getObjectMessageAsJsonObject()
         );
      }

      public boolean isPropertiesAsList() {
         return this.propertiesAsList;
      }

      public B setPropertiesAsList(boolean var1) {
         this.propertiesAsList = var1;
         return this.asBuilder();
      }

      public boolean getObjectMessageAsJsonObject() {
         return this.objectMessageAsJsonObject;
      }

      public B setObjectMessageAsJsonObject(boolean var1) {
         this.objectMessageAsJsonObject = var1;
         return this.asBuilder();
      }

      @Override
      public KeyValuePair[] getAdditionalFields() {
         return this.additionalFields;
      }

      public B setAdditionalFields(KeyValuePair[] var1) {
         this.additionalFields = var1;
         return this.asBuilder();
      }
   }
}
