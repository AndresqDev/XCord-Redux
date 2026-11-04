package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsQuery, InetSocketAddress>> {
   private final DnsQueryEncoder encoder;

   public DatagramDnsQueryEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public DatagramDnsQueryEncoder(DnsRecordEncoder var1) {
      this.encoder = new DnsQueryEncoder(var1);
   }

   protected void encode(ChannelHandlerContext var1, AddressedEnvelope<DnsQuery, InetSocketAddress> var2, List<Object> var3) throws Exception {
      InetSocketAddress var4 = (InetSocketAddress)var2.recipient();
      DnsQuery var5 = (DnsQuery)var2.content();
      ByteBuf var6 = this.allocateBuffer(var1, var2);
      boolean var7 = false;

      try {
         this.encoder.encode(var5, var6);
         var7 = true;
      } finally {
         if (!var7) {
            var6.release();
         }
      }

      var3.add(new DatagramPacket(var6, var4, null));
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, AddressedEnvelope<DnsQuery, InetSocketAddress> var2) throws Exception {
      return var1.alloc().ioBuffer(1024);
   }
}
