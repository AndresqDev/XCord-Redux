package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class EthernetPacket {
   private static final byte[] DUMMY_SOURCE_MAC_ADDRESS = new byte[]{0, 0, 94, 0, 83, 0};
   private static final byte[] DUMMY_DESTINATION_MAC_ADDRESS = new byte[]{0, 0, 94, 0, 83, -1};
   private static final int V4 = 2048;
   private static final int V6 = 34525;

   private EthernetPacket() {
   }

   static void writeIPv4(ByteBuf var0, ByteBuf var1) {
      writePacket(var0, var1, DUMMY_SOURCE_MAC_ADDRESS, DUMMY_DESTINATION_MAC_ADDRESS, 2048);
   }

   static void writeIPv6(ByteBuf var0, ByteBuf var1) {
      writePacket(var0, var1, DUMMY_SOURCE_MAC_ADDRESS, DUMMY_DESTINATION_MAC_ADDRESS, 34525);
   }

   private static void writePacket(ByteBuf var0, ByteBuf var1, byte[] var2, byte[] var3, int var4) {
      var0.writeBytes(var3);
      var0.writeBytes(var2);
      var0.writeShort(var4);
      var0.writeBytes(var1);
   }
}
