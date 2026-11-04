package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RFC2965VersionAttributeHandler implements CommonCookieAttributeHandler {
   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (var2 == null) {
         throw new MalformedCookieException("Missing value for version attribute");
      } else {
         int var3 = -1;

         try {
            var3 = Integer.parseInt(var2);
         } catch (NumberFormatException var5) {
            var3 = -1;
         }

         if (var3 < 0) {
            throw new MalformedCookieException("Invalid cookie version.");
         } else {
            var1.setVersion(var3);
         }
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (var1 instanceof SetCookie2 && var1 instanceof ClientCookie && !((ClientCookie)var1).containsAttribute("version")) {
         throw new CookieRestrictionViolationException("Violates RFC 2965. Version attribute is required.");
      }
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      return true;
   }

   @Override
   public String getAttributeName() {
      return "version";
   }
}
