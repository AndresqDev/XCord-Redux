package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MinecraftEncoder extends MessageToByteEncoder<DefinedPacket> {
   private Protocol protocol;
   private boolean server;
   private int protocolVersion;

   protected void encode(ChannelHandlerContext var1, DefinedPacket var2, ByteBuf var3) throws Exception {
      Protocol.DirectionData var4 = this.server ? this.protocol.TO_CLIENT : this.protocol.TO_SERVER;
      DefinedPacket.writeVarInt(var4.getId((Class<? extends DefinedPacket>)var2.getClass(), this.protocolVersion), var3);
      var2.write0(var3, var4.getDirection(), this.protocolVersion);
   }

   public MinecraftEncoder(Protocol var1, boolean var2, int var3) {
      this.protocol = var1;
      this.server = var2;
      this.protocolVersion = var3;
   }

   public void setProtocol(Protocol var1) {
      this.protocol = var1;
   }

   public void setProtocolVersion(int var1) {
      this.protocolVersion = var1;
   }
}
