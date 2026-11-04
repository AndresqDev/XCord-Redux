package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import java.net.UnknownHostException;
import java.util.List;

final class DnsRecordResolveContext extends DnsResolveContext<DnsRecord> {
   DnsRecordResolveContext(DnsNameResolver var1, Promise<?> var2, DnsQuestion var3, DnsRecord[] var4, DnsServerAddressStream var5, int var6) {
      this(var1, var2, var3.name(), var3.dnsClass(), new DnsRecordType[]{var3.type()}, var4, var5, var6);
   }

   private DnsRecordResolveContext(
      DnsNameResolver var1, Promise<?> var2, String var3, int var4, DnsRecordType[] var5, DnsRecord[] var6, DnsServerAddressStream var7, int var8
   ) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   DnsResolveContext<DnsRecord> newResolverContext(
      DnsNameResolver var1, Promise<?> var2, String var3, int var4, DnsRecordType[] var5, DnsRecord[] var6, DnsServerAddressStream var7, int var8
   ) {
      return new DnsRecordResolveContext(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   DnsRecord convertRecord(DnsRecord var1, String var2, DnsRecord[] var3, EventLoop var4) {
      return ReferenceCountUtil.retain(var1);
   }

   @Override
   List<DnsRecord> filterResults(List<DnsRecord> var1) {
      return var1;
   }

   boolean isCompleteEarly(DnsRecord var1) {
      return false;
   }

   @Override
   boolean isDuplicateAllowed() {
      return true;
   }

   void cache(String var1, DnsRecord[] var2, DnsRecord var3, DnsRecord var4) {
   }

   @Override
   void cache(String var1, DnsRecord[] var2, UnknownHostException var3) {
   }

   @Override
   DnsCnameCache cnameCache() {
      return NoopDnsCnameCache.INSTANCE;
   }
}
