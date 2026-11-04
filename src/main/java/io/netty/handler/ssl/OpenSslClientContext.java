package io.netty.handler.ssl;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslClientContext extends OpenSslContext {
   private final OpenSslSessionContext sessionContext;

   @Deprecated
   public OpenSslClientContext() throws SSLException {
      this(null, null, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public OpenSslClientContext(File var1) throws SSLException {
      this(var1, null);
   }

   @Deprecated
   public OpenSslClientContext(TrustManagerFactory var1) throws SSLException {
      this(null, var1);
   }

   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2) throws SSLException {
      this(var1, var2, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
   }

   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, ApplicationProtocolConfig var4, long var5, long var7) throws SSLException {
      this(var1, var2, null, null, null, null, var3, IdentityCipherSuiteFilter.INSTANCE, var4, var5, var7);
   }

   @Deprecated
   public OpenSslClientContext(
      File var1, TrustManagerFactory var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, long var6, long var8
   ) throws SSLException {
      this(var1, var2, null, null, null, null, var3, var4, var5, var6, var8);
   }

   @Deprecated
   public OpenSslClientContext(
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
      this(
         toX509CertificatesInternal(var1),
         var2,
         toX509CertificatesInternal(var3),
         toPrivateKeyInternal(var4, var5),
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
   }

   OpenSslClientContext(
      X509Certificate[] var1,
      TrustManagerFactory var2,
      X509Certificate[] var3,
      PrivateKey var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      ApplicationProtocolConfig var9,
      String[] var10,
      long var11,
      long var13,
      boolean var15,
      String var16,
      Entry<SslContextOption<?>, Object>... var17
   ) throws SSLException {
      super(var7, var8, var9, 0, var3, ClientAuth.NONE, var10, false, var15, var17);
      boolean var18 = false;

      try {
         OpenSslKeyMaterialProvider.validateKeyMaterialSupported(var3, var4, var5);
         this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext(
            this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6, var16, var11, var13
         );
         var18 = true;
      } finally {
         if (!var18) {
            this.release();
         }
      }
   }

   @Override
   public OpenSslSessionContext sessionContext() {
      return this.sessionContext;
   }
}
