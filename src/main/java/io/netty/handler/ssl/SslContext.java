package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.internal.EmptyArrays;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
   static final String ALIAS = "key";
   static final CertificateFactory X509_CERT_FACTORY;
   private final boolean startTls;
   private final AttributeMap attributes = new DefaultAttributeMap();

   public static SslProvider defaultServerProvider() {
      return defaultProvider();
   }

   public static SslProvider defaultClientProvider() {
      return defaultProvider();
   }

   private static SslProvider defaultProvider() {
      return OpenSsl.isAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
   }

   @Deprecated
   public static SslContext newServerContext(File var0, File var1) throws SSLException {
      return newServerContext(var0, var1, null);
   }

   @Deprecated
   public static SslContext newServerContext(File var0, File var1, String var2) throws SSLException {
      return newServerContext(null, var0, var1, var2);
   }

   @Deprecated
   public static SslContext newServerContext(File var0, File var1, String var2, Iterable<String> var3, Iterable<String> var4, long var5, long var7) throws SSLException {
      return newServerContext(null, var0, var1, var2, var3, var4, var5, var7);
   }

   @Deprecated
   public static SslContext newServerContext(
      File var0, File var1, String var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, long var6, long var8
   ) throws SSLException {
      return newServerContext(null, var0, var1, var2, var3, var4, var5, var6, var8);
   }

   @Deprecated
   public static SslContext newServerContext(SslProvider var0, File var1, File var2) throws SSLException {
      return newServerContext(var0, var1, var2, null);
   }

   @Deprecated
   public static SslContext newServerContext(SslProvider var0, File var1, File var2, String var3) throws SSLException {
      return newServerContext(var0, var1, var2, var3, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider var0, File var1, File var2, String var3, Iterable<String> var4, Iterable<String> var5, long var6, long var8
   ) throws SSLException {
      return newServerContext(var0, var1, var2, var3, var4, IdentityCipherSuiteFilter.INSTANCE, toApplicationProtocolConfig(var5), var6, var8);
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider var0, File var1, File var2, String var3, TrustManagerFactory var4, Iterable<String> var5, Iterable<String> var6, long var7, long var9
   ) throws SSLException {
      return newServerContext(var0, null, var4, var1, var2, var3, null, var5, IdentityCipherSuiteFilter.INSTANCE, toApplicationProtocolConfig(var6), var7, var9);
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider var0, File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, ApplicationProtocolConfig var6, long var7, long var9
   ) throws SSLException {
      return newServerContext(var0, null, null, var1, var2, var3, null, var4, var5, var6, var7, var9, KeyStore.getDefaultType());
   }

   @Deprecated
   public static SslContext newServerContext(
      SslProvider var0,
      File var1,
      TrustManagerFactory var2,
      File var3,
      File var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      ApplicationProtocolConfig var9,
      long var10,
      long var12
   ) throws SSLException {
      return newServerContext(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var12, KeyStore.getDefaultType());
   }

   static SslContext newServerContext(
      SslProvider var0,
      File var1,
      TrustManagerFactory var2,
      File var3,
      File var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      ApplicationProtocolConfig var9,
      long var10,
      long var12,
      String var14
   ) throws SSLException {
      try {
         return newServerContextInternal(
            var0,
            null,
            toX509Certificates(var1),
            var2,
            toX509Certificates(var3),
            toPrivateKey(var4, var5),
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            var12,
            ClientAuth.NONE,
            null,
            false,
            false,
            var14
         );
      } catch (Exception var16) {
         if (var16 instanceof SSLException) {
            throw (SSLException)var16;
         } else {
            throw new SSLException("failed to initialize the server-side SSL context", var16);
         }
      }
   }

   static SslContext newServerContextInternal(
      SslProvider var0,
      Provider var1,
      X509Certificate[] var2,
      TrustManagerFactory var3,
      X509Certificate[] var4,
      PrivateKey var5,
      String var6,
      KeyManagerFactory var7,
      Iterable<String> var8,
      CipherSuiteFilter var9,
      ApplicationProtocolConfig var10,
      long var11,
      long var13,
      ClientAuth var15,
      String[] var16,
      boolean var17,
      boolean var18,
      String var19,
      Entry<SslContextOption<?>, Object>... var20
   ) throws SSLException {
      if (var0 == null) {
         var0 = defaultServerProvider();
      }

      switch (var0) {
         case JDK:
            if (var18) {
               throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + var0);
            }

            return new JdkSslServerContext(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var13, var15, var16, var17, var19);
         case OPENSSL:
            verifyNullSslContextProvider(var0, var1);
            return new OpenSslServerContext(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var13, var15, var16, var17, var18, var19, var20);
         case OPENSSL_REFCNT:
            verifyNullSslContextProvider(var0, var1);
            return new ReferenceCountedOpenSslServerContext(
               var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var13, var15, var16, var17, var18, var19, var20
            );
         default:
            throw new Error(var0.toString());
      }
   }

   private static void verifyNullSslContextProvider(SslProvider var0, Provider var1) {
      if (var1 != null) {
         throw new IllegalArgumentException("Java Security Provider unsupported for SslProvider: " + var0);
      }
   }

   @Deprecated
   public static SslContext newClientContext() throws SSLException {
      return newClientContext(null, null, null);
   }

   @Deprecated
   public static SslContext newClientContext(File var0) throws SSLException {
      return newClientContext(null, var0);
   }

   @Deprecated
   public static SslContext newClientContext(TrustManagerFactory var0) throws SSLException {
      return newClientContext(null, null, var0);
   }

   @Deprecated
   public static SslContext newClientContext(File var0, TrustManagerFactory var1) throws SSLException {
      return newClientContext(null, var0, var1);
   }

   @Deprecated
   public static SslContext newClientContext(File var0, TrustManagerFactory var1, Iterable<String> var2, Iterable<String> var3, long var4, long var6) throws SSLException {
      return newClientContext(null, var0, var1, var2, var3, var4, var6);
   }

   @Deprecated
   public static SslContext newClientContext(
      File var0, TrustManagerFactory var1, Iterable<String> var2, CipherSuiteFilter var3, ApplicationProtocolConfig var4, long var5, long var7
   ) throws SSLException {
      return newClientContext(null, var0, var1, var2, var3, var4, var5, var7);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider var0) throws SSLException {
      return newClientContext(var0, null, null);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider var0, File var1) throws SSLException {
      return newClientContext(var0, var1, null);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider var0, TrustManagerFactory var1) throws SSLException {
      return newClientContext(var0, null, var1);
   }

   @Deprecated
   public static SslContext newClientContext(SslProvider var0, File var1, TrustManagerFactory var2) throws SSLException {
      return newClientContext(var0, var1, var2, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider var0, File var1, TrustManagerFactory var2, Iterable<String> var3, Iterable<String> var4, long var5, long var7
   ) throws SSLException {
      return newClientContext(var0, var1, var2, null, null, null, null, var3, IdentityCipherSuiteFilter.INSTANCE, toApplicationProtocolConfig(var4), var5, var7);
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider var0,
      File var1,
      TrustManagerFactory var2,
      Iterable<String> var3,
      CipherSuiteFilter var4,
      ApplicationProtocolConfig var5,
      long var6,
      long var8
   ) throws SSLException {
      return newClientContext(var0, var1, var2, null, null, null, null, var3, var4, var5, var6, var8);
   }

   @Deprecated
   public static SslContext newClientContext(
      SslProvider var0,
      File var1,
      TrustManagerFactory var2,
      File var3,
      File var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      ApplicationProtocolConfig var9,
      long var10,
      long var12
   ) throws SSLException {
      try {
         return newClientContextInternal(
            var0,
            null,
            toX509Certificates(var1),
            var2,
            toX509Certificates(var3),
            toPrivateKey(var4, var5),
            var5,
            var6,
            var7,
            var8,
            var9,
            null,
            var10,
            var12,
            false,
            KeyStore.getDefaultType()
         );
      } catch (Exception var15) {
         if (var15 instanceof SSLException) {
            throw (SSLException)var15;
         } else {
            throw new SSLException("failed to initialize the client-side SSL context", var15);
         }
      }
   }

   static SslContext newClientContextInternal(
      SslProvider var0,
      Provider var1,
      X509Certificate[] var2,
      TrustManagerFactory var3,
      X509Certificate[] var4,
      PrivateKey var5,
      String var6,
      KeyManagerFactory var7,
      Iterable<String> var8,
      CipherSuiteFilter var9,
      ApplicationProtocolConfig var10,
      String[] var11,
      long var12,
      long var14,
      boolean var16,
      String var17,
      Entry<SslContextOption<?>, Object>... var18
   ) throws SSLException {
      if (var0 == null) {
         var0 = defaultClientProvider();
      }

      switch (var0) {
         case JDK:
            if (var16) {
               throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + var0);
            }

            return new JdkSslClientContext(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var14, var17);
         case OPENSSL:
            verifyNullSslContextProvider(var0, var1);
            OpenSsl.ensureAvailability();
            return new OpenSslClientContext(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var14, var16, var17, var18);
         case OPENSSL_REFCNT:
            verifyNullSslContextProvider(var0, var1);
            OpenSsl.ensureAvailability();
            return new ReferenceCountedOpenSslClientContext(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var14, var16, var17, var18);
         default:
            throw new Error(var0.toString());
      }
   }

   static ApplicationProtocolConfig toApplicationProtocolConfig(Iterable<String> var0) {
      ApplicationProtocolConfig var1;
      if (var0 == null) {
         var1 = ApplicationProtocolConfig.DISABLED;
      } else {
         var1 = new ApplicationProtocolConfig(
            ApplicationProtocolConfig.Protocol.NPN_AND_ALPN,
            ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL,
            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
            var0
         );
      }

      return var1;
   }

   protected SslContext() {
      this(false);
   }

   protected SslContext(boolean var1) {
      this.startTls = var1;
   }

   public final AttributeMap attributes() {
      return this.attributes;
   }

   public final boolean isServer() {
      return !this.isClient();
   }

   public abstract boolean isClient();

   public abstract List<String> cipherSuites();

   public long sessionCacheSize() {
      return (long)this.sessionContext().getSessionCacheSize();
   }

   public long sessionTimeout() {
      return (long)this.sessionContext().getSessionTimeout();
   }

   @Deprecated
   public final List<String> nextProtocols() {
      return this.applicationProtocolNegotiator().protocols();
   }

   public abstract ApplicationProtocolNegotiator applicationProtocolNegotiator();

   public abstract SSLEngine newEngine(ByteBufAllocator var1);

   public abstract SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3);

   public abstract SSLSessionContext sessionContext();

   public final SslHandler newHandler(ByteBufAllocator var1) {
      return this.newHandler(var1, this.startTls);
   }

   protected SslHandler newHandler(ByteBufAllocator var1, boolean var2) {
      return new SslHandler(this.newEngine(var1), var2);
   }

   public SslHandler newHandler(ByteBufAllocator var1, Executor var2) {
      return this.newHandler(var1, this.startTls, var2);
   }

   protected SslHandler newHandler(ByteBufAllocator var1, boolean var2, Executor var3) {
      return new SslHandler(this.newEngine(var1), var2, var3);
   }

   public final SslHandler newHandler(ByteBufAllocator var1, String var2, int var3) {
      return this.newHandler(var1, var2, var3, this.startTls);
   }

   protected SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4) {
      return new SslHandler(this.newEngine(var1, var2, var3), var4);
   }

   public SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, Executor var4) {
      return this.newHandler(var1, var2, var3, this.startTls, var4);
   }

   protected SslHandler newHandler(ByteBufAllocator var1, String var2, int var3, boolean var4, Executor var5) {
      return new SslHandler(this.newEngine(var1, var2, var3), var4, var5);
   }

   @Deprecated
   protected static PKCS8EncodedKeySpec generateKeySpec(char[] var0, byte[] var1) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
      if (var0 == null) {
         return new PKCS8EncodedKeySpec(var1);
      } else {
         EncryptedPrivateKeyInfo var2 = new EncryptedPrivateKeyInfo(var1);
         SecretKeyFactory var3 = SecretKeyFactory.getInstance(var2.getAlgName());
         PBEKeySpec var4 = new PBEKeySpec(var0);
         SecretKey var5 = var3.generateSecret(var4);
         Cipher var6 = Cipher.getInstance(var2.getAlgName());
         var6.init(2, var5, var2.getAlgParameters());
         return var2.getKeySpec(var6);
      }
   }

   protected static KeyStore buildKeyStore(X509Certificate[] var0, PrivateKey var1, char[] var2, String var3) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
      if (var3 == null) {
         var3 = KeyStore.getDefaultType();
      }

      KeyStore var4 = KeyStore.getInstance(var3);
      var4.load(null, null);
      var4.setKeyEntry("key", var1, var2, var0);
      return var4;
   }

   protected static PrivateKey toPrivateKey(File var0, String var1) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      if (var0 == null) {
         return null;
      } else {
         if (BouncyCastlePemReader.isAvailable()) {
            PrivateKey var2 = BouncyCastlePemReader.getPrivateKey(var0, var1);
            if (var2 != null) {
               return var2;
            }
         }

         return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(var0), var1);
      }
   }

   protected static PrivateKey toPrivateKey(InputStream var0, String var1) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      if (var0 == null) {
         return null;
      } else {
         if (BouncyCastlePemReader.isAvailable()) {
            PrivateKey var2 = BouncyCastlePemReader.getPrivateKey(var0, var1);
            if (var2 != null) {
               return var2;
            }
         }

         return getPrivateKeyFromByteBuffer(PemReader.readPrivateKey(var0), var1);
      }
   }

   private static PrivateKey getPrivateKeyFromByteBuffer(ByteBuf var0, String var1) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
      byte[] var2 = new byte[var0.readableBytes()];
      var0.readBytes(var2).release();
      PKCS8EncodedKeySpec var3 = generateKeySpec(var1 == null ? null : var1.toCharArray(), var2);

      try {
         return KeyFactory.getInstance("RSA").generatePrivate(var3);
      } catch (InvalidKeySpecException var9) {
         try {
            return KeyFactory.getInstance("DSA").generatePrivate(var3);
         } catch (InvalidKeySpecException var8) {
            try {
               return KeyFactory.getInstance("EC").generatePrivate(var3);
            } catch (InvalidKeySpecException var7) {
               throw new InvalidKeySpecException("Neither RSA, DSA nor EC worked", var7);
            }
         }
      }
   }

   @Deprecated
   protected static TrustManagerFactory buildTrustManagerFactory(File var0, TrustManagerFactory var1) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      return buildTrustManagerFactory(var0, var1, null);
   }

   protected static TrustManagerFactory buildTrustManagerFactory(File var0, TrustManagerFactory var1, String var2) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      X509Certificate[] var3 = toX509Certificates(var0);
      return buildTrustManagerFactory(var3, var1, var2);
   }

   protected static X509Certificate[] toX509Certificates(File var0) throws CertificateException {
      return var0 == null ? null : getCertificatesFromBuffers(PemReader.readCertificates(var0));
   }

   protected static X509Certificate[] toX509Certificates(InputStream var0) throws CertificateException {
      return var0 == null ? null : getCertificatesFromBuffers(PemReader.readCertificates(var0));
   }

   private static X509Certificate[] getCertificatesFromBuffers(ByteBuf[] var0) throws CertificateException {
      CertificateFactory var1 = CertificateFactory.getInstance("X.509");
      X509Certificate[] var2 = new X509Certificate[var0.length];
      boolean var17 = false /* VF: Semaphore variable */;

      try {
         var17 = true;

         for (int var3 = 0; var3 < var0.length; var3++) {
            ByteBufInputStream var4 = new ByteBufInputStream(var0[var3], false);

            try {
               var2[var3] = (X509Certificate)var1.generateCertificate(var4);
            } finally {
               try {
                  var4.close();
               } catch (IOException var22) {
                  throw new RuntimeException(var22);
               }
            }
         }

         var17 = false;
      } finally {
         if (var17) {
            for (ByteBuf var12 : var0) {
               var12.release();
            }
         }
      }

      for (ByteBuf var6 : var0) {
         var6.release();
      }

      return var2;
   }

   protected static TrustManagerFactory buildTrustManagerFactory(X509Certificate[] var0, TrustManagerFactory var1, String var2) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      if (var2 == null) {
         var2 = KeyStore.getDefaultType();
      }

      KeyStore var3 = KeyStore.getInstance(var2);
      var3.load(null, null);
      int var4 = 1;

      for (X509Certificate var8 : var0) {
         String var9 = Integer.toString(var4);
         var3.setCertificateEntry(var9, var8);
         var4++;
      }

      if (var1 == null) {
         var1 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      }

      var1.init(var3);
      return var1;
   }

   static PrivateKey toPrivateKeyInternal(File var0, String var1) throws SSLException {
      try {
         return toPrivateKey(var0, var1);
      } catch (Exception var3) {
         throw new SSLException(var3);
      }
   }

   static X509Certificate[] toX509CertificatesInternal(File var0) throws SSLException {
      try {
         return toX509Certificates(var0);
      } catch (CertificateException var2) {
         throw new SSLException(var2);
      }
   }

   protected static KeyManagerFactory buildKeyManagerFactory(
      X509Certificate[] var0, String var1, PrivateKey var2, String var3, KeyManagerFactory var4, String var5
   ) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
      if (var1 == null) {
         var1 = KeyManagerFactory.getDefaultAlgorithm();
      }

      char[] var6 = keyStorePassword(var3);
      KeyStore var7 = buildKeyStore(var0, var2, var6, var5);
      return buildKeyManagerFactory(var7, var1, var6, var4);
   }

   static KeyManagerFactory buildKeyManagerFactory(KeyStore var0, String var1, char[] var2, KeyManagerFactory var3) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      if (var3 == null) {
         if (var1 == null) {
            var1 = KeyManagerFactory.getDefaultAlgorithm();
         }

         var3 = KeyManagerFactory.getInstance(var1);
      }

      var3.init(var0, var2);
      return var3;
   }

   static char[] keyStorePassword(String var0) {
      return var0 == null ? EmptyArrays.EMPTY_CHARS : var0.toCharArray();
   }

   static {
      try {
         X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var1) {
         throw new IllegalStateException("unable to instance X.509 CertificateFactory", var1);
      }
   }
}
