package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class UDPPacket {
   private static final short UDP_HEADER_SIZE = 8;

   private UDPPacket() {
   }

   static void writePacket(ByteBuf var0, ByteBuf var1, int var2, int var3) {
      var0.writeShort(var2);
      var0.writeShort(var3);
      var0.writeShort(8 + var1.readableBytes());
      var0.writeShort(1);
      var0.writeBytes(var1);
   }
}
