package org.apache.http.impl.cookie;

import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class DefaultCookieSpec implements CookieSpec {
   private final RFC2965Spec strict;
   private final RFC2109Spec obsoleteStrict;
   private final NetscapeDraftSpec netscapeDraft;

   DefaultCookieSpec(RFC2965Spec var1, RFC2109Spec var2, NetscapeDraftSpec var3) {
      this.strict = var1;
      this.obsoleteStrict = var2;
      this.netscapeDraft = var3;
   }

   public DefaultCookieSpec(String[] var1, boolean var2) {
      this.strict = new RFC2965Spec(
         var2,
         new RFC2965VersionAttributeHandler(),
         new BasicPathHandler(),
         new RFC2965DomainAttributeHandler(),
         new RFC2965PortAttributeHandler(),
         new BasicMaxAgeHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new RFC2965CommentUrlAttributeHandler(),
         new RFC2965DiscardAttributeHandler()
      );
      this.obsoleteStrict = new RFC2109Spec(
         var2,
         new RFC2109VersionHandler(),
         new BasicPathHandler(),
         new RFC2109DomainHandler(),
         new BasicMaxAgeHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler()
      );
      this.netscapeDraft = new NetscapeDraftSpec(
         new BasicDomainHandler(),
         new BasicPathHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new BasicExpiresHandler(var1 != null ? (String[])var1.clone() : new String[]{"EEE, dd-MMM-yy HH:mm:ss z"})
      );
   }

   public DefaultCookieSpec() {
      this(null, false);
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      HeaderElement[] var3 = var1.getElements();
      boolean var4 = false;
      boolean var5 = false;

      for (HeaderElement var9 : var3) {
         if (var9.getParameterByName("version") != null) {
            var4 = true;
         }

         if (var9.getParameterByName("expires") != null) {
            var5 = true;
         }
      }

      if (!var5 && var4) {
         return "Set-Cookie2".equals(var1.getName()) ? this.strict.parse(var3, var2) : this.obsoleteStrict.parse(var3, var2);
      } else {
         NetscapeDraftHeaderParser var11 = NetscapeDraftHeaderParser.DEFAULT;
         CharArrayBuffer var12;
         ParserCursor var13;
         if (var1 instanceof FormattedHeader) {
            var12 = ((FormattedHeader)var1).getBuffer();
            var13 = new ParserCursor(((FormattedHeader)var1).getValuePos(), var12.length());
         } else {
            String var14 = var1.getValue();
            if (var14 == null) {
               throw new MalformedCookieException("Header value is null");
            }

            var12 = new CharArrayBuffer(var14.length());
            var12.append(var14);
            var13 = new ParserCursor(0, var12.length());
         }

         var3 = new HeaderElement[]{var11.parseHeader(var12, var13)};
         return this.netscapeDraft.parse(var3, var2);
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      if (var1.getVersion() > 0) {
         if (var1 instanceof SetCookie2) {
            this.strict.validate(var1, var2);
         } else {
            this.obsoleteStrict.validate(var1, var2);
         }
      } else {
         this.netscapeDraft.validate(var1, var2);
      }
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      if (var1.getVersion() > 0) {
         return var1 instanceof SetCookie2 ? this.strict.match(var1, var2) : this.obsoleteStrict.match(var1, var2);
      } else {
         return this.netscapeDraft.match(var1, var2);
      }
   }

   @Override
   public List<Header> formatCookies(List<Cookie> var1) {
      Args.notNull(var1, "List of cookies");
      int var2 = Integer.MAX_VALUE;
      boolean var3 = true;

      for (Cookie var5 : var1) {
         if (!(var5 instanceof SetCookie2)) {
            var3 = false;
         }

         if (var5.getVersion() < var2) {
            var2 = var5.getVersion();
         }
      }

      if (var2 > 0) {
         return var3 ? this.strict.formatCookies(var1) : this.obsoleteStrict.formatCookies(var1);
      } else {
         return this.netscapeDraft.formatCookies(var1);
      }
   }

   @Override
   public int getVersion() {
      return this.strict.getVersion();
   }

   @Override
   public Header getVersionHeader() {
      return null;
   }

   @Override
   public String toString() {
      return "default";
   }
}
