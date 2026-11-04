package org.apache.logging.log4j.core.layout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractLayout<T extends Serializable> implements Layout<T> {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected final Configuration configuration;
   protected long eventCount;
   protected final byte[] footer;
   protected final byte[] header;

   @Deprecated
   public AbstractLayout(byte[] var1, byte[] var2) {
      this(null, var1, var2);
   }

   public AbstractLayout(Configuration var1, byte[] var2, byte[] var3) {
      this.configuration = var1;
      this.header = var2;
      this.footer = var3;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   @Override
   public Map<String, String> getContentFormat() {
      return new HashMap<>();
   }

   @Override
   public byte[] getFooter() {
      return this.footer;
   }

   @Override
   public byte[] getHeader() {
      return this.header;
   }

   protected void markEvent() {
      this.eventCount++;
   }

   public void encode(LogEvent var1, ByteBufferDestination var2) {
      byte[] var3 = this.toByteArray(var1);
      var2.writeBytes(var3, 0, var3.length);
   }

   public abstract static class Builder<B extends AbstractLayout.Builder<B>> {
      @PluginConfiguration
      private Configuration configuration;
      @PluginBuilderAttribute
      private byte[] footer;
      @PluginBuilderAttribute
      private byte[] header;

      public B asBuilder() {
         return (B)this;
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public byte[] getFooter() {
         return this.footer;
      }

      public byte[] getHeader() {
         return this.header;
      }

      public B setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this.asBuilder();
      }

      public B setFooter(byte[] var1) {
         this.footer = var1;
         return this.asBuilder();
      }

      public B setHeader(byte[] var1) {
         this.header = var1;
         return this.asBuilder();
      }
   }
}
