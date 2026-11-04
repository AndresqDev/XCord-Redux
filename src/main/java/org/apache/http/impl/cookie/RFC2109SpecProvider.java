package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

@Obsolete
@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RFC2109SpecProvider implements CookieSpecProvider {
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public RFC2109SpecProvider(PublicSuffixMatcher var1, boolean var2) {
      this.oneHeader = var2;
      this.publicSuffixMatcher = var1;
   }

   public RFC2109SpecProvider(PublicSuffixMatcher var1) {
      this(var1, false);
   }

   public RFC2109SpecProvider() {
      this(null, false);
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new RFC2109Spec(
                  this.oneHeader,
                  new RFC2109VersionHandler(),
                  new BasicPathHandler(),
                  PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher),
                  new BasicMaxAgeHandler(),
                  new BasicSecureHandler(),
                  new BasicCommentHandler()
               );
            }
         }
      }

      return this.cookieSpec;
   }
}
