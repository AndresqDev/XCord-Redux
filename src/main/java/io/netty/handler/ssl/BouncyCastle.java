package io.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

final class BouncyCastle {
   private static final boolean BOUNCY_CASTLE_ON_CLASSPATH;

   static boolean isAvailable() {
      return BOUNCY_CASTLE_ON_CLASSPATH;
   }

   static boolean isInUse(SSLEngine var0) {
      return var0.getClass().getPackage().getName().startsWith("org.bouncycastle.jsse.provider");
   }

   private BouncyCastle() {
   }

   static {
      boolean var0 = false;

      try {
         Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider");
         var0 = true;
      } catch (Throwable var2) {
      }

      BOUNCY_CASTLE_ON_CLASSPATH = var0;
   }
}
