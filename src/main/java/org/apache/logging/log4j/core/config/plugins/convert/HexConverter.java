package org.apache.logging.log4j.core.config.plugins.convert;

public class HexConverter {
   public static byte[] parseHexBinary(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1 / 2];

      for (byte var3 = 0; var3 < var1; var3 += 2) {
         var2[var3 / 2] = (byte)((Character.digit(var0.charAt(var3), 16) << 4) + Character.digit(var0.charAt(var3 + 1), 16));
      }

      return var2;
   }
}
