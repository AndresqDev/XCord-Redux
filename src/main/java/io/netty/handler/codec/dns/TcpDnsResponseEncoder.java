package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public final class TcpDnsResponseEncoder extends MessageToMessageEncoder<DnsResponse> {
   private final DnsRecordEncoder encoder;

   public TcpDnsResponseEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public TcpDnsResponseEncoder(DnsRecordEncoder var1) {
      this.encoder = ObjectUtil.checkNotNull(var1, "encoder");
   }

   protected void encode(ChannelHandlerContext var1, DnsResponse var2, List<Object> var3) throws Exception {
      ByteBuf var4 = var1.alloc().ioBuffer(1024);
      var4.writerIndex(var4.writerIndex() + 2);
      DnsMessageUtil.encodeDnsResponse(this.encoder, var2, var4);
      var4.setShort(0, var4.readableBytes() - 2);
      var3.add(var4);
   }
}
