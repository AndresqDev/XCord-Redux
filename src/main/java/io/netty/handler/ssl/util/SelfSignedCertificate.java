package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class SelfSignedCertificate {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
   private static final Date DEFAULT_NOT_BEFORE = new Date(
      SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 31536000000L)
   );
   private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
   private static final int DEFAULT_KEY_LENGTH_BITS = SystemPropertyUtil.getInt("io.netty.handler.ssl.util.selfSignedKeyStrength", 2048);
   private final File certificate;
   private final File privateKey;
   private final X509Certificate cert;
   private final PrivateKey key;

   public SelfSignedCertificate() throws CertificateException {
      this(DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER, "RSA", DEFAULT_KEY_LENGTH_BITS);
   }

   public SelfSignedCertificate(Date var1, Date var2) throws CertificateException {
      this("localhost", var1, var2, "RSA", DEFAULT_KEY_LENGTH_BITS);
   }

   public SelfSignedCertificate(Date var1, Date var2, String var3, int var4) throws CertificateException {
      this("localhost", var1, var2, var3, var4);
   }

   public SelfSignedCertificate(String var1) throws CertificateException {
      this(var1, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER, "RSA", DEFAULT_KEY_LENGTH_BITS);
   }

   public SelfSignedCertificate(String var1, String var2, int var3) throws CertificateException {
      this(var1, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER, var2, var3);
   }

   public SelfSignedCertificate(String var1, Date var2, Date var3) throws CertificateException {
      this(var1, ThreadLocalInsecureRandom.current(), DEFAULT_KEY_LENGTH_BITS, var2, var3, "RSA");
   }

   public SelfSignedCertificate(String var1, Date var2, Date var3, String var4, int var5) throws CertificateException {
      this(var1, ThreadLocalInsecureRandom.current(), var5, var2, var3, var4);
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, int var3) throws CertificateException {
      this(var1, var2, var3, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER, "RSA");
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, String var3, int var4) throws CertificateException {
      this(var1, var2, var4, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER, var3);
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, int var3, Date var4, Date var5) throws CertificateException {
      this(var1, var2, var3, var4, var5, "RSA");
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, int var3, Date var4, Date var5, String var6) throws CertificateException {
      if (!"EC".equalsIgnoreCase(var6) && !"RSA".equalsIgnoreCase(var6)) {
         throw new IllegalArgumentException("Algorithm not valid: " + var6);
      } else {
         KeyPair var7;
         try {
            KeyPairGenerator var8 = KeyPairGenerator.getInstance(var6);
            var8.initialize(var3, var2);
            var7 = var8.generateKeyPair();
         } catch (NoSuchAlgorithmException var24) {
            throw new Error(var24);
         }

         String[] var27;
         try {
            var27 = BouncyCastleSelfSignedCertGenerator.generate(var1, var7, var2, var4, var5, var6);
         } catch (Throwable var23) {
            logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", var23);

            try {
               var27 = OpenJdkSelfSignedCertGenerator.generate(var1, var7, var2, var4, var5, var6);
            } catch (Throwable var22) {
               logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", var22);
               CertificateException var11 = new CertificateException(
                  "No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", var22
               );
               ThrowableUtil.addSuppressed(var11, var23);
               throw var11;
            }
         }

         this.certificate = new File(var27[0]);
         this.privateKey = new File(var27[1]);
         this.key = var7.getPrivate();
         FileInputStream var9 = null;

         try {
            var9 = new FileInputStream(this.certificate);
            this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(var9);
         } catch (Exception var21) {
            throw new CertificateEncodingException(var21);
         } finally {
            if (var9 != null) {
               try {
                  var9.close();
               } catch (IOException var25) {
                  if (logger.isWarnEnabled()) {
                     logger.warn("Failed to close a file: " + this.certificate, (Throwable)var25);
                  }
               }
            }
         }
      }
   }

   public File certificate() {
      return this.certificate;
   }

   public File privateKey() {
      return this.privateKey;
   }

   public X509Certificate cert() {
      return this.cert;
   }

   public PrivateKey key() {
      return this.key;
   }

   public void delete() {
      safeDelete(this.certificate);
      safeDelete(this.privateKey);
   }

   static String[] newSelfSignedCertificate(String var0, PrivateKey var1, X509Certificate var2) throws IOException, CertificateEncodingException {
      ByteBuf var3 = Unpooled.wrappedBuffer(var1.getEncoded());

      String var5;
      try {
         ByteBuf var4 = Base64.encode(var3, true);

         try {
            var5 = "-----BEGIN PRIVATE KEY-----\n" + var4.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
         } finally {
            var4.release();
         }
      } finally {
         var3.release();
      }

      var0 = var0.replaceAll("[^\\w.-]", "x");
      File var6 = PlatformDependent.createTempFile("keyutil_" + var0 + '_', ".key", null);
      var6.deleteOnExit();
      FileOutputStream var7 = new FileOutputStream(var6);

      try {
         var7.write(var5.getBytes(CharsetUtil.US_ASCII));
         var7.close();
         var7 = null;
      } finally {
         if (var7 != null) {
            safeClose(var6, var7);
            safeDelete(var6);
         }
      }

      var3 = Unpooled.wrappedBuffer(var2.getEncoded());

      String var8;
      try {
         ByteBuf var56 = Base64.encode(var3, true);

         try {
            var8 = "-----BEGIN CERTIFICATE-----\n" + var56.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
         } finally {
            var56.release();
         }
      } finally {
         var3.release();
      }

      File var9 = PlatformDependent.createTempFile("keyutil_" + var0 + '_', ".crt", null);
      var9.deleteOnExit();
      FileOutputStream var10 = new FileOutputStream(var9);

      try {
         var10.write(var8.getBytes(CharsetUtil.US_ASCII));
         var10.close();
         var10 = null;
      } finally {
         if (var10 != null) {
            safeClose(var9, var10);
            safeDelete(var9);
            safeDelete(var6);
         }
      }

      return new String[]{var9.getPath(), var6.getPath()};
   }

   private static void safeDelete(File var0) {
      if (!var0.delete() && logger.isWarnEnabled()) {
         logger.warn("Failed to delete a file: " + var0);
      }
   }

   private static void safeClose(File var0, OutputStream var1) {
      try {
         var1.close();
      } catch (IOException var3) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to close a file: " + var0, (Throwable)var3);
         }
      }
   }
}
