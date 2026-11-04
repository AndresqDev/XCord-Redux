package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidHost;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.core.net.AbstractSocketManager;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.net.DatagramSocketManager;
import org.apache.logging.log4j.core.net.Protocol;
import org.apache.logging.log4j.core.net.SocketOptions;
import org.apache.logging.log4j.core.net.SslSocketManager;
import org.apache.logging.log4j.core.net.TcpSocketManager;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "Socket",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class SocketAppender extends AbstractOutputStreamAppender<AbstractSocketManager> {
   private final Object advertisement;
   private final Advertiser advertiser;

   @PluginBuilderFactory
   public static SocketAppender.Builder newBuilder() {
      return new SocketAppender.Builder();
   }

   protected SocketAppender(
      String var1, Layout<? extends Serializable> var2, Filter var3, AbstractSocketManager var4, boolean var5, boolean var6, Advertiser var7, Property[] var8
   ) {
      super(var1, var2, var3, var5, var6, var8, var4);
      if (var7 != null) {
         HashMap var9 = new HashMap<>(var2.getContentFormat());
         var9.putAll(var4.getContentFormat());
         var9.put("contentType", var2.getContentType());
         var9.put("name", var1);
         this.advertisement = var7.advertise(var9);
      } else {
         this.advertisement = null;
      }

      this.advertiser = var7;
   }

   @Deprecated
   protected SocketAppender(
      String var1, Layout<? extends Serializable> var2, Filter var3, AbstractSocketManager var4, boolean var5, boolean var6, Advertiser var7
   ) {
      this(var1, var2, var3, var4, var5, var6, var7, Property.EMPTY_ARRAY);
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      if (this.advertiser != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      return true;
   }

   @Deprecated
   @PluginFactory
   public static SocketAppender createAppender(
      String var0,
      int var1,
      Protocol var2,
      SslConfiguration var3,
      int var4,
      int var5,
      boolean var6,
      String var7,
      boolean var8,
      boolean var9,
      Layout<? extends Serializable> var10,
      Filter var11,
      boolean var12,
      Configuration var13
   ) {
      return newBuilder()
         .withAdvertise(var12)
         .setConfiguration(var13)
         .withConnectTimeoutMillis(var4)
         .setFilter(var11)
         .withHost(var0)
         .setIgnoreExceptions(var9)
         .withImmediateFail(var6)
         .setLayout(var10)
         .setName(var7)
         .withPort(var1)
         .withProtocol(var2)
         .withReconnectDelayMillis(var5)
         .withSslConfiguration(var3)
         .build();
   }

   @Deprecated
   public static SocketAppender createAppender(
      String var0,
      String var1,
      String var2,
      SslConfiguration var3,
      int var4,
      String var5,
      String var6,
      String var7,
      String var8,
      String var9,
      Layout<? extends Serializable> var10,
      Filter var11,
      String var12,
      Configuration var13
   ) {
      boolean var14 = Booleans.parseBoolean(var8, true);
      boolean var15 = Boolean.parseBoolean(var12);
      boolean var16 = Booleans.parseBoolean(var9, true);
      boolean var17 = Booleans.parseBoolean(var6, true);
      int var18 = AbstractAppender.parseInt(var5, 0);
      int var19 = AbstractAppender.parseInt(var1, 0);
      Protocol var20 = var2 == null ? Protocol.UDP : Protocol.valueOf(var2);
      return createAppender(var0, var19, var20, var3, var4, var18, var17, var7, var14, var16, var10, var11, var15, var13);
   }

   @Deprecated
   protected static AbstractSocketManager createSocketManager(
      String var0, Protocol var1, String var2, int var3, int var4, SslConfiguration var5, int var6, boolean var7, Layout<? extends Serializable> var8, int var9
   ) {
      return createSocketManager(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, null);
   }

   protected static AbstractSocketManager createSocketManager(
      String var0,
      Protocol var1,
      String var2,
      int var3,
      int var4,
      SslConfiguration var5,
      int var6,
      boolean var7,
      Layout<? extends Serializable> var8,
      int var9,
      SocketOptions var10
   ) {
      if (var1 == Protocol.TCP && var5 != null) {
         var1 = Protocol.SSL;
      }

      if (var1 != Protocol.SSL && var5 != null) {
         LOGGER.info("Appender {} ignoring SSL configuration for {} protocol", var0, var1);
      }

      switch (var1) {
         case TCP:
            return TcpSocketManager.getSocketManager(var2, var3, var4, var6, var7, var8, var9, var10);
         case UDP:
            return DatagramSocketManager.getSocketManager(var2, var3, var8, var9);
         case SSL:
            return SslSocketManager.getSocketManager(var5, var2, var3, var4, var6, var7, var8, var9, var10);
         default:
            throw new IllegalArgumentException(var1.toString());
      }
   }

   @Override
   protected void directEncodeEvent(LogEvent var1) {
      this.writeByteArrayToManager(var1);
   }

   public abstract static class AbstractBuilder<B extends SocketAppender.AbstractBuilder<B>> extends AbstractOutputStreamAppender.Builder<B> {
      @PluginBuilderAttribute
      private boolean advertise;
      @PluginBuilderAttribute
      private int connectTimeoutMillis;
      @PluginBuilderAttribute
      @ValidHost
      private String host = "localhost";
      @PluginBuilderAttribute
      private boolean immediateFail = true;
      @PluginBuilderAttribute
      @ValidPort
      private int port;
      @PluginBuilderAttribute
      private Protocol protocol = Protocol.TCP;
      @PluginBuilderAttribute
      @PluginAliases({"reconnectDelay", "reconnectionDelay", "delayMillis", "reconnectionDelayMillis"})
      private int reconnectDelayMillis;
      @PluginElement("SocketOptions")
      private SocketOptions socketOptions;
      @PluginElement("SslConfiguration")
      @PluginAliases({"SslConfig"})
      private SslConfiguration sslConfiguration;

      public boolean getAdvertise() {
         return this.advertise;
      }

      public int getConnectTimeoutMillis() {
         return this.connectTimeoutMillis;
      }

      public String getHost() {
         return this.host;
      }

      public int getPort() {
         return this.port;
      }

      public Protocol getProtocol() {
         return this.protocol;
      }

      public SslConfiguration getSslConfiguration() {
         return this.sslConfiguration;
      }

      public boolean getImmediateFail() {
         return this.immediateFail;
      }

      public B withAdvertise(boolean var1) {
         this.advertise = var1;
         return this.asBuilder();
      }

      public B withConnectTimeoutMillis(int var1) {
         this.connectTimeoutMillis = var1;
         return this.asBuilder();
      }

      public B withHost(String var1) {
         this.host = var1;
         return this.asBuilder();
      }

      public B withImmediateFail(boolean var1) {
         this.immediateFail = var1;
         return this.asBuilder();
      }

      public B withPort(int var1) {
         this.port = var1;
         return this.asBuilder();
      }

      public B withProtocol(Protocol var1) {
         this.protocol = var1;
         return this.asBuilder();
      }

      public B withReconnectDelayMillis(int var1) {
         this.reconnectDelayMillis = var1;
         return this.asBuilder();
      }

      public B withSocketOptions(SocketOptions var1) {
         this.socketOptions = var1;
         return this.asBuilder();
      }

      public B withSslConfiguration(SslConfiguration var1) {
         this.sslConfiguration = var1;
         return this.asBuilder();
      }

      public int getReconnectDelayMillis() {
         return this.reconnectDelayMillis;
      }

      public SocketOptions getSocketOptions() {
         return this.socketOptions;
      }
   }

   public static class Builder
      extends SocketAppender.AbstractBuilder<SocketAppender.Builder>
      implements org.apache.logging.log4j.core.util.Builder<SocketAppender> {
      public SocketAppender build() {
         boolean var1 = this.isImmediateFlush();
         boolean var2 = this.isBufferedIo();
         Layout var3 = this.getLayout();
         if (var3 == null) {
            SocketAppender.LOGGER.error("No layout provided for SocketAppender");
            return null;
         } else {
            String var4 = this.getName();
            if (var4 == null) {
               SocketAppender.LOGGER.error("No name provided for SocketAppender");
               return null;
            } else {
               Protocol var5 = this.getProtocol();
               Protocol var6 = var5 != null ? var5 : Protocol.TCP;
               if (var6 == Protocol.UDP) {
                  var1 = true;
               }

               AbstractSocketManager var7 = SocketAppender.createSocketManager(
                  var4,
                  var6,
                  this.getHost(),
                  this.getPort(),
                  this.getConnectTimeoutMillis(),
                  this.getSslConfiguration(),
                  this.getReconnectDelayMillis(),
                  this.getImmediateFail(),
                  var3,
                  this.getBufferSize(),
                  this.getSocketOptions()
               );
               return new SocketAppender(
                  var4,
                  var3,
                  this.getFilter(),
                  var7,
                  this.isIgnoreExceptions(),
                  !var2 || var1,
                  this.getAdvertise() ? this.getConfiguration().getAdvertiser() : null,
                  this.getPropertyArray()
               );
            }
         }
      }
   }
}
