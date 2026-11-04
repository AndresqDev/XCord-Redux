package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

final class AuthoritativeDnsServerCacheAdapter implements AuthoritativeDnsServerCache {
   private static final DnsRecord[] EMPTY = new DnsRecord[0];
   private final DnsCache cache;

   AuthoritativeDnsServerCacheAdapter(DnsCache var1) {
      this.cache = ObjectUtil.checkNotNull(var1, "cache");
   }

   @Override
   public DnsServerAddressStream get(String var1) {
      List var2 = this.cache.get(var1, EMPTY);
      if (var2 == null || var2.isEmpty()) {
         return null;
      } else if (((DnsCacheEntry)var2.get(0)).cause() != null) {
         return null;
      } else {
         ArrayList var3 = new ArrayList(var2.size());
         int var4 = 0;

         do {
            InetAddress var5 = ((DnsCacheEntry)var2.get(var4)).address();
            var3.add(new InetSocketAddress(var5, 53));
         } while (++var4 < var2.size());

         return new SequentialDnsServerAddressStream(var3, 0);
      }
   }

   @Override
   public void cache(String var1, InetSocketAddress var2, long var3, EventLoop var5) {
      if (!var2.isUnresolved()) {
         this.cache.cache(var1, EMPTY, var2.getAddress(), var3, var5);
      }
   }

   @Override
   public void clear() {
      this.cache.clear();
   }

   @Override
   public boolean clear(String var1) {
      return this.cache.clear(var1);
   }
}
