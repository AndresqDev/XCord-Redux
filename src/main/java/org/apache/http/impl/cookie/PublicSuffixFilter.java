package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;

@Deprecated
public class PublicSuffixFilter implements CookieAttributeHandler {
   private final CookieAttributeHandler wrapped;
   private Collection<String> exceptions;
   private Collection<String> suffixes;
   private PublicSuffixMatcher matcher;

   public PublicSuffixFilter(CookieAttributeHandler var1) {
      this.wrapped = var1;
   }

   public void setPublicSuffixes(Collection<String> var1) {
      this.suffixes = var1;
      this.matcher = null;
   }

   public void setExceptions(Collection<String> var1) {
      this.exceptions = var1;
      this.matcher = null;
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      return this.isForPublicSuffix(var1) ? false : this.wrapped.match(var1, var2);
   }

   @Override
   public void parse(SetCookie var1, String var2) throws MalformedCookieException {
      this.wrapped.parse(var1, var2);
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      this.wrapped.validate(var1, var2);
   }

   private boolean isForPublicSuffix(Cookie var1) {
      if (this.matcher == null) {
         this.matcher = new PublicSuffixMatcher(this.suffixes, this.exceptions);
      }

      return this.matcher.matches(var1.getDomain());
   }
}
