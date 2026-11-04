package org.apache.http.conn.util;

public class DnsUtils {
   private DnsUtils() {
   }

   private static boolean isUpper(char var0) {
      return var0 >= 'A' && var0 <= 'Z';
   }

   public static String normalize(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = 0;

         int var2;
         for (var2 = var0.length(); var2 > 0 && !isUpper(var0.charAt(var1)); var2--) {
            var1++;
         }

         if (var2 > 0) {
            StringBuilder var3 = new StringBuilder(var0.length());
            var3.append(var0, 0, var1);

            while (var2 > 0) {
               char var4 = var0.charAt(var1);
               if (isUpper(var4)) {
                  var3.append((char)(var4 + ' '));
               } else {
                  var3.append(var4);
               }

               var1++;
               var2--;
            }

            return var3.toString();
         } else {
            return var0;
         }
      }
   }
}
