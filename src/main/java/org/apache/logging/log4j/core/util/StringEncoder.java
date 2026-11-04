package org.apache.logging.log4j.core.util;

import java.nio.charset.Charset;

public final class StringEncoder {
   private StringEncoder() {
   }

   public static byte[] toBytes(String var0, Charset var1) {
      return var0 != null ? var0.getBytes(var1 != null ? var1 : Charset.defaultCharset()) : null;
   }

   @Deprecated
   public static byte[] encodeSingleByteChars(CharSequence var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1];
      encodeString(var0, 0, var1, var2);
      return var2;
   }

   @Deprecated
   public static int encodeIsoChars(CharSequence var0, int var1, byte[] var2, int var3, int var4) {
      int var5;
      for (var5 = 0; var5 < var4; var5++) {
         char var6 = var0.charAt(var1++);
         if (var6 > 255) {
            break;
         }

         var2[var3++] = (byte)var6;
      }

      return var5;
   }

   @Deprecated
   public static int encodeString(CharSequence var0, int var1, int var2, byte[] var3) {
      int var4 = 0;
      int var5 = Math.min(var2, var3.length);
      int var6 = var1 + var5;

      while (var1 < var6) {
         int var7 = encodeIsoChars(var0, var1, var3, var4, var5);
         var1 += var7;
         var4 += var7;
         if (var7 != var5) {
            char var8 = var0.charAt(var1++);
            if (Character.isHighSurrogate(var8) && var1 < var6 && Character.isLowSurrogate(var0.charAt(var1))) {
               if (var2 > var3.length) {
                  var6++;
                  var2--;
               }

               var1++;
            }

            var3[var4++] = 63;
            var5 = Math.min(var6 - var1, var3.length - var4);
         }
      }

      return var4;
   }
}
