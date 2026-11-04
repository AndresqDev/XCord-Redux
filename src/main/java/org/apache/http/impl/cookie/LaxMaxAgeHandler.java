package org.apache.http.impl.cookie;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class LaxMaxAgeHandler extends AbstractCookieAttributeHandler implements CommonCookieAttributeHandler {
   private static final Pattern MAX_AGE_PATTERN = Pattern.compile("^\\-?[0-9]+$");

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      if (!TextUtils.isBlank(var2)) {
         Matcher var3 = MAX_AGE_PATTERN.matcher(var2);
         if (var3.matches()) {
            int var4;
            try {
               var4 = Integer.parseInt(var2);
            } catch (NumberFormatException var6) {
               return;
            }

            Date var5 = var4 >= 0 ? new Date(System.currentTimeMillis() + (long)var4 * 1000L) : new Date(Long.MIN_VALUE);
            var1.setExpiryDate(var5);
         }
      }
   }

   @Override
   public String getAttributeName() {
      return "max-age";
   }
}
