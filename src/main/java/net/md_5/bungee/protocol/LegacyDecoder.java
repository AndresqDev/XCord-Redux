package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;

public class LegacyDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.isReadable()) {
         var2.markReaderIndex();
         short var4 = var2.readUnsignedByte();
         if (var4 != 254) {
            if (var4 == 2 && var2.isReadable()) {
               var2.skipBytes(var2.readableBytes());
               var3.add(new PacketWrapper(new LegacyHandshake(), Unpooled.EMPTY_BUFFER));
            } else {
               var2.resetReaderIndex();
               var1.pipeline().remove(this);
            }
         } else {
            var3.add(new PacketWrapper(new LegacyPing(var2.isReadable() && var2.readUnsignedByte() == 1), Unpooled.EMPTY_BUFFER));
         }
      }
   }
}
