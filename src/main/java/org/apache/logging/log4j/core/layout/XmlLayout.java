package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;

@Plugin(
   name = "XmlLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class XmlLayout extends AbstractJacksonLayout {
   private static final String ROOT_TAG = "Events";

   @Deprecated
   protected XmlLayout(boolean var1, boolean var2, boolean var3, boolean var4, Charset var5, boolean var6) {
      this(null, var1, var2, var3, var4, null, var5, var6, false, false, false, null);
   }

   private XmlLayout(
      Configuration var1,
      boolean var2,
      boolean var3,
      boolean var4,
      boolean var5,
      String var6,
      Charset var7,
      boolean var8,
      boolean var9,
      boolean var10,
      boolean var11,
      KeyValuePair[] var12
   ) {
      super(var1, new JacksonFactory.XML(var8, var9).newWriter(var2, var3, var5, var11), var7, var5, var4, false, var6, null, null, var10, var12);
   }

   @Override
   public byte[] getHeader() {
      if (!this.complete) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append("<?xml version=\"1.0\" encoding=\"");
         var1.append(this.getCharset().name());
         var1.append("\"?>");
         var1.append(this.eol);
         var1.append('<');
         var1.append("Events");
         var1.append(" xmlns=\"http://logging.apache.org/log4j/2.0/events\">");
         var1.append(this.eol);
         return var1.toString().getBytes(this.getCharset());
      }
   }

   @Override
   public byte[] getFooter() {
      return !this.complete ? null : this.getBytes("</Events>" + this.eol);
   }

   @Override
   public Map<String, String> getContentFormat() {
      HashMap var1 = new HashMap();
      var1.put("xsd", "log4j-events.xsd");
      var1.put("version", "2.0");
      return var1;
   }

   @Override
   public String getContentType() {
      return "text/xml; charset=" + this.getCharset();
   }

   @Deprecated
   public static XmlLayout createLayout(boolean var0, boolean var1, boolean var2, boolean var3, Charset var4, boolean var5) {
      return new XmlLayout(null, var0, var1, var2, var3, null, var4, var5, false, false, false, null);
   }

   @PluginBuilderFactory
   public static <B extends XmlLayout.Builder<B>> B newBuilder() {
      return new XmlLayout.Builder<B>().asBuilder();
   }

   public static XmlLayout createDefaultLayout() {
      return new XmlLayout(null, false, false, false, false, null, StandardCharsets.UTF_8, true, false, false, false, null);
   }

   public static class Builder<B extends XmlLayout.Builder<B>>
      extends AbstractJacksonLayout.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<XmlLayout> {
      public Builder() {
         this.setCharset(StandardCharsets.UTF_8);
      }

      public XmlLayout build() {
         return new XmlLayout(
            this.getConfiguration(),
            this.isLocationInfo(),
            this.isProperties(),
            this.isComplete(),
            this.isCompact(),
            this.getEndOfLine(),
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
