package net.md_5.bungee.entitymap;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_7_2 extends EntityMap {
   static final EntityMap INSTANCE = new EntityMap_1_7_2();

   EntityMap_1_7_2() {
      this.addRewrite(4, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(10, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(11, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(12, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(13, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(14, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(15, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(16, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(17, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(18, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(20, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(21, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(22, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(23, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(24, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(25, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(26, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(27, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(28, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(29, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(30, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(32, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(37, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(44, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(2, ProtocolConstants.Direction.TO_SERVER, false);
      this.addRewrite(10, ProtocolConstants.Direction.TO_SERVER, false);
      this.addRewrite(11, ProtocolConstants.Direction.TO_SERVER, false);
   }

   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
      super.rewriteClientbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 13 || var5 == 27) {
         rewriteInt(var1, var2, var3, var4 + var6 + 4);
      } else if (var5 == 19) {
         byte var7 = var1.getByte(var6);

         for (int var8 = 0; var8 < var7; var8++) {
            rewriteInt(var1, var2, var3, var6 + 1 + var8 * 4);
         }
      } else if (var5 == 14) {
         DefinedPacket.readVarInt(var1);
         short var11 = var1.readUnsignedByte();
         if (var11 == 60 || var11 == 90) {
            var1.skipBytes(14);
            int var12 = var1.readerIndex();
            int var9 = var1.readInt();
            int var10 = -1;
            if (var9 == var2) {
               var1.setInt(var12, var3);
               var10 = var3;
            } else if (var9 == var3) {
               var1.setInt(var12, var2);
               var10 = var2;
            }

            if (var10 != -1) {
               if (var10 == 0 && var9 != 0) {
                  var1.readerIndex(var4);
                  var1.writerIndex(var1.readableBytes() - 6);
               } else if (var10 != 0 && var9 == 0) {
                  var1.readerIndex(var4);
                  var1.capacity(var1.readableBytes() + 6);
                  var1.writerIndex(var1.readableBytes() + 6);
               }
            }
         }
      }

      var1.readerIndex(var4);
   }
}
