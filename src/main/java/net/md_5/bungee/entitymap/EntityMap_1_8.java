package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_8 extends EntityMap {
   static final EntityMap_1_8 INSTANCE = new EntityMap_1_8();

   EntityMap_1_8() {
      this.addRewrite(4, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(10, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(11, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(12, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(13, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(14, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(15, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(16, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(17, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(18, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(20, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(21, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(22, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(23, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(24, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(25, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(26, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(27, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(28, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(29, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(30, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(32, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(37, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(44, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(67, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(73, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(2, ProtocolConstants.Direction.TO_SERVER, true);
      this.addRewrite(11, ProtocolConstants.Direction.TO_SERVER, true);
   }

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
      super.rewriteClientbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 13) {
         DefinedPacket.readVarInt(var1);
         rewriteVarInt(var1, var2, var3, var1.readerIndex());
      } else if (var5 == 27) {
         rewriteInt(var1, var2, var3, var4 + var6 + 4);
      } else if (var5 == 19) {
         int var7 = DefinedPacket.readVarInt(var1);
         int[] var8 = new int[var7];

         for (int var9 = 0; var9 < var7; var9++) {
            var8[var9] = DefinedPacket.readVarInt(var1);
         }

         var1.readerIndex(var4 + var6);
         var1.writerIndex(var4 + var6);
         DefinedPacket.writeVarInt(var7, var1);

         for (int var12 : var8) {
            if (var12 == var2) {
               var12 = var3;
            } else if (var12 == var3) {
               var12 = var2;
            }

            DefinedPacket.writeVarInt(var12, var1);
         }
      } else if (var5 == 14) {
         DefinedPacket.readVarInt(var1);
         short var13 = var1.readUnsignedByte();
         if (var13 == 60 || var13 == 90) {
            var1.skipBytes(14);
            int var16 = var1.readerIndex();
            int var20 = var1.readInt();
            int var22 = var20;
            if (var20 == var2) {
               var22 = var3;
               var1.setInt(var16, var3);
            } else if (var20 == var3) {
               var22 = var2;
               var1.setInt(var16, var2);
            }

            if (var20 > 0 && var22 <= 0) {
               var1.writerIndex(var1.writerIndex() - 6);
            } else if (var22 > 0 && var20 <= 0) {
               var1.ensureWritable(6);
               var1.writerIndex(var1.writerIndex() + 6);
            }
         }
      } else if (var5 == 12) {
         DefinedPacket.readVarInt(var1);
         int var14 = var1.readerIndex() - var4 - var6;
         UUID var17 = DefinedPacket.readUUID(var1);
         UserConnection var21;
         if ((var21 = BungeeCord.getInstance().getPlayerByOfflineUUID(var17)) != null) {
            int var23 = var1.writerIndex();
            var1.readerIndex(var4);
            var1.writerIndex(var4 + var6 + var14);
            DefinedPacket.writeUUID(var21.getUniqueId(), var1);
            var1.writerIndex(var23);
         }
      } else if (var5 == 66) {
         short var15 = var1.readUnsignedByte();
         if (var15 == 1) {
            DefinedPacket.readVarInt(var1);
            rewriteInt(var1, var2, var3, var1.readerIndex());
         } else if (var15 == 2) {
            int var18 = var1.readerIndex();
            rewriteVarInt(var1, var2, var3, var1.readerIndex());
            var1.readerIndex(var18);
            DefinedPacket.readVarInt(var1);
            rewriteInt(var1, var2, var3, var1.readerIndex());
         }
      }

      var1.readerIndex(var4);
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
      super.rewriteServerbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 24 && !BungeeCord.getInstance().getConfig().isIpForward()) {
         UUID var7 = DefinedPacket.readUUID(var1);
         ProxiedPlayer var8;
         if ((var8 = BungeeCord.getInstance().getPlayer(var7)) != null) {
            int var9 = var1.writerIndex();
            var1.readerIndex(var4);
            var1.writerIndex(var4 + var6);
            DefinedPacket.writeUUID(((UserConnection)var8).getPendingConnection().getOfflineId(), var1);
            var1.writerIndex(var9);
         }
      }

      var1.readerIndex(var4);
   }
}
