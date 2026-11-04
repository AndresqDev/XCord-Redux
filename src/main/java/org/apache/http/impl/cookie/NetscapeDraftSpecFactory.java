package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NetscapeDraftSpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final CookieSpec cookieSpec;

   public NetscapeDraftSpecFactory(String[] var1) {
      this.cookieSpec = new NetscapeDraftSpec(var1);
   }

   public NetscapeDraftSpecFactory() {
      this(null);
   }

   @Override
   public CookieSpec newInstance(HttpParams var1) {
      if (var1 != null) {
         String[] var2 = null;
         Collection var3 = (Collection)var1.getParameter("http.protocol.cookie-datepatterns");
         if (var3 != null) {
            var2 = new String[var3.size()];
            var2 = var3.toArray(var2);
         }

         return new NetscapeDraftSpec(var2);
      } else {
         return new NetscapeDraftSpec();
      }
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      return this.cookieSpec;
   }
}
