package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.StringEncoder;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StringBuilders;

public abstract class AbstractStringLayout extends AbstractLayout<String> implements StringLayout, LocationAware {
   protected static final int DEFAULT_STRING_BUILDER_SIZE = 1024;
   protected static final int MAX_STRING_BUILDER_SIZE = Math.max(1024, size("log4j.layoutStringBuilder.maxSize", 2048));
   private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal<>();
   private Encoder<StringBuilder> textEncoder;
   private final Charset charset;
   private final AbstractStringLayout.Serializer footerSerializer;
   private final AbstractStringLayout.Serializer headerSerializer;

   @Override
   public boolean requiresLocation() {
      return false;
   }

   protected static StringBuilder getStringBuilder() {
      if (AbstractLogger.getRecursionDepth() > 1) {
         return new StringBuilder(1024);
      } else {
         StringBuilder var0 = threadLocal.get();
         if (var0 == null) {
            var0 = new StringBuilder(1024);
            threadLocal.set(var0);
         }

         trimToMaxSize(var0);
         var0.setLength(0);
         return var0;
      }
   }

   private static int size(String var0, int var1) {
      return PropertiesUtil.getProperties().getIntegerProperty(var0, var1);
   }

   protected static void trimToMaxSize(StringBuilder var0) {
      StringBuilders.trimToMaxSize(var0, MAX_STRING_BUILDER_SIZE);
   }

   protected AbstractStringLayout(Charset var1) {
      this(var1, (byte[])null, (byte[])null);
   }

   protected AbstractStringLayout(Charset var1, byte[] var2, byte[] var3) {
      super(null, var2, var3);
      this.headerSerializer = null;
      this.footerSerializer = null;
      this.charset = var1 == null ? StandardCharsets.UTF_8 : var1;
      this.textEncoder = Constants.ENABLE_DIRECT_ENCODERS ? new StringBuilderEncoder(this.charset) : null;
   }

   protected AbstractStringLayout(Configuration var1, Charset var2, AbstractStringLayout.Serializer var3, AbstractStringLayout.Serializer var4) {
      super(var1, null, null);
      this.headerSerializer = var3;
      this.footerSerializer = var4;
      this.charset = var2 == null ? StandardCharsets.UTF_8 : var2;
      this.textEncoder = Constants.ENABLE_DIRECT_ENCODERS ? new StringBuilderEncoder(this.charset) : null;
   }

   protected byte[] getBytes(String var1) {
      return var1.getBytes(this.charset);
   }

   @Override
   public Charset getCharset() {
      return this.charset;
   }

   @Override
   public String getContentType() {
      return "text/plain";
   }

   @Override
   public byte[] getFooter() {
      return this.serializeToBytes(this.footerSerializer, super.getFooter());
   }

   public AbstractStringLayout.Serializer getFooterSerializer() {
      return this.footerSerializer;
   }

   @Override
   public byte[] getHeader() {
      return this.serializeToBytes(this.headerSerializer, super.getHeader());
   }

   public AbstractStringLayout.Serializer getHeaderSerializer() {
      return this.headerSerializer;
   }

   private DefaultLogEventFactory getLogEventFactory() {
      return DefaultLogEventFactory.getInstance();
   }

   protected Encoder<StringBuilder> getStringBuilderEncoder() {
      if (this.textEncoder == null) {
         this.textEncoder = new StringBuilderEncoder(this.getCharset());
      }

      return this.textEncoder;
   }

   protected byte[] serializeToBytes(AbstractStringLayout.Serializer var1, byte[] var2) {
      String var3 = this.serializeToString(var1);
      return var3 == null ? var2 : StringEncoder.toBytes(var3, this.getCharset());
   }

   protected String serializeToString(AbstractStringLayout.Serializer var1) {
      if (var1 == null) {
         return null;
      } else {
         LoggerConfig var2 = this.getConfiguration().getRootLogger();
         LogEvent var3 = this.getLogEventFactory().createEvent(var2.getName(), null, "", var2.getLevel(), null, null, null);
         return var1.toSerializable(var3);
      }
   }

   @Override
   public byte[] toByteArray(LogEvent var1) {
      return this.getBytes(this.toSerializable(var1));
   }

   public abstract static class Builder<B extends AbstractStringLayout.Builder<B>> extends AbstractLayout.Builder<B> {
      @PluginBuilderAttribute("charset")
      private Charset charset;
      @PluginElement("footerSerializer")
      private AbstractStringLayout.Serializer footerSerializer;
      @PluginElement("headerSerializer")
      private AbstractStringLayout.Serializer headerSerializer;

      public Charset getCharset() {
         return this.charset;
      }

      public AbstractStringLayout.Serializer getFooterSerializer() {
         return this.footerSerializer;
      }

      public AbstractStringLayout.Serializer getHeaderSerializer() {
         return this.headerSerializer;
      }

      public B setCharset(Charset var1) {
         this.charset = var1;
         return this.asBuilder();
      }

      public B setFooterSerializer(AbstractStringLayout.Serializer var1) {
         this.footerSerializer = var1;
         return this.asBuilder();
      }

      public B setHeaderSerializer(AbstractStringLayout.Serializer var1) {
         this.headerSerializer = var1;
         return this.asBuilder();
      }
   }

   public interface Serializer extends AbstractStringLayout.Serializer2 {
      String toSerializable(LogEvent var1);

      @Override
      default StringBuilder toSerializable(LogEvent var1, StringBuilder var2) {
         var2.append(this.toSerializable(var1));
         return var2;
      }
   }

   public interface Serializer2 {
      StringBuilder toSerializable(LogEvent var1, StringBuilder var2);
   }
}
