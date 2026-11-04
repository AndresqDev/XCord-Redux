package org.apache.logging.log4j.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;

public final class NameUtil {
   private NameUtil() {
   }

   public static String getSubName(String var0) {
      if (Strings.isEmpty(var0)) {
         return null;
      } else {
         int var1 = var0.lastIndexOf(46);
         return var1 > 0 ? var0.substring(0, var1) : "";
      }
   }

   public static String md5(String var0) {
      Objects.requireNonNull(var0, "input");

      try {
         byte[] var1 = var0.getBytes();
         MessageDigest var2 = MessageDigest.getInstance("MD5");
         byte[] var3 = var2.digest(var1);
         StringBuilder var4 = new StringBuilder(var3.length * 2);

         for (byte var8 : var3) {
            String var9 = Integer.toHexString(255 & var8);
            if (var9.length() == 1) {
               var4.append('0');
            }

            var4.append(var9);
         }

         return var4.toString();
      } catch (NoSuchAlgorithmException var10) {
         throw new RuntimeException(var10);
      }
   }
}
