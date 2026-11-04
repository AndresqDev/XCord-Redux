package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsResponse, InetSocketAddress>> {
   private final DnsRecordEncoder recordEncoder;

   public DatagramDnsResponseEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public DatagramDnsResponseEncoder(DnsRecordEncoder var1) {
      this.recordEncoder = ObjectUtil.checkNotNull(var1, "recordEncoder");
   }

   protected void encode(ChannelHandlerContext var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2, List<Object> var3) throws Exception {
      InetSocketAddress var4 = (InetSocketAddress)var2.recipient();
      DnsResponse var5 = (DnsResponse)var2.content();
      ByteBuf var6 = this.allocateBuffer(var1, var2);
      DnsMessageUtil.encodeDnsResponse(this.recordEncoder, var5, var6);
      var3.add(new DatagramPacket(var6, var4, null));
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, AddressedEnvelope<DnsResponse, InetSocketAddress> var2) throws Exception {
      return var1.alloc().ioBuffer(1024);
   }
}
