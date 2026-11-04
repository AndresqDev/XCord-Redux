package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RFC2965CommentUrlAttributeHandler implements CommonCookieAttributeHandler {
   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      if (var1 instanceof SetCookie2) {
         SetCookie2 var3 = (SetCookie2)var1;
         var3.setCommentURL(var2);
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      return true;
   }

   @Override
   public String getAttributeName() {
      return "commenturl";
   }
}
