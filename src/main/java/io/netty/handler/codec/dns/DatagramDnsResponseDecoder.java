package io.netty.handler.codec.dns;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.net.InetSocketAddress;
import java.util.List;

@ChannelHandler.Sharable
public class DatagramDnsResponseDecoder extends MessageToMessageDecoder<DatagramPacket> {
   private final DnsResponseDecoder<InetSocketAddress> responseDecoder;

   public DatagramDnsResponseDecoder() {
      this(DnsRecordDecoder.DEFAULT);
   }

   public DatagramDnsResponseDecoder(DnsRecordDecoder var1) {
      this.responseDecoder = new DnsResponseDecoder<InetSocketAddress>(var1) {
         protected DnsResponse newResponse(InetSocketAddress var1, InetSocketAddress var2, int var3, DnsOpCode var4, DnsResponseCode var5) {
            return new DatagramDnsResponse(var1, var2, var3, var4, var5);
         }
      };
   }

   protected void decode(ChannelHandlerContext var1, DatagramPacket var2, List<Object> var3) throws Exception {
      try {
         var3.add(this.decodeResponse(var1, var2));
      } catch (IndexOutOfBoundsException var5) {
         throw new CorruptedFrameException("Unable to decode response", var5);
      }
   }

   protected DnsResponse decodeResponse(ChannelHandlerContext var1, DatagramPacket var2) throws Exception {
      return this.responseDecoder.decode(var2.sender(), var2.recipient(), var2.content());
   }
}
