package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;

final class TCPPacket {
   private static final short OFFSET = 20480;

   private TCPPacket() {
   }

   static void writePacket(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4, int var5, TCPPacket.TCPFlag... var6) {
      var0.writeShort(var4);
      var0.writeShort(var5);
      var0.writeInt(var2);
      var0.writeInt(var3);
      var0.writeShort(20480 | TCPPacket.TCPFlag.getFlag(var6));
      var0.writeShort(65535);
      var0.writeShort(1);
      var0.writeShort(0);
      if (var1 != null) {
         var0.writeBytes(var1);
      }
   }

   static enum TCPFlag {
      FIN(1),
      SYN(2),
      RST(4),
      PSH(8),
      ACK(16),
      URG(32),
      ECE(64),
      CWR(128);

      private final int value;

      private TCPFlag(int var3) {
         this.value = var3;
      }

      static int getFlag(TCPPacket.TCPFlag... var0) {
         int var1 = 0;

         for (TCPPacket.TCPFlag var5 : var0) {
            var1 |= var5.value;
         }

         return var1;
      }
   }
}
