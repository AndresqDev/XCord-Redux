package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.Library;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OpenSsl {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
   private static final Throwable UNAVAILABILITY_CAUSE;
   static final List<String> DEFAULT_CIPHERS;
   static final Set<String> AVAILABLE_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
   private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
   private static final boolean USE_KEYMANAGER_FACTORY;
   private static final boolean SUPPORTS_OCSP;
   private static final boolean TLSV13_SUPPORTED;
   private static final boolean IS_BORINGSSL;
   static final Set<String> SUPPORTED_PROTOCOLS_SET;
   static final String[] EXTRA_SUPPORTED_TLS_1_3_CIPHERS;
   static final String EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING;
   static final String[] NAMED_GROUPS;
   private static final String[] DEFAULT_NAMED_GROUPS = new String[]{"x25519", "secp256r1", "secp384r1", "secp521r1"};
   private static final String CERT = "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----";
   private static final String KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----";

   static String checkTls13Ciphers(InternalLogger var0, String var1) {
      if (IS_BORINGSSL && !var1.isEmpty()) {
         assert EXTRA_SUPPORTED_TLS_1_3_CIPHERS.length > 0;

         HashSet var2 = new HashSet(EXTRA_SUPPORTED_TLS_1_3_CIPHERS.length);
         Collections.addAll(var2, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
         boolean var3 = false;

         for (String var7 : var1.split(":")) {
            if (var2.isEmpty()) {
               var3 = true;
               break;
            }

            if (!var2.remove(var7) && !var2.remove(CipherSuiteConverter.toJava(var7, "TLS"))) {
               var3 = true;
               break;
            }
         }

         var3 |= !var2.isEmpty();
         if (var3) {
            if (var0.isInfoEnabled()) {
               StringBuilder var10 = new StringBuilder(128);

               for (String var8 : var1.split(":")) {
                  var10.append(CipherSuiteConverter.toJava(var8, "TLS")).append(":");
               }

               var10.setLength(var10.length() - 1);
               var0.info(
                  "BoringSSL doesn't allow to enable or disable TLSv1.3 ciphers explicitly. Provided TLSv1.3 ciphers: '{}', default TLSv1.3 ciphers that will be used: '{}'.",
                  var10,
                  EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING
               );
            }

            return EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING;
         }
      }

      return var1;
   }

   static boolean isSessionCacheSupported() {
      return (long)version() >= 269484032L;
   }

   static X509Certificate selfSignedCertificate() throws CertificateException {
      return (X509Certificate)SslContext.X509_CERT_FACTORY
         .generateCertificate(
            new ByteArrayInputStream(
               "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----"
                  .getBytes(CharsetUtil.US_ASCII)
            )
         );
   }

   private static boolean doesSupportOcsp() {
      boolean var0 = false;
      if ((long)version() >= 268443648L) {
         long var1 = -1L;

         try {
            var1 = SSLContext.make(16, 1);
            SSLContext.enableOcsp(var1, false);
            var0 = true;
         } catch (Exception var7) {
         } finally {
            if (var1 != -1L) {
               SSLContext.free(var1);
            }
         }
      }

      return var0;
   }

   private static boolean doesSupportProtocol(int var0, int var1) {
      if (var1 == 0) {
         return false;
      } else {
         long var2 = -1L;

         boolean var5;
         try {
            var2 = SSLContext.make(var0, 2);
            return true;
         } catch (Exception var9) {
            var5 = false;
         } finally {
            if (var2 != -1L) {
               SSLContext.free(var2);
            }
         }

         return var5;
      }
   }

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   @Deprecated
   public static boolean isAlpnSupported() {
      return (long)version() >= 268443648L;
   }

   public static boolean isOcspSupported() {
      return SUPPORTS_OCSP;
   }

   public static int version() {
      return isAvailable() ? SSL.version() : -1;
   }

   public static String versionString() {
      return isAvailable() ? SSL.versionString() : null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)new UnsatisfiedLinkError("failed to load the required native library").initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   @Deprecated
   public static Set<String> availableCipherSuites() {
      return availableOpenSslCipherSuites();
   }

   public static Set<String> availableOpenSslCipherSuites() {
      return AVAILABLE_OPENSSL_CIPHER_SUITES;
   }

   public static Set<String> availableJavaCipherSuites() {
      return AVAILABLE_JAVA_CIPHER_SUITES;
   }

   public static boolean isCipherSuiteAvailable(String var0) {
      String var1 = CipherSuiteConverter.toOpenSsl(var0, IS_BORINGSSL);
      if (var1 != null) {
         var0 = var1;
      }

      return AVAILABLE_OPENSSL_CIPHER_SUITES.contains(var0);
   }

   public static boolean supportsKeyManagerFactory() {
      return SUPPORTS_KEYMANAGER_FACTORY;
   }

   @Deprecated
   public static boolean supportsHostnameValidation() {
      return isAvailable();
   }

   static boolean useKeyManagerFactory() {
      return USE_KEYMANAGER_FACTORY;
   }

   static long memoryAddress(ByteBuf var0) {
      assert var0.isDirect();

      return var0.hasMemoryAddress() ? var0.memoryAddress() : Buffer.address(var0.internalNioBuffer(0, var0.readableBytes()));
   }

   private OpenSsl() {
   }

   private static void loadTcNative() throws Exception {
      String var0 = PlatformDependent.normalizedOs();
      String var1 = PlatformDependent.normalizedArch();
      LinkedHashSet var2 = new LinkedHashSet(5);
      String var3 = "netty_tcnative";
      if ("linux".equals(var0)) {
         for (String var6 : PlatformDependent.normalizedLinuxClassifiers()) {
            var2.add(var3 + "_" + var0 + '_' + var1 + "_" + var6);
         }

         var2.add(var3 + "_" + var0 + '_' + var1);
         var2.add(var3 + "_" + var0 + '_' + var1 + "_fedora");
      } else {
         var2.add(var3 + "_" + var0 + '_' + var1);
      }

      var2.add(var3 + "_" + var1);
      var2.add(var3);
      NativeLibraryLoader.loadFirstAvailable(PlatformDependent.getClassLoader(SSLContext.class), var2.toArray(new String[0]));
   }

   private static boolean initializeTcNative(String var0) throws Exception {
      return Library.initialize("provided", var0);
   }

   static void releaseIfNeeded(ReferenceCounted var0) {
      if (var0.refCnt() > 0) {
         ReferenceCountUtil.safeRelease(var0);
      }
   }

   static boolean isTlsv13Supported() {
      return TLSV13_SUPPORTED;
   }

   static boolean isBoringSSL() {
      return IS_BORINGSSL;
   }

   static {
      Object var0 = null;
      if (SystemPropertyUtil.getBoolean("io.netty.handler.ssl.noOpenSsl", false)) {
         var0 = new UnsupportedOperationException("OpenSSL was explicit disabled with -Dio.netty.handler.ssl.noOpenSsl=true");
         logger.debug("netty-tcnative explicit disabled; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", (Throwable)var0);
      } else {
         try {
            Class.forName("io.netty.internal.tcnative.SSLContext", false, PlatformDependent.getClassLoader(OpenSsl.class));
         } catch (ClassNotFoundException var65) {
            var0 = var65;
            logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
         }

         if (var0 == null) {
            try {
               loadTcNative();
            } catch (Throwable var64) {
               var0 = var64;
               logger.debug(
                  "Failed to load netty-tcnative; "
                     + OpenSslEngine.class.getSimpleName()
                     + " will be unavailable, unless the application has already loaded the symbols by some other means. See https://netty.io/wiki/forked-tomcat-native.html for more information.",
                  var64
               );
            }

            try {
               String var1 = SystemPropertyUtil.get("io.netty.handler.ssl.openssl.engine", null);
               if (var1 == null) {
                  logger.debug("Initialize netty-tcnative using engine: 'default'");
               } else {
                  logger.debug("Initialize netty-tcnative using engine: '{}'", var1);
               }

               initializeTcNative(var1);
               var0 = null;
            } catch (Throwable var70) {
               if (var0 == null) {
                  var0 = var70;
               }

               logger.debug(
                  "Failed to initialize netty-tcnative; "
                     + OpenSslEngine.class.getSimpleName()
                     + " will be unavailable. See https://netty.io/wiki/forked-tomcat-native.html for more information.",
                  var70
               );
            }
         }
      }

      UNAVAILABILITY_CAUSE = (Throwable)var0;
      if (var0 == null) {
         logger.debug("netty-tcnative using native library: {}", SSL.versionString());
         ArrayList var71 = new ArrayList();
         LinkedHashSet var2 = new LinkedHashSet(128);
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         String[] var6 = DEFAULT_NAMED_GROUPS;
         String[] var7 = new String[var6.length];

         for (int var8 = 0; var8 < var6.length; var8++) {
            var7[var8] = GroupsConverter.toOpenSsl(var6[var8]);
         }

         IS_BORINGSSL = "BoringSSL".equals(versionString());
         if (!IS_BORINGSSL) {
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS = EmptyArrays.EMPTY_STRINGS;
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = "";
         } else {
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS = new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256"};
            StringBuilder var72 = new StringBuilder(128);

            for (String var12 : EXTRA_SUPPORTED_TLS_1_3_CIPHERS) {
               var72.append(var12).append(":");
            }

            var72.setLength(var72.length() - 1);
            EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = var72.toString();
         }

         try {
            long var73 = SSLContext.make(63, 1);
            long var77 = 0L;
            long var80 = 0L;
            long var14 = 0L;
            long var16 = 0L;

            try {
               if (SslProvider.isTlsv13Supported(SslProvider.JDK)) {
                  try {
                     StringBuilder var18 = new StringBuilder();

                     for (String var20 : SslUtils.TLSV13_CIPHERS) {
                        String var21 = CipherSuiteConverter.toOpenSsl(var20, IS_BORINGSSL);
                        if (var21 != null) {
                           var18.append(var21).append(':');
                        }
                     }

                     if (var18.length() == 0) {
                        var5 = false;
                     } else {
                        var18.setLength(var18.length() - 1);
                        SSLContext.setCipherSuite(var73, var18.toString(), true);
                        var5 = true;
                     }
                  } catch (Exception var67) {
                     var5 = false;
                  }
               }

               SSLContext.setCipherSuite(var73, "ALL", false);
               long var81 = SSL.newSSL(var73, true);

               try {
                  for (String var23 : SSL.getCiphers(var81)) {
                     if (var23 != null && !var23.isEmpty() && !var2.contains(var23) && (var5 || !SslUtils.isTLSv13Cipher(var23))) {
                        var2.add(var23);
                     }
                  }

                  if (IS_BORINGSSL) {
                     Collections.addAll(var2, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
                     Collections.addAll(var2, "AEAD-AES128-GCM-SHA256", "AEAD-AES256-GCM-SHA384", "AEAD-CHACHA20-POLY1305-SHA256");
                  }

                  PemPrivateKey var83 = PemPrivateKey.valueOf(
                     "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----"
                        .getBytes(CharsetUtil.US_ASCII)
                  );

                  try {
                     SSLContext.setCertificateCallback(var73, null);
                     X509Certificate var86 = selfSignedCertificate();
                     var77 = ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, var86);
                     var14 = SSL.parseX509Chain(var77);
                     var80 = ReferenceCountedOpenSslContext.toBIO(UnpooledByteBufAllocator.DEFAULT, var83.retain());
                     var16 = SSL.parsePrivateKey(var80, null);
                     SSL.setKeyMaterial(var81, var14, var16);
                     var3 = true;

                     try {
                        boolean var88 = SystemPropertyUtil.contains("io.netty.handler.ssl.openssl.useKeyManagerFactory");
                        if (!IS_BORINGSSL) {
                           var4 = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useKeyManagerFactory", true);
                           if (var88) {
                              logger.info(
                                 "System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and so will be ignored in the future"
                              );
                           }
                        } else {
                           var4 = true;
                           if (var88) {
                              logger.info(
                                 "System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and will be ignored when using BoringSSL"
                              );
                           }
                        }
                     } catch (Throwable var61) {
                        logger.debug("Failed to get useKeyManagerFactory system property.");
                     }
                  } catch (Error var62) {
                     logger.debug("KeyManagerFactory not supported.");
                  } finally {
                     var83.release();
                  }
               } finally {
                  SSL.freeSSL(var81);
                  if (var77 != 0L) {
                     SSL.freeBIO(var77);
                  }

                  if (var80 != 0L) {
                     SSL.freeBIO(var80);
                  }

                  if (var14 != 0L) {
                     SSL.freeX509Chain(var14);
                  }

                  if (var16 != 0L) {
                     SSL.freePrivateKey(var16);
                  }
               }

               String var84 = SystemPropertyUtil.get("jdk.tls.namedGroups", null);
               if (var84 == null) {
                  var6 = var7;
               } else {
                  String[] var87 = var84.split(",");
                  LinkedHashSet var89 = new LinkedHashSet(var87.length);
                  LinkedHashSet var90 = new LinkedHashSet(var87.length);
                  LinkedHashSet var24 = new LinkedHashSet();

                  for (String var28 : var87) {
                     String var29 = GroupsConverter.toOpenSsl(var28);
                     if (SSLContext.setCurvesList(var73, new String[]{var29})) {
                        var90.add(var29);
                        var89.add(var28);
                     } else {
                        var24.add(var28);
                     }
                  }

                  if (var89.isEmpty()) {
                     var6 = var7;
                     logger.info(
                        "All configured namedGroups are not supported: {}. Use default: {}.",
                        Arrays.toString(var24.toArray(EmptyArrays.EMPTY_STRINGS)),
                        Arrays.toString((Object[])DEFAULT_NAMED_GROUPS)
                     );
                  } else {
                     String[] var91 = var89.toArray(EmptyArrays.EMPTY_STRINGS);
                     if (var24.isEmpty()) {
                        logger.info("Using configured namedGroups -D 'jdk.tls.namedGroup': {} ", Arrays.toString((Object[])var91));
                     } else {
                        logger.info(
                           "Using supported configured namedGroups: {}. Unsupported namedGroups: {}. ",
                           Arrays.toString((Object[])var91),
                           Arrays.toString(var24.toArray(EmptyArrays.EMPTY_STRINGS))
                        );
                     }

                     var6 = var90.toArray(EmptyArrays.EMPTY_STRINGS);
                  }
               }
            } finally {
               SSLContext.free(var73);
            }
         } catch (Exception var69) {
            logger.warn("Failed to get the list of available OpenSSL cipher suites.", (Throwable)var69);
         }

         NAMED_GROUPS = var6;
         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(var2);
         LinkedHashSet var74 = new LinkedHashSet(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2);

         for (String var78 : AVAILABLE_OPENSSL_CIPHER_SUITES) {
            if (!SslUtils.isTLSv13Cipher(var78)) {
               var74.add(CipherSuiteConverter.toJava(var78, "TLS"));
               var74.add(CipherSuiteConverter.toJava(var78, "SSL"));
            } else {
               var74.add(var78);
            }
         }

         SslUtils.addIfSupported(var74, var71, SslUtils.DEFAULT_CIPHER_SUITES);
         SslUtils.addIfSupported(var74, var71, SslUtils.TLSV13_CIPHER_SUITES);
         SslUtils.addIfSupported(var74, var71, EXTRA_SUPPORTED_TLS_1_3_CIPHERS);
         SslUtils.useFallbackCiphersIfDefaultIsEmpty(var71, var74);
         DEFAULT_CIPHERS = Collections.unmodifiableList(var71);
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(var74);
         LinkedHashSet var76 = new LinkedHashSet(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size());
         var76.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
         var76.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
         AVAILABLE_CIPHER_SUITES = var76;
         SUPPORTS_KEYMANAGER_FACTORY = var3;
         USE_KEYMANAGER_FACTORY = var4;
         LinkedHashSet var79 = new LinkedHashSet(6);
         var79.add("SSLv2Hello");
         if (doesSupportProtocol(1, SSL.SSL_OP_NO_SSLv2)) {
            var79.add("SSLv2");
         }

         if (doesSupportProtocol(2, SSL.SSL_OP_NO_SSLv3)) {
            var79.add("SSLv3");
         }

         if (doesSupportProtocol(4, SSL.SSL_OP_NO_TLSv1)) {
            var79.add("TLSv1");
         }

         if (doesSupportProtocol(8, SSL.SSL_OP_NO_TLSv1_1)) {
            var79.add("TLSv1.1");
         }

         if (doesSupportProtocol(16, SSL.SSL_OP_NO_TLSv1_2)) {
            var79.add("TLSv1.2");
         }

         if (var5 && doesSupportProtocol(32, SSL.SSL_OP_NO_TLSv1_3)) {
            var79.add("TLSv1.3");
            TLSV13_SUPPORTED = true;
         } else {
            TLSV13_SUPPORTED = false;
         }

         SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(var79);
         SUPPORTS_OCSP = doesSupportOcsp();
         if (logger.isDebugEnabled()) {
            logger.debug("Supported protocols (OpenSSL): {} ", SUPPORTED_PROTOCOLS_SET);
            logger.debug("Default cipher suites (OpenSSL): {}", DEFAULT_CIPHERS);
         }
      } else {
         DEFAULT_CIPHERS = Collections.emptyList();
         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_CIPHER_SUITES = Collections.emptySet();
         SUPPORTS_KEYMANAGER_FACTORY = false;
         USE_KEYMANAGER_FACTORY = false;
         SUPPORTED_PROTOCOLS_SET = Collections.emptySet();
         SUPPORTS_OCSP = false;
         TLSV13_SUPPORTED = false;
         IS_BORINGSSL = false;
         EXTRA_SUPPORTED_TLS_1_3_CIPHERS = EmptyArrays.EMPTY_STRINGS;
         EXTRA_SUPPORTED_TLS_1_3_CIPHERS_STRING = "";
         NAMED_GROUPS = DEFAULT_NAMED_GROUPS;
      }
   }
}
