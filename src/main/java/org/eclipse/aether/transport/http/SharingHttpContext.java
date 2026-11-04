package org.eclipse.aether.transport.http;

import java.io.Closeable;
import org.apache.http.protocol.BasicHttpContext;

final class SharingHttpContext extends BasicHttpContext implements Closeable {
   private final LocalState state;
   private final SharingAuthCache authCache;

   SharingHttpContext(LocalState var1) {
      this.state = var1;
      this.authCache = new SharingAuthCache(var1);
      super.setAttribute("http.auth.auth-cache", this.authCache);
   }

   @Override
   public Object getAttribute(String var1) {
      return "http.user-token".equals(var1) ? this.state.getUserToken() : super.getAttribute(var1);
   }

   @Override
   public void setAttribute(String var1, Object var2) {
      if ("http.user-token".equals(var1)) {
         this.state.setUserToken(var2);
      } else {
         super.setAttribute(var1, var2);
      }
   }

   @Override
   public Object removeAttribute(String var1) {
      if ("http.user-token".equals(var1)) {
         this.state.setUserToken(null);
         return null;
      } else {
         return super.removeAttribute(var1);
      }
   }

   @Override
   public void close() {
      this.authCache.clear();
   }
}
