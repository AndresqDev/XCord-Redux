package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

public class AddressUtil {
   public static String sanitizeAddress(InetSocketAddress var0) {
      Preconditions.checkArgument(!var0.isUnresolved(), "Unresolved address");
      String var1 = var0.getAddress().getHostAddress();
      if (var0.getAddress() instanceof Inet6Address) {
         int var2 = var1.indexOf(37);
         return var2 == -1 ? var1 : var1.substring(0, var2);
      } else {
         return var1;
      }
   }

   private AddressUtil() {
   }
}
