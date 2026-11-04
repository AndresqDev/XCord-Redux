package org.apache.http.client.utils;

import java.util.StringTokenizer;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class Rfc3492Idn implements Idn {
   private static final int base = 36;
   private static final int tmin = 1;
   private static final int tmax = 26;
   private static final int skew = 38;
   private static final int damp = 700;
   private static final int initial_bias = 72;
   private static final int initial_n = 128;
   private static final char delimiter = '-';
   private static final String ACE_PREFIX = "xn--";

   private int adapt(int var1, int var2, boolean var3) {
      int var4;
      if (var3) {
         var4 = var1 / 700;
      } else {
         var4 = var1 / 2;
      }

      var4 += var4 / var2;

      byte var5;
      for (var5 = 0; var4 > 455; var5 += 36) {
         var4 /= 35;
      }

      return var5 + 36 * var4 / (var4 + 38);
   }

   private int digit(char var1) {
      if (var1 >= 'A' && var1 <= 'Z') {
         return var1 - 65;
      } else if (var1 >= 'a' && var1 <= 'z') {
         return var1 - 97;
      } else if (var1 >= '0' && var1 <= '9') {
         return var1 - 48 + 26;
      } else {
         throw new IllegalArgumentException("illegal digit: " + var1);
      }
   }

   @Override
   public String toUnicode(String var1) {
      StringBuilder var2 = new StringBuilder(var1.length());
      StringTokenizer var3 = new StringTokenizer(var1, ".");

      while (var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         if (var2.length() > 0) {
            var2.append('.');
         }

         if (var4.startsWith("xn--")) {
            var4 = this.decode(var4.substring(4));
         }

         var2.append(var4);
      }

      return var2.toString();
   }

   protected String decode(String var1) {
      String var2 = var1;
      int var3 = 128;
      int var14 = 0;
      int var5 = 72;
      StringBuilder var6 = new StringBuilder(var1.length());
      int var7 = var1.lastIndexOf(45);
      if (var7 != -1) {
         var6.append(var1.subSequence(0, var7));
         var2 = var1.substring(var7 + 1);
      }

      while (!var2.isEmpty()) {
         int var8 = var14;
         int var9 = 1;

         for (byte var10 = 36; !var2.isEmpty(); var10 += 36) {
            char var11 = var2.charAt(0);
            var2 = var2.substring(1);
            int var12 = this.digit(var11);
            var14 += var12 * var9;
            int var13;
            if (var10 <= var5 + 1) {
               var13 = 1;
            } else if (var10 >= var5 + 26) {
               var13 = 26;
            } else {
               var13 = var10 - var5;
            }

            if (var12 < var13) {
               break;
            }

            var9 *= 36 - var13;
         }

         var5 = this.adapt(var14 - var8, var6.length() + 1, var8 == 0);
         var3 += var14 / (var6.length() + 1);
         var14 %= var6.length() + 1;
         var6.insert(var14, (char)var3);
         var14++;
      }

      return var6.toString();
   }
}
