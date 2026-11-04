package org.apache.http.impl.cookie;

import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
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
public class BasicDomainHandler implements CommonCookieAttributeHandler {
   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (TextUtils.isBlank(var2)) {
         throw new MalformedCookieException("Blank or null value for domain attribute");
      } else if (!var2.endsWith(".")) {
         String var3 = var2;
         if (var2.startsWith(".")) {
            var3 = var2.substring(1);
         }

         var3 = var3.toLowerCase(Locale.ROOT);
         var1.setDomain(var3);
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      String var3 = var2.getHost();
      String var4 = var1.getDomain();
      if (var4 == null) {
         throw new CookieRestrictionViolationException("Cookie 'domain' may not be null");
      } else if (!var3.equals(var4) && !domainMatch(var4, var3)) {
         throw new CookieRestrictionViolationException("Illegal 'domain' attribute \"" + var4 + "\". Domain of origin: \"" + var3 + "\"");
      }
   }

   static boolean domainMatch(String var0, String var1) {
      if (!InetAddressUtils.isIPv4Address(var1) && !InetAddressUtils.isIPv6Address(var1)) {
         String var2 = var0.startsWith(".") ? var0.substring(1) : var0;
         if (var1.endsWith(var2)) {
            int var3 = var1.length() - var2.length();
            if (var3 == 0) {
               return true;
            }

            if (var3 > 1 && var1.charAt(var3 - 1) == '.') {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      String var3 = var2.getHost();
      String var4 = var1.getDomain();
      if (var4 == null) {
         return false;
      } else {
         if (var4.startsWith(".")) {
            var4 = var4.substring(1);
         }

         var4 = var4.toLowerCase(Locale.ROOT);
         if (var3.equals(var4)) {
            return true;
         } else {
            return var1 instanceof ClientCookie && ((ClientCookie)var1).containsAttribute("domain") ? domainMatch(var4, var3) : false;
         }
      }
   }

   @Override
   public String getAttributeName() {
      return "domain";
   }
}
