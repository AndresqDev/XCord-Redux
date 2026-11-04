package org.apache.logging.log4j.core.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AbstractWatcher;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.Watcher;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "http",
   category = "Watcher",
   elementType = "watcher",
   printObject = true
)
@PluginAliases({"https"})
public class HttpWatcher extends AbstractWatcher {
   private Logger LOGGER = StatusLogger.getLogger();
   private SslConfiguration sslConfiguration = SslConfigurationFactory.getSslConfiguration();
   private URL url;
   private volatile long lastModifiedMillis;
   private static final int NOT_MODIFIED = 304;
   private static final int OK = 200;
   private static final int BUF_SIZE = 1024;
   private static final String HTTP = "http";
   private static final String HTTPS = "https";

   public HttpWatcher(Configuration var1, Reconfigurable var2, List<ConfigurationListener> var3, long var4) {
      super(var1, var2, var3);
      this.lastModifiedMillis = var4;
   }

   @Override
   public long getLastModified() {
      return this.lastModifiedMillis;
   }

   @Override
   public boolean isModified() {
      return this.refreshConfiguration();
   }

   @Override
   public void watching(Source var1) {
      if (!var1.getURI().getScheme().equals("http") && !var1.getURI().getScheme().equals("https")) {
         throw new IllegalArgumentException("HttpWatcher requires a url using the HTTP or HTTPS protocol, not " + var1.getURI().getScheme());
      } else {
         try {
            this.url = var1.getURI().toURL();
         } catch (MalformedURLException var3) {
            throw new IllegalArgumentException("Invalid URL for HttpWatcher " + var1.getURI(), var3);
         }

         super.watching(var1);
      }
   }

   @Override
   public Watcher newWatcher(Reconfigurable var1, List<ConfigurationListener> var2, long var3) {
      HttpWatcher var5 = new HttpWatcher(this.getConfiguration(), var1, var2, var3);
      if (this.getSource() != null) {
         var5.watching(this.getSource());
      }

      return var5;
   }

   private boolean refreshConfiguration() {
      try {
         HttpURLConnection var1 = UrlConnectionFactory.createConnection(this.url, this.lastModifiedMillis, this.sslConfiguration);
         var1.connect();

         try {
            int var2 = var1.getResponseCode();
            switch (var2) {
               case 200:
                  try (InputStream var3 = var1.getInputStream()) {
                     ConfigurationSource var46 = this.getConfiguration().getConfigurationSource();
                     var46.setData(this.readStream(var3));
                     this.lastModifiedMillis = var1.getLastModified();
                     var46.setModifiedMillis(this.lastModifiedMillis);
                     this.LOGGER.debug("Content was modified for {}", this.url.toString());
                     return true;
                  } catch (IOException var42) {
                     try (InputStream var4 = var1.getErrorStream()) {
                        this.LOGGER.info("Error accessing configuration at {}: {}", this.url, this.readStream(var4));
                     } catch (IOException var40) {
                        this.LOGGER.error("Error accessing configuration at {}: {}", this.url, var42.getMessage());
                     }

                     return false;
                  }
               case 304:
                  this.LOGGER.debug("Configuration Not Modified");
                  return false;
               default:
                  if (var2 < 0) {
                     this.LOGGER.info("Invalid response code returned");
                  } else {
                     this.LOGGER.info("Unexpected response code returned {}", var2);
                  }

                  return false;
            }
         } catch (IOException var43) {
            this.LOGGER.error("Error accessing configuration at {}: {}", this.url, var43.getMessage());
         }
      } catch (IOException var44) {
         this.LOGGER.error("Error connecting to configuration at {}: {}", this.url, var44.getMessage());
      }

      return false;
   }

   private byte[] readStream(InputStream var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      byte[] var3 = new byte[1024];

      int var4;
      while ((var4 = var1.read(var3)) != -1) {
         var2.write(var3, 0, var4);
      }

      return var2.toByteArray();
   }
}
