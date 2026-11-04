package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class EntityStatus extends DefinedPacket {
   public static final byte DEBUG_INFO_REDUCED = 22;
   public static final byte DEBUG_INFO_NORMAL = 23;
   private int entityId;
   private byte status;

   @Override
   public void read(ByteBuf var1) {
      this.entityId = var1.readInt();
      this.status = var1.readByte();
   }

   @Override
   public void write(ByteBuf var1) {
      var1.writeInt(this.entityId);
      var1.writeByte(this.status);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public byte getStatus() {
      return this.status;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void setStatus(byte var1) {
      this.status = var1;
   }

   @Override
   public String toString() {
      return "EntityStatus(entityId=" + this.getEntityId() + ", status=" + this.getStatus() + ")";
   }

   public EntityStatus() {
   }

   public EntityStatus(int var1, byte var2) {
      this.entityId = var1;
      this.status = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EntityStatus)) {
         return false;
      } else {
         EntityStatus var2 = (EntityStatus)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            return this.getEntityId() != var2.getEntityId() ? false : this.getStatus() == var2.getStatus();
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EntityStatus;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getEntityId();
      return var2 * 59 + this.getStatus();
   }
}
