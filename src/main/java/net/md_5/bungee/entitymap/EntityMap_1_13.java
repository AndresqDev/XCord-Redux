package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_13 extends EntityMap {
   static final EntityMap_1_13 INSTANCE = new EntityMap_1_13();

   EntityMap_1_13() {
      this.addRewrite(0, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(1, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(3, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(4, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(5, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(6, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(8, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(28, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(39, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(40, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(41, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(42, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(51, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(54, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(57, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(60, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(63, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(64, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(65, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(66, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(70, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(79, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(80, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(82, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(83, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(13, ProtocolConstants.Direction.TO_SERVER, true);
      this.addRewrite(25, ProtocolConstants.Direction.TO_SERVER, true);
   }

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3, int var4) {
      super.rewriteClientbound(var1, var2, var3);
      int var5 = var1.readerIndex();
      int var6 = DefinedPacket.readVarInt(var1);
      int var7 = var1.readerIndex() - var5;
      int var8 = var1.readerIndex();
      switch (var6) {
         case 0:
            DefinedPacket.readVarInt(var1);
            DefinedPacket.readUUID(var1);
            short var18 = var1.readUnsignedByte();
            if (var18 == 60 || var18 == 90 || var18 == 91) {
               if (var18 == 60 || var18 == 91) {
                  var2++;
                  var3++;
               }

               var1.skipBytes(26);
               int var20 = var1.readerIndex();
               int var22 = var1.readInt();
               if (var22 == var2) {
                  var1.setInt(var20, var3);
               } else if (var22 == var3) {
                  var1.setInt(var20, var2);
               }
            }
            break;
         case 5:
            DefinedPacket.readVarInt(var1);
            int var19 = var1.readerIndex() - var5 - var7;
            UUID var21 = DefinedPacket.readUUID(var1);
            UserConnection var23;
            if ((var23 = BungeeCord.getInstance().getPlayerByOfflineUUID(var21)) != null) {
               int var24 = var1.writerIndex();
               var1.readerIndex(var5);
               var1.writerIndex(var5 + var7 + var19);
               DefinedPacket.writeUUID(var23.getUniqueId(), var1);
               var1.writerIndex(var24);
            }
            break;
         case 47:
            short var15 = var1.readUnsignedByte();
            if (var15 == 1) {
               DefinedPacket.readVarInt(var1);
               rewriteInt(var1, var2, var3, var1.readerIndex());
            } else if (var15 == 2) {
               int var16 = var1.readerIndex();
               rewriteVarInt(var1, var2, var3, var1.readerIndex());
               var1.readerIndex(var16);
               DefinedPacket.readVarInt(var1);
               rewriteInt(var1, var2, var3, var1.readerIndex());
            }
            break;
         case 63:
            DefinedPacket.readVarInt(var1);
            rewriteMetaVarInt(var1, var2 + 1, var3 + 1, 6, var4);
            rewriteMetaVarInt(var1, var2, var3, 7, var4);
            rewriteMetaVarInt(var1, var2, var3, 13, var4);
            break;
         case 64:
            rewriteInt(var1, var2, var3, var5 + var7 + 4);
            break;
         case 70:
            DefinedPacket.readVarInt(var1);
            var8 = var1.readerIndex();
         case 53:
            int var9 = DefinedPacket.readVarInt(var1);
            int[] var10 = new int[var9];

            for (int var11 = 0; var11 < var9; var11++) {
               var10[var11] = DefinedPacket.readVarInt(var1);
            }

            var1.readerIndex(var8);
            var1.writerIndex(var8);
            DefinedPacket.writeVarInt(var9, var1);

            for (int var14 : var10) {
               if (var14 == var2) {
                  var14 = var3;
               } else if (var14 == var3) {
                  var14 = var2;
               }

               DefinedPacket.writeVarInt(var14, var1);
            }
            break;
         case 79:
            DefinedPacket.readVarInt(var1);
            rewriteVarInt(var1, var2, var3, var1.readerIndex());
      }

      var1.readerIndex(var5);
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
      super.rewriteServerbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 40 && !BungeeCord.getInstance().getConfig().isIpForward()) {
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
