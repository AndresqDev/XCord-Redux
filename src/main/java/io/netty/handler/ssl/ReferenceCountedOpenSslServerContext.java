package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext extends ReferenceCountedOpenSslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
   private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
   private final OpenSslServerSessionContext sessionContext;

   ReferenceCountedOpenSslServerContext(
      X509Certificate[] var1,
      TrustManagerFactory var2,
      X509Certificate[] var3,
      PrivateKey var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      ApplicationProtocolConfig var9,
      long var10,
      long var12,
      ClientAuth var14,
      String[] var15,
      boolean var16,
      boolean var17,
      String var18,
      Entry<SslContextOption<?>, Object>... var19
   ) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9), var10, var12, var14, var15, var16, var17, var18, var19);
   }

   ReferenceCountedOpenSslServerContext(
      X509Certificate[] var1,
      TrustManagerFactory var2,
      X509Certificate[] var3,
      PrivateKey var4,
      String var5,
      KeyManagerFactory var6,
      Iterable<String> var7,
      CipherSuiteFilter var8,
      OpenSslApplicationProtocolNegotiator var9,
      long var10,
      long var12,
      ClientAuth var14,
      String[] var15,
      boolean var16,
      boolean var17,
      String var18,
      Entry<SslContextOption<?>, Object>... var19
   ) throws SSLException {
      super(var7, var8, var9, 1, var3, var14, var15, var16, var17, true, var19);
      boolean var20 = false;

      try {
         this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6, var18, var10, var12);
         if (SERVER_ENABLE_SESSION_TICKET) {
            this.sessionContext.setTicketKeys(new OpenSslSessionTicketKey[0]);
         }

         var20 = true;
      } finally {
         if (!var20) {
            this.release();
         }
      }
   }

   public OpenSslServerSessionContext sessionContext() {
      return this.sessionContext;
   }

   static OpenSslServerSessionContext newSessionContext(
      ReferenceCountedOpenSslContext var0,
      long var1,
      OpenSslEngineMap var3,
      X509Certificate[] var4,
      TrustManagerFactory var5,
      X509Certificate[] var6,
      PrivateKey var7,
      String var8,
      KeyManagerFactory var9,
      String var10,
      long var11,
      long var13
   ) throws SSLException {
      OpenSslKeyMaterialProvider var15 = null;

      OpenSslServerSessionContext var40;
      try {
         try {
            SSLContext.setVerify(var1, 0, 10);
            if (!OpenSsl.useKeyManagerFactory()) {
               if (var9 != null) {
                  throw new IllegalArgumentException("KeyManagerFactory not supported");
               }

               ObjectUtil.checkNotNull(var6, "keyCertChain");
               setKeyMaterial(var1, var6, var7, var8);
            } else {
               if (var9 == null) {
                  char[] var16 = keyStorePassword(var8);
                  KeyStore var17 = buildKeyStore(var6, var7, var16, var10);
                  if (var17.aliases().hasMoreElements()) {
                     var9 = new OpenSslX509KeyManagerFactory();
                  } else {
                     var9 = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
                  }

                  var9.init(var17, var16);
               }

               var15 = providerFor((KeyManagerFactory)var9, var8);
               SSLContext.setCertificateCallback(
                  var1, new ReferenceCountedOpenSslServerContext.OpenSslServerCertificateCallback(var3, new OpenSslKeyMaterialManager(var15))
               );
            }
         } catch (Exception var33) {
            throw new SSLException("failed to set certificate and key", var33);
         }

         try {
            if (var4 != null) {
               var5 = buildTrustManagerFactory(var4, var5, var10);
            } else if (var5 == null) {
               var5 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
               var5.init((KeyStore)null);
            }

            X509TrustManager var37 = chooseTrustManager(var5.getTrustManagers());
            setVerifyCallback(var1, var3, var37);
            X509Certificate[] var39 = var37.getAcceptedIssuers();
            if (var39 != null && var39.length > 0) {
               long var18 = 0L;

               try {
                  var18 = toBIO(ByteBufAllocator.DEFAULT, var39);
                  if (!SSLContext.setCACertificateBio(var1, var18)) {
                     throw new SSLException("unable to setup accepted issuers for trustmanager " + var37);
                  }
               } finally {
                  freeBio(var18);
               }
            }

            if (PlatformDependent.javaVersion() >= 8) {
               SSLContext.setSniHostnameMatcher(var1, new ReferenceCountedOpenSslServerContext.OpenSslSniHostnameMatcher(var3));
            }
         } catch (SSLException var34) {
            throw var34;
         } catch (Exception var35) {
            throw new SSLException("unable to setup trustmanager", var35);
         }

         OpenSslServerSessionContext var38 = new OpenSslServerSessionContext(var0, var15);
         var38.setSessionIdContext(ID);
         var38.setSessionCacheEnabled(SERVER_ENABLE_SESSION_CACHE);
         if (var11 > 0L) {
            var38.setSessionCacheSize((int)Math.min(var11, 2147483647L));
         }

         if (var13 > 0L) {
            var38.setSessionTimeout((int)Math.min(var13, 2147483647L));
         }

         var15 = null;
         var40 = var38;
      } finally {
         if (var15 != null) {
            var15.destroy();
         }
      }

      return var40;
   }

   @SuppressJava6Requirement(
      reason = "Guarded by java version check"
   )
   private static void setVerifyCallback(long var0, OpenSslEngineMap var2, X509TrustManager var3) {
      if (useExtendedTrustManager(var3)) {
         SSLContext.setCertVerifyCallback(
            var0, new ReferenceCountedOpenSslServerContext.ExtendedTrustManagerVerifyCallback(var2, (X509ExtendedTrustManager)var3)
         );
      } else {
         SSLContext.setCertVerifyCallback(var0, new ReferenceCountedOpenSslServerContext.TrustManagerVerifyCallback(var2, var3));
      }
   }

   @SuppressJava6Requirement(
      reason = "Usage guarded by java version check"
   )
   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509ExtendedTrustManager manager;

      ExtendedTrustManagerVerifyCallback(OpenSslEngineMap var1, X509ExtendedTrustManager var2) {
         super(var1);
         this.manager = var2;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception {
         this.manager.checkClientTrusted(var2, var3, var1);
      }
   }

   private static final class OpenSslServerCertificateCallback implements CertificateCallback {
      private final OpenSslEngineMap engineMap;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslServerCertificateCallback(OpenSslEngineMap var1, OpenSslKeyMaterialManager var2) {
         this.engineMap = var1;
         this.keyManagerHolder = var2;
      }

      public void handle(long var1, byte[] var3, byte[][] var4) throws Exception {
         ReferenceCountedOpenSslEngine var5 = this.engineMap.get(var1);
         if (var5 != null) {
            try {
               this.keyManagerHolder.setKeyMaterialServerSide(var5);
            } catch (Throwable var7) {
               var5.initHandshakeException(var7);
               if (var7 instanceof Exception) {
                  throw (Exception)var7;
               } else {
                  throw new SSLException(var7);
               }
            }
         }
      }
   }

   private static final class OpenSslSniHostnameMatcher implements SniHostNameMatcher {
      private final OpenSslEngineMap engineMap;

      OpenSslSniHostnameMatcher(OpenSslEngineMap var1) {
         this.engineMap = var1;
      }

      public boolean match(long var1, String var3) {
         ReferenceCountedOpenSslEngine var4 = this.engineMap.get(var1);
         if (var4 != null) {
            return var4.checkSniHostnameMatch(var3.getBytes(CharsetUtil.UTF_8));
         } else {
            ReferenceCountedOpenSslServerContext.logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", var1);
            return false;
         }
      }
   }

   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509TrustManager manager;

      TrustManagerVerifyCallback(OpenSslEngineMap var1, X509TrustManager var2) {
         super(var1);
         this.manager = var2;
      }

      @Override
      void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception {
         this.manager.checkClientTrusted(var2, var3);
      }
   }
}
