package io.netty.resolver.dns;

import java.util.List;

public final class MultiDnsServerAddressStreamProvider implements DnsServerAddressStreamProvider {
   private final DnsServerAddressStreamProvider[] providers;

   public MultiDnsServerAddressStreamProvider(List<DnsServerAddressStreamProvider> var1) {
      this.providers = var1.toArray(new DnsServerAddressStreamProvider[0]);
   }

   public MultiDnsServerAddressStreamProvider(DnsServerAddressStreamProvider... var1) {
      this.providers = (DnsServerAddressStreamProvider[])var1.clone();
   }

   @Override
   public DnsServerAddressStream nameServerAddressStream(String var1) {
      for (DnsServerAddressStreamProvider var5 : this.providers) {
         DnsServerAddressStream var6 = var5.nameServerAddressStream(var1);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }
}
