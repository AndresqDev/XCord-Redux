package org.fusesource.jansi;

import java.io.UnsupportedEncodingException;
import org.fusesource.jansi.internal.Kernel32;

public class WindowsSupport {
   public static String getLastErrorMessage() {
      int var0 = Kernel32.GetLastError();
      return getErrorMessage(var0);
   }

   public static String getErrorMessage(int var0) {
      short var1 = 160;
      byte[] var2 = new byte[var1];
      Kernel32.FormatMessageW(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, 0L, var0, 0, var2, var1, null);

      try {
         return new String(var2, "UTF-16LE").trim();
      } catch (UnsupportedEncodingException var4) {
         throw new IllegalStateException(var4);
      }
   }
}
