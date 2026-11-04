package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class PcapHeaders {
   private static final byte[] GLOBAL_HEADER = new byte[]{-95, -78, -61, -44, 0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 1};

   private PcapHeaders() {
   }

   public static void writeGlobalHeader(ByteBuf var0) {
      var0.writeBytes(GLOBAL_HEADER);
   }

   static void writePacketHeader(ByteBuf var0, int var1, int var2, int var3, int var4) {
      var0.writeInt(var1);
      var0.writeInt(var2);
      var0.writeInt(var3);
      var0.writeInt(var4);
   }
}
