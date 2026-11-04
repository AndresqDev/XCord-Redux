package org.eclipse.aether.transport.http;

import java.io.Closeable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpHost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.util.ConfigUtils;

final class GlobalState implements Closeable {
   private static final String KEY = GlobalState.class.getName();
   private static final String CONFIG_PROP_CACHE_STATE = "aether.connector.http.cacheState";
   private final ConcurrentMap<SslConfig, HttpClientConnectionManager> connectionManagers = new ConcurrentHashMap<>();
   private final ConcurrentMap<GlobalState.CompoundKey, Object> userTokens = new ConcurrentHashMap<>();
   private final ConcurrentMap<HttpHost, AuthSchemePool> authSchemePools = new ConcurrentHashMap<>();
   private final ConcurrentMap<GlobalState.CompoundKey, Boolean> expectContinues = new ConcurrentHashMap<>();

   public static GlobalState get(RepositorySystemSession var0) {
      RepositoryCache var2 = var0.getCache();
      GlobalState var1;
      if (var2 != null && ConfigUtils.getBoolean(var0, true, "aether.connector.http.cacheState")) {
         Object var3 = var2.get(var0, KEY);
         if (var3 instanceof GlobalState) {
            var1 = (GlobalState)var3;
         } else {
            synchronized (GlobalState.class) {
               var3 = var2.get(var0, KEY);
               if (var3 instanceof GlobalState) {
                  var1 = (GlobalState)var3;
               } else {
                  var1 = new GlobalState();
                  var2.put(var0, KEY, var1);
               }
            }
         }
      } else {
         var1 = null;
      }

      return var1;
   }

   private GlobalState() {
   }

   @Override
   public void close() {
      Iterator var1 = this.connectionManagers.entrySet().iterator();

      while (var1.hasNext()) {
         HttpClientConnectionManager var2 = (HttpClientConnectionManager)((Entry)var1.next()).getValue();
         var1.remove();
         var2.shutdown();
      }
   }

   public HttpClientConnectionManager getConnectionManager(SslConfig var1) {
      HttpClientConnectionManager var2 = this.connectionManagers.get(var1);
      if (var2 == null) {
         HttpClientConnectionManager var3 = newConnectionManager(var1);
         var2 = this.connectionManagers.putIfAbsent(var1, var3);
         if (var2 != null) {
            var3.shutdown();
         } else {
            var2 = var3;
         }
      }

      return var2;
   }

   public static HttpClientConnectionManager newConnectionManager(SslConfig var0) {
      RegistryBuilder var1 = RegistryBuilder.<PlainConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory());
      if (var0 == null) {
         var1.register("https", SSLConnectionSocketFactory.getSystemSocketFactory());
      } else {
         SSLSocketFactory var2 = var0.context != null ? var0.context.getSocketFactory() : (SSLSocketFactory)SSLSocketFactory.getDefault();
         HostnameVerifier var3 = var0.verifier != null ? var0.verifier : SSLConnectionSocketFactory.getDefaultHostnameVerifier();
         var1.register("https", new SSLConnectionSocketFactory(var2, var0.protocols, var0.cipherSuites, var3));
      }

      PoolingHttpClientConnectionManager var4 = new PoolingHttpClientConnectionManager(var1.build());
      var4.setMaxTotal(100);
      var4.setDefaultMaxPerRoute(50);
      return var4;
   }

   public Object getUserToken(GlobalState.CompoundKey var1) {
      return this.userTokens.get(var1);
   }

   public void setUserToken(GlobalState.CompoundKey var1, Object var2) {
      if (var2 != null) {
         this.userTokens.put(var1, var2);
      } else {
         this.userTokens.remove(var1);
      }
   }

   public ConcurrentMap<HttpHost, AuthSchemePool> getAuthSchemePools() {
      return this.authSchemePools;
   }

   public Boolean getExpectContinue(GlobalState.CompoundKey var1) {
      return this.expectContinues.get(var1);
   }

   public void setExpectContinue(GlobalState.CompoundKey var1, boolean var2) {
      this.expectContinues.put(var1, Boolean.valueOf(var2));
   }

   static class CompoundKey {
      private final Object[] keys;

      CompoundKey(Object... var1) {
         this.keys = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass().equals(var1.getClass())) {
            GlobalState.CompoundKey var2 = (GlobalState.CompoundKey)var1;
            return Arrays.equals(this.keys, var2.keys);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         byte var1 = 17;
         return var1 * 31 + Arrays.hashCode(this.keys);
      }

      @Override
      public String toString() {
         return Arrays.toString(this.keys);
      }
   }
}
