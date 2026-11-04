package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Encoder extends MessageToMessageEncoder<ByteBuf> {
   private final boolean breakLines;
   private final Base64Dialect dialect;

   public Base64Encoder() {
      this(true);
   }

   public Base64Encoder(boolean var1) {
      this(var1, Base64Dialect.STANDARD);
   }

   public Base64Encoder(boolean var1, Base64Dialect var2) {
      this.dialect = ObjectUtil.checkNotNull(var2, "dialect");
      this.breakLines = var1;
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(Base64.encode(var2, var2.readerIndex(), var2.readableBytes(), this.breakLines, this.dialect));
   }
}
