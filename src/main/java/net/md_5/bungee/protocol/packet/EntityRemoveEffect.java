package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;

public class EntityRemoveEffect extends MultiVersionPacketV17 {
   private int entityId;
   private int effectId;

   public void read(ByteBuf var1) {
      this.entityId = readVarInt(var1);
      this.effectId = var1.readUnsignedByte();
   }

   protected void v17Read(ByteBuf var1) {
      this.entityId = var1.readInt();
      this.effectId = var1.readUnsignedByte();
   }

   public void write(ByteBuf var1) {
      writeVarInt(this.entityId, var1);
      var1.writeByte(this.effectId);
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   protected void v17Write(ByteBuf var1) {
      var1.writeInt(this.entityId);
      var1.writeByte(this.effectId);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public int getEffectId() {
      return this.effectId;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void setEffectId(int var1) {
      this.effectId = var1;
   }

   public String toString() {
      return "EntityRemoveEffect(entityId=" + this.getEntityId() + ", effectId=" + this.getEffectId() + ")";
   }

   public EntityRemoveEffect() {
   }

   public EntityRemoveEffect(int var1, int var2) {
      this.entityId = var1;
      this.effectId = var2;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EntityRemoveEffect)) {
         return false;
      } else {
         EntityRemoveEffect var2 = (EntityRemoveEffect)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            return this.getEntityId() != var2.getEntityId() ? false : this.getEffectId() == var2.getEffectId();
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EntityRemoveEffect;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getEntityId();
      return var2 * 59 + this.getEffectId();
   }
}
