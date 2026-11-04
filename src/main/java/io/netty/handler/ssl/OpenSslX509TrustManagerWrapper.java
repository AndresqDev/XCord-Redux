package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class OpenSslX509TrustManagerWrapper {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(OpenSslX509TrustManagerWrapper.class);
   private static final OpenSslX509TrustManagerWrapper.TrustManagerWrapper WRAPPER;

   private OpenSslX509TrustManagerWrapper() {
   }

   static X509TrustManager wrapIfNeeded(X509TrustManager var0) {
      return WRAPPER.wrapIfNeeded(var0);
   }

   private static SSLContext newSSLContext() throws NoSuchAlgorithmException, NoSuchProviderException {
      return SSLContext.getInstance("TLS", "SunJSSE");
   }

   static {
      OpenSslX509TrustManagerWrapper.TrustManagerWrapper var0 = new OpenSslX509TrustManagerWrapper.TrustManagerWrapper() {
         @Override
         public X509TrustManager wrapIfNeeded(X509TrustManager var1) {
            return var1;
         }
      };
      Throwable var1 = null;
      Throwable var2 = PlatformDependent.getUnsafeUnavailabilityCause();
      if (var2 == null) {
         final SSLContext var3;
         try {
            var3 = newSSLContext();
            var3.init(null, new TrustManager[]{new X509TrustManager() {
               @Override
               public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
                  throw new CertificateException();
               }

               @Override
               public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
                  throw new CertificateException();
               }

               @Override
               public X509Certificate[] getAcceptedIssuers() {
                  return EmptyArrays.EMPTY_X509_CERTIFICATES;
               }
            }}, null);
         } catch (Throwable var6) {
            var3 = null;
            var1 = var6;
         }

         if (var1 != null) {
            LOGGER.debug("Unable to access wrapped TrustManager", var1);
         } else {
            Object var5 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     Field var1 = SSLContext.class.getDeclaredField("contextSpi");
                     long var2 = PlatformDependent.objectFieldOffset(var1);
                     Object var4 = PlatformDependent.getObject(var3, var2);
                     if (var4 != null) {
                        Class var5 = var4.getClass();

                        do {
                           try {
                              Field var6 = var5.getDeclaredField("trustManager");
                              long var7 = PlatformDependent.objectFieldOffset(var6);
                              Object var9 = PlatformDependent.getObject(var4, var7);
                              if (var9 instanceof X509ExtendedTrustManager) {
                                 return new OpenSslX509TrustManagerWrapper.UnsafeTrustManagerWrapper(var2, var7);
                              }
                           } catch (NoSuchFieldException var10) {
                           }

                           var5 = var5.getSuperclass();
                        } while (var5 != null);
                     }

                     throw new NoSuchFieldException();
                  } catch (NoSuchFieldException var11) {
                     return var11;
                  } catch (SecurityException var12) {
                     return var12;
                  }
               }
            });
            if (var5 instanceof Throwable) {
               LOGGER.debug("Unable to access wrapped TrustManager", (Throwable)var5);
            } else {
               var0 = (OpenSslX509TrustManagerWrapper.TrustManagerWrapper)var5;
            }
         }
      } else {
         LOGGER.debug("Unable to access wrapped TrustManager", var1);
      }

      WRAPPER = var0;
   }

   private interface TrustManagerWrapper {
      X509TrustManager wrapIfNeeded(X509TrustManager var1);
   }

   private static final class UnsafeTrustManagerWrapper implements OpenSslX509TrustManagerWrapper.TrustManagerWrapper {
      private final long spiOffset;
      private final long tmOffset;

      UnsafeTrustManagerWrapper(long var1, long var3) {
         this.spiOffset = var1;
         this.tmOffset = var3;
      }

      @SuppressJava6Requirement(
         reason = "Usage guarded by java version check"
      )
      @Override
      public X509TrustManager wrapIfNeeded(X509TrustManager var1) {
         if (!(var1 instanceof X509ExtendedTrustManager)) {
            try {
               SSLContext var2 = OpenSslX509TrustManagerWrapper.newSSLContext();
               var2.init(null, new TrustManager[]{var1}, null);
               Object var3 = PlatformDependent.getObject(var2, this.spiOffset);
               if (var3 != null) {
                  Object var4 = PlatformDependent.getObject(var3, this.tmOffset);
                  if (var4 instanceof X509ExtendedTrustManager) {
                     return (X509TrustManager)var4;
                  }
               }
            } catch (NoSuchAlgorithmException var5) {
               PlatformDependent.throwException(var5);
            } catch (KeyManagementException var6) {
               PlatformDependent.throwException(var6);
            } catch (NoSuchProviderException var7) {
               PlatformDependent.throwException(var7);
            }
         }

         return var1;
      }
   }
}
