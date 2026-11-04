package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SslConfigurationFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static SslConfiguration sslConfiguration = null;
   private static final String trustStorelocation = "log4j2.trustStoreLocation";
   private static final String trustStorePassword = "log4j2.trustStorePassword";
   private static final String trustStorePasswordFile = "log4j2.trustStorePasswordFile";
   private static final String trustStorePasswordEnvVar = "log4j2.trustStorePasswordEnvironmentVariable";
   private static final String trustStoreKeyStoreType = "log4j2.trustStoreKeyStoreType";
   private static final String trustStoreKeyManagerFactoryAlgorithm = "log4j2.trustStoreKeyManagerFactoryAlgorithm";
   private static final String keyStoreLocation = "log4j2.keyStoreLocation";
   private static final String keyStorePassword = "log4j2.keyStorePassword";
   private static final String keyStorePasswordFile = "log4j2.keyStorePasswordFile";
   private static final String keyStorePasswordEnvVar = "log4j2.keyStorePasswordEnvironmentVariable";
   private static final String keyStoreType = "log4j2.keyStoreType";
   private static final String keyStoreKeyManagerFactoryAlgorithm = "log4j2.keyStoreKeyManagerFactoryAlgorithm";
   private static final String verifyHostName = "log4j2.sslVerifyHostName";

   public static SslConfiguration getSslConfiguration() {
      return sslConfiguration;
   }

   static {
      PropertiesUtil var0 = PropertiesUtil.getProperties();
      KeyStoreConfiguration var1 = null;
      TrustStoreConfiguration var2 = null;
      String var3 = var0.getStringProperty("log4j2.trustStoreLocation");
      if (var3 != null) {
         String var4 = var0.getStringProperty("log4j2.trustStorePassword");
         char[] var5 = null;
         if (var4 != null) {
            var5 = var4.toCharArray();
         }

         try {
            var2 = TrustStoreConfiguration.createKeyStoreConfiguration(
               var3,
               var5,
               var0.getStringProperty("log4j2.trustStorePasswordEnvironmentVariable"),
               var0.getStringProperty("log4j2.trustStorePasswordFile"),
               var0.getStringProperty("log4j2.trustStoreKeyStoreType"),
               var0.getStringProperty("log4j2.trustStoreKeyManagerFactoryAlgorithm")
            );
         } catch (Exception var8) {
            LOGGER.warn("Unable to create trust store configuration due to: {} {}", var8.getClass().getName(), var8.getMessage());
         }
      }

      var3 = var0.getStringProperty("log4j2.keyStoreLocation");
      if (var3 != null) {
         String var10 = var0.getStringProperty("log4j2.keyStorePassword");
         char[] var12 = null;
         if (var10 != null) {
            var12 = var10.toCharArray();
         }

         try {
            var1 = KeyStoreConfiguration.createKeyStoreConfiguration(
               var3,
               var12,
               var0.getStringProperty("log4j2.keyStorePasswordEnvironmentVariable"),
               var0.getStringProperty("log4j2.keyStorePasswordFile"),
               var0.getStringProperty("log4j2.keyStoreType"),
               var0.getStringProperty("log4j2.keyStoreKeyManagerFactoryAlgorithm")
            );
         } catch (Exception var7) {
            LOGGER.warn("Unable to create key store configuration due to: {} {}", var7.getClass().getName(), var7.getMessage());
         }
      }

      if (var2 != null || var1 != null) {
         boolean var11 = var0.getBooleanProperty("log4j2.sslVerifyHostName", false);
         sslConfiguration = SslConfiguration.createSSLConfiguration("https", var1, var2, var11);
      }
   }
}
