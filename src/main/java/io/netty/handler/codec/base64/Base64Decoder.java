package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Decoder extends MessageToMessageDecoder<ByteBuf> {
   private final Base64Dialect dialect;

   public Base64Decoder() {
      this(Base64Dialect.STANDARD);
   }

   public Base64Decoder(Base64Dialect var1) {
      this.dialect = ObjectUtil.checkNotNull(var1, "dialect");
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(Base64.decode(var2, var2.readerIndex(), var2.readableBytes(), this.dialect));
   }
}
