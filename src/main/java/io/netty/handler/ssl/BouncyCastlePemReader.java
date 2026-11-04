package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;

final class BouncyCastlePemReader {
   private static Throwable UNAVAILABILITY_CAUSE;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastlePemReader.class);
   private static Provider bcProvider;
   private static Boolean attemptedLoading = false;

   public static boolean hasAttemptedLoading() {
      return attemptedLoading;
   }

   public static boolean isAvailable() {
      if (!hasAttemptedLoading()) {
         tryLoading();
      }

      return UNAVAILABILITY_CAUSE == null;
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   private static void tryLoading() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            try {
               Class var1 = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider", true, this.getClass().getClassLoader());
               BouncyCastlePemReader.bcProvider = (Provider)var1.getConstructor().newInstance();
               BouncyCastlePemReader.logger.debug("Bouncy Castle provider available");
               BouncyCastlePemReader.attemptedLoading = true;
               return null;
            } catch (Throwable var5) {
               BouncyCastlePemReader.logger.debug("Cannot load Bouncy Castle provider", var5);
               BouncyCastlePemReader.UNAVAILABILITY_CAUSE = var5;
               BouncyCastlePemReader.attemptedLoading = true;
               return null;
            } finally {
               ;
            }
         }
      });
   }

   public static PrivateKey getPrivateKey(InputStream var0, String var1) {
      if (!isAvailable()) {
         if (logger.isDebugEnabled()) {
            logger.debug("Bouncy castle provider is unavailable.", unavailabilityCause());
         }

         return null;
      } else {
         try {
            PEMParser var2 = newParser(var0);
            return getPrivateKey(var2, var1);
         } catch (Exception var3) {
            logger.debug("Unable to extract private key", (Throwable)var3);
            return null;
         }
      }
   }

   public static PrivateKey getPrivateKey(File var0, String var1) {
      if (!isAvailable()) {
         if (logger.isDebugEnabled()) {
            logger.debug("Bouncy castle provider is unavailable.", unavailabilityCause());
         }

         return null;
      } else {
         try {
            PEMParser var2 = newParser(var0);
            return getPrivateKey(var2, var1);
         } catch (Exception var3) {
            logger.debug("Unable to extract private key", (Throwable)var3);
            return null;
         }
      }
   }

   private static JcaPEMKeyConverter newConverter() {
      return new JcaPEMKeyConverter().setProvider(bcProvider);
   }

   private static PrivateKey getPrivateKey(PEMParser var0, String var1) throws IOException, PKCSException, OperatorCreationException {
      PrivateKey var15;
      try {
         Object var2 = var0.readObject();
         if (logger.isDebugEnabled()) {
            logger.debug("Parsed PEM object of type {} and assume key is {}encrypted", var2.getClass().getName(), var1 == null ? "not " : "");
         }

         JcaPEMKeyConverter var3 = newConverter();
         PrivateKey var4 = null;
         if (var1 == null) {
            if (var2 instanceof PrivateKeyInfo) {
               var4 = var3.getPrivateKey((PrivateKeyInfo)var2);
            } else if (var2 instanceof PEMKeyPair) {
               var4 = var3.getKeyPair((PEMKeyPair)var2).getPrivate();
            } else {
               logger.debug("Unable to handle PEM object of type {} as a non encrypted key", var2.getClass());
            }
         } else if (var2 instanceof PEMEncryptedKeyPair) {
            PEMDecryptorProvider var5 = new JcePEMDecryptorProviderBuilder().setProvider(bcProvider).build(var1.toCharArray());
            var4 = var3.getKeyPair(((PEMEncryptedKeyPair)var2).decryptKeyPair(var5)).getPrivate();
         } else if (var2 instanceof PKCS8EncryptedPrivateKeyInfo) {
            InputDecryptorProvider var14 = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider(bcProvider).build(var1.toCharArray());
            var4 = var3.getPrivateKey(((PKCS8EncryptedPrivateKeyInfo)var2).decryptPrivateKeyInfo(var14));
         } else {
            logger.debug("Unable to handle PEM object of type {} as a encrypted key", var2.getClass());
         }

         if (var4 == null && logger.isDebugEnabled()) {
            logger.debug("No key found");
         }

         var15 = var4;
      } finally {
         if (var0 != null) {
            try {
               var0.close();
            } catch (Exception var12) {
               logger.debug("Failed closing pem parser", (Throwable)var12);
            }
         }
      }

      return var15;
   }

   private static PEMParser newParser(File var0) throws FileNotFoundException {
      return new PEMParser(new FileReader(var0));
   }

   private static PEMParser newParser(InputStream var0) {
      return new PEMParser(new InputStreamReader(var0, CharsetUtil.US_ASCII));
   }

   private BouncyCastlePemReader() {
   }
}
