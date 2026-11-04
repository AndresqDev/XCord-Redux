package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class ReferenceCountedOpenSslClientContext extends ReferenceCountedOpenSslContext {
   private static final Set<String> SUPPORTED_KEY_TYPES = Collections.unmodifiableSet(
      new LinkedHashSet<>(Arrays.asList("RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC"))
   );
   private final OpenSslSessionContext sessionContext;

   ReferenceCountedOpenSslClientContext(
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
      super(var7, var8, toNegotiator(var9), 0, var3, ClientAuth.NONE, var10, false, var15, true, var17);
      boolean var18 = false;

      try {
         this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6, var16, var11, var13);
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

   static OpenSslSessionContext newSessionContext(
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
      if ((var7 != null || var6 == null) && (var7 == null || var6 != null)) {
         OpenSslKeyMaterialProvider var15 = null;

         ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext var29;
         try {
            try {
               if (!OpenSsl.useKeyManagerFactory()) {
                  if (var9 != null) {
                     throw new IllegalArgumentException("KeyManagerFactory not supported");
                  }

                  if (var6 != null) {
                     setKeyMaterial(var1, var6, var7, var8);
                  }
               } else {
                  if (var9 == null && var6 != null) {
                     char[] var16 = keyStorePassword(var8);
                     KeyStore var17 = buildKeyStore(var6, var7, var16, var10);
                     if (var17.aliases().hasMoreElements()) {
                        var9 = new OpenSslX509KeyManagerFactory();
                     } else {
                        var9 = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
                     }

                     var9.init(var17, var16);
                     var15 = providerFor(var9, var8);
                  } else if (var9 != null) {
                     var15 = providerFor(var9, var8);
                  }

                  if (var15 != null) {
                     OpenSslKeyMaterialManager var26 = new OpenSslKeyMaterialManager(var15);
                     SSLContext.setCertificateCallback(var1, new ReferenceCountedOpenSslClientContext.OpenSslClientCertificateCallback(var3, var26));
                  }
               }
            } catch (Exception var23) {
               throw new SSLException("failed to set certificate and key", var23);
            }

            SSLContext.setVerify(var1, 1, 10);

            try {
               if (var4 != null) {
                  var5 = buildTrustManagerFactory(var4, var5, var10);
               } else if (var5 == null) {
                  var5 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                  var5.init((KeyStore)null);
               }

               X509TrustManager var27 = chooseTrustManager(var5.getTrustManagers());
               setVerifyCallback(var1, var3, var27);
            } catch (Exception var22) {
               if (var15 != null) {
                  var15.destroy();
               }

               throw new SSLException("unable to setup trustmanager", var22);
            }

            ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext var28 = new ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext(
               var0, var15
            );
            var28.setSessionCacheEnabled(CLIENT_ENABLE_SESSION_CACHE);
            if (var11 > 0L) {
               var28.setSessionCacheSize((int)Math.min(var11, 2147483647L));
            }

            if (var13 > 0L) {
               var28.setSessionTimeout((int)Math.min(var13, 2147483647L));
            }

            if (CLIENT_ENABLE_SESSION_TICKET) {
               var28.setTicketKeys(new OpenSslSessionTicketKey[0]);
            }

            var15 = null;
            var29 = var28;
         } finally {
            if (var15 != null) {
               var15.destroy();
            }
         }

         return var29;
      } else {
         throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
      }
   }

   @SuppressJava6Requirement(
      reason = "Guarded by java version check"
   )
   private static void setVerifyCallback(long var0, OpenSslEngineMap var2, X509TrustManager var3) {
      if (useExtendedTrustManager(var3)) {
         SSLContext.setCertVerifyCallback(
            var0, new ReferenceCountedOpenSslClientContext.ExtendedTrustManagerVerifyCallback(var2, (X509ExtendedTrustManager)var3)
         );
      } else {
         SSLContext.setCertVerifyCallback(var0, new ReferenceCountedOpenSslClientContext.TrustManagerVerifyCallback(var2, var3));
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
         this.manager.checkServerTrusted(var2, var3, var1);
      }
   }

   private static final class OpenSslClientCertificateCallback implements CertificateCallback {
      private final OpenSslEngineMap engineMap;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslClientCertificateCallback(OpenSslEngineMap var1, OpenSslKeyMaterialManager var2) {
         this.engineMap = var1;
         this.keyManagerHolder = var2;
      }

      public void handle(long var1, byte[] var3, byte[][] var4) throws Exception {
         ReferenceCountedOpenSslEngine var5 = this.engineMap.get(var1);
         if (var5 != null) {
            try {
               Set var6 = supportedClientKeyTypes(var3);
               String[] var7 = var6.toArray(new String[0]);
               X500Principal[] var8;
               if (var4 == null) {
                  var8 = null;
               } else {
                  var8 = new X500Principal[var4.length];

                  for (int var9 = 0; var9 < var4.length; var9++) {
                     var8[var9] = new X500Principal(var4[var9]);
                  }
               }

               this.keyManagerHolder.setKeyMaterialClientSide(var5, var7, var8);
            } catch (Throwable var10) {
               var5.initHandshakeException(var10);
               if (var10 instanceof Exception) {
                  throw (Exception)var10;
               } else {
                  throw new SSLException(var10);
               }
            }
         }
      }

      private static Set<String> supportedClientKeyTypes(byte[] var0) {
         if (var0 == null) {
            return ReferenceCountedOpenSslClientContext.SUPPORTED_KEY_TYPES;
         } else {
            HashSet var1 = new HashSet(var0.length);

            for (byte var5 : var0) {
               String var6 = clientKeyType(var5);
               if (var6 != null) {
                  var1.add(var6);
               }
            }

            return var1;
         }
      }

      private static String clientKeyType(byte var0) {
         switch (var0) {
            case 1:
               return "RSA";
            case 3:
               return "DH_RSA";
            case 64:
               return "EC";
            case 65:
               return "EC_RSA";
            case 66:
               return "EC_EC";
            default:
               return null;
         }
      }
   }

   static final class OpenSslClientSessionContext extends OpenSslSessionContext {
      OpenSslClientSessionContext(ReferenceCountedOpenSslContext var1, OpenSslKeyMaterialProvider var2) {
         super(var1, var2, SSL.SSL_SESS_CACHE_CLIENT, new OpenSslClientSessionCache(var1.engineMap));
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
         this.manager.checkServerTrusted(var2, var3);
      }
   }
}
