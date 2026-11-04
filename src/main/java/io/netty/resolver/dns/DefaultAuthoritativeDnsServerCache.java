package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefaultAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache {
   private final int minTtl;
   private final int maxTtl;
   private final Comparator<InetSocketAddress> comparator;
   private final Cache<InetSocketAddress> resolveCache = new Cache<InetSocketAddress>() {
      protected boolean shouldReplaceAll(InetSocketAddress var1) {
         return false;
      }

      protected boolean equals(InetSocketAddress var1, InetSocketAddress var2) {
         return PlatformDependent.javaVersion() >= 7
            ? var1.getHostString().equalsIgnoreCase(var2.getHostString())
            : var1.getHostName().equalsIgnoreCase(var2.getHostName());
      }

      @Override
      protected void sortEntries(String var1, List<InetSocketAddress> var2) {
         if (DefaultAuthoritativeDnsServerCache.this.comparator != null) {
            Collections.sort(var2, DefaultAuthoritativeDnsServerCache.this.comparator);
         }
      }
   };

   public DefaultAuthoritativeDnsServerCache() {
      this(0, Cache.MAX_SUPPORTED_TTL_SECS, null);
   }

   public DefaultAuthoritativeDnsServerCache(int var1, int var2, Comparator<InetSocketAddress> var3) {
      this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var1, "minTtl"));
      this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositive(var2, "maxTtl"));
      if (var1 > var2) {
         throw new IllegalArgumentException("minTtl: " + var1 + ", maxTtl: " + var2 + " (expected: 0 <= minTtl <= maxTtl)");
      } else {
         this.comparator = var3;
      }
   }

   @Override
   public DnsServerAddressStream get(String var1) {
      ObjectUtil.checkNotNull(var1, "hostname");
      List var2 = this.resolveCache.get(var1);
      return var2 != null && !var2.isEmpty() ? new SequentialDnsServerAddressStream(var2, 0) : null;
   }

   @Override
   public void cache(String var1, InetSocketAddress var2, long var3, EventLoop var5) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var2, "address");
      ObjectUtil.checkNotNull(var5, "loop");
      if (PlatformDependent.javaVersion() < 7 || var2.getHostString() != null) {
         this.resolveCache.cache(var1, var2, Math.max(this.minTtl, (int)Math.min((long)this.maxTtl, var3)), var5);
      }
   }

   @Override
   public void clear() {
      this.resolveCache.clear();
   }

   @Override
   public boolean clear(String var1) {
      return this.resolveCache.clear(ObjectUtil.checkNotNull(var1, "hostname"));
   }

   @Override
   public String toString() {
      return "DefaultAuthoritativeDnsServerCache(minTtl=" + this.minTtl + ", maxTtl=" + this.maxTtl + ", cached nameservers=" + this.resolveCache.size() + ')';
   }
}
