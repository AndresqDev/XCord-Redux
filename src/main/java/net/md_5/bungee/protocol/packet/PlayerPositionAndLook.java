package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerPositionAndLook extends PlayerPosition {
   private float yaw;
   private float pitch;
   private int teleportId;

   public PlayerPositionAndLook(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      super(var1, var3, var5, var10);
      this.yaw = var7;
      this.pitch = var8;
      this.teleportId = var9;
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      var1.writeDouble(this.x);
      if (var3 < 16) {
         var1.writeDouble(this.y + 1.62F);
      } else {
         var1.writeDouble(this.y);
      }

      var1.writeDouble(this.z);
      var1.writeFloat(this.yaw);
      var1.writeFloat(this.pitch);
      if (var3 < 16) {
         var1.writeBoolean(false);
      } else {
         var1.writeByte(0);
      }

      if (var3 >= 107) {
         DefinedPacket.writeVarInt(this.teleportId, var1);
      }

      if (var3 >= 755) {
         var1.writeBoolean(true);
      }
   }

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      if (var3 < 16) {
         var1.readDouble();
      }

      this.z = var1.readDouble();
      this.yaw = var1.readFloat();
      this.pitch = var1.readFloat();
      this.onGround = var1.readBoolean();
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getTeleportId() {
      return this.teleportId;
   }

   public void setYaw(float var1) {
      this.yaw = var1;
   }

   public void setPitch(float var1) {
      this.pitch = var1;
   }

   public void setTeleportId(int var1) {
      this.teleportId = var1;
   }

   @Override
   public String toString() {
      return "PlayerPositionAndLook(yaw=" + this.getYaw() + ", pitch=" + this.getPitch() + ", teleportId=" + this.getTeleportId() + ")";
   }

   public PlayerPositionAndLook() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerPositionAndLook)) {
         return false;
      } else {
         PlayerPositionAndLook var2 = (PlayerPositionAndLook)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else if (Float.compare(this.getYaw(), var2.getYaw()) != 0) {
            return false;
         } else {
            return Float.compare(this.getPitch(), var2.getPitch()) != 0 ? false : this.getTeleportId() == var2.getTeleportId();
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerPositionAndLook;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      var2 = var2 * 59 + Float.floatToIntBits(this.getYaw());
      var2 = var2 * 59 + Float.floatToIntBits(this.getPitch());
      return var2 * 59 + this.getTeleportId();
   }
}
