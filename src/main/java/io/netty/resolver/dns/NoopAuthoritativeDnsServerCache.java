package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetSocketAddress;

public final class NoopAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache {
   public static final NoopAuthoritativeDnsServerCache INSTANCE = new NoopAuthoritativeDnsServerCache();

   private NoopAuthoritativeDnsServerCache() {
   }

   @Override
   public DnsServerAddressStream get(String var1) {
      return null;
   }

   @Override
   public void cache(String var1, InetSocketAddress var2, long var3, EventLoop var5) {
   }

   @Override
   public void clear() {
   }

   @Override
   public boolean clear(String var1) {
      return false;
   }
}
