package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public final class TcpDnsQueryEncoder extends MessageToByteEncoder<DnsQuery> {
   private final DnsQueryEncoder encoder;

   public TcpDnsQueryEncoder() {
      this(DnsRecordEncoder.DEFAULT);
   }

   public TcpDnsQueryEncoder(DnsRecordEncoder var1) {
      this.encoder = new DnsQueryEncoder(var1);
   }

   protected void encode(ChannelHandlerContext var1, DnsQuery var2, ByteBuf var3) throws Exception {
      var3.writerIndex(var3.writerIndex() + 2);
      this.encoder.encode(var2, var3);
      var3.setShort(0, var3.readableBytes() - 2);
   }

   protected ByteBuf allocateBuffer(ChannelHandlerContext var1, DnsQuery var2, boolean var3) {
      return var3 ? var1.alloc().ioBuffer(1024) : var1.alloc().heapBuffer(1024);
   }
}
