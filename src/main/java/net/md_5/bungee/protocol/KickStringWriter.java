package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class KickStringWriter extends MessageToByteEncoder<String> {
   protected void encode(ChannelHandlerContext var1, String var2, ByteBuf var3) throws Exception {
      var3.writeByte(255);
      var3.writeShort(var2.length());

      for (char var7 : var2.toCharArray()) {
         var3.writeChar(var7);
      }
   }
}
