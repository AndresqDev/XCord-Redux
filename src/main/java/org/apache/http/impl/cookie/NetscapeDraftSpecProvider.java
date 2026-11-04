package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

@Obsolete
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NetscapeDraftSpecProvider implements CookieSpecProvider {
   private final String[] datepatterns;
   private volatile CookieSpec cookieSpec;

   public NetscapeDraftSpecProvider(String[] var1) {
      this.datepatterns = var1;
   }

   public NetscapeDraftSpecProvider() {
      this(null);
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new NetscapeDraftSpec(this.datepatterns);
            }
         }
      }

      return this.cookieSpec;
   }
}
