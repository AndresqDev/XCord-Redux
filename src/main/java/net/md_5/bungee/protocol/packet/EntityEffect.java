package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EntityEffect extends MultiVersionPacketV17 {
   private int entityId;
   private int effectId;
   private int amplifier;
   private int duration;
   private boolean hideParticles;

   protected void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.entityId = var1.readInt();
      this.effectId = var1.readUnsignedByte();
      this.amplifier = var1.readUnsignedByte();
      this.duration = var1.readShort();
   }

   public void read(ByteBuf var1) {
      this.entityId = readVarInt(var1);
      this.effectId = var1.readUnsignedByte();
      this.amplifier = var1.readUnsignedByte();
      this.duration = readVarInt(var1);
      this.hideParticles = var1.readBoolean();
   }

   protected void v17Write(ByteBuf var1) {
      var1.writeInt(this.effectId);
      var1.writeByte(this.effectId);
      var1.writeByte(this.amplifier);
      var1.writeShort(this.duration);
   }

   public void write(ByteBuf var1) {
      writeVarInt(this.entityId, var1);
      var1.writeByte(this.effectId);
      var1.writeByte(this.amplifier);
      writeVarInt(this.duration, var1);
      var1.writeBoolean(this.hideParticles);
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public int getEffectId() {
      return this.effectId;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getDuration() {
      return this.duration;
   }

   public boolean isHideParticles() {
      return this.hideParticles;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void setEffectId(int var1) {
      this.effectId = var1;
   }

   public void setAmplifier(int var1) {
      this.amplifier = var1;
   }

   public void setDuration(int var1) {
      this.duration = var1;
   }

   public void setHideParticles(boolean var1) {
      this.hideParticles = var1;
   }

   public String toString() {
      return "EntityEffect(entityId="
         + this.getEntityId()
         + ", effectId="
         + this.getEffectId()
         + ", amplifier="
         + this.getAmplifier()
         + ", duration="
         + this.getDuration()
         + ", hideParticles="
         + this.isHideParticles()
         + ")";
   }

   public EntityEffect() {
   }

   public EntityEffect(int var1, int var2, int var3, int var4, boolean var5) {
      this.entityId = var1;
      this.effectId = var2;
      this.amplifier = var3;
      this.duration = var4;
      this.hideParticles = var5;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EntityEffect)) {
         return false;
      } else {
         EntityEffect var2 = (EntityEffect)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getEntityId() != var2.getEntityId()) {
            return false;
         } else if (this.getEffectId() != var2.getEffectId()) {
            return false;
         } else if (this.getAmplifier() != var2.getAmplifier()) {
            return false;
         } else {
            return this.getDuration() != var2.getDuration() ? false : this.isHideParticles() == var2.isHideParticles();
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof EntityEffect;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getEntityId();
      var2 = var2 * 59 + this.getEffectId();
      var2 = var2 * 59 + this.getAmplifier();
      var2 = var2 * 59 + this.getDuration();
      return var2 * 59 + (this.isHideParticles() ? 79 : 97);
   }
}
