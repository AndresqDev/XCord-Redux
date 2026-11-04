package org.apache.http.conn.ssl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.DnsUtils;
import org.apache.http.conn.util.DomainType;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.conn.util.PublicSuffixMatcher;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public final class DefaultHostnameVerifier implements HostnameVerifier {
   private final Log log = LogFactory.getLog(this.getClass());
   private final PublicSuffixMatcher publicSuffixMatcher;

   public DefaultHostnameVerifier(PublicSuffixMatcher var1) {
      this.publicSuffixMatcher = var1;
   }

   public DefaultHostnameVerifier() {
      this(null);
   }

   @Override
   public boolean verify(String var1, SSLSession var2) {
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

   public void verify(String var1, X509Certificate var2) throws SSLException {
      DefaultHostnameVerifier.HostNameType var3 = determineHostFormat(var1);
      List var4 = getSubjectAltNames(var2);
      if (var4 != null && !var4.isEmpty()) {
         switch (var3) {
            case IPv4:
               matchIPAddress(var1, var4);
               break;
            case IPv6:
               matchIPv6Address(var1, var4);
               break;
            default:
               matchDNSName(var1, var4, this.publicSuffixMatcher);
         }
      } else {
         X500Principal var5 = var2.getSubjectX500Principal();
         String var6 = extractCN(var5.getName("RFC2253"));
         if (var6 == null) {
            throw new SSLException("Certificate subject for <" + var1 + "> doesn't contain " + "a common name and does not have alternative names");
         }

         matchCN(var1, var6, this.publicSuffixMatcher);
      }
   }

   static void matchIPAddress(String var0, List<SubjectName> var1) throws SSLException {
      for (int var2 = 0; var2 < var1.size(); var2++) {
         SubjectName var3 = (SubjectName)var1.get(var2);
         if (var3.getType() == 7 && var0.equals(var3.getValue())) {
            return;
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + var0 + "> doesn't match any " + "of the subject alternative names: " + var1);
   }

   static void matchIPv6Address(String var0, List<SubjectName> var1) throws SSLException {
      String var2 = normaliseAddress(var0);

      for (int var3 = 0; var3 < var1.size(); var3++) {
         SubjectName var4 = (SubjectName)var1.get(var3);
         if (var4.getType() == 7) {
            String var5 = normaliseAddress(var4.getValue());
            if (var2.equals(var5)) {
               return;
            }
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + var0 + "> doesn't match any " + "of the subject alternative names: " + var1);
   }

   static void matchDNSName(String var0, List<SubjectName> var1, PublicSuffixMatcher var2) throws SSLException {
      String var3 = DnsUtils.normalize(var0);

      for (int var4 = 0; var4 < var1.size(); var4++) {
         SubjectName var5 = (SubjectName)var1.get(var4);
         if (var5.getType() == 2) {
            String var6 = DnsUtils.normalize(var5.getValue());
            if (matchIdentityStrict(var3, var6, var2)) {
               return;
            }
         }
      }

      throw new SSLPeerUnverifiedException("Certificate for <" + var0 + "> doesn't match any " + "of the subject alternative names: " + var1);
   }

   static void matchCN(String var0, String var1, PublicSuffixMatcher var2) throws SSLException {
      String var3 = DnsUtils.normalize(var0);
      String var4 = DnsUtils.normalize(var1);
      if (!matchIdentityStrict(var3, var4, var2)) {
         throw new SSLPeerUnverifiedException("Certificate for <" + var0 + "> doesn't match " + "common name of the certificate subject: " + var1);
      }
   }

   static boolean matchDomainRoot(String var0, String var1) {
      return var1 == null ? false : var0.endsWith(var1) && (var0.length() == var1.length() || var0.charAt(var0.length() - var1.length() - 1) == '.');
   }

   private static boolean matchIdentity(String var0, String var1, PublicSuffixMatcher var2, DomainType var3, boolean var4) {
      if (var2 != null && var0.contains(".") && !matchDomainRoot(var0, var2.getDomainRoot(var1, var3))) {
         return false;
      } else {
         int var5 = var1.indexOf(42);
         if (var5 != -1) {
            String var6 = var1.substring(0, var5);
            String var7 = var1.substring(var5 + 1);
            if (!var6.isEmpty() && !var0.startsWith(var6)) {
               return false;
            } else if (!var7.isEmpty() && !var0.endsWith(var7)) {
               return false;
            } else {
               if (var4) {
                  String var8 = var0.substring(var6.length(), var0.length() - var7.length());
                  if (var8.contains(".")) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return var0.equalsIgnoreCase(var1);
         }
      }
   }

   static boolean matchIdentity(String var0, String var1, PublicSuffixMatcher var2) {
      return matchIdentity(var0, var1, var2, null, false);
   }

   static boolean matchIdentity(String var0, String var1) {
      return matchIdentity(var0, var1, null, null, false);
   }

   static boolean matchIdentityStrict(String var0, String var1, PublicSuffixMatcher var2) {
      return matchIdentity(var0, var1, var2, null, true);
   }

   static boolean matchIdentityStrict(String var0, String var1) {
      return matchIdentity(var0, var1, null, null, true);
   }

   static boolean matchIdentity(String var0, String var1, PublicSuffixMatcher var2, DomainType var3) {
      return matchIdentity(var0, var1, var2, var3, false);
   }

   static boolean matchIdentityStrict(String var0, String var1, PublicSuffixMatcher var2, DomainType var3) {
      return matchIdentity(var0, var1, var2, var3, true);
   }

   static String extractCN(String var0) throws SSLException {
      if (var0 == null) {
         return null;
      } else {
         try {
            LdapName var1 = new LdapName(var0);
            List var2 = var1.getRdns();

            for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
               Rdn var4 = (Rdn)var2.get(var3);
               Attributes var5 = var4.toAttributes();
               Attribute var6 = var5.get("cn");
               if (var6 != null) {
                  try {
                     Object var7 = var6.get();
                     if (var7 != null) {
                        return var7.toString();
                     }
                  } catch (NoSuchElementException var8) {
                  } catch (NamingException var9) {
                  }
               }
            }

            return null;
         } catch (InvalidNameException var10) {
            throw new SSLException(var0 + " is not a valid X500 distinguished name");
         }
      }
   }

   static DefaultHostnameVerifier.HostNameType determineHostFormat(String var0) {
      if (InetAddressUtils.isIPv4Address(var0)) {
         return DefaultHostnameVerifier.HostNameType.IPv4;
      } else {
         String var1 = var0;
         if (var0.startsWith("[") && var0.endsWith("]")) {
            var1 = var0.substring(1, var0.length() - 1);
         }

         return InetAddressUtils.isIPv6Address(var1) ? DefaultHostnameVerifier.HostNameType.IPv6 : DefaultHostnameVerifier.HostNameType.DNS;
      }
   }

   static List<SubjectName> getSubjectAltNames(X509Certificate var0) {
      try {
         Collection var1 = var0.getSubjectAlternativeNames();
         if (var1 == null) {
            return Collections.emptyList();
         } else {
            ArrayList var2 = new ArrayList();

            for (List var4 : var1) {
               Integer var5 = var4.size() >= 2 ? (Integer)var4.get(0) : null;
               if (var5 != null && (var5 == 2 || var5 == 7)) {
                  Object var6 = var4.get(1);
                  if (var6 instanceof String) {
                     var2.add(new SubjectName((String)var6, var5));
                  } else if (var6 instanceof byte[]) {
                  }
               }
            }

            return var2;
         }
      } catch (CertificateParsingException var7) {
         return Collections.emptyList();
      }
   }

   static String normaliseAddress(String var0) {
      if (var0 == null) {
         return var0;
      } else {
         try {
            InetAddress var1 = InetAddress.getByName(var0);
            return var1.getHostAddress();
         } catch (UnknownHostException var2) {
            return var0;
         }
      }
   }

   static enum HostNameType {
      IPv4(7),
      IPv6(7),
      DNS(2);

      final int subjectType;

      private HostNameType(int var3) {
         this.subjectType = var3;
      }
   }
}
