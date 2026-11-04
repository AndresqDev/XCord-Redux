package io.netty.handler.ssl;

import java.security.Provider;

public enum SslProvider {
   JDK,
   OPENSSL,
   OPENSSL_REFCNT;

   public static boolean isAlpnSupported(SslProvider var0) {
      switch (var0) {
         case JDK:
            return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
         case OPENSSL:
         case OPENSSL_REFCNT:
            return OpenSsl.isAlpnSupported();
         default:
            throw new Error("Unknown SslProvider: " + var0);
      }
   }

   public static boolean isTlsv13Supported(SslProvider var0) {
      return isTlsv13Supported(var0, null);
   }

   public static boolean isTlsv13Supported(SslProvider var0, Provider var1) {
      switch (var0) {
         case JDK:
            return SslUtils.isTLSv13SupportedByJDK(var1);
         case OPENSSL:
         case OPENSSL_REFCNT:
            return OpenSsl.isTlsv13Supported();
         default:
            throw new Error("Unknown SslProvider: " + var0);
      }
   }

   static boolean isTlsv13EnabledByDefault(SslProvider var0, Provider var1) {
      switch (var0) {
         case JDK:
            return SslUtils.isTLSv13EnabledByJDK(var1);
         case OPENSSL:
         case OPENSSL_REFCNT:
            return OpenSsl.isTlsv13Supported();
         default:
            throw new Error("Unknown SslProvider: " + var0);
      }
   }
}
