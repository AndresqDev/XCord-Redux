package io.netty.resolver.dns;

import io.netty.channel.EventLoop;

public interface DnsCnameCache {
   String get(String var1);

   void cache(String var1, String var2, long var3, EventLoop var5);

   void clear();

   boolean clear(String var1);
}
