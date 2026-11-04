package org.apache.http.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.util.Args;

public class SSLContextBuilder {
   static final String TLS = "TLS";
   private String protocol;
   private final Set<KeyManager> keyManagers;
   private String keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
   private String keyStoreType = KeyStore.getDefaultType();
   private final Set<TrustManager> trustManagers;
   private String trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
   private SecureRandom secureRandom;
   private Provider provider;

   public static SSLContextBuilder create() {
      return new SSLContextBuilder();
   }

   public SSLContextBuilder() {
      this.keyManagers = new LinkedHashSet<>();
      this.trustManagers = new LinkedHashSet<>();
   }

   @Deprecated
   public SSLContextBuilder useProtocol(String var1) {
      this.protocol = var1;
      return this;
   }

   public SSLContextBuilder setProtocol(String var1) {
      this.protocol = var1;
      return this;
   }

   public SSLContextBuilder setSecureRandom(SecureRandom var1) {
      this.secureRandom = var1;
      return this;
   }

   public SSLContextBuilder setProvider(Provider var1) {
      this.provider = var1;
      return this;
   }

   public SSLContextBuilder setProvider(String var1) {
      this.provider = Security.getProvider(var1);
      return this;
   }

   public SSLContextBuilder setKeyStoreType(String var1) {
      this.keyStoreType = var1;
      return this;
   }

   public SSLContextBuilder setKeyManagerFactoryAlgorithm(String var1) {
      this.keyManagerFactoryAlgorithm = var1;
      return this;
   }

   public SSLContextBuilder setTrustManagerFactoryAlgorithm(String var1) {
      this.trustManagerFactoryAlgorithm = var1;
      return this;
   }

   public SSLContextBuilder loadTrustMaterial(KeyStore var1, TrustStrategy var2) throws NoSuchAlgorithmException, KeyStoreException {
      TrustManagerFactory var3 = TrustManagerFactory.getInstance(
         this.trustManagerFactoryAlgorithm == null ? TrustManagerFactory.getDefaultAlgorithm() : this.trustManagerFactoryAlgorithm
      );
      var3.init(var1);
      TrustManager[] var4 = var3.getTrustManagers();
      if (var4 != null) {
         if (var2 != null) {
            for (int var5 = 0; var5 < var4.length; var5++) {
               TrustManager var6 = var4[var5];
               if (var6 instanceof X509TrustManager) {
                  var4[var5] = new SSLContextBuilder.TrustManagerDelegate((X509TrustManager)var6, var2);
               }
            }
         }

         Collections.addAll(this.trustManagers, var4);
      }

      return this;
   }

   public SSLContextBuilder loadTrustMaterial(TrustStrategy var1) throws NoSuchAlgorithmException, KeyStoreException {
      return this.loadTrustMaterial(null, var1);
   }

   public SSLContextBuilder loadTrustMaterial(File var1, char[] var2, TrustStrategy var3) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
      Args.notNull(var1, "Truststore file");
      KeyStore var4 = KeyStore.getInstance(this.keyStoreType);
      FileInputStream var5 = new FileInputStream(var1);

      try {
         var4.load(var5, var2);
      } finally {
         var5.close();
      }

      return this.loadTrustMaterial(var4, var3);
   }

   public SSLContextBuilder loadTrustMaterial(File var1, char[] var2) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
      return this.loadTrustMaterial(var1, var2, null);
   }

   public SSLContextBuilder loadTrustMaterial(File var1) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
      return this.loadTrustMaterial(var1, null);
   }

   public SSLContextBuilder loadTrustMaterial(URL var1, char[] var2, TrustStrategy var3) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
      Args.notNull(var1, "Truststore URL");
      KeyStore var4 = KeyStore.getInstance(this.keyStoreType);
      InputStream var5 = var1.openStream();

      try {
         var4.load(var5, var2);
      } finally {
         var5.close();
      }

      return this.loadTrustMaterial(var4, var3);
   }

   public SSLContextBuilder loadTrustMaterial(URL var1, char[] var2) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
      return this.loadTrustMaterial(var1, var2, null);
   }

   public SSLContextBuilder loadKeyMaterial(KeyStore var1, char[] var2, PrivateKeyStrategy var3) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
      KeyManagerFactory var4 = KeyManagerFactory.getInstance(
         this.keyManagerFactoryAlgorithm == null ? KeyManagerFactory.getDefaultAlgorithm() : this.keyManagerFactoryAlgorithm
      );
      var4.init(var1, var2);
      KeyManager[] var5 = var4.getKeyManagers();
      if (var5 != null) {
         if (var3 != null) {
            for (int var6 = 0; var6 < var5.length; var6++) {
               KeyManager var7 = var5[var6];
               if (var7 instanceof X509ExtendedKeyManager) {
                  var5[var6] = new SSLContextBuilder.KeyManagerDelegate((X509ExtendedKeyManager)var7, var3);
               }
            }
         }

         Collections.addAll(this.keyManagers, var5);
      }

      return this;
   }

   public SSLContextBuilder loadKeyMaterial(KeyStore var1, char[] var2) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
      return this.loadKeyMaterial(var1, var2, null);
   }

   public SSLContextBuilder loadKeyMaterial(File var1, char[] var2, char[] var3, PrivateKeyStrategy var4) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
      Args.notNull(var1, "Keystore file");
      KeyStore var5 = KeyStore.getInstance(this.keyStoreType);
      FileInputStream var6 = new FileInputStream(var1);

      try {
         var5.load(var6, var2);
      } finally {
         var6.close();
      }

      return this.loadKeyMaterial(var5, var3, var4);
   }

   public SSLContextBuilder loadKeyMaterial(File var1, char[] var2, char[] var3) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
      return this.loadKeyMaterial(var1, var2, var3, null);
   }

   public SSLContextBuilder loadKeyMaterial(URL var1, char[] var2, char[] var3, PrivateKeyStrategy var4) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
      Args.notNull(var1, "Keystore URL");
      KeyStore var5 = KeyStore.getInstance(this.keyStoreType);
      InputStream var6 = var1.openStream();

      try {
         var5.load(var6, var2);
      } finally {
         var6.close();
      }

      return this.loadKeyMaterial(var5, var3, var4);
   }

   public SSLContextBuilder loadKeyMaterial(URL var1, char[] var2, char[] var3) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
      return this.loadKeyMaterial(var1, var2, var3, null);
   }

   protected void initSSLContext(SSLContext var1, Collection<KeyManager> var2, Collection<TrustManager> var3, SecureRandom var4) throws KeyManagementException {
      var1.init(!var2.isEmpty() ? var2.toArray(new KeyManager[var2.size()]) : null, !var3.isEmpty() ? var3.toArray(new TrustManager[var3.size()]) : null, var4);
   }

   public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
      String var2 = this.protocol != null ? this.protocol : "TLS";
      SSLContext var1;
      if (this.provider != null) {
         var1 = SSLContext.getInstance(var2, this.provider);
      } else {
         var1 = SSLContext.getInstance(var2);
      }

      this.initSSLContext(var1, this.keyManagers, this.trustManagers, this.secureRandom);
      return var1;
   }

   @Override
   public String toString() {
      return "[provider="
         + this.provider
         + ", protocol="
         + this.protocol
         + ", keyStoreType="
         + this.keyStoreType
         + ", keyManagerFactoryAlgorithm="
         + this.keyManagerFactoryAlgorithm
         + ", keyManagers="
         + this.keyManagers
         + ", trustManagerFactoryAlgorithm="
         + this.trustManagerFactoryAlgorithm
         + ", trustManagers="
         + this.trustManagers
         + ", secureRandom="
         + this.secureRandom
         + "]";
   }

   static class KeyManagerDelegate extends X509ExtendedKeyManager {
      private final X509ExtendedKeyManager keyManager;
      private final PrivateKeyStrategy aliasStrategy;

      KeyManagerDelegate(X509ExtendedKeyManager var1, PrivateKeyStrategy var2) {
         this.keyManager = var1;
         this.aliasStrategy = var2;
      }

      @Override
      public String[] getClientAliases(String var1, Principal[] var2) {
         return this.keyManager.getClientAliases(var1, var2);
      }

      public Map<String, PrivateKeyDetails> getClientAliasMap(String[] var1, Principal[] var2) {
         HashMap var3 = new HashMap();

         for (String var7 : var1) {
            String[] var8 = this.keyManager.getClientAliases(var7, var2);
            if (var8 != null) {
               for (String var12 : var8) {
                  var3.put(var12, new PrivateKeyDetails(var7, this.keyManager.getCertificateChain(var12)));
               }
            }
         }

         return var3;
      }

      public Map<String, PrivateKeyDetails> getServerAliasMap(String var1, Principal[] var2) {
         HashMap var3 = new HashMap();
         String[] var4 = this.keyManager.getServerAliases(var1, var2);
         if (var4 != null) {
            for (String var8 : var4) {
               var3.put(var8, new PrivateKeyDetails(var1, this.keyManager.getCertificateChain(var8)));
            }
         }

         return var3;
      }

      @Override
      public String chooseClientAlias(String[] var1, Principal[] var2, Socket var3) {
         Map var4 = this.getClientAliasMap(var1, var2);
         return this.aliasStrategy.chooseAlias(var4, var3);
      }

      @Override
      public String[] getServerAliases(String var1, Principal[] var2) {
         return this.keyManager.getServerAliases(var1, var2);
      }

      @Override
      public String chooseServerAlias(String var1, Principal[] var2, Socket var3) {
         Map var4 = this.getServerAliasMap(var1, var2);
         return this.aliasStrategy.chooseAlias(var4, var3);
      }

      @Override
      public X509Certificate[] getCertificateChain(String var1) {
         return this.keyManager.getCertificateChain(var1);
      }

      @Override
      public PrivateKey getPrivateKey(String var1) {
         return this.keyManager.getPrivateKey(var1);
      }

      @Override
      public String chooseEngineClientAlias(String[] var1, Principal[] var2, SSLEngine var3) {
         Map var4 = this.getClientAliasMap(var1, var2);
         return this.aliasStrategy.chooseAlias(var4, null);
      }

      @Override
      public String chooseEngineServerAlias(String var1, Principal[] var2, SSLEngine var3) {
         Map var4 = this.getServerAliasMap(var1, var2);
         return this.aliasStrategy.chooseAlias(var4, null);
      }
   }

   static class TrustManagerDelegate implements X509TrustManager {
      private final X509TrustManager trustManager;
      private final TrustStrategy trustStrategy;

      TrustManagerDelegate(X509TrustManager var1, TrustStrategy var2) {
         this.trustManager = var1;
         this.trustStrategy = var2;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         this.trustManager.checkClientTrusted(var1, var2);
      }

      @Override
      public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
         if (!this.trustStrategy.isTrusted(var1, var2)) {
            this.trustManager.checkServerTrusted(var1, var2);
         }
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return this.trustManager.getAcceptedIssuers();
      }
   }
}
