package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicPathHandler implements CommonCookieAttributeHandler {
   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      var1.setPath(!TextUtils.isBlank(var2) ? var2 : "/");
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
   }

   static boolean pathMatch(String var0, String var1) {
      String var2 = var1;
      if (var1 == null) {
         var2 = "/";
      }

      if (var2.length() > 1 && var2.endsWith("/")) {
         var2 = var2.substring(0, var2.length() - 1);
      }

      if (var0.startsWith(var2)) {
         if (var2.equals("/")) {
            return true;
         }

         if (var0.length() == var2.length()) {
            return true;
         }

         if (var0.charAt(var2.length()) == '/') {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      return pathMatch(var2.getPath(), var1.getPath());
   }

   @Override
   public String getAttributeName() {
      return "path";
   }
}
