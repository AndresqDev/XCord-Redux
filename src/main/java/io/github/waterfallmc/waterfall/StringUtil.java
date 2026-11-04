package io.github.waterfallmc.waterfall;

public class StringUtil {
   public static boolean isBlank(String var0) {
      if (var0.isEmpty()) {
         return true;
      } else {
         int var1 = var0.length();

         for (int var2 = 0; var2 < var1; var2++) {
            if (!Character.isWhitespace(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   private StringUtil() {
   }
}
