package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf> {
   private final Charset charset;

   public StringDecoder() {
      this(Charset.defaultCharset());
   }

   public StringDecoder(Charset var1) {
      this.charset = ObjectUtil.checkNotNull(var1, "charset");
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      var3.add(var2.toString(this.charset));
   }
}
