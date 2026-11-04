package io.netty.resolver.dns;

import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

final class ShuffledDnsServerAddressStream implements DnsServerAddressStream {
   private final List<InetSocketAddress> addresses;
   private int i;

   ShuffledDnsServerAddressStream(List<InetSocketAddress> var1) {
      this.addresses = var1;
      this.shuffle();
   }

   private ShuffledDnsServerAddressStream(List<InetSocketAddress> var1, int var2) {
      this.addresses = var1;
      this.i = var2;
   }

   private void shuffle() {
      Collections.shuffle(this.addresses, PlatformDependent.threadLocalRandom());
   }

   @Override
   public InetSocketAddress next() {
      int var1 = this.i;
      InetSocketAddress var2 = this.addresses.get(var1);
      if (++var1 < this.addresses.size()) {
         this.i = var1;
      } else {
         this.i = 0;
         this.shuffle();
      }

      return var2;
   }

   @Override
   public int size() {
      return this.addresses.size();
   }

   public ShuffledDnsServerAddressStream duplicate() {
      return new ShuffledDnsServerAddressStream(this.addresses, this.i);
   }

   @Override
   public String toString() {
      return SequentialDnsServerAddressStream.toString("shuffled", this.i, this.addresses);
   }
}
