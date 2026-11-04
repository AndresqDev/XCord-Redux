package io.netty.handler.ssl;

import io.netty.handler.ssl.util.KeyManagerFactoryWrapper;
import io.netty.handler.ssl.util.TrustManagerFactoryWrapper;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public final class SslContextBuilder {
   private static final Entry[] EMPTY_ENTRIES = new Entry[0];
   private final boolean forServer;
   private SslProvider provider;
   private Provider sslContextProvider;
   private X509Certificate[] trustCertCollection;
   private TrustManagerFactory trustManagerFactory;
   private X509Certificate[] keyCertChain;
   private PrivateKey key;
   private String keyPassword;
   private KeyManagerFactory keyManagerFactory;
   private Iterable<String> ciphers;
   private CipherSuiteFilter cipherFilter = IdentityCipherSuiteFilter.INSTANCE;
   private ApplicationProtocolConfig apn;
   private long sessionCacheSize;
   private long sessionTimeout;
   private ClientAuth clientAuth = ClientAuth.NONE;
   private String[] protocols;
   private boolean startTls;
   private boolean enableOcsp;
   private String keyStoreType = KeyStore.getDefaultType();
   private final Map<SslContextOption<?>, Object> options = new HashMap<>();

   public static SslContextBuilder forClient() {
      return new SslContextBuilder(false);
   }

   public static SslContextBuilder forServer(File var0, File var1) {
      return new SslContextBuilder(true).keyManager(var0, var1);
   }

   public static SslContextBuilder forServer(InputStream var0, InputStream var1) {
      return new SslContextBuilder(true).keyManager(var0, var1);
   }

   public static SslContextBuilder forServer(PrivateKey var0, X509Certificate... var1) {
      return new SslContextBuilder(true).keyManager(var0, var1);
   }

   public static SslContextBuilder forServer(PrivateKey var0, Iterable<? extends X509Certificate> var1) {
      return forServer(var0, toArray(var1, EmptyArrays.EMPTY_X509_CERTIFICATES));
   }

   public static SslContextBuilder forServer(File var0, File var1, String var2) {
      return new SslContextBuilder(true).keyManager(var0, var1, var2);
   }

   public static SslContextBuilder forServer(InputStream var0, InputStream var1, String var2) {
      return new SslContextBuilder(true).keyManager(var0, var1, var2);
   }

   public static SslContextBuilder forServer(PrivateKey var0, String var1, X509Certificate... var2) {
      return new SslContextBuilder(true).keyManager(var0, var1, var2);
   }

   public static SslContextBuilder forServer(PrivateKey var0, String var1, Iterable<? extends X509Certificate> var2) {
      return forServer(var0, var1, toArray(var2, EmptyArrays.EMPTY_X509_CERTIFICATES));
   }

   public static SslContextBuilder forServer(KeyManagerFactory var0) {
      return new SslContextBuilder(true).keyManager(var0);
   }

   public static SslContextBuilder forServer(KeyManager var0) {
      return new SslContextBuilder(true).keyManager(var0);
   }

   private SslContextBuilder(boolean var1) {
      this.forServer = var1;
   }

   public <T> SslContextBuilder option(SslContextOption<T> var1, T var2) {
      if (var2 == null) {
         this.options.remove(var1);
      } else {
         this.options.put(var1, var2);
      }

      return this;
   }

   public SslContextBuilder sslProvider(SslProvider var1) {
      this.provider = var1;
      return this;
   }

   public SslContextBuilder keyStoreType(String var1) {
      this.keyStoreType = var1;
      return this;
   }

   public SslContextBuilder sslContextProvider(Provider var1) {
      this.sslContextProvider = var1;
      return this;
   }

   public SslContextBuilder trustManager(File var1) {
      try {
         return this.trustManager(SslContext.toX509Certificates(var1));
      } catch (Exception var3) {
         throw new IllegalArgumentException("File does not contain valid certificates: " + var1, var3);
      }
   }

   public SslContextBuilder trustManager(InputStream var1) {
      try {
         return this.trustManager(SslContext.toX509Certificates(var1));
      } catch (Exception var3) {
         throw new IllegalArgumentException("Input stream does not contain valid certificates.", var3);
      }
   }

   public SslContextBuilder trustManager(X509Certificate... var1) {
      this.trustCertCollection = var1 != null ? (X509Certificate[])var1.clone() : null;
      this.trustManagerFactory = null;
      return this;
   }

   public SslContextBuilder trustManager(Iterable<? extends X509Certificate> var1) {
      return this.trustManager(toArray(var1, EmptyArrays.EMPTY_X509_CERTIFICATES));
   }

   public SslContextBuilder trustManager(TrustManagerFactory var1) {
      this.trustCertCollection = null;
      this.trustManagerFactory = var1;
      return this;
   }

   public SslContextBuilder trustManager(TrustManager var1) {
      this.trustManagerFactory = new TrustManagerFactoryWrapper(var1);
      this.trustCertCollection = null;
      return this;
   }

   public SslContextBuilder keyManager(File var1, File var2) {
      return this.keyManager(var1, var2, null);
   }

   public SslContextBuilder keyManager(InputStream var1, InputStream var2) {
      return this.keyManager(var1, var2, null);
   }

   public SslContextBuilder keyManager(PrivateKey var1, X509Certificate... var2) {
      return this.keyManager(var1, null, var2);
   }

   public SslContextBuilder keyManager(PrivateKey var1, Iterable<? extends X509Certificate> var2) {
      return this.keyManager(var1, toArray(var2, EmptyArrays.EMPTY_X509_CERTIFICATES));
   }

   public SslContextBuilder keyManager(File var1, File var2, String var3) {
      X509Certificate[] var4;
      try {
         var4 = SslContext.toX509Certificates(var1);
      } catch (Exception var8) {
         throw new IllegalArgumentException("File does not contain valid certificates: " + var1, var8);
      }

      PrivateKey var5;
      try {
         var5 = SslContext.toPrivateKey(var2, var3);
      } catch (Exception var7) {
         throw new IllegalArgumentException("File does not contain valid private key: " + var2, var7);
      }

      return this.keyManager(var5, var3, var4);
   }

   public SslContextBuilder keyManager(InputStream var1, InputStream var2, String var3) {
      X509Certificate[] var4;
      try {
         var4 = SslContext.toX509Certificates(var1);
      } catch (Exception var8) {
         throw new IllegalArgumentException("Input stream not contain valid certificates.", var8);
      }

      PrivateKey var5;
      try {
         var5 = SslContext.toPrivateKey(var2, var3);
      } catch (Exception var7) {
         throw new IllegalArgumentException("Input stream does not contain valid private key.", var7);
      }

      return this.keyManager(var5, var3, var4);
   }

   public SslContextBuilder keyManager(PrivateKey var1, String var2, X509Certificate... var3) {
      if (this.forServer) {
         ObjectUtil.checkNonEmpty(var3, "keyCertChain");
         ObjectUtil.checkNotNull(var1, "key required for servers");
      }

      if (var3 != null && var3.length != 0) {
         for (X509Certificate var7 : var3) {
            ObjectUtil.checkNotNullWithIAE(var7, "cert");
         }

         this.keyCertChain = (X509Certificate[])var3.clone();
      } else {
         this.keyCertChain = null;
      }

      this.key = var1;
      this.keyPassword = var2;
      this.keyManagerFactory = null;
      return this;
   }

   public SslContextBuilder keyManager(PrivateKey var1, String var2, Iterable<? extends X509Certificate> var3) {
      return this.keyManager(var1, var2, toArray(var3, EmptyArrays.EMPTY_X509_CERTIFICATES));
   }

   public SslContextBuilder keyManager(KeyManagerFactory var1) {
      if (this.forServer) {
         ObjectUtil.checkNotNull(var1, "keyManagerFactory required for servers");
      }

      this.keyCertChain = null;
      this.key = null;
      this.keyPassword = null;
      this.keyManagerFactory = var1;
      return this;
   }

   public SslContextBuilder keyManager(KeyManager var1) {
      if (this.forServer) {
         ObjectUtil.checkNotNull(var1, "keyManager required for servers");
      }

      if (var1 != null) {
         this.keyManagerFactory = new KeyManagerFactoryWrapper(var1);
      } else {
         this.keyManagerFactory = null;
      }

      this.keyCertChain = null;
      this.key = null;
      this.keyPassword = null;
      return this;
   }

   public SslContextBuilder ciphers(Iterable<String> var1) {
      return this.ciphers(var1, IdentityCipherSuiteFilter.INSTANCE);
   }

   public SslContextBuilder ciphers(Iterable<String> var1, CipherSuiteFilter var2) {
      this.cipherFilter = ObjectUtil.checkNotNull(var2, "cipherFilter");
      this.ciphers = var1;
      return this;
   }

   public SslContextBuilder applicationProtocolConfig(ApplicationProtocolConfig var1) {
      this.apn = var1;
      return this;
   }

   public SslContextBuilder sessionCacheSize(long var1) {
      this.sessionCacheSize = var1;
      return this;
   }

   public SslContextBuilder sessionTimeout(long var1) {
      this.sessionTimeout = var1;
      return this;
   }

   public SslContextBuilder clientAuth(ClientAuth var1) {
      this.clientAuth = ObjectUtil.checkNotNull(var1, "clientAuth");
      return this;
   }

   public SslContextBuilder protocols(String... var1) {
      this.protocols = var1 == null ? null : (String[])var1.clone();
      return this;
   }

   public SslContextBuilder protocols(Iterable<String> var1) {
      return this.protocols(toArray(var1, EmptyArrays.EMPTY_STRINGS));
   }

   public SslContextBuilder startTls(boolean var1) {
      this.startTls = var1;
      return this;
   }

   public SslContextBuilder enableOcsp(boolean var1) {
      this.enableOcsp = var1;
      return this;
   }

   public SslContext build() throws SSLException {
      return this.forServer
         ? SslContext.newServerContextInternal(
            this.provider,
            this.sslContextProvider,
            this.trustCertCollection,
            this.trustManagerFactory,
            this.keyCertChain,
            this.key,
            this.keyPassword,
            this.keyManagerFactory,
            this.ciphers,
            this.cipherFilter,
            this.apn,
            this.sessionCacheSize,
            this.sessionTimeout,
            this.clientAuth,
            this.protocols,
            this.startTls,
            this.enableOcsp,
            this.keyStoreType,
            toArray(this.options.entrySet(), EMPTY_ENTRIES)
         )
         : SslContext.newClientContextInternal(
            this.provider,
            this.sslContextProvider,
            this.trustCertCollection,
            this.trustManagerFactory,
            this.keyCertChain,
            this.key,
            this.keyPassword,
            this.keyManagerFactory,
            this.ciphers,
            this.cipherFilter,
            this.apn,
            this.protocols,
            this.sessionCacheSize,
            this.sessionTimeout,
            this.enableOcsp,
            this.keyStoreType,
            toArray(this.options.entrySet(), EMPTY_ENTRIES)
         );
   }

   private static <T> T[] toArray(Iterable<? extends T> var0, T[] var1) {
      if (var0 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();

         for (Object var4 : var0) {
            var2.add(var4);
         }

         return (T[])var2.toArray(var1);
      }
   }
}
