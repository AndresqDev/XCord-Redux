package org.eclipse.aether.transport.http;

import java.util.Arrays;
import java.util.Objects;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.util.ConfigUtils;

final class SslConfig {
   private static final String CIPHER_SUITES = "https.cipherSuites";
   private static final String PROTOCOLS = "https.protocols";
   final SSLContext context;
   final HostnameVerifier verifier;
   final String[] cipherSuites;
   final String[] protocols;

   SslConfig(RepositorySystemSession var1, AuthenticationContext var2) {
      this.context = var2 != null ? var2.get("ssl.context", SSLContext.class) : null;
      this.verifier = var2 != null ? var2.get("ssl.hostnameVerifier", HostnameVerifier.class) : null;
      this.cipherSuites = split(get(var1, "https.cipherSuites"));
      this.protocols = split(get(var1, "https.protocols"));
   }

   private static String get(RepositorySystemSession var0, String var1) {
      String var2 = ConfigUtils.getString(var0, null, "aether.connector." + var1, var1);
      if (var2 == null) {
         var2 = System.getProperty(var1);
      }

      return var2;
   }

   private static String[] split(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.split(",+") : null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         SslConfig var2 = (SslConfig)var1;
         return Objects.equals(this.context, var2.context)
            && Objects.equals(this.verifier, var2.verifier)
            && Arrays.equals((Object[])this.cipherSuites, (Object[])var2.cipherSuites)
            && Arrays.equals((Object[])this.protocols, (Object[])var2.protocols);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + hash(this.context);
      var1 = var1 * 31 + hash(this.verifier);
      var1 = var1 * 31 + Arrays.hashCode((Object[])this.cipherSuites);
      return var1 * 31 + Arrays.hashCode((Object[])this.protocols);
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }
}
