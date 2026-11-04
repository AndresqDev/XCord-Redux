package io.netty.resolver.dns;

import io.netty.util.internal.ObjectUtil;

abstract class UniSequentialDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private final DnsServerAddresses addresses;

   UniSequentialDnsServerAddressStreamProvider(DnsServerAddresses var1) {
      this.addresses = ObjectUtil.checkNotNull(var1, "addresses");
   }

   @Override
   public final DnsServerAddressStream nameServerAddressStream(String var1) {
      return this.addresses.stream();
   }
}
