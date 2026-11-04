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
public class RFC2109SpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final CookieSpec cookieSpec;

   public RFC2109SpecFactory(String[] var1, boolean var2) {
      this.cookieSpec = new RFC2109Spec(var1, var2);
   }

   public RFC2109SpecFactory() {
      this(null, false);
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

         boolean var4 = var1.getBooleanParameter("http.protocol.single-cookie-header", false);
         return new RFC2109Spec(var2, var4);
      } else {
         return new RFC2109Spec();
      }
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      return this.cookieSpec;
   }
}
