package org.apache.http.impl.cookie;

import java.util.Date;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicExpiresHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {
   private final String[] datePatterns;

   public BasicExpiresHandler(String[] var1) {
      Args.notNull(var1, "Array of date patterns");
      this.datePatterns = (String[])var1.clone();
   }

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (var2 == null) {
         throw new MalformedCookieException("Missing value for 'expires' attribute");
      } else {
         Date var3 = org.apache.http.client.utils.DateUtils.parseDate(var2, this.datePatterns);
         if (var3 == null) {
            throw new MalformedCookieException("Invalid 'expires' attribute: " + var2);
         } else {
            var1.setExpiryDate(var3);
         }
      }
   }

   @Override
   public String getAttributeName() {
      return "expires";
   }
}
