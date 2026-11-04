package org.apache.http.impl.cookie;

import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NetscapeDomainHandler extends BasicDomainHandler {
   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (TextUtils.isBlank(var2)) {
         throw new MalformedCookieException("Blank or null value for domain attribute");
      } else {
         var1.setDomain(var2);
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      String var3 = var2.getHost();
      String var4 = var1.getDomain();
      if (!var3.equals(var4) && !BasicDomainHandler.domainMatch(var4, var3)) {
         throw new CookieRestrictionViolationException("Illegal domain attribute \"" + var4 + "\". Domain of origin: \"" + var3 + "\"");
      } else {
         if (var3.contains(".")) {
            int var5 = new StringTokenizer(var4, ".").countTokens();
            if (isSpecialDomain(var4)) {
               if (var5 < 2) {
                  throw new CookieRestrictionViolationException(
                     "Domain attribute \"" + var4 + "\" violates the Netscape cookie specification for " + "special domains"
                  );
               }
            } else if (var5 < 3) {
               throw new CookieRestrictionViolationException("Domain attribute \"" + var4 + "\" violates the Netscape cookie specification");
            }
         }
      }
   }

   private static boolean isSpecialDomain(String var0) {
      String var1 = var0.toUpperCase(Locale.ROOT);
      return var1.endsWith(".COM")
         || var1.endsWith(".EDU")
         || var1.endsWith(".NET")
         || var1.endsWith(".GOV")
         || var1.endsWith(".MIL")
         || var1.endsWith(".ORG")
         || var1.endsWith(".INT");
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      String var3 = var2.getHost();
      String var4 = var1.getDomain();
      return var4 == null ? false : var3.endsWith(var4);
   }

   @Override
   public String getAttributeName() {
      return "domain";
   }
}
