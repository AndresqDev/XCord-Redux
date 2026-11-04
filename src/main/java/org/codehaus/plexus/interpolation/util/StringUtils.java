package org.codehaus.plexus.interpolation.util;

public class StringUtils {
   public static String replace(String var0, String var1, String var2) {
      return replace(var0, var1, var2, -1);
   }

   public static String replace(String var0, String var1, String var2, int var3) {
      if (var0 != null && var1 != null && var2 != null && var1.length() != 0) {
         StringBuilder var4 = new StringBuilder(var0.length());
         int var5 = 0;

         int var6;
         while ((var6 = var0.indexOf(var1, var5)) != -1) {
            var4.append(var0, var5, var6).append(var2);
            var5 = var6 + var1.length();
            if (--var3 == 0) {
               break;
            }
         }

         var4.append(var0, var5, var0.length());
         return var4.toString();
      } else {
         return var0;
      }
   }

   public static String capitalizeFirstLetter(String var0) {
      char var1 = var0.charAt(0);
      char var2 = Character.toTitleCase(var1);
      if (var1 == var2) {
         return var0;
      } else {
         StringBuilder var3 = new StringBuilder(var0.length());
         var3.append(var2);
         var3.append(var0, 1, var0.length());
         return var3.toString();
      }
   }
}
