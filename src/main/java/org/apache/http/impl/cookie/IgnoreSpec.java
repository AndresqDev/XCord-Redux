package org.apache.http.impl.cookie;

import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class IgnoreSpec extends CookieSpecBase {
   @Override
   public int getVersion() {
      return 0;
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      return Collections.emptyList();
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      return false;
   }

   @Override
   public List<Header> formatCookies(List<Cookie> var1) {
      return Collections.emptyList();
   }

   @Override
   public Header getVersionHeader() {
      return null;
   }
}
