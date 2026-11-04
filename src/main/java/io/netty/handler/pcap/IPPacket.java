package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class IPPacket {
   private static final byte MAX_TTL = -1;
   private static final short V4_HEADER_SIZE = 20;
   private static final byte TCP = 6;
   private static final byte UDP = 17;
   private static final int IPV6_VERSION_TRAFFIC_FLOW = 60000000;

   private IPPacket() {
   }

   static void writeUDPv4(ByteBuf var0, ByteBuf var1, int var2, int var3) {
      writePacketv4(var0, var1, 17, var2, var3);
   }

   static void writeUDPv6(ByteBuf var0, ByteBuf var1, byte[] var2, byte[] var3) {
      writePacketv6(var0, var1, 17, var2, var3);
   }

   static void writeTCPv4(ByteBuf var0, ByteBuf var1, int var2, int var3) {
      writePacketv4(var0, var1, 6, var2, var3);
   }

   static void writeTCPv6(ByteBuf var0, ByteBuf var1, byte[] var2, byte[] var3) {
      writePacketv6(var0, var1, 6, var2, var3);
   }

   private static void writePacketv4(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      var0.writeByte(69);
      var0.writeByte(0);
      var0.writeShort(20 + var1.readableBytes());
      var0.writeShort(0);
      var0.writeShort(0);
      var0.writeByte(-1);
      var0.writeByte(var2);
      var0.writeShort(0);
      var0.writeInt(var3);
      var0.writeInt(var4);
      var0.writeBytes(var1);
   }

   private static void writePacketv6(ByteBuf var0, ByteBuf var1, int var2, byte[] var3, byte[] var4) {
      var0.writeInt(60000000);
      var0.writeShort(var1.readableBytes());
      var0.writeByte(var2 & 0xFF);
      var0.writeByte(-1);
      var0.writeBytes(var3);
      var0.writeBytes(var4);
      var0.writeBytes(var1);
   }
}
