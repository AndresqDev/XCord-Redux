package org.eclipse.aether.transport.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.eclipse.aether.repository.AuthenticationContext;

final class DeferredCredentialsProvider implements CredentialsProvider {
   private final CredentialsProvider delegate = new BasicCredentialsProvider();
   private final Map<AuthScope, DeferredCredentialsProvider.Factory> factories = new HashMap<>();

   public void setCredentials(AuthScope var1, DeferredCredentialsProvider.Factory var2) {
      this.factories.put(var1, var2);
   }

   @Override
   public void setCredentials(AuthScope var1, Credentials var2) {
      this.delegate.setCredentials(var1, var2);
   }

   @Override
   public Credentials getCredentials(AuthScope var1) {
      synchronized (this.factories) {
         Iterator var3 = this.factories.entrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            if (var1.match((AuthScope)var4.getKey()) >= 0) {
               var3.remove();
               this.delegate.setCredentials((AuthScope)var4.getKey(), ((DeferredCredentialsProvider.Factory)var4.getValue()).newCredentials());
            }
         }
      }

      return this.delegate.getCredentials(var1);
   }

   @Override
   public void clear() {
      this.delegate.clear();
   }

   static class BasicFactory implements DeferredCredentialsProvider.Factory {
      private final AuthenticationContext authContext;

      BasicFactory(AuthenticationContext var1) {
         this.authContext = var1;
      }

      @Override
      public Credentials newCredentials() {
         String var1 = this.authContext.get("username");
         if (var1 == null) {
            return null;
         } else {
            String var2 = this.authContext.get("password");
            return new UsernamePasswordCredentials(var1, var2);
         }
      }
   }

   interface Factory {
      Credentials newCredentials();
   }

   static class NtlmFactory implements DeferredCredentialsProvider.Factory {
      private final AuthenticationContext authContext;

      NtlmFactory(AuthenticationContext var1) {
         this.authContext = var1;
      }

      @Override
      public Credentials newCredentials() {
         String var1 = this.authContext.get("username");
         if (var1 == null) {
            return null;
         } else {
            String var2 = this.authContext.get("password");
            String var3 = this.authContext.get("ntlm.domain");
            String var4 = this.authContext.get("ntlm.workstation");
            if (var3 == null) {
               int var5 = var1.indexOf(92);
               if (var5 < 0) {
                  var3 = guessDomain();
               } else {
                  var3 = var1.substring(0, var5);
                  var1 = var1.substring(var5 + 1);
               }
            }

            if (var4 == null) {
               var4 = guessWorkstation();
            }

            return new NTCredentials(var1, var2, var4, var3);
         }
      }

      private static String guessDomain() {
         return safeNtlmString(System.getProperty("http.auth.ntlm.domain"), System.getenv("USERDOMAIN"));
      }

      private static String guessWorkstation() {
         String var0 = null;

         try {
            var0 = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException var2) {
         }

         return safeNtlmString(System.getProperty("http.auth.ntlm.host"), System.getenv("COMPUTERNAME"), var0);
      }

      private static String safeNtlmString(String... var0) {
         for (String var4 : var0) {
            if (var4 != null) {
               return var4;
            }
         }

         return "";
      }
   }
}
