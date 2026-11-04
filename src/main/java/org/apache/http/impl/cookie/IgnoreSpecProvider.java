package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class IgnoreSpecProvider implements CookieSpecProvider {
   private volatile CookieSpec cookieSpec;

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new IgnoreSpec();
            }
         }
      }

      return this.cookieSpec;
   }
}
