package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class DefaultDnsCache implements DnsCache {
   private final Cache<DefaultDnsCache.DefaultDnsCacheEntry> resolveCache = new Cache<DefaultDnsCache.DefaultDnsCacheEntry>() {
      protected boolean shouldReplaceAll(DefaultDnsCache.DefaultDnsCacheEntry var1) {
         return var1.cause() != null;
      }

      protected boolean equals(DefaultDnsCache.DefaultDnsCacheEntry var1, DefaultDnsCache.DefaultDnsCacheEntry var2) {
         if (var1.address() != null) {
            return var1.address().equals(var2.address());
         } else {
            return var2.address() != null ? false : var1.cause().equals(var2.cause());
         }
      }
   };
   private final int minTtl;
   private final int maxTtl;
   private final int negativeTtl;

   public DefaultDnsCache() {
      this(0, Cache.MAX_SUPPORTED_TTL_SECS, 0);
   }

   public DefaultDnsCache(int var1, int var2, int var3) {
      this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var1, "minTtl"));
      this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var2, "maxTtl"));
      if (var1 > var2) {
         throw new IllegalArgumentException("minTtl: " + var1 + ", maxTtl: " + var2 + " (expected: 0 <= minTtl <= maxTtl)");
      } else {
         this.negativeTtl = ObjectUtil.checkPositiveOrZero(var3, "negativeTtl");
      }
   }

   public int minTtl() {
      return this.minTtl;
   }

   public int maxTtl() {
      return this.maxTtl;
   }

   public int negativeTtl() {
      return this.negativeTtl;
   }

   @Override
   public void clear() {
      this.resolveCache.clear();
   }

   @Override
   public boolean clear(String var1) {
      ObjectUtil.checkNotNull(var1, "hostname");
      return this.resolveCache.clear(appendDot(var1));
   }

   private static boolean emptyAdditionals(DnsRecord[] var0) {
      return var0 == null || var0.length == 0;
   }

   @Override
   public List<? extends DnsCacheEntry> get(String var1, DnsRecord[] var2) {
      ObjectUtil.checkNotNull(var1, "hostname");
      return !emptyAdditionals(var2) ? Collections.emptyList() : this.resolveCache.get(appendDot(var1));
   }

   @Override
   public DnsCacheEntry cache(String var1, DnsRecord[] var2, InetAddress var3, long var4, EventLoop var6) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var3, "address");
      ObjectUtil.checkNotNull(var6, "loop");
      DefaultDnsCache.DefaultDnsCacheEntry var7 = new DefaultDnsCache.DefaultDnsCacheEntry(var1, var3);
      if (this.maxTtl != 0 && emptyAdditionals(var2)) {
         this.resolveCache.cache(appendDot(var1), var7, Math.max(this.minTtl, (int)Math.min((long)this.maxTtl, var4)), var6);
         return var7;
      } else {
         return var7;
      }
   }

   @Override
   public DnsCacheEntry cache(String var1, DnsRecord[] var2, Throwable var3, EventLoop var4) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var3, "cause");
      ObjectUtil.checkNotNull(var4, "loop");
      DefaultDnsCache.DefaultDnsCacheEntry var5 = new DefaultDnsCache.DefaultDnsCacheEntry(var1, var3);
      if (this.negativeTtl != 0 && emptyAdditionals(var2)) {
         this.resolveCache.cache(appendDot(var1), var5, this.negativeTtl, var4);
         return var5;
      } else {
         return var5;
      }
   }

   @Override
   public String toString() {
      return "DefaultDnsCache(minTtl="
         + this.minTtl
         + ", maxTtl="
         + this.maxTtl
         + ", negativeTtl="
         + this.negativeTtl
         + ", cached resolved hostname="
         + this.resolveCache.size()
         + ')';
   }

   private static String appendDot(String var0) {
      return StringUtil.endsWith(var0, '.') ? var0 : var0 + '.';
   }

   private static final class DefaultDnsCacheEntry implements DnsCacheEntry {
      private final String hostname;
      private final InetAddress address;
      private final Throwable cause;

      DefaultDnsCacheEntry(String var1, InetAddress var2) {
         this.hostname = var1;
         this.address = var2;
         this.cause = null;
      }

      DefaultDnsCacheEntry(String var1, Throwable var2) {
         this.hostname = var1;
         this.cause = var2;
         this.address = null;
      }

      @Override
      public InetAddress address() {
         return this.address;
      }

      @Override
      public Throwable cause() {
         return this.cause;
      }

      String hostname() {
         return this.hostname;
      }

      @Override
      public String toString() {
         return this.cause != null ? this.hostname + '/' + this.cause : this.address.toString();
      }
   }
}
