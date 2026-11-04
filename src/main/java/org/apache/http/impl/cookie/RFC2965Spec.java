package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Obsolete
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class RFC2965Spec extends RFC2109Spec {
   public RFC2965Spec() {
      this(null, false);
   }

   public RFC2965Spec(String[] var1, boolean var2) {
      super(
         var2,
         new RFC2965VersionAttributeHandler(),
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
         new RFC2965DomainAttributeHandler(),
         new RFC2965PortAttributeHandler(),
         new BasicMaxAgeHandler(),
         new BasicSecureHandler(),
         new BasicCommentHandler(),
         new BasicExpiresHandler(var1 != null ? (String[])var1.clone() : DATE_PATTERNS),
         new RFC2965CommentUrlAttributeHandler(),
         new RFC2965DiscardAttributeHandler()
      );
   }

   RFC2965Spec(boolean var1, CommonCookieAttributeHandler... var2) {
      super(var1, var2);
   }

   @Override
   public List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      if (!var1.getName().equalsIgnoreCase("Set-Cookie2")) {
         throw new MalformedCookieException("Unrecognized cookie header '" + var1.toString() + "'");
      } else {
         HeaderElement[] var3 = var1.getElements();
         return this.createCookies(var3, adjustEffectiveHost(var2));
      }
   }

   @Override
   protected List<Cookie> parse(HeaderElement[] var1, CookieOrigin var2) throws MalformedCookieException {
      return this.createCookies(var1, adjustEffectiveHost(var2));
   }

   private List<Cookie> createCookies(HeaderElement[] var1, CookieOrigin var2) throws MalformedCookieException {
      ArrayList var3 = new ArrayList(var1.length);

      for (HeaderElement var7 : var1) {
         String var8 = var7.getName();
         String var9 = var7.getValue();
         if (var8 == null || var8.isEmpty()) {
            throw new MalformedCookieException("Cookie name may not be empty");
         }

         BasicClientCookie2 var10 = new BasicClientCookie2(var8, var9);
         var10.setPath(getDefaultPath(var2));
         var10.setDomain(getDefaultDomain(var2));
         var10.setPorts(new int[]{var2.getPort()});
         NameValuePair[] var11 = var7.getParameters();
         HashMap var12 = new HashMap(var11.length);

         for (int var13 = var11.length - 1; var13 >= 0; var13--) {
            NameValuePair var14 = var11[var13];
            var12.put(var14.getName().toLowerCase(Locale.ROOT), var14);
         }

         for (Entry var19 : var12.entrySet()) {
            NameValuePair var15 = (NameValuePair)var19.getValue();
            String var16 = var15.getName().toLowerCase(Locale.ROOT);
            var10.setAttribute(var16, var15.getValue());
            CookieAttributeHandler var17 = this.findAttribHandler(var16);
            if (var17 != null) {
               var17.parse(var10, var15.getValue());
            }
         }

         var3.add(var10);
      }

      return var3;
   }

   @Override
   public void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      super.validate(var1, adjustEffectiveHost(var2));
   }

   @Override
   public boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");
      return super.match(var1, adjustEffectiveHost(var2));
   }

   @Override
   protected void formatCookieAsVer(CharArrayBuffer var1, Cookie var2, int var3) {
      super.formatCookieAsVer(var1, var2, var3);
      if (var2 instanceof ClientCookie) {
         String var4 = ((ClientCookie)var2).getAttribute("port");
         if (var4 != null) {
            var1.append("; $Port");
            var1.append("=\"");
            if (!var4.trim().isEmpty()) {
               int[] var5 = var2.getPorts();
               if (var5 != null) {
                  int var6 = var5.length;

                  for (int var7 = 0; var7 < var6; var7++) {
                     if (var7 > 0) {
                        var1.append(",");
                     }

                     var1.append(Integer.toString(var5[var7]));
                  }
               }
            }

            var1.append("\"");
         }
      }
   }

   private static CookieOrigin adjustEffectiveHost(CookieOrigin var0) {
      String var1 = var0.getHost();
      boolean var2 = true;

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var4 == '.' || var4 == ':') {
            var2 = false;
            break;
         }
      }

      return var2 ? new CookieOrigin(var1 + ".local", var0.getPort(), var0.getPath(), var0.isSecure()) : var0;
   }

   @Override
   public int getVersion() {
      return 1;
   }

   @Override
   public Header getVersionHeader() {
      CharArrayBuffer var1 = new CharArrayBuffer(40);
      var1.append("Cookie2");
      var1.append(": ");
      var1.append("$Version=");
      var1.append(Integer.toString(this.getVersion()));
      return new BufferedHeader(var1);
   }

   @Override
   public String toString() {
      return "rfc2965";
   }
}
