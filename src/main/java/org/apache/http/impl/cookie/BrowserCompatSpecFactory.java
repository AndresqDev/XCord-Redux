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
public class BrowserCompatSpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final BrowserCompatSpecFactory.SecurityLevel securityLevel;
   private final CookieSpec cookieSpec;

   public BrowserCompatSpecFactory(String[] var1, BrowserCompatSpecFactory.SecurityLevel var2) {
      this.securityLevel = var2;
      this.cookieSpec = new BrowserCompatSpec(var1, var2);
   }

   public BrowserCompatSpecFactory(String[] var1) {
      this(null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public BrowserCompatSpecFactory() {
      this(null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
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

         return new BrowserCompatSpec(var2, this.securityLevel);
      } else {
         return new BrowserCompatSpec(null, this.securityLevel);
      }
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      return this.cookieSpec;
   }

   public static enum SecurityLevel {
      SECURITYLEVEL_DEFAULT,
      SECURITYLEVEL_IE_MEDIUM;
   }
}
