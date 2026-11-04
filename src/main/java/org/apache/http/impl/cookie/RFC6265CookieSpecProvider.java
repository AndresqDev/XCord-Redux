package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RFC6265CookieSpecProvider implements CookieSpecProvider {
   private final RFC6265CookieSpecProvider.CompatibilityLevel compatibilityLevel;
   private final PublicSuffixMatcher publicSuffixMatcher;
   private volatile CookieSpec cookieSpec;

   public RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel var1, PublicSuffixMatcher var2) {
      this.compatibilityLevel = var1 != null ? var1 : RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED;
      this.publicSuffixMatcher = var2;
   }

   public RFC6265CookieSpecProvider(PublicSuffixMatcher var1) {
      this(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, var1);
   }

   public RFC6265CookieSpecProvider() {
      this(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, null);
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               switch (this.compatibilityLevel) {
                  case STRICT:
                     this.cookieSpec = new RFC6265StrictSpec(
                        new BasicPathHandler(),
                        PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher),
                        new BasicMaxAgeHandler(),
                        new BasicSecureHandler(),
                        new BasicExpiresHandler(RFC6265StrictSpec.DATE_PATTERNS)
                     );
                     break;
                  case IE_MEDIUM_SECURITY:
                     this.cookieSpec = new RFC6265LaxSpec(
                        new BasicPathHandler() {
                           @Override
                           public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
                           }
                        },
                        PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher),
                        new BasicMaxAgeHandler(),
                        new BasicSecureHandler(),
                        new BasicExpiresHandler(RFC6265StrictSpec.DATE_PATTERNS)
                     );
                     break;
                  default:
                     this.cookieSpec = new RFC6265LaxSpec(
                        new BasicPathHandler(),
                        PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher),
                        new LaxMaxAgeHandler(),
                        new BasicSecureHandler(),
                        new LaxExpiresHandler()
                     );
               }
            }
         }
      }

      return this.cookieSpec;
   }

   public static enum CompatibilityLevel {
      STRICT,
      RELAXED,
      IE_MEDIUM_SECURITY;
   }
}
