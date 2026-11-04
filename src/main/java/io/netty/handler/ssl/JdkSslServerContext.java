package io.netty.handler.ssl;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

@Deprecated
public final class JdkSslServerContext extends JdkSslContext {
   @Deprecated
   public JdkSslServerContext(File var1, File var2) throws SSLException {
      this(null, var1, var2, null, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L, null);
   }

   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3) throws SSLException {
      this(var1, var2, var3, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
   }

   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3, Iterable<String> var4, Iterable<String> var5, long var6, long var8) throws SSLException {
      this(
         null,
         var1,
         var2,
         var3,
         var4,
         IdentityCipherSuiteFilter.INSTANCE,
         toNegotiator(toApplicationProtocolConfig(var5), true),
         var6,
         var8,
         KeyStore.getDefaultType()
      );
   }

   @Deprecated
   public JdkSslServerContext(
      File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, ApplicationProtocolConfig var6, long var7, long var9
   ) throws SSLException {
      this(null, var1, var2, var3, var4, var5, toNegotiator(var6, true), var7, var9, KeyStore.getDefaultType());
   }

   @Deprecated
   public JdkSslServerContext(
      File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, JdkApplicationProtocolNegotiator var6, long var7, long var9
   ) throws SSLException {
      this(null, var1, var2, var3, var4, var5, var6, var7, var9, KeyStore.getDefaultType());
   }

   JdkSslServerContext(
      Provider var1,
      File var2,
      File var3,
      String var4,
      Iterable<String> var5,
      CipherSuiteFilter var6,
      JdkApplicationProtocolNegotiator var7,
      long var8,
      long var10,
      String var12
   ) throws SSLException {
      super(
         newSSLContext(var1, null, null, toX509CertificatesInternal(var2), toPrivateKeyInternal(var3, var4), var4, null, var8, var10, var12),
         false,
         var5,
         var6,
         var7,
         ClientAuth.NONE,
         null,
         false
      );
   }

   @Deprecated
   public JdkSslServerContext(
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
      super(
         newSSLContext(
            null, toX509CertificatesInternal(var1), var2, toX509CertificatesInternal(var3), toPrivateKeyInternal(var4, var5), var5, var6, var10, var12, null
         ),
         false,
         var7,
         var8,
         var9,
         ClientAuth.NONE,
         null,
         false
      );
   }

   @Deprecated
   public JdkSslServerContext(
      File var1,
      TrustManagerFactory var2,
      File var3,
      File var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      JdkApplicationProtocolNegotiator var9,
      long var10,
      long var12
   ) throws SSLException {
      super(
         newSSLContext(
            null,
            toX509CertificatesInternal(var1),
            var2,
            toX509CertificatesInternal(var3),
            toPrivateKeyInternal(var4, var5),
            var5,
            var6,
            var10,
            var12,
            KeyStore.getDefaultType()
         ),
         false,
         var7,
         var8,
         var9,
         ClientAuth.NONE,
         null,
         false
      );
   }

   JdkSslServerContext(
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
      String var18
   ) throws SSLException {
      super(newSSLContext(var1, var2, var3, var4, var5, var6, var7, var11, var13, var18), false, var8, var9, toNegotiator(var10, true), var15, var16, var17);
   }

   private static SSLContext newSSLContext(
      Provider var0,
      X509Certificate[] var1,
      TrustManagerFactory var2,
      X509Certificate[] var3,
      PrivateKey var4,
      String var5,
      KeyManagerFactory var6,
      long var7,
      long var9,
      String var11
   ) throws SSLException {
      if (var4 == null && var6 == null) {
         throw new NullPointerException("key, keyManagerFactory");
      } else {
         try {
            if (var1 != null) {
               var2 = buildTrustManagerFactory(var1, var2, var11);
            }

            if (var4 != null) {
               var6 = buildKeyManagerFactory(var3, null, var4, var5, var6, null);
            }

            SSLContext var12 = var0 == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", var0);
            var12.init(var6.getKeyManagers(), var2 == null ? null : var2.getTrustManagers(), null);
            SSLSessionContext var13 = var12.getServerSessionContext();
            if (var7 > 0L) {
               var13.setSessionCacheSize((int)Math.min(var7, 2147483647L));
            }

            if (var9 > 0L) {
               var13.setSessionTimeout((int)Math.min(var9, 2147483647L));
            }

            return var12;
         } catch (Exception var14) {
            if (var14 instanceof SSLException) {
               throw (SSLException)var14;
            } else {
               throw new SSLException("failed to initialize the server-side SSL context", var14);
            }
         }
      }
   }
}
