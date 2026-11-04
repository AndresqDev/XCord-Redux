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
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultCookieSpecProvider implements CookieSpecProvider {
   private final DefaultCookieSpecProvider.CompatibilityLevel compatibilityLevel;
   private final PublicSuffixMatcher publicSuffixMatcher;
   private final String[] datepatterns;
   private final boolean oneHeader;
   private volatile CookieSpec cookieSpec;

   public DefaultCookieSpecProvider(DefaultCookieSpecProvider.CompatibilityLevel var1, PublicSuffixMatcher var2, String[] var3, boolean var4) {
      this.compatibilityLevel = var1 != null ? var1 : DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT;
      this.publicSuffixMatcher = var2;
      this.datepatterns = var3;
      this.oneHeader = var4;
   }

   public DefaultCookieSpecProvider(DefaultCookieSpecProvider.CompatibilityLevel var1, PublicSuffixMatcher var2) {
      this(var1, var2, null, false);
   }

   public DefaultCookieSpecProvider(PublicSuffixMatcher var1) {
      this(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, var1, null, false);
   }

   public DefaultCookieSpecProvider() {
      this(DefaultCookieSpecProvider.CompatibilityLevel.DEFAULT, null, null, false);
   }

   @Override
   public CookieSpec create(HttpContext var1) {
      if (this.cookieSpec == null) {
         synchronized (this) {
            if (this.cookieSpec == null) {
               RFC2965Spec var3 = new RFC2965Spec(
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
               RFC2109Spec var4 = new RFC2109Spec(
                  this.oneHeader,
                  new RFC2109VersionHandler(),
                  new BasicPathHandler(),
                  PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher),
                  new BasicMaxAgeHandler(),
                  new BasicSecureHandler(),
                  new BasicCommentHandler()
               );
               NetscapeDraftSpec var5 = new NetscapeDraftSpec(
                  PublicSuffixDomainFilter.decorate(new BasicDomainHandler(), this.publicSuffixMatcher),
                  this.compatibilityLevel == DefaultCookieSpecProvider.CompatibilityLevel.IE_MEDIUM_SECURITY ? new BasicPathHandler() {
                     @Override
                     public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
                     }
                  } : new BasicPathHandler(),
                  new BasicSecureHandler(),
                  new BasicCommentHandler(),
                  new BasicExpiresHandler(this.datepatterns != null ? (String[])this.datepatterns.clone() : new String[]{"EEE, dd-MMM-yy HH:mm:ss z"})
               );
               this.cookieSpec = new DefaultCookieSpec(var3, var4, var5);
            }
         }
      }

      return this.cookieSpec;
   }

   public static enum CompatibilityLevel {
      DEFAULT,
      IE_MEDIUM_SECURITY;
   }
}
