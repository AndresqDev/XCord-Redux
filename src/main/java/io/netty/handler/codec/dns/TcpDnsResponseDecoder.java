package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import java.net.SocketAddress;

public final class TcpDnsResponseDecoder extends LengthFieldBasedFrameDecoder {
   private final DnsResponseDecoder<SocketAddress> responseDecoder;

   public TcpDnsResponseDecoder() {
      this(DnsRecordDecoder.DEFAULT, 65536);
   }

   public TcpDnsResponseDecoder(DnsRecordDecoder var1, int var2) {
      super(var2, 0, 2, 0, 2);
      this.responseDecoder = new DnsResponseDecoder<SocketAddress>(var1) {
         @Override
         protected DnsResponse newResponse(SocketAddress var1, SocketAddress var2, int var3, DnsOpCode var4, DnsResponseCode var5) {
            return new DefaultDnsResponse(var3, var4, var5);
         }
      };
   }

   @Override
   protected Object decode(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      ByteBuf var3 = (ByteBuf)super.decode(var1, var2);
      if (var3 == null) {
         return null;
      } else {
         DnsResponse var4;
         try {
            var4 = this.responseDecoder.decode(var1.channel().remoteAddress(), var1.channel().localAddress(), var3.slice());
         } finally {
            var3.release();
         }

         return var4;
      }
   }

   @Override
   protected ByteBuf extractFrame(ChannelHandlerContext var1, ByteBuf var2, int var3, int var4) {
      return var2.copy(var3, var4);
   }
}
