package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

class EntityMap_1_16_2 extends EntityMap {
   static final EntityMap_1_16_2 INSTANCE_1_16_2 = new EntityMap_1_16_2(4, 45);
   static final EntityMap_1_16_2 INSTANCE_1_17 = new EntityMap_1_16_2(4, 45);
   static final EntityMap_1_16_2 INSTANCE_1_18 = new EntityMap_1_16_2(4, 45);
   static final EntityMap_1_16_2 INSTANCE_1_19 = new EntityMap_1_16_2(2, 47);
   static final EntityMap_1_16_2 INSTANCE_1_19_1 = new EntityMap_1_16_2(2, 48);
   private final int spawnPlayerId;
   private final int spectateId;

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3, int var4) {
      int var5 = var1.readerIndex();
      int var6 = DefinedPacket.readVarInt(var1);
      int var7 = var1.readerIndex() - var5;
      if (var6 == this.spawnPlayerId) {
         DefinedPacket.readVarInt(var1);
         int var8 = var1.readerIndex() - var5 - var7;
         UUID var9 = DefinedPacket.readUUID(var1);
         UserConnection var10;
         if ((var10 = BungeeCord.getInstance().getPlayerByOfflineUUID(var9)) != null) {
            int var11 = var1.writerIndex();
            var1.readerIndex(var5);
            var1.writerIndex(var5 + var7 + var8);
            DefinedPacket.writeUUID(var10.getUniqueId(), var1);
            var1.writerIndex(var11);
         }
      }

      var1.readerIndex(var5);
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == this.spectateId) {
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

   private EntityMap_1_16_2(int var1, int var2) {
      this.spawnPlayerId = var1;
      this.spectateId = var2;
   }
}
