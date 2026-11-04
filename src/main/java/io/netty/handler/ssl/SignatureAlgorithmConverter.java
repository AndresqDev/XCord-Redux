package io.netty.handler.ssl;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SignatureAlgorithmConverter {
   private static final Pattern PATTERN = Pattern.compile(
      "(?:(^[a-zA-Z].+)With(.+)Encryption$)|(?:(^[a-zA-Z].+)(?:_with_|-with-|_pkcs1_|_pss_rsae_)(.+$))|(?:(^[a-zA-Z].+)_(.+$))"
   );

   private SignatureAlgorithmConverter() {
   }

   static String toJavaName(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Matcher var1 = PATTERN.matcher(var0);
         if (var1.matches()) {
            String var2 = var1.group(1);
            if (var2 != null) {
               return var2.toUpperCase(Locale.ROOT) + "with" + var1.group(2).toUpperCase(Locale.ROOT);
            }

            if (var1.group(3) != null) {
               return var1.group(4).toUpperCase(Locale.ROOT) + "with" + var1.group(3).toUpperCase(Locale.ROOT);
            }

            if (var1.group(5) != null) {
               return var1.group(6).toUpperCase(Locale.ROOT) + "with" + var1.group(5).toUpperCase(Locale.ROOT);
            }
         }

         return null;
      }
   }
}
