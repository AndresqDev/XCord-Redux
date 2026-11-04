package io.netty.resolver.dns;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;

final class SequentialDnsServerAddressStream implements DnsServerAddressStream {
   private final List<? extends InetSocketAddress> addresses;
   private int i;

   SequentialDnsServerAddressStream(List<? extends InetSocketAddress> var1, int var2) {
      this.addresses = var1;
      this.i = var2;
   }

   @Override
   public InetSocketAddress next() {
      int var1 = this.i;
      InetSocketAddress var2 = this.addresses.get(var1);
      if (++var1 < this.addresses.size()) {
         this.i = var1;
      } else {
         this.i = 0;
      }

      return var2;
   }

   @Override
   public int size() {
      return this.addresses.size();
   }

   public SequentialDnsServerAddressStream duplicate() {
      return new SequentialDnsServerAddressStream(this.addresses, this.i);
   }

   @Override
   public String toString() {
      return toString("sequential", this.i, this.addresses);
   }

   static String toString(String var0, int var1, Collection<? extends InetSocketAddress> var2) {
      StringBuilder var3 = new StringBuilder(var0.length() + 2 + var2.size() * 16);
      var3.append(var0).append("(index: ").append(var1);
      var3.append(", addrs: (");

      for (InetSocketAddress var5 : var2) {
         var3.append(var5).append(", ");
      }

      var3.setLength(var3.length() - 2);
      var3.append("))");
      return var3.toString();
   }
}
