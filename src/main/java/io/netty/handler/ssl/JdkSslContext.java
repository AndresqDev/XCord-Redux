package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;

public class JdkSslContext extends SslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
   static final String PROTOCOL = "TLS";
   private static final String[] DEFAULT_PROTOCOLS;
   private static final List<String> DEFAULT_CIPHERS;
   private static final List<String> DEFAULT_CIPHERS_NON_TLSV13;
   private static final Set<String> SUPPORTED_CIPHERS;
   private static final Set<String> SUPPORTED_CIPHERS_NON_TLSV13;
   private static final Provider DEFAULT_PROVIDER;
   private final String[] protocols;
   private final String[] cipherSuites;
   private final List<String> unmodifiableCipherSuites;
   private final JdkApplicationProtocolNegotiator apn;
   private final ClientAuth clientAuth;
   private final SSLContext sslContext;
   private final boolean isClient;

   private static String[] defaultProtocols(SSLContext var0, SSLEngine var1) {
      String[] var2 = var0.getDefaultSSLParameters().getProtocols();
      HashSet var3 = new HashSet(var2.length);
      Collections.addAll(var3, var2);
      ArrayList var4 = new ArrayList();
      SslUtils.addIfSupported(var3, var4, "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1");
      return !var4.isEmpty() ? var4.toArray(EmptyArrays.EMPTY_STRINGS) : var1.getEnabledProtocols();
   }

   private static Set<String> supportedCiphers(SSLEngine var0) {
      String[] var1 = var0.getSupportedCipherSuites();
      LinkedHashSet var2 = new LinkedHashSet(var1.length);

      for (int var3 = 0; var3 < var1.length; var3++) {
         String var4 = var1[var3];
         var2.add(var4);
         if (var4.startsWith("SSL_")) {
            String var5 = "TLS_" + var4.substring("SSL_".length());

            try {
               var0.setEnabledCipherSuites(new String[]{var5});
               var2.add(var5);
            } catch (IllegalArgumentException var7) {
            }
         }
      }

      return var2;
   }

   private static List<String> defaultCiphers(SSLEngine var0, Set<String> var1) {
      ArrayList var2 = new ArrayList();
      SslUtils.addIfSupported(var1, var2, SslUtils.DEFAULT_CIPHER_SUITES);
      SslUtils.useFallbackCiphersIfDefaultIsEmpty(var2, var0.getEnabledCipherSuites());
      return var2;
   }

   private static boolean isTlsV13Supported(String[] var0) {
      for (String var4 : var0) {
         if ("TLSv1.3".equals(var4)) {
            return true;
         }
      }

      return false;
   }

   @Deprecated
   public JdkSslContext(SSLContext var1, boolean var2, ClientAuth var3) {
      this(var1, var2, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, var3, null, false);
   }

   @Deprecated
   public JdkSslContext(SSLContext var1, boolean var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, ClientAuth var6) {
      this(var1, var2, var3, var4, var5, var6, null, false);
   }

   public JdkSslContext(
      SSLContext var1,
      boolean var2,
      Iterable<String> var3,
      CipherSuiteFilter var4,
      ApplicationProtocolConfig var5,
      ClientAuth var6,
      String[] var7,
      boolean var8
   ) {
      this(var1, var2, var3, var4, toNegotiator(var5, !var2), var6, var7 == null ? null : (String[])var7.clone(), var8);
   }

   JdkSslContext(
      SSLContext var1,
      boolean var2,
      Iterable<String> var3,
      CipherSuiteFilter var4,
      JdkApplicationProtocolNegotiator var5,
      ClientAuth var6,
      String[] var7,
      boolean var8
   ) {
      super(var8);
      this.apn = ObjectUtil.checkNotNull(var5, "apn");
      this.clientAuth = ObjectUtil.checkNotNull(var6, "clientAuth");
      this.sslContext = ObjectUtil.checkNotNull(var1, "sslContext");
      List var9;
      Set var10;
      if (DEFAULT_PROVIDER.equals(var1.getProvider())) {
         this.protocols = var7 == null ? DEFAULT_PROTOCOLS : var7;
         if (isTlsV13Supported(this.protocols)) {
            var10 = SUPPORTED_CIPHERS;
            var9 = DEFAULT_CIPHERS;
         } else {
            var10 = SUPPORTED_CIPHERS_NON_TLSV13;
            var9 = DEFAULT_CIPHERS_NON_TLSV13;
         }
      } else {
         SSLEngine var11 = var1.createSSLEngine();

         try {
            if (var7 == null) {
               this.protocols = defaultProtocols(var1, var11);
            } else {
               this.protocols = var7;
            }

            var10 = supportedCiphers(var11);
            var9 = defaultCiphers(var11, var10);
            if (!isTlsV13Supported(this.protocols)) {
               for (String var15 : SslUtils.DEFAULT_TLSV13_CIPHER_SUITES) {
                  var10.remove(var15);
                  var9.remove(var15);
               }
            }
         } finally {
            ReferenceCountUtil.release(var11);
         }
      }

      this.cipherSuites = ObjectUtil.checkNotNull(var4, "cipherFilter").filterCipherSuites(var3, var9, var10);
      this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
      this.isClient = var2;
   }

   public final SSLContext context() {
      return this.sslContext;
   }

   @Override
   public final boolean isClient() {
      return this.isClient;
   }

   @Override
   public final SSLSessionContext sessionContext() {
      return this.isServer() ? this.context().getServerSessionContext() : this.context().getClientSessionContext();
   }

   @Override
   public final List<String> cipherSuites() {
      return this.unmodifiableCipherSuites;
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1) {
      return this.configureAndWrapEngine(this.context().createSSLEngine(), var1);
   }

   @Override
   public final SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3) {
      return this.configureAndWrapEngine(this.context().createSSLEngine(var2, var3), var1);
   }

   private SSLEngine configureAndWrapEngine(SSLEngine var1, ByteBufAllocator var2) {
      var1.setEnabledCipherSuites(this.cipherSuites);
      var1.setEnabledProtocols(this.protocols);
      var1.setUseClientMode(this.isClient());
      if (this.isServer()) {
         switch (this.clientAuth) {
            case OPTIONAL:
               var1.setWantClientAuth(true);
               break;
            case REQUIRE:
               var1.setNeedClientAuth(true);
            case NONE:
               break;
            default:
               throw new Error("Unknown auth " + this.clientAuth);
         }
      }

      JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var3 = this.apn.wrapperFactory();
      return var3 instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory
         ? ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)var3).wrapSslEngine(var1, var2, this.apn, this.isServer())
         : var3.wrapSslEngine(var1, this.apn, this.isServer());
   }

   public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.apn;
   }

   static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig var0, boolean var1) {
      if (var0 == null) {
         return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
      } else {
         switch (var0.protocol()) {
            case NONE:
               return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
            case ALPN:
               if (var1) {
                  switch (var0.selectorFailureBehavior()) {
                     case FATAL_ALERT:
                        return new JdkAlpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
                     case NO_ADVERTISE:
                        return new JdkAlpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
                     default:
                        throw new UnsupportedOperationException("JDK provider does not support " + var0.selectorFailureBehavior() + " failure behavior");
                  }
               } else {
                  switch (var0.selectedListenerFailureBehavior()) {
                     case ACCEPT:
                        return new JdkAlpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
                     case FATAL_ALERT:
                        return new JdkAlpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
                     default:
                        throw new UnsupportedOperationException("JDK provider does not support " + var0.selectedListenerFailureBehavior() + " failure behavior");
                  }
               }
            case NPN:
               if (var1) {
                  switch (var0.selectedListenerFailureBehavior()) {
                     case ACCEPT:
                        return new JdkNpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
                     case FATAL_ALERT:
                        return new JdkNpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
                     default:
                        throw new UnsupportedOperationException("JDK provider does not support " + var0.selectedListenerFailureBehavior() + " failure behavior");
                  }
               } else {
                  switch (var0.selectorFailureBehavior()) {
                     case FATAL_ALERT:
                        return new JdkNpnApplicationProtocolNegotiator(true, var0.supportedProtocols());
                     case NO_ADVERTISE:
                        return new JdkNpnApplicationProtocolNegotiator(false, var0.supportedProtocols());
                     default:
                        throw new UnsupportedOperationException("JDK provider does not support " + var0.selectorFailureBehavior() + " failure behavior");
                  }
               }
            default:
               throw new UnsupportedOperationException("JDK provider does not support " + var0.protocol() + " protocol");
         }
      }
   }

   static KeyManagerFactory buildKeyManagerFactory(File var0, File var1, String var2, KeyManagerFactory var3, String var4) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
      String var5 = Security.getProperty("ssl.KeyManagerFactory.algorithm");
      if (var5 == null) {
         var5 = "SunX509";
      }

      return buildKeyManagerFactory(var0, var5, var1, var2, var3, var4);
   }

   @Deprecated
   protected static KeyManagerFactory buildKeyManagerFactory(File var0, File var1, String var2, KeyManagerFactory var3) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
      return buildKeyManagerFactory(var0, var1, var2, var3, KeyStore.getDefaultType());
   }

   static KeyManagerFactory buildKeyManagerFactory(File var0, String var1, File var2, String var3, KeyManagerFactory var4, String var5) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
      return buildKeyManagerFactory(toX509Certificates(var0), var1, toPrivateKey(var2, var3), var3, var4, var5);
   }

   @Deprecated
   protected static KeyManagerFactory buildKeyManagerFactory(File var0, String var1, File var2, String var3, KeyManagerFactory var4) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
      return buildKeyManagerFactory(toX509Certificates(var0), var1, toPrivateKey(var2, var3), var3, var4, KeyStore.getDefaultType());
   }

   static {
      JdkSslContext.Defaults var0 = new JdkSslContext.Defaults();
      var0.init();
      DEFAULT_PROVIDER = var0.defaultProvider;
      DEFAULT_PROTOCOLS = var0.defaultProtocols;
      SUPPORTED_CIPHERS = var0.supportedCiphers;
      DEFAULT_CIPHERS = var0.defaultCiphers;
      DEFAULT_CIPHERS_NON_TLSV13 = var0.defaultCiphersNonTLSv13;
      SUPPORTED_CIPHERS_NON_TLSV13 = var0.supportedCiphersNonTLSv13;
      if (logger.isDebugEnabled()) {
         logger.debug("Default protocols (JDK): {} ", Arrays.asList(DEFAULT_PROTOCOLS));
         logger.debug("Default cipher suites (JDK): {}", DEFAULT_CIPHERS);
      }
   }

   private static final class Defaults {
      String[] defaultProtocols;
      List<String> defaultCiphers;
      List<String> defaultCiphersNonTLSv13;
      Set<String> supportedCiphers;
      Set<String> supportedCiphersNonTLSv13;
      Provider defaultProvider;

      private Defaults() {
      }

      void init() {
         SSLContext var1;
         try {
            var1 = SSLContext.getInstance("TLS");
            var1.init(null, null, null);
         } catch (Exception var5) {
            throw new Error("failed to initialize the default SSL context", var5);
         }

         this.defaultProvider = var1.getProvider();
         SSLEngine var2 = var1.createSSLEngine();
         this.defaultProtocols = JdkSslContext.defaultProtocols(var1, var2);
         this.supportedCiphers = Collections.unmodifiableSet(JdkSslContext.supportedCiphers(var2));
         this.defaultCiphers = Collections.unmodifiableList(JdkSslContext.defaultCiphers(var2, this.supportedCiphers));
         ArrayList var3 = new ArrayList<>(this.defaultCiphers);
         var3.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
         this.defaultCiphersNonTLSv13 = Collections.unmodifiableList(var3);
         LinkedHashSet var4 = new LinkedHashSet<>(this.supportedCiphers);
         var4.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
         this.supportedCiphersNonTLSv13 = Collections.unmodifiableSet(var4);
      }
   }
}
