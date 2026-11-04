package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.DefaultDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.concurrent.Promise;
import java.net.InetSocketAddress;

final class TcpDnsQueryContext extends DnsQueryContext {
   private final Channel channel;

   TcpDnsQueryContext(
      DnsNameResolver var1,
      Channel var2,
      InetSocketAddress var3,
      DnsQuestion var4,
      DnsRecord[] var5,
      Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> var6
   ) {
      super(var1, var3, var4, var5, var6);
      this.channel = var2;
   }

   @Override
   protected DnsQuery newQuery(int var1) {
      return new DefaultDnsQuery(var1);
   }

   @Override
   protected Channel channel() {
      return this.channel;
   }

   @Override
   protected String protocol() {
      return "TCP";
   }
}
