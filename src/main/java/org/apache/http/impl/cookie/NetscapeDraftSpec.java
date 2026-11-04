package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Obsolete
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class NetscapeDraftSpec extends CookieSpecBase {
   protected static final String EXPIRES_PATTERN = "EEE, dd-MMM-yy HH:mm:ss z";

   public NetscapeDraftSpec(String[] var1) {
      super(
         new BasicPathHandler(),
         new NetscapeDomainHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new BasicExpiresHandler(var1 != null ? (String[])var1.clone() : new String[]{"EEE, dd-MMM-yy HH:mm:ss z"})
      );
   }

   NetscapeDraftSpec(CommonCookieAttributeHandler... var1) {
      super(var1);
   }

   public NetscapeDraftSpec() {
      this((String[])null);
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      if (!var1.getName().equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + var1.toString() + "'");
      } else {
         NetscapeDraftHeaderParser var3 = NetscapeDraftHeaderParser.DEFAULT;
         CharArrayBuffer var4;
         ParserCursor var5;
         if (var1 instanceof FormattedHeader) {
            var4 = ((FormattedHeader)var1).getBuffer();
            var5 = new ParserCursor(((FormattedHeader)var1).getValuePos(), var4.length());
         } else {
            String var6 = var1.getValue();
            if (var6 == null) {
               throw new MalformedCookieException("Header value is null");
            }

            var4 = new CharArrayBuffer(var6.length());
            var4.append(var6);
            var5 = new ParserCursor(0, var4.length());
         }

         return this.parse(new HeaderElement[]{var3.parseHeader(var4, var5)}, var2);
      }
   }

   @Override
   public List<Header> formatCookies(List<Cookie> var1) {
      Args.notEmpty(var1, "List of cookies");
      CharArrayBuffer var2 = new CharArrayBuffer(20 * var1.size());
      var2.append("Cookie");
      var2.append(": ");

      for (int var3 = 0; var3 < var1.size(); var3++) {
         Cookie var4 = (Cookie)var1.get(var3);
         if (var3 > 0) {
            var2.append("; ");
         }

         var2.append(var4.getName());
         String var5 = var4.getValue();
         if (var5 != null) {
            var2.append("=");
            var2.append(var5);
         }
      }

      ArrayList var6 = new ArrayList(1);
      var6.add(new BufferedHeader(var2));
      return var6;
   }

   @Override
   public int getVersion() {
      return 0;
   }

   @Override
   public Header getVersionHeader() {
      return null;
   }

   @Override
   public String toString() {
      return "netscape";
   }
}
