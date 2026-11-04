package io.netty.resolver;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultHostsFileEntriesResolver implements HostsFileEntriesResolver {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHostsFileEntriesResolver.class);
   private static final long DEFAULT_REFRESH_INTERVAL = SystemPropertyUtil.getLong("io.netty.hostsFileRefreshInterval", 0L);
   private final long refreshInterval;
   private final AtomicLong lastRefresh = new AtomicLong(System.nanoTime());
   private final HostsFileEntriesProvider.Parser hostsFileParser;
   private volatile Map<String, List<InetAddress>> inet4Entries;
   private volatile Map<String, List<InetAddress>> inet6Entries;

   public DefaultHostsFileEntriesResolver() {
      this(HostsFileEntriesProvider.parser(), DEFAULT_REFRESH_INTERVAL);
   }

   DefaultHostsFileEntriesResolver(HostsFileEntriesProvider.Parser var1, long var2) {
      this.hostsFileParser = var1;
      this.refreshInterval = ObjectUtil.checkPositiveOrZero(var2, "refreshInterval");
      HostsFileEntriesProvider var4 = parseEntries(var1);
      this.inet4Entries = var4.ipv4Entries();
      this.inet6Entries = var4.ipv6Entries();
   }

   @Override
   public InetAddress address(String var1, ResolvedAddressTypes var2) {
      return firstAddress(this.addresses(var1, var2));
   }

   public List<InetAddress> addresses(String var1, ResolvedAddressTypes var2) {
      String var3 = this.normalize(var1);
      this.ensureHostsFileEntriesAreFresh();
      switch (var2) {
         case IPV4_ONLY:
            return this.inet4Entries.get(var3);
         case IPV6_ONLY:
            return this.inet6Entries.get(var3);
         case IPV4_PREFERRED:
            List var4 = this.inet4Entries.get(var3);
            return var4 != null ? allAddresses(var4, this.inet6Entries.get(var3)) : this.inet6Entries.get(var3);
         case IPV6_PREFERRED:
            List var5 = this.inet6Entries.get(var3);
            return var5 != null ? allAddresses(var5, this.inet4Entries.get(var3)) : this.inet4Entries.get(var3);
         default:
            throw new IllegalArgumentException("Unknown ResolvedAddressTypes " + var2);
      }
   }

   private void ensureHostsFileEntriesAreFresh() {
      long var1 = this.refreshInterval;
      if (var1 != 0L) {
         long var3 = this.lastRefresh.get();
         long var5 = System.nanoTime();
         if (var5 - var3 > var1 && this.lastRefresh.compareAndSet(var3, var5)) {
            HostsFileEntriesProvider var7 = parseEntries(this.hostsFileParser);
            this.inet4Entries = var7.ipv4Entries();
            this.inet6Entries = var7.ipv6Entries();
         }
      }
   }

   String normalize(String var1) {
      return var1.toLowerCase(Locale.ENGLISH);
   }

   private static List<InetAddress> allAddresses(List<InetAddress> var0, List<InetAddress> var1) {
      ArrayList var2 = new ArrayList(var0.size() + (var1 == null ? 0 : var1.size()));
      var2.addAll(var0);
      if (var1 != null) {
         var2.addAll(var1);
      }

      return var2;
   }

   private static InetAddress firstAddress(List<InetAddress> var0) {
      return var0 != null && !var0.isEmpty() ? (InetAddress)var0.get(0) : null;
   }

   private static HostsFileEntriesProvider parseEntries(HostsFileEntriesProvider.Parser var0) {
      return PlatformDependent.isWindows() ? var0.parseSilently(Charset.defaultCharset(), CharsetUtil.UTF_16, CharsetUtil.UTF_8) : var0.parseSilently();
   }

   static {
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.hostsFileRefreshInterval: {}", DEFAULT_REFRESH_INTERVAL);
      }
   }
}
