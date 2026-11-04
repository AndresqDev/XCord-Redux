package org.jline.utils;

import java.util.HashMap;

public class Levenshtein {
   public static int distance(CharSequence var0, CharSequence var1) {
      return distance(var0, var1, 1, 1, 1, 1);
   }

   public static int distance(CharSequence var0, CharSequence var1, int var2, int var3, int var4, int var5) {
      if (2 * var5 < var3 + var2) {
         throw new IllegalArgumentException("Unsupported cost assignment");
      } else if (var0.length() == 0) {
         return var1.length() * var3;
      } else if (var1.length() == 0) {
         return var0.length() * var2;
      } else {
         int[][] var6 = new int[var0.length()][var1.length()];
         HashMap var7 = new HashMap();
         if (var0.charAt(0) != var1.charAt(0)) {
            var6[0][0] = Math.min(var4, var2 + var3);
         }

         var7.put(var0.charAt(0), 0);

         for (int var8 = 1; var8 < var0.length(); var8++) {
            int var9 = var6[var8 - 1][0] + var2;
            int var10 = (var8 + 1) * var2 + var3;
            int var11 = var8 * var2 + (var0.charAt(var8) == var1.charAt(0) ? 0 : var4);
            var6[var8][0] = Math.min(Math.min(var9, var10), var11);
         }

         for (int var19 = 1; var19 < var1.length(); var19++) {
            int var21 = (var19 + 1) * var3 + var2;
            int var23 = var6[0][var19 - 1] + var3;
            int var25 = var19 * var3 + (var0.charAt(0) == var1.charAt(var19) ? 0 : var4);
            var6[0][var19] = Math.min(Math.min(var21, var23), var25);
         }

         for (int var20 = 1; var20 < var0.length(); var20++) {
            int var22 = var0.charAt(var20) == var1.charAt(0) ? 0 : -1;

            for (int var24 = 1; var24 < var1.length(); var24++) {
               Integer var26 = (Integer)var7.get(var1.charAt(var24));
               int var12 = var22;
               int var13 = var6[var20 - 1][var24] + var2;
               int var14 = var6[var20][var24 - 1] + var3;
               int var15 = var6[var20 - 1][var24 - 1];
               if (var0.charAt(var20) != var1.charAt(var24)) {
                  var15 += var4;
               } else {
                  var22 = var24;
               }

               int var16;
               if (var26 != null && var12 != -1) {
                  int var17 = var26;
                  int var18;
                  if (var17 == 0 && var12 == 0) {
                     var18 = 0;
                  } else {
                     var18 = var6[Math.max(0, var17 - 1)][Math.max(0, var12 - 1)];
                  }

                  var16 = var18 + (var20 - var17 - 1) * var2 + (var24 - var12 - 1) * var3 + var5;
               } else {
                  var16 = Integer.MAX_VALUE;
               }

               var6[var20][var24] = Math.min(Math.min(Math.min(var13, var14), var15), var16);
            }

            var7.put(var0.charAt(var20), var20);
         }

         return var6[var0.length() - 1][var1.length() - 1];
      }
   }
}
