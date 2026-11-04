package org.eclipse.aether.transport.http;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.conn.HttpClientConnectionManager;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

final class LocalState implements Closeable {
   private final GlobalState global;
   private final HttpClientConnectionManager connMgr;
   private final GlobalState.CompoundKey userTokenKey;
   private volatile Object userToken;
   private final GlobalState.CompoundKey expectContinueKey;
   private volatile Boolean expectContinue;
   private volatile Boolean webDav;
   private final ConcurrentMap<HttpHost, AuthSchemePool> authSchemePools;

   LocalState(RepositorySystemSession var1, RemoteRepository var2, SslConfig var3) {
      this.global = GlobalState.get(var1);
      this.userToken = this;
      if (this.global == null) {
         this.connMgr = GlobalState.newConnectionManager(var3);
         this.userTokenKey = null;
         this.expectContinueKey = null;
         this.authSchemePools = new ConcurrentHashMap<>();
      } else {
         this.connMgr = this.global.getConnectionManager(var3);
         this.userTokenKey = new GlobalState.CompoundKey(var2.getId(), var2.getUrl(), var2.getAuthentication(), var2.getProxy());
         this.expectContinueKey = new GlobalState.CompoundKey(var2.getUrl(), var2.getProxy());
         this.authSchemePools = this.global.getAuthSchemePools();
      }
   }

   public HttpClientConnectionManager getConnectionManager() {
      return this.connMgr;
   }

   public Object getUserToken() {
      if (this.userToken == this) {
         this.userToken = this.global != null ? this.global.getUserToken(this.userTokenKey) : null;
      }

      return this.userToken;
   }

   public void setUserToken(Object var1) {
      this.userToken = var1;
      if (this.global != null) {
         this.global.setUserToken(this.userTokenKey, var1);
      }
   }

   public boolean isExpectContinue() {
      if (this.expectContinue == null) {
         this.expectContinue = !Boolean.FALSE.equals(this.global != null ? this.global.getExpectContinue(this.expectContinueKey) : null);
      }

      return this.expectContinue;
   }

   public void setExpectContinue(boolean var1) {
      this.expectContinue = var1;
      if (this.global != null) {
         this.global.setExpectContinue(this.expectContinueKey, var1);
      }
   }

   public Boolean getWebDav() {
      return this.webDav;
   }

   public void setWebDav(boolean var1) {
      this.webDav = var1;
   }

   public AuthScheme getAuthScheme(HttpHost var1) {
      AuthSchemePool var2 = this.authSchemePools.get(var1);
      return var2 != null ? var2.get() : null;
   }

   public void setAuthScheme(HttpHost var1, AuthScheme var2) {
      AuthSchemePool var3 = this.authSchemePools.get(var1);
      if (var3 == null) {
         AuthSchemePool var4 = new AuthSchemePool();
         var3 = this.authSchemePools.putIfAbsent(var1, var4);
         if (var3 == null) {
            var3 = var4;
         }
      }

      var3.put(var2);
   }

   @Override
   public void close() {
      if (this.global == null) {
         this.connMgr.shutdown();
      }
   }
}
