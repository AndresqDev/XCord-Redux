package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public final class NoopDnsCache implements DnsCache {
   public static final NoopDnsCache INSTANCE = new NoopDnsCache();

   private NoopDnsCache() {
   }

   @Override
   public void clear() {
   }

   @Override
   public boolean clear(String var1) {
      return false;
   }

   @Override
   public List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2) {
      return Collections.emptyList();
   }

   @Override
   public DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6) {
      return new NoopDnsCache.NoopDnsCacheEntry(var3);
   }

   @Override
   public DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4) {
      return null;
   }

   @Override
   public String toString() {
      return NoopDnsCache.class.getSimpleName();
   }

   private static final class NoopDnsCacheEntry implements DnsCacheEntry {
      private final InetAddress address;

      NoopDnsCacheEntry(InetAddress var1) {
         this.address = var1;
      }

      @Override
      public InetAddress address() {
         return this.address;
      }

      @Override
      public Throwable cause() {
         return null;
      }

      @Override
      public String toString() {
         return this.address.toString();
      }
   }
}
