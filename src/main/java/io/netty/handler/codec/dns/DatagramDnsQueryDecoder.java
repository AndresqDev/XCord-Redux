package io.netty.handler.codec.dns;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsQueryDecoder extends MessageToMessageDecoder<DatagramPacket> {
   private final DnsRecordDecoder recordDecoder;

   public DatagramDnsQueryDecoder() {
      this(DnsRecordDecoder.DEFAULT);
   }

   public DatagramDnsQueryDecoder(DnsRecordDecoder var1) {
      this.recordDecoder = ObjectUtil.checkNotNull(var1, "recordDecoder");
   }

   protected void decode(ChannelHandlerContext var1, final DatagramPacket var2, List<Object> var3) throws Exception {
      DnsQuery var4 = DnsMessageUtil.decodeDnsQuery(this.recordDecoder, var2.content(), new DnsMessageUtil.DnsQueryFactory() {
         @Override
         public DnsQuery newQuery(int var1, DnsOpCode var2x) {
            return new DatagramDnsQuery(var2.sender(), var2.recipient(), var1, var2x);
         }
      });
      var3.add(var4);
   }
}
