package net.md_5.bungee.entitymap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Property;

class EntityMap_1_7_6 extends EntityMap_1_7_2 {
   static final EntityMap_1_7_6 INSTANCE = new EntityMap_1_7_6();

   @SuppressFBWarnings({"DLS_DEAD_LOCAL_STORE"})
   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
      super.rewriteClientbound(var1, var2, var3);
      int var4 = var1.readerIndex();
      int var5 = DefinedPacket.readVarInt(var1);
      int var6 = var1.readerIndex() - var4;
      if (var5 == 12) {
         DefinedPacket.readVarInt(var1);
         int var7 = var1.readerIndex() - var4 - var6;
         String var8 = DefinedPacket.readString(var1);
         String var9 = DefinedPacket.readString(var1);
         int var10 = DefinedPacket.readVarInt(var1);
         if (var10 == 0) {
            UserConnection var11 = (UserConnection)BungeeCord.getInstance().getPlayer(var9);
            if (var11 != null) {
               LoginResult var12 = var11.getPendingConnection().getLoginProfile();
               if (var12 != null && var12.getProperties() != null && var12.getProperties().length >= 1) {
                  ByteBuf var13 = var1.copy();
                  var1.readerIndex(var4);
                  var1.writerIndex(var4 + var6 + var7);
                  DefinedPacket.writeString(var11.getUniqueId().toString(), var1);
                  DefinedPacket.writeString(var9, var1);
                  DefinedPacket.writeVarInt(var12.getProperties().length, var1);

                  for (Property var17 : var12.getProperties()) {
                     DefinedPacket.writeString(var17.getName(), var1);
                     DefinedPacket.writeString(var17.getValue(), var1);
                     DefinedPacket.writeString(var17.getSignature(), var1);
                  }

                  var1.writeBytes(var13);
                  var13.release();
               }
            }
         }
      }

      var1.readerIndex(var4);
   }
}
