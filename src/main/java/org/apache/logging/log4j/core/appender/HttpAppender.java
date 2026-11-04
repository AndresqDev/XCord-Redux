package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;

@Plugin(
   name = "Http",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class HttpAppender extends AbstractAppender {
   private final HttpManager manager;

   @PluginBuilderFactory
   public static <B extends HttpAppender.Builder<B>> B newBuilder() {
      return new HttpAppender.Builder<B>().asBuilder();
   }

   private HttpAppender(String var1, Layout<? extends Serializable> var2, Filter var3, boolean var4, HttpManager var5, Property[] var6) {
      super(var1, var3, var2, var4, var6);
      Objects.requireNonNull(var2, "layout");
      this.manager = Objects.requireNonNull(var5, "manager");
   }

   @Override
   public void start() {
      super.start();
      this.manager.startup();
   }

   @Override
   public void append(LogEvent var1) {
      try {
         this.manager.send(this.getLayout(), var1);
      } catch (Exception var3) {
         this.error("Unable to send HTTP in appender [" + this.getName() + "]", var1, var3);
      }
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   @Override
   public String toString() {
      return "HttpAppender{name=" + this.getName() + ", state=" + this.getState() + '}';
   }

   public static class Builder<B extends HttpAppender.Builder<B>>
      extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<HttpAppender> {
      @PluginBuilderAttribute
      @Required(
         message = "No URL provided for HttpAppender"
      )
      private URL url;
      @PluginBuilderAttribute
      private String method = "POST";
      @PluginBuilderAttribute
      private int connectTimeoutMillis = 0;
      @PluginBuilderAttribute
      private int readTimeoutMillis = 0;
      @PluginElement("Headers")
      private Property[] headers;
      @PluginElement("SslConfiguration")
      private SslConfiguration sslConfiguration;
      @PluginBuilderAttribute
      private boolean verifyHostname = true;

      public HttpAppender build() {
         HttpURLConnectionManager var1 = new HttpURLConnectionManager(
            this.getConfiguration(),
            this.getConfiguration().getLoggerContext(),
            this.getName(),
            this.url,
            this.method,
            this.connectTimeoutMillis,
            this.readTimeoutMillis,
            this.headers,
            this.sslConfiguration,
            this.verifyHostname
         );
         return new HttpAppender(this.getName(), this.getLayout(), this.getFilter(), this.isIgnoreExceptions(), var1, this.getPropertyArray());
      }

      public URL getUrl() {
         return this.url;
      }

      public String getMethod() {
         return this.method;
      }

      public int getConnectTimeoutMillis() {
         return this.connectTimeoutMillis;
      }

      public int getReadTimeoutMillis() {
         return this.readTimeoutMillis;
      }

      public Property[] getHeaders() {
         return this.headers;
      }

      public SslConfiguration getSslConfiguration() {
         return this.sslConfiguration;
      }

      public boolean isVerifyHostname() {
         return this.verifyHostname;
      }

      public B setUrl(URL var1) {
         this.url = var1;
         return this.asBuilder();
      }

      public B setMethod(String var1) {
         this.method = var1;
         return this.asBuilder();
      }

      public B setConnectTimeoutMillis(int var1) {
         this.connectTimeoutMillis = var1;
         return this.asBuilder();
      }

      public B setReadTimeoutMillis(int var1) {
         this.readTimeoutMillis = var1;
         return this.asBuilder();
      }

      public B setHeaders(Property[] var1) {
         this.headers = var1;
         return this.asBuilder();
      }

      public B setSslConfiguration(SslConfiguration var1) {
         this.sslConfiguration = var1;
         return this.asBuilder();
      }

      public B setVerifyHostname(boolean var1) {
         this.verifyHostname = var1;
         return this.asBuilder();
      }
   }
}
