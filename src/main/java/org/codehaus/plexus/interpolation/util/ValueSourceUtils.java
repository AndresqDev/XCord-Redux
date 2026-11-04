package org.codehaus.plexus.interpolation.util;

import java.util.Collection;

public final class ValueSourceUtils {
   private ValueSourceUtils() {
   }

   public static String trimPrefix(String var0, Collection<String> var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         String var3 = null;

         for (String var5 : var1) {
            if (var0.startsWith(var5)) {
               var3 = var0.substring(var5.length());
               if (var3.startsWith(".")) {
                  var3 = var3.substring(1);
               }
               break;
            }
         }

         if (var3 == null && var2) {
            var3 = var0;
         }

         return var3;
      }
   }

   public static String trimPrefix(String var0, String[] var1, boolean var2) {
      if (var0 == null) {
         return null;
      } else {
         String var3 = null;

         for (String var7 : var1) {
            if (var0.startsWith(var7)) {
               var3 = var0.substring(var7.length());
               if (var3.startsWith(".")) {
                  var3 = var3.substring(1);
               }
               break;
            }
         }

         if (var3 == null && var2) {
            var3 = var0;
         }

         return var3;
      }
   }
}
