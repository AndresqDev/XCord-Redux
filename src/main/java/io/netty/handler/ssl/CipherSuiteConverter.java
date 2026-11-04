package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CipherSuiteConverter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
   private static final Pattern JAVA_CIPHERSUITE_PATTERN = Pattern.compile("^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
   private static final Pattern OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile(
      "^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$"
   );
   private static final Pattern JAVA_AES_CBC_PATTERN = Pattern.compile("^(AES)_([0-9]+)_CBC$");
   private static final Pattern JAVA_AES_PATTERN = Pattern.compile("^(AES)_([0-9]+)_(.*)$");
   private static final Pattern OPENSSL_AES_CBC_PATTERN = Pattern.compile("^(AES)([0-9]+)$");
   private static final Pattern OPENSSL_AES_PATTERN = Pattern.compile("^(AES)([0-9]+)-(.*)$");
   private static final ConcurrentMap<String, String> j2o = PlatformDependent.newConcurrentHashMap();
   private static final ConcurrentMap<String, Map<String, String>> o2j = PlatformDependent.newConcurrentHashMap();
   private static final Map<String, String> j2oTls13;
   private static final Map<String, Map<String, String>> o2jTls13;

   static void clearCache() {
      j2o.clear();
      o2j.clear();
   }

   static boolean isJ2OCached(String var0, String var1) {
      return var1.equals(j2o.get(var0));
   }

   static boolean isO2JCached(String var0, String var1, String var2) {
      Map var3 = o2j.get(var0);
      return var3 == null ? false : var2.equals(var3.get(var1));
   }

   public static String toOpenSsl(String var0, boolean var1) {
      String var2 = j2o.get(var0);
      return var2 != null ? var2 : cacheFromJava(var0, var1);
   }

   private static String cacheFromJava(String var0, boolean var1) {
      String var2 = j2oTls13.get(var0);
      if (var2 != null) {
         return var1 ? var2 : var0;
      } else {
         String var3 = toOpenSslUncached(var0, var1);
         if (var3 == null) {
            return null;
         } else {
            j2o.putIfAbsent(var0, var3);
            String var4 = var0.substring(4);
            HashMap var5 = new HashMap(4);
            var5.put("", var4);
            var5.put("SSL", "SSL_" + var4);
            var5.put("TLS", "TLS_" + var4);
            o2j.put(var3, var5);
            logger.debug("Cipher suite mapping: {} => {}", var0, var3);
            return var3;
         }
      }
   }

   static String toOpenSslUncached(String var0, boolean var1) {
      String var2 = j2oTls13.get(var0);
      if (var2 != null) {
         return var1 ? var2 : var0;
      } else {
         Matcher var3 = JAVA_CIPHERSUITE_PATTERN.matcher(var0);
         if (!var3.matches()) {
            return null;
         } else {
            String var4 = toOpenSslHandshakeAlgo(var3.group(1));
            String var5 = toOpenSslBulkCipher(var3.group(2));
            String var6 = toOpenSslHmacAlgo(var3.group(3));
            if (var4.isEmpty()) {
               return var5 + '-' + var6;
            } else {
               return var5.contains("CHACHA20") ? var4 + '-' + var5 : var4 + '-' + var5 + '-' + var6;
            }
         }
      }
   }

   private static String toOpenSslHandshakeAlgo(String var0) {
      boolean var1 = var0.endsWith("_EXPORT");
      if (var1) {
         var0 = var0.substring(0, var0.length() - 7);
      }

      if ("RSA".equals(var0)) {
         var0 = "";
      } else if (var0.endsWith("_anon")) {
         var0 = 'A' + var0.substring(0, var0.length() - 5);
      }

      if (var1) {
         if (var0.isEmpty()) {
            var0 = "EXP";
         } else {
            var0 = "EXP-" + var0;
         }
      }

      return var0.replace('_', '-');
   }

   private static String toOpenSslBulkCipher(String var0) {
      if (var0.startsWith("AES_")) {
         Matcher var1 = JAVA_AES_CBC_PATTERN.matcher(var0);
         if (var1.matches()) {
            return var1.replaceFirst("$1$2");
         }

         var1 = JAVA_AES_PATTERN.matcher(var0);
         if (var1.matches()) {
            return var1.replaceFirst("$1$2-$3");
         }
      }

      if ("3DES_EDE_CBC".equals(var0)) {
         return "DES-CBC3";
      } else if ("RC4_128".equals(var0) || "RC4_40".equals(var0)) {
         return "RC4";
      } else if ("DES40_CBC".equals(var0) || "DES_CBC_40".equals(var0)) {
         return "DES-CBC";
      } else {
         return "RC2_CBC_40".equals(var0) ? "RC2-CBC" : var0.replace('_', '-');
      }
   }

   private static String toOpenSslHmacAlgo(String var0) {
      return var0;
   }

   public static String toJava(String var0, String var1) {
      Map var2 = o2j.get(var0);
      if (var2 == null) {
         var2 = cacheFromOpenSsl(var0);
         if (var2 == null) {
            return null;
         }
      }

      String var3 = (String)var2.get(var1);
      if (var3 == null) {
         String var4 = (String)var2.get("");
         if (var4 == null) {
            return null;
         }

         var3 = var1 + '_' + var4;
      }

      return var3;
   }

   private static Map<String, String> cacheFromOpenSsl(String var0) {
      Map var1 = o2jTls13.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         String var2 = toJavaUncached0(var0, false);
         if (var2 == null) {
            return null;
         } else {
            String var3 = "SSL_" + var2;
            String var4 = "TLS_" + var2;
            HashMap var5 = new HashMap(4);
            var5.put("", var2);
            var5.put("SSL", var3);
            var5.put("TLS", var4);
            o2j.putIfAbsent(var0, var5);
            j2o.putIfAbsent(var4, var0);
            j2o.putIfAbsent(var3, var0);
            logger.debug("Cipher suite mapping: {} => {}", var4, var0);
            logger.debug("Cipher suite mapping: {} => {}", var3, var0);
            return var5;
         }
      }
   }

   static String toJavaUncached(String var0) {
      return toJavaUncached0(var0, true);
   }

   private static String toJavaUncached0(String var0, boolean var1) {
      if (var1) {
         Map var2 = o2jTls13.get(var0);
         if (var2 != null) {
            return (String)var2.get("TLS");
         }
      }

      Matcher var8 = OPENSSL_CIPHERSUITE_PATTERN.matcher(var0);
      if (!var8.matches()) {
         return null;
      } else {
         String var3 = var8.group(1);
         boolean var4;
         if (var3 == null) {
            var3 = "";
            var4 = false;
         } else if (var3.startsWith("EXP-")) {
            var3 = var3.substring(4);
            var4 = true;
         } else if ("EXP".equals(var3)) {
            var3 = "";
            var4 = true;
         } else {
            var4 = false;
         }

         var3 = toJavaHandshakeAlgo(var3, var4);
         String var5 = toJavaBulkCipher(var8.group(2), var4);
         String var6 = toJavaHmacAlgo(var8.group(3));
         String var7 = var3 + "_WITH_" + var5 + '_' + var6;
         return var5.contains("CHACHA20") ? var7 + "_SHA256" : var7;
      }
   }

   private static String toJavaHandshakeAlgo(String var0, boolean var1) {
      if (var0.isEmpty()) {
         var0 = "RSA";
      } else if ("ADH".equals(var0)) {
         var0 = "DH_anon";
      } else if ("AECDH".equals(var0)) {
         var0 = "ECDH_anon";
      }

      var0 = var0.replace('-', '_');
      return var1 ? var0 + "_EXPORT" : var0;
   }

   private static String toJavaBulkCipher(String var0, boolean var1) {
      if (var0.startsWith("AES")) {
         Matcher var2 = OPENSSL_AES_CBC_PATTERN.matcher(var0);
         if (var2.matches()) {
            return var2.replaceFirst("$1_$2_CBC");
         }

         var2 = OPENSSL_AES_PATTERN.matcher(var0);
         if (var2.matches()) {
            return var2.replaceFirst("$1_$2_$3");
         }
      }

      if ("DES-CBC3".equals(var0)) {
         return "3DES_EDE_CBC";
      } else if ("RC4".equals(var0)) {
         return var1 ? "RC4_40" : "RC4_128";
      } else if ("DES-CBC".equals(var0)) {
         return var1 ? "DES_CBC_40" : "DES_CBC";
      } else if ("RC2-CBC".equals(var0)) {
         return var1 ? "RC2_CBC_40" : "RC2_CBC";
      } else {
         return var0.replace('-', '_');
      }
   }

   private static String toJavaHmacAlgo(String var0) {
      return var0;
   }

   static void convertToCipherStrings(Iterable<String> var0, StringBuilder var1, StringBuilder var2, boolean var3) {
      for (String var5 : var0) {
         if (var5 == null) {
            break;
         }

         String var6 = toOpenSsl(var5, var3);
         if (var6 == null) {
            var6 = var5;
         }

         if (!OpenSsl.isCipherSuiteAvailable(var6)) {
            throw new IllegalArgumentException("unsupported cipher suite: " + var5 + '(' + var6 + ')');
         }

         if (!SslUtils.isTLSv13Cipher(var6) && !SslUtils.isTLSv13Cipher(var5)) {
            var1.append(var6);
            var1.append(':');
         } else {
            var2.append(var6);
            var2.append(':');
         }
      }

      if (var1.length() == 0 && var2.length() == 0) {
         throw new IllegalArgumentException("empty cipher suites");
      } else {
         if (var1.length() > 0) {
            var1.setLength(var1.length() - 1);
         }

         if (var2.length() > 0) {
            var2.setLength(var2.length() - 1);
         }
      }
   }

   private CipherSuiteConverter() {
   }

   static {
      HashMap var0 = new HashMap();
      var0.put("TLS_AES_128_GCM_SHA256", "AEAD-AES128-GCM-SHA256");
      var0.put("TLS_AES_256_GCM_SHA384", "AEAD-AES256-GCM-SHA384");
      var0.put("TLS_CHACHA20_POLY1305_SHA256", "AEAD-CHACHA20-POLY1305-SHA256");
      j2oTls13 = Collections.unmodifiableMap(var0);
      HashMap var1 = new HashMap();
      var1.put("TLS_AES_128_GCM_SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
      var1.put("TLS_AES_256_GCM_SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
      var1.put("TLS_CHACHA20_POLY1305_SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
      var1.put("AEAD-AES128-GCM-SHA256", Collections.singletonMap("TLS", "TLS_AES_128_GCM_SHA256"));
      var1.put("AEAD-AES256-GCM-SHA384", Collections.singletonMap("TLS", "TLS_AES_256_GCM_SHA384"));
      var1.put("AEAD-CHACHA20-POLY1305-SHA256", Collections.singletonMap("TLS", "TLS_CHACHA20_POLY1305_SHA256"));
      o2jTls13 = Collections.unmodifiableMap(var1);
   }
}
