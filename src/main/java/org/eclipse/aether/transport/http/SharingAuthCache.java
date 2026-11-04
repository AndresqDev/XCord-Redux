package org.eclipse.aether.transport.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;

final class SharingAuthCache implements AuthCache {
   private final LocalState state;
   private final Map<HttpHost, AuthScheme> authSchemes;

   SharingAuthCache(LocalState var1) {
      this.state = var1;
      this.authSchemes = new HashMap<>();
   }

   private static HttpHost toKey(HttpHost var0) {
      if (var0.getPort() <= 0) {
         int var1 = var0.getSchemeName().equalsIgnoreCase("https") ? 443 : 80;
         return new HttpHost(var0.getHostName(), var1, var0.getSchemeName());
      } else {
         return var0;
      }
   }

   @Override
   public AuthScheme get(HttpHost var1) {
      var1 = toKey(var1);
      AuthScheme var2 = this.authSchemes.get(var1);
      if (var2 == null) {
         var2 = this.state.getAuthScheme(var1);
         this.authSchemes.put(var1, var2);
      }

      return var2;
   }

   @Override
   public void put(HttpHost var1, AuthScheme var2) {
      if (var2 != null) {
         this.authSchemes.put(toKey(var1), var2);
      } else {
         this.remove(var1);
      }
   }

   @Override
   public void remove(HttpHost var1) {
      this.authSchemes.remove(toKey(var1));
   }

   @Override
   public void clear() {
      this.share();
      this.authSchemes.clear();
   }

   private void share() {
      for (Entry var2 : this.authSchemes.entrySet()) {
         this.state.setAuthScheme((HttpHost)var2.getKey(), (AuthScheme)var2.getValue());
      }
   }

   @Override
   public String toString() {
      return this.authSchemes.toString();
   }
}
