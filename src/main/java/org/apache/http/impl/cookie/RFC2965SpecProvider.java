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
public class RFC2965SpecProvider implements CookieSpecProvider {
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public RFC2965SpecProvider(PublicSuffixMatcher var1, boolean var2) {
      this.oneHeader = var2;
      this.publicSuffixMatcher = var1;
   }

   public RFC2965SpecProvider(PublicSuffixMatcher var1) {
      this(var1, false);
   }

   public RFC2965SpecProvider() {
      this(null, false);
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               this.cookieSpec = new RFC2965Spec(
                  this.oneHeader,
                  new RFC2965VersionAttributeHandler(),
                  new BasicPathHandler(),
                  PublicSuffixDomainFilter.decorate(new RFC2965DomainAttributeHandler(), this.publicSuffixMatcher),
                  new RFC2965PortAttributeHandler(),
                  new BasicMaxAgeHandler(),
                  new BasicSecureHandler(),
                  new BasicCommentHandler(),
                  new RFC2965CommentUrlAttributeHandler(),
                  new RFC2965DiscardAttributeHandler()
               );
            }
         }
      }

      return this.cookieSpec;
   }
}
