package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {
   private final Log log = LogFactory.getLog(this.getClass());
   private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList("Negotiate", "NTLM", "Digest", "Basic"));

   protected Map<String, Header> parseChallenges(Header[] var1) throws MalformedChallengeException {
      HashMap var2 = new HashMap(var1.length);

      for (Header var6 : var1) {
         CharArrayBuffer var7;
         int var8;
         if (var6 instanceof FormattedHeader) {
            var7 = ((FormattedHeader)var6).getBuffer();
            var8 = ((FormattedHeader)var6).getValuePos();
         } else {
            String var9 = var6.getValue();
            if (var9 == null) {
               throw new MalformedChallengeException("Header value is null");
            }

            var7 = new CharArrayBuffer(var9.length());
            var7.append(var9);
            var8 = 0;
         }

         while (var8 < var7.length() && HTTP.isWhitespace(var7.charAt(var8))) {
            var8++;
         }

         int var12 = var8;

         while (var8 < var7.length() && !HTTP.isWhitespace(var7.charAt(var8))) {
            var8++;
         }

         String var11 = var7.substring(var12, var8);
         var2.put(var11.toLowerCase(Locale.ROOT), var6);
      }

      return var2;
   }

   protected List<String> getAuthPreferences() {
      return DEFAULT_SCHEME_PRIORITY;
   }

   protected List<String> getAuthPreferences(HttpResponse var1, HttpContext var2) {
      return this.getAuthPreferences();
   }

   @Override
   public AuthScheme selectScheme(Map<String, Header> var1, HttpResponse var2, HttpContext var3) throws AuthenticationException {
      AuthSchemeRegistry var4 = (AuthSchemeRegistry)var3.getAttribute("http.authscheme-registry");
      Asserts.notNull(var4, "AuthScheme registry");
      List var5 = this.getAuthPreferences(var2, var3);
      if (var5 == null) {
         var5 = DEFAULT_SCHEME_PRIORITY;
      }

      if (this.log.isDebugEnabled()) {
         this.log.debug("Authentication schemes in the order of preference: " + var5);
      }

      AuthScheme var6 = null;

      for (String var8 : var5) {
         Header var9 = (Header)var1.get(var8.toLowerCase(Locale.ENGLISH));
         if (var9 != null) {
            if (this.log.isDebugEnabled()) {
               this.log.debug(var8 + " authentication scheme selected");
            }

            try {
               var6 = var4.getAuthScheme(var8, var2.getParams());
               break;
            } catch (IllegalStateException var11) {
               if (this.log.isWarnEnabled()) {
                  this.log.warn("Authentication scheme " + var8 + " not supported");
               }
            }
         } else if (this.log.isDebugEnabled()) {
            this.log.debug("Challenge for " + var8 + " authentication scheme not available");
         }
      }

      if (var6 == null) {
         throw new AuthenticationException("Unable to respond to any of these challenges: " + var1);
      } else {
         return var6;
      }
   }
}
