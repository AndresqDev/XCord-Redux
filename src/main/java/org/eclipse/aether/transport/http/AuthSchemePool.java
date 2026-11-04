package org.eclipse.aether.transport.http;

import java.util.LinkedList;
import org.apache.http.auth.AuthScheme;
import org.apache.http.impl.auth.BasicScheme;

final class AuthSchemePool {
   private final LinkedList<AuthScheme> authSchemes = new LinkedList<>();
   private String schemeName;

   public synchronized AuthScheme get() {
      Object var1 = null;
      if (!this.authSchemes.isEmpty()) {
         var1 = this.authSchemes.removeLast();
      } else if ("Basic".equalsIgnoreCase(this.schemeName)) {
         var1 = new BasicScheme();
      }

      return (AuthScheme)var1;
   }

   public synchronized void put(AuthScheme var1) {
      if (var1 != null) {
         if (!var1.getSchemeName().equals(this.schemeName)) {
            this.schemeName = var1.getSchemeName();
            this.authSchemes.clear();
         }

         this.authSchemes.add(var1);
      }
   }
}
