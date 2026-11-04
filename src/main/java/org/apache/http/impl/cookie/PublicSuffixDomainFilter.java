package org.apache.http.impl.cookie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixList;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class PublicSuffixDomainFilter implements CommonCookieAttributeHandler {
   private final CommonCookieAttributeHandler handler;
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final Map<String, Boolean> localDomainMap;

   private static Map<String, Boolean> createLocalDomainMap() {
      ConcurrentHashMap var0 = new ConcurrentHashMap();
      var0.put(".localhost.", Boolean.TRUE);
      var0.put(".test.", Boolean.TRUE);
      var0.put(".local.", Boolean.TRUE);
      var0.put(".local", Boolean.TRUE);
      var0.put(".localdomain", Boolean.TRUE);
      return var0;
   }

   public PublicSuffixDomainFilter(CommonCookieAttributeHandler var1, PublicSuffixMatcher var2) {
      this.handler = Args.notNull(var1, "Cookie handler");
      this.publicSuffixMatcher = Args.notNull(var2, "Public suffix matcher");
      this.localDomainMap = createLocalDomainMap();
   }

   public PublicSuffixDomainFilter(CommonCookieAttributeHandler var1, PublicSuffixList var2) {
      Args.notNull(var1, "Cookie handler");
      Args.notNull(var2, "Public suffix list");
      this.handler = var1;
      this.publicSuffixMatcher = new PublicSuffixMatcher(var2.getRules(), var2.getExceptions());
      this.localDomainMap = createLocalDomainMap();
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      String var3 = var1.getDomain();
      if (var3 == null) {
         return false;
      } else {
         int var4 = var3.indexOf(46);
         if (var4 >= 0) {
            String var5 = var3.substring(var4);
            if (!this.localDomainMap.containsKey(var5) && this.publicSuffixMatcher.matches(var3)) {
               return false;
            }
         } else if (!var3.equalsIgnoreCase(var2.getHost()) && this.publicSuffixMatcher.matches(var3)) {
            return false;
         }

         return this.handler.match(var1, var2);
      }
   }

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      this.handler.parse(var1, var2);
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      this.handler.validate(var1, var2);
   }

   @Override
   public String getAttributeName() {
      return this.handler.getAttributeName();
   }

   public static CommonCookieAttributeHandler decorate(CommonCookieAttributeHandler var0, PublicSuffixMatcher var1) {
      Args.notNull(var0, "Cookie attribute handler");
      return (CommonCookieAttributeHandler)(var1 != null ? new PublicSuffixDomainFilter(var0, var1) : var0);
   }
}
