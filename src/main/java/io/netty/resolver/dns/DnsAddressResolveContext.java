package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.concurrent.Promise;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

final class DnsAddressResolveContext extends DnsResolveContext<InetAddress> {
   private final DnsCache resolveCache;
   private final AuthoritativeDnsServerCache authoritativeDnsServerCache;
   private final boolean completeEarlyIfPossible;

   DnsAddressResolveContext(
      DnsNameResolver var1,
      Promise<?> var2,
      String var3,
      DnsRecord[] var4,
      DnsServerAddressStream var5,
      int var6,
      DnsCache var7,
      AuthoritativeDnsServerCache var8,
      boolean var9
   ) {
      super(var1, var2, var3, 1, var1.resolveRecordTypes(), var4, var5, var6);
      this.resolveCache = var7;
      this.authoritativeDnsServerCache = var8;
      this.completeEarlyIfPossible = var9;
   }

   @Override
   DnsResolveContext<InetAddress> newResolverContext(
      DnsNameResolver var1, Promise<?> var2, String var3, int var4, DnsRecordType[] var5, DnsRecord[] var6, DnsServerAddressStream var7, int var8
   ) {
      return new DnsAddressResolveContext(var1, var2, var3, var6, var7, var8, this.resolveCache, this.authoritativeDnsServerCache, this.completeEarlyIfPossible);
   }

   InetAddress convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4) {
      return DnsAddressDecoder.decodeAddress(var1, var2, this.parent.isDecodeIdn());
   }

   @Override
   List<InetAddress> filterResults(List<InetAddress> var1) {
      Collections.sort(var1, PreferredAddressTypeComparator.comparator(this.parent.preferredAddressType()));
      return var1;
   }

   boolean isCompleteEarly(InetAddress var1) {
      return this.completeEarlyIfPossible && this.parent.preferredAddressType().addressType() == var1.getClass();
   }

   @Override
   boolean isDuplicateAllowed() {
      return false;
   }

   void cache(String var1, DnsRecord[] var2, DnsRecord var3, InetAddress var4) {
      this.resolveCache.cache(var1, var2, var4, var3.timeToLive(), this.parent.ch.eventLoop());
   }

   @Override
   void cache(String var1, DnsRecord[] var2, UnknownHostException var3) {
      this.resolveCache.cache(var1, var2, var3, this.parent.ch.eventLoop());
   }

   @Override
   void doSearchDomainQuery(String var1, Promise<List<InetAddress>> var2) {
      if (!DnsNameResolver.doResolveAllCached(var1, this.additionals, var2, this.resolveCache, this.parent.resolvedInternetProtocolFamiliesUnsafe())) {
         super.doSearchDomainQuery(var1, var2);
      }
   }

   @Override
   DnsCache resolveCache() {
      return this.resolveCache;
   }

   @Override
   AuthoritativeDnsServerCache authoritativeDnsServerCache() {
      return this.authoritativeDnsServerCache;
   }
}
