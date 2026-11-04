package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.internal.ObjectUtil;

public final class TcpDnsQueryDecoder extends LengthFieldBasedFrameDecoder {
   private final DnsRecordDecoder decoder;

   public TcpDnsQueryDecoder() {
      this(DnsRecordDecoder.DEFAULT, 65535);
   }

   public TcpDnsQueryDecoder(DnsRecordDecoder var1, int var2) {
      super(var2, 0, 2, 0, 2);
      this.decoder = ObjectUtil.checkNotNull(var1, "decoder");
   }

   @Override
   protected Object decode(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      ByteBuf var3 = (ByteBuf)super.decode(var1, var2);
      return var3 == null ? null : DnsMessageUtil.decodeDnsQuery(this.decoder, var3.slice(), new DnsMessageUtil.DnsQueryFactory() {
         @Override
         public DnsQuery newQuery(int var1, DnsOpCode var2) {
            return new DefaultDnsQuery(var1, var2);
         }
      });
   }
}
