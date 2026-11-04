package org.apache.http.impl.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Authenticator.RequestorType;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SystemDefaultCredentialsProvider implements CredentialsProvider {
   private static final Map<String, String> SCHEME_MAP = new ConcurrentHashMap<>();
   private final BasicCredentialsProvider internal = new BasicCredentialsProvider();

   private static String translateScheme(String var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = SCHEME_MAP.get(var0);
         return var1 != null ? var1 : var0;
      }
   }

   @Override
   public void setCredentials(AuthScope var1, Credentials var2) {
      this.internal.setCredentials(var1, var2);
   }

   private static PasswordAuthentication getSystemCreds(String var0, AuthScope var1, RequestorType var2) {
      return Authenticator.requestPasswordAuthentication(var1.getHost(), null, var1.getPort(), var0, null, translateScheme(var1.getScheme()), null, var2);
   }

   @Override
   public Credentials getCredentials(AuthScope var1) {
      Args.notNull(var1, "Auth scope");
      Credentials var2 = this.internal.getCredentials(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1.getHost();
         if (var3 != null) {
            HttpHost var4 = var1.getOrigin();
            String var5 = var4 != null ? var4.getSchemeName() : (var1.getPort() == 443 ? "https" : "http");
            PasswordAuthentication var6 = getSystemCreds(var5, var1, RequestorType.SERVER);
            if (var6 == null) {
               var6 = getSystemCreds(var5, var1, RequestorType.PROXY);
            }

            if (var6 == null) {
               var6 = getProxyCredentials("http", var1);
               if (var6 == null) {
                  var6 = getProxyCredentials("https", var1);
               }
            }

            if (var6 != null) {
               String var7 = System.getProperty("http.auth.ntlm.domain");
               if (var7 != null) {
                  return new NTCredentials(var6.getUserName(), new String(var6.getPassword()), null, var7);
               }

               return (Credentials)(
                  "NTLM".equalsIgnoreCase(var1.getScheme())
                     ? new NTCredentials(var6.getUserName(), new String(var6.getPassword()), null, null)
                     : new UsernamePasswordCredentials(var6.getUserName(), new String(var6.getPassword()))
               );
            }
         }

         return null;
      }
   }

   private static PasswordAuthentication getProxyCredentials(String var0, AuthScope var1) {
      String var2 = System.getProperty(var0 + ".proxyHost");
      if (var2 == null) {
         return null;
      } else {
         String var3 = System.getProperty(var0 + ".proxyPort");
         if (var3 == null) {
            return null;
         } else {
            try {
               AuthScope var4 = new AuthScope(var2, Integer.parseInt(var3));
               if (var1.match(var4) >= 0) {
                  String var5 = System.getProperty(var0 + ".proxyUser");
                  if (var5 == null) {
                     return null;
                  }

                  String var6 = System.getProperty(var0 + ".proxyPassword");
                  return new PasswordAuthentication(var5, var6 != null ? var6.toCharArray() : new char[0]);
               }
            } catch (NumberFormatException var7) {
            }

            return null;
         }
      }
   }

   @Override
   public void clear() {
      this.internal.clear();
   }

   static {
      SCHEME_MAP.put("Basic".toUpperCase(Locale.ROOT), "Basic");
      SCHEME_MAP.put("Digest".toUpperCase(Locale.ROOT), "Digest");
      SCHEME_MAP.put("NTLM".toUpperCase(Locale.ROOT), "NTLM");
      SCHEME_MAP.put("Negotiate".toUpperCase(Locale.ROOT), "SPNEGO");
      SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ROOT), "Kerberos");
   }
}
