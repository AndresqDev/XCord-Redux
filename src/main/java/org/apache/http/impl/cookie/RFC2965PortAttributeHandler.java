package org.apache.http.impl.cookie;

import java.util.StringTokenizer;
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
public class RFC2965PortAttributeHandler implements CommonCookieAttributeHandler {
   private static int[] parsePortAttribute(String var0) throws MalformedCookieException {
      StringTokenizer var1 = new StringTokenizer(var0, ",");
      int[] var2 = new int[var1.countTokens()];

      try {
         for (int var3 = 0; var1.hasMoreTokens(); var3++) {
            var2[var3] = Integer.parseInt(var1.nextToken().trim());
            if (var2[var3] < 0) {
               throw new MalformedCookieException("Invalid Port attribute.");
            }
         }

         return var2;
      } catch (NumberFormatException var4) {
         throw new MalformedCookieException("Invalid Port attribute: " + var4.getMessage());
      }
   }

   private static boolean portMatch(int var0, int[] var1) {
      boolean var2 = false;

      for (int var6 : var1) {
         if (var0 == var6) {
            var2 = true;
            break;
         }
      }

      return var2;
   }

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (var1 instanceof SetCookie2) {
         SetCookie2 var3 = (SetCookie2)var1;
         if (var2 != null && !var2.trim().isEmpty()) {
            int[] var4 = parsePortAttribute(var2);
            var3.setPorts(var4);
         }
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      int var3 = var2.getPort();
      if (var1 instanceof ClientCookie && ((ClientCookie)var1).containsAttribute("port") && !portMatch(var3, var1.getPorts())) {
         throw new CookieRestrictionViolationException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
      }
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      int var3 = var2.getPort();
      if (var1 instanceof ClientCookie && ((ClientCookie)var1).containsAttribute("port")) {
         if (var1.getPorts() == null) {
            return false;
         }

         if (!portMatch(var3, var1.getPorts())) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String getAttributeName() {
      return "port";
   }
}
