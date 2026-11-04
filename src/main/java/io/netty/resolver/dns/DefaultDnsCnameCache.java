package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public final class DefaultDnsCnameCache implements DnsCnameCache {
   private final int minTtl;
   private final int maxTtl;
   private final Cache<String> cache = new Cache<String>() {
      protected boolean shouldReplaceAll(String var1) {
         return true;
      }

      protected boolean equals(String var1, String var2) {
         return AsciiString.contentEqualsIgnoreCase(var1, var2);
      }
   };

   public DefaultDnsCnameCache() {
      this(0, Cache.MAX_SUPPORTED_TTL_SECS);
   }

   public DefaultDnsCnameCache(int var1, int var2) {
      this.minTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositiveOrZero(var1, "minTtl"));
      this.maxTtl = Math.min(Cache.MAX_SUPPORTED_TTL_SECS, ObjectUtil.checkPositive(var2, "maxTtl"));
      if (var1 > var2) {
         throw new IllegalArgumentException("minTtl: " + var1 + ", maxTtl: " + var2 + " (expected: 0 <= minTtl <= maxTtl)");
      }
   }

   @Override
   public String get(String var1) {
      List var2 = this.cache.get(ObjectUtil.checkNotNull(var1, "hostname"));
      return var2 != null && !var2.isEmpty() ? (String)var2.get(0) : null;
   }

   @Override
   public void cache(String var1, String var2, long var3, EventLoop var5) {
      ObjectUtil.checkNotNull(var1, "hostname");
      ObjectUtil.checkNotNull(var2, "cname");
      ObjectUtil.checkNotNull(var5, "loop");
      this.cache.cache(var1, var2, Math.max(this.minTtl, (int)Math.min((long)this.maxTtl, var3)), var5);
   }

   @Override
   public void clear() {
      this.cache.clear();
   }

   @Override
   public boolean clear(String var1) {
      return this.cache.clear(ObjectUtil.checkNotNull(var1, "hostname"));
   }
}
