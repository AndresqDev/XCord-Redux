package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Obsolete
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class RFC2109Spec extends CookieSpecBase {
   static final String[] DATE_PATTERNS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy"};
   private final boolean oneHeader;

   public RFC2109Spec(String[] var1, boolean var2) {
      super(
         new RFC2109VersionHandler(),
         new BasicPathHandler() {
            @Override
            public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
               if (!this.match(var1, var2)) {
                  throw new CookieRestrictionViolationException(
                     "Illegal 'path' attribute \"" + var1.getPath() + "\". Path of origin: \"" + var2.getPath() + "\""
                  );
               }
            }
         },
         new RFC2109DomainHandler(),
         new BasicMaxAgeHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new BasicExpiresHandler(var1 != null ? (String[])var1.clone() : DATE_PATTERNS)
      );
      this.oneHeader = var2;
   }

   public RFC2109Spec() {
      this(null, false);
   }

   protected RFC2109Spec(boolean var1, CommonCookieAttributeHandler... var2) {
      super(var2);
      this.oneHeader = var1;
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      if (!var1.getName().equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + var1.toString() + "'");
      } else {
         HeaderElement[] var3 = var1.getElements();
         return this.parse(var3, var2);
      }
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      String var3 = var1.getName();
      if (var3.indexOf(32) != -1) {
         throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
      } else if (var3.startsWith("$")) {
         throw new CookieRestrictionViolationException("Cookie name may not start with $");
      } else {
         super.validate(var1, var2);
      }
   }

   @Override
   public List<Header> formatCookies(List<Cookie> var1) {
      Args.notEmpty(var1, "List of cookies");
      Object var2;
      if (var1.size() > 1) {
         var2 = new ArrayList(var1);
         Collections.sort((List<Cookie>)var2, CookiePathComparator.INSTANCE);
      } else {
         var2 = var1;
      }

      return this.oneHeader ? this.doFormatOneHeader((List<Cookie>)var2) : this.doFormatManyHeaders((List<Cookie>)var2);
   }

   private List<Header> doFormatOneHeader(List<Cookie> var1) {
      int var2 = Integer.MAX_VALUE;

      for (Cookie var4 : var1) {
         if (var4.getVersion() < var2) {
            var2 = var4.getVersion();
         }
      }

      CharArrayBuffer var7 = new CharArrayBuffer(40 * var1.size());
      var7.append("Cookie");
      var7.append(": ");
      var7.append("$Version=");
      var7.append(Integer.toString(var2));

      for (Cookie var5 : var1) {
         var7.append("; ");
         this.formatCookieAsVer(var7, var5, var2);
      }

      ArrayList var9 = new ArrayList(1);
      var9.add(new BufferedHeader(var7));
      return var9;
   }

   private List<Header> doFormatManyHeaders(List<Cookie> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (Cookie var4 : var1) {
         int var5 = var4.getVersion();
         CharArrayBuffer var6 = new CharArrayBuffer(40);
         var6.append("Cookie: ");
         var6.append("$Version=");
         var6.append(Integer.toString(var5));
         var6.append("; ");
         this.formatCookieAsVer(var6, var4, var5);
         var2.add(new BufferedHeader(var6));
      }

      return var2;
   }

   protected void formatParamAsVer(CharArrayBuffer var1, String var2, String var3, int var4) {
      var1.append(var2);
      var1.append("=");
      if (var3 != null) {
         if (var4 > 0) {
            var1.append('"');
            var1.append(var3);
            var1.append('"');
         } else {
            var1.append(var3);
         }
      }
   }

   protected void formatCookieAsVer(CharArrayBuffer var1, Cookie var2, int var3) {
      this.formatParamAsVer(var1, var2.getName(), var2.getValue(), var3);
      if (var2.getPath() != null && var2 instanceof ClientCookie && ((ClientCookie)var2).containsAttribute("path")) {
         var1.append("; ");
         this.formatParamAsVer(var1, "$Path", var2.getPath(), var3);
      }

      if (var2.getDomain() != null && var2 instanceof ClientCookie && ((ClientCookie)var2).containsAttribute("domain")) {
         var1.append("; ");
         this.formatParamAsVer(var1, "$Domain", var2.getDomain(), var3);
      }
   }

   @Override
   public int getVersion() {
      return 1;
   }

   @Override
   public Header getVersionHeader() {
      return null;
   }

   @Override
   public String toString() {
      return "rfc2109";
   }
}
