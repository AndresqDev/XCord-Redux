package net.md_5.bungee.entitymap;

import io.netty.buffer.ByteBuf;

public class EntityMap_Dummy extends EntityMap {
   public static final EntityMap_Dummy INSTANCE = new EntityMap_Dummy();

   EntityMap_Dummy() {
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3) {
   }

   @Override
   public void rewriteServerbound(ByteBuf var1, int var2, int var3, int var4) {
   }

   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3) {
   }

   @Override
   public void rewriteClientbound(ByteBuf var1, int var2, int var3, int var4) {
   }
}
