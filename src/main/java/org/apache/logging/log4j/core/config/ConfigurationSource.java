package org.apache.logging.log4j.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.net.ssl.LaxHostnameVerifier;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AuthorizationProvider;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class ConfigurationSource {
   public static final ConfigurationSource NULL_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
   public static final ConfigurationSource COMPOSITE_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
   private static final String HTTPS = "https";
   private final File file;
   private final URL url;
   private final String location;
   private final InputStream stream;
   private volatile byte[] data;
   private volatile Source source;
   private final long lastModified;
   private volatile long modifiedMillis;

   public ConfigurationSource(InputStream var1, File var2) {
      this.stream = Objects.requireNonNull(var1, "stream is null");
      this.file = Objects.requireNonNull(var2, "file is null");
      this.location = var2.getAbsolutePath();
      this.url = null;
      this.data = null;
      long var3 = 0L;

      try {
         var3 = var2.lastModified();
      } catch (Exception var6) {
      }

      this.lastModified = var3;
   }

   public ConfigurationSource(InputStream var1, URL var2) {
      this.stream = Objects.requireNonNull(var1, "stream is null");
      this.url = Objects.requireNonNull(var2, "URL is null");
      this.location = var2.toString();
      this.file = null;
      this.data = null;
      this.lastModified = 0L;
   }

   public ConfigurationSource(InputStream var1, URL var2, long var3) {
      this.stream = Objects.requireNonNull(var1, "stream is null");
      this.url = Objects.requireNonNull(var2, "URL is null");
      this.location = var2.toString();
      this.file = null;
      this.data = null;
      this.lastModified = var3;
   }

   public ConfigurationSource(InputStream var1) throws IOException {
      this(toByteArray(var1), null, 0L);
   }

   public ConfigurationSource(Source var1, byte[] var2, long var3) throws IOException {
      Objects.requireNonNull(var1, "source is null");
      this.data = Objects.requireNonNull(var2, "data is null");
      this.stream = new ByteArrayInputStream(var2);
      this.file = var1.getFile();
      this.url = var1.getURI().toURL();
      this.location = var1.getLocation();
      this.lastModified = var3;
   }

   private ConfigurationSource(byte[] var1, URL var2, long var3) {
      this.data = Objects.requireNonNull(var1, "data is null");
      this.stream = new ByteArrayInputStream(var1);
      this.file = null;
      this.url = var2;
      this.location = null;
      this.lastModified = var3;
      if (var2 == null) {
         this.data = var1;
      }
   }

   private static byte[] toByteArray(InputStream var0) throws IOException {
      int var1 = Math.max(4096, var0.available());
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(var1);
      byte[] var3 = new byte[var1];

      for (int var4 = var0.read(var3); var4 > 0; var4 = var0.read(var3)) {
         var2.write(var3, 0, var4);
      }

      return var2.toByteArray();
   }

   public File getFile() {
      return this.file;
   }

   public URL getURL() {
      return this.url;
   }

   public void setSource(Source var1) {
      this.source = var1;
   }

   public void setData(byte[] var1) {
      this.data = var1;
   }

   public void setModifiedMillis(long var1) {
      this.modifiedMillis = var1;
   }

   public URI getURI() {
      URI var1 = null;
      if (this.url != null) {
         try {
            var1 = this.url.toURI();
         } catch (URISyntaxException var6) {
         }
      }

      if (var1 == null && this.file != null) {
         var1 = this.file.toURI();
      }

      if (var1 == null && this.location != null) {
         try {
            var1 = new URI(this.location);
         } catch (URISyntaxException var5) {
            try {
               var1 = new URI("file://" + this.location);
            } catch (URISyntaxException var4) {
            }
         }
      }

      return var1;
   }

   public long getLastModified() {
      return this.lastModified;
   }

   public String getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.stream;
   }

   public ConfigurationSource resetInputStream() throws IOException {
      if (this.source != null) {
         return new ConfigurationSource(this.source, this.data, this.lastModified);
      } else if (this.file != null) {
         return new ConfigurationSource(new FileInputStream(this.file), this.file);
      } else if (this.url != null && this.data != null) {
         return new ConfigurationSource(this.data, this.url, this.modifiedMillis == 0L ? this.lastModified : this.modifiedMillis);
      } else if (this.url != null) {
         return fromUri(this.getURI());
      } else {
         return this.data != null ? new ConfigurationSource(this.data, null, this.lastModified) : null;
      }
   }

   @Override
   public String toString() {
      if (this.location != null) {
         return this.location;
      } else if (this == NULL_SOURCE) {
         return "NULL_SOURCE";
      } else {
         int var1 = this.data == null ? -1 : this.data.length;
         return "stream (" + var1 + " bytes, unknown location)";
      }
   }

   public static ConfigurationSource fromUri(URI var0) {
      File var1 = FileUtils.fileFromUri(var0);
      if (var1 != null && var1.exists() && var1.canRead()) {
         try {
            return new ConfigurationSource(new FileInputStream(var1), var1);
         } catch (FileNotFoundException var10) {
            ConfigurationFactory.LOGGER.error("Cannot locate file {}", var0.getPath(), var10);
         }
      }

      if (ConfigurationFactory.isClassLoaderUri(var0)) {
         ClassLoader var11 = LoaderUtil.getThreadContextClassLoader();
         String var12 = ConfigurationFactory.extractClassLoaderUriPath(var0);
         return fromResource(var12, var11);
      } else if (!var0.isAbsolute()) {
         ConfigurationFactory.LOGGER.error("File not found in file system or classpath: {}", var0.toString());
         return null;
      } else {
         try {
            URL var2 = var0.toURL();
            URLConnection var3 = UrlConnectionFactory.createConnection(var2);
            InputStream var4 = var3.getInputStream();
            long var5 = var3.getLastModified();
            return new ConfigurationSource(var4, var0.toURL(), var5);
         } catch (FileNotFoundException var7) {
            ConfigurationFactory.LOGGER.warn("Could not locate file {}", var0.toString());
         } catch (MalformedURLException var8) {
            ConfigurationFactory.LOGGER.error("Invalid URL {}", var0.toString(), var8);
         } catch (Exception var9) {
            ConfigurationFactory.LOGGER.error("Unable to access {}", var0.toString(), var9);
         }

         return null;
      }
   }

   public static ConfigurationSource fromResource(String var0, ClassLoader var1) {
      URL var2 = Loader.getResource(var0, var1);
      return var2 == null ? null : getConfigurationSource(var2);
   }

   private static ConfigurationSource getConfigurationSource(URL var0) {
      try {
         URLConnection var1 = var0.openConnection();
         AuthorizationProvider var2 = ConfigurationFactory.authorizationProvider(PropertiesUtil.getProperties());
         var2.addAuthorization(var1);
         if (var0.getProtocol().equals("https")) {
            SslConfiguration var3 = SslConfigurationFactory.getSslConfiguration();
            if (var3 != null) {
               ((HttpsURLConnection)var1).setSSLSocketFactory(var3.getSslSocketFactory());
               if (!var3.isVerifyHostName()) {
                  ((HttpsURLConnection)var1).setHostnameVerifier(LaxHostnameVerifier.INSTANCE);
               }
            }
         }

         File var7 = FileUtils.fileFromUri(var0.toURI());

         try {
            return var7 != null
               ? new ConfigurationSource(var1.getInputStream(), FileUtils.fileFromUri(var0.toURI()))
               : new ConfigurationSource(var1.getInputStream(), var0, var1.getLastModified());
         } catch (FileNotFoundException var5) {
            ConfigurationFactory.LOGGER.info("Unable to locate file {}, ignoring.", var0.toString());
            return null;
         }
      } catch (URISyntaxException | IOException var6) {
         ConfigurationFactory.LOGGER.warn("Error accessing {} due to {}, ignoring.", var0.toString(), var6.getMessage());
         return null;
      }
   }
}
