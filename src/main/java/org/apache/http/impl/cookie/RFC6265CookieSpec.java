package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePriorityComparator;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class RFC6265CookieSpec implements CookieSpec {
   private static final char PARAM_DELIMITER = ';';
   private static final char COMMA_CHAR = ',';
   private static final char EQUAL_CHAR = '=';
   private static final char DQUOTE_CHAR = '"';
   private static final char ESCAPE_CHAR = '\\';
   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59);
   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(59);
   private static final BitSet SPECIAL_CHARS = TokenParser.INIT_BITSET(32, 34, 44, 59, 92);
   private final CookieAttributeHandler[] attribHandlers;
   private final Map<String, CookieAttributeHandler> attribHandlerMap;
   private final TokenParser tokenParser;

   protected RFC6265CookieSpec(CommonCookieAttributeHandler... var1) {
      this.attribHandlers = (CookieAttributeHandler[])var1.clone();
      this.attribHandlerMap = new ConcurrentHashMap<>(var1.length);

      for (CommonCookieAttributeHandler var5 : var1) {
         this.attribHandlerMap.put(var5.getAttributeName().toLowerCase(Locale.ROOT), var5);
      }

      this.tokenParser = TokenParser.INSTANCE;
   }

   static String getDefaultPath(CookieOrigin var0) {
      String var1 = var0.getPath();
      int var2 = var1.lastIndexOf(47);
      if (var2 >= 0) {
         if (var2 == 0) {
            var2 = 1;
         }

         var1 = var1.substring(0, var2);
      }

      return var1;
   }

   static String getDefaultDomain(CookieOrigin var0) {
      return var0.getHost();
   }

   @Override
   public final List<Cookie> parse(Header var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Header");
      Args.notNull(var2, "Cookie origin");
      if (!var1.getName().equalsIgnoreCase("Set-Cookie")) {
         throw new MalformedCookieException("Unrecognized cookie header: '" + var1.toString() + "'");
      } else {
         CharArrayBuffer var3;
         ParserCursor var4;
         if (var1 instanceof FormattedHeader) {
            var3 = ((FormattedHeader)var1).getBuffer();
            var4 = new ParserCursor(((FormattedHeader)var1).getValuePos(), var3.length());
         } else {
            String var5 = var1.getValue();
            if (var5 == null) {
               throw new MalformedCookieException("Header value is null");
            }

            var3 = new CharArrayBuffer(var5.length());
            var3.append(var5);
            var4 = new ParserCursor(0, var3.length());
         }

         String var15 = this.tokenParser.parseToken(var3, var4, TOKEN_DELIMS);
         if (var15.isEmpty()) {
            return Collections.emptyList();
         } else if (var4.atEnd()) {
            return Collections.emptyList();
         } else {
            char var6 = var3.charAt(var4.getPos());
            var4.updatePos(var4.getPos() + 1);
            if (var6 != '=') {
               throw new MalformedCookieException("Cookie value is invalid: '" + var1.toString() + "'");
            } else {
               String var7 = this.tokenParser.parseValue(var3, var4, VALUE_DELIMS);
               if (!var4.atEnd()) {
                  var4.updatePos(var4.getPos() + 1);
               }

               BasicClientCookie var8 = new BasicClientCookie(var15, var7);
               var8.setPath(getDefaultPath(var2));
               var8.setDomain(getDefaultDomain(var2));
               var8.setCreationDate(new Date());
               LinkedHashMap var9 = new LinkedHashMap();

               while (!var4.atEnd()) {
                  String var10 = this.tokenParser.parseToken(var3, var4, TOKEN_DELIMS).toLowerCase(Locale.ROOT);
                  String var11 = null;
                  if (!var4.atEnd()) {
                     char var12 = var3.charAt(var4.getPos());
                     var4.updatePos(var4.getPos() + 1);
                     if (var12 == '=') {
                        var11 = this.tokenParser.parseToken(var3, var4, VALUE_DELIMS);
                        if (!var4.atEnd()) {
                           var4.updatePos(var4.getPos() + 1);
                        }
                     }
                  }

                  var8.setAttribute(var10, var11);
                  var9.put(var10, var11);
               }

               if (var9.containsKey("max-age")) {
                  var9.remove("expires");
               }

               for (Entry var17 : var9.entrySet()) {
                  String var18 = (String)var17.getKey();
                  String var13 = (String)var17.getValue();
                  CookieAttributeHandler var14 = this.attribHandlerMap.get(var18);
                  if (var14 != null) {
                     var14.parse(var8, var13);
                  }
               }

               return Collections.singletonList(var8);
            }
         }
      }
   }

   @Override
   public final void validate(Cookie var1, CookieOrigin var2) throws MalformedCookieException {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");

      for (CookieAttributeHandler var6 : this.attribHandlers) {
         var6.validate(var1, var2);
      }
   }

   @Override
   public final boolean match(Cookie var1, CookieOrigin var2) {
      Args.notNull(var1, "Cookie");
      Args.notNull(var2, "Cookie origin");

      for (CookieAttributeHandler var6 : this.attribHandlers) {
         if (!var6.match(var1, var2)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public List<Header> formatCookies(List<Cookie> var1) {
      Args.notEmpty(var1, "List of cookies");
      Object var2;
      if (var1.size() > 1) {
         var2 = new ArrayList(var1);
         Collections.sort((List<Cookie>)var2, CookiePriorityComparator.INSTANCE);
      } else {
         var2 = var1;
      }

      CharArrayBuffer var3 = new CharArrayBuffer(20 * var2.size());
      var3.append("Cookie");
      var3.append(": ");

      for (int var4 = 0; var4 < var2.size(); var4++) {
         Cookie var5 = (Cookie)var2.get(var4);
         if (var4 > 0) {
            var3.append(';');
            var3.append(' ');
         }

         var3.append(var5.getName());
         String var6 = var5.getValue();
         if (var6 != null) {
            var3.append('=');
            if (!this.containsSpecialChar(var6)) {
               var3.append(var6);
            } else {
               var3.append('"');

               for (int var7 = 0; var7 < var6.length(); var7++) {
                  char var8 = var6.charAt(var7);
                  if (var8 == '"' || var8 == '\\') {
                     var3.append('\\');
                  }

                  var3.append(var8);
               }

               var3.append('"');
            }
         }
      }

      ArrayList var9 = new ArrayList(1);
      var9.add(new BufferedHeader(var3));
      return var9;
   }

   boolean containsSpecialChar(CharSequence var1) {
      return this.containsChars(var1, SPECIAL_CHARS);
   }

   boolean containsChars(CharSequence var1, BitSet var2) {
      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var2.get(var4)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final int getVersion() {
      return 0;
   }

   @Override
   public final Header getVersionHeader() {
      return null;
   }
}
