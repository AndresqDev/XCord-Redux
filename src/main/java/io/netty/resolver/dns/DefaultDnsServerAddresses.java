package io.netty.resolver.dns;

import java.net.InetSocketAddress;
import java.util.List;

abstract class DefaultDnsServerAddresses extends DnsServerAddresses {
   protected final List<InetSocketAddress> addresses;
   private final String strVal;

   DefaultDnsServerAddresses(String var1, List<InetSocketAddress> var2) {
      this.addresses = var2;
      StringBuilder var3 = new StringBuilder(var1.length() + 2 + var2.size() * 16);
      var3.append(var1).append('(');

      for (InetSocketAddress var5 : var2) {
         var3.append(var5).append(", ");
      }

      var3.setLength(var3.length() - 2);
      var3.append(')');
      this.strVal = var3.toString();
   }

   @Override
   public String toString() {
      return this.strVal;
   }
}
