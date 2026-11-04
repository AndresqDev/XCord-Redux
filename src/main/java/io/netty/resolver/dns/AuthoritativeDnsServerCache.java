package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import java.net.InetSocketAddress;

public interface AuthoritativeDnsServerCache {
   DnsServerAddressStream get(String var1);

   void cache(String var1, InetSocketAddress var2, long var3, EventLoop var5);

   void clear();

   boolean clear(String var1);
}
