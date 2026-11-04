package org.apache.http.conn.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.util.Args;

@Deprecated
public abstract class AbstractVerifier implements X509HostnameVerifier {
   private final Log log = LogFactory.getLog(this.getClass());
   static final String[] BAD_COUNTRY_2LDS = new String[]{"ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org"};

   @Override
   public final void verify(String var1, SSLSocket var2) throws IOException {
      Args.notNull(var1, "Host");
      SSLSession var3 = var2.getSession();
      if (var3 == null) {
         InputStream var4 = var2.getInputStream();
         var4.available();
         var3 = var2.getSession();
         if (var3 == null) {
            var2.startHandshake();
            var3 = var2.getSession();
         }
      }

      Certificate[] var6 = var3.getPeerCertificates();
      X509Certificate var5 = (X509Certificate)var6[0];
      this.verify(var1, var5);
   }

   @Override
   public final boolean verify(String var1, SSLSession var2) {
      try {
         Certificate[] var3 = var2.getPeerCertificates();
         X509Certificate var4 = (X509Certificate)var3[0];
         this.verify(var1, var4);
         return true;
      } catch (SSLException var5) {
         if (this.log.isDebugEnabled()) {
            this.log.debug(var5.getMessage(), var5);
         }

         return false;
      }
   }

   @Override
   public final void verify(String var1, X509Certificate var2) throws SSLException {
      List var3 = DefaultHostnameVerifier.getSubjectAltNames(var2);
      ArrayList var4 = new ArrayList();
      if (!InetAddressUtils.isIPv4Address(var1) && !InetAddressUtils.isIPv6Address(var1)) {
         for (SubjectName var9 : var3) {
            if (var9.getType() == 2) {
               var4.add(var9.getValue());
            }
         }
      } else {
         for (SubjectName var6 : var3) {
            if (var6.getType() == 7) {
               var4.add(var6.getValue());
            }
         }
      }

      X500Principal var8 = var2.getSubjectX500Principal();
      String var10 = DefaultHostnameVerifier.extractCN(var8.getName("RFC2253"));
      this.verify(var1, var10 != null ? new String[]{var10} : null, var4 != null && !var4.isEmpty() ? var4.toArray(new String[var4.size()]) : null);
   }

   public final void verify(String var1, String[] var2, String[] var3, boolean var4) throws SSLException {
      String var5 = var2 != null && var2.length > 0 ? var2[0] : null;
      List var6 = var3 != null && var3.length > 0 ? Arrays.asList(var3) : null;
      String var7 = InetAddressUtils.isIPv6Address(var1) ? DefaultHostnameVerifier.normaliseAddress(var1.toLowerCase(Locale.ROOT)) : var1;
      if (var6 != null) {
         for (String var9 : var6) {
            String var10 = InetAddressUtils.isIPv6Address(var9) ? DefaultHostnameVerifier.normaliseAddress(var9) : var9;
            if (matchIdentity(var7, var10, var4)) {
               return;
            }
         }

         throw new SSLException("Certificate for <" + var1 + "> doesn't match any " + "of the subject alternative names: " + var6);
      } else if (var5 != null) {
         String var8 = InetAddressUtils.isIPv6Address(var5) ? DefaultHostnameVerifier.normaliseAddress(var5) : var5;
         if (!matchIdentity(var7, var8, var4)) {
            throw new SSLException("Certificate for <" + var1 + "> doesn't match " + "common name of the certificate subject: " + var5);
         }
      } else {
         throw new SSLException("Certificate subject for <" + var1 + "> doesn't contain " + "a common name and does not have alternative names");
      }
   }

   private static boolean matchIdentity(String var0, String var1, boolean var2) {
      if (var0 == null) {
         return false;
      } else {
         String var3 = var0.toLowerCase(Locale.ROOT);
         String var4 = var1.toLowerCase(Locale.ROOT);
         String[] var5 = var4.split("\\.");
         boolean var6 = var5.length >= 3 && var5[0].endsWith("*") && (!var2 || validCountryWildcard(var5));
         if (!var6) {
            return var3.equals(var4);
         } else {
            String var8 = var5[0];
            boolean var7;
            if (var8.length() > 1) {
               String var9 = var8.substring(0, var8.length() - 1);
               String var10 = var4.substring(var8.length());
               String var11 = var3.substring(var9.length());
               var7 = var3.startsWith(var9) && var11.endsWith(var10);
            } else {
               var7 = var3.endsWith(var4.substring(1));
            }

            return var7 && (!var2 || countDots(var3) == countDots(var4));
         }
      }
   }

   private static boolean validCountryWildcard(String[] var0) {
      return var0.length == 3 && var0[2].length() == 2 ? Arrays.binarySearch(BAD_COUNTRY_2LDS, var0[1]) < 0 : true;
   }

   public static boolean acceptableCountryWildcard(String var0) {
      return validCountryWildcard(var0.split("\\."));
   }

   public static String[] getCNs(X509Certificate var0) {
      String var1 = var0.getSubjectX500Principal().toString();

      try {
         String var2 = DefaultHostnameVerifier.extractCN(var1);
         return var2 != null ? new String[]{var2} : null;
      } catch (SSLException var3) {
         return null;
      }
   }

   public static String[] getDNSSubjectAlts(X509Certificate var0) {
      List var1 = DefaultHostnameVerifier.getSubjectAltNames(var0);
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();

         for (SubjectName var4 : var1) {
            if (var4.getType() == 2) {
               var2.add(var4.getValue());
            }
         }

         return var2.isEmpty() ? var2.toArray(new String[var2.size()]) : null;
      }
   }

   public static int countDots(String var0) {
      int var1 = 0;

      for (int var2 = 0; var2 < var0.length(); var2++) {
         if (var0.charAt(var2) == '.') {
            var1++;
         }
      }

      return var1;
   }

   static {
      Arrays.sort((Object[])BAD_COUNTRY_2LDS);
   }
}
