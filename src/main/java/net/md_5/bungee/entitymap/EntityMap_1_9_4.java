package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

class EntityMap_1_9_4 extends EntityMap {
   static final EntityMap_1_9_4 INSTANCE = new EntityMap_1_9_4();

   EntityMap_1_9_4() {
      this.addRewrite(0, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(1, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(3, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(4, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(5, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(6, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(8, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(27, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(37, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(38, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(39, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(40, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(47, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(49, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(52, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(54, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(57, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(58, ProtocolConstants.Direction.TO_CLIENT, false);
      this.addRewrite(59, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(60, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(64, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(72, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(73, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(74, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(75, ProtocolConstants.Direction.TO_CLIENT, true);
      this.addRewrite(10, ProtocolConstants.Direction.TO_SERVER, true);
      this.addRewrite(20, ProtocolConstants.Direction.TO_SERVER, true);
   }

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
      super.rewriteClientbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      int var7 = var1.readerIndex();
      switch (var5) {
         case 0:
            DefinedPacket.readVarInt(var1);
            DefinedPacket.readUUID(var1);
            short var17 = var1.readUnsignedByte();
            if (var17 == 60 || var17 == 90 || var17 == 91) {
               if (var17 == 60 || var17 == 91) {
                  var2++;
                  var3++;
               }

               var1.skipBytes(26);
               int var19 = var1.readerIndex();
               int var21 = var1.readInt();
               if (var21 == var2) {
                  var1.setInt(var19, var3);
               } else if (var21 == var3) {
                  var1.setInt(var19, var2);
               }
            }
            break;
         case 5:
            DefinedPacket.readVarInt(var1);
            int var18 = var1.readerIndex() - var4 - var6;
            UUID var20 = DefinedPacket.readUUID(var1);
            UserConnection var22;
            if ((var22 = BungeeCord.getInstance().getPlayerByOfflineUUID(var20)) != null) {
               int var23 = var1.writerIndex();
               var1.readerIndex(var4);
               var1.writerIndex(var4 + var6 + var18);
               DefinedPacket.writeUUID(var22.getUniqueId(), var1);
               var1.writerIndex(var23);
            }
            break;
         case 44:
            short var14 = var1.readUnsignedByte();
            if (var14 == 1) {
               DefinedPacket.readVarInt(var1);
               rewriteInt(var1, var2, var3, var1.readerIndex());
            } else if (var14 == 2) {
               int var15 = var1.readerIndex();
               rewriteVarInt(var1, var2, var3, var1.readerIndex());
               var1.readerIndex(var15);
               DefinedPacket.readVarInt(var1);
               rewriteInt(var1, var2, var3, var1.readerIndex());
            }
            break;
         case 57:
            DefinedPacket.readVarInt(var1);
            rewriteMetaVarInt(var1, var2 + 1, var3 + 1, 5);
            rewriteMetaVarInt(var1, var2, var3, 12);
            break;
         case 58:
            rewriteInt(var1, var2, var3, var4 + var6 + 4);
            break;
         case 64:
            DefinedPacket.readVarInt(var1);
            var7 = var1.readerIndex();
         case 48:
            int var8 = DefinedPacket.readVarInt(var1);
            int[] var9 = new int[var8];

            for (int var10 = 0; var10 < var8; var10++) {
               var9[var10] = DefinedPacket.readVarInt(var1);
            }

            var1.readerIndex(var7);
            var1.writerIndex(var7);
            DefinedPacket.writeVarInt(var8, var1);

            for (int var13 : var9) {
               if (var13 == var2) {
                  var13 = var3;
               } else if (var13 == var3) {
                  var13 = var2;
               }

               DefinedPacket.writeVarInt(var13, var1);
            }
            break;
         case 72:
            DefinedPacket.readVarInt(var1);
            rewriteVarInt(var1, var2, var3, var1.readerIndex());
      }

      var1.readerIndex(var4);
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
      super.rewriteServerbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 27 && !BungeeCord.getInstance().getConfig().isIpForward()) {
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
