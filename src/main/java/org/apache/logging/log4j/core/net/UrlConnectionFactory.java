package org.apache.logging.log4j.core.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.net.ssl.LaxHostnameVerifier;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AuthorizationProvider;

public class UrlConnectionFactory {
   private static int DEFAULT_TIMEOUT = 60000;
   private static int connectTimeoutMillis = DEFAULT_TIMEOUT;
   private static int readTimeoutMillis = DEFAULT_TIMEOUT;
   private static final String JSON = "application/json";
   private static final String XML = "application/xml";
   private static final String PROPERTIES = "text/x-java-properties";
   private static final String TEXT = "text/plain";
   private static final String HTTP = "http";
   private static final String HTTPS = "https";

   public static HttpURLConnection createConnection(URL var0, long var1, SslConfiguration var3) throws IOException {
      HttpURLConnection var4 = (HttpURLConnection)var0.openConnection();
      AuthorizationProvider var5 = ConfigurationFactory.getAuthorizationProvider();
      if (var5 != null) {
         var5.addAuthorization(var4);
      }

      var4.setAllowUserInteraction(false);
      var4.setDoOutput(true);
      var4.setDoInput(true);
      var4.setRequestMethod("GET");
      if (connectTimeoutMillis > 0) {
         var4.setConnectTimeout(connectTimeoutMillis);
      }

      if (readTimeoutMillis > 0) {
         var4.setReadTimeout(readTimeoutMillis);
      }

      String[] var6 = var0.getFile().split("\\.");
      String var7 = var6[var6.length - 1].trim();
      String var8 = isXml(var7) ? "application/xml" : (isJson(var7) ? "application/json" : (isProperties(var7) ? "text/x-java-properties" : "text/plain"));
      var4.setRequestProperty("Content-Type", var8);
      if (var1 > 0L) {
         var4.setIfModifiedSince(var1);
      }

      if (var0.getProtocol().equals("https") && var3 != null) {
         ((HttpsURLConnection)var4).setSSLSocketFactory(var3.getSslSocketFactory());
         if (!var3.isVerifyHostName()) {
            ((HttpsURLConnection)var4).setHostnameVerifier(LaxHostnameVerifier.INSTANCE);
         }
      }

      return var4;
   }

   public static URLConnection createConnection(URL var0) throws IOException {
      Object var1 = null;
      if (!var0.getProtocol().equals("https") && !var0.getProtocol().equals("http")) {
         var1 = var0.openConnection();
      } else {
         var1 = createConnection(var0, 0L, SslConfigurationFactory.getSslConfiguration());
      }

      return (URLConnection)var1;
   }

   private static boolean isXml(String var0) {
      return var0.equalsIgnoreCase("xml");
   }

   private static boolean isJson(String var0) {
      return var0.equalsIgnoreCase("json") || var0.equalsIgnoreCase("jsn");
   }

   private static boolean isProperties(String var0) {
      return var0.equalsIgnoreCase("properties");
   }
}
