package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringEncoder extends MessageToMessageEncoder<CharSequence> {
   private final Charset charset;

   public StringEncoder() {
      this(Charset.defaultCharset());
   }

   public StringEncoder(Charset var1) {
      this.charset = ObjectUtil.checkNotNull(var1, "charset");
   }

   protected void encode(ChannelHandlerContext var1, CharSequence var2, List<Object> var3) throws Exception {
      if (var2.length() != 0) {
         var3.add(ByteBufUtil.encodeString(var1.alloc(), CharBuffer.wrap(var2), this.charset));
      }
   }
}
