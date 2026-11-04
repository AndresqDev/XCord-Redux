package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BrowserCompatSpec extends CookieSpecBase {
   private static final String[] DEFAULT_DATE_PATTERNS = new String[]{
      "EEE, dd MMM yyyy HH:mm:ss zzz",
      "EEE, dd-MMM-yy HH:mm:ss zzz",
      "EEE MMM d HH:mm:ss yyyy",
      "EEE, dd-MMM-yyyy HH:mm:ss z",
      "EEE, dd-MMM-yyyy HH-mm-ss z",
      "EEE, dd MMM yy HH:mm:ss z",
      "EEE dd-MMM-yyyy HH:mm:ss z",
      "EEE dd MMM yyyy HH:mm:ss z",
      "EEE dd-MMM-yyyy HH-mm-ss z",
      "EEE dd-MMM-yy HH:mm:ss z",
      "EEE dd MMM yy HH:mm:ss z",
      "EEE,dd-MMM-yy HH:mm:ss z",
      "EEE,dd-MMM-yyyy HH:mm:ss z",
      "EEE, dd-MM-yyyy HH:mm:ss z"
   };

   public BrowserCompatSpec(String[] var1, BrowserCompatSpecFactory.SecurityLevel var2) {
      super(
         new BrowserCompatVersionAttributeHandler(),
         new BasicDomainHandler(),
         var2 == BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_IE_MEDIUM ? new BasicPathHandler() {
            @Override
            public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
            }
         } : new BasicPathHandler(),
         new BasicMaxAgeHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new BasicExpiresHandler(var1 != null ? (String[])var1.clone() : DEFAULT_DATE_PATTERNS)
      );
   }

   public BrowserCompatSpec(String[] var1) {
      this(var1, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public BrowserCompatSpec() {
      this(null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      String var3 = var1.getName();
      if (!var3.equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + var1.toString() + "'");
      } else {
         HeaderElement[] var4 = var1.getElements();
         boolean var5 = false;
         boolean var6 = false;

         for (HeaderElement var10 : var4) {
            if (var10.getParameterByName("version") != null) {
               var5 = true;
            }

            if (var10.getParameterByName("expires") != null) {
               var6 = true;
            }
         }

         if (!var6 && var5) {
            return this.parse(var4, var2);
         } else {
            NetscapeDraftHeaderParser var19 = NetscapeDraftHeaderParser.DEFAULT;
            CharArrayBuffer var20;
            ParserCursor var21;
            if (var1 instanceof FormattedHeader) {
               var20 = ((FormattedHeader)var1).getBuffer();
               var21 = new ParserCursor(((FormattedHeader)var1).getValuePos(), var20.length());
            } else {
               String var22 = var1.getValue();
               if (var22 == null) {
                  throw new MalformedCookieException("Header value is null");
               }

               var20 = new CharArrayBuffer(var22.length());
               var20.append(var22);
               var21 = new ParserCursor(0, var20.length());
            }

            HeaderElement var23 = var19.parseHeader(var20, var21);
            String var11 = var23.getName();
            String var12 = var23.getValue();
            if (var11 != null && !var11.isEmpty()) {
               BasicClientCookie var13 = new BasicClientCookie(var11, var12);
               var13.setPath(getDefaultPath(var2));
               var13.setDomain(getDefaultDomain(var2));
               NameValuePair[] var14 = var23.getParameters();

               for (int var15 = var14.length - 1; var15 >= 0; var15--) {
                  NameValuePair var16 = var14[var15];
                  String var17 = var16.getName().toLowerCase(Locale.ROOT);
                  var13.setAttribute(var17, var16.getValue());
                  CookieAttributeHandler var18 = this.findAttribHandler(var17);
                  if (var18 != null) {
                     var18.parse(var13, var16.getValue());
                  }
               }

               if (var6) {
                  var13.setVersion(0);
               }

               return Collections.singletonList(var13);
            } else {
               throw new MalformedCookieException("Cookie name may not be empty");
            }
         }
      }
   }

   private static boolean isQuoteEnclosed(String var0) {
      return var0 != null && var0.startsWith("\"") && var0.endsWith("\"");
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

         String var5 = var4.getName();
         String var6 = var4.getValue();
         if (var4.getVersion() > 0 && !isQuoteEnclosed(var6)) {
            BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(var2, new BasicHeaderElement(var5, var6), false);
         } else {
            var2.append(var5);
            var2.append("=");
            if (var6 != null) {
               var2.append(var6);
            }
         }
      }

      ArrayList var7 = new ArrayList(1);
      var7.add(new BufferedHeader(var2));
      return var7;
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
      return "compatibility";
   }
}
