package org.apache.http.conn.ssl;

import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

@Deprecated
public class SSLContextBuilder {
   static final String TLS = "TLS";
   static final String SSL = "SSL";
   private String protocol;
   private final Set<KeyManager> keymanagers = new LinkedHashSet<>();
   private final Set<TrustManager> trustmanagers = new LinkedHashSet<>();
   private SecureRandom secureRandom;

   public SSLContextBuilder useTLS() {
      this.protocol = "TLS";
      return this;
   }

   public SSLContextBuilder useSSL() {
      this.protocol = "SSL";
      return this;
   }

   public SSLContextBuilder useProtocol(String var1) {
      this.protocol = var1;
      return this;
   }

   public SSLContextBuilder setSecureRandom(SecureRandom var1) {
      this.secureRandom = var1;
      return this;
   }

   public SSLContextBuilder loadTrustMaterial(KeyStore var1, TrustStrategy var2) throws NoSuchAlgorithmException, KeyStoreException {
      TrustManagerFactory var3 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
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

         for (TrustManager var8 : var4) {
            this.trustmanagers.add(var8);
         }
      }

      return this;
   }

   public SSLContextBuilder loadTrustMaterial(KeyStore var1) throws NoSuchAlgorithmException, KeyStoreException {
      return this.loadTrustMaterial(var1, null);
   }

   public SSLContextBuilder loadKeyMaterial(KeyStore var1, char[] var2) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
      this.loadKeyMaterial(var1, var2, null);
      return this;
   }

   public SSLContextBuilder loadKeyMaterial(KeyStore var1, char[] var2, PrivateKeyStrategy var3) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
      KeyManagerFactory var4 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      var4.init(var1, var2);
      KeyManager[] var5 = var4.getKeyManagers();
      if (var5 != null) {
         if (var3 != null) {
            for (int var6 = 0; var6 < var5.length; var6++) {
               KeyManager var7 = var5[var6];
               if (var7 instanceof X509KeyManager) {
                  var5[var6] = new SSLContextBuilder.KeyManagerDelegate((X509KeyManager)var7, var3);
               }
            }
         }

         for (KeyManager var9 : var5) {
            this.keymanagers.add(var9);
         }
      }

      return this;
   }

   public SSLContext build() throws NoSuchAlgorithmException, KeyManagementException {
      SSLContext var1 = SSLContext.getInstance(this.protocol != null ? this.protocol : "TLS");
      var1.init(
         !this.keymanagers.isEmpty() ? this.keymanagers.toArray(new KeyManager[this.keymanagers.size()]) : null,
         !this.trustmanagers.isEmpty() ? this.trustmanagers.toArray(new TrustManager[this.trustmanagers.size()]) : null,
         this.secureRandom
      );
      return var1;
   }

   static class KeyManagerDelegate implements X509KeyManager {
      private final X509KeyManager keyManager;
      private final PrivateKeyStrategy aliasStrategy;

      KeyManagerDelegate(X509KeyManager var1, PrivateKeyStrategy var2) {
         this.keyManager = var1;
         this.aliasStrategy = var2;
      }

      @Override
      public String[] getClientAliases(String var1, Principal[] var2) {
         return this.keyManager.getClientAliases(var1, var2);
      }

      @Override
      public String chooseClientAlias(String[] var1, Principal[] var2, Socket var3) {
         HashMap var4 = new HashMap();

         for (String var8 : var1) {
            String[] var9 = this.keyManager.getClientAliases(var8, var2);
            if (var9 != null) {
               for (String var13 : var9) {
                  var4.put(var13, new PrivateKeyDetails(var8, this.keyManager.getCertificateChain(var13)));
               }
            }
         }

         return this.aliasStrategy.chooseAlias(var4, var3);
      }

      @Override
      public String[] getServerAliases(String var1, Principal[] var2) {
         return this.keyManager.getServerAliases(var1, var2);
      }

      @Override
      public String chooseServerAlias(String var1, Principal[] var2, Socket var3) {
         HashMap var4 = new HashMap();
         String[] var5 = this.keyManager.getServerAliases(var1, var2);
         if (var5 != null) {
            for (String var9 : var5) {
               var4.put(var9, new PrivateKeyDetails(var1, this.keyManager.getCertificateChain(var9)));
            }
         }

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
