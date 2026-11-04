package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.DatagramDnsQuery;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.concurrent.Promise;
import java.net.InetSocketAddress;

final class DatagramDnsQueryContext extends DnsQueryContext {
   DatagramDnsQueryContext(
      DnsNameResolver var1, InetSocketAddress var2, DnsQuestion var3, DnsRecord[] var4, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> var5
   ) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   protected DnsQuery newQuery(int var1) {
      return new DatagramDnsQuery(null, this.nameServerAddr(), var1);
   }

   @Override
   protected Channel channel() {
      return this.parent().ch;
   }

   @Override
   protected String protocol() {
      return "UDP";
   }
}
