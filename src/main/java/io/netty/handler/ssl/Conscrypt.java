package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
   private static final Method IS_CONSCRYPT_SSLENGINE;

   static boolean isAvailable() {
      return IS_CONSCRYPT_SSLENGINE != null;
   }

   static boolean isEngineSupported(SSLEngine var0) {
      try {
         return IS_CONSCRYPT_SSLENGINE != null && (Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, var0);
      } catch (IllegalAccessException var2) {
         return false;
      } catch (InvocationTargetException var3) {
         throw new RuntimeException(var3);
      }
   }

   private Conscrypt() {
   }

   static {
      Method var0 = null;
      if (PlatformDependent.javaVersion() >= 8 && PlatformDependent.javaVersion() < 15 || PlatformDependent.isAndroid()) {
         try {
            Class var1 = Class.forName("org.conscrypt.OpenSSLProvider", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
            var1.newInstance();
            Class var2 = Class.forName("org.conscrypt.Conscrypt", true, PlatformDependent.getClassLoader(ConscryptAlpnSslEngine.class));
            var0 = var2.getMethod("isConscrypt", SSLEngine.class);
         } catch (Throwable var3) {
         }
      }

      IS_CONSCRYPT_SSLENGINE = var0;
   }
}
