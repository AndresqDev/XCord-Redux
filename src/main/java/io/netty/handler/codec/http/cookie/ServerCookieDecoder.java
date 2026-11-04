package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class ServerCookieDecoder extends CookieDecoder {
   private static final String RFC2965_VERSION = "$Version";
   private static final String RFC2965_PATH = "$Path";
   private static final String RFC2965_DOMAIN = "$Domain";
   private static final String RFC2965_PORT = "$Port";
   public static final ServerCookieDecoder STRICT = new ServerCookieDecoder(true);
   public static final ServerCookieDecoder LAX = new ServerCookieDecoder(false);

   private ServerCookieDecoder(boolean var1) {
      super(var1);
   }

   public List<Cookie> decodeAll(String var1) {
      ArrayList var2 = new ArrayList();
      this.decode(var2, var1);
      return Collections.unmodifiableList(var2);
   }

   public Set<Cookie> decode(String var1) {
      TreeSet var2 = new TreeSet();
      this.decode(var2, var1);
      return var2;
   }

   private void decode(Collection<? super Cookie> var1, String var2) {
      int var3 = ObjectUtil.checkNotNull(var2, "header").length();
      if (var3 != 0) {
         int var4 = 0;
         boolean var5 = false;
         if (var2.regionMatches(true, 0, "$Version", 0, "$Version".length())) {
            var4 = var2.indexOf(59) + 1;
            var5 = true;
         }

         while (var4 != var3) {
            int var6 = var2.charAt(var4);
            if (var6 != 9 && var6 != 10 && var6 != 11 && var6 != 12 && var6 != 13 && var6 != 32 && var6 != 44 && var6 != 59) {
               var6 = var4;

               int var7;
               int var8;
               int var9;
               while (true) {
                  char var10 = var2.charAt(var4);
                  if (var10 == ';') {
                     var7 = var4;
                     var9 = -1;
                     var8 = -1;
                     break;
                  }

                  if (var10 == '=') {
                     var7 = var4++;
                     if (var4 == var3) {
                        var9 = 0;
                        var8 = 0;
                     } else {
                        var8 = var4;
                        int var11 = var2.indexOf(59, var4);
                        var9 = var4 = var11 > 0 ? var11 : var3;
                     }
                     break;
                  }

                  if (++var4 == var3) {
                     var7 = var3;
                     var9 = -1;
                     var8 = -1;
                     break;
                  }
               }

               if (!var5
                  || !var2.regionMatches(var6, "$Path", 0, "$Path".length())
                     && !var2.regionMatches(var6, "$Domain", 0, "$Domain".length())
                     && !var2.regionMatches(var6, "$Port", 0, "$Port".length())) {
                  DefaultCookie var13 = this.initCookie(var2, var6, var7, var8, var9);
                  if (var13 != null) {
                     var1.add(var13);
                  }
               }
            } else {
               var4++;
            }
         }
      }
   }
}
