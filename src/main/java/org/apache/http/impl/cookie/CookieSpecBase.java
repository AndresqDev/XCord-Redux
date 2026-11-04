package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public abstract class CookieSpecBase extends AbstractCookieSpec {
   public CookieSpecBase() {
   }

   protected CookieSpecBase(HashMap<String, CookieAttributeHandler> var1) {
      super(var1);
   }

   protected CookieSpecBase(CommonCookieAttributeHandler... var1) {
      super(var1);
   }

   protected static String getDefaultPath(CookieOrigin var0) {
      String var1 = var0.getPath();
      int var2 = var1.lastIndexOf(47);
      if (var2 >= 0) {
         if (var2 == 0) {
            var2 = 1;
         }

         var1 = var1.substring(0, var2);
      }

      return var1;
   }

   protected static String getDefaultDomain(CookieOrigin var0) {
      return var0.getHost();
   }

   protected List<Cookie> parse(HeaderElement[] var1, CookieOrigin var2) throws MalformedCookieException {
      ArrayList var3 = new ArrayList(var1.length);

      for (HeaderElement var7 : var1) {
         String var8 = var7.getName();
         String var9 = var7.getValue();
         if (var8 != null && !var8.isEmpty()) {
            BasicClientCookie var10 = new BasicClientCookie(var8, var9);
            var10.setPath(getDefaultPath(var2));
            var10.setDomain(getDefaultDomain(var2));
            NameValuePair[] var11 = var7.getParameters();

            for (int var12 = var11.length - 1; var12 >= 0; var12--) {
               NameValuePair var13 = var11[var12];
               String var14 = var13.getName().toLowerCase(Locale.ROOT);
               var10.setAttribute(var14, var13.getValue());
               CookieAttributeHandler var15 = this.findAttribHandler(var14);
               if (var15 != null) {
                  var15.parse(var10, var13.getValue());
               }
            }

            var3.add(var10);
         }
      }

      return var3;
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");

      for (CookieAttributeHandler var4 : this.getAttribHandlers()) {
         var4.validate(var1, var2);
      }
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");

      for (CookieAttributeHandler var4 : this.getAttribHandlers()) {
         if (!var4.match(var1, var2)) {
            return false;
         }
      }

      return true;
   }
}
