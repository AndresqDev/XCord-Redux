package org.apache.http.impl.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicCredentialsProvider implements CredentialsProvider {
   private final ConcurrentHashMap<AuthScope, Credentials> credMap = new ConcurrentHashMap<>();

   @Override
   public void setCredentials(AuthScope var1, Credentials var2) {
      Args.notNull(var1, "Authentication scope");
      this.credMap.put(var1, var2);
   }

   private static Credentials matchCredentials(Map<AuthScope, Credentials> var0, AuthScope var1) {
      Credentials var2 = (Credentials)var0.get(var1);
      if (var2 == null) {
         int var3 = -1;
         AuthScope var4 = null;

         for (AuthScope var6 : var0.keySet()) {
            int var7 = var1.match(var6);
            if (var7 > var3) {
               var3 = var7;
               var4 = var6;
            }
         }

         if (var4 != null) {
            var2 = (Credentials)var0.get(var4);
         }
      }

      return var2;
   }

   @Override
   public Credentials getCredentials(AuthScope var1) {
      Args.notNull(var1, "Authentication scope");
      return matchCredentials(this.credMap, var1);
   }

   @Override
   public void clear() {
      this.credMap.clear();
   }

   @Override
   public String toString() {
      return this.credMap.toString();
   }
}
