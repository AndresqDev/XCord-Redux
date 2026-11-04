package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.net.ssl.LaxHostnameVerifier;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;

public class HttpURLConnectionManager extends HttpManager {
   private static final Charset CHARSET = Charset.forName("US-ASCII");
   private final URL url;
   private final boolean isHttps;
   private final String method;
   private final int connectTimeoutMillis;
   private final int readTimeoutMillis;
   private final Property[] headers;
   private final SslConfiguration sslConfiguration;
   private final boolean verifyHostname;

   public HttpURLConnectionManager(
      Configuration var1, LoggerContext var2, String var3, URL var4, String var5, int var6, int var7, Property[] var8, SslConfiguration var9, boolean var10
   ) {
      super(var1, var2, var3);
      this.url = var4;
      if (!var4.getProtocol().equalsIgnoreCase("http") && !var4.getProtocol().equalsIgnoreCase("https")) {
         throw new ConfigurationException("URL must have scheme http or https");
      } else {
         this.isHttps = this.url.getProtocol().equalsIgnoreCase("https");
         this.method = Objects.requireNonNull(var5, "method");
         this.connectTimeoutMillis = var6;
         this.readTimeoutMillis = var7;
         this.headers = var8 != null ? var8 : Property.EMPTY_ARRAY;
         this.sslConfiguration = var9;
         if (this.sslConfiguration != null && !this.isHttps) {
            throw new ConfigurationException("SSL configuration can only be specified with URL scheme https");
         } else {
            this.verifyHostname = var10;
         }
      }
   }

   @Override
   public void send(Layout<?> var1, LogEvent var2) throws IOException {
      HttpURLConnection var3 = (HttpURLConnection)this.url.openConnection();
      var3.setAllowUserInteraction(false);
      var3.setDoOutput(true);
      var3.setDoInput(true);
      var3.setRequestMethod(this.method);
      if (this.connectTimeoutMillis > 0) {
         var3.setConnectTimeout(this.connectTimeoutMillis);
      }

      if (this.readTimeoutMillis > 0) {
         var3.setReadTimeout(this.readTimeoutMillis);
      }

      if (var1.getContentType() != null) {
         var3.setRequestProperty("Content-Type", var1.getContentType());
      }

      for (Property var7 : this.headers) {
         var3.setRequestProperty(
            var7.getName(), var7.isValueNeedsLookup() ? this.getConfiguration().getStrSubstitutor().replace(var2, var7.getValue()) : var7.getValue()
         );
      }

      if (this.sslConfiguration != null) {
         ((HttpsURLConnection)var3).setSSLSocketFactory(this.sslConfiguration.getSslSocketFactory());
      }

      if (this.isHttps && !this.verifyHostname) {
         ((HttpsURLConnection)var3).setHostnameVerifier(LaxHostnameVerifier.INSTANCE);
      }

      byte[] var60 = var1.toByteArray(var2);
      var3.setFixedLengthStreamingMode(var60.length);
      var3.connect();

      try (OutputStream var61 = var3.getOutputStream()) {
         var61.write(var60);
      }

      byte[] var62 = new byte[1024];

      try (InputStream var64 = var3.getInputStream()) {
         while (-1 != var64.read(var62)) {
         }
      } catch (IOException var59) {
         StringBuilder var65 = new StringBuilder();

         try (InputStream var8 = var3.getErrorStream()) {
            var65.append(var3.getResponseCode());
            if (var3.getResponseMessage() != null) {
               var65.append(' ').append(var3.getResponseMessage());
            }

            if (var8 != null) {
               var65.append(" - ");

               int var10;
               while (-1 != (var10 = var8.read(var62))) {
                  var65.append(new String(var62, 0, var10, CHARSET));
               }
            }
         }

         if (var3.getResponseCode() > -1) {
            throw new IOException(var65.toString());
         } else {
            throw var59;
         }
      }
   }
}
