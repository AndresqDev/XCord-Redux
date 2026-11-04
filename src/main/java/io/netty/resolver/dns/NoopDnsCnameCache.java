package io.netty.resolver.dns;

import io.netty.channel.EventLoop;

public final class NoopDnsCnameCache implements DnsCnameCache {
   public static final NoopDnsCnameCache INSTANCE = new NoopDnsCnameCache();

   private NoopDnsCnameCache() {
   }

   @Override
   public String get(String var1) {
      return null;
   }

   @Override
   public void cache(String var1, String var2, long var3, EventLoop var5) {
   }

   @Override
   public void clear() {
   }

   @Override
   public boolean clear(String var1) {
      return false;
   }
}
