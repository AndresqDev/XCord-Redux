package io.netty.resolver.dns;

import io.netty.channel.socket.InternetProtocolFamily;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Comparator;

final class PreferredAddressTypeComparator implements Comparator<InetAddress> {
   private static final PreferredAddressTypeComparator IPv4 = new PreferredAddressTypeComparator(Inet4Address.class);
   private static final PreferredAddressTypeComparator IPv6 = new PreferredAddressTypeComparator(Inet6Address.class);
   private final Class<? extends InetAddress> preferredAddressType;

   static PreferredAddressTypeComparator comparator(InternetProtocolFamily var0) {
      switch (var0) {
         case IPv4:
            return IPv4;
         case IPv6:
            return IPv6;
         default:
            throw new IllegalArgumentException();
      }
   }

   private PreferredAddressTypeComparator(Class<? extends InetAddress> var1) {
      this.preferredAddressType = var1;
   }

   public int compare(InetAddress var1, InetAddress var2) {
      if (var1.getClass() == var2.getClass()) {
         return 0;
      } else {
         return this.preferredAddressType.isAssignableFrom(var1.getClass()) ? -1 : 1;
      }
   }
}
